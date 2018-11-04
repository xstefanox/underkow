package io.github.xstefanox.underkow.exception

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.ExceptionHandler.THROWABLE
import kotlin.reflect.KClass

/**
 * A [SuspendingHttpHandler] that can handle exception-throwing exchanges.
 */
class SuspendingExceptionHandler(private val exceptionHandlers: Map<KClass<out Throwable>, SuspendingHttpHandler>) : SuspendingHttpHandler {

    override suspend fun handleRequest(exchange: HttpServerExchange) {

        val throwable = exchange.getAttachment(THROWABLE) ?: throw ThrowableNotAttachedException(exchange)

        (exceptionHandlers[throwable::class] ?: throw throwable).handleRequest(exchange)
    }
}