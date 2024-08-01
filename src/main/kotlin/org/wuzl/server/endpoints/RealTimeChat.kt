package org.wuzl.server.endpoints

import jakarta.websocket.*
import jakarta.websocket.CloseReason.CloseCodes
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import org.wuzl.data.SessionData
import org.wuzl.data.SessionDataEncoder
import org.wuzl.data.VoiceChannel
import java.nio.ByteBuffer
import java.util.*

/**
 * @author Emilio Zottel
 * @since 19.07.2024, Fr.
 */
@ServerEndpoint("/rtc/{channel}", encoders = [SessionDataEncoder::class])
class RealTimeChat {

    companion object {

        private val sessions = hashMapOf<VoiceChannel, HashSet<Session>>()

    }

    @OnOpen
    fun onOpen(
        @PathParam("channel") uuid: String,
        session: Session,
    ) {
        val channel = VoiceChannel[UUID.fromString(uuid)]

        if (channel === null) {
            session.close(CloseReason(CloseCodes.UNEXPECTED_CONDITION, "VoiceChannel(uuid=$uuid) does not exist"))
            return
        }

        sessions.computeIfAbsent(channel) { hashSetOf() }
            .add(session)

        println("${session.id} opened")
    }

    @OnMessage
    fun onMessage(
        @PathParam("channel") uuid: String,
        bytes: ByteBuffer,
        session: Session,
    ) {
        broadcastExcept(bytes, session, sessions[VoiceChannel[UUID.fromString(uuid)]]!!)
    }

    @OnClose
    fun onClose(
        @PathParam("channel") uuid: String,
        session: Session,
    ) {
        // TODO: handle non-existent uuid when closing session manually
        sessions[VoiceChannel[UUID.fromString(uuid)]]!!.remove(session)
        println("${session.id} closed")
    }

    private fun broadcastExcept(
        bytes: ByteBuffer,
        sourceSession: Session,
        sessions: HashSet<Session>,
    ) {
        with(SessionData(sourceSession.id, bytes.array())) {
            sessions.asSequence()
                .filter { it != sourceSession }
                .forEach { it.asyncRemote.sendObject(this) }
        }
    }

}