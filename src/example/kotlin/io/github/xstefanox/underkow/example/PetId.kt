package io.github.xstefanox.underkow.example

import java.util.UUID
import java.util.UUID.randomUUID

/**
 * The id of a [Pet].
 */
internal data class PetId(private val value: UUID) {

    constructor() : this(randomUUID())

    constructor(value: String): this(UUID.fromString(value))

    override fun toString() = value.toString()
}
