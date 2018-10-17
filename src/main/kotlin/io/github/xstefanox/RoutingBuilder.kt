package io.github.xstefanox

import io.undertow.server.HttpServerExchange
import io.undertow.server.RoutingHandler
import io.undertow.util.HttpString
import io.undertow.util.Methods.DELETE
import io.undertow.util.Methods.GET
import io.undertow.util.Methods.PATCH
import io.undertow.util.Methods.POST
import io.undertow.util.Methods.PUT

typealias Handler = (HttpServerExchange) -> Unit

class RoutingBuilder {

    private val templates = mutableMapOf<String, Map<HttpString, Handler>>()

    fun get(template: String, httpHandler: Handler) {
        templates[template] = mapOf(GET to httpHandler)
    }

    fun post(template: String, httpHandler: Handler) {
        templates[template] = mapOf(POST to httpHandler)
    }

    fun put(template: String, httpHandler: Handler) {
        templates[template] = mapOf(PUT to httpHandler)
    }

    fun patch(template: String, httpHandler: Handler) {
        templates[template] = mapOf(PATCH to httpHandler)
    }

    fun delete(template: String, httpHandler: Handler) {
        templates[template] = mapOf(DELETE to httpHandler)
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