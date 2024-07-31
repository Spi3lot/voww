package org.wuzl.gui

import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(WuzlGui::class.java, *args)
}

/**
 *  @since 31.07.2024, Mi.
 *  @author Emilio Zottel
 */
class WuzlGui : Application() {

    @FXML
    lateinit var channelListView: ListView<String>

    @FXML
    lateinit var connectButton: Button

    override fun start(primaryStage: Stage) {
        val root: Parent = FXMLLoader.load(WuzlGui::class.java.getResource("wuzl-gui.fxml"))
        primaryStage.title = "Wuzl"
        primaryStage.scene = Scene(root)
        primaryStage.show()
    }

}