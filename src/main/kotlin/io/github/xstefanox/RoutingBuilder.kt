package io.github.xstefanox

import io.undertow.server.HttpServerExchange
import io.undertow.server.RoutingHandler
import io.undertow.util.HttpString
import io.undertow.util.Methods.GET

typealias Handler = (HttpServerExchange) -> Unit

class RoutingBuilder {

    private val templates = mutableMapOf<String, Map<HttpString, Handler>>()

    fun get(template: String, httpHandler: Handler) {
        templates[template] = mapOf(GET to httpHandler)
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