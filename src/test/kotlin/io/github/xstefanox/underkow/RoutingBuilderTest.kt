package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.dsl.RoutingBuilder
import io.github.xstefanox.underkow.test.AnException
import io.github.xstefanox.underkow.test.mockDispatcher
import io.github.xstefanox.underkow.test.mockExchange
import io.github.xstefanox.underkow.test.mockStandardHandler
import io.github.xstefanox.underkow.test.requesting
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.mockk.mockk
import io.mockk.verify
import io.undertow.util.Methods.GET
import org.junit.jupiter.api.Test

internal class RoutingBuilderTest {

    @Test
    fun `routing builder should produce a new object on every execution`() {

        val routingBuilder = RoutingBuilder(dispatcher = mockk(), suspendingHttpHandler = mockk())

        val routingHandler1 = routingBuilder.build()
        val routingHandler2 = routingBuilder.build()

        routingHandler1 shouldNotBe routingHandler2
    }

    @Test
    fun `empty base prefixes should be accepted`() {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/")

        val handler = RoutingBuilder(dispatcher = mockDispatcher(), suspendingHttpHandler = mockk()).apply {
            get("") {}
        }.build()

        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    @Test
    fun `blank base prefixes should be trimmed and thus accepted`() {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/")

        val handler = RoutingBuilder(" ", dispatcher = mockDispatcher(), suspendingHttpHandler = mockk()).apply {
            get("") {}
        }.build()

        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    @Test
    fun `base prefixes starting with blank characters should be trimmed`() {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/prefix")

        val handler = RoutingBuilder(" /prefix", dispatcher = mockDispatcher(), suspendingHttpHandler = mockk()).apply {
            get("") {}
        }.build()

        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    @Test
    fun `base prefixes ending with blank characters should be trimmed`() {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/prefix")

        val handler = RoutingBuilder("/prefix ", dispatcher = mockDispatcher(), suspendingHttpHandler = mockk()).apply {
            get("") {}
        }.build()

        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    @Test
    fun `empty nested prefixes should not be accepted`() {

        val routingBuilder = RoutingBuilder(dispatcher = mockDispatcher(), suspendingHttpHandler = mockk())

        shouldThrow<IllegalArgumentException> {
            routingBuilder.path("") {}
        }
    }

    @Test
    fun `blank nested prefixes should not be accepted`() {

        val routingBuilder = RoutingBuilder(dispatcher = mockDispatcher(), suspendingHttpHandler = mockk())

        shouldThrow<IllegalArgumentException> {
            routingBuilder.path(" ") {}
        }
    }

    @Test
    fun `nested prefixes starting with blank characters should be trimmed`() {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/prefix")

        val handler = RoutingBuilder(dispatcher = mockDispatcher(), suspendingHttpHandler = mockk()).apply {
            path(" /prefix") {
                get("") {}
            }
        }.build()

        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    @Test
    fun `nested prefixes ending with blank characters should be trimmed`() {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/prefix")

        val handler = RoutingBuilder(dispatcher = mockDispatcher(), suspendingHttpHandler = mockk()).apply {
            path("/prefix ") {
                get("") {}
            }
        }.build()

        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    @Test
    fun `empty paths should be accepted`() {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/")

        val handler = RoutingBuilder(dispatcher = mockDispatcher(), suspendingHttpHandler = mockk()).apply {
            get("") {}
        }.build()

        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    @Test
    fun `blank paths should not be accepted`() {

        val routingBuilder = RoutingBuilder(dispatcher = mockDispatcher(), suspendingHttpHandler = mockk())

        shouldThrow<IllegalArgumentException> {
            routingBuilder.path(" ") {}
        }
    }

    @Test
    fun `routing definition evaluation should happen on builder completion only`() {

        val routingBuilder = RoutingBuilder(dispatcher = mockDispatcher(), suspendingHttpHandler = mockk())

        routingBuilder.path("/test") {
            throw AnException()
        }

        shouldThrow<AnException> {
            routingBuilder.build()
        }
    }
}
