package org.wuzl.client

import jakarta.websocket.ContainerProvider
import jakarta.websocket.Session
import org.wuzl.communication.EndpointUri
import org.wuzl.communication.EndpointUri.RTC
import org.wuzl.communication.data.VoiceChannel

/**
 * @author Emilio Zottel (5AHIF)
 * @since 05.08.2024, Mo.
 **/
object SessionManager {

    fun VoiceClient.openSession(channel: VoiceChannel?): Session {
        return openSession("$RTC/${channel?.uuid ?: ""}")
    }

    fun <T : WebSocketClient> T.openSession(path: String): Session {
        return ContainerProvider.getWebSocketContainer().run {
            connectToServer(this@openSession, EndpointUri.absolute(path))
        }
    }

}