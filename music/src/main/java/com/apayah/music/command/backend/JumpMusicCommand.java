package com.apayah.music.command.backend;

import com.apayah.music.command.backend.contract.Command;

public class JumpMusicCommand implements Command {
    private int index;

    public int getIndex() {
        return index;
    }

    public JumpMusicCommand(int idx) {
        this.index = idx;
    }
}
