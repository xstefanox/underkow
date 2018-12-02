package io.github.xstefanox.underkow

import io.undertow.server.HttpServerExchange

/**
 * Adapt a handler defined as a lambda function to be called as a [SuspendingHttpHandler].
 *
 * @param handler the function that will be delegeted to handle the received requests.
 */
class FunctionHandlerAdapter(private val handler: suspend (HttpServerExchange) -> Unit) : SuspendingHttpHandler {

    /**
     * Handle the request by delegating to the wrapped function.
     *
     * @param exchange the HTTP request/response exchange.
     */
    override suspend fun handleRequest(exchange: HttpServerExchange) {
        handler.invoke(exchange)
    }
}
