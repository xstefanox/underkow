package io.github.xstefanox.underkow

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

/**
 * Adapt a standard [HttpHandler] to be called as a [SuspendingHttpHandler].
 */
class HttpHandlerAdapter(private val handler: HttpHandler) : SuspendingHttpHandler {

    override suspend fun handleRequest(exchange: HttpServerExchange) {
        handler.handleRequest(exchange)
    }
}
