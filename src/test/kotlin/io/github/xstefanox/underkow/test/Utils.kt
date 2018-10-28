package io.github.xstefanox.underkow.test

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.undertow.Undertow
import io.undertow.server.HttpHandler

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
