package io.github.xstefanox.underkow.test

import io.github.xstefanox.underkow.chain.ChainedHttpHandler
import io.github.xstefanox.underkow.chain.next
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
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

/**
 * The default TCP port used for testing
 */
const val TEST_HTTP_PORT = 8282

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
fun mockHandler(): HttpHandler {

    val httpHandler = mockk<HttpHandler>()
    every { httpHandler.handleRequest(any()) } just Runs

    return httpHandler
}

/**
 * Return a simple [HttpHandler] mock that delegates to ist successor without actually doing nothing.
 */
fun mockFilter(): HttpHandler = mockHandler().apply {

    val exchange = slot<HttpServerExchange>()

    every {
        handleRequest(capture(exchange))
    } answers {
        exchange.captured.next()
    }
}

/**
 * Return a simple [HttpServerExchange] mock that saves its attachments into a map.
 */
fun mockExchange() = mockk<HttpServerExchange>().apply {

    val attachments = mutableMapOf<AttachmentKey<*>, Any>()
    val key = slot<AttachmentKey<ChainedHttpHandler>>()
    val attachment = slot<ChainedHttpHandler>()

    every {
        putAttachment(capture(key), capture(attachment))
    } answers {
        attachments[key.captured] = attachment.captured
        attachment.captured
    }

    every {
        getAttachment(capture(key))
    } answers {
        attachments[key.captured] as ChainedHttpHandler
    }
}

fun request(method: HttpString, path: String, expect: Int) {

    val requestSpecification = given()
    val requestPath = "http://localhost:$TEST_HTTP_PORT$path"

    when (method) {
        GET -> requestSpecification.get(requestPath)
        POST -> requestSpecification.post(requestPath)
        PUT -> requestSpecification.put(requestPath)
        PATCH -> requestSpecification.patch(requestPath)
        DELETE -> requestSpecification.delete(requestPath)
        else -> throw AssertionError("unsupported method $method")
    }
        .then()
        .assertThat()
        .statusCode(expect)
}
