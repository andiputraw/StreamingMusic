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

    @FXML
    private StackPane contentArea;

    private static AppLayoutController instance;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        // Load default content (main page)
        loadMainContent();
        System.out.println("AppLayoutController initialized successfully");
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
     * Generic method to load any FXML content
     */
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();

            // Clear current content and add new content
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

            System.out.println("Successfully loaded: " + fxmlPath);

        } catch (IOException e) {
            System.err.println("Error loading content from " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Switch to specific content by filename
     */
    public void switchToContent(String fxmlFileName) {
        loadContent("/fxml/" + fxmlFileName);
    }
}