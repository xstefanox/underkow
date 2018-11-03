package io.github.xstefanox.underkow.chain

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

/**
 * Decorate a [HttpHandler] with a reference to the next element of the chain.
 */
class ChainedHttpHandler(private val httpHandler: HttpHandler) : HttpHandler {

    var next: ChainedHttpHandler? = null

    override fun handleRequest(exchange: HttpServerExchange?) {
        httpHandler.handleRequest(exchange)
    }
}
