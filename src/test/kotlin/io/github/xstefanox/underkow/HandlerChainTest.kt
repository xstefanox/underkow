package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.mockExchange
import io.github.xstefanox.underkow.test.mockFilter
import io.github.xstefanox.underkow.test.mockHandler
import io.kotlintest.specs.StringSpec
import io.mockk.verify
import io.mockk.verifyOrder

class HandlerChainTest : StringSpec({

    "a delegating filter should trigger the execution of the final handler" {

        val handler1 = mockFilter()
        val handler2 = mockHandler()
        val exchange = mockExchange()

        val handlerChain = HandlerChain(listOf(handler1, handler2))

        handlerChain.handleRequest(exchange)

        verifyOrder {
            handler1.handleRequest(eq(exchange))
            handler2.handleRequest(eq(exchange))
        }
    }

    "multiple filters should delegate recursively until the final handler" {

        val handler1 = mockFilter()
        val handler2 = mockFilter()
        val handler3 = mockHandler()
        val exchange = mockExchange()

        val handlerChain = HandlerChain(listOf(handler1, handler2, handler3))

        handlerChain.handleRequest(exchange)

        verifyOrder {
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

        val handlerChain = HandlerChain(listOf(handler1, handler2, handler3))

        handlerChain.handleRequest(exchange)

        verifyOrder {
            handler1.handleRequest(eq(exchange))
            handler2.handleRequest(eq(exchange))
        }

        verify(exactly = 0) { handler3.handleRequest(eq(exchange)) }
    }
})
