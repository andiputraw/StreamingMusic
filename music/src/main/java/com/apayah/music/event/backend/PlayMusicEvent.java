package com.apayah.music.event.backend;

import com.apayah.music.event.backend.contract.BackendEvent;

public class PlayMusicEvent implements BackendEvent {
    private String url;
    public String getUrl() {
        return url;
    }
    public PlayMusicEvent(String url){
        this.url = url;
    }
}
