package org.wuzl.communication.data

import jakarta.websocket.Encoder
import java.io.ObjectOutputStream
import java.io.OutputStream

/**
 *  @since 30.07.2024, Di.
 *  @author Emilio Zottel
 */
class SessionDataEncoder : Encoder.BinaryStream<SessionData> {

    override fun encode(sessionData: SessionData, os: OutputStream) {
        ObjectOutputStream(os).writeObject(sessionData)
    }

}