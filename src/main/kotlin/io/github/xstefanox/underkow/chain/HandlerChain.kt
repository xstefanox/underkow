package io.github.xstefanox.underkow.chain

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

/**
 * Wrap a collection of handlers in a chain that can be traversed from the head to the tail. Once exhausted, the chain
 * cannot be used anymore.
 */
internal class HandlerChain(handlers: Collection<HttpHandler>) : HttpHandler {

    private var currentChainedHttpHandler: ChainedHttpHandler

    init {
        if (handlers.isEmpty()) {
            throw EmptyHandlerChainException()
        }

        if (handlers.toSet().size < handlers.size) {
            throw DuplicateHandlersInChainException()
        }

        currentChainedHttpHandler = handlers.map(HandlerChain::ChainedHttpHandler).reduceRight { element, tail ->
            element.next = tail
            element
        }
    }

    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.putAttachment(HANDLER_CHAIN, this)
        currentChainedHttpHandler.handleRequest(exchange)
    }

    fun advance(): HttpHandler {
        currentChainedHttpHandler = currentChainedHttpHandler.next ?: throw HandlerChainExhaustedException()
        return currentChainedHttpHandler
    }

    private class ChainedHttpHandler(val httpHandler: HttpHandler) : HttpHandler {

        var next: ChainedHttpHandler? = null

        override fun handleRequest(exchange: HttpServerExchange?) {
            httpHandler.handleRequest(exchange)
        }
    }
}
