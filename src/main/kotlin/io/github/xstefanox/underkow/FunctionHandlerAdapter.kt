package io.github.xstefanox.underkow

import io.undertow.server.HttpServerExchange

/**
 * Adapt a handler defined as a lambda function to be called as a [SuspendingHttpHandler].
 */
class FunctionHandlerAdapter(private val handler: suspend (HttpServerExchange) -> Unit) : SuspendingHttpHandler {

    override suspend fun handleRequest(exchange: HttpServerExchange) {
        handler.invoke(exchange)
    }
}
