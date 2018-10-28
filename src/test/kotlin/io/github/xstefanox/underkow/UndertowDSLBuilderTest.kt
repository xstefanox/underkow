package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.assert
import io.github.xstefanox.underkow.test.mockHandler
import io.kotlintest.specs.StringSpec
import io.mockk.verify
import io.restassured.RestAssured
import org.apache.http.HttpStatus.SC_OK

class UndertowDSLBuilderTest : StringSpec({

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
})
