package org.wuzl.client

import jakarta.websocket.*
import org.wuzl.client.SessionManager.openSession
import org.wuzl.client.audio.AudioContainer
import org.wuzl.client.audio.AudioManager
import org.wuzl.communication.data.SessionData
import org.wuzl.communication.data.SessionDataDecoder
import org.wuzl.communication.data.VoiceChannel
import kotlin.concurrent.thread

@ClientEndpoint(decoders = [SessionDataDecoder::class])
class VoiceClient : WebSocketClient {

    val audioContainer = AudioContainer()

    private var channel: VoiceChannel? = null

    var session: Session? = null

    var senderThread: Thread? = null

    fun joinVoiceChannel(newChannel: VoiceChannel) {
        if (newChannel.uuid != channel?.uuid) {
            session = openSession(newChannel)

            if (channel == null) {
                startSending()
            }

            channel = newChannel
        }
    }

    /**
     * Unmute
     * TODO: use the microphoneVolume parameter
     */
    fun startSending(microphoneVolume: Float = 1.0f) {
        senderThread = newSenderThread()
        println("Unmuted self")
    }

    fun leaveVoiceChannel() {
        channel = null
        stopSending()
        session?.close(CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, "Disconnected"))
        // onClose will be called in case session was != null
    }

    /**
     * Mute
     */
    fun stopSending() {
        senderThread?.interrupt()
        senderThread = null
        println("Muted self")
    }

    private fun newSenderThread(): Thread {
        return if (AudioManager.DISCARD_OUTDATED) {
            thread {
                while (true) {
                    if (audioContainer.microphone.available() > audioContainer.microphoneBuffer.capacity()) {
                        readFromMicrophone(ByteArray(audioContainer.microphone.available() - audioContainer.microphoneBuffer.capacity()))
                    }

                    readFromMicrophoneAndSend()
                }
            }
        } else {
            thread {
                while (true) {
                    readFromMicrophoneAndSend()
                }
            }
        }
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

    private fun readFromMicrophoneAndSend() {
        readFromMicrophone(audioContainer.microphoneBuffer.array())
        session!!.basicRemote.sendBinary(audioContainer.microphoneBuffer)
    }

    private fun readFromMicrophone(bytes: ByteArray) {
        audioContainer.microphone.read(bytes, 0, bytes.size)
    }

}