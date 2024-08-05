package org.wuzl.communication.data

import jakarta.websocket.Encoder
import java.io.ObjectOutputStream
import java.io.OutputStream

/**
 *  @since 30.07.2024, Di.
 *  @author Emilio Zottel
 */
class VoiceChannelEncoder : Encoder.BinaryStream<VoiceChannel> {

    override fun encode(channel: VoiceChannel, os: OutputStream) {
        ObjectOutputStream(os).writeObject(channel)
    }

}