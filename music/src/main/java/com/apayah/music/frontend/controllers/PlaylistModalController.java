package com.apayah.music.frontend.controllers;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlaylistModalController {

    @FXML private VBox radioContainer;
    @FXML private TextField playlistNameField;


    private Stage stage;
    private String newPlaylistName;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPlaylists(List<String> playlists) {
        for (String name : playlists) {
            CheckBox cb = new CheckBox(name);
            radioContainer.getChildren().add(cb);
        }
    }

    @FXML
    private void onOk() {
        if (playlistNameField.getText() != null && !playlistNameField.getText().isEmpty()) {
            newPlaylistName = playlistNameField.getText();
        }
        stage.close();
    }

    @FXML
    private void onCancel() {
        newPlaylistName = null;
        stage.close();
    }

    public String getNewPlaylistName() {
        return newPlaylistName;
    }
}
