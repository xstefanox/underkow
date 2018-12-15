package io.github.xstefanox.underkow.example

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.github.xstefanox.underkow.chain.next
import io.github.xstefanox.underkow.undertow
import io.undertow.server.HttpServerExchange
import io.undertow.server.ResponseCommitListener
import io.undertow.util.StatusCodes.BAD_REQUEST
import io.undertow.util.StatusCodes.CREATED
import io.undertow.util.StatusCodes.NOT_FOUND
import io.undertow.util.StatusCodes.OK
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PetStore")

internal val responseLogger = ResponseCommitListener { exchange ->
    logger.info("[RES] ${exchange.requestMethod} ${exchange.requestPath} : ${exchange.statusCode}")
}

internal val requestLogger = object : SuspendingHttpHandler {
    override suspend fun handleRequest(exchange: HttpServerExchange) {

        val requestId = RequestId()

        exchange.addResponseCommitListener(responseLogger)
        exchange.putAttachment(REQUEST_ID, requestId)

        logger.info("[REQ] ${exchange.requestMethod} ${exchange.requestPath}")

        exchange.next()
    }
}

fun main() {

    System.setProperty("org.jboss.logging.provider", "slf4j")

    val pets = mutableMapOf<PetId, Pet>()
    val petNames = mutableMapOf<String, Pet>()

    undertow(8181) {
        path("/pets", requestLogger) {

            get { exchange ->
                exchange.send(OK, CollectionResponse(pets.values))
            }

            get("/{id}") { exchange ->

                val pet = pets[exchange.petId]

                if (pet != null) {
                    exchange.send(OK, pet)
                } else {
                    exchange.send(NOT_FOUND)
                }
            }

            post { exchange ->

                exchange.requestReceiver.receiveFullString { _, body ->

                    val name = (json.parse<PetPost>(body) ?: throw RuntimeException()).name
                    val pet = Pet(PetId(), name)

                    if (petNames.putIfAbsent(name, pet) != null) {
                        exchange.send(BAD_REQUEST, """
                            {
                                "message": "a pet with name '$name' already exists"
                            }
                        """.trimIndent())
                    } else {

                        logger.info("creating pet $pet")
                        pets[pet.id] = pet
                        logger.info("$pet created")

                        exchange.send(CREATED, pet)
                    }
                }
            }

            delete("/{id}") { exchange ->

                if (pets.containsKey(exchange.petId)) {

                    logger.info("deleting pet ${exchange.petId}")
                    val pet = pets.remove(exchange.petId)
                    petNames.remove(pet!!.name)
                    logger.info("$pet deleted")

                    exchange.send(OK, pet)

                } else {
                    exchange.send(NOT_FOUND)
                }
            }
        }
    }.start()
}
