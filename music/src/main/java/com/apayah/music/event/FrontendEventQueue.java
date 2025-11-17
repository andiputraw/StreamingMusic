package com.apayah.music.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.apayah.music.event.frontend.contract.FrontendEvent;

public class FrontendEventQueue {
    public static final BlockingQueue<FrontendEvent> queue = new ArrayBlockingQueue<>(100);
}
