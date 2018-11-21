package io.github.xstefanox.underkow.example

import java.util.UUID
import java.util.UUID.randomUUID

/**
 * An identifier used to mark each request.
 */
internal data class RequestId(private val value: UUID) {

    constructor() : this(randomUUID())

    override fun toString() = value.toString()
}
