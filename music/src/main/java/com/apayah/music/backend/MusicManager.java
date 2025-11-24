package com.apayah.music.backend;
import javax.sound.sampled.LineUnavailableException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

public class MusicManager {

    private AudioPlayer player;

    public MusicManager(AudioPlayer player) throws LineUnavailableException {
        this.player = player;
    }

    public void play(Music music) {
        this.player.playTrack(music.getTrack());
    }

    public void seek(long milis) {
        player.getPlayingTrack().setPosition(milis);
    }

    public void resume() {
        if (this.player.isPaused()) {
            this.player.setPaused(false);
        }
    }

    public void pause() {
        if (!this.player.isPaused()) {
            this.player.setPaused(true);
        }
    }

    public boolean isPlaying() {
        if (this.player.getPlayingTrack() == null) {
            return false;
        }
        return getMusicTimePlayed() <= getCurrentlyPlaying().getTrack().getDuration();
    }

    public long getMusicTimePlayed() {
        return this.player.getPlayingTrack().getPosition();
    }

    public Music getCurrentlyPlaying() {
        return new Music(this.player.getPlayingTrack());
    }

}