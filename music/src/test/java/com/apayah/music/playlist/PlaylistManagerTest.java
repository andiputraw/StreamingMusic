package com.apayah.music.playlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaylistManagerTest {

    @Mock
    private PlaylistStorage mockStorage; // Mock the dependency

    private PlaylistManager playlistManager;

    @BeforeEach
    public void setUp() {
        // Get the singleton instance
        playlistManager = PlaylistManager.getInstance();
        
        // Inject the mock storage implementation
        playlistManager.setStorage(mockStorage);
        
        // Tell the mock what to do when load() is called.
        // This ensures the manager starts with a clean slate.
        when(mockStorage.load()).thenReturn(new ArrayList<>());
        
        // Manually trigger a load to clear any existing data from previous runs
        playlistManager.loadPlaylists();
    }

    @Test
    public void testBuatPlaylist() {
        // Action
        playlistManager.buatPlaylist("Chill Vibes");

        // Assertions
        assertNotNull(playlistManager.getPlaylist("Chill Vibes"));
        assertEquals(1, playlistManager.getSemuaPlaylist().size());
        
        // Verify that the manager tried to save the new state via the storage interface
        verify(mockStorage).save(anyList());
    }

    @Test
    public void testBuatPlaylist_Duplicate() {
        // Action
        playlistManager.buatPlaylist("Chill Vibes");
        playlistManager.buatPlaylist("Chill Vibes"); // Try to add it again

        // Assertions
        assertEquals(1, playlistManager.getSemuaPlaylist().size());
        
        // Verify save was only called for the first, successful creation
        verify(mockStorage, times(1)).save(anyList());
    }

    @Test
    public void testHapusPlaylist() {
        // Setup
        playlistManager.buatPlaylist("Chill Vibes");
        
        // Action
        playlistManager.hapusPlaylist("Chill Vibes");

        // Assertions
        assertNull(playlistManager.getPlaylist("Chill Vibes"));
        assertTrue(playlistManager.getSemuaPlaylist().isEmpty());
        
        // Verify save was called for creation and deletion
        verify(mockStorage, times(2)).save(anyList());
    }

    @Test
    public void testTambahLaguKePlaylist() {
        // Setup
        playlistManager.buatPlaylist("My Favorites");
        
        // Action
        playlistManager.tambahLaguKePlaylist("My Favorites", "Espresso", "link-to-espresso");
        
        // Assertions
        Playlist playlist = playlistManager.getPlaylist("My Favorites");
        assertNotNull(playlist);
        assertEquals(1, playlist.getDaftarLagu().size());
        assertEquals("Espresso|link-to-espresso", playlist.getDaftarLagu().get(0));

        // Verify save was called for creation and for adding the song
        verify(mockStorage, times(2)).save(anyList());
    }

    @Test
    public void testGetPlaylist_NotFound() {
        // Action & Assertion
        assertNull(playlistManager.getPlaylist("Non-Existent Playlist"));
        // Verify that no save operations happened
        verify(mockStorage, never()).save(anyList());
    }
}
