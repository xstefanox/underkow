package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.mockExchange
import io.github.xstefanox.underkow.test.mockStandardHandler
import io.github.xstefanox.underkow.test.requesting
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.mockk.verify
import io.undertow.server.RoutingHandler
import io.undertow.util.Methods.GET

class RoutingBuilderTest : StringSpec({

    fun RoutingBuilder.respondingTo(path: String): RoutingHandler {
        return apply {
            get(path) {}
        }.build()
    }

    "routing builder should produce a new object on every execution" {

        val routingBuilder = RoutingBuilder()

        val routingHandler1 = routingBuilder.build()
        val routingHandler2 = routingBuilder.build()

        routingHandler1 shouldNotBe routingHandler2
    }

    "empty base prefixes should be accepted" {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/")

        val handler = RoutingBuilder("").respondingTo("")
        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    "blank base prefixes should be trimmed and thus accepted" {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/")

        val handler = RoutingBuilder(" ").respondingTo("")
        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    "base prefixes starting with blank characters should be trimmed" {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/prefix")

        val handler = RoutingBuilder(" /prefix").respondingTo("")
        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    "base prefixes ending with blank characters should be trimmed" {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/prefix")

        val handler = RoutingBuilder("/prefix ").respondingTo("")
        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    "empty nested prefixes should not be accepted" {

        val routingBuilder = RoutingBuilder()

        shouldThrow<IllegalArgumentException> {
            routingBuilder.path("") {}
        }
    }

    "blank nested prefixes should not be accepted" {

        val routingBuilder = RoutingBuilder()

        shouldThrow<IllegalArgumentException> {
            routingBuilder.path(" ") {}
        }
    }

    "nested prefixes starting with blank characters should be trimmed" {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/prefix")

        val handler = RoutingBuilder().apply {
            path(" /prefix") {
                get("") {}
            }
        }.build()

        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    "nested prefixes ending with blank characters should be trimmed" {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/prefix")

        val handler = RoutingBuilder().apply {
            path("/prefix ") {
                get("") {}
            }
        }.build()

        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    "empty paths should be accepted" {

        val fallbackHandler = mockStandardHandler()
        val exchange = mockExchange().requesting(GET, "/")

        val handler = RoutingBuilder().respondingTo("")
        handler.fallbackHandler = fallbackHandler

        handler.handleRequest(exchange)

        verify(exactly = 0) { fallbackHandler.handleRequest(eq(exchange)) }
    }

    "blank paths should not be accepted" {

        val routingBuilder = RoutingBuilder()

        shouldThrow<IllegalArgumentException> {
            routingBuilder.get(" ") {}
        }
    }
})
