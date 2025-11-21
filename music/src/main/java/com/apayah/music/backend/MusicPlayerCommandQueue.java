package com.apayah.music.backend;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.apayah.music.command.backend.AddMusicEvent;
import com.apayah.music.command.backend.JumpMusicEvent;
import com.apayah.music.command.backend.PauseMusicEvent;
import com.apayah.music.command.backend.ResumeMusicEvent;
import com.apayah.music.command.backend.SeekMusicEvent;
import com.apayah.music.command.backend.contract.Command;

public class MusicPlayerCommandQueue {
    private final BlockingQueue<Command> queue;
    private final MusicManager manager;
    private final MusicQueue musicQueue;

    public MusicPlayerCommandQueue(MusicManager manager, MusicQueue musicQueue) {
        this.queue = new ArrayBlockingQueue<Command>(1024);
        this.manager = manager;
        this.musicQueue = musicQueue;
        startMessageLoop();
    }

    public void enqueue(Command cmd){
        this.queue.add(cmd);
    }



    public void startMessageLoop() {
        new Thread(() -> {
            while (true) {
                try {
                    Command event = queue.take();
                    if(event instanceof AddMusicEvent) {
                        manager.play(((AddMusicEvent)event).getMusic());
                    }
                    if(event instanceof ResumeMusicEvent) {
                        manager.resume();
                    }
                    if(event instanceof PauseMusicEvent) {
                        manager.pause();
                    }
                    if(event instanceof JumpMusicEvent) {
                        musicQueue.jumpQueue(((JumpMusicEvent)event).getIndex());
                    }
                    if(event instanceof SeekMusicEvent) {
                        manager.seek(((SeekMusicEvent)event).getMilis());
                    }

                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }
}
