package com.apayah.music.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.LineUnavailableException;

import com.apayah.music.event.BackendEventQueue;
import com.apayah.music.event.backend.AddMusicEvent;
import com.apayah.music.event.backend.JumpMusicEvent;
import com.apayah.music.event.backend.PauseMusicEvent;
import com.apayah.music.event.backend.ResumeMusicEvent;
import com.apayah.music.event.backend.SeekMusicEvent;
import com.apayah.music.event.backend.contract.BackendEvent;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class MusicPlayer {
    private AudioPlayerManager manager;
    private AudioPlayer player;
    private Speaker speaker;
    private final MusicQueueScheduler scheduler;
    private final AudioDataFormat audioFormat = StandardAudioDataFormats.COMMON_PCM_S16_BE;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public MusicPlayer(AudioPlayerManager manager) throws LineUnavailableException {
        this.manager = manager;
        manager.getConfiguration().setOutputFormat(audioFormat);
        this.player = manager.createPlayer();
        this.scheduler = new MusicQueueScheduler(player);
        this.player.addListener(scheduler);
        this.player.setPaused(true);
        this.speaker = new Speaker(player, audioFormat);
        startMessageLoop();
    }

      public void startMessageLoop() {
        new Thread(() -> {
            while (true) {
                try {
                    // 60 fps
                    Thread.sleep(16);
                    BackendEvent event = BackendEventQueue.queue.take();
                    if(event instanceof AddMusicEvent) {
                        loadMusic(((AddMusicEvent)event).getUrl());
                    }
                    if(event instanceof ResumeMusicEvent) {
                        resume();
                    }
                    if(event instanceof PauseMusicEvent) {
                        pause();
                    }
                    if(event instanceof JumpMusicEvent) {
                        jump(((JumpMusicEvent)event).getIndex());
                    }
                    if(event instanceof SeekMusicEvent) {
                        seek(((SeekMusicEvent)event).getMilis());
                    }

                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public CompletableFuture<List<Music>> searchMusic(String query) {
    
    return CompletableFuture.supplyAsync(() -> {
        
        CompletableFuture<List<Music>> promise = new CompletableFuture<>();

        manager.loadItem(query, new FunctionalResultHandler(
            track -> {
                System.out.print("Track get");
                promise.complete(List.of(new Music(track)));
            },
            playlist -> {
                List<Music> loadedTitles = new ArrayList<>();
                System.out.print("Playlists get");
                for (AudioTrack track : playlist.getTracks()) {
                    loadedTitles.add(new Music(track));
                }
                // We loaded a playlist, return all titles
                promise.complete(loadedTitles);
            },
            () -> {
                System.out.print("No matchs");
                promise.complete(List.of());
            }, // No matches
            e -> promise.completeExceptionally(e) // Error
            ));

        return promise.join();

    }, executor);
}

    public void jump(int index) {
        scheduler.jump(index);
    }

    public void seek(long milis) {
        player.getPlayingTrack().setPosition(milis);
    }

    public void resume() {
        if(this.player.isPaused()) {
            this.player.setPaused(false);
        }
    }

    public void pause() {
        if(!this.player.isPaused()) {
            this.player.setPaused(true);
        }
    }

    public boolean isPlaying() {
        if(this.player.getPlayingTrack() == null ) {
            return false;
        }
        return getMusicTimePlayed() <= getCurrentlyPlaying().getTrack().getDuration();
    }

    public long getMusicTimePlayed() {
        return this.player.getPlayingTrack().getPosition();
    }

    public Music getCurrentlyPlaying() {
        return new Music(this.player.getPlayingTrack());
    }

    public void loadMusic(String url) {
        this.manager.loadItem(url, new FunctionalResultHandler(
                track -> {
                    Music music = new Music(track);
                    scheduler.queue(music);
                    System.out.println("Loaded: " + track.getInfo().title);
                },
                playlist -> {
                    List<AudioTrack> tracks = playlist.getTracks();
                    for (AudioTrack audioTrack : tracks) {
                        scheduler.queue(new Music(audioTrack));
                        System.out.println("Loaded: " + audioTrack.getInfo().title);
                    }
                },
                () -> {
                    System.out.println("No matches found");
                },
                e -> System.out.println("Error: " + e.getMessage())));
    }

    public static void main(String[] args) throws Exception {
        var manager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(manager); 
        var player = new MusicPlayer(manager);
        var musicUrl = "https://soundcloud.com/turkeybaconclub/sets/hollow-knight-silksong?utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing";
        
        player.searchMusic("scsearch:Phil Collins").thenAccept(musics -> {
            for (Music music : musics) {
                AudioTrackInfo info =   music.getTrack().getInfo();
                System.out.println("Title " + info.title  + " Author: " + info.author + " url:  " + info.uri);
            }
        });

        

        player.resume();
        BackendEventQueue.queue.put(new AddMusicEvent(musicUrl));
        System.out.println("Music started");
        Thread.sleep(10000);
        
        System.out.println("Jump");
        BackendEventQueue.queue.put(new JumpMusicEvent(3));

        Thread.sleep(10000);
        System.out.println("Seek");
        
        BackendEventQueue.queue.put(new SeekMusicEvent(0));

        Thread.sleep(10000);
        System.out.println("Pause");
        
        BackendEventQueue.queue.put(new PauseMusicEvent());

        Thread.sleep(10000);
        System.out.println("Resume");
        
        BackendEventQueue.queue.put(new ResumeMusicEvent());


        Thread.sleep(99999999);    
    }
}
