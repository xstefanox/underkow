package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.AnException
import io.github.xstefanox.underkow.test.AnotherException
import io.github.xstefanox.underkow.test.TEST_HTTP_PORT
import io.github.xstefanox.underkow.test.assert
import io.github.xstefanox.underkow.test.mockFilter
import io.github.xstefanox.underkow.test.mockHandler
import io.github.xstefanox.underkow.test.mockStandardHandler
import io.github.xstefanox.underkow.test.request
import io.github.xstefanox.underkow.test.throwing
import io.mockk.Ordering.ORDERED
import io.mockk.coVerify
import io.undertow.util.Methods.DELETE
import io.undertow.util.Methods.GET
import io.undertow.util.Methods.HEAD
import io.undertow.util.Methods.OPTIONS
import io.undertow.util.Methods.PATCH
import io.undertow.util.Methods.POST
import io.undertow.util.Methods.PUT
import io.undertow.util.StatusCodes.INTERNAL_SERVER_ERROR
import io.undertow.util.StatusCodes.NOT_FOUND
import io.undertow.util.StatusCodes.OK
import org.junit.jupiter.api.Test

internal class DSLTest {

    @Test
    fun `Undertow DSL builder should return an Undertow instance`() {

        val undertow = undertow {
            port = TEST_HTTP_PORT
        }

        try {
            undertow.start()
        } finally {
            undertow.stop()
        }
    }

