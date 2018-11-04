package io.github.xstefanox.underkow.chain

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.undertow.server.HttpServerExchange

/**
 * Decorate a [SuspendingHttpHandler] with a reference to the next element of the chain.
 */
class ChainedHttpHandler(private val httpHandler: SuspendingHttpHandler) : SuspendingHttpHandler {

    var next: ChainedHttpHandler? = null

    override suspend fun handleRequest(exchange: HttpServerExchange) {
        httpHandler.handleRequest(exchange)
    }
}
