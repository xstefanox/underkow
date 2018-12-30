package io.github.xstefanox.underkow.dsl

import io.undertow.server.RoutingHandler

/**
 * Bind a [RoutingBuilder] with its initializer function.
 */
internal data class RouteInitializer(
    val routingBuilder: RoutingBuilder,
    val block: RoutingBuilder.() -> Unit
) {
    /**
     * Build the [RoutingHandler] defined by this initializing function applied to this [RoutingBuilder].
     */
    fun build(): RoutingHandler = routingBuilder.apply(block).build()
}
