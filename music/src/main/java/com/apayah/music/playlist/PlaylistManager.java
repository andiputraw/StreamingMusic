package com.apayah.music.playlist;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlaylistManager {

    private static PlaylistManager instance;
    private ObservableList<Playlist> semuaPlaylist;
    private final String filePath = "playlists.dat";

    private PlaylistManager() {
        semuaPlaylist = FXCollections.observableArrayList();
        loadDariFile();
    }

    public static synchronized PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(semuaPlaylist));
            System.out.println("Playlist berhasil disimpan!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadDariFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            List<Playlist> loadedPlaylists = (List<Playlist>) ois.readObject();
            semuaPlaylist.setAll(loadedPlaylists);
            System.out.println("Playlist berhasil dimuat dari file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
