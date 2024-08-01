package org.wuzl.data

import jakarta.websocket.Encoder
import java.nio.ByteBuffer

/**
 *  @since 30.07.2024, Di.
 *  @author Emilio Zottel
 */
object VoiceChannelEncoder : Encoder.Binary<VoiceChannel> {

    override fun encode(channel: VoiceChannel): ByteBuffer {
        val uuidBytes = channel.uuid.toString().toByteArray()
        val nameBytes = channel.name.toByteArray()

        return ByteBuffer.allocate(2 * Int.SIZE_BYTES + uuidBytes.size + nameBytes.size)
            .putInt(uuidBytes.size)
            .put(uuidBytes)
            .putInt(nameBytes.size)
            .put(nameBytes)
            .flip()
    }

}