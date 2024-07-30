package org.wuzl.client

import jakarta.websocket.*
import org.wuzl.data.SessionData
import org.wuzl.data.SessionDataDecoder
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

fun main() {
    val client = WebSocketClient("ws://62.47.159.43:8025/rtc")
    client.startSending()
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

    private val lock = ReentrantLock()

    private val condition = lock.newCondition()

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

    fun startSending() {
        thread(isDaemon = true) {
            while (session != null) {
                if (DISCARD_OUTDATED && microphone.available() > microphoneBuffer.capacity()) {
                    readFromMicrophone(ByteArray(microphone.available() - microphoneBuffer.capacity()))
                }

                lock.withLock {
                    if (speakers.isEmpty()) {
                        condition.await()
                    }

                    readFromMicrophone(microphoneBuffer.array())
                    session?.basicRemote?.sendBinary(microphoneBuffer)
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
        speakers.computeIfAbsent(sessionData.id) { condition.signal(); newSourceDataLine() }
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
                start()
            }
    }

}