package io.github.xstefanox.underkow.chain

import io.github.xstefanox.underkow.exception.SuspendingExceptionHandler
import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.github.xstefanox.underkow.test.mockExchange
import io.github.xstefanox.underkow.test.mockFilter
import io.github.xstefanox.underkow.test.mockHandler
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.mockk.Ordering.ORDERED
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlin.reflect.KClass

class HandlerChainTest : StringSpec({

    "a delegating filter should trigger the execution of the final handler" {

        val handler1 = mockFilter()
        val handler2 = mockHandler()
        val exchange = mockExchange()

        val handlerChain = HandlerChain(listOf(handler1, handler2), SuspendingExceptionHandler(emptyMap()))

        handlerChain.handleRequest(exchange)

        coVerify(ordering = ORDERED) {
            handler1.handleRequest(eq(exchange))
            handler2.handleRequest(eq(exchange))
        }
    }

    "multiple filters should delegate recursively until the final handler" {

        val handler1 = mockFilter()
        val handler2 = mockFilter()
        val handler3 = mockHandler()
        val exchange = mockExchange()

        val handlerChain = HandlerChain(listOf(handler1, handler2, handler3), SuspendingExceptionHandler(emptyMap()))

        handlerChain.handleRequest(exchange)

        coVerify(ordering = ORDERED) {
            handler1.handleRequest(eq(exchange))
            handler2.handleRequest(eq(exchange))
            handler3.handleRequest(eq(exchange))
        }
    }

    "when a filter does not delegate, the following filters should not be triggered" {

        val handler1 = mockFilter()
        val handler2 = mockHandler()
        val handler3 = mockHandler()
        val exchange = mockExchange()

        val handlerChain = HandlerChain(listOf(handler1, handler2, handler3), SuspendingExceptionHandler(emptyMap()))

        handlerChain.handleRequest(exchange)

        coVerify(ordering = ORDERED) {
            handler1.handleRequest(eq(exchange))
            handler2.handleRequest(eq(exchange))
        }

        coVerify(exactly = 0) { handler3.handleRequest(eq(exchange)) }
    }

    "the handler chain should be not empty" {

        shouldThrow<EmptyHandlerChainException> {
            HandlerChain(emptyList(), SuspendingExceptionHandler(emptyMap()))
        }
    }

    "the handler chain should not contain duplicates" {

        val handler = mockFilter()

        shouldThrow<DuplicateHandlersInChainException> {
            HandlerChain(listOf(handler, handler), SuspendingExceptionHandler(emptyMap()))
        }
    }

    "the last handler in the chain should not forward the request handling" {

        val handler = mockFilter()
        val exchange = mockExchange()
        val asyncExceptionHandler = mockk<SuspendingExceptionHandler>()

        coEvery {
            asyncExceptionHandler.handleRequest(any())
        } just runs

        val exceptionHandlerMap: Map<KClass<out Throwable>, SuspendingHttpHandler> = mapOf(
            HandlerChainExhaustedException::class to asyncExceptionHandler
        )

        val handlerChain = HandlerChain(listOf(handler), SuspendingExceptionHandler(exceptionHandlerMap))

        handlerChain.handleRequest(exchange)

        coVerify { asyncExceptionHandler.handleRequest(eq(exchange)) }
    }
})
