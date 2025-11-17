package com.apayah.music.event.backend;

import com.apayah.music.event.backend.contract.BackendEvent;

public class JumpMusicEvent implements BackendEvent {
    private int index;

    public int getIndex() {
        return index;
    }

    public JumpMusicEvent(int idx) {
        this.index = idx;
    }
}
