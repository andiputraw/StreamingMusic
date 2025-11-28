package com.apayah.music.backend;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;


import javax.sound.sampled.*;

public class Speaker {
    private final AudioPlayer player;
    private final SourceDataLine line;

    public Speaker(AudioPlayer player, AudioDataFormat format) throws LineUnavailableException {
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
        
        // Open with a larger buffer (e.g. 1 second worth of audio) to prevent underflow
        int bufferSize = format.sampleRate * format.channelCount * 2; 
        this.line.open(javaFormat, bufferSize);
        this.line.start();
        
        // Start a thread to constantly pump audio to speakers
        Thread t = new Thread(this::pumpAudio);
        t.setDaemon(true);
        t.start();
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
            System.err.println("Speaker thread died: " + e.getMessage());
        }
    }
}