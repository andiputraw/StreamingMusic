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
    private ControlFXMLController musicControlController;

    private double translationX = 0;
    private static FXMLDocumentController instance;

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

            } else {
                System.err.println("AppLayoutController instance not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading PlaylistFXML: " + e.getMessage());
        }
     };
    }

    // @FXML
    // private void onPlaylistItemClicked(MouseEvent event) {
    //     try {
    //         // Use AppLayout to switch to playlist content
    //         AppLayoutController appController = AppLayoutController.getInstance();
    //         if (appController != null) {
    //             appController.switchToContent("PlaylistFXML.fxml");
    //         } else {
    //             System.err.println("AppLayoutController instance not found");
    //         }

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         System.err.println("Error loading PlaylistFXML: " + e.getMessage());
    //     }
    // }
    
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
            playlistContainer.getChildren().add(createPlaylistItem(playlist, "@../image/song1.jpg"));
        }
    }

    private HBox createPlaylistItem(Playlist playlist, String imageUrl) {
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

        Label label = new Label(playlist.getNama());
        label.getStyleClass().add("recently-played-label");

        vbox.getChildren().add(label);
        playlistItem.getChildren().addAll(imageView, vbox);

        return playlistItem;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        playlistManager = PlaylistManager.getInstance();
        
        loadPlaylists();

        playlistManager.getSemuaPlaylist().addListener((ListChangeListener<Playlist>) c -> {
            loadPlaylists();
        });

        // Register as music update listener
        AppState.getInstance().addMusicUpdateListener(this);
        
        // Check if there is already music playing and update UI
        Music currentMusic = AppState.getInstance().getCurrentMusic();
        if (currentMusic != null) {
            System.out.println("DEBUG FXMLDocument: Found current music in AppState, updating UI");
            updateMusicDetails(currentMusic);
        }

        // Debug: Check FXML field bindings
        System.out.println("DEBUG FXMLDocument: FXML field check:");
        System.out.println("  detailAlbumCover: " + detailAlbumCover);
        System.out.println("  detailSongName: " + detailSongName);
        System.out.println("  detailArtistName: " + detailArtistName);
        System.out.println("  detailAlbumName: " + detailAlbumName);
        System.out.println("  detailDuration: " + detailDuration);
        System.out.println("  musicControl: " + musicControl);

        // Test initial update
        if (detailSongName != null) {
            System.out.println("DEBUG FXMLDocument: Fields are properly bound, testing initial update");
        } else {
            System.out.println("ERROR FXMLDocument: detailSongName field is null - FXML binding failed!");
        }

        // Initialize music control if available
        if (musicControl != null) {
            System.out.println("DEBUG FXMLDocument: Music control initialized successfully");
            // Try to get the controller
            try {
                musicControlController = (ControlFXMLController) musicControl.getProperties().get("controller");
                if (musicControlController == null) {
                    System.out.println("DEBUG FXMLDocument: Controller not found in properties, will use singleton");
                    musicControlController = ControlFXMLController.getInstance();
                }
            } catch (Exception e) {
                System.out.println("DEBUG FXMLDocument: Error getting controller: " + e.getMessage());
                musicControlController = ControlFXMLController.getInstance();
            }
        } else {
            System.out.println("ERROR FXMLDocument: Music control is null - include failed!");
            // Fallback to singleton
            musicControlController = ControlFXMLController.getInstance();
        }

        if (musicControlController != null) {
            System.out.println("DEBUG FXMLDocument: Music control controller ready");
        } else {
            System.out.println("DEBUG FXMLDocument: Music control controller not yet available");
        }
    }

    /**
     * Get singleton instance
     */
    public static FXMLDocumentController getInstance() {
        return instance;
    }

    /**
     * Update the music detail panel with new song information
     */
    public void updateMusicDetails(String songTitle, String artist, String album, String duration, String imageUrl) {
        System.out.println("DEBUG FXMLDocument: updateMusicDetails called with title='" + songTitle + "', artist='" 
                + artist + "'");
        System.out.println(
                "DEBUG FXMLDocument: detailSongName=" + detailSongName + ", detailArtistName=" + detailArtistName);

        // Also update music control via AppLayoutController
        updateMusicControl(songTitle, artist, imageUrl, parseDuration(duration));

        // If FXML fields are null, try alternative approach immediately
        if (detailSongName == null) {
            System.out.println("ERROR FXMLDocument: detailSongName is null! Trying alternative lookup...");
            tryAlternativeUIUpdate(songTitle, artist, album, duration, imageUrl);
            return;
        }

        // Use FXML fields if available
        detailSongName.setText(songTitle != null ? songTitle : "Unknown Song");
        System.out.println("DEBUG FXMLDocument: Updated detailSongName to: " + detailSongName.getText());

        if (detailArtistName != null) {
            detailArtistName.setText(artist != null ? artist : "Unknown Artist");
            System.out.println("DEBUG FXMLDocument: Updated detailArtistName to: " + detailArtistName.getText());
        } else {
            System.out.println("ERROR FXMLDocument: detailArtistName is null!");
        }

        if (detailAlbumName != null) {
            detailAlbumName.setText(album != null ? album : "Unknown Album");
            System.out.println("DEBUG FXMLDocument: Updated detailAlbumName to: " + detailAlbumName.getText());
        } else {
            System.out.println("ERROR FXMLDocument: detailAlbumName is null!");
        }

        if (detailDuration != null) {
            detailDuration.setText("Duration: " + (duration != null ? formatDuration(duration) : "0:00"));
            System.out.println("DEBUG FXMLDocument: Updated detailDuration to: " + detailDuration.getText());
        } else {
            System.out.println("ERROR FXMLDocument: detailDuration is null!");
        }

        if (detailAlbumCover != null && imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image albumImage = new Image(imageUrl);
                detailAlbumCover.setImage(albumImage);
            } catch (Exception e) {
                System.out.println("Could not load album cover: " + imageUrl);
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

    /**
     * Clear the music detail panel
     */
    public void clearMusicDetails() {
        updateMusicDetails("No Song Selected", "No Artist", "No Album", "0", null);
    }

    /**
     * Alternative method to update UI using scene lookup when FXML binding fails
     */
    private void tryAlternativeUIUpdate(String songTitle, String artist, String album, String duration,
            String imageUrl) {
        // Use Platform.runLater to ensure UI thread access
        javafx.application.Platform.runLater(() -> {
            try {
                System.out.println("DEBUG FXMLDocument: Attempting alternative UI update using scene lookup");

                // Multiple strategies to find the scene
                javafx.scene.Scene scene = null;

                if (label != null && label.getScene() != null) {
                    scene = label.getScene();
                    System.out.println("DEBUG FXMLDocument: Got scene from label");
                } else if (sliderButton != null && sliderButton.getScene() != null) {
                    scene = sliderButton.getScene();
                    System.out.println("DEBUG FXMLDocument: Got scene from sliderButton");
                }

                if (scene != null) {
                    var songNameLabel = (Label) scene.lookup("#detailSongName");
                    var artistNameLabel = (Label) scene.lookup("#detailArtistName");
                    var albumNameLabel = (Label) scene.lookup("#detailAlbumName");
                    var durationLabel = (Label) scene.lookup("#detailDuration");
                    var albumCover = (ImageView) scene.lookup("#detailAlbumCover");

                    System.out.println("DEBUG FXMLDocument: Scene lookup results:");
                    System.out.println("  songNameLabel: " + songNameLabel);
                    System.out.println("  artistNameLabel: " + artistNameLabel);
                    System.out.println("  albumNameLabel: " + albumNameLabel);
                    System.out.println("  durationLabel: " + durationLabel);
                    System.out.println("  albumCover: " + albumCover);

                    if (songNameLabel != null) {
                        songNameLabel.setText(songTitle != null ? songTitle : "Unknown Song");
                        System.out.println("DEBUG FXMLDocument: Alternative update - Updated song name via lookup to: "
                                + songNameLabel.getText());
                    } else {
                        System.out.println("ERROR FXMLDocument: Could not find detailSongName via lookup");
                    }

                    if (artistNameLabel != null) {
                        artistNameLabel.setText(artist != null ? artist : "Unknown Artist");
                        System.out
                                .println("DEBUG FXMLDocument: Alternative update - Updated artist name via lookup to: "
                                        + artistNameLabel.getText());
                    } else {
                        System.out.println("ERROR FXMLDocument: Could not find detailArtistName via lookup");
                    }

                    if (albumNameLabel != null) {
                        albumNameLabel.setText(album != null ? album : "Unknown Album");
                        System.out.println("DEBUG FXMLDocument: Alternative update - Updated album name via lookup to: "
                                + albumNameLabel.getText());
                    }

                    if (durationLabel != null) {
                        durationLabel.setText("Duration: " + (duration != null ? formatDuration(duration) : "0:00"));
                        System.out.println("DEBUG FXMLDocument: Alternative update - Updated duration via lookup to: "
                                + durationLabel.getText());
                    }

                    if (albumCover != null && imageUrl != null && !imageUrl.isEmpty()) {
                        try {
                            javafx.scene.image.Image albumImage = new javafx.scene.image.Image(imageUrl);
                            albumCover.setImage(albumImage);
                            System.out
                                    .println("DEBUG FXMLDocument: Alternative update - Updated album cover via lookup");
                        } catch (Exception e) {
                            System.out.println("Could not load album cover via lookup: " + imageUrl);
                        }
                    }

                } else {
                    System.out.println("ERROR FXMLDocument: Cannot perform scene lookup - no scene available");
                }

            } catch (Exception e) {
                System.out.println("ERROR FXMLDocument: Alternative UI update failed: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Test method to manually update UI - for debugging
     */
    public void testUpdateUI() {
        System.out.println("Testing manual UI update...");
        updateMusicDetails("Test Song", "Test Artist", "Test Album", "180000", "https://via.placeholder.com/180");
        System.out.println("Manual UI update completed");
    }

    /**
     * Update music control via AppLayoutController
     */
    private void updateMusicControl(String title, String artist, String albumCover, double durationInSeconds) {
        try {
            AppLayoutController appController = AppLayoutController.getInstance();
            if (appController != null) {
                System.out.println("FXMLDocument: Updating music control - " + title + " by " + artist);
                appController.updateMusicControl(title, artist, albumCover, durationInSeconds);
            } else {
                System.out.println("FXMLDocument: AppLayoutController not available");
            }
        } catch (Exception e) {
            System.out.println("FXMLDocument: Error updating music control: " + e.getMessage());
        }
    }

    /**
     * Parse duration string to seconds
     */
    private double parseDuration(String duration) {
        try {
            if (duration == null || duration.isEmpty())
                return 180.0; // default 3 minutes

            // If it's already in milliseconds
            if (duration.matches("\\d+")) {
                return Double.parseDouble(duration) / 1000.0;
            }

            // If it's in format "mm:ss"
            if (duration.contains(":")) {
                String[] parts = duration.split(":");
                if (parts.length == 2) {
                    int minutes = Integer.parseInt(parts[0]);
                    int seconds = Integer.parseInt(parts[1]);
                    return minutes * 60.0 + seconds;
                }
            }

            return 180.0; // fallback
        } catch (Exception e) {
            return 180.0; // fallback on error
        }
    }

    @Override
    public void onMusicChanged(Music music) {
        System.out.println("DEBUG FXMLDocument: Received music update event");
        javafx.application.Platform.runLater(() -> {
            updateMusicDetails(music);
        });
    }
}