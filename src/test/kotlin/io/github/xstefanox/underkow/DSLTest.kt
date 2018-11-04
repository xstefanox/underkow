package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.TEST_HTTP_PORT
import io.github.xstefanox.underkow.test.assert
import io.github.xstefanox.underkow.test.mockFilter
import io.github.xstefanox.underkow.test.mockHandler
import io.github.xstefanox.underkow.test.request
import io.kotlintest.specs.StringSpec
import io.mockk.Ordering.ORDERED
import io.mockk.coVerify
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
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

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "routes should be grouped by path prefix" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(TEST_HTTP_PORT) {

            path("/prefix") {

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

        coVerify(exactly = 1) { httpHandler1.handleRequest(any()) }
        coVerify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    "multiple path groups could be defined in the same block" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(TEST_HTTP_PORT) {

            path("/prefix1") {

                get("/test1", httpHandler1)
            }

            path("/prefix2") {

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

        coVerify(exactly = 1) { httpHandler1.handleRequest(any()) }
        coVerify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    "nesting path groups should nest routes" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(TEST_HTTP_PORT) {

            path("/prefix1") {

                get("/test1", httpHandler1)

                path("/prefix2") {

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

        coVerify(exactly = 1) { httpHandler1.handleRequest(any()) }
        coVerify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    "base prefix should be applied to all routes" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(TEST_HTTP_PORT, base = "/base") {

            path("/prefix1") {

                get("/test1", httpHandler1)
            }

            path("/prefix2") {

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

        coVerify(exactly = 1) { httpHandler1.handleRequest(any()) }
        coVerify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    "a filter should be applied to every nested route" {

        val httpHandler = mockHandler()
        val filter = mockFilter()

        undertow(TEST_HTTP_PORT) {
            path("/prefix", filter) {

                get("/test", httpHandler)
            }
        } assert {

            request(
                method = GET,
                path = "/prefix/test",
                expect = OK
            )
        }

        coVerify(ordering = ORDERED) {
            filter.handleRequest(any())
            httpHandler.handleRequest(any())
        }
    }

    "a filter should not be applied to non nested route" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()
        val filter = mockFilter()

        undertow(TEST_HTTP_PORT) {

            path("/prefix", filter) {

                get("/test1", httpHandler1)
            }

            get("/test2", httpHandler2)
        } assert {

            request(
                method = GET,
                path = "/test2",
                expect = OK
            )
        }

        coVerify(exactly = 0) { filter.handleRequest(any()) }
        coVerify(exactly = 0) { httpHandler1.handleRequest(any()) }
        coVerify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    "a filter should be applied to nested path groups" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()
        val httpHandler3 = mockHandler()
        val filter1 = mockFilter()

        undertow(TEST_HTTP_PORT) {

            path("/prefix1", filter1) {

                get("/test1", httpHandler1)

                path("/prefix2") {

                    get("/test2", httpHandler2)
                }
            }

            get("/test3", httpHandler3)
        } assert {

            request(
                method = GET,
                path = "/prefix1/prefix2/test2",
                expect = OK
            )
        }

        coVerify(ordering = ORDERED) {
            filter1.handleRequest(any())
            httpHandler2.handleRequest(any())
        }

        coVerify(exactly = 0) { httpHandler1.handleRequest(any()) }
        coVerify(exactly = 0) { httpHandler3.handleRequest(any()) }
    }

    "nested filters should be applied in chain" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()
        val httpHandler3 = mockHandler()
        val filter1 = mockFilter()
        val filter2 = mockFilter()

        undertow(TEST_HTTP_PORT) {

            path("/prefix1", filter1) {

                get("/test1", httpHandler1)

                path("/prefix2", filter2) {

                    get("/test2", httpHandler2)
                }
            }

            get("/test3", httpHandler3)
        } assert {

            request(
                method = GET,
                path = "/prefix1/prefix2/test2",
                expect = OK
            )
        }

        coVerify(ordering = ORDERED) {
            filter1.handleRequest(any())
            filter2.handleRequest(any())
            httpHandler2.handleRequest(any())
        }

        coVerify(exactly = 0) { httpHandler1.handleRequest(any()) }
        coVerify(exactly = 0) { httpHandler3.handleRequest(any()) }
    }

    "multiple filters configured on the same path should be applied in the given order" {

        val filter1 = mockFilter()
        val filter2 = mockFilter()
        val httpHandler = mockHandler()

        undertow(TEST_HTTP_PORT) {

            path("/prefix", filter1, filter2) {

                get("/test", httpHandler)
            }
        } assert {

            request(
                method = GET,
                path = "/prefix/test",
                expect = OK
            )
        }

        coVerify(ordering = ORDERED) {
            filter1.handleRequest(any())
            filter2.handleRequest(any())
            httpHandler.handleRequest(any())
        }
    }
})
