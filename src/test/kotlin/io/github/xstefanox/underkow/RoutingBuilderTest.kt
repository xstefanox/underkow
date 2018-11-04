package io.github.xstefanox.underkow

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec

class RoutingBuilderTest : StringSpec({

    "routing builder should produce a new object on every execution" {

        val routingBuilder = RoutingBuilder()

        val routingHandler1 = routingBuilder.build()
        val routingHandler2 = routingBuilder.build()

        routingHandler1 shouldNotBe routingHandler2
    }
})
