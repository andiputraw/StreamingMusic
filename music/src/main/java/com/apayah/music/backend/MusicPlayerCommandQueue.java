package com.apayah.music.backend;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.apayah.music.command.backend.AddMusicCommand;
import com.apayah.music.command.backend.ClearQueueCommand;
import com.apayah.music.command.backend.JumpMusicCommand;
import com.apayah.music.command.backend.PauseMusicCommand;
import com.apayah.music.command.backend.ResumeMusicCommand;
import com.apayah.music.command.backend.SeekMusicCommand;
import com.apayah.music.command.backend.contract.Command;

public class MusicPlayerCommandQueue {
    private final BlockingQueue<Command> queue;
    private final MusicManager manager;
    private final MusicQueueScheduler musicQueue;

    public MusicPlayerCommandQueue(MusicManager manager, MusicQueueScheduler musicQueue) {
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
                    if(event instanceof AddMusicCommand) {
                        musicQueue.queue(((AddMusicCommand)event).getMusic());
                    }
                    if(event instanceof ResumeMusicCommand) {
                        manager.resume();
                    }
                    if(event instanceof PauseMusicCommand) {
                        manager.pause();
                    }
                    if(event instanceof JumpMusicCommand) {
                        musicQueue.jump(((JumpMusicCommand)event).getIndex());
                    }
                    if(event instanceof SeekMusicCommand) {
                        manager.seek(((SeekMusicCommand)event).getMilis());
                    }
                    if(event instanceof ClearQueueCommand) {
                        musicQueue.clear();
                    }

                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }
}