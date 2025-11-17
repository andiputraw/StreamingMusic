package com.apayah.music.backend;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MusicQueue {
    private List<Music> musics;
    
    private int pos;
    
    public MusicQueue() {
        this.musics = new ArrayList<>();
        pos = 0;
    }
    
    public List<Music> getMusics() {
        return musics;
    }

    public void addQueue(Music music) {
        musics.add(music);
    }

    public Music jumpQueue(int pos) {
        if(pos > musics.size()) {
            throw new IndexOutOfBoundsException();
        }

        this.pos = pos;
        return getCurrent();
    }

    public Music getCurrent() {
        return this.musics.get(pos - 1);
    }

    public boolean isEmpty() {
        return this.musics.size() == 0;
    }

    public Music next() {
        if(isOnEnd()) {
            new IndexOutOfBoundsException();
        }

        this.pos += 1;

        return getCurrent();
    }

    public boolean isOnEnd() {
        return this.pos >= this.musics.size(); 
    }
    
}
