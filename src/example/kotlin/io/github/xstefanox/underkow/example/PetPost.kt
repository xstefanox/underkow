package io.github.xstefanox.underkow.example

import kotlinx.serialization.Serializable

/**
 * Map a POST request to create a new [Pet].
 */
@Serializable
internal data class PetPost(val name: String)
