package com.apayah.music.frontend.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class HeaderFXMLController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(HeaderFXMLController.class.getName());

    @FXML
    private Button homeButton;

    @FXML
    private TextField searchField; // This links to the TextField in the FXML file

    @FXML
    private Button searchButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info(() -> "HeaderFXMLController initialized successfully");

        // Add listener for search field
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) ->
                LOGGER.fine(() -> "Search query: " + newValue)
            );
        }
    }

    /**
     * Handle home button click - navigate to main page (FXMLDocument)
     */
    @FXML
    private void onHomeButtonClick(ActionEvent event) {
        try {
            // Get the AppLayoutController instance to switch content
            AppLayoutController appController = AppLayoutController.getInstance();
            if (appController != null) {
                appController.loadMainContent(); // This loads FXMLDocument.fxml
                LOGGER.info(() -> "Navigated to main page (FXMLDocument.fxml)");
            } else {
                LOGGER.severe(() -> "AppLayoutController instance not found");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error navigating to main page", e);
        }
    }

    /**
     * Handle search functionality (placeholder)
     */
    @FXML
    private void onSearchAction(ActionEvent event) {
        if (searchField != null) {
            String searchText = searchField.getText();
            LOGGER.info(() -> "Search for: " + searchText);
        }
    }

    @FXML
    private void onSearchButtonClick(ActionEvent event) {
        String query = searchField.getText();
        LOGGER.info(() -> "Search button clicked, query: " + query);

        if (query != null && !query.trim().isEmpty()) {
            AppLayoutController appController = AppLayoutController.getInstance();
            if (appController != null) {
                appController.loadSearchContent(query);
            } else {
                LOGGER.severe(() -> "AppLayoutController instance not found");
            }
        }
    }
}
