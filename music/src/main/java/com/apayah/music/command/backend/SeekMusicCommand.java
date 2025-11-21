package com.apayah.music.command.backend;

import com.apayah.music.command.backend.contract.Command;

public class SeekMusicCommand implements Command {
    private long milis;

    public long getMilis() {
        return milis;
    }

    public SeekMusicCommand(long milis) {
        this.milis = milis;
    }

}
