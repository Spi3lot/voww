package org.wuzl.gui

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.Slider
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.wuzl.client.ChannelClient
import org.wuzl.client.VoiceClient
import org.wuzl.communication.data.VoiceChannel

fun main(args: Array<String>) {
    Application.launch(WuzlGui::class.java, *args)
}

/**
 *  @since 31.07.2024, Mi.
 *  @author Emilio Zottel
 */
class WuzlGui : Application() {

    @FXML
    lateinit var channelListView: ListView<VoiceChannel>

    @FXML
    lateinit var volumeSlider: Slider

    @FXML
    lateinit var connectButton: Button

    @FXML
    lateinit var muteButton: Button

    @FXML
    lateinit var disconnectButton: Button

    private lateinit var voiceClient: VoiceClient

    private lateinit var channelClient: ChannelClient

    @FXML
    fun initialize() {
        voiceClient = VoiceClient()
        channelClient = ChannelClient(this)
        volumeSlider.valueProperty().addListener { _, _, newValue -> voiceClient.audioContainer.speakerGain = newValue.toFloat() }
        muteButton.onAction = EventHandler { toggleMuteStatus() }

        connectButton.onAction = EventHandler {
            val channel = channelListView.selectionModel.selectedItem

            if (channel != null) {
                voiceClient.joinVoiceChannel(channel)
                muteButton.isVisible = true
            }
        }

        disconnectButton.onAction = EventHandler {
            voiceClient.leaveVoiceChannel()
            muteButton.isVisible = false
        }
    }

    override fun start(stage: Stage) {
        val root: Parent = FXMLLoader.load(WuzlGui::class.java.getResource("wuzl-gui.fxml"))
        stage.title = "Wuzl"
        stage.scene = Scene(root)
        stage.show()
    }

    private fun toggleMuteStatus() {
        val muted = isMuted() ?: return

        if (muted) {
            voiceClient.startSending()
            muteButton.text = "Mute"
            muteButton.textFill = Color.RED
        } else {
            voiceClient.stopSending()
            muteButton.text = "Unmute"
            muteButton.textFill = Color.GREEN
        }
    }

    private fun isMuted(): Boolean? {
        if (voiceClient.session == null) {
            return null
        }

        return voiceClient.senderThread == null
    }

}
