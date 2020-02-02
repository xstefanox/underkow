package io.github.xstefanox.underkow.example

import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import io.undertow.util.HttpString
import io.undertow.util.PathTemplateMatch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.serializer

internal val CONTENT_TYPE = HttpString("Content-Type")

internal const val APPLICATION_JSON = "application/json"

internal val REQUEST_ID = AttachmentKey.create(RequestId::class.java)

internal val KOTLINX_JSON = Json(JsonConfiguration.Stable.copy(strictMode = false), SerializersModule {
    contextual(PetIdSerializer)
})

internal fun String.toPetId(): PetId = PetId(this)

internal val HttpServerExchange.petId: PetId
    get() = (getAttachment<PathTemplateMatch>(PathTemplateMatch.ATTACHMENT_KEY).parameters["id"]
        ?: throw RuntimeException()).toPetId()

internal fun HttpServerExchange.send(statusCode: Int) {
    this.statusCode = statusCode
}

@UseExperimental(ImplicitReflectionSerializer::class)
internal inline fun <reified T : @Serializable Any> HttpServerExchange.send(statusCode: Int, body: T) {
    this.statusCode = statusCode
    this.responseHeaders.add(CONTENT_TYPE, APPLICATION_JSON)
    this.responseSender.send(KOTLINX_JSON.stringify(T::class.serializer(), body))
}

@UseExperimental(ImplicitReflectionSerializer::class)
internal inline fun <reified T : Any> HttpServerExchange.send(statusCode: Int, body: CollectionResponse<T>) {
    this.statusCode = statusCode
    this.responseHeaders.add(CONTENT_TYPE, APPLICATION_JSON)
    this.responseSender.send(KOTLINX_JSON.stringify(CollectionResponse.serializer(T::class.serializer()), body))
}
