package io.github.xstefanox.underkow.exception

import io.github.xstefanox.underkow.test.coShouldThrow
import io.github.xstefanox.underkow.test.mockExchange
import io.github.xstefanox.underkow.test.mockHandler
import io.kotlintest.specs.StringSpec
import io.mockk.coVerify
import io.mockk.every
import io.undertow.server.handlers.ExceptionHandler.THROWABLE
import kotlinx.coroutines.runBlocking

class SuspendingExceptionHandlerTest : StringSpec({

    class AnException : Exception()

    "an exchange with not throwable attched cannot be handled" {

        val exceptionHandler = SuspendingExceptionHandler(emptyMap())

        coShouldThrow<ThrowableNotAttachedException> {
            exceptionHandler.handleRequest(mockExchange())
        }
    }

    "exception handled by the exception handler should trigger the associated handler" {

        val exception = AnException()
        val handler = mockHandler()
        val exchange = mockExchange().apply {
            every {
                getAttachment(THROWABLE)
            } answers {
                exception
            }
        }

        val exceptionHandler = SuspendingExceptionHandler(mapOf(
            AnException::class to handler
        ))

        runBlocking {
            exceptionHandler.handleRequest(exchange)
        }

        coVerify {
            handler.handleRequest(eq(exchange))
        }
    }

    "exception not handled by the exception handler should be rethrown" {

        val exception = AnException()

        val exchange = mockExchange().apply {
            every {
                getAttachment(THROWABLE)
            } answers {
                exception
            }
        }

        val exceptionHandler = SuspendingExceptionHandler(emptyMap())

        coShouldThrow<AnException> {
            exceptionHandler.handleRequest(exchange)
        }
    }
})
