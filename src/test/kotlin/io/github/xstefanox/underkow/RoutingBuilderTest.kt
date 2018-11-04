package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.TEST_HTTP_PORT
import io.github.xstefanox.underkow.test.assert
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.restassured.RestAssured
import io.undertow.Undertow
import io.undertow.server.RoutingHandler
import org.apache.http.HttpStatus.SC_OK

class RoutingBuilderTest : StringSpec({

    fun withUndertow(port: Int, routingHandler: RoutingHandler): Undertow {
        return Undertow.builder()
            .addHttpListener(port, "0.0.0.0")
            .setHandler(routingHandler)
            .build()
    }

    "routing builder should produce a new object on every execution" {

        val routingBuilder = RoutingBuilder()

        val routingHandler1 = routingBuilder.build()
        val routingHandler2 = routingBuilder.build()

        routingHandler1 shouldNotBe routingHandler2
    }

    "GET request should be added to the handler" {

        val routingBuilder = RoutingBuilder()
        routingBuilder.get("/test") {}

        withUndertow(TEST_HTTP_PORT, routingBuilder.build()) assert {

            RestAssured.given()
                .get("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }
    }

    "POST request should be added to the handler" {

        val routingBuilder = RoutingBuilder()
        routingBuilder.post("/test") {}

        withUndertow(TEST_HTTP_PORT, routingBuilder.build()) assert {

            RestAssured.given()
                .post("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }
    }

    "PUT request should be added to the handler" {

        val routingBuilder = RoutingBuilder()
        routingBuilder.put("/test") {}

        withUndertow(TEST_HTTP_PORT, routingBuilder.build()) assert {

            RestAssured.given()
                .put("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }
    }

    "PATCH request should be added to the handler" {

        val routingBuilder = RoutingBuilder()
        routingBuilder.patch("/test") {}

        withUndertow(TEST_HTTP_PORT, routingBuilder.build()) assert {

            RestAssured.given()
                .patch("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }
    }

    "DELETE request should be added to the handler" {

        val routingBuilder = RoutingBuilder()
        routingBuilder.delete("/test") {}

        withUndertow(TEST_HTTP_PORT, routingBuilder.build()) assert {

            RestAssured.given()
                .delete("http://localhost:8282/test")
                .then()
                .assertThat()
                .statusCode(SC_OK)
        }
    }
})
