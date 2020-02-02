package io.github.xstefanox.underkow.example

import kotlinx.serialization.Serializable

/**
 * Expose a collection of elements of any type.
 */
@Serializable
internal data class CollectionResponse<T>(val items: Collection<@Serializable T>)
