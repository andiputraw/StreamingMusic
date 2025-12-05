package com.apayah.music.frontend.controllers;

import com.apayah.music.backend.Music;
import com.apayah.music.frontend.AppState;
import com.apayah.music.playlist.Playlist;
import com.apayah.music.playlist.PlaylistManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable, AppState.MusicUpdateListener {

    @FXML
    private Label label;

    @FXML
    private Button sliderButton;

    // Music detail panel fields
    @FXML
    private ImageView detailAlbumCover;
    @FXML
    private Label detailSongName;
    @FXML
    private Label detailArtistName;
    @FXML
    private Label detailAlbumName;
    @FXML
    private Label detailDuration;

    // Music Control Bar
    @FXML
    private StackPane musicControl;

    private double translationX = 0;

    @FXML
    private VBox playlistContainer;

    @FXML
    private Button addPlaylistButton;

    private PlaylistManager playlistManager;

    @FXML
    private void handleSlideButtonClick(ActionEvent event) {
        // Geser tombol ke kanan setiap kali diklik
        translationX += 50; // Geser tombol sejauh 50 piksel
        sliderButton.setTranslateX(translationX); // Atur posisi tombol
    }

    @FXML
    private EventHandler<? super MouseEvent> onPlaylistItemClicked(Playlist playlist) {
        return (MouseEvent event) -> {
            try {
                // Use AppLayout to switch to playlist content
                AppLayoutController appController = AppLayoutController.getInstance();
                AppState.getInstance().setSelectedPlaylist(playlist);
                if (appController != null) {
                    appController.switchToContent("PlaylistFXML.fxml");
                    PlaylistFXMLController cntrl = PlaylistFXMLController.getInstance();
                    cntrl.loadFromPlaylist(playlist);
                    cntrl.setPlaylistTitle(playlist.getNama());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @FXML
    private void onAddPlaylistButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlaylistModal.fxml"));
            Parent root = loader.load();

            PlaylistModalController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Create New Playlist");
            stage.setScene(new Scene(root));

            controller.setStage(stage);

            stage.showAndWait();

            String newPlaylistName = controller.getNewPlaylistName();
            if (newPlaylistName != null && !newPlaylistName.isEmpty()) {
                playlistManager.buatPlaylist(newPlaylistName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlaylists() {
        playlistContainer.getChildren().clear();
        for (Playlist playlist : playlistManager.getSemuaPlaylist()) {
            playlistContainer.getChildren().add(createPlaylistItem(playlist));
        }
    }

    private HBox createPlaylistItem(Playlist playlist) {
        var onClickHandler = onPlaylistItemClicked(playlist);
        HBox playlistItem = new HBox();
        playlistItem.setAlignment(Pos.CENTER_LEFT);
        playlistItem.setPrefHeight(60.0);
        playlistItem.setPrefWidth(400.0);
        playlistItem.setSpacing(12.0);
        playlistItem.getStyleClass().add("playlist-box");
        playlistItem.setOnMouseClicked(onClickHandler);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(44.0);
        imageView.setFitWidth(44.0);
        imageView.getStyleClass().add("playlist-image");
        // A default image for all playlists for now
        imageView.setImage(new Image(getClass().getResourceAsStream("/image/playlist.png")));

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setSpacing(2.0);

        Label playlistLabel = new Label(playlist.getNama());
        playlistLabel.getStyleClass().add("recently-played-label");

        vbox.getChildren().add(playlistLabel);
        playlistItem.getChildren().addAll(imageView, vbox);

        return playlistItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playlistManager = PlaylistManager.getInstance();

        loadPlaylists();

        playlistManager.getSemuaPlaylist().addListener(
            (ListChangeListener<Playlist>) c -> loadPlaylists()
        );


        // Register as music update listener
        AppState.getInstance().addMusicUpdateListener(this);

        // Check if there is already music playing and update UI
        Music currentMusic = AppState.getInstance().getCurrentMusic();
        if (currentMusic != null) {
            updateMusicDetails(currentMusic);
        }

        // Initialize music control if available
        // Initialize music control if available
        if (musicControl != null) {
            // Try to get the controller
            try {
                ControlFXMLController musicControlController = (ControlFXMLController) musicControl.getProperties().get("controller");
                if (musicControlController == null) {
                    ControlFXMLController.getInstance();
                }
            } catch (Exception e) {
                ControlFXMLController.getInstance();
            }
        } else {
            // Fallback to singleton
            ControlFXMLController.getInstance();
        }

    }

    /**
     * Get singleton instance using Bill Pugh pattern
     */
    public static FXMLDocumentController getInstance() {
        return FXMLDocumentControllerHolder.INSTANCE;
    }

    /**
     * Update the music detail panel with new song information
     */
    public void updateMusicDetails(String songTitle, String artist, String album, String duration, String imageUrl) {
        if (detailSongName == null) {
            return;
        }

        // Use FXML fields if available
        detailSongName.setText(songTitle != null ? songTitle : "Unknown Song");

        if (detailArtistName != null) {
            detailArtistName.setText(artist != null ? artist : "Unknown Artist");
        }

        if (detailAlbumName != null) {
            detailAlbumName.setText(album != null ? album : "Unknown Album");
        }

        if (detailDuration != null) {
            detailDuration.setText("Duration: " + (duration != null ? formatDuration(duration) : "0:00"));
        }

        if (detailAlbumCover != null && imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image albumImage = new Image(imageUrl);
                detailAlbumCover.setImage(albumImage);
            } catch (Exception e) {
                // Keep the default image
            }
        }
    }

    /**
     * Update music details using Music object
     */
    public void updateMusicDetails(Music music) {
        if (music == null || music.getTrack() == null) {
            return;
        }

        var info = music.getTrack().getInfo();
        updateMusicDetails(
                info.title,
                info.author,
                "", // Album info might not be available in track info
                String.valueOf(info.length),
                info.artworkUrl);
    }

    /**
     * Format duration from milliseconds to mm:ss format
     */
    private String formatDuration(String durationStr) {
        try {
            long milliseconds = Long.parseLong(durationStr);
            long seconds = milliseconds / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%d:%02d", minutes, seconds);
        } catch (NumberFormatException e) {
            return durationStr; // Return original if parsing fails
        }
    }


    @Override
    public void onMusicChanged(Music music) {
        javafx.application.Platform.runLater(() -> updateMusicDetails(music));
    }

    /**
     * Bill Pugh singleton holder class
     */
    private static class FXMLDocumentControllerHolder {
        static final FXMLDocumentController INSTANCE = new FXMLDocumentController();
    }

}