package io.github.xstefanox.underkow

import io.undertow.Undertow

/**
 * Default is to listen to requests directed to any host.
 */
const val DEFAULT_HOST = "0.0.0.0"

/**
 * Default is to not apply any prefix to the configured routes.
 */
const val DEFAULT_PREFIX = ""

/**
 * Build an [Undertow] instance using a dedicated DSL.
 *
 * @param port the TCP port used to listen for incoming connections.
 * @param host the IP destination address used to listen for incoming requests; defaults to [DEFAULT_HOST].
 * @param base the base uri used to prefix every route that will be defined using the provided DSL.
 * @param init the function used to configure the server routing.
 * @return the initialized Undertow server instance.
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
