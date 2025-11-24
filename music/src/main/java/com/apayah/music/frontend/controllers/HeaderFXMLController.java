package com.apayah.music.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class HeaderFXMLController {

    @FXML
    private TextField searchField; // This links to the TextField in the FXML file

    // Method to handle the search functionality (if needed)
    @FXML
    public void initialize() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Here you can implement search logic when the text is changed
            System.out.println("Search query: " + newValue);
        });
    }
}
