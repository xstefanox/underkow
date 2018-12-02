package io.github.xstefanox.underkow

import io.undertow.server.HttpServerExchange

/**
 * A handler for a HTTP request tht can execute suspending functions.
 */
interface SuspendingHttpHandler {

    /**
     * Handle the request.
     *
     * @param exchange the HTTP request/response exchange.
     */
    suspend fun handleRequest(exchange: HttpServerExchange)
}
