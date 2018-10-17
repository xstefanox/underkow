package io.github.xstefanox

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.restassured.RestAssured
import io.undertow.Undertow
import org.apache.http.HttpStatus.SC_OK

class RoutingBuilderTest : StringSpec({

    System.setProperty("org.jboss.logging.provider", "slf4j")

    "routing builder should produce a new object on every execution" {

        val routingBuilder = RoutingBuilder()

        val routingHandler1 = routingBuilder.build()
        val routingHandler2 = routingBuilder.build()

        routingHandler1 shouldNotBe routingHandler2
    }

    "GET request should be added to the handler" {

        val routingBuilder = RoutingBuilder()
        routingBuilder.get("/test") {}

        val undertow = Undertow.builder()
                .addHttpListener(8282, "0.0.0.0")
                .setHandler(routingBuilder.build())
                .build()

        undertow.start()

        try {
            RestAssured.given()
                    .get("http://localhost:8282/test")
                    .then()
                    .assertThat()
                    .statusCode(SC_OK)
        } finally {
            undertow.stop()
        }
    }
})
