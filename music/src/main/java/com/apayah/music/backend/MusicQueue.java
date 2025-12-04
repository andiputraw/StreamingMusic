package com.apayah.music.backend;


import java.util.ArrayList;
import java.util.List;

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
        return this.musics.isEmpty();
    }

    public int getCurrentIndex() {
        return this.pos - 1;
    }

    public Music next() {
        if(isOnEnd()) {
            throw new IndexOutOfBoundsException();
        }

        this.pos += 1;

        return getCurrent();
    }

    public boolean isOnEnd() {
        return this.pos >= this.musics.size(); 
    }

    public boolean isOnStart() {
        return this.pos <= 1;
    }
    
    public void clearQueue() {
        this.musics.clear();
        this.pos = 0;
    }
}