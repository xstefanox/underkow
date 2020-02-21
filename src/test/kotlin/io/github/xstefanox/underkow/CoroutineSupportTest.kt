@file:Suppress("kotlin:S100")

package io.github.xstefanox.underkow

import io.github.xstefanox.underkow.test.TEST_HTTP_PORT
import io.github.xstefanox.underkow.test.assert
import io.github.xstefanox.underkow.test.request
import io.undertow.util.Methods.GET
import io.undertow.util.StatusCodes.OK
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class CoroutineSupportTest {

    private val logger: Logger = LoggerFactory.getLogger(CoroutineSupportTest::class.java)

    @Test
    fun `handlers could be implemented with suspending functions`() {

        undertow {

            port = TEST_HTTP_PORT

            routing {

                path("/prefix") {

                    get("/test") {
                        val result = doSomethingSuspending()
                        it.responseSender.send(result)
                    }
                }
            }
        } assert {

            request(
                method = GET,
                path = "/prefix/test",
                expect = OK
            )
        }
    }

    private suspend fun doSomethingSuspending(): String {
        val deferred = GlobalScope.async {
            logger.info("running async")
            "this is the result"
        }
        return deferred.await()
    }
}
