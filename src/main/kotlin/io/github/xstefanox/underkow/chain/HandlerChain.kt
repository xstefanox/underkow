package io.github.xstefanox.underkow.chain

import io.github.xstefanox.underkow.HttpScope
import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.github.xstefanox.underkow.exception.SuspendingExceptionHandler
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.ExceptionHandler.THROWABLE
import io.undertow.util.SameThreadExecutor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * Wrap a collection of handlers in a chain that can be traversed from the head to the tail; once exhausted, the chain
 * cannot be used anymore.
 *
 * This handler is implemented as a chain of responsibility that can be composed by a builder, without the need of each
 * handler to have a direct reference to the next and the delegation is implemented using the visitor pattern: each
 * handler delegate to the next by inviting the [HttpServerExchange], that has the complete reference of ther chain, to
 * be visited by the next handler.
 * The pointer to the current element of the chain is the state of the traversal and is saved in an [HttpServerExchange]
 * attachment to maintain this object stateless and composable.
 *
 * @param handlers the chain used to delegate the request handling; the handlers are applied in order, hence it is
 *                 important to use an ordered collection.
 * @param exceptionHandler the handler used to handle the exceptions thrown during the handling of the requests.
 */
class HandlerChain(handlers: Collection<SuspendingHttpHandler>, private val exceptionHandler: SuspendingExceptionHandler) : HttpHandler {

    private val head: ChainedHttpHandler

    init {
        if (handlers.isEmpty()) {
            throw EmptyHandlerChainException()
        }

        if (handlers.toSet().size < handlers.size) {
            throw DuplicateHandlersInChainException()
        }

        head = handlers.map(::ChainedHttpHandler).reduceRight { element, tail ->
            element.next = tail
            element
        }
    }

    /**
     * Save a reference to the head of the chain in the [HttpServerExchange] and delegate the handling of the request to
     * the chain itself.
     */
    override fun handleRequest(exchange: HttpServerExchange) {

        exchange.putAttachment(CURRENT_HANDLER, head)

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            HttpScope.launch {
                exchange.putAttachment(THROWABLE, throwable)
                exceptionHandler.handleRequest(exchange)
            }
        }

        exchange.dispatch(SameThreadExecutor.INSTANCE, Runnable {
            HttpScope.launch(exceptionHandler) {
                head.handleRequest(exchange)
            }
        })
    }
}
