package io.github.xstefanox.underkow.example

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

/**
 * Represent a [Pet], identifiable by an id and a name.
 */
@Serializable
internal data class Pet(
    @ContextualSerialization val id: PetId,
    val name: String
)
