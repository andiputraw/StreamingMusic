package com.apayah.music.backend;

import com.apayah.music.frontend.AppState;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread-safe scheduler that adapts your MusicQueue to be event-driven.
 * This class fixes the bugs in your original MusicQueue logic.
 */
public class MusicQueueScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private MusicQueue queue;
    private final Object queueLock = new Object(); // Used for synchronization

    private static final Logger log = LoggerFactory.getLogger(MusicQueueScheduler.class);


    public MusicQueueScheduler(AudioPlayer player, MusicQueue queue) {
        this.player = player;
        this.queue = queue;
    }

    /**
     * Called by Lavaplayer when a track ends.
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        log.error("Track Exception: {}" , exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        log.warn("Track stuck: {} (threshold {} ms)", track.getInfo().title, thresholdMs);
        nextTrack();
    }

    /**
     * Adds a new Music object to the queue.
     * If nothing is playing, it starts playback.
     */
    public void queue(Music music) {
        boolean playNow = false;
        synchronized (queueLock) {
            queue.addQueue(music);
            // If the player is idle and this is the first song, start playing.
            if (player.getPlayingTrack() == null && queue.getMusics().size() == 1) {
                playNow = true;
            }
        }

        if (playNow) {
            nextTrack(); // Start the first track
        }
    }

    /**
     * Safely plays the next track in the queue.
     */
    public void nextTrack() {
        Music nextMusic = null;
        synchronized (queueLock) {
            // We check *before* calling next() to avoid bugs in your class
            if (!queue.isOnEnd()) {
                nextMusic = queue.next();
            }
        }

        if (nextMusic != null) {
            // Clone the track to ensure it can be played again if it was played before
            player.startTrack(nextMusic.getTrack().makeClone(), false);
            AppState.getInstance().notifyMusicChanged(nextMusic);
        } else {
            // End of queue, player stops.
        }
    }

    /**
     * Jumps to a specific track in the queue.
     * 
     * @param index The 0-based index (e.g., 0 for the first song).
     */
    public boolean jump(int index) {
        Music musicToPlay = null;
        synchronized (queueLock) {
            try {
                // Your queue.jumpQueue is 1-based, so we add 1
                musicToPlay = queue.jumpQueue(index + 1);
            } catch (IndexOutOfBoundsException e) {
                return false; // Invalid index
            }
        }

        if (musicToPlay != null) {
            player.startTrack(musicToPlay.getTrack().makeClone(), false);
            AppState.getInstance().notifyMusicChanged(musicToPlay);
            return true;
        }
        return false;
    }

    /**
     * Gets a thread-safe *copy* of the music list.
     */
    public List<Music> getQueue() {
        synchronized (queueLock) {
            return new ArrayList<>(queue.getMusics());
        }
    }

    /**
     * Gets the currently playing music.
     */
    public Music getCurrentlyPlaying() {
        synchronized (queueLock) {
            if (player.getPlayingTrack() == null || queue.isEmpty()) {
                return null;
            }
            try {
                // This can fail if pos=0, but nextTrack/jump should prevent that.
                return queue.getCurrent();
            } catch (IndexOutOfBoundsException e) {
                return null; // Queue is out of sync
            }
        }
    }

    /**
     * Clears the queue and stops the player.
     */
    public void clearQueue() {
        synchronized (queueLock) {
            // The safest way to clear your queue is to just make a new one
            this.queue = new MusicQueue();
            player.stopTrack();
        }
    }

    /**
     * Clears the music queue safely.
     */
    public void clear() {
        synchronized (queueLock) {
            queue.clearQueue();
        }
    }
}