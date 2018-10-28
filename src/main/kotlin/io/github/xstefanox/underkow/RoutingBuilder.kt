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

class RoutingBuilder(private val prefix: String = "") {

    private val templates = mutableMapOf<String, Map<HttpString, HttpHandler>>()

    fun get(template: String, handler: (HttpServerExchange) -> Unit) = addHandler(GET, template, handler)

    fun get(template: String, handler: HttpHandler) = addHandler(GET, template, handler)

    fun post(template: String, handler: (HttpServerExchange) -> Unit) = addHandler(POST, template, handler)

    fun post(template: String, handler: HttpHandler) = addHandler(POST, template, handler)

    fun put(template: String, handler: (HttpServerExchange) -> Unit) = addHandler(PUT, template, handler)

    fun put(template: String, handler: HttpHandler) = addHandler(PUT, template, handler)

    fun patch(template: String, handler: (HttpServerExchange) -> Unit) = addHandler(PATCH, template, handler)

    fun patch(template: String, handler: HttpHandler) = addHandler(PATCH, template, handler)

    fun delete(template: String, handler: (HttpServerExchange) -> Unit) = addHandler(DELETE, template, handler)

    fun delete(template: String, handler: HttpHandler) = addHandler(DELETE, template, handler)

    fun build(): RoutingHandler {

        val routingHandler = RoutingHandler()

        templates.forEach { template, map ->
            map.forEach { method, handler ->
                routingHandler.add(method, template, handler)
            }
        }

        return routingHandler
    }

    private fun addHandler(method: HttpString, template: String, handler: HttpHandler) {
        templates[prefix + template] = mapOf(method to handler)
    }

    private fun addHandler(method: HttpString, template: String, handler: (HttpServerExchange) -> Unit) {
        addHandler(method, template, HttpHandler {
            handler.invoke(it)
        })
    }

    fun group(prefix: String, init: RoutingBuilder.() -> Unit) {
        templates.putAll(RoutingBuilder(prefix).apply(init).templates)
    }
}
