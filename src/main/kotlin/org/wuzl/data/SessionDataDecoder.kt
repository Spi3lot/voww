package org.wuzl.data

import jakarta.websocket.Decoder
import java.nio.ByteBuffer

/**
 *  @since 30.07.2024, Di.
 *  @author Emilio Zottel
 */
object SessionDataDecoder : Decoder.Binary<SessionData> {

    override fun decode(bytes: ByteBuffer): SessionData {
        val sessionId = ByteArray(bytes.int).apply { bytes[this] }
        val data = ByteArray(bytes.remaining()).apply { bytes[this] }
        return SessionData(String(sessionId), data)
    }

    override fun willDecode(bytes: ByteBuffer): Boolean {
        return true
    }

}