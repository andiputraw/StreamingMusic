package com.apayah.music.playlist;

import java.io.*;
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

    private ObservableList<Playlist> semuaPlaylist;
    private static final String FILE_PATH = "playlists.dat";

    private static final Logger log = LoggerFactory.getLogger(PlaylistManager.class);

      private static class Holder {
        private static final PlaylistManager INSTANCE = new PlaylistManager();
    }

    public static PlaylistManager getInstance() {
        return Holder.INSTANCE;
    }

    private PlaylistManager() {
        semuaPlaylist = FXCollections.observableArrayList();
        loadDariFile();
    }


    public void buatPlaylist(String namaPlaylist) {
        // Check if a playlist with the same name already exists
        if (semuaPlaylist.stream().noneMatch(p -> p.getNama().equals(namaPlaylist))) {
            semuaPlaylist.add(new Playlist(namaPlaylist));
            simpanKeFile();
        }
    }

    public void hapusPlaylist(String namaPlaylist) {
        semuaPlaylist.removeIf(p -> p.getNama().equals(namaPlaylist));
        simpanKeFile();
    }

    public void tambahLaguKePlaylist(String namaPlaylist, String judul, String link) {
        for (Playlist p : semuaPlaylist) {
            if (p.getNama().equals(namaPlaylist)) {
                p.tambahLagu(judul, link);
                simpanKeFile();
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

    public void simpanKeFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(semuaPlaylist));
            log.info("Playlist berhasil disimpan!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadDariFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            List<Playlist> loadedPlaylists = (List<Playlist>) ois.readObject();
            semuaPlaylist.setAll(loadedPlaylists);
            log.info("Playlist berhasil dimuat dari file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
