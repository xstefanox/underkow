package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.chain.HandlerChain
import io.github.xstefanox.underkow.exception.SuspendingExceptionHandler
import io.github.xstefanox.underkow.exception.UnhandledExceptionHandler
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
import kotlin.reflect.KClass

private val UNHANDLED_EXCEPTION_HANDLER = UnhandledExceptionHandler()

class RoutingBuilder(prefix: String = "", private val filters: Collection<SuspendingHttpHandler> = emptyList()) {

    private val logger: Logger = LoggerFactory.getLogger(RoutingBuilder::class.java)

    private val prefix: String = prefix.trim()

    private val templates = mutableMapOf<String, MutableMap<HttpString, SuspendingHttpHandler>>()

    private val paths = mutableListOf<RoutingHandler>()

    private val exceptions = mutableMapOf<KClass<out Throwable>, SuspendingHttpHandler>()

    init {
        require(this.prefix.isEmpty() || this.prefix.isNotBlank()) {
            "prefix must not be blank"
        }
    }

    fun get(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(GET, template, handler)

    fun get(template: String, handler: SuspendingHttpHandler) = addHandler(GET, template, handler)

    fun get(template: String, handler: HttpHandler) = addHandler(GET, template, handler)

    fun post(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(POST, template, handler)

    fun post(template: String, handler: SuspendingHttpHandler) = addHandler(POST, template, handler)

    fun post(template: String, handler: HttpHandler) = addHandler(POST, template, handler)

    fun put(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(PUT, template, handler)

    fun put(template: String, handler: SuspendingHttpHandler) = addHandler(PUT, template, handler)

    fun put(template: String, handler: HttpHandler) = addHandler(PUT, template, handler)

    fun patch(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(PATCH, template, handler)

    fun patch(template: String, handler: SuspendingHttpHandler) = addHandler(PATCH, template, handler)

    fun patch(template: String, handler: HttpHandler) = addHandler(PATCH, template, handler)

    fun delete(template: String, handler: suspend (HttpServerExchange) -> Unit) = addHandler(DELETE, template, handler)

    fun delete(template: String, handler: SuspendingHttpHandler) = addHandler(DELETE, template, handler)

    fun delete(template: String, handler: HttpHandler) = addHandler(DELETE, template, handler)

    fun build(): RoutingHandler {

        val routingHandler = RoutingHandler()

        templates.forEach { template, map ->
            map.forEach { method, handler ->
                logger.info("found route $method $template")
                routingHandler.add(method, template, HandlerChain(filters + handler, SuspendingExceptionHandler(exceptions, UNHANDLED_EXCEPTION_HANDLER)))
            }
        }

        paths.forEach { group ->
            routingHandler.addAll(group)
        }

        return routingHandler
    }

    fun path(prefix: String, vararg filters: SuspendingHttpHandler, init: RoutingBuilder.() -> Unit) {

        require(prefix.isNotBlank()) {
            "prefix must not be blank"
        }

        paths += RoutingBuilder(
            this.prefix + prefix.trim(),
            this.filters + filters.toList())
            .apply(init)
            .build()
    }

    private fun addHandler(method: HttpString, template: String, handler: SuspendingHttpHandler) {

        require(template.isEmpty() || template.isNotBlank()) {
            "prefix must not be blank"
        }

        val pathHandlers = templates.computeIfAbsent(prefix + template) {
            mutableMapOf()
        }

        pathHandlers[method] = handler
    }

    private fun addHandler(method: HttpString, template: String, handler: HttpHandler) {
        addHandler(method, template, HttpHandlerAdapter(handler))
    }

    private fun addHandler(method: HttpString, template: String, handler: suspend (HttpServerExchange) -> Unit) {
        addHandler(method, template, FunctionHandlerAdapter(handler))
    }

    fun on(exception: KClass<out Exception>, handler: SuspendingHttpHandler) {
        exceptions[exception] = handler
    }

    inline fun <reified T : Exception> on(handler: SuspendingHttpHandler) {
        on(T::class, handler)
    }

    inline fun <reified T : Exception> on(handler: HttpHandler) {
        on(T::class, HttpHandlerAdapter(handler))
    }

    inline fun <reified T : Exception> on(noinline handler: suspend (HttpServerExchange) -> Unit) {
        on(T::class, FunctionHandlerAdapter(handler))
    }
}
