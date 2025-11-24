/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apayah.music.frontend;

/**
 *
 * @author PLN
 */
import javafx.scene.image.Image;

public class SongData {
    private int number;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private Image image;
    private com.apayah.music.backend.Music music; // Reference to original Music object

    // Constructor
    public SongData(int number, String title, String artist, String album, String duration, Image image) {
        this.number = number;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.image = image;
        this.music = null;
    }

    // Constructor with Music object
    public SongData(int number, String title, String artist, String album, String duration, Image image,
            com.apayah.music.backend.Music music) {
        this.number = number;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.image = image;
        this.music = music;
    }

    // Getter methods for TableView PropertyValueFactory
    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title + "\n" + artist; // Combined title and artist for display
    }

    public String getSongTitle() {
        return title; // Keep for backward compatibility
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDuration() {
        return duration;
    }

    public Image getImage() {
        return image;
    }

    // Setter methods
    public void setNumber(int number) {
        this.number = number;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public com.apayah.music.backend.Music getMusic() {
        return music;
    }

    public void setMusic(com.apayah.music.backend.Music music) {
        this.music = music;
    }
}
