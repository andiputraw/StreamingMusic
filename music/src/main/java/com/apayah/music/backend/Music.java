package com.apayah.music.backend;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class Music {
    private AudioTrack track;
    
    public AudioTrack getTrack() {
        return track;
    }

    public Music(AudioTrack track) {
        this.track = track;
    }
}
