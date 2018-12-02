package io.github.xstefanox.underkow.chain

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.undertow.server.HttpServerExchange

/**
 * Decorate a [SuspendingHttpHandler] with a reference to the next element of the chain.
 *
 * @param httpHandler the wrapped handler.
 */
class ChainedHttpHandler(private val httpHandler: SuspendingHttpHandler) : SuspendingHttpHandler {

    /**
     * The next handler of the chain; if `null`, this is the last handler.
     */
    var next: ChainedHttpHandler? = null

    /**
     * Handle the request by delegating to the wrapped [SuspendingHttpHandler].
     *
     * @param exchange the HTTP request/response exchange.
     */
    override suspend fun handleRequest(exchange: HttpServerExchange) {
        httpHandler.handleRequest(exchange)
    }
}
