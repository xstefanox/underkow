package io.github.xstefanox.underkow.test

import io.undertow.Undertow

/**
 * Allow running a list of assertions in the scope of a running Undertow instance, ensuring that the instance will be
 * automatically shut down upon completion.
 */
infix fun Undertow.assert(block: () -> Unit) {

    start()

    try {
        block()
    } finally {
        stop()
    }
}
