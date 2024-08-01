package org.wuzl.data

import jakarta.websocket.Encoder
import java.nio.ByteBuffer

/**
 *  @since 30.07.2024, Di.
 *  @author Emilio Zottel
 */
object SessionDataEncoder : Encoder.Binary<SessionData> {

    override fun encode(obj: SessionData): ByteBuffer {
        return with(obj.id.toByteArray()) {
            ByteBuffer.allocate(Int.SIZE_BYTES + size + obj.data.size)
                .putInt(obj.id.length)
                .put(this)
                .put(obj.data)
                .flip() as ByteBuffer
        }
    }

}