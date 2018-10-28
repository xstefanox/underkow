package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.assert
import io.github.xstefanox.underkow.test.mockHandler
import io.kotlintest.specs.StringSpec
import io.mockk.verify
import io.restassured.RestAssured
import org.apache.http.HttpStatus.SC_OK

class DSLTest : StringSpec({

    "Undertow DSL builder should return an Undertow instance" {

        val undertow = undertow(8282, "0.0.0.0") {

        }

        try {
            undertow.start()
        } finally {
            undertow.stop()
        }
    }

    "configuring a GET request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            get("/test", httpHandler)

        } assert {

            RestAssured.given()
                .get("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "GET requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            get("/test") {
                httpHandler.handleRequest(it)
            }

        } assert {

            RestAssured.given()
                .get("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "configuring a POST request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            post("/test", httpHandler)

        } assert {

            RestAssured.given()
                .post("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "POST requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            post("/test") {
                httpHandler.handleRequest(it)
            }

        } assert {

            RestAssured.given()
                .post("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "configuring a PUT request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            put("/test", httpHandler)

        } assert {

            RestAssured.given()
                .put("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "PUT requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            put("/test") {
                httpHandler.handleRequest(it)
            }

        } assert {

            RestAssured.given()
                .put("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "configuring a PATCH request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            patch("/test", httpHandler)

        } assert {

            RestAssured.given()
                .patch("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "PATCH requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            patch("/test") {
                httpHandler.handleRequest(it)
            }

        } assert {

            RestAssured.given()
                .patch("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "configuring a DELETE request should add the handler to the server" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            delete("/test", httpHandler)

        } assert {

            RestAssured.given()
                .delete("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "DELETE requests could be defined inline without the need of an explicit cast" {

        val httpHandler = mockHandler()

        undertow(8282, "0.0.0.0") {

            delete("/test") {
                httpHandler.handleRequest(it)
            }

        } assert {

            RestAssured.given()
                .delete("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler.handleRequest(any()) }
    }

    "routes should be grouped by path prefix" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(8282, "0.0.0.0") {

            group("/prefix") {

                get("/test1", httpHandler1)

                get("/test2", httpHandler2)
            }

        } assert {

            RestAssured.given()
                .get("http://localhost:8282/prefix/test1")
                .then()
                .assertThat()
                .statusCode(SC_OK)

            RestAssured.given()
                .get("http://localhost:8282/prefix/test2")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }

        verify(exactly = 1) { httpHandler1.handleRequest(any()) }
        verify(exactly = 1) { httpHandler2.handleRequest(any()) }
    }

    "multiple groups could be defined in the same block" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(8282, "0.0.0.0") {

            group("/prefix1") {

                get("/test1", httpHandler1)
            }

            group("/prefix2") {

                get("/test2", httpHandler2)
            }

        } assert {

            RestAssured.given()
                    .get("http://localhost:8282/prefix1/test1")
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)

            RestAssured.given()
                    .get("http://localhost:8282/prefix2/test2")
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
        }
    }

    "nesting groups should nest routes" {

        val httpHandler1 = mockHandler()
        val httpHandler2 = mockHandler()

        undertow(8282, "0.0.0.0") {

            group("/prefix1") {

                get("/test1", httpHandler1)

                group("/prefix2") {

                    get("/test2", httpHandler2)
                }
            }

        } assert {

            RestAssured.given()
                    .get("http://localhost:8282/prefix1/test1")
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)

            RestAssured.given()
                    .get("http://localhost:8282/prefix1/prefix2/test2")
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
        }
    }
})
