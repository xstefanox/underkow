package io.github.xstefanox.underkow

import io.undertow.Undertow

/**
 * Default is to listen to requested directed to any host.
 */
private const val DEFAULT_HOST = "0.0.0.0"

/**
 * Default is to not apply any prefix to the configured routes
 */
private const val DEFAULT_PREFIX = ""

/**
 * Build an [Undertow] instance using a dedicated DSL.
 */
fun undertow(port: Int, host: String = DEFAULT_HOST, base: String = DEFAULT_PREFIX, init: RoutingBuilder.() -> Unit): Undertow {
    return Undertow.builder()
        .addHttpListener(port, host)
        .setHandler(
            RoutingBuilder(base)
                .apply(init)
                .build()
        )
        .build()
}
