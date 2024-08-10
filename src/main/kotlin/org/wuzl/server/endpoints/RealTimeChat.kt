package org.wuzl.server.endpoints

import jakarta.websocket.*
import jakarta.websocket.CloseReason.CloseCodes
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import org.wuzl.communication.EndpointUri.RTC
import org.wuzl.communication.data.SessionDataEncoder
import org.wuzl.communication.data.VoiceChannel
import org.wuzl.server.TrafficManager
import java.nio.ByteBuffer
import java.util.*

/**
 * @author Emilio Zottel
 * @since 19.07.2024, Fr.
 */
@ServerEndpoint("/$RTC/{channel}", encoders = [SessionDataEncoder::class])
class RealTimeChat {

    companion object {

        private val sessions = hashMapOf<VoiceChannel, HashSet<Session>>()

    }

    @OnOpen
    fun onOpen(@PathParam("channel") uuid: String, session: Session) {
        val channel = Hub.CHANNELS[UUID.fromString(uuid)]

        if (channel === null) {
            session.close(CloseReason(CloseCodes.UNEXPECTED_CONDITION, "VoiceChannel(uuid=$uuid) does not exist"))
            return
        }

        sessions.computeIfAbsent(channel) { hashSetOf() }
            .add(session)

        println("${session.id} opened")
    }

    @OnMessage
    fun onMessage(@PathParam("channel") uuid: String, bytes: ByteBuffer, session: Session) {
        TrafficManager.broadcast(bytes, sessions[Hub.CHANNELS[UUID.fromString(uuid)]]!!, session)
    }

    @OnClose
    fun onClose(@PathParam("channel") uuid: String, session: Session) {
        // TODO: handle non-existent uuid when closing session manually
        sessions[Hub.CHANNELS[UUID.fromString(uuid)]]!!.remove(session)
        println("${session.id} closed")
    }

}