package io.github.xstefanox.underkow

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class MapUtilsTest {

    @Test
    internal fun `only absent keys should be added`() {

        val map = mutableMapOf(
            "current1" to 1,
            "current2" to 2
        )

        val other = mapOf(
            "current1" to -1,
            "new" to 0
        )

        map.putAllIfAbsent(other)

        map shouldBe mapOf(
            "current1" to 1,
            "current2" to 2,
            "new" to 0
        )
    }
}
