package com.apayah.music.frontend.controllers;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlaylistModalController {

    @FXML private VBox radioContainer;

    private Stage stage;
    private List<String> selectedPlaylists = new ArrayList<>();

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
        selectedPlaylists.clear();

        for (var node : radioContainer.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) {
                selectedPlaylists.add(cb.getText());
            }
        }

        stage.close();
    }

    @FXML
    private void onCancel() {
        selectedPlaylists.clear();
        stage.close();
    }

    public List<String> getSelectedPlaylists() {
        return selectedPlaylists;
    }
}
