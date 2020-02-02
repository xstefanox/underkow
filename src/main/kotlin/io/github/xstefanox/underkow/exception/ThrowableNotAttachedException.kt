package io.github.xstefanox.underkow.exception

import io.undertow.server.HttpServerExchange

/**
 * Thrown when a [Throwable] is not found attached to the given [HttpServerExchange].
 */
class ThrowableNotAttachedException(
    exchange: HttpServerExchange
) : Exception("throwable attachment not found on $exchange")