    @Test
    fun `configuring a HEAD request should add the handler to the server`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                head("/test", httpHandler)
            }
        } assert {

            request(
                method = HEAD,
                path = "/test",
                expect = OK
            )
        }

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    @Test
    fun `HEAD requests could be defined inline without the need of an explicit cast`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                head("/test") {
                    httpHandler.handleRequest(it)
                }
            }
        } assert {

            request(
                method = HEAD,
                path = "/test",
                expect = OK
            )
        }

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    @Test
    fun `HEAD requests could be writter as regular, non-suspending HttpHandler`() {

        val httpHandler = mockStandardHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                head("/test", httpHandler)
            }
        } assert {

            request(
                method = HEAD,
                path = "/test",
                expect = OK
            )
        }
    }

    @Test
    fun `configuring a GET request should add the handler to the server`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                get("/test", httpHandler)
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

    @Test
    fun `GET requests could be defined inline without the need of an explicit cast`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                get("/test") {
                    httpHandler.handleRequest(it)
                }
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

    @Test
    fun `GET requests could be writter as regular, non-suspending HttpHandler`() {

        val httpHandler = mockStandardHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                get("/test", httpHandler)
            }
        } assert {

            request(
                method = GET,
                path = "/test",
                expect = OK
            )
        }
    }

    @Test
    fun `configuring a POST request should add the handler to the server`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                post("/test", httpHandler)
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

    @Test
    fun `POST requests could be defined inline without the need of an explicit cast`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                post("/test") {
                    httpHandler.handleRequest(it)
                }
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

    @Test
    fun `POST requests could be writter as regular, non-suspending HttpHandler`() {

        val httpHandler = mockStandardHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                post("/test", httpHandler)
            }
        } assert {

            request(
                method = POST,
                path = "/test",
                expect = OK
            )
        }
    }

    @Test
    fun `configuring a PUT request should add the handler to the server`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                put("/test", httpHandler)
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

    @Test
    fun `PUT requests could be defined inline without the need of an explicit cast`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                put("/test") {
                    httpHandler.handleRequest(it)
                }
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

    @Test
    fun `PUT requests could be writter as regular, non-suspending HttpHandler`() {

        val httpHandler = mockStandardHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                put("/test", httpHandler)
            }
        } assert {

            request(
                method = PUT,
                path = "/test",
                expect = OK
            )
        }
    }

    @Test
    fun `configuring a PATCH request should add the handler to the server`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                patch("/test", httpHandler)
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

    @Test
    fun `PATCH requests could be defined inline without the need of an explicit cast`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                patch("/test") {
                    httpHandler.handleRequest(it)
                }
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

    @Test
    fun `PATCH requests could be writter as regular, non-suspending HttpHandler`() {

        val httpHandler = mockStandardHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                patch("/test", httpHandler)
            }
        } assert {

            request(
                method = PATCH,
                path = "/test",
                expect = OK
            )
        }
    }

    @Test
    fun `configuring a DELETE request should add the handler to the server`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                delete("/test", httpHandler)
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

    @Test
    fun `DELETE requests could be defined inline without the need of an explicit cast`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                delete("/test") {
                    httpHandler.handleRequest(it)
                }
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

    @Test
    fun `DELETE requests could be writter as regular, non-suspending HttpHandler`() {

        val httpHandler = mockStandardHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                delete("/test", httpHandler)
            }
        } assert {

            request(
                method = DELETE,
                path = "/test",
                expect = OK
            )
        }
    }

    @Test
    fun `configuring a OPTIONS request should add the handler to the server`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                options("/test", httpHandler)
            }
        } assert {

            request(
                method = OPTIONS,
                path = "/test",
                expect = OK
            )
        }

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    @Test
    fun `OPTIONS requests could be defined inline without the need of an explicit cast`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                options("/test") {
                    httpHandler.handleRequest(it)
                }
            }
        } assert {

            request(
                method = OPTIONS,
                path = "/test",
                expect = OK
            )
        }

        coVerify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    @Test
    fun `OPTIONS requests could be writter as regular, non-suspending HttpHandler`() {

        val httpHandler = mockStandardHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                options("/test", httpHandler)
            }
        } assert {

            request(
                method = OPTIONS,
                path = "/test",
                expect = OK
            )
        }
    }

    @Test
    fun `routes should be grouped by path prefix`() {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                path("/prefix") {

                    get("/test1", httpHandler1)

                    get("/test2", httpHandler2)
                }
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

    @Test
    fun `multiple path groups could be defined in the same block`() {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                path("/prefix1") {

                    get("/test1", httpHandler1)
                }

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
                path = "/prefix2/test2",
                expect = OK
            )
        }

        coVerify(exactly = 1) { httpHandler1.handleRequest(any()) }
        coVerify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    @Test
    fun `nesting path groups should nest routes`() {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                path("/prefix1") {

                    get("/test1", httpHandler1)

                    path("/prefix2") {

                        get("/test2", httpHandler2)
                    }
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

    @Test
    fun `base prefix should be applied to all routes`() {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow {

            port = TEST_HTTP_PORT

            routing("/base") {

                path("/prefix1") {

                    get("/test1", httpHandler1)
                }

                path("/prefix2") {

                    get("/test2", httpHandler2)
                }
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

    @Test
    fun `a filter should be applied to every nested route`() {

        val httpHandler = mockHandler()
        val filter = mockFilter()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                path("/prefix", filter) {

                    get("/test", httpHandler)
                }
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

    @Test
    fun `a filter should not be applied to non nested route`() {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()
        val filter = mockFilter()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                path("/prefix", filter) {

                    get("/test1", httpHandler1)
                }

                get("/test2", httpHandler2)
            }
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

    @Test
    fun `a filter should be applied to nested path groups`() {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()
        val httpHandler3 = mockHandler()
        val filter1 = mockFilter()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                path("/prefix1", filter1) {

                    get("/test1", httpHandler1)

                    path("/prefix2") {

                        get("/test2", httpHandler2)
                    }
                }

                get("/test3", httpHandler3)
            }
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

    @Test
    fun `nested filters should be applied in chain`() {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()
        val httpHandler3 = mockHandler()
        val filter1 = mockFilter()
        val filter2 = mockFilter()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                path("/prefix1", filter1) {

                    get("/test1", httpHandler1)

                    path("/prefix2", filter2) {

                        get("/test2", httpHandler2)
                    }
                }

                get("/test3", httpHandler3)
            }
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

    @Test
    fun `multiple filters declared on the same path should be applied in the given order`() {

        val filter1 = mockFilter()
        val filter2 = mockFilter()
        val httpHandler = mockHandler()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                path("/prefix", filter1, filter2) {

                    get("/test", httpHandler)
                }
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

    @Test
    fun `a route should be handled by more than one method`() {

        val httpHandler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                get("/test", httpHandler)
                post("/test", httpHandler)
                put("/test", httpHandler)
                patch("/test", httpHandler)
                delete("/test", httpHandler)
            }
        } assert {

            request(
                method = GET,
                path = "/test",
                expect = OK
            )

            request(
                method = POST,
                path = "/test",
                expect = OK
            )

            request(
                method = PUT,
                path = "/test",
                expect = OK
            )

            request(
                method = PATCH,
                path = "/test",
                expect = OK
            )

            request(
                method = DELETE,
                path = "/test",
                expect = OK
            )
        }
    }

    @Test
    fun `exceptions should be handled by the configured handlers`() {

        val handler = mockHandler().throwing(AnException())
        val testException1Handler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                get("/test", handler)
                on<AnException>(testException1Handler)
            }
        } assert {

            request(
                method = GET,
                path = "/test",
                expect = OK
            )
        }

        coVerify {
            testException1Handler.handleRequest(any())
        }
    }

    @Test
    fun `when no exception handled are declared, any exception should return an INTERNAL SERVER ERROR`() {

        val handler = mockHandler().throwing(AnException())

        undertow {

            port = TEST_HTTP_PORT

            routing {

                get("/test", handler)
            }
        } assert {
            request(
                method = GET,
                path = "/test",
                expect = INTERNAL_SERVER_ERROR
            )
        }
    }

    @Test
    fun `an exception not handled by a declared handler should return an INTERNAL SERVER ERROR`() {

        val handler = mockHandler().throwing(AnException())
        val testException2Handler = mockHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                get("/test", handler)
                on<AnotherException>(testException2Handler)
            }
        } assert {
            request(
                method = GET,
                path = "/test",
                expect = INTERNAL_SERVER_ERROR
            )
        }
    }

    @Test
    fun `exception handler should be applied only to handlers inside the path groups into which they are declared`() {

        val handler1 = mockHandler().throwing(AnException())
        val handler2 = mockHandler().throwing(AnException())
        val testException1Handler = mockHandler()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                get("/test1", handler1)

                path("/prefix2") {
                    get("/test2", handler2)
                    on<AnException>(testException1Handler)
                }
            }
        } assert {

            request(
                method = GET,
                path = "/prefix2/test2",
                expect = OK
            )

            request(
                method = GET,
                path = "/test1",
                expect = INTERNAL_SERVER_ERROR
            )
        }
    }

    @Test
    fun `exception handlers could be writter as regular, non-suspending HttpHandler`() {

        val handler = mockHandler().throwing(AnException())
        val testException1Handler = mockStandardHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                get("/test", handler)
                on<AnException>(testException1Handler)
            }
        } assert {

            request(
                method = GET,
                path = "/test",
                expect = OK
            )
        }

        coVerify {
            testException1Handler.handleRequest(any())
        }
    }

    @Test
    fun `exception handlers could be defined inline without the need of an explicit cast`() {

        val handler = mockHandler().throwing(AnException())
        val testException1Handler = mockStandardHandler()

        undertow {
            port = TEST_HTTP_PORT
            routing {
                get("/test", handler)
                on<AnException> {
                    testException1Handler.handleRequest(it)
                }
            }
        } assert {

            request(
                method = GET,
                path = "/test",
                expect = OK
            )
        }

        coVerify {
            testException1Handler.handleRequest(any())
        }
    }

    @Test
    fun `exception handlers should be inherited by nested builders`() {

        val handler = mockHandler().throwing(AnException())
        val exceptionHandler1 = mockHandler()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                on<AnException>(exceptionHandler1)
                get("/test", handler)

                path("/nested") {
                    get("/test", handler)
                }
            }
        } assert {
            request(
                method = GET,
                path = "/nested/test",
                expect = OK
            )
        }

        coVerify {
            exceptionHandler1.handleRequest(any())
        }
    }

    @Test
    fun `nested exception handler should override handlers defined by parent builders`() {

        val handler = mockHandler().throwing(AnException())
        val exceptionHandler1 = mockHandler()
        val exceptionHandler2 = mockHandler()

        undertow {

            port = TEST_HTTP_PORT

            routing {

                on<AnException>(exceptionHandler1)
                get("/test", handler)

                path("/nested") {
                    on<AnException>(exceptionHandler2)
                    get("/test", handler)
                }
            }
        } assert {
            request(
                method = GET,
                path = "/nested/test",
                expect = OK
            )
        }

        coVerify {
            exceptionHandler2.handleRequest(any())
        }

        coVerify(exactly = 0) {
            exceptionHandler1.handleRequest(any())
        }
    }

    @Test
    fun `routing definition should be overridden if defined multiple times`() {

        val handler = mockHandler()

        undertow {

            port = TEST_HTTP_PORT

            routing("/overridden") {
                get("/invalid", handler)
            }

            routing {
                get("/valid", handler)
            }
        } assert {

            request(
                method = GET,
                path = "/overridden/invalid",
                expect = NOT_FOUND
            )

            request(
                method = GET,
                path = "/valid",
                expect = OK
            )
        }
    }
}
