package io.github.xstefanox.underkow.exception

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.StatusCodes.INTERNAL_SERVER_ERROR

/**
 * Handle each exceotion not handled by any other handler by returning an [INTERNAL_SERVER_ERROR] status code to the
 * client, to ensure that no exchange remains pending.
 */
class UnhandledExceptionHandler : SuspendingHttpHandler {

    override suspend fun handleRequest(exchange: HttpServerExchange) {
        exchange.statusCode = INTERNAL_SERVER_ERROR
        exchange.responseSender.send("")
    }
}
