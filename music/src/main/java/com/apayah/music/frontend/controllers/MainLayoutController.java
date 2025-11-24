/*
 * Controller for MainLayout.fxml
 */
package com.apayah.music.frontend.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

/**
 * FXML Controller class for MainLayout
 *
 * @author PLN
 */
public class MainLayoutController implements Initializable {

    @FXML
    private Parent mainContent;
    
    @FXML
    private ControlFXMLController musicControlController;
    
    private static MainLayoutController instance;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Controller initialization - FXML includes are handled automatically
        instance = this;
        System.out.println("MainLayoutController initialized successfully");
    }

    /**
     * Get singleton instance of MainLayoutController
     */
    public static MainLayoutController getInstance() {
        return instance;
    }

    /**
     * Switch the main content to a different FXML file
     */
    public void switchContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent newContent = loader.load();

            // Since we use fx:include, we need to reload the entire scene
            System.out.println("Switching content to: " + fxmlFile);

        } catch (IOException e) {
            System.err.println("Error switching content to " + fxmlFile + ": " + e.getMessage());
        }
    }

    /**
     * Navigate to playlist view
     */
    public void showPlaylistView() {
        switchContent("PlaylistFXML.fxml");
    }

    /**
     * Navigate to main view
     */
    public void showMainView() {
        switchContent("FXMLDocument.fxml");
    }

    /**
     * Get reference to music control controller
     */
    public ControlFXMLController getMusicControlController() {
        return musicControlController;
    }

    /**
     * Set song information in the control bar
     */
    public void setSongInfo(String title, String artist, String albumCoverPath) {
        if (musicControlController != null) {
            musicControlController.setSongInfo(title, artist, albumCoverPath);
        }
    }

    /**
     * Set song duration in the control bar
     */
    public void setSongDuration(double duration) {
        if (musicControlController != null) {
            musicControlController.setDuration(duration);
        }
    }

    /**
     * Set current time in the control bar
     */
    public void setCurrentTime(double time) {
        if (musicControlController != null) {
            musicControlController.setCurrentTime(time);
        }
    }
}