package com.apayah.music.command.backend;

import com.apayah.music.backend.Music;
import com.apayah.music.command.backend.contract.Command;

public class AddMusicCommand implements Command {
    private Music music;
    public Music getMusic() {
        return music;
    }
    public AddMusicCommand(Music music){
        this.music = music;
    }
}
