package org.wuzl.client.audio

import org.wuzl.client.audio.AudioManager.masterGain
import java.nio.ByteBuffer
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

/**
 * @author Emilio Zottel
 * @since 05.08.2024, Mo.
 **/
class AudioContainer {

    val microphoneBuffer: ByteBuffer = ByteBuffer.allocate(AudioManager.BUFFER_SIZE)

    val microphone: TargetDataLine = AudioManager.defaultTargetDataLine()

    val speakers = hashMapOf<String, SourceDataLine>()

    // min volume is 0.0001f = 10^(-80/20)
    // max volume is 2.0f = 10^(6.0206/20)
    var speakerGain = 1.0f
        set(value) {
            field = value

            speakers.values
                .map { it.masterGain() }
                .forEach { it.value = value.coerceIn(it.minimum, it.maximum) }
        }

}