package io.github.xstefanox.underkow.test

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.restassured.RestAssured.given
import io.undertow.Undertow
import io.undertow.server.HttpHandler
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
