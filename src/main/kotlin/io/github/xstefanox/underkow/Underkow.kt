@file:JvmName("Underkow")

package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.dsl.ServerBuilder
import io.undertow.Undertow

/**
 * Build an [Undertow] instance using a dedicated DSL.
 *
 * @param init the function used to configure the server routing.
 * @return the initialized Undertow server instance.
 */
fun undertow(init: ServerBuilder.() -> Unit): Undertow = ServerBuilder().apply(init).build()
