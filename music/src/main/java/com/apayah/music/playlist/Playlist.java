package com.apayah.music.playlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nama;
    private List<String> daftarLagu;

    public Playlist(String nama) {
        this.nama = nama;
        this.daftarLagu = new ArrayList<>();
    }

    public String getNama() {
        return nama;
    }

    public void tambahLagu(String judul, String link) {
        daftarLagu.add(judul + "|" + link);
    }

    public void hapusLagu(String judulLagu) {
        daftarLagu.remove(judulLagu);
    }

    public List<String> getDaftarLagu() {
        return daftarLagu;
    }
    public List<String> getDaftarLinkLagu() {
        return daftarLagu.stream().map((v) -> this.getLink(v)).toList();
    }

    public String getJudul(String data){
        String[] parts = data.split("\\|");
        return parts.length > 0 ? parts[0] : "";

    }

    public String getLink(String data){
        String[] parts = data.split("\\|");
        return parts.length > 1 ? parts [1] : "";
    }

    @Override
    public String toString() {
        return "Playlist: " + nama + " (" + daftarLagu.size() + " lagu)";
    }
}
