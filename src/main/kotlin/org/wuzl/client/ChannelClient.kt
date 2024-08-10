package org.wuzl.client

import jakarta.websocket.ClientEndpoint
import jakarta.websocket.OnMessage
import org.wuzl.client.SessionManager.openSession
import org.wuzl.communication.EndpointUri.HUB
import org.wuzl.communication.data.VoiceChannel
import org.wuzl.communication.data.VoiceChannelDecoder
import org.wuzl.gui.WuzlGui

/**
 * @author Emilio Zottel
 * @since 09.08.2024, Fr.
 **/
@ClientEndpoint(decoders = [VoiceChannelDecoder::class])
class ChannelClient(private val gui: WuzlGui) : WebSocketClient {

    init {
        openSession(HUB)
    }

    @OnMessage
    fun onMessage(channel: VoiceChannel) {
        gui.channelListView.items.add(channel)
    }

}