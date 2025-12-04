package com.apayah.music.playlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistTest {

    private Playlist playlist;

    @BeforeEach
    public void setUp() {
        playlist = new Playlist("My Test Playlist");
    }

    @Test
    public void testGetNama() {
        assertEquals("My Test Playlist", playlist.getNama());
    }

    @Test
    public void testTambahLagu() {
        playlist.tambahLagu("Test Song", "http://example.com/song.mp3");
        List<String> daftarLagu = playlist.getDaftarLagu();
        assertEquals(1, daftarLagu.size());
        assertEquals("Test Song|http://example.com/song.mp3", daftarLagu.get(0));
    }

    @Test
    public void testHapusLagu() {
        String songData = "Test Song|http://example.com/song.mp3";
        playlist.tambahLagu("Test Song", "http://example.com/song.mp3");
        playlist.hapusLagu(songData);
        assertTrue(playlist.getDaftarLagu().isEmpty());
    }

    @Test
    public void testGetDaftarLinkLagu() {
        playlist.tambahLagu("Song 1", "link1");
        playlist.tambahLagu("Song 2", "link2");
        List<String> links = playlist.getDaftarLinkLagu();
        assertEquals(2, links.size());
        assertTrue(links.contains("link1"));
        assertTrue(links.contains("link2"));
    }

    @Test
    public void testGetJudul() {
        String data = "A Cool Song|http://cool.com/song";
        assertEquals("A Cool Song", playlist.getJudul(data));
    }

    @Test
    public void testGetJudul_NoLink() {
        String data = "A Cool Song";
        assertEquals("A Cool Song", playlist.getJudul(data));
    }
    
    @Test
    public void testGetJudul_Empty() {
        String data = "";
        assertEquals("", playlist.getJudul(data));
    }

    @Test
    public void testGetLink() {
        String data = "A Cool Song|http://cool.com/song";
        assertEquals("http://cool.com/song", playlist.getLink(data));
    }

    @Test
    public void testGetLink_NoLink() {
        String data = "A Cool Song";
        assertEquals("", playlist.getLink(data));
    }

    @Test
    public void testGetLink_Empty() {
        String data = "";
        assertEquals("", playlist.getLink(data));
    }

    @Test
    public void testToString() {
        playlist.tambahLagu("Song 1", "link1");
        assertEquals("Playlist: My Test Playlist (1 lagu)", playlist.toString());
    }
}