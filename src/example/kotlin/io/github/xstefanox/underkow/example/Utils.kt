package io.github.xstefanox.underkow.example

import com.beust.klaxon.Klaxon
import io.undertow.server.HttpServerExchange
import io.undertow.util.AttachmentKey
import io.undertow.util.HttpString
import io.undertow.util.PathTemplateMatch

internal val CONTENT_TYPE = HttpString("Content-Type")

internal const val APPLICATION_JSON = "application/json"

internal val REQUEST_ID = AttachmentKey.create(RequestId::class.java)

internal val json = Klaxon().converter(PetIdConverter())

internal fun Any.toJson(): String = json.toJsonString(this)

internal fun String.toPetId(): PetId = PetId(this)

internal val HttpServerExchange.petId: PetId
    get() = (getAttachment<PathTemplateMatch>(PathTemplateMatch.ATTACHMENT_KEY).parameters["id"]
        ?: throw RuntimeException()).toPetId()

internal fun HttpServerExchange.send(statusCode: Int, body: Any = "") {

    this.statusCode = statusCode
    this.responseHeaders.add(CONTENT_TYPE, APPLICATION_JSON)

    if (body !is String) {
        this.responseSender.send(body.toJson())
    } else {
        this.responseSender.send(body)
    }
}
