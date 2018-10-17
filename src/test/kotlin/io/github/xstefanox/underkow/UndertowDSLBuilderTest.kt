package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.undertow
import io.kotlintest.specs.StringSpec

class UndertowDSLBuilderTest : StringSpec({

    "Undertow DSL builder should return an Undertow instance" {

        val undertow = undertow(8282, "0.0.0.0") {

        }

        try {
            undertow.start()
        } finally {
            undertow.stop()
        }
    }
})
