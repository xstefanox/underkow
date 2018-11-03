package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.TEST_HTTP_PORT
import io.github.xstefanox.underkow.test.assert
import io.github.xstefanox.underkow.test.mockHandler
import io.github.xstefanox.underkow.test.request
import io.kotlintest.specs.StringSpec
import io.mockk.verify
import io.undertow.util.Methods.DELETE
import io.undertow.util.Methods.GET
import io.undertow.util.Methods.PATCH
import io.undertow.util.Methods.POST
import io.undertow.util.Methods.PUT
import io.undertow.util.StatusCodes.OK

class DSLTest : StringSpec({

    "Undertow DSL builder should return an Undertow instance" {

        val undertow = undertow(TEST_HTTP_PORT) {
        }

        try {
            undertow.start()
        } finally {
            undertow.stop()
        }
    }

    "configuring a GET request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {
            get("/test", httpHandler)
        } assert {

            request(
                method = GET,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "GET requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {
            get("/test") {
                httpHandler.handleRequest(it)
            }
        } assert {

            request(
                method = GET,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "configuring a POST request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {
            post("/test", httpHandler)
        } assert {

            request(
                method = POST,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "POST requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {
            post("/test") {
                httpHandler.handleRequest(it)
            }
        } assert {

            request(
                method = POST,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "configuring a PUT request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {
            put("/test", httpHandler)
        } assert {

            request(
                method = PUT,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "PUT requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {
            put("/test") {
                httpHandler.handleRequest(it)
            }
        } assert {

            request(
                method = PUT,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "configuring a PATCH request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {
            patch("/test", httpHandler)
        } assert {

            request(
                method = PATCH,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "PATCH requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {
            patch("/test") {
                httpHandler.handleRequest(it)
            }
        } assert {

            request(
                method = PATCH,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "configuring a DELETE request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {
            delete("/test", httpHandler)
        } assert {

            request(
                method = DELETE,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "DELETE requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {

            delete("/test") {
                httpHandler.handleRequest(it)
            }
        } assert {

            request(
                method = DELETE,
                path = "/test",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "routes should be grouped by path prefix" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(TEST_HTTP_PORT) {

            group("/prefix") {

                get("/test1", httpHandler1)

                get("/test2", httpHandler2)
            }
        } assert {

            request(
                method = GET,
                path = "/prefix/test1",
                expect = OK
            )

            request(
                method = GET,
                path = "/prefix/test2",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler1.handleRequest(any()) }
        verify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    "multiple groups could be defined in the same block" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(TEST_HTTP_PORT) {

            group("/prefix1") {

                get("/test1", httpHandler1)
            }

            group("/prefix2") {

                get("/test2", httpHandler2)
            }
        } assert {

            request(
                method = GET,
                path = "/prefix1/test1",
                expect = OK
            )

            request(
                method = GET,
                path = "/prefix2/test2",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler1.handleRequest(any()) }
        verify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    "nesting groups should nest routes" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(TEST_HTTP_PORT) {

            group("/prefix1") {

                get("/test1", httpHandler1)

                group("/prefix2") {

                    get("/test2", httpHandler2)
                }
            }
        } assert {

            request(
                method = GET,
                path = "/prefix1/test1",
                expect = OK
            )

            request(
                method = GET,
                path = "/prefix1/prefix2/test2",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler1.handleRequest(any()) }
        verify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    "base prefix should be applied to all routes" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(TEST_HTTP_PORT, base = "/base") {

            group("/prefix1") {

                get("/test1", httpHandler1)
            }

            group("/prefix2") {

                get("/test2", httpHandler2)
            }
        } assert {

            request(
                method = GET,
                path = "/base/prefix1/test1",
                expect = OK
            )

            request(
                method = GET,
                path = "/base/prefix2/test2",
                expect = OK
            )
        }

        verify(exactly = 1) { httpHandler1.handleRequest(any()) }
        verify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }
})
