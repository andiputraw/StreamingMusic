package com.apayah.music.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.apayah.music.command.backend.AddMusicCommand;
import com.apayah.music.command.backend.JumpMusicCommand;
import com.apayah.music.command.backend.PauseMusicCommand;
import com.apayah.music.command.backend.ResumeMusicCommand;
import com.apayah.music.command.backend.SeekMusicCommand;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

public class MusicPlayerFacade {
    private final AudioPlayerManager manager;
    private final AudioPlayer player;
    private final MusicManager musicManager;
    private final Speaker speaker;
    private final MusicQueue musicQueue;
    private final MusicQueueScheduler scheduler;
    private final AudioDataFormat audioFormat = StandardAudioDataFormats.COMMON_PCM_S16_BE;
    private final MusicPlayerCommandQueue commandQueue;
    private final MusicSearcher searcher;

    public MusicPlayerFacade() throws Exception {
        this.manager = new DefaultAudioPlayerManager();
        
        AudioSourceManagers.registerRemoteSources(manager);
        manager.getConfiguration().setOutputFormat(audioFormat);
        this.musicQueue = new MusicQueue();
        
        this.player = manager.createPlayer();
        this.player.setPaused(true);

        this.musicManager = new MusicManager(player);
        this.scheduler = new MusicQueueScheduler(this.player, this.musicQueue);
        this.player.addListener(scheduler);
        this.speaker = new Speaker(player, audioFormat);
        this.searcher = new MusicSearcher(this.manager);
        this.commandQueue = new MusicPlayerCommandQueue(musicManager, this.scheduler);
    }

    void play(Music music) {
        //
    }

    void pause() {
        commandQueue.enqueue(new PauseMusicCommand());
    }

    void resume() {
        commandQueue.enqueue(new ResumeMusicCommand());
    }

    void seek(long milis) {
        commandQueue.enqueue(new SeekMusicCommand(milis));
    }

    void jump(int index) {
        commandQueue.enqueue(new JumpMusicCommand(index));
    }

    void addToQueue(Music music) {
        commandQueue.enqueue(new AddMusicCommand(music));
    }


    void addToQueue(List<Music> musics) {
        for (Music music : musics) {
            commandQueue.enqueue(new AddMusicCommand(music));
        }
    }

    CompletableFuture<List<Music>> search(String query) {
        return searcher.searchMusic(query);
    }

    List<Music> playingQueue() {
        return this.musicQueue.getMusics();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Start");
        var facade = new MusicPlayerFacade();
        facade.search("https://soundcloud.com/turkeybaconclub/sets/hollow-knight-silksong?utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing").
        thenAccept(musics -> {
            System.out.println("Loaded");
            facade.addToQueue(musics);
            facade.resume();
        });
        System.out.println("ZZZ.....");

        Thread.sleep(99999999);    
    }
}