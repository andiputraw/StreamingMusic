package com.apayah.music.frontend;

import com.apayah.music.backend.Music;
import com.apayah.music.backend.MusicPlayerFacade;
import com.apayah.music.playlist.Playlist;

import javafx.scene.image.Image;

public class AppState {
    private static AppState instance = new AppState();
    private MusicPlayerFacade musicPlayer;
    private Music currentMusic;
    private Playlist selectedPlaylist;

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
    
    public interface MusicUpdateListener {
        void onMusicChanged(Music music);
    }
    
    private java.util.List<MusicUpdateListener> listeners = new java.util.ArrayList<>();
    
    public void addMusicUpdateListener(MusicUpdateListener listener) {
        listeners.add(listener);
    }
    
    public void removeMusicUpdateListener(MusicUpdateListener listener) {
        listeners.remove(listener);
    }
    
    public void notifyMusicChanged(Music music) {
        this.currentMusic = music;
        for (MusicUpdateListener listener : listeners) {
            try {
                listener.onMusicChanged(music);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Music getCurrentMusic() {
        return currentMusic;
    }

    public void setCurrentMusic(Music currentMusic) {
        this.currentMusic = currentMusic;
    }

    public void setSelectedPlaylist(Playlist playlist) {
        this.selectedPlaylist = playlist;
    }
}
