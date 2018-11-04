package io.github.xstefanox.underkow.test

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.github.xstefanox.underkow.chain.next
import io.kotlintest.shouldThrow
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.restassured.RestAssured.given
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import io.undertow.util.HttpString
import io.undertow.util.Methods.DELETE
import io.undertow.util.Methods.GET
import io.undertow.util.Methods.PATCH
import io.undertow.util.Methods.POST
import io.undertow.util.Methods.PUT
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.Executor

/**
 * The default TCP port used for testing
 */
const val TEST_HTTP_PORT = 8282

private val LOGGER = LoggerFactory.getLogger("test")

/**
 * Allow running a list of assertions in the scope of a running Undertow instance, ensuring that the instance will be
 * automatically shut down upon completion.
 */
infix fun Undertow.assert(block: () -> Unit) {

    start()

    try {
        block()
    } finally {
        stop()
    }
}

/**
 * Return a simple [HttpHandler] mock that executes without actually doing nothing.
 */
fun mockHandler(): SuspendingHttpHandler {

    val handler = mockk<SuspendingHttpHandler>()
    val exchange = slot<HttpServerExchange>()

    coEvery {
        handler.handleRequest(capture(exchange))
    } coAnswers {
        LOGGER.info("reached handler {}", handler)
        exchange.captured.endExchange()
    }

    return handler
}

/**
 * Return a simple [HttpHandler] mock that delegates to ist successor without actually doing nothing.
 */
fun mockFilter(): SuspendingHttpHandler = mockHandler().apply {

    val filter = this
    val exchange = slot<HttpServerExchange>()

    coEvery {
        handleRequest(capture(exchange))
    } coAnswers {
        LOGGER.info("passing through filter {}", filter)
        exchange.captured.next()
    }
}

/**
 * Return a simple [HttpServerExchange] mock that saves its attachments into a map.
 */
fun mockExchange() = mockk<HttpServerExchange>().apply {

    val exchange = this
    val attachments = mutableMapOf<AttachmentKey<*>, Any>()
    val runnable = slot<Runnable>()
    val executor = slot<Executor>()

    val attachmentKey = slot<AttachmentKey<Any>>()
    val attachmentValue = slot<Any>()

    every {
        putAttachment(capture(attachmentKey), capture(attachmentValue))
    } answers {
        attachments[attachmentKey.captured] = attachmentValue.captured
        attachmentValue.captured
    }

    val attachmentKeyGet = slot<AttachmentKey<Any>>()

    every {
        getAttachment(capture(attachmentKeyGet))
    } answers {
        attachments[attachmentKeyGet.captured]
    }

    every {
        dispatch(capture(executor), capture(runnable))
    } answers {
        executor.captured.execute(runnable.captured)
        exchange
    }

    every {
        endExchange()
    } answers {
        exchange
    }
}

/**
 * Launch a request to the [Undertow]
 */
fun request(method: HttpString, path: String, expect: Int) {

    val requestSpecification = given()
    val requestPath = "http://localhost:$TEST_HTTP_PORT$path"

    val launchRequest = when (method) {
        GET -> requestSpecification.get(requestPath)
        POST -> requestSpecification.post(requestPath)
        PUT -> requestSpecification.put(requestPath)
        PATCH -> requestSpecification.patch(requestPath)
        DELETE -> requestSpecification.delete(requestPath)
        else -> throw AssertionError("unsupported method $method")
    }

    launchRequest
        .then()
        .assertThat()
        .statusCode(expect)
}

inline fun <reified T : Throwable> coShouldThrow(noinline block: suspend () -> Any?): T = runBlocking {
    shouldThrow<T> {
        block()
    }
}
