package com.apayah.music.backend;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.ArrayList;
import java.util.List;

/**
 * A thread-safe scheduler that adapts your MusicQueue to be event-driven.
 * This class fixes the bugs in your original MusicQueue logic.
 */
public class MusicQueueScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private MusicQueue queue;
    private final Object queueLock = new Object(); // Used for synchronization

    public MusicQueueScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new MusicQueue();
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
            player.startTrack(nextMusic.getTrack(), false);
        } else {
            // End of queue, player stops.
        }
    }

    /**
     * Jumps to a specific track in the queue.
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
            player.startTrack(musicToPlay.getTrack(), false);
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
}