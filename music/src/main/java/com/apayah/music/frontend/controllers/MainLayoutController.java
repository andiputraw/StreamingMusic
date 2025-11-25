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

        // Debug: Print controller references
        System.out.println("Debug - musicControlController: " + musicControlController);
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
        // Try direct field first, then singleton
        if (musicControlController != null) {
            return musicControlController;
        }
        // Fallback to singleton instance
        return ControlFXMLController.getInstance();
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

    /**
     * Update all song information including duration and reset progress
     */
    public void updateSongInfo(String title, String artist, String albumCoverPath, double durationInSeconds) {
        if (musicControlController != null) {
            musicControlController.updateSongInfo(title, artist, albumCoverPath, durationInSeconds);
        }
    }

    /**
     * Update music detail panel
     */
    public void updateMusicDetailPanel(String songTitle, String artist, String album, String duration,
            String imageUrl) {
        try {
            var detailController = FXMLDocumentController.getInstance();
            if (detailController != null) {
                detailController.updateMusicDetails(songTitle, artist, album, duration, imageUrl);
            }
        } catch (Exception e) {
            System.err.println("Error updating music detail panel: " + e.getMessage());
        }
    }

    /**
     * Update music detail panel with Music object
     */
    public void updateMusicDetailPanel(com.apayah.music.backend.Music music) {
        try {
            var detailController = FXMLDocumentController.getInstance();
            if (detailController != null) {
                detailController.updateMusicDetails(music);
            }
        } catch (Exception e) {
            System.err.println("Error updating music detail panel: " + e.getMessage());
        }
    }
}