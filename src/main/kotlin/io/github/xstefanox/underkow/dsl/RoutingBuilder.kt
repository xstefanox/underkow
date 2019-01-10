package io.github.xstefanox.underkow.dsl

import io.github.xstefanox.underkow.FunctionHandlerAdapter
import io.github.xstefanox.underkow.HttpHandlerAdapter
import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.github.xstefanox.underkow.chain.HandlerChain
import io.github.xstefanox.underkow.dispatcher.ExchangeDispatcher
import io.github.xstefanox.underkow.exception.SuspendingExceptionHandler
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

/**
 * A builder used to define the routes supported under a configured path prefix.
 *
 * @param prefix the path prefix applied to each route configured by this builder.
 * @param filters the filters applied to each call handled by the routes defined by this builder.
 */
@UndertowDsl
class RoutingBuilder(
    prefix: String = DEFAULT_PREFIX,
    private val filters: Collection<SuspendingHttpHandler> = emptyList(),
    private val dispatcher: ExchangeDispatcher
) {

    private val logger: Logger = LoggerFactory.getLogger(RoutingBuilder::class.java)

    private val prefix: String = prefix.trim()

    private val templates = mutableMapOf<String, MutableMap<HttpString, SuspendingHttpHandler>>()

    private val paths = mutableListOf<RouteInitializer>()

    private val exceptions = mutableMapOf<KClass<out Throwable>, SuspendingHttpHandler>()

    init {
        require(this.prefix.isEmpty() || this.prefix.isNotBlank()) {
            BLANK_PREFIX_REQUIREMENT_MESSAGE
        }
    }

    /**
     * Add a new GET route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests, defined as a lambda function.
     */
    fun get(template: String = DEFAULT_PREFIX, handler: suspend (HttpServerExchange) -> Unit) = addHandler(GET, template, handler)

    /**
     * Add a new GET route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests.
     */
    fun get(template: String = DEFAULT_PREFIX, handler: SuspendingHttpHandler) = addHandler(GET, template, handler)

    /**
     * Add a new GET route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests; it will be wrapped into a [SuspendingHttpHandler].
     */
    fun get(template: String = DEFAULT_PREFIX, handler: HttpHandler) = addHandler(GET, template, handler)

    /**
     * Add a new POST route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests, defined as a lambda function.
     */
    fun post(template: String = DEFAULT_PREFIX, handler: suspend (HttpServerExchange) -> Unit) = addHandler(POST, template, handler)

    /**
     * Add a new POST route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests.
     */
    fun post(template: String = DEFAULT_PREFIX, handler: SuspendingHttpHandler) = addHandler(POST, template, handler)

    /**
     * Add a new POST route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests; it will be wrapped into a [SuspendingHttpHandler].
     */
    fun post(template: String = DEFAULT_PREFIX, handler: HttpHandler) = addHandler(POST, template, handler)

    /**
     * Add a new PUT route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests, defined as a lambda function.
     */
    fun put(template: String = DEFAULT_PREFIX, handler: suspend (HttpServerExchange) -> Unit) = addHandler(PUT, template, handler)

    /**
     * Add a new PUT route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests.
     */
    fun put(template: String = DEFAULT_PREFIX, handler: SuspendingHttpHandler) = addHandler(PUT, template, handler)

    /**
     * Add a new PUT route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests; it will be wrapped into a [SuspendingHttpHandler].
     */
    fun put(template: String = DEFAULT_PREFIX, handler: HttpHandler) = addHandler(PUT, template, handler)

    /**
     * Add a new PATCH route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests, defined as a lambda function.
     */
    fun patch(template: String = DEFAULT_PREFIX, handler: suspend (HttpServerExchange) -> Unit) = addHandler(PATCH, template, handler)

    /**
     * Add a new PATCH route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests.
     */
    fun patch(template: String = DEFAULT_PREFIX, handler: SuspendingHttpHandler) = addHandler(PATCH, template, handler)

    /**
     * Add a new PATCH route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests; it will be wrapped into a [SuspendingHttpHandler].
     */
    fun patch(template: String = DEFAULT_PREFIX, handler: HttpHandler) = addHandler(PATCH, template, handler)

    /**
     * Add a new DELETE route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests, defined as a lambda function.
     */
    fun delete(template: String = DEFAULT_PREFIX, handler: suspend (HttpServerExchange) -> Unit) = addHandler(DELETE, template, handler)

    /**
     * Add a new DELETE route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests.
     */
    fun delete(template: String = DEFAULT_PREFIX, handler: SuspendingHttpHandler) = addHandler(DELETE, template, handler)

    /**
     * Add a new DELETE route to the list of routes configured by this builder.
     *
     * @param template the route template uri.
     * @param handler the handler that will receive the requests; it will be wrapped into a [SuspendingHttpHandler].
     */
    fun delete(template: String = DEFAULT_PREFIX, handler: HttpHandler) = addHandler(DELETE, template, handler)

    /**
     * Begin the definition of a set of routes nested in the given path prefix. Every call to this method overwrites a
     * previously defined nested routing, if present.
     *
     * @param prefix the path prefix that will be applied to each route defined by the nested builder; it cannot be an
     *               empty or blank string.
     * @param filters the collection of filters that will be applied to every request received by the routes defined by
     *                the nested builder.
     * @param init the lambda function used to configure the nested builder.
     */
    fun path(prefix: String, vararg filters: SuspendingHttpHandler, init: RoutingBuilder.() -> Unit) {

        require(prefix.isNotBlank()) {
            BLANK_PREFIX_REQUIREMENT_MESSAGE
        }

        val routingBuilder = RoutingBuilder(
            this.prefix + prefix.trim(),
            this.filters + filters.toList(),
            dispatcher)

        paths += RouteInitializer(routingBuilder, init)
    }

    /**
     * Configure the builder to handle the exceptions of the given class using the given handler.
     *
     * @param exception the class of the exceptions to handle.
     * @param handler the handler used to handle the catched exceptions.
     */
    fun on(exception: KClass<out Exception>, handler: SuspendingHttpHandler) {
        exceptions[exception] = handler
    }

    /**
     * Configure the builder to handle the exceptions of the given class using the given handler.
     *
     * @param T the class of the exceptions to handle.
     * @param handler the handler used to handle the catched exceptions.
     */
    inline fun <reified T : Exception> on(handler: SuspendingHttpHandler) {
        on(T::class, handler)
    }

    /**
     * Configure the builder to handle the exceptions of the given class using the given handler.
     *
     * @param T the class of the exceptions to handle.
     * @param handler the handler used to handle the catched exceptions; it will be wrapped into a
     *                [SuspendingHttpHandler].
     */
    inline fun <reified T : Exception> on(handler: HttpHandler) {
        on(T::class, HttpHandlerAdapter(handler))
    }

    /**
     * Configure the builder to handle the exceptions of the given class using the given handler.
     *
     * @param T the class of the exceptions to handle.
     * @param handler the handler used to handle the catched exceptions; it will be wrapped into a
     *                [SuspendingHttpHandler].
     */
    inline fun <reified T : Exception> on(noinline handler: suspend (HttpServerExchange) -> Unit) {
        on(T::class, FunctionHandlerAdapter(handler))
    }

    /**
     * Build a new [RoutingHandler] from this builder.
     *
     * @return a [RoutingHandler] configured with the DSL defined by this builder.
     */
    fun build(): RoutingHandler {

        val routingHandler = RoutingHandler()

        templates.forEach { template, map ->
            map.forEach { method, handler ->
                logger.debug("found route $method $template")
                routingHandler.add(method, template, HandlerChain(filters + handler, SuspendingExceptionHandler(exceptions, UNHANDLED_EXCEPTION_HANDLER), dispatcher))
            }
        }

        paths.forEach { group ->
            routingHandler.addAll(group.build())
        }

        return routingHandler
    }

    private fun addHandler(method: HttpString, template: String = DEFAULT_PREFIX, handler: SuspendingHttpHandler) {

        require(template.isEmpty() || template.isNotBlank()) {
            BLANK_PREFIX_REQUIREMENT_MESSAGE
        }

        val pathHandlers = templates.computeIfAbsent(prefix + template) {
            mutableMapOf()
        }

        pathHandlers[method] = handler
    }

    private fun addHandler(method: HttpString, template: String = DEFAULT_PREFIX, handler: HttpHandler) {
        addHandler(method, template, HttpHandlerAdapter(handler))
    }

    private fun addHandler(method: HttpString, template: String = DEFAULT_PREFIX, handler: suspend (HttpServerExchange) -> Unit) {
        addHandler(method, template, FunctionHandlerAdapter(handler))
    }
}