package io.github.xstefanox.underkow.dsl

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.undertow.Undertow

/**
 * The builder used to configure the server routing.
 *
 * @param filters the collection of filters that will be applied to every request received by the routes define by this
 *                builder; they will be applied in order, so be sure to use an ordered collection.
 */
@UndertowDsl
class ServerBuilder(
    private val filters: Collection<SuspendingHttpHandler> = emptyList()
) {

    /**
     * The TCP port used to listen for incoming connections; defaults to [DEFAULT_PORT].
     */
    var port = DEFAULT_PORT

    /**
     * The IP destination address used to listen for incoming requests; defaults to [DEFAULT_HOST].
     */
    var host: String = DEFAULT_HOST

    private var routingBuilder: RoutingBuilder? = null

    /**
     * Begin the definition of the server routing. Every call to this method overwrites a previously defined routing, if
     * present.
     *
     * @param prefix the path prefix that will be applied to each route defined by the nested builder.
     * @param filters the collection of filters that will be applied to every request received by the server.
     * @param init the lambda function used to configure the routing.
     */
    fun routing(prefix: String = DEFAULT_PREFIX, vararg filters: SuspendingHttpHandler, init: RoutingBuilder.() -> Unit) {
        routingBuilder = RoutingBuilder(prefix, filters.toList()).apply(init)
    }

    /**
     * Build a new [Undertow] instance from this builder.
     *
     * @return an [Undertow] server configured with the DSL defined by this builder.
     */
    fun build(): Undertow = Undertow.builder()
        .setHandler(routingBuilder?.build())
        .addHttpListener(port, host)
        .build()
}
