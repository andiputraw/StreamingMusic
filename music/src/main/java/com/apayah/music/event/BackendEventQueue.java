package com.apayah.music.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.apayah.music.event.backend.contract.BackendEvent;

public class BackendEventQueue {
    public static final BlockingQueue<BackendEvent> queue = new ArrayBlockingQueue<>(100);
}