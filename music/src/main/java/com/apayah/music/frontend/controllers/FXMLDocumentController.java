package com.apayah.music.frontend.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private Button sliderButton;

    private double translationX = 0;

    @FXML
    private void handleSlideButtonClick(ActionEvent event) {
        // Geser tombol ke kanan setiap kali diklik
        translationX += 50; // Geser tombol sejauh 50 piksel
        sliderButton.setTranslateX(translationX); // Atur posisi tombol
    }

    @FXML
    private void onPlaylistItemClicked(MouseEvent event) {
        try {
            // Use AppLayout to switch to playlist content
            AppLayoutController appController = AppLayoutController.getInstance();
            if (appController != null) {
                appController.switchToContent("PlaylistContent.fxml");
            } else {
                System.err.println("AppLayoutController instance not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading PlaylistFXML: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
