package com.apayah.music.backend;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import javax.sound.sampled.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Speaker {
    private final AudioPlayer player;
    private final SourceDataLine line;

    private static final Logger log = LoggerFactory.getLogger(Speaker.class);

    private volatile boolean running = true; // <-- Add condition flag
    private Thread pumpThread;

    public Speaker(AudioPlayer player, AudioDataFormat format) throws LineUnavailableException {
        this.player = player;

        AudioFormat javaFormat = new AudioFormat(
                format.sampleRate,
                16,
                format.channelCount,
                true,
                true);

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, javaFormat);
        this.line = (SourceDataLine) AudioSystem.getLine(info);

        int bufferSize = format.sampleRate * format.channelCount * 2;
        this.line.open(javaFormat, bufferSize);
        this.line.start();

        pumpThread = new Thread(this::pumpAudio, "Speaker-AudioPump");
        pumpThread.setDaemon(true);
        pumpThread.start();
    }

    private void pumpAudio() {
        try {
            while (running) { // <-- Loop ends when running = false
                AudioFrame frame = player.provide();

                if (frame != null) {
                    line.write(frame.getData(), 0, frame.getDataLength());
                } else {
                    Thread.sleep(5);
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log.error("Speaker thread error: {}", e.getMessage(), e);
        } finally {
            log.info("Shutting down speaker output.");
            line.stop();
            line.close();
        }
    }

    /**
     * Gracefully stops the speaker thread.
     */
    public void stop() {
        running = false; // Signal thread to stop
        if (pumpThread != null) {
            pumpThread.interrupt(); // Wake it if it's sleeping
        }
    }
}
