package io.github.xstefanox.underkow.chain

import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey

/**
 * The key used to identify a [HandlerChain] attached to a [HttpServerExchange].
 */
internal val HANDLER_CHAIN = AttachmentKey.create(HandlerChain::class.java)

/**
 * Make a [HttpServerExchange] delegate itself to the next handler in the chain.
 */
fun HttpServerExchange.next() {
    getAttachment(HANDLER_CHAIN)
        .advance()
        .handleRequest(this)
}
