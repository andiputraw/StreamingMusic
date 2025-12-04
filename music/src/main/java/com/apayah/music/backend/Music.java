package com.apayah.music.backend;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Music {
    private AudioTrack track;
    
    public AudioTrack getTrack() {
        return track;
    }

    public Music(AudioTrack track) {
        this.track = track;
    }
}