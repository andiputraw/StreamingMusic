package com.apayah.music.frontend.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.apayah.music.backend.Music;
import com.apayah.music.frontend.AppState;
import com.apayah.music.frontend.SongData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PlaylistFXMLController implements Initializable, AppState.MusicUpdateListener {

    @FXML
    private TableView<SongData> songTableView; // Menghubungkan dengan TableView di FXML
    @FXML
    private TableColumn<SongData, Void> actionColumn; // Add column for action buttons
    @FXML
    private Button playButton;
    @FXML
    private Button addButton;

    // Modal components
    @FXML
    private StackPane modalOverlay;
    @FXML
    private VBox playlistContainer;
    @FXML
    private Button closeModalButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addToPlaylistButton;

    // Radio buttons for playlist selection
    @FXML
    private RadioButton playlist1Radio;
    @FXML
    private RadioButton playlist2Radio;
    @FXML
    private RadioButton playlist3Radio;
    private ToggleGroup playlistToggleGroup;

    // Variable to store selected song for modal
    private SongData selectedSongForModal;

    // Reference to parent controller for accessing control bar
    private MainLayoutController parentController;

    // Detail panel fields for PlaylistFXML
    @FXML
    private ImageView playlistDetailAlbumCover;
    @FXML
    private Label playlistDetailSongName;
    @FXML
    private Label playlistDetailArtistName;
    @FXML
    private Label playlistDetailAlbumName;
    @FXML
    private Label playlistDetailDuration;

    // Singleton instance
    private static PlaylistFXMLController instance;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        System.out.println("PlaylistFXMLController initialized successfully");

        // Register as music update listener
        AppState.getInstance().addMusicUpdateListener(this);

        // Check if there is already music playing and update UI
        Music currentMusic = AppState.getInstance().getCurrentMusic();
        if (currentMusic != null) {
            System.out.println("DEBUG PlaylistFXML: Found current music in AppState, updating UI");
            updatePlaylistDetailPanel(currentMusic);
        }

        // Debug: Check FXML field bindings
        System.out.println("DEBUG: FXML field check:");
        System.out.println("  songTableView: " + songTableView);
        System.out.println("  playlistDetailSongName: " + playlistDetailSongName);
        System.out.println("  playlistDetailArtistName: " + playlistDetailArtistName);
        System.out.println("  playlistDetailAlbumName: " + playlistDetailAlbumName);
        System.out.println("  playlistDetailDuration: " + playlistDetailDuration);
        System.out.println("  playlistDetailAlbumCover: " + playlistDetailAlbumCover);

        // Initialize song table
        initializeSongTable();

        // Setup action column with Add buttons
        setupActionColumn();

        // Initialize modal as hidden
        if (modalOverlay != null) {
            modalOverlay.setVisible(false);
            modalOverlay.setManaged(false);
        }

        // Create and setup toggle group for radio buttons
        playlistToggleGroup = new ToggleGroup();
        if (playlist1Radio != null && playlist2Radio != null && playlist3Radio != null) {
            playlist1Radio.setToggleGroup(playlistToggleGroup);
            playlist2Radio.setToggleGroup(playlistToggleGroup);
            playlist3Radio.setToggleGroup(playlistToggleGroup);
        }
    }

    /**
     * Get singleton instance
     */
    public static PlaylistFXMLController getInstance() {
        return instance;
    }

    private void initializeSongTable() {
        var musicPlayerFacade = AppState.getInstance().getMusicPlayer();

        musicPlayerFacade.search("Girl you loud").thenAccept(musics -> {
            ObservableList<SongData> songs = FXCollections.observableArrayList();

            for (Music music : musics) {
                var info = music.getTrack().getInfo();
                songs.add(new SongData(0, info.title, info.author, "", formatDurationFromMillis(info.length),
                        new Image(info.artworkUrl), music));
            }

            // Menetapkan ObservableList ke TableView
            if (songTableView != null) {
                songTableView.setItems(songs);

                // Add row click listener untuk play lagu
                songTableView.setRowFactory(tv -> {
                    var row = new javafx.scene.control.TableRow<SongData>();
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && (!row.isEmpty())) {
                            SongData rowData = row.getItem();
                            playSelectedSong(rowData);
                        }
                    });
                    return row;
                });
            }

        });

    }

    /**
     * Setup the action column with Add buttons for each row
     */
    private void setupActionColumn() {
        if (actionColumn != null) {
            actionColumn.setCellFactory(
                    new Callback<TableColumn<SongData, Void>, javafx.scene.control.TableCell<SongData, Void>>() {
                        @Override
                        public javafx.scene.control.TableCell<SongData, Void> call(
                                final TableColumn<SongData, Void> param) {
                            final javafx.scene.control.TableCell<SongData, Void> cell = new javafx.scene.control.TableCell<SongData, Void>() {

                                private final Button addBtn = new Button("Add");

                                {
                                    addBtn.getStyleClass().add("table-add-button");
                                    addBtn.setOnAction((ActionEvent event) -> {
                                        SongData song = getTableView().getItems().get(getIndex());
                                        selectedSongForModal = song;
                                        showAddToPlaylistModal(event);
                                    });
                                }

                                @Override
                                public void updateItem(Void item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                    } else {
                                        setGraphic(addBtn);
                                    }
                                }
                            };
                            return cell;
                        }
                    });
        }
    }

    /**
     * Show the Add to Playlist modal
     */
    @FXML
    private void showAddToPlaylistModal(ActionEvent event) {
        // if (modalOverlay != null) {
        // modalOverlay.setVisible(true);
        // modalOverlay.setManaged(true);
        // modalOverlay.toFront();

        // // Clear any previous selection
        // if (playlistToggleGroup != null) {
        // playlistToggleGroup.selectToggle(null);
        // }
        // }
        try {

            openPlaylistModal();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @FXML
    private void openPlaylistModal() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlaylistModal.fxml"));
        Parent root = loader.load();

        PlaylistModalController controller = loader.getController();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Choose Playlist");

        modal.setScene(new Scene(root));
        controller.setStage(modal);

        // Example playlists:
        controller.setPlaylists(List.of("Rock", "Jazz", "Chill", "EDM"));

        modal.showAndWait();

        List<String> selected = controller.getSelectedPlaylists();
        if (!selected.isEmpty()) {
            System.out.println("User chose: " + selected);
        }

    }

    /**
     * Hide the Add to Playlist modal
     */
    @FXML
    private void hideAddToPlaylistModal(ActionEvent event) {
        if (modalOverlay != null) {
            modalOverlay.setVisible(false);
            modalOverlay.setManaged(false);
        }
        System.out.println("Add to Playlist modal closed");
    }

    /**
     * Add song to selected playlist
     */
    @FXML
    private void addToSelectedPlaylist(ActionEvent event) {
        if (playlistToggleGroup != null && playlistToggleGroup.getSelectedToggle() != null) {
            RadioButton selectedRadio = (RadioButton) playlistToggleGroup.getSelectedToggle();
            String selectedPlaylist = "";

            if (selectedRadio == playlist1Radio) {
                selectedPlaylist = "BL8M";
            } else if (selectedRadio == playlist2Radio) {
                selectedPlaylist = "My Favorites";
            } else if (selectedRadio == playlist3Radio) {
                selectedPlaylist = "Chill Vibes";
            }

            // Get song name to add
            String songName = "";
            if (selectedSongForModal != null) {
                songName = selectedSongForModal.getSongTitle() + " - " +
                        selectedSongForModal.getAlbum();
            } else {
                // Fallback to selected table item
                SongData selectedSong = getSelectedSong();
                if (selectedSong != null) {
                    songName = selectedSong.getSongTitle() + " - " + selectedSong.getAlbum();
                } else {
                    songName = "Unknown Song";
                }
            }

            System.out.println("Added '" + songName + "' to playlist: " + selectedPlaylist);

            // Hide modal after adding
            hideAddToPlaylistModal(event);

            // Show success message
            showSuccessMessage("'" + (selectedSongForModal != null ? selectedSongForModal.getSongTitle() : "Song") +
                    "' added to " + selectedPlaylist + " successfully!");

            // Clear the selected song
            selectedSongForModal = null;

        } else {
            System.out.println("No playlist selected");
            showErrorMessage("Please select a playlist first!");
        }
    }

    /**
     * Show success message (placeholder for actual implementation)
     */
    private void showSuccessMessage(String message) {
        System.out.println("SUCCESS: " + message);
        // Here you could show a toast notification or update UI
    }

    /**
     * Show error message (placeholder for actual implementation)
     */
    private void showErrorMessage(String message) {
        System.out.println("ERROR: " + message);
        // Here you could show an error dialog or notification
    }

    /**
     * Get the currently selected song from table
     */
    private SongData getSelectedSong() {
        if (songTableView != null) {
            return songTableView.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    /**
     * Public method to show modal from external classes
     */
    public void showAddModal() {
        showAddToPlaylistModal(null);
    }

    /**
     * Set parent controller reference to access control bar
     */
    public void setParentController(MainLayoutController parentController) {
        this.parentController = parentController;
    }

    /**
     * Play selected song from playlist
     */
    private void playSelectedSong(SongData songData) {
        if (songData == null || songData.getMusic() == null) {
            System.out.println("No song data or music object available");
            return;
        }

        try {
            // Get music player facade
            var musicPlayerFacade = AppState.getInstance().getMusicPlayer();
            var info = songData.getMusic().getTrack().getInfo();

            // Add song to queue and play immediately
            musicPlayerFacade.play(songData.getMusic());
            musicPlayerFacade.resume();

            // Notify everyone about the change
            AppState.getInstance().notifyMusicChanged(songData.getMusic());

            // Update control bar UI
            updateControlBar(info);

            // Update detail panel UI
            updateDetailPanel(songData.getMusic());

            System.out.println("Playing: " + songData.getSongTitle() + " by " + songData.getArtist());

        } catch (Exception e) {
            System.out.println("Error playing song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update control bar with song information
     */
    private void updateControlBar(com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo info) {
        try {
            System.out.println("DEBUG: Updating control bar for song: " + info.title);
            double durationInSeconds = info.length / 1000.0;

            // Method 1: Via AppLayoutController (primary approach)
            AppLayoutController appController = AppLayoutController.getInstance();
            if (appController != null) {
                System.out.println("DEBUG: Using AppLayoutController to update music control");
                appController.updateMusicControl(info.title, info.author, "", durationInSeconds);
                return;
            }

            // Method 2: Direct singleton fallback
            ControlFXMLController controlController = ControlFXMLController.getInstance();
            if (controlController != null) {
                System.out.println("DEBUG: Using direct singleton to update music control");
                controlController.updateSongInfo(info.title, info.author, "", durationInSeconds);
                controlController.startPlayback();
                return;
            }

            System.out.println("ERROR: Could not find any controller instance to update music control");
        } catch (Exception e) {
            System.out.println("Error updating control bar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update detail panel with song information
     */
    private void updateDetailPanel(Music music) {
        try {
            System.out.println("DEBUG: Updating detail panel for music");

            // Update local detail panel in PlaylistFXML
            updatePlaylistDetailPanel(music);

            // Also try to update PlaylistFXMLController instance if we're not in playlist
            // page
            var playlistController = PlaylistFXMLController.getInstance();
            if (playlistController != null && playlistController != this) {
                System.out.println("DEBUG: Updating external PlaylistFXMLController instance");
                playlistController.updatePlaylistDetailPanelExternal(music);
            }

            // Also try to update FXMLDocumentController if available
            var detailController = FXMLDocumentController.getInstance();
            System.out.println("DEBUG: FXMLDocumentController instance: " + detailController);

            if (detailController != null) {
                detailController.updateMusicDetails(music);
                System.out.println("DEBUG: FXMLDocument detail panel update completed");
            } else {
                System.out.println("INFO: FXMLDocumentController not available (normal for PlaylistFXML)");
            }
        } catch (Exception e) {
            System.out.println("Error updating detail panel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * External method to update playlist detail panel from other controllers
     */
    public void updatePlaylistDetailPanelExternal(Music music) {
        System.out.println("DEBUG: External call to update playlist detail panel");
        updatePlaylistDetailPanel(music);
    }

    /**
     * Update the local detail panel in PlaylistFXML
     */
    private void updatePlaylistDetailPanel(Music music) {
        if (music == null || music.getTrack() == null) {
            return;
        }

        var info = music.getTrack().getInfo();
        System.out.println("DEBUG: Updating playlist detail panel with: " + info.title);

        // Update song name - try alternative lookup if field is null
        if (playlistDetailSongName != null) {
            playlistDetailSongName.setText(info.title != null ? info.title : "Unknown Song");
            System.out.println("DEBUG: Updated playlistDetailSongName to: " + playlistDetailSongName.getText());
        } else {
            System.out.println("ERROR: playlistDetailSongName is null - trying alternative lookup");
            tryPlaylistAlternativeUIUpdate(info.title, info.author, info.length);
            return;
        }

        // Update artist name
        if (playlistDetailArtistName != null) {
            playlistDetailArtistName.setText(info.author != null ? info.author : "Unknown Artist");
            System.out.println("DEBUG: Updated playlistDetailArtistName to: " + playlistDetailArtistName.getText());
        } else {
            System.out.println("ERROR: playlistDetailArtistName is null");
        }

        // Update album name (might not be available)
        if (playlistDetailAlbumName != null) {
            playlistDetailAlbumName.setText("Unknown Album"); // Track info usually doesn't have album
        }

        // Update duration
        if (playlistDetailDuration != null) {
            String duration = formatDurationFromMillis(info.length);
            playlistDetailDuration.setText("Duration: " + duration);
            System.out.println("DEBUG: Updated playlistDetailDuration to: " + playlistDetailDuration.getText());
        } else {
            System.out.println("ERROR: playlistDetailDuration is null");
        }

        // Update album cover
        if (playlistDetailAlbumCover != null && info.artworkUrl != null && !info.artworkUrl.isEmpty()) {
            try {
                Image albumImage = new Image(info.artworkUrl);
                playlistDetailAlbumCover.setImage(albumImage);
                System.out.println("DEBUG: Updated playlistDetailAlbumCover with: " + info.artworkUrl);
            } catch (Exception e) {
                System.out.println("Could not load album cover: " + info.artworkUrl);
            }
        } else {
            System.out.println("DEBUG: playlistDetailAlbumCover is null or no artwork URL");
        }

        System.out.println("DEBUG: Playlist detail panel update completed");
    }

    /**
     * Format duration from milliseconds to mm:ss format
     */
    private String formatDurationFromMillis(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Alternative method to update UI using scene lookup when FXML binding fails
     */
    private void tryPlaylistAlternativeUIUpdate(String songTitle, String artist, long durationMs) {
        // Use Platform.runLater to ensure UI thread access
        javafx.application.Platform.runLater(() -> {
            try {
                System.out.println("DEBUG Playlist: Attempting alternative UI update using scene lookup");

                // Multiple strategies to find the scene
                javafx.scene.Scene scene = null;

                if (songTableView != null && songTableView.getScene() != null) {
                    scene = songTableView.getScene();
                    System.out.println("DEBUG Playlist: Got scene from songTableView");
                } else if (playButton != null && playButton.getScene() != null) {
                    scene = playButton.getScene();
                    System.out.println("DEBUG Playlist: Got scene from playButton");
                }

                if (scene != null) {
                    // Try to find elements by ID in the scene
                    javafx.scene.control.Label songNameLabel = (javafx.scene.control.Label) scene
                            .lookup("#playlistDetailSongName");
                    javafx.scene.control.Label artistNameLabel = (javafx.scene.control.Label) scene
                            .lookup("#playlistDetailArtistName");
                    javafx.scene.control.Label durationLabel = (javafx.scene.control.Label) scene
                            .lookup("#playlistDetailDuration");
                    javafx.scene.image.ImageView albumCoverView = (javafx.scene.image.ImageView) scene
                            .lookup("#playlistDetailAlbumCover");

                    if (songNameLabel != null) {
                        songNameLabel.setText(songTitle != null ? songTitle : "Unknown Song");
                        System.out.println("DEBUG Playlist: Alternative update - Updated song name via lookup to: "
                                + songNameLabel.getText());
                    } else {
                        System.out.println("ERROR Playlist: Could not find playlistDetailSongName via lookup");
                    }

                    if (artistNameLabel != null) {
                        artistNameLabel.setText(artist != null ? artist : "Unknown Artist");
                        System.out.println("DEBUG Playlist: Alternative update - Updated artist name via lookup to: "
                                + artistNameLabel.getText());
                    } else {
                        System.out.println("ERROR Playlist: Could not find playlistDetailArtistName via lookup");
                    }

                    if (durationLabel != null) {
                        String formattedDuration = formatDurationFromMillis(durationMs);
                        durationLabel.setText("Duration: " + formattedDuration);
                        System.out.println("DEBUG Playlist: Alternative update - Updated duration via lookup to: "
                                + durationLabel.getText());
                    } else {
                        System.out.println("ERROR Playlist: Could not find playlistDetailDuration via lookup");
                    }

                    if (albumCoverView != null) {
                        // We can't easily set the image here without the URL, but at least we checked
                        // it exists
                        System.out.println("DEBUG Playlist: Found albumCoverView via lookup");
                    }

                } else {
                    System.out.println("ERROR Playlist: Cannot perform scene lookup - no scene available");
                }

            } catch (Exception e) {
                System.out.println("ERROR Playlist: Alternative UI update failed: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onMusicChanged(Music music) {
        System.out.println("DEBUG Playlist: Received music update event");
        javafx.application.Platform.runLater(() -> {
            updatePlaylistDetailPanel(music);
        });
    }
}
