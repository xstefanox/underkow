package io.github.xstefanox.underkow

import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.EventSource
import com.launchdarkly.eventsource.MessageEvent
import io.github.xstefanox.underkow.test.TEST_HTTP_PORT
import io.github.xstefanox.underkow.test.assert
import io.kotlintest.matchers.beEmpty
import io.kotlintest.shouldNot
import io.undertow.Handlers.serverSentEvents
import java.lang.Thread.sleep
import java.net.URI
import java.time.Duration
import java.util.UUID.randomUUID
import kotlin.concurrent.thread
import net.jodah.failsafe.Failsafe
import net.jodah.failsafe.RetryPolicy
import net.jodah.failsafe.function.CheckedRunnable
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class SseTest {

    private val logger: Logger = LoggerFactory.getLogger(SseTest::class.java)

    @Test
    fun `events should be received`() {

        val lock = Unit
        val messages = mutableListOf<String>()

        val clientEventHandler = object : EventHandler {

            override fun onOpen() {
                logger.info("handler opened")
            }

            override fun onComment(comment: String) {
                logger.warn("comment $comment received")
            }

            override fun onMessage(event: String, messageEvent: MessageEvent) {
                logger.info("received message $event")
                synchronized(lock) {
                    messages += event
                }
            }

            override fun onClosed() {
                logger.info("handler closed")
            }

            override fun onError(t: Throwable) {
                logger.error(t.message)
            }
        }

        val retryPolicy = RetryPolicy<Unit>()
            .handle(AssertionError::class.java)
            .withDelay(Duration.ofSeconds(1))
            .withMaxRetries(3)
            .onFailedAttempt {

                logger.warn("assertion attempt ${it.attemptCount}: ${it.lastFailure.message}")
            }

        val serverSentEventHandler = serverSentEvents { _, _ ->
            logger.info("client connected")
        }

        val producer = thread(isDaemon = true, name = "producer") {
            while (true) {

                val message = "test ${randomUUID()}"

                serverSentEventHandler.connections.forEach { connection ->
                    logger.info("producing message $message")
                    connection.send(message)
                }

                try {
                    sleep(200)
                } catch (e: InterruptedException) {
                    logger.info("terminating")
                }
            }
        }

        try {
            undertow {
                port = TEST_HTTP_PORT
                routing {
                    sse("/test", serverSentEventHandler)
                }
            } assert {

                val requestPath = "http://localhost:$TEST_HTTP_PORT/test"

                EventSource.Builder(clientEventHandler, URI(requestPath)).build().use { eventSource ->

                    eventSource.start()

                    Failsafe.with(retryPolicy).run(CheckedRunnable {
                        synchronized(lock) {
                            messages shouldNot beEmpty()
                        }
                    })
                }
            }
        } finally {
            producer.interrupt()
        }
    }
}
