@file:JvmName("Extensions")

package io.github.xstefanox.underkow.chain

import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey

/**
 * The key used to identify the current [ChainedHttpHandler] of a [HandlerChain] attached to a [HttpServerExchange].
 */
internal val CURRENT_HANDLER = AttachmentKey.create(ChainedHttpHandler::class.java)

/**
 * Make a [HttpServerExchange] delegate itself to the next handler in the chain.
 */
suspend fun HttpServerExchange.next() {
    val nextHandler = getAttachment(CURRENT_HANDLER).next ?: throw HandlerChainExhaustedException()
    putAttachment(CURRENT_HANDLER, nextHandler)
    nextHandler.handleRequest(this)
}
