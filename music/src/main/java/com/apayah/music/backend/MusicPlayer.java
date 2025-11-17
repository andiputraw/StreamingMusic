package com.apayah.music.backend;

import java.util.List;

import javax.sound.sampled.LineUnavailableException;

import com.apayah.music.event.BackendEventQueue;
import com.apayah.music.event.backend.PlayMusicEvent;
import com.apayah.music.event.backend.contract.BackendEvent;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MusicPlayer {
    private AudioPlayerManager manager;
    private AudioPlayer player;
    private Speaker speaker;
    private final MusicQueueScheduler scheduler;
    private final AudioDataFormat audioFormat = StandardAudioDataFormats.COMMON_PCM_S16_BE;

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
                    BackendEvent event = BackendEventQueue.queue.take(); // Waits here for a message
                    if(event instanceof PlayMusicEvent) {
                        loadMusic(((PlayMusicEvent)event).getUrl());
                    }

                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public void resume() {
        if(this.player.isPaused()) {
            this.player.setPaused(false);
        }
    }

    public void stop() {
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
        return this.getCurrentlyPlaying();
    }

    public void addQueue() {
    }

    public void clearQueue() {
    }

    public void jump(int index) {
    }

    public List<Music> list() {
        return null;
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
        // player.loadMusic(musicUrl);
        player.resume();
        BackendEventQueue.queue.put(new PlayMusicEvent(musicUrl));

        Thread.sleep(99999999);    
    }
}
