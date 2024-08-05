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
import javafx.stage.Stage
import org.wuzl.client.WebSocketClient
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
    lateinit var disconnectButton: Button

    private val client = WebSocketClient(this)

    override fun start(stage: Stage) {
        val root: Parent = FXMLLoader.load(WuzlGui::class.java.getResource("wuzl-gui.fxml"))
        stage.title = "Wuzl"
        stage.scene = Scene(root)
        stage.show()
    }

    fun initialize() {
        connectButton.onAction = EventHandler {
            val channel = channelListView.selectionModel.selectedItem

            if (channel != null) {  // Do not disconnect when trying to connect to nothing
                client.joinVoiceChannel(channel)
                client.startSending()
            }
        }

        disconnectButton.onAction = EventHandler {
//            client.joinVoiceChannel(null)
        }

        volumeSlider.valueProperty().addListener { _, _, newValue ->
            client.audioContainer.speakerGain = newValue.toFloat()
        }
    }

}
