package io.github.xstefanox.underkow.exception

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.ExceptionHandler.THROWABLE
import kotlin.reflect.KClass

/**
 * A [SuspendingHttpHandler] that can handle exception-throwing exchanges.
 *
 * @param exceptionHandlers a map of handlers indexed by exception classes.
 * @param unhandledExceptionHandler used to handle exceptions that cannot be handled by any handler in the map.
 */
class SuspendingExceptionHandler(
    private val exceptionHandlers: Map<KClass<out Throwable>, SuspendingHttpHandler>,
    private val unhandledExceptionHandler: SuspendingHttpHandler
) : SuspendingHttpHandler {

    /**
     * Handle the request by trying to delegate to a [SuspendingHttpHandler] associated to the exception class.
     * Fallback to a default handler if no exception specific handler is configured.
     *
     * @param exchange the HTTP request/response exchange.
     */
    override suspend fun handleRequest(exchange: HttpServerExchange) {

        val throwable = exchange.getAttachment(THROWABLE) ?: throw ThrowableNotAttachedException(exchange)

        (exceptionHandlers[throwable::class] ?: unhandledExceptionHandler).handleRequest(exchange)
    }
}
