package com.apayah.music.frontend.controllers;

import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.control.cell.PropertyValueFactory;
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

        musicPlayerFacade
        // Membuat ObservableList dengan data lagu sesuai format baru
        ObservableList<SongData> songs = FXCollections.observableArrayList(
                new SongData(1, "Mr. Chu", "Apink", "Pink Blossom", "3:35", new Image("file:./image/song1.jpg")),
                new SongData(2, "HIP", "MAMAMOO", "reality in BLACK", "3:15", new Image("file:./image/song2.jpg")),
                new SongData(3, "P.S. I LOVE YOU", "Paul Partohap", "Best Album", "4:12",
                        new Image("file:./image/sSPH844ILIt_large.jpg")),
                new SongData(4, "Old Love", "Zion.T", "Red Light", "3:45", new Image("file:./image/song1.jpg")),
                new SongData(5, "Sample Song", "Sample Artist", "Sample Album", "2:58",
                        new Image("file:./image/song2.jpg")));

        // Menetapkan ObservableList ke TableView
        if (songTableView != null) {
            songTableView.setItems(songs);
        }
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
                songName = selectedSongForModal.getSongTitle() + " - " + selectedSongForModal.getAlbum();
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
}
