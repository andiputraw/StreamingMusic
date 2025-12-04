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
        this.queue = new ArrayBlockingQueue<>(1024);
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
                   handleEvent(event);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private void handleEvent(Command event) {

    if (event instanceof AddMusicCommand add) {
        musicQueue.queue(add.getMusic());
        return;
    }

    if (event instanceof ResumeMusicCommand) {
        manager.resume();
        return;
    }

    if (event instanceof PauseMusicCommand) {
        manager.pause();
        return;
    }

    if (event instanceof JumpMusicCommand jump) {
        musicQueue.jump(jump.getIndex());
        return;
    }

    if (event instanceof SeekMusicCommand seek) {
        manager.seek(seek.getMilis());
        return;
    }

    if (event instanceof ClearQueueCommand) {
        musicQueue.clear();
    }
}
}