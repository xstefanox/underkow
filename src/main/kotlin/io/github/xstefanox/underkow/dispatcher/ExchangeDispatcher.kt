package io.github.xstefanox.underkow.dispatcher

import io.undertow.server.HttpServerExchange

/**
 * Dispatch the given [HttpServerExchange] to a consumer that will handle it.
 */
interface ExchangeDispatcher {

    /**
     * Dispatch the exchange.
     *
     * @param exchange the exchange representing a request to handle.
     * @param block the actual code used to handle the request.
     */
    fun dispatch(exchange: HttpServerExchange, block: suspend () -> Unit)
}