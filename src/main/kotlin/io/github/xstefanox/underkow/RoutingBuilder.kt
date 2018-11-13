package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.chain.HandlerChain
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.RoutingHandler
import io.undertow.util.HttpString
import io.undertow.util.Methods.DELETE
import io.undertow.util.Methods.GET
import io.undertow.util.Methods.PATCH
import io.undertow.util.Methods.POST
import io.undertow.util.Methods.PUT
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RoutingBuilder(private val prefix: String = "", private val filters: Collection<HttpHandler> = emptyList()) {

    private val logger: Logger = LoggerFactory.getLogger(RoutingBuilder::class.java)

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
                logger.info("found route $method $template")
                routingHandler.add(method, template, HandlerChain(filters + handler))
            }
        }

        paths.forEach { group ->
            routingHandler.addAll(group)
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

    private val paths = mutableListOf<RoutingHandler>()

    fun path(prefix: String, vararg filters: HttpHandler, init: RoutingBuilder.() -> Unit) {
        paths += buildHandler(this.prefix + prefix, this.filters + filters.toList(), init)
    }
}
