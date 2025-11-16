package com.apayah.music.backend;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import javax.sound.sampled.*;

public class SpeakerOutput {
    private final AudioPlayer player;
    private final SourceDataLine line;

    public SpeakerOutput(AudioPlayer player, AudioDataFormat format) throws LineUnavailableException {
        this.player = player;
        // AudioDataFormat format = player.
        
        // Convert Lavaplayer format to standard Java Sound format
        AudioFormat javaFormat = new AudioFormat(
            format.sampleRate, 
            16, // Sample size in bits (Lavaplayer standard)
            format.channelCount, 
            true, // Signed
            true  // Big Endian
        );

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, javaFormat);
        this.line = (SourceDataLine) AudioSystem.getLine(info);
        this.line.open(javaFormat);
        this.line.start();
        
        // Start a thread to constantly pump audio to speakers
        new Thread(this::pumpAudio).start();
    }

    private void pumpAudio() {
        try {
            while (true) {
                // Grab a frame of audio (20ms) from Lavaplayer
                AudioFrame frame = player.provide();
                
                if (frame != null) {
                    // Write it to the computer's speakers
                    line.write(frame.getData(), 0, frame.getDataLength());
                } else {
                    // No audio? Sleep briefly to save CPU
                    Thread.sleep(5);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // 1. Setup the Manager
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager); // Handles YouTube, SoundCloud, etc.
        playerManager.getConfiguration().setOutputFormat(StandardAudioDataFormats.COMMON_PCM_S16_BE);

        // 2. Create the Player
        AudioPlayer player = playerManager.createPlayer();
        new SpeakerOutput(player, StandardAudioDataFormats.COMMON_PCM_S16_BE);

        // 3. Connect Player to Speakers (The Bridge)

        // 4. Load and Play a URL (YouTube, Twitch, HTTP MP3, etc.)
        String url = "https://soundcloud.com/turkeybaconclub/sets/hollow-knight-silksong?utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing";

        playerManager.loadItem(url, new FunctionalResultHandler(
            track -> {
                System.out.println("Playing: " + track.getInfo().title);
                player.playTrack(track);
            },
            playlist -> {
                System.out.println("Playing playlist: " + playlist.getName());
                player.playTrack(playlist.getTracks().get(0)); // Play first song
            },
            () -> System.out.println("No match found"),
            e -> System.out.println("Error: " + e.getMessage())
        ));
        
        // Keep main thread alive
        Thread.sleep(999999); 
    }
}