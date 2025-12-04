package com.apayah.music.backend;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MusicQueueTest {

    private MusicQueue musicQueue;

    @Mock
    private AudioTrack mockTrack1;

    @Mock
    private AudioTrack mockTrack2;

    private Music music1;
    private Music music2;

    @BeforeEach
    void setUp() {
        musicQueue = new MusicQueue();
        music1 = new Music(mockTrack1);
        music2 = new Music(mockTrack2);
    }

    @Test
    void testAddQueue() {
        assertTrue(musicQueue.isEmpty());
        musicQueue.addQueue(music1);
        assertFalse(musicQueue.isEmpty());
        assertEquals(1, musicQueue.getMusics().size());
        assertEquals(music1, musicQueue.getMusics().get(0));
    }

    @Test
    void testJumpQueue() {
        musicQueue.addQueue(music1);
        musicQueue.addQueue(music2);
        
        Music current = musicQueue.jumpQueue(2);
        assertEquals(music2, current);
        assertEquals(1, musicQueue.getCurrentIndex());
    }

    @Test
    void testJumpQueueOutOfBounds() {
        musicQueue.addQueue(music1);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            musicQueue.jumpQueue(2);
        });
    }

    @Test
    void testGetCurrent() {
        musicQueue.addQueue(music1);
        musicQueue.addQueue(music2);
        musicQueue.jumpQueue(1);
        assertEquals(music1, musicQueue.getCurrent());
        musicQueue.jumpQueue(2);
        assertEquals(music2, musicQueue.getCurrent());
    }

    @Test
    void testIsEmpty() {
        assertTrue(musicQueue.isEmpty());
        musicQueue.addQueue(music1);
        assertFalse(musicQueue.isEmpty());
    }

    @Test
    void testGetCurrentIndex() {
        musicQueue.addQueue(music1);
        musicQueue.jumpQueue(1);
        assertEquals(0, musicQueue.getCurrentIndex());
    }

    @Test
    void testNext() {
        musicQueue.addQueue(music1);
        musicQueue.addQueue(music2);
        musicQueue.jumpQueue(1);
        
        Music nextMusic = musicQueue.next();
        assertEquals(music2, nextMusic);
        assertEquals(1, musicQueue.getCurrentIndex());
    }

    @Test
    void testNextThrowsExceptionAtEnd() {
        musicQueue.addQueue(music1);
        musicQueue.jumpQueue(1);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            musicQueue.next();
        });
    }

    @Test
    void testIsOnEnd() {
        musicQueue.addQueue(music1);
        musicQueue.jumpQueue(1);
        assertTrue(musicQueue.isOnEnd());
        musicQueue.addQueue(music2);
        assertFalse(musicQueue.isOnEnd());
    }

    @Test
    void testIsOnStart() {
        musicQueue.addQueue(music1);
        musicQueue.addQueue(music2);
        musicQueue.jumpQueue(1);
        assertTrue(musicQueue.isOnStart());
        musicQueue.next();
        assertFalse(musicQueue.isOnStart());
    }
    
    @Test
    void testClearQueue() {
        musicQueue.addQueue(music1);
        musicQueue.addQueue(music2);
        assertFalse(musicQueue.isEmpty());
        musicQueue.clearQueue();
        assertTrue(musicQueue.isEmpty());
        assertEquals(0, musicQueue.getMusics().size());
    }
}