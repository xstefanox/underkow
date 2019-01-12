package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.dsl.ServerBuilder
import io.kotlintest.shouldThrow
import org.junit.jupiter.api.Test

internal class ServerBuilderTest {

    @Test
    fun `routing definition evaluation should happen on builder completion only`() {

        class TestException : Exception()

        val serverBuilder = ServerBuilder()

        serverBuilder.routing {
            throw TestException()
        }

        shouldThrow<TestException> {
            serverBuilder.build()
        }
    }
}
