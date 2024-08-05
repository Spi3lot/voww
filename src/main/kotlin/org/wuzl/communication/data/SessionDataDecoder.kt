package org.wuzl.communication.data

import jakarta.websocket.Decoder
import java.io.InputStream
import java.io.ObjectInputStream

/**
 *  @since 30.07.2024, Di.
 *  @author Emilio Zottel
 */
class SessionDataDecoder : Decoder.BinaryStream<SessionData> {

    override fun decode(inputStream: InputStream): SessionData {
        return ObjectInputStream(inputStream).readObject() as SessionData
    }

}