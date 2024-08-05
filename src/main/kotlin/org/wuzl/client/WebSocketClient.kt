package org.wuzl.client

import jakarta.websocket.*
import org.wuzl.communication.data.SessionData
import org.wuzl.communication.data.SessionDataDecoder
import org.wuzl.communication.data.VoiceChannel
import org.wuzl.gui.WuzlGui
import org.wuzl.client.SessionManager.openSession
import org.wuzl.client.audio.AudioContainer
import org.wuzl.client.audio.AudioManager
import org.wuzl.communication.EndpointUri.HUB
import kotlin.concurrent.thread

@ClientEndpoint(decoders = [SessionDataDecoder::class])
class WebSocketClient(private val gui: WuzlGui) {

    private var session: Session? = openSession(HUB)

    val audioContainer = AudioContainer()

    fun joinVoiceChannel(channel: VoiceChannel) {
        session = openSession(channel)
    }

    fun startSending(microphoneVolume: Float = 1.0f) {  // TODO: use the microphoneVolume parameter
        thread {
            while (session != null && session?.isOpen == true) {  // TODO: locks
                if (AudioManager.DISCARD_OUTDATED && audioContainer.microphone.available() > audioContainer.microphoneBuffer.capacity()) {
                    readFromMicrophone(ByteArray(audioContainer.microphone.available() - audioContainer.microphoneBuffer.capacity()))
                }

                readFromMicrophone(audioContainer.microphoneBuffer.array())
                session?.basicRemote?.sendBinary(audioContainer.microphoneBuffer)
            }
        }
    }

    fun stopSending() {
        session?.close(CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, "Disconnected"))
        // onClose should now be called in case rtcSession was != null
        // TODO: check if that is the case
    }

    @OnOpen
    fun onOpen() {
        println("Session opened")
    }

    @OnMessage
    fun onMessage(sessionData: SessionData) {
        audioContainer.speakers.computeIfAbsent(sessionData.id) { AudioManager.defaultSourceDataLine(audioContainer.speakerGain) }
            .write(sessionData.data, 0, sessionData.data.size)
    }

    @OnClose
    fun onClose(reason: CloseReason) {
        session = null
        println("Session closed: $reason")
    }

    private fun readFromMicrophone(bytes: ByteArray) {
        audioContainer.microphone.read(bytes, 0, bytes.size)
    }

}