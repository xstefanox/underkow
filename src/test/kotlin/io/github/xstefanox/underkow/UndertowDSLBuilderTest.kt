package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.assert
import io.kotlintest.specs.StringSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.restassured.RestAssured
import io.undertow.server.HttpHandler
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

        val httpHandler = mockk<HttpHandler>()
        every { httpHandler.handleRequest(any()) } just Runs

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

        val httpHandler = mockk<HttpHandler>()
        every { httpHandler.handleRequest(any()) } just Runs

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
})
