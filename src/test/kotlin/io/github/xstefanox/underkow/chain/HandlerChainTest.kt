package io.github.xstefanox.underkow.chain

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.github.xstefanox.underkow.exception.SuspendingExceptionHandler
import io.github.xstefanox.underkow.exception.UnhandledExceptionHandler
import io.github.xstefanox.underkow.test.eventually
import io.github.xstefanox.underkow.test.mockDispatcher
import io.github.xstefanox.underkow.test.mockExchange
import io.github.xstefanox.underkow.test.mockFilter
import io.github.xstefanox.underkow.test.mockHandler
import io.kotlintest.shouldThrow
import io.mockk.Ordering.ORDERED
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

internal class HandlerChainTest {

    @Test
    fun `a delegating filter should trigger the execution of the final handler`() {

        val handler1 = mockFilter()
        val handler2 = mockHandler()
        val exchange = mockExchange()
        val unhandledExceptionHandler = mockk<UnhandledExceptionHandler>()
        val dispatcher = mockDispatcher()

        val handlerChain = HandlerChain(listOf(handler1, handler2), SuspendingExceptionHandler(emptyMap(), unhandledExceptionHandler), dispatcher)

        handlerChain.handleRequest(exchange)

        eventually {
            coVerify(ordering = ORDERED) {
                handler1.handleRequest(eq(exchange))
                handler2.handleRequest(eq(exchange))
            }
        }
    }

    @Test
    fun `multiple filters should delegate recursively until the final handler`() {

        val handler1 = mockFilter()
        val handler2 = mockFilter()
        val handler3 = mockHandler()
        val exchange = mockExchange()
        val unhandledExceptionHandler = mockk<UnhandledExceptionHandler>()
        val dispatcher = mockDispatcher()

        val handlerChain = HandlerChain(listOf(handler1, handler2, handler3), SuspendingExceptionHandler(emptyMap(), unhandledExceptionHandler), dispatcher)

        handlerChain.handleRequest(exchange)

        eventually {
            coVerify(ordering = ORDERED) {
                handler1.handleRequest(eq(exchange))
                handler2.handleRequest(eq(exchange))
                handler3.handleRequest(eq(exchange))
            }
        }
    }

    @Test
    fun `when a filter does not delegate, the following filters should not be triggered`() {

        val handler1 = mockFilter()
        val handler2 = mockHandler()
        val handler3 = mockHandler()
        val exchange = mockExchange()
        val unhandledExceptionHandler = mockk<UnhandledExceptionHandler>()
        val dispatcher = mockDispatcher()

        val handlerChain = HandlerChain(listOf(handler1, handler2, handler3), SuspendingExceptionHandler(emptyMap(), unhandledExceptionHandler), dispatcher)

        handlerChain.handleRequest(exchange)

        eventually {
            coVerify(ordering = ORDERED) {
                handler1.handleRequest(eq(exchange))
                handler2.handleRequest(eq(exchange))
            }

            coVerify(exactly = 0) {
                handler3.handleRequest(eq(exchange))
            }
        }
    }

    @Test
    fun `the handler chain should be not empty`() {

        val unhandledExceptionHandler = mockk<UnhandledExceptionHandler>()
        val dispatcher = mockDispatcher()

        shouldThrow<EmptyHandlerChainException> {
            HandlerChain(emptyList(), SuspendingExceptionHandler(emptyMap(), unhandledExceptionHandler), dispatcher)
        }
    }

    @Test
    fun `the handler chain should not contain duplicates`() {

        val handler = mockFilter()
        val unhandledExceptionHandler = mockk<UnhandledExceptionHandler>()
        val dispatcher = mockDispatcher()

        shouldThrow<DuplicateHandlersInChainException> {
            HandlerChain(listOf(handler, handler), SuspendingExceptionHandler(emptyMap(), unhandledExceptionHandler), dispatcher)
        }
    }

    @Test
    fun `the last handler in the chain should not forward the request handling`() {

        val handler = mockFilter()
        val exchange = mockExchange()
        val asyncExceptionHandler = mockk<SuspendingExceptionHandler>()
        val unhandledExceptionHandler = mockk<UnhandledExceptionHandler>()
        val dispatcher = mockDispatcher()

        coEvery {
            asyncExceptionHandler.handleRequest(any())
        } just runs

        val exceptionHandlerMap: Map<KClass<out Throwable>, SuspendingHttpHandler> = mapOf(
            HandlerChainExhaustedException::class to asyncExceptionHandler
        )

        val handlerChain = HandlerChain(listOf(handler), SuspendingExceptionHandler(exceptionHandlerMap, unhandledExceptionHandler), dispatcher)

        handlerChain.handleRequest(exchange)

        eventually {
            coVerify {
                asyncExceptionHandler.handleRequest(eq(exchange))
            }
        }
    }
}
