package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.mockHandler
import io.kotlintest.specs.StringSpec
import io.mockk.*
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey

class HandlerChainTest : StringSpec({

    // mock a filter that delegates to its successor
    fun mockFilter() : HttpHandler = mockHandler().apply {

        val exchange = slot<HttpServerExchange>()

        every {
            handleRequest(capture(exchange))
        } answers {
            exchange.captured.next()
        }
    }

    // mock the exchange and make it save the handler chain
    fun mockExchange() = mockk<HttpServerExchange>().apply {

        val attachments = mutableMapOf<AttachmentKey<*>, Any>()
        val key = slot<AttachmentKey<HandlerChain>>()
        val attachment = slot<HandlerChain>()

        every {
            putAttachment(capture(key), capture(attachment))
        } answers {
            attachments[key.captured] = attachment.captured
            attachment.captured
        }

        every {
            getAttachment(capture(key))
        } answers {
            attachments[key.captured] as HandlerChain
        }
    }

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
