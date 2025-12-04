package com.apayah.music.playlist;

import java.util.List;

/**
 * An interface that defines the contract for loading and saving playlists.
 * This abstraction allows us to swap out the storage mechanism (e.g., from file to database)
 * and makes the PlaylistManager easier to test.
 */
public interface PlaylistStorage {
    /**
     * Saves a list of playlists to a persistent storage.
     * @param playlists The list of playlists to save.
     */
    void save(List<Playlist> playlists);

    /**
     * Loads a list of playlists from a persistent storage.
     * @return A list of loaded playlists, or an empty list if none are found.
     */
    List<Playlist> load();
}
