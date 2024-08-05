package org.wuzl.client.audio

import javax.sound.sampled.*

/**
 * @author Emilio Zottel
 * @since 05.08.2024, Mo.
 **/
object AudioManager {

    const val DISCARD_OUTDATED = true

    const val BUFFER_SIZE = 16384

    private val speakerAudioFormat = AudioFormat(48000f, 24, 1, true, true)

    private val microphoneAudioFormat = AudioFormat(44100f, 16, 1, true, true)

    // TODO: actually return default device
    fun defaultSourceDataLine(speakerGain: Float): SourceDataLine {
        return AudioSystem.getSourceDataLine(speakerAudioFormat)
            .prepare()
            .apply {
                masterGain().value = speakerGain
            }
    }

    // TODO: actually return default device
    fun defaultTargetDataLine(): TargetDataLine {
        return AudioSystem.getTargetDataLine(microphoneAudioFormat).prepare()
    }

    fun SourceDataLine.masterGain(): FloatControl {
        return getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
    }

    private fun <T : DataLine> T.prepare(): T {
        open()
        start()
        return this
    }

}