/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.apayah.music.frontend.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.apayah.music.frontend.AppState;
import com.apayah.music.backend.Music;

/**
 * FXML Controller class
 *
 * @author PLN
 */
public class ControlFXMLController implements Initializable, AppState.MusicUpdateListener {

    @FXML
    private ImageView albumCoverImage;
    @FXML
    private Label songTitleLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Button shuffleButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button playPauseButton;
    @FXML
    private ImageView playPauseIcon;
    @FXML
    private Button nextButton;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Slider progressSlider;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private Button queueButton;
    @FXML
    private Button addToPlaylistButton;

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
    private Button confirmAddButton;

    // Radio buttons for playlist selection
    @FXML
    private RadioButton playlist1Radio;
    @FXML
    private RadioButton playlist2Radio;
    @FXML
    private RadioButton playlist3Radio;
    private ToggleGroup playlistToggleGroup;

    // Music player state
    private boolean isPlaying = true;
    private boolean isShuffleOn = false;
    private double currentTime = 146.0; // in seconds (2:26)
    private double totalTime = 240.0; // in seconds (4:00)
    private Timeline timeline;
    private static ControlFXMLController instance;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        System.out.println("ControlFXMLController initialized successfully");
        
        // Register as music update listener
        AppState.getInstance().addMusicUpdateListener(this);
        
        // Check if there is already music playing and update UI
        Music currentMusic = AppState.getInstance().getCurrentMusic();
        if (currentMusic != null) {
            System.out.println("DEBUG ControlFXML: Found current music in AppState, updating UI");
            onMusicChanged(currentMusic);
        }

        // Initialize progress slider
        progressSlider.setMin(0);
        progressSlider.setMax(totalTime);
        progressSlider.setValue(currentTime);

        // Update time labels
        updateTimeLabels();

        // Create timeline for updating progress
        createTimeline();

        // Setup slider listeners
        setupSliderListeners();

