package com.apayah.music.playlist;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistManager {

    private Map<String, Playlist> semuaPlaylist;
    private final String filePath = "playlists.dat";

    public PlaylistManager() {
        semuaPlaylist = new HashMap<>();
        loadDariFile();
    }

    public void buatPlaylist(String namaPlaylist) {
        if (!semuaPlaylist.containsKey(namaPlaylist)) {
            semuaPlaylist.put(namaPlaylist, new Playlist(namaPlaylist));
            simpanKeFile();
        }
    }

    public void hapusPlaylist(String namaPlaylist) {
        semuaPlaylist.remove(namaPlaylist);
        simpanKeFile();
    }

    public void tambahLaguKePlaylist(String namaPlaylist, String judul, String link) {
        Playlist p = semuaPlaylist.get(namaPlaylist);
        if (p != null) {
            p.tambahLagu(judul, link);
            simpanKeFile();
        }
    }

    // Lihat semua playlist
    public Map<String, Playlist> getSemuaPlaylist() {
        return semuaPlaylist;
    }

    // Ambil playlist
    public Playlist getPlaylist(String namaPlaylist) {
        return semuaPlaylist.get(namaPlaylist);
    }

    // =============== FILE HANDLING ===============

    public void simpanKeFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(semuaPlaylist);
            System.out.println("Playlist berhasil disimpan!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadDariFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Belum ada file playlist, membuat baru...");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            semuaPlaylist = (Map<String, Playlist>) ois.readObject();
            System.out.println("Playlist berhasil dimuat dari file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        // PlaylistManager manager = new PlaylistManager();
        // manager.buatPlaylist("AndiBesar");
        // manager.buatPlaylist("NazwaKecil");
        // manager.tambahLaguKePlaylist("AndiBesar", "Small Fragile Hearts");
        // manager.tambahLaguKePlaylist("AndiBesar", "Baby");
        // manager.tambahLaguKePlaylist("NazwaKecil", "Small Fragile Hearts");
        // manager.simpanKeFile();

        // PlaylistManager manager = new PlaylistManager();
        // manager.buatPlaylist("AndiBesar");
        // manager.tambahLaguKePlaylist("AndiBesar", "Small Fragile Hearts", "https://soundcloud.com/victorlundbergofficial/small-fragile-hearts");

        PlaylistManager manager = new PlaylistManager();
        Playlist percobaan = manager.getPlaylist("AndiBesar");

        System.out.println("=== PLAYLIST " + percobaan.getNama() + " ===");

        for (String item : percobaan.getDaftarLagu()) {
            System.out.println("- Judul: " + percobaan.getJudul(item));
            System.out.println("  Link : " + percobaan.getLink(item));
        }

    }
}
