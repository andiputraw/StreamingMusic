package com.apayah.music.frontend.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Controller for AppLayout.fxml - Manages dynamic content switching
 */
public class AppLayoutController implements Initializable {

    // Singleton instance - will be set when FXML instantiates this controller
    private static AppLayoutController instance = null;

    @FXML
    private StackPane contentArea;

    // Reference to music control controller for easy access
    private ControlFXMLController musicControlController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Capture this instance for singleton access
        synchronized (AppLayoutController.class) {
            if (instance == null) {
                instance = this;
            }
        }

        // Wait a bit for ControlFXMLController to be initialized
        javafx.application.Platform.runLater(() -> {
            musicControlController = ControlFXMLController.getInstance();

            // Test music control update
            if (musicControlController != null) {
                musicControlController.updateSongInfo("Test Song", "Test Artist", "", 180.0);
            }
        });

        // Load default content (main page)
        loadMainContent();
    }

    /**
     * Get singleton instance
     */
    public static AppLayoutController getInstance() {
        return instance;
    }

    /**
     * Load main content (FXMLDocument)
     */
    public void loadMainContent() {
        loadContent("/fxml/FXMLDocument.fxml");
    }


    /**
     * Load playlist content (PlaylistFXML)
     */
    public void loadPlaylistContent() {
        loadContent("/fxml/PlaylistFXML.fxml");
    }

    /**
     * Load playlist content with search query
     */
    public void loadSearchContent(String query) {
        if (contentArea == null) {
            return; // contentArea not yet initialized
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlaylistFXML.fxml"));
            Node content = loader.load();

            PlaylistFXMLController controller = loader.getController();
            if (controller != null) {
                controller.performSearch(query);
            }

            // Clear current content and add new content
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generic method to load any FXML content
     */
    private void loadContent(String fxmlPath) {
        if (contentArea == null) {
            return; // contentArea not yet initialized
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();

            // Clear current content and add new content
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switch to specific content by filename
     */
    public void switchToContent(String fxmlFileName) {
        loadContent("/fxml/" + fxmlFileName);
    }

    /**
     * Get music control controller instance
     */
    public ControlFXMLController getMusicControlController() {
        if (musicControlController == null) {
            musicControlController = ControlFXMLController.getInstance();
        }
        return musicControlController;
    }

    /**
     * Update music control from anywhere in the app
     */
    public void updateMusicControl(String title, String artist, String albumCover, double durationInSeconds) {
        ControlFXMLController controller = getMusicControlController();
        if (controller != null) {
            controller.updateSongInfo(title, artist, albumCover, durationInSeconds);
            controller.startPlayback();
        }
    }
}