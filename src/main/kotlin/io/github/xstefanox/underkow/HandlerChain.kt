package io.github.xstefanox.underkow

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey

private val HANDLER_CHAIN = AttachmentKey.create(HandlerChain::class.java)

fun HttpServerExchange.next() {

    val handlerChain = getAttachment(HANDLER_CHAIN)

    handlerChain.advance().handleRequest(this)
}

class HandlerChain(handlers: List<HttpHandler>) : HttpHandler {

    private var currentChainedHttpHandler: ChainedHttpHandler

    init {
        require(handlers.isNotEmpty()) {
            "handler chain must not be empty"
        }

        currentChainedHttpHandler = handlers.map(::ChainedHttpHandler).reduceRight { element, tail ->
            element.next = tail
            element
        }

        println(currentChainedHttpHandler)
    }

    override fun handleRequest(exchange: HttpServerExchange) {
        exchange.putAttachment(HANDLER_CHAIN, this)
        currentChainedHttpHandler.handleRequest(exchange)
    }

    fun advance(): HttpHandler {
        currentChainedHttpHandler = currentChainedHttpHandler.next ?: throw IllegalStateException("no more handlers in the chain")
        return currentChainedHttpHandler
    }

    private class ChainedHttpHandler(val httpHandler: HttpHandler) : HttpHandler {

        var next: ChainedHttpHandler? = null

        override fun handleRequest(exchange: HttpServerExchange?) {
            httpHandler.handleRequest(exchange)
        }
    }
}
