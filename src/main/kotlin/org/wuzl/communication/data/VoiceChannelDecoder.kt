package org.wuzl.communication.data

import jakarta.websocket.Decoder
import java.io.InputStream
import java.io.ObjectInputStream

/**
 *  @since 30.07.2024, Di.
 *  @author Emilio Zottel
 */
class VoiceChannelDecoder : Decoder.BinaryStream<VoiceChannel> {

    override fun decode(inputStream: InputStream): VoiceChannel {
        return ObjectInputStream(inputStream).readObject() as VoiceChannel
    }

}