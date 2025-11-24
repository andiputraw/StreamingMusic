package com.apayah.music.frontend;

import com.apayah.music.backend.Music;
import com.apayah.music.backend.MusicPlayerFacade;

import javafx.scene.image.Image;

public class AppState {
    private static AppState instance = new AppState();
    private MusicPlayerFacade musicPlayer;

    public AppState() {
        try {
            this.musicPlayer = new MusicPlayerFacade();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public MusicPlayerFacade getMusicPlayer() {
        return musicPlayer;
    }

    public void setMusicPlayer(MusicPlayerFacade musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public static AppState getInstance() {

        return instance;
    }
}
