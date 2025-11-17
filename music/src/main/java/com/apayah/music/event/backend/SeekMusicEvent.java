package com.apayah.music.event.backend;

import com.apayah.music.event.backend.contract.BackendEvent;

public class SeekMusicEvent implements BackendEvent {
    private long milis;

    public long getMilis() {
        return milis;
    }

    public SeekMusicEvent(long milis) {
        this.milis = milis;
    }

}
