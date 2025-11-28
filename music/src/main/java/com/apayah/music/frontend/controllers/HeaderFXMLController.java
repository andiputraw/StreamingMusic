package com.apayah.music.frontend.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class HeaderFXMLController implements Initializable {

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
        System.out.println("HeaderFXMLController initialized successfully");

        // Add listener for search field
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                // Here you can implement search logic when the text is changed
                System.out.println("Search query: " + newValue);
            });
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
                System.out.println("Navigated to main page (FXMLDocument.fxml)");
            } else {
                System.err.println("AppLayoutController instance not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error navigating to main page: " + e.getMessage());
        }
    }

    /**
     * Handle search functionality (placeholder)
     */
    @FXML
    private void onSearchAction(ActionEvent event) {
        if (searchField != null) {
            String searchText = searchField.getText();
            System.out.println("Search for: " + searchText);
            // TODO: Implement search functionality
        }
    }

    @FXML
    private void onSearchButtonClick(ActionEvent event) {
        String query = searchField.getText();
        System.out.println("Search button clicked, query: " + query);
        
        if (query != null && !query.trim().isEmpty()) {
            AppLayoutController appController = AppLayoutController.getInstance();
            if (appController != null) {
                appController.loadSearchContent(query);
            } else {
                System.err.println("AppLayoutController instance not found");
            }
        }
    }
}
