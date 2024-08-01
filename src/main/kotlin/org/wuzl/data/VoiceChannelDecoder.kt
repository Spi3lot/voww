package org.wuzl.data

import jakarta.websocket.Decoder
import java.nio.ByteBuffer
import java.util.*

/**
 *  @since 30.07.2024, Di.
 *  @author Emilio Zottel
 */
object VoiceChannelDecoder : Decoder.Binary<VoiceChannel> {

    override fun decode(bytes: ByteBuffer): VoiceChannel {
        val uuidBytes = ByteArray(bytes.int).apply { bytes[this] }
        val nameBytes = ByteArray(bytes.int).apply { bytes[this] }
        return VoiceChannel(UUID.nameUUIDFromBytes(uuidBytes), String(nameBytes))  // TODO: maybe change to UUID.fromString(String(uuidBytes))
    }

    override fun willDecode(bytes: ByteBuffer): Boolean {
        return true
    }

}