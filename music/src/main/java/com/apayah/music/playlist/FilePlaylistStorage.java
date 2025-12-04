package com.apayah.music.playlist;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A file-based implementation of the PlaylistStorage interface.
 * It saves and loads playlists to a local .dat file using serialization.
 */
public class FilePlaylistStorage implements PlaylistStorage {

    private static final String FILE_PATH = "playlists.dat";
    private static final Logger log = LoggerFactory.getLogger(FilePlaylistStorage.class);

    @Override
    public void save(List<Playlist> playlists) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(playlists));
            log.info("Playlists successfully saved to {}", FILE_PATH);
        } catch (IOException e) {
            log.error("Failed to save playlists to file", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Playlist> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            log.warn("Playlist data file not found at {}, starting fresh.", FILE_PATH);
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Playlist> loadedPlaylists = (List<Playlist>) ois.readObject();
            log.info("Playlists successfully loaded from {}", FILE_PATH);
            return loadedPlaylists;
        } catch (IOException | ClassNotFoundException e) {
            log.error("Failed to load playlists from file, starting fresh.", e);
            // Return an empty list on error to prevent crashing the app
            return new ArrayList<>();
        }
    }
}
