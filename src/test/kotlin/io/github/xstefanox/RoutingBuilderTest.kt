package io.github.xstefanox

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.restassured.RestAssured
import io.undertow.Undertow
import io.undertow.server.RoutingHandler
import org.apache.http.HttpStatus.SC_OK

class RoutingBuilderTest : StringSpec({

    System.setProperty("org.jboss.logging.provider", "slf4j")

    fun assertWithUndertow(port : Int, routingHandler: RoutingHandler, block: () -> Unit) {

        val undertow = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(routingHandler)
                .build()

        undertow.start()

        try {
            block()
        } finally {
            undertow.stop()
        }
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

        assertWithUndertow(8282, routingBuilder.build()) {

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

        assertWithUndertow(8282, routingBuilder.build()) {

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

        assertWithUndertow(8282, routingBuilder.build()) {

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

        assertWithUndertow(8282, routingBuilder.build()) {

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

        assertWithUndertow(8282, routingBuilder.build()) {

            RestAssured.given()
                    .delete("http://localhost:8282/test")
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
        }
    }
})