        // Initialize modal
        initializeModal();
    }

    private void createTimeline() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (isPlaying && currentTime < totalTime) {
                        currentTime++;
                        progressSlider.setValue(currentTime);
                        updateTimeLabels();
                    }
                    if (currentTime >= totalTime) {
                        // Song finished, go to next song
                        onNextAction(null);
                    }
                }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        if (isPlaying) {
            timeline.play();
        }
    }

    private void setupSliderListeners() {
        // Listen for slider value changes
        progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (progressSlider.isValueChanging()) {
                currentTime = newValue.doubleValue();
                updateTimeLabels();
            }
        });
    }

    private void updateTimeLabels() {
        currentTimeLabel.setText(formatTime(currentTime));
        totalTimeLabel.setText(formatTime(totalTime));
    }

    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, secs);
    }

    /**
     * Format duration from milliseconds to seconds
     */
    public static double millisecondsToSeconds(long milliseconds) {
        return milliseconds / 1000.0;
    }

    @FXML
    private void onShuffleAction(ActionEvent event) {
        isShuffleOn = !isShuffleOn;
        if (isShuffleOn) {
            shuffleButton.setStyle("-fx-background-color: #1db954; -fx-background-radius: 15;");
        } else {
            shuffleButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 15;");
        }
        System.out.println("Shuffle: " + (isShuffleOn ? "ON" : "OFF"));
    }

    @FXML
    private void onPreviousAction(ActionEvent event) {
        // Reset to beginning or go to previous song
        if (currentTime > 3.0) {
            currentTime = 0.0;
        } else {
            // Go to previous song logic here
            loadPreviousSong();
        }
        progressSlider.setValue(currentTime);
        updateTimeLabels();
        System.out.println("Previous song");
    }

    @FXML
    private void onPlayPauseAction(ActionEvent event) {
        isPlaying = !isPlaying;
        var musicPlayer = AppState.getInstance().getMusicPlayer();

        if (isPlaying) {
            if (musicPlayer != null) {
                musicPlayer.resume();
            }
            // Change to pause icon
            updatePlayPauseIcon();
            if (timeline != null) timeline.play();
            System.out.println("Playing");
        } else {
            if (musicPlayer != null) {
                musicPlayer.pause();
            }
            // Change to play icon
            updatePlayPauseIcon();
            if (timeline != null) timeline.pause();
            System.out.println("Paused");
        }
    }

    @FXML
    private void onNextAction(ActionEvent event) {
        // Go to next song
        loadNextSong();
        System.out.println("Next song");
    }

    @FXML
    private void onProgressSliderClicked(MouseEvent event) {
        // Calculate new position based on click
        double mouseX = event.getX();
        double sliderWidth = progressSlider.getWidth();
        double percentage = mouseX / sliderWidth;
        double newTime = percentage * totalTime;

        currentTime = Math.max(0, Math.min(newTime, totalTime));
        progressSlider.setValue(currentTime);
        updateTimeLabels();

        System.out.println("Seeked to: " + formatTime(currentTime));
    }

    @FXML
    private void onProgressSliderDragged(MouseEvent event) {
        // Handle dragging - the valueProperty listener will handle the update
        System.out.println("Dragging to: " + formatTime(currentTime));
    }

    @FXML
    private void onQueueAction(ActionEvent event) {
        System.out.println("Queue button clicked");
        // Open queue/playlist view
    }

    private void loadPreviousSong() {
        // Simulate loading previous song
        songTitleLabel.setText("Previous Song");
        artistLabel.setText("Previous Artist");
        currentTime = 0.0;
        totalTime = 200.0; // 3:20
        progressSlider.setMax(totalTime);
        progressSlider.setValue(currentTime);
        updateTimeLabels();
    }

    private void loadNextSong() {
        // Simulate loading next song
        songTitleLabel.setText("Next Song");
        artistLabel.setText("Next Artist");
        currentTime = 0.0;
        totalTime = 180.0; // 3:00
        progressSlider.setMax(totalTime);
        progressSlider.setValue(currentTime);
        updateTimeLabels();
    }

    // Public methods for external control
    public void setSongInfo(String title, String artist, String albumCoverPath) {
        System.out.println("DEBUG ControlFXML: setSongInfo START");
        System.out.println("  Received title: '" + title + "'");
        System.out.println("  Received artist: '" + artist + "'");
        System.out.println("  Received albumCoverPath: '" + albumCoverPath + "'");
        System.out.println("  songTitleLabel: " + songTitleLabel);
        System.out.println("  artistLabel: " + artistLabel);
        System.out.println("  albumCoverImage: " + albumCoverImage);

        if (songTitleLabel != null) {
            String oldText = songTitleLabel.getText();
            songTitleLabel.setText(title);
            System.out.println("  songTitleLabel updated: '" + oldText + "' -> '" + songTitleLabel.getText() + "'");
        } else {
            System.out.println("  ERROR: songTitleLabel is NULL!");
        }

        if (artistLabel != null) {
            String oldText = artistLabel.getText();
            artistLabel.setText(artist);
            System.out.println("  artistLabel updated: '" + oldText + "' -> '" + artistLabel.getText() + "'");
        } else {
            System.out.println("  ERROR: artistLabel is NULL!");
        }
        
        // Update album cover - handle both file paths and URLs
        if (albumCoverPath != null && !albumCoverPath.isEmpty()) {
            try {
                Image albumImage;
                // Check if it's a URL or file path
                if (albumCoverPath.startsWith("http://") || albumCoverPath.startsWith("https://")) {
                    System.out.println("  Loading album cover from URL: " + albumCoverPath);
                    albumImage = new Image(albumCoverPath, true); // true for background loading
                } else {
                    System.out.println("  Loading album cover from file: " + albumCoverPath);
                    albumImage = new Image("file:" + albumCoverPath);
                }
                
                if (albumCoverImage != null) {
                    albumCoverImage.setImage(albumImage);
                    System.out.println("  Album cover updated successfully");
                } else {
                    System.out.println("  ERROR: albumCoverImage is NULL!");
                }
            } catch (Exception e) {
                System.out.println("  ERROR loading album cover: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("  No album cover to update (empty path)");
        }
        
        System.out.println("DEBUG ControlFXML: setSongInfo END");
    }

    public void setDuration(double duration) {
        this.totalTime = duration;
        progressSlider.setMax(totalTime);
        updateTimeLabels();
    }

    public void setCurrentTime(double time) {
        this.currentTime = time;
        progressSlider.setValue(currentTime);
        updateTimeLabels();
    }

    /**
     * Update all song information at once including duration and reset progress
     */
    public void updateSongInfo(String title, String artist, String albumCoverPath, double durationInSeconds) {
        System.out.println("=========================================");
        System.out.println("DEBUG ControlFXML: updateSongInfo called");
        System.out.println("  title: '" + title + "'");
        System.out.println("  artist: '" + artist + "'");
        System.out.println("  albumCoverPath: '" + albumCoverPath + "'");
        System.out.println("  duration: " + durationInSeconds);
        System.out.println("=========================================");
        
        // Update song info (title, artist, album cover)
        setSongInfo(title, artist, albumCoverPath);
        
        // Update duration and reset progress
        setDuration(durationInSeconds);
        setCurrentTime(0); // Reset to beginning

        // Stop existing timeline
        if (timeline != null) {
            timeline.stop();
        }
        
        // Create new timeline with new duration
        createTimeline();

        // Force update to playing state
        isPlaying = true;
        updatePlayPauseIcon();
        
        // Start timeline
        if (timeline != null) {
            timeline.play();
        }
        
        System.out.println("DEBUG ControlFXML: updateSongInfo completed - labels should now show: " + title + " - " + artist);
    }

    /**
     * Start or resume playback
     */
    public void startPlayback() {
        if (!isPlaying) {
            isPlaying = true;
            updatePlayPauseIcon();
            if (timeline != null) {
                timeline.play();
            }
        }
    }

    /**
     * Pause playback
     */
    public void pausePlayback() {
        if (isPlaying) {
            isPlaying = false;
            updatePlayPauseIcon();
            if (timeline != null) {
                timeline.pause();
            }
        }
    }

    /**
     * Update play/pause icon based on current state
     */
    private void updatePlayPauseIcon() {
        if (playPauseIcon != null) {
            try {
                String iconName = isPlaying ? "/image/pause.png" : "/image/play.png";
                URL iconUrl = getClass().getResource(iconName);
                if (iconUrl != null) {
                    Image icon = new Image(iconUrl.toExternalForm());
                    playPauseIcon.setImage(icon);
                } else {
                    System.out.println("Could not find icon: " + iconName);
                }
            } catch (Exception e) {
                System.out.println("Could not load play/pause icon: " + e.getMessage());
            }
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public double getTotalTime() {
        return totalTime;
    }

    // Modal-related methods
    private void initializeModal() {
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
        System.out.println("Add to Playlist modal opened from Control Bar for: " + songTitleLabel.getText());
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
     * Add current playing song to selected playlist
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

            String currentSong = songTitleLabel.getText() + " - " + artistLabel.getText();
            System.out.println("Added '" + currentSong + "' to playlist: " + selectedPlaylist);

            // Hide modal after adding
            hideAddToPlaylistModal(event);

            // Show success message
            showSuccessMessage("'" + songTitleLabel.getText() + "' added to " + selectedPlaylist + "!");

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
     * Get singleton instance
     */
    public static ControlFXMLController getInstance() {
        return instance;
    }

    /**
     * Test method to manually update control bar - for debugging
     */
    public void testUpdateControlBar() {
        System.out.println("Testing manual control bar update...");
        updateSongInfo("Test Song", "Test Artist", "", 180.0);
        startPlayback();
        System.out.println("Manual control bar update completed");
    }

    @Override
    public void onMusicChanged(Music music) {
        if (music == null || music.getTrack() == null) return;
        var info = music.getTrack().getInfo();
        javafx.application.Platform.runLater(() -> {
            updateSongInfo(info.title, info.author, info.artworkUrl, info.length / 1000.0);
        });
    }
}
