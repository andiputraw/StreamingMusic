/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package com.apayah.music.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author PLN
 */
public class JavaFXApplication1 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load new AppLayout instead of MainLayout
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/AppLayout.fxml"));

        Scene scene = new Scene(root, 1280, 800);

        // Load CSS if available
        String cssPath = getClass().getResource("/css/style.css") != null
                ? getClass().getResource("/css/style.css").toExternalForm()
                : null;
        String modalCss = getClass().getResource("/css/darkmodal.css").toExternalForm();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
            scene.getStylesheets().add(modalCss);
        }

        stage.setTitle("Music Player Application - Fullscreen");
        stage.setScene(scene);
        stage.setFullScreen(false); 

        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
