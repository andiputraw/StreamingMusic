package com.apayah.music.backend;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicManagerTest {

    @Mock
    private AudioPlayer mockPlayer;

    @Mock
    private AudioTrack mockTrack;

    private MusicManager musicManager;

    @BeforeEach
    void setUp() {
        musicManager = new MusicManager(mockPlayer);
    }

    @Test
    void testPlay() {
        Music music = new Music(mockTrack);
        when(mockTrack.makeClone()).thenReturn(mockTrack);

        musicManager.play(music);

        verify(mockPlayer).playTrack(mockTrack);
    }

    @Test
    void testSeek() {
        when(mockPlayer.getPlayingTrack()).thenReturn(mockTrack);
        long seekPosition = 10000L;

        musicManager.seek(seekPosition);

        verify(mockTrack).setPosition(seekPosition);
    }

    @Test
    void testResumeWhenPaused() {
        when(mockPlayer.isPaused()).thenReturn(true);

        musicManager.resume();

        verify(mockPlayer).setPaused(false);
    }

    @Test
    void testResumeWhenNotPaused() {
        when(mockPlayer.isPaused()).thenReturn(false);

        musicManager.resume();

        verify(mockPlayer, never()).setPaused(false);
    }

    @Test
    void testPauseWhenNotPaused() {
        when(mockPlayer.isPaused()).thenReturn(false);

        musicManager.pause();

        verify(mockPlayer).setPaused(true);
    }

    @Test
    void testPauseWhenPaused() {
        when(mockPlayer.isPaused()).thenReturn(true);

        musicManager.pause();

        verify(mockPlayer, never()).setPaused(true);
    }

    @Test
    void testIsPlayingWhenTrackIsNull() {
        when(mockPlayer.getPlayingTrack()).thenReturn(null);

        assertFalse(musicManager.isPlaying());
    }

    @Test
    void testIsPlayingWhenTrackIsPlaying() {
        when(mockPlayer.getPlayingTrack()).thenReturn(mockTrack);
        when(mockTrack.getPosition()).thenReturn(5000L);
        when(mockTrack.getDuration()).thenReturn(10000L);

        assertTrue(musicManager.isPlaying());
    }

    @Test
    void testIsPlayingWhenTrackIsFinished() {
        when(mockPlayer.getPlayingTrack()).thenReturn(mockTrack);
        when(mockTrack.getPosition()).thenReturn(10000L);
        when(mockTrack.getDuration()).thenReturn(10000L);

        assertTrue(musicManager.isPlaying());
    }

    @Test
    void testIsPlayingWhenTrackPositionExceedsDuration() {
        when(mockPlayer.getPlayingTrack()).thenReturn(mockTrack);
        when(mockTrack.getPosition()).thenReturn(10001L);
        when(mockTrack.getDuration()).thenReturn(10000L);

        assertFalse(musicManager.isPlaying());
    }

    @Test
    void testGetMusicTimePlayed() {
        when(mockPlayer.getPlayingTrack()).thenReturn(mockTrack);
        when(mockTrack.getPosition()).thenReturn(12345L);

        assertEquals(12345L, musicManager.getMusicTimePlayed());
    }

    @Test
    void testGetCurrentlyPlaying() {
        when(mockPlayer.getPlayingTrack()).thenReturn(mockTrack);

        Music currentlyPlaying = musicManager.getCurrentlyPlaying();

        assertEquals(mockTrack, currentlyPlaying.getTrack());
    }
}