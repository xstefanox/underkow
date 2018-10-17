package io.github.xstefanox.underkow

import io.undertow.Undertow
import io.undertow.server.RoutingHandler

fun buildHandler(init: RoutingBuilder.() -> Unit): RoutingHandler {
    return RoutingBuilder().apply(init).build()
}

fun undertow(port: Int, host: String, init: RoutingBuilder.() -> Unit): Undertow {
    return Undertow.builder()
            .addHttpListener(port, host)
            .setHandler(buildHandler(init))
            .build()
}
