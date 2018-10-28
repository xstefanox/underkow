package io.github.xstefanox.underkow

import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.RoutingHandler
import io.undertow.util.HttpString
import io.undertow.util.Methods.DELETE
import io.undertow.util.Methods.GET
import io.undertow.util.Methods.PATCH
import io.undertow.util.Methods.POST
import io.undertow.util.Methods.PUT

class RoutingBuilder {

    private val templates = mutableMapOf<String, Map<HttpString, HttpHandler>>()

    fun get(template: String, httpHandler: (HttpServerExchange) -> Unit) {
        templates[template] = mapOf(GET to HttpHandler {
            httpHandler.invoke(it)
        })
    }

    fun get(template: String, httpHandler: HttpHandler) {
        templates[template] = mapOf(GET to httpHandler)
    }

    fun post(template: String, httpHandler: (HttpServerExchange) -> Unit) {
        templates[template] = mapOf(POST to HttpHandler {
            httpHandler.invoke(it)
        })
    }

    fun put(template: String, httpHandler: (HttpServerExchange) -> Unit) {
        templates[template] = mapOf(PUT to HttpHandler {
            httpHandler.invoke(it)
        })
    }

    fun patch(template: String, httpHandler: (HttpServerExchange) -> Unit) {
        templates[template] = mapOf(PATCH to HttpHandler {
            httpHandler.invoke(it)
        })
    }

    fun delete(template: String, httpHandler: (HttpServerExchange) -> Unit) {
        templates[template] = mapOf(DELETE to HttpHandler {
            httpHandler.invoke(it)
        })
    }

    fun build(): RoutingHandler {

        val routingHandler = RoutingHandler()

        templates.forEach { template, map ->
            map.forEach { method, handler ->
                routingHandler.add(method, template, handler)
            }
        }

        return routingHandler
    }
}