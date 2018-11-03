package io.github.xstefanox.underkow.chain

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

/**
 * Wrap a collection of handlers in a chain that can be traversed from the head to the tail; once exhausted, the chain
 * cannot be used anymore.
 * The pointer to the current element of the chain is the state of the traversal and is saved in an [HttpServerExchange]
 * attachment to maintain this object stateless.
 */
class HandlerChain(handlers: Collection<HttpHandler>) : HttpHandler {

    private val head: ChainedHttpHandler

    init {
        if (handlers.isEmpty()) {
            throw EmptyHandlerChainException()
        }

        if (handlers.toSet().size < handlers.size) {
            throw DuplicateHandlersInChainException()
        }

        head = handlers.map(::ChainedHttpHandler).reduceRight { element, tail ->
            element.next = tail
            element
        }
    }

    /**
     * Save a reference to the head of the chain in the [HttpServerExchange] and delegate the handling of the request to
     * the chain itself.
     */
    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.putAttachment(CURRENT_HANDLER, head)
        head.handleRequest(exchange)
    }
}
