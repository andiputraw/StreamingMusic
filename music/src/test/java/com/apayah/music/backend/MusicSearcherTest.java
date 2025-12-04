package com.apayah.music.backend;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicSearcherTest {

    @Mock
    private AudioPlayerManager mockManager;

    @InjectMocks
    private MusicSearcher musicSearcher;

    @Test
    void testSearchMusicWithQuery() throws ExecutionException, InterruptedException {
        String query = "test query";
        AudioTrack mockTrack = mock(AudioTrack.class);

        doAnswer(invocation -> {
            FunctionalResultHandler handler = invocation.getArgument(1);
            handler.trackLoaded(mockTrack);
            return null;
        }).when(mockManager).loadItem(eq("scsearch:" + query), any(FunctionalResultHandler.class));

        CompletableFuture<List<Music>> future = musicSearcher.searchMusic(query);

        List<Music> result = future.get();
        assertEquals(1, result.size());
        assertEquals(mockTrack, result.get(0).getTrack());
    }

    @Test
    void testSearchMusicWithUrl() throws ExecutionException, InterruptedException {
        String url = "http://example.com/music.mp3";
        AudioTrack mockTrack = mock(AudioTrack.class);

        doAnswer(invocation -> {
            FunctionalResultHandler handler = invocation.getArgument(1);
            handler.trackLoaded(mockTrack);
            return null;
        }).when(mockManager).loadItem(eq(url), any(FunctionalResultHandler.class));

        CompletableFuture<List<Music>> future = musicSearcher.searchMusic(url);

        List<Music> result = future.get();
        assertEquals(1, result.size());
        assertEquals(mockTrack, result.get(0).getTrack());
    }

    @Test
    void testSearchMusicPlaylistLoaded() throws ExecutionException, InterruptedException {
        String query = "test query";
        AudioPlaylist mockPlaylist = mock(AudioPlaylist.class);
        AudioTrack mockTrack1 = mock(AudioTrack.class);
        AudioTrack mockTrack2 = mock(AudioTrack.class);
        when(mockPlaylist.getTracks()).thenReturn(List.of(mockTrack1, mockTrack2));

        doAnswer(invocation -> {
            FunctionalResultHandler handler = invocation.getArgument(1);
            handler.playlistLoaded(mockPlaylist);
            return null;
        }).when(mockManager).loadItem(eq("scsearch:" + query), any(FunctionalResultHandler.class));

        CompletableFuture<List<Music>> future = musicSearcher.searchMusic(query);

        List<Music> result = future.get();
        assertEquals(2, result.size());
        assertEquals(mockTrack1, result.get(0).getTrack());
        assertEquals(mockTrack2, result.get(1).getTrack());
    }

    @Test
    void testSearchMusicNoMatches() throws ExecutionException, InterruptedException {
        String query = "test query";

        doAnswer(invocation -> {
            FunctionalResultHandler handler = invocation.getArgument(1);
            handler.noMatches();
            return null;
        }).when(mockManager).loadItem(eq("scsearch:" + query), any(FunctionalResultHandler.class));

        CompletableFuture<List<Music>> future = musicSearcher.searchMusic(query);

        List<Music> result = future.get();
        assertEquals(0, result.size());
    }

    @Test
    void testSearchMusicLoadFailed() {
        String query = "test query";
        FriendlyException exception = new FriendlyException("load failed", FriendlyException.Severity.COMMON, null);

        doAnswer(invocation -> {
            FunctionalResultHandler handler = invocation.getArgument(1);
            handler.loadFailed(exception);
            return null;
        }).when(mockManager).loadItem(eq("scsearch:" + query), any(FunctionalResultHandler.class));

        CompletableFuture<List<Music>> future = musicSearcher.searchMusic(query);

        ExecutionException thrown = assertThrows(ExecutionException.class, future::get);
        assertEquals(exception, thrown.getCause());
    }
}