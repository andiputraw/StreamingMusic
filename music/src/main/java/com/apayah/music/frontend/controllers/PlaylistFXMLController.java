package com.apayah.music.frontend.controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.apayah.music.backend.Music;
import com.apayah.music.backend.MusicPlayerFacade;
import com.apayah.music.frontend.AppState;
import com.apayah.music.frontend.SongData;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import com.apayah.music.playlist.Playlist;
import com.apayah.music.playlist.PlaylistManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class PlaylistFXMLController implements Initializable, AppState.MusicUpdateListener {

    private static final String UNKNOWN_SONG = "Unknown Song";

    // Singleton instance - will be set when FXML instantiates this controller
    private static PlaylistFXMLController instance;

    @FXML
    private TableView<SongData> songTableView;
    @FXML
    private TableColumn<SongData, Void> actionColumn;
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

    @FXML
    private Label playlistTitle;
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

    // Header for the playlist
    @FXML
    private StackPane playlistHeader;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Capture this instance for singleton access
        synchronized (PlaylistFXMLController.class) {
            if (instance == null) {
                instance = this;
            }
        }

        // Register as music update listener
        AppState.getInstance().addMusicUpdateListener(this);

        // Check if there is already music playing and update UI
        Music currentMusic = AppState.getInstance().getCurrentMusic();
        if (currentMusic != null) {
            updatePlaylistDetailPanel(currentMusic);
        }

        // Setup row factory for double-click playback
        if (songTableView != null) {
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

    /**
     * Setup the action column with Add buttons for each row
     */
    private void setupActionColumn() {
        if (actionColumn != null) {
            actionColumn.setCellFactory(param -> new ActionButtonTableCell());
        }
    }

    /**
     * Custom TableCell for displaying action buttons in the table
     */
    private class ActionButtonTableCell extends javafx.scene.control.TableCell<SongData, Void> {
        private final Button addBtn;

        public ActionButtonTableCell() {
            addBtn = new Button("Add");
            addBtn.getStyleClass().add("table-add-button");
            addBtn.setOnAction(this::handleAddButtonAction);
        }

        private void handleAddButtonAction(ActionEvent event) {
            SongData song = getTableView().getItems().get(getIndex());
            selectedSongForModal = song;
            showAddToPlaylistModal(event);
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
    }

    /**
     * Show the Add to Playlist modal
     */
    @FXML
    private void showAddToPlaylistModal(ActionEvent event) {
        List<String> playlistNames = PlaylistManager.getInstance().getSemuaPlaylist().stream()
                .map(p -> p.getNama()).toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(playlistNames.get(0), playlistNames);
        dialog.setTitle("Add to Playlist");
        dialog.setHeaderText("Select a playlist to add the song to.");
        dialog.setContentText("Playlist:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(playlistName -> {
            String songName = "";
            if (selectedSongForModal != null) {
                songName = selectedSongForModal.getSongTitle() + " - " +
                        selectedSongForModal.getAlbum();
            } else {
                SongData selectedSong = getSelectedSong();
                if (selectedSong != null) {
                    songName = UNKNOWN_SONG;
                }
            }
            PlaylistManager.getInstance().tambahLaguKePlaylist(playlistName, songName,
                    selectedSongForModal.getMusic().getTrack().getInfo().uri);
            selectedSongForModal = null;
        });
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
    }

    /**
     * Add song to selected playlist
     */
    @FXML
    private void addToSelectedPlaylist(ActionEvent event) {
        if (playlistToggleGroup != null && playlistToggleGroup.getSelectedToggle() != null) {
            getSelectedPlaylistName();

            hideAddToPlaylistModal(event);
        }
    }

    /**
     * Helper method to get selected playlist name
     */
    private String getSelectedPlaylistName() {
        RadioButton selectedRadio = (RadioButton) playlistToggleGroup.getSelectedToggle();
        if (selectedRadio == playlist1Radio) {
            return "BL8M";
        } else if (selectedRadio == playlist2Radio) {
            return "My Favorites";
        } else if (selectedRadio == playlist3Radio) {
            return "Chill Vibes";
        }
        return "";
    }

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
     * /**
     * Set parent controller reference to access control bar
     */
    // Removed unused setParentController method
    @FXML
    public void onPlayPlaylistButtonClick() {
        var musicPlayerFacade = AppState.getInstance().getMusicPlayer();
        musicPlayerFacade.pause();
        musicPlayerFacade.clearQueue();
        ObservableList<SongData> allSongs = songTableView.getItems();
        for (SongData song : allSongs) {
            if (song.getMusic() != null) {
                musicPlayerFacade.addToQueue(song.getMusic());
            }
        }
        musicPlayerFacade.jump(0);
        musicPlayerFacade.resume();

    }

    /**
     * Play selected song from playlist
     */
    private void playSelectedSong(SongData songData) {
        if (songData == null || songData.getMusic() == null) {
            return;
        }

        try {
            // Get music player facade
            var musicPlayerFacade = AppState.getInstance().getMusicPlayer();

            // Check if we are in search mode (playlist header is hidden)
            boolean isSearchMode = (playlistHeader != null && !playlistHeader.isVisible());

            if (isSearchMode) {
                musicPlayerFacade.clearQueue();
                ObservableList<SongData> allSongs = songTableView.getItems();
                for (SongData song : allSongs) {
                    if (song.getMusic() != null) {
                        musicPlayerFacade.addToQueue(song.getMusic());
                    }
                }

                int indexToJump = songTableView.getItems().indexOf(songData);
                musicPlayerFacade.jump(indexToJump);

            } else {
                int indexToJump = musicPlayerFacade.playingQueue().size();
                musicPlayerFacade.addToQueue(songData.getMusic());
                musicPlayerFacade.jump(indexToJump);
            }

            // Resume playback
            musicPlayerFacade.resume();

            // Notify everyone about the change
            AppState.getInstance().notifyMusicChanged(songData.getMusic());

            // Update control bar UI
            if (songData.getMusic().getTrack() != null) {
                updateControlBar(songData.getMusic().getTrack().getInfo());
            }

            // Update UI immediately
            updatePlaylistDetailPanel(songData.getMusic());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update control bar with song information
     */
    private void updateControlBar(com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo info) {
        try {
            double durationInSeconds = info.length / 1000.0;

            // Method 1: Via AppLayoutController (primary approach)
            AppLayoutController appController = AppLayoutController.getInstance();
            if (appController != null) {
                appController.updateMusicControl(info.title, info.author, "", durationInSeconds);
                return;
            }

            // Method 2: Direct singleton fallback
            ControlFXMLController controlController = ControlFXMLController.getInstance();
            if (controlController != null) {
                controlController.updateSongInfo(info.title, info.author, "", durationInSeconds);
                controlController.startPlayback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * External method to update playlist detail panel from other controllers
     */
    public void updatePlaylistDetailPanelExternal(Music music) {
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

        if (playlistDetailSongName != null) {
            playlistDetailSongName.setText(info.title != null ? info.title : UNKNOWN_SONG);
            updateDetailPanelFields(info);
        } else {
            tryPlaylistAlternativeUIUpdate(info.title, info.author, info.length);
        }
    }

    /**
     * Update individual detail panel fields
     */
    private void updateDetailPanelFields(AudioTrackInfo info) {
        if (playlistDetailArtistName != null) {
            playlistDetailArtistName.setText(info.author != null ? info.author : "Unknown Artist");
        }

        if (playlistDetailAlbumName != null) {
            playlistDetailAlbumName.setText("Unknown Album");
        }

        if (playlistDetailDuration != null) {
            String duration = formatDurationFromMillis(info.length);
            playlistDetailDuration.setText("Duration: " + duration);
        }

        updateAlbumCover(info);
    }

    /**
     * Update album cover image
     */
    private void updateAlbumCover(AudioTrackInfo info) {
        if (playlistDetailAlbumCover != null && info.artworkUrl != null && !info.artworkUrl.isEmpty()) {
            try {
                Image albumImage = new Image(info.artworkUrl);
                playlistDetailAlbumCover.setImage(albumImage);
            } catch (Exception e) {
                // Image loading failed, continue without cover
            }
        }
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
        javafx.application.Platform.runLater(() -> {
            try {
                javafx.scene.Scene scene = findScene();
                if (scene != null) {
                    updateUIElementsFromScene(scene, songTitle, artist, durationMs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Find the scene from available UI components
     */
    private javafx.scene.Scene findScene() {
        if (songTableView != null && songTableView.getScene() != null) {
            return songTableView.getScene();
        }
        if (playButton != null && playButton.getScene() != null) {
            return playButton.getScene();
        }
        return null;
    }

    /**
     * Update UI elements found in the scene
     */
    private void updateUIElementsFromScene(javafx.scene.Scene scene, String songTitle, String artist, long durationMs) {
        updateSongNameLabel(scene, songTitle);
        updateArtistNameLabel(scene, artist);
        updateDurationLabel(scene, durationMs);
    }

    /**
     * Update song name label
     */
    private void updateSongNameLabel(javafx.scene.Scene scene, String songTitle) {
        javafx.scene.control.Label songNameLabel = (javafx.scene.control.Label) scene.lookup("#playlistDetailSongName");
        if (songNameLabel != null) {
            songNameLabel.setText(songTitle != null ? songTitle : UNKNOWN_SONG);
        }
    }

    /**
     * Update artist name label
     */
    private void updateArtistNameLabel(javafx.scene.Scene scene, String artist) {
        javafx.scene.control.Label artistNameLabel = (javafx.scene.control.Label) scene
                .lookup("#playlistDetailArtistName");
        if (artistNameLabel != null) {
            artistNameLabel.setText(artist != null ? artist : "Unknown Artist");
        }
    }

    /**
     * Update duration label
     */
    private void updateDurationLabel(javafx.scene.Scene scene, long durationMs) {
        javafx.scene.control.Label durationLabel = (javafx.scene.control.Label) scene.lookup("#playlistDetailDuration");
        if (durationLabel != null) {
            String formattedDuration = formatDurationFromMillis(durationMs);
            durationLabel.setText("Duration: " + formattedDuration);
        }
    }

    @Override
    public void onMusicChanged(Music music) {
        Platform.runLater(() -> updatePlaylistDetailPanel(music));
    }

    /**
     * Perform search for songs
     */
    public void performSearch(String query) {
        if (playlistHeader != null) {
            playlistHeader.setVisible(false);
            playlistHeader.setManaged(false);
        }

        AppState.getInstance().getMusicPlayer().search(query).thenAccept(musics -> Platform.runLater(() -> {
            ObservableList<SongData> searchResults = FXCollections.observableArrayList();
            int index = 1;
            for (Music music : musics) {
                searchResults.add(new SongData(
                        index++,
                        music.getTrack().getInfo().title,
                        music.getTrack().getInfo().author,
                        "-",
                        formatDurationFromMillis(music.getTrack().getDuration()),
                        null,
                        music));
            }
            songTableView.setItems(searchResults);
        }));

    }

    public void loadFromPlaylist(Playlist playlist) {
        MusicPlayerFacade musicPlayer = AppState.getInstance().getMusicPlayer();
        List<String> links = playlist.getDaftarLinkLagu();
        ObservableList<SongData> searchResults = FXCollections.observableArrayList();
        for (int i = 0; i < links.size(); i++) {
            int index = i;

            musicPlayer.search(links.get(index)).thenAccept(musics -> Platform.runLater(() -> {
                if (!musics.isEmpty()) {
                    Music m = musics.get(0);
                    AudioTrackInfo info = m.getTrack().getInfo();

                    searchResults.add(new SongData(
                            index,
                            info.title,
                            info.author,
                            "-",
                            formatDurationFromMillis(m.getTrack().getDuration()),
                            new Image(info.artworkUrl),
                            m));

                    if (songTableView != null) {
                        songTableView.setItems(searchResults);
                    }
                }
            }));

        }
    }

    public void setPlaylistTitle(String title) {
        this.playlistTitle.setText(title);
    }
}
