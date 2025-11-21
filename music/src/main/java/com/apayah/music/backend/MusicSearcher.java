package com.apayah.music.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MusicSearcher {
    private AudioPlayerManager manager;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public MusicSearcher(AudioPlayerManager manager) {
        this.manager = manager;
    }

    private CompletableFuture<List<Music>> search(String query) {

        return CompletableFuture.supplyAsync(() -> {

            CompletableFuture<List<Music>> promise = new CompletableFuture<>();

            manager.loadItem(query, new FunctionalResultHandler(
                    track -> {
                        promise.complete(List.of(new Music(track)));
                    },
                    playlist -> {
                        List<Music> loadedTitles = new ArrayList<>();
                        for (AudioTrack track : playlist.getTracks()) {
                            loadedTitles.add(new Music(track));
                        }
                        // We loaded a playlist, return all titles
                        promise.complete(loadedTitles);
                    },
                    () -> {
                        promise.complete(List.of());
                    }, // No matches
                    e -> promise.completeExceptionally(e) // Error
            ));

            return promise.join();

        }, executor);
    }

    public CompletableFuture<List<Music>> searchMusic(String query) {
        if (query.startsWith("http")) {
            return search(query);
        } else {

            return search("scsearch:" + query);
        }
    }

    public CompletableFuture<List<Music>> random() {
        return search("I'm feeling lucky");
    }

}
