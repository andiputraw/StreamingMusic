package com.apayah.music.event.backend;

import com.apayah.music.event.backend.contract.BackendEvent;

public class AddMusicEvent implements BackendEvent {
    private String url;
    public String getUrl() {
        return url;
    }
    public AddMusicEvent(String url){
        this.url = url;
    }
}
