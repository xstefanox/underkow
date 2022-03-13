package io.github.xstefanox.underkow.exception

import io.github.xstefanox.underkow.test.AnException
import io.github.xstefanox.underkow.test.coShouldThrow
import io.github.xstefanox.underkow.test.mockExchange
import io.github.xstefanox.underkow.test.mockHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.undertow.server.handlers.ExceptionHandler.THROWABLE
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class SuspendingExceptionHandlerTest {

    @Test
    fun `an exchange without attached throwable cannot be handled`() {

        val unhandledExceptionHandler = mockk<UnhandledExceptionHandler>()

        val exceptionHandler = SuspendingExceptionHandler(emptyMap(), unhandledExceptionHandler)

        coShouldThrow<ThrowableNotAttachedException> {
            exceptionHandler.handleRequest(mockExchange())
        }
    }

    @Test
    fun `an exception handled by the exception handler should trigger the associated handler`() {

        val exception = AnException()
        val handler = mockHandler()
        val exchange = mockExchange().apply {
            every {
                getAttachment(THROWABLE)
            } answers {
                exception
            }
        }
        val unhandledExceptionHandler = mockk<UnhandledExceptionHandler>()

        val exceptionHandler = SuspendingExceptionHandler(
            mapOf(
                AnException::class to handler
            ),
            unhandledExceptionHandler
        )

        runBlocking {
            exceptionHandler.handleRequest(exchange)
        }

        coVerify {
            handler.handleRequest(eq(exchange))
        }
    }

    @Test
    fun `an exception not handled by the exception handler should complete the exception`() {

        val exception = AnException()
        val unhandledExceptionHandler = mockk<UnhandledExceptionHandler>().apply {
            coEvery {
                handleRequest(any())
            } just runs
        }

        val exchange = mockExchange().apply {
            every {
                getAttachment(THROWABLE)
            } answers {
                exception
            }
        }

        runBlocking {
            SuspendingExceptionHandler(emptyMap(), unhandledExceptionHandler).handleRequest(exchange)
        }

        coVerify {
            unhandledExceptionHandler.handleRequest(eq(exchange))
        }
    }
}
