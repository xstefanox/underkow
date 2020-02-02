package io.github.xstefanox.underkow.example

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.withName

internal object PetIdSerializer : KSerializer<PetId> {

    override val descriptor: SerialDescriptor = StringDescriptor.withName("PetIdSerializer")

    override fun deserialize(decoder: Decoder): PetId {
        return PetId(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, obj: PetId) {
        encoder.encodeString(obj.toString())
    }
}
