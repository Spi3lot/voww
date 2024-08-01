package org.wuzl.client

import jakarta.websocket.*
import org.wuzl.data.SessionData
import org.wuzl.data.SessionDataDecoder
import org.wuzl.data.VoiceChannel
import org.wuzl.gui.WuzlGui
import java.net.URI
import java.nio.ByteBuffer
import javax.sound.sampled.*
import kotlin.concurrent.thread

@ClientEndpoint(decoders = [SessionDataDecoder::class])
class WebSocketClient(private val gui: WuzlGui) {

    companion object {

        private const val ENDPOINT_URI = "ws://localhost:8025/rtc"

        private const val DISCARD_OUTDATED = true

        private const val BUFFER_SIZE = 16384

        private val audioFormat = AudioFormat(44100f, 16, 1, true, true)

    }

    // min volume is 0.0001f = 10^(-80/20)
    // max volume is 2.0f = 10^(6.0206/20)
    var speakerGain = 1.0f
        set(value) {
            field = value

            speakers.values
                .map { it.masterGain() }
                .forEach { it.value = value.coerceIn(it.minimum, it.maximum) }
        }

    private val speakers = hashMapOf<String, SourceDataLine>()

    private val microphoneBuffer = ByteBuffer.allocate(BUFFER_SIZE)

    private val microphone: TargetDataLine = AudioSystem.getTargetDataLine(audioFormat).apply {
        open()
        start()
    }

    private var session: Session? = null

    fun joinVoiceChannel(channel: VoiceChannel) {
        session = ContainerProvider.getWebSocketContainer().run {
            connectToServer(this@WebSocketClient, URI.create("$ENDPOINT_URI/${channel.uuid}"))
        }
    }

    fun startSending(microphoneVolume: Float = 1.0f) {  // TODO: use the microphoneVolume parameter
        thread {
            while (session != null && session?.isOpen == true) {  // TODO: locks
                if (DISCARD_OUTDATED && microphone.available() > microphoneBuffer.capacity()) {
                    readFromMicrophone(ByteArray(microphone.available() - microphoneBuffer.capacity()))
                }

                readFromMicrophone(microphoneBuffer.array())
                session?.basicRemote?.sendBinary(microphoneBuffer)
            }
        }
    }

    fun stopSending() {
        session!!.close()
        session = null
    }

    @OnOpen
    fun onOpen() {
        println("Session opened")
    }

    @OnMessage
    fun onMessage(sessionData: SessionData) {
        speakers.computeIfAbsent(sessionData.id) { newSourceDataLine() }
            .write(sessionData.data, 0, sessionData.data.size)
    }

    @OnMessage
    fun onMessage(channel: VoiceChannel) {
        gui.channelListView.items.add(channel)
    }

    @OnClose
    fun onClose() {
        session = null
        println("Session closed")
    }

    private fun readFromMicrophone(bytes: ByteArray) {
        microphone.read(bytes, 0, bytes.size)
    }

    private fun newSourceDataLine(): SourceDataLine {
        return AudioSystem.getSourceDataLine(audioFormat).apply {
            open()
            masterGain().value = speakerGain
            start()
        }
    }

    private fun SourceDataLine.masterGain(): FloatControl {
        return getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
    }

}