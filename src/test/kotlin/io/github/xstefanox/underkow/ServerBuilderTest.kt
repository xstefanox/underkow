package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.dsl.ServerBuilder
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class ServerBuilderTest : StringSpec({

    "routing definition evaluation should happen on builder completion only" {

        class TestException : Exception()

        val serverBuilder = ServerBuilder()

        serverBuilder.routing {
            throw TestException()
        }

        shouldThrow<TestException> {
            serverBuilder.build()
        }
    }
})
