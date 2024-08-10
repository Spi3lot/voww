package org.wuzl.server.endpoints

import jakarta.websocket.OnClose
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.ServerEndpoint
import org.wuzl.communication.EndpointUri.HUB
import org.wuzl.communication.data.VoiceChannel
import org.wuzl.communication.data.VoiceChannelEncoder
import java.util.*

/**
 * @author Emilio Zottel
 * @since 19.07.2024, Fr.
 */
@ServerEndpoint("/$HUB", encoders = [VoiceChannelEncoder::class])
class Hub {

    companion object {

        private val sessions = hashSetOf<Session>()

        val CHANNELS = hashMapOf<UUID, VoiceChannel>(
            with(UUID.randomUUID()) { this to VoiceChannel(this, "EÖV") },
            with(UUID.randomUUID()) { this to VoiceChannel(this, "ZÖV") },
            with(UUID.randomUUID()) { this to VoiceChannel(this, "DÖV") },
            with(UUID.randomUUID()) { this to VoiceChannel(this, "VÖV") },
            with(UUID.randomUUID()) { this to VoiceChannel(this, "FÖV") },
            with(UUID.randomUUID()) { this to VoiceChannel(this, "SÖV") },
        )

    }

    @OnOpen
    fun onOpen(session: Session) {
        sessions.add(session)
        CHANNELS.values.forEach { session.asyncRemote.sendObject(it) }
        println("${session.id} opened")
    }

    @OnClose
    fun onClose(session: Session) {
        sessions.remove(session)
        println("${session.id} closed")
    }

}