package org.wuzl.client

import jakarta.websocket.*
import org.wuzl.data.SessionData
import org.wuzl.data.SessionDataDecoder
import java.net.URI
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread
import kotlin.math.log10

fun main(args: Array<String>) {
    val client = WebSocketClient("ws://62.47.159.43:8025/rtc")

    if (args.isNotEmpty()) {
        client.speakerVolume = args[0].toFloat()
    }

    client.startSending(if (args.size > 1) args[1].toFloat() else 1.0f)
    println("Press ENTER to stop the client...")
    readln()
}

@ClientEndpoint(decoders = [SessionDataDecoder::class])
class WebSocketClient(endpointUri: String) {

    companion object {

        private const val DISCARD_OUTDATED = true

        private const val BUFFER_SIZE = 16384

        private val audioFormat = AudioFormat(44100f, 16, 1, true, true)

    }

    // min volume is 0.0001f = 10^(-80/20)
    // max volume is 2.0f = 10^(6.0206/20)
    var speakerVolume = 1.0f
        set(value) {
            field = value.coerceIn(0.0001f, 2.0f)
            println("Linear volume set to $field")
        }

    private val speakers = hashMapOf<String, SourceDataLine>()

    private val microphoneBuffer = ByteBuffer.allocate(BUFFER_SIZE)

    private val microphone = AudioSystem.getTargetDataLine(audioFormat)
        .apply {
            open()
            start()
        }

    private var session = ContainerProvider.getWebSocketContainer()
        .run {
            connectToServer(this@WebSocketClient, URI.create(endpointUri))
        }

    fun startSending(microphoneVolume: Float = 1.0f) {  // TODO: use the microphoneVolume parameter
        thread(isDaemon = true) {
            while (session != null) {
                if (DISCARD_OUTDATED && microphone.available() > microphoneBuffer.capacity()) {
                    readFromMicrophone(ByteArray(microphone.available() - microphoneBuffer.capacity()))
                }

                readFromMicrophone(microphoneBuffer.array())
                session?.basicRemote?.sendBinary(microphoneBuffer)
            }
        }
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

    @OnClose
    fun onClose() {
        session = null
        println("Session closed")
    }

    private fun readFromMicrophone(bytes: ByteArray) {
        microphone.read(bytes, 0, bytes.size)
    }

    private fun newSourceDataLine(): SourceDataLine {
        return AudioSystem.getSourceDataLine(audioFormat)
            .apply {
                open()
                (getControl(FloatControl.Type.MASTER_GAIN) as FloatControl).value = log10(speakerVolume) * 20
                start()
            }
    }

}