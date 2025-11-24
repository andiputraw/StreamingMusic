package com.apayah.music.frontend.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.apayah.music.backend.Music;
import com.apayah.music.frontend.AppState;
import com.apayah.music.frontend.SongData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class PlaylistFXMLController implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

    private void initializeSongTable() {
        var musicPlayerFacade = AppState.getInstance().getMusicPlayer();

        musicPlayerFacade.search("Indonesia Raya").thenAccept(musics -> {
            ObservableList<SongData> songs = FXCollections.observableArrayList();

            for (Music music : musics) {
                var info = music.getTrack().getInfo();
                songs.add(new SongData(0, info.title, info.author, "", String.valueOf(info.length),
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
        if (modalOverlay != null) {
            modalOverlay.setVisible(true);
            modalOverlay.setManaged(true);
            modalOverlay.toFront();

            // Clear any previous selection
            if (playlistToggleGroup != null) {
                playlistToggleGroup.selectToggle(null);
            }
        }
        System.out.println("Add to Playlist modal opened");
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

            // Add song to queue and play immediately
            musicPlayerFacade.addToQueue(songData.getMusic());
            // musicPlayerFacade.jump(musicPlayerFacade.playingQueue().size()); 
            musicPlayerFacade.resume();

            // Update control bar UI if parent controller is available
            if (parentController != null) {
                try {
                    var info = songData.getMusic().getTrack().getInfo();
                    parentController.setSongInfo(info.title, info.author, "");
                    parentController.setSongDuration(info.length / 1000.0); // Convert to seconds
                    parentController.setCurrentTime(0); // Reset to beginning
                } catch (Exception e) {
                    System.out.println("Error updating control bar: " + e.getMessage());
                }
            } else {
                // Try to get instance directly if no parent reference
                try {
                    var mainController = MainLayoutController.getInstance();
                    if (mainController != null) {
                        var info = songData.getMusic().getTrack().getInfo();
                        mainController.setSongInfo(info.title, info.author, "");
                        mainController.setSongDuration(info.length / 1000.0);
                        mainController.setCurrentTime(0); // Reset to beginning
                    }
                } catch (Exception e) {
                    System.out.println("Error getting MainLayoutController instance: " + e.getMessage());
                }
            }

            System.out.println("Playing: " + songData.getSongTitle() + " by " + songData.getArtist());

        } catch (Exception e) {
            System.out.println("Error playing song: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
