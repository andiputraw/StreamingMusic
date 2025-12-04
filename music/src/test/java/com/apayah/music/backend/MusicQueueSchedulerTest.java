package com.apayah.music.backend;

import com.apayah.music.frontend.AppState;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicQueueSchedulerTest {

    @Mock
    private AudioPlayer mockPlayer;

    @Mock
    private MusicQueue mockQueue;

    @Mock
    private AudioTrack mockTrack;

    @Mock
    private AppState mockAppState;

    @InjectMocks
    private MusicQueueScheduler scheduler;

    private Music music;

    @BeforeEach
    void setUp() {
        music = new Music(mockTrack);
    }

    @Test
    void testQueueFirstSong() {
        when(mockQueue.getMusics()).thenReturn(Collections.singletonList(music));
        when(mockPlayer.getPlayingTrack()).thenReturn(null);
        when(mockQueue.isOnEnd()).thenReturn(false);
        when(mockQueue.next()).thenReturn(music);
        when(mockTrack.makeClone()).thenReturn(mockTrack);

        try (MockedStatic<AppState> mockedStatic = mockStatic(AppState.class)) {
            mockedStatic.when(AppState::getInstance).thenReturn(mockAppState);
            scheduler.queue(music);
            verify(mockQueue).addQueue(music);
            verify(mockPlayer).startTrack(mockTrack, false);
            verify(mockAppState).notifyMusicChanged(music);
        }
    }

    @Test
    void testQueueAnotherSong() {
        when(mockPlayer.getPlayingTrack()).thenReturn(mockTrack);
        scheduler.queue(music);
        verify(mockQueue).addQueue(music);
        verify(mockPlayer, never()).startTrack(any(AudioTrack.class), anyBoolean());
    }

    @Test
    void testNextTrack() {
        when(mockQueue.isOnEnd()).thenReturn(false);
        when(mockQueue.next()).thenReturn(music);
        when(mockTrack.makeClone()).thenReturn(mockTrack);

        try (MockedStatic<AppState> mockedStatic = mockStatic(AppState.class)) {
            mockedStatic.when(AppState::getInstance).thenReturn(mockAppState);
            scheduler.nextTrack();
            verify(mockPlayer).startTrack(mockTrack, false);
            verify(mockAppState).notifyMusicChanged(music);
        }
    }

    @Test
    void testNextTrackAtEndOfQueue() {
        when(mockQueue.isOnEnd()).thenReturn(true);
        scheduler.nextTrack();
        verify(mockPlayer, never()).startTrack(any(AudioTrack.class), anyBoolean());
    }

    @Test
    void testOnTrackEnd() {
        when(mockQueue.isOnEnd()).thenReturn(false);
        when(mockQueue.next()).thenReturn(music);
        when(mockTrack.makeClone()).thenReturn(mockTrack);

        try (MockedStatic<AppState> mockedStatic = mockStatic(AppState.class)) {
            mockedStatic.when(AppState::getInstance).thenReturn(mockAppState);
            scheduler.onTrackEnd(mockPlayer, mockTrack, AudioTrackEndReason.FINISHED);
            verify(mockPlayer).startTrack(mockTrack, false);
        }
    }

    @Test
    void testJump() {
        when(mockQueue.jumpQueue(1)).thenReturn(music);
        when(mockTrack.makeClone()).thenReturn(mockTrack);

        try (MockedStatic<AppState> mockedStatic = mockStatic(AppState.class)) {
            mockedStatic.when(AppState::getInstance).thenReturn(mockAppState);
            assertTrue(scheduler.jump(0));
            verify(mockPlayer).startTrack(mockTrack, false);
            verify(mockAppState).notifyMusicChanged(music);
        }
    }

    @Test
    void testJumpInvalidIndex() {
        when(mockQueue.jumpQueue(anyInt())).thenThrow(new IndexOutOfBoundsException());
        assertFalse(scheduler.jump(0));
        verify(mockPlayer, never()).startTrack(any(AudioTrack.class), anyBoolean());
    }

    @Test
    void testGetQueue() {
        List<Music> musicList = Collections.singletonList(music);
        when(mockQueue.getMusics()).thenReturn(musicList);
        assertEquals(musicList, scheduler.getQueue());
    }

    @Test
    void testGetCurrentlyPlaying() {
        when(mockPlayer.getPlayingTrack()).thenReturn(mockTrack);
        when(mockQueue.isEmpty()).thenReturn(false);
        when(mockQueue.getCurrent()).thenReturn(music);
        assertEquals(music, scheduler.getCurrentlyPlaying());
    }

    @Test
    void testGetCurrentlyPlayingWhenNull() {
        when(mockPlayer.getPlayingTrack()).thenReturn(null);
        assertNull(scheduler.getCurrentlyPlaying());
    }

    @Test
    void testClearQueue() {
        scheduler.clearQueue();
        verify(mockPlayer).stopTrack();
    }
}