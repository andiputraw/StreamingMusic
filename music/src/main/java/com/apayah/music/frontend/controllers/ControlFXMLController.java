/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.apayah.music.frontend.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import com.apayah.music.frontend.AppState;
import com.apayah.music.backend.Music;
import com.apayah.music.backend.MusicPlayerFacade;

/**
 * FXML Controller class
 *
 * @author PLN
 */
public class ControlFXMLController implements Initializable, AppState.MusicUpdateListener {

    // Singleton instance - will be set when FXML instantiates this controller
    private static ControlFXMLController instance;

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

    // Music player state
    private boolean isPlaying = true;
    private boolean isShuffleOn = false;
    private double currentTime = 146.0; // in seconds (2:26)
    private double totalTime = 240.0; // in seconds (4:00)
    private Timeline timeline;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Capture this instance for singleton access
        instance = this;

        // Register as music update listener
        AppState.getInstance().addMusicUpdateListener(this);

        // Check if there is already music playing and update UI
        Music currentMusic = AppState.getInstance().getCurrentMusic();
        if (currentMusic != null) {
            onMusicChanged(currentMusic);
        }

        // Initialize progress slider
        if (progressSlider != null) {
            progressSlider.setMin(0);
            progressSlider.setMax(totalTime);
            progressSlider.setValue(currentTime);
        }

        // Update time labels
        updateTimeLabels();

        // Create timeline for updating progress
        createTimeline();

        // Setup slider listeners
        setupSliderListeners();
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
        timeline.setCycleCount(Animation.INDEFINITE);
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
        AppState.getInstance().getMusicPlayer().pref();
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
            if (timeline != null)
                timeline.play();
        } else {
            if (musicPlayer != null) {
                musicPlayer.pause();
            }
            // Change to play icon
            updatePlayPauseIcon();
            if (timeline != null)
                timeline.pause();
        }
    }

    @FXML
    private void onNextAction(ActionEvent event) {
        // Go to next song
        loadNextSong();
        AppState.getInstance().getMusicPlayer().next();

    }

    @FXML
    private void onProgressSliderClicked(MouseEvent event) {
        MusicPlayerFacade musicPlayer = AppState.getInstance().getMusicPlayer();
        // Calculate new position based on click
        double mouseX = event.getX();
        double sliderWidth = progressSlider.getWidth();
        double percentage = mouseX / sliderWidth;
        double newTime = percentage * totalTime;

        currentTime = Math.max(0, Math.min(newTime, totalTime));
        progressSlider.setValue(currentTime);
        updateTimeLabels();

        musicPlayer.seek((long) currentTime * 1000);
    }

    @FXML
    private void onProgressSliderDragged(MouseEvent event) {
        // Handle dragging - the valueProperty listener will handle the update
    }

    @FXML
    private void onQueueAction(ActionEvent event) {
        // Open queue/playlist view
    }

    private void loadPreviousSong() {
        MusicPlayerFacade musicPlayer = AppState.getInstance().getMusicPlayer();
        if (musicPlayer == null) return;
        
        try {
            List<Music> queue = musicPlayer.playingQueue();
            Music currentMusic = AppState.getInstance().getCurrentMusic();
            
            if (queue == null || queue.isEmpty()) return;
            
            int currentIndex = -1;
            if (currentMusic != null) {
                for (int i = 0; i < queue.size(); i++) {
                    if (queue.get(i).equals(currentMusic)) {
                        currentIndex = i;
                        break;
                    }
                }
            }
            
            // Calculate previous index with circular navigation
            int previousIndex = (currentIndex - 1 + queue.size()) % queue.size();
            musicPlayer.jump(previousIndex);
        } catch (Exception e) {
            // Silently ignore errors
        }
    }

    private void loadNextSong() {
        MusicPlayerFacade musicPlayer = AppState.getInstance().getMusicPlayer();
        if (musicPlayer == null) return;
        
        try {
            List<Music> queue = musicPlayer.playingQueue();
            Music currentMusic = AppState.getInstance().getCurrentMusic();
            
            if (queue == null || queue.isEmpty()) return;
            
            int currentIndex = -1;
            if (currentMusic != null) {
                for (int i = 0; i < queue.size(); i++) {
                    if (queue.get(i).equals(currentMusic)) {
                        currentIndex = i;
                        break;
                    }
                }
            }
            
            // Calculate next index with circular navigation
            int nextIndex = (currentIndex + 1) % queue.size();
            musicPlayer.jump(nextIndex);
        } catch (Exception e) {
            // Silently ignore errors
        }
    }

    // Public methods for external control
    public void setSongInfo(String title, String artist, String albumCoverPath) {
        if (songTitleLabel != null) {
            songTitleLabel.setText(title);
        }

        if (artistLabel != null) {
            artistLabel.setText(artist);
        }

        updateAlbumCover(albumCoverPath);
    }

    private void updateAlbumCover(String albumCoverPath) {
        if (albumCoverImage == null) {
            return;
        }

        if (albumCoverPath != null && !albumCoverPath.isEmpty()) {
            loadAlbumCoverFromPath(albumCoverPath);
        } else {
            loadDefaultAlbumCover();
        }
    }

    private void loadAlbumCoverFromPath(String albumCoverPath) {
        try {
            Image albumImage = isUrlPath(albumCoverPath)
                    ? new Image(albumCoverPath, true)
                    : new Image("file:" + albumCoverPath);
            albumCoverImage.setImage(albumImage);
        } catch (Exception e) {
            // Silently ignore album cover loading errors
        }
    }

    private void loadDefaultAlbumCover() {
        try {
            albumCoverImage.setImage(
                    new Image(getClass().getResource("/image/default_album_cover.png").toExternalForm()));
        } catch (Exception e) {
            // Silently ignore album cover loading errors
        }
    }

    private boolean isUrlPath(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }

    public void setDuration(double duration) {
        this.totalTime = duration;
        if (progressSlider != null) {
            progressSlider.setMax(totalTime);
        }
        updateTimeLabels();
    }

    public void setCurrentTime(double time) {
        this.currentTime = time;
        if (progressSlider != null) {
            progressSlider.setValue(currentTime);
        }
        updateTimeLabels();
    }

    /**
     * Update all song information at once including duration and reset progress
     */
    public void updateSongInfo(String title, String artist, String albumCoverPath, double durationInSeconds) {
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
                }
            } catch (Exception e) {
                // Silently ignore icon loading errors
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
        updateSongInfo("Test Song", "Test Artist", "", 180.0);
        startPlayback();
    }

    @Override
    public void onMusicChanged(Music music) {
        if (music == null || music.getTrack() == null)
            return;
        var info = music.getTrack().getInfo();
        javafx.application.Platform
                .runLater(() -> updateSongInfo(info.title, info.author, info.artworkUrl, info.length / 1000.0));
    }
}
