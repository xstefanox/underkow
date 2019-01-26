package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.dsl.ServerBuilder
import io.github.xstefanox.underkow.test.AnException
import io.kotlintest.shouldThrow
import org.junit.jupiter.api.Test

internal class ServerBuilderTest {

    @Test
    fun `routing definition evaluation should happen on builder completion only`() {

        val serverBuilder = ServerBuilder()

        serverBuilder.routing {
            throw AnException()
        }

        shouldThrow<AnException> {
            serverBuilder.build()
        }
    }
}
