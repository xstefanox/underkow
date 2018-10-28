package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig

object ProjectConfig : AbstractProjectConfig() {

    init {
        System.setProperty("org.jboss.logging.provider", "slf4j")
    }
}
