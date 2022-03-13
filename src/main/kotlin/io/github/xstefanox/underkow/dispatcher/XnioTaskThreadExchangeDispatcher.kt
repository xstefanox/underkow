package io.github.xstefanox.underkow.dispatcher

import io.undertow.server.HttpServerExchange
import kotlinx.coroutines.runBlocking

/**
 * Dispatch the [HttpServerExchange] to a thread of the [org.xnio.XnioWorker].
 */
class XnioTaskThreadExchangeDispatcher : ExchangeDispatcher {

    override fun dispatch(exchange: HttpServerExchange, block: suspend () -> Unit) {
        exchange.dispatch(
            Runnable {
                // the runnable is dispatched to a task thread, hence it is safe to block
                runBlocking {
                    block()
                }
            }
        )
    }
}
