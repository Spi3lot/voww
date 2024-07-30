package org.wuzl.server.endpoints

import jakarta.websocket.OnClose
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.ServerEndpoint
import org.wuzl.data.SessionData
import org.wuzl.data.SessionDataEncoder
import java.nio.ByteBuffer

/**
 * @author Emilio Zottel
 * @since 19.07.2024, Fr.
 */
@ServerEndpoint("/rtc", encoders = [SessionDataEncoder::class])
class RealTimeChat {

    companion object {

        private val sessions = hashSetOf<Session>()

    }

    @OnOpen
    fun onOpen(session: Session) {
        sessions.add(session)
        println("${session.id} opened")
    }

    @OnMessage
    fun onMessage(bytes: ByteBuffer, session: Session) {
        broadcast(bytes, session)
    }

    @OnClose
    fun onClose(session: Session) {
        sessions.remove(session)
        println("${session.id} closed")
    }

    private fun broadcast(bytes: ByteBuffer, sourceSession: Session) {
        with(SessionData(sourceSession.id, bytes.array())) {
            sessions.asSequence()
                .filter { it != sourceSession }
                .forEach { it.asyncRemote.sendObject(this) }
        }
    }

}