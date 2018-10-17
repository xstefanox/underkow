package io.github.xstefanox.underkow

import io.undertow.server.HttpServerExchange

typealias Handler = (HttpServerExchange) -> Unit
