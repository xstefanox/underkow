package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.chain.HandlerChain
import io.github.xstefanox.underkow.exception.SuspendingExceptionHandler
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

class RoutingBuilder(private val prefix: String = "", private val filters: Collection<SuspendingHttpHandler> = emptyList()) {

    private val logger: Logger = LoggerFactory.getLogger(RoutingBuilder::class.java)

    private val templates = mutableMapOf<String, Map<HttpString, SuspendingHttpHandler>>()

    private val paths = mutableListOf<RoutingHandler>()

    fun get(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(GET, template, handler)

    fun get(template: String, handler: SuspendingHttpHandler) = addHandler(GET, template, handler)

    fun post(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(POST, template, handler)

    fun post(template: String, handler: SuspendingHttpHandler) = addHandler(POST, template, handler)

    fun put(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(PUT, template, handler)

    fun put(template: String, handler: SuspendingHttpHandler) = addHandler(PUT, template, handler)

    fun patch(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(PATCH, template, handler)

    fun patch(template: String, handler: SuspendingHttpHandler) = addHandler(PATCH, template, handler)

    fun delete(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(DELETE, template, handler)

    fun delete(template: String, handler: SuspendingHttpHandler) = addHandler(DELETE, template, handler)

    fun build(): RoutingHandler {

        val routingHandler = RoutingHandler()

        templates.forEach { template, map ->
            map.forEach { method, handler ->
                logger.info("found route $method $template")
                routingHandler.add(method, template, HandlerChain(filters + handler, SuspendingExceptionHandler(emptyMap())))
            }
        }

        paths.forEach { group ->
            routingHandler.addAll(group)
        }

        return routingHandler
    }

    fun path(prefix: String, vararg filters: SuspendingHttpHandler, init: RoutingBuilder.() -> Unit) {
        paths += RoutingBuilder(
            this.prefix + prefix,
            this.filters + filters.toList())
            .apply(init)
            .build()
    }

    private fun addHandler(method: HttpString, template: String, handler: SuspendingHttpHandler) {
        templates[prefix + template] = mapOf(method to handler)
    }

    private fun addHandler(method: HttpString, template: String, handler: suspend (HttpServerExchange) -> Unit) {
        addHandler(method, template, object : SuspendingHttpHandler {
            override suspend fun handleRequest(exchange: HttpServerExchange) {
                handler.invoke(exchange)
            }
        })
    }
}
