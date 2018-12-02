package io.github.xstefanox.underkow

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

/**
 * Adapt a standard [HttpHandler] to be called as a [SuspendingHttpHandler].
 *
 * @param handler the [HttpHandler] that will be delegeted to handle the received requests.
 */
class HttpHandlerAdapter(private val handler: HttpHandler) : SuspendingHttpHandler {

    /**
     * Handle the request by delegating to the wrapped [HttpHandler].
     *
     * @param exchange the HTTP request/response exchange.
     */
    override suspend fun handleRequest(exchange: HttpServerExchange) {
        handler.handleRequest(exchange)
    }
}
