package com.apayah.music.playlist;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Karena hanya ada 1 PlaylistManager.
 */
@SuppressWarnings("java:S6548")
public class PlaylistManager {

    private static PlaylistManager instance;
    private final ObservableList<Playlist> semuaPlaylist;
    private PlaylistStorage storage; // Depend on the interface

    private static final Logger log = LoggerFactory.getLogger(PlaylistManager.class);


    private PlaylistManager() {
        // Default to file-based storage
        this.storage = new FilePlaylistStorage();
        semuaPlaylist = FXCollections.observableArrayList();
        // Load initial data
        loadPlaylists();
    }

    public static synchronized PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }

    /**
     * Injects a storage mechanism, primarily for testing purposes.
     * @param storage The PlaylistStorage implementation to use.
     */
    public void setStorage(PlaylistStorage storage) {
        this.storage = storage;
    }

    /**
     * Clears all playlists and reloads them from the configured storage.
     */
    public void loadPlaylists() {
        List<Playlist> loaded = storage.load();
        semuaPlaylist.setAll(loaded);
        log.info("PlaylistManager loaded {} playlists from storage.", loaded.size());
    }

    public void buatPlaylist(String namaPlaylist) {
        // Check if a playlist with the same name already exists
        if (semuaPlaylist.stream().noneMatch(p -> p.getNama().equals(namaPlaylist))) {
            semuaPlaylist.add(new Playlist(namaPlaylist));
            storage.save(new ArrayList<>(semuaPlaylist));
        }
    }

    public void hapusPlaylist(String namaPlaylist) {
        semuaPlaylist.removeIf(p -> p.getNama().equals(namaPlaylist));
        storage.save(new ArrayList<>(semuaPlaylist));
    }

    public void tambahLaguKePlaylist(String namaPlaylist, String judul, String link) {
        for (Playlist p : semuaPlaylist) {
            if (p.getNama().equals(namaPlaylist)) {
                p.tambahLagu(judul, link);
                storage.save(new ArrayList<>(semuaPlaylist));
                break;
            }
        }
    }

    public ObservableList<Playlist> getSemuaPlaylist() {
        return semuaPlaylist;
    }

    public Playlist getPlaylist(String namaPlaylist) {
        for (Playlist p : semuaPlaylist) {
            if (p.getNama().equals(namaPlaylist)) {
                return p;
            }
        }
        return null;
    }
}
