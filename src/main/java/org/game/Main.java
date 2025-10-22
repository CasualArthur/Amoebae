package org.game;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.io.IOException;

public class Main {
    static MusicPlayer player;
    public static void main(String[] args) throws IOException {

        UIManager.put("Button.disabledText", new ColorUIResource(Color.BLACK));

        player = new MusicPlayer();
        GameFrame gameFrame = new GameFrame(player);
        gameFrame.displayMenu();

    }

    public static void playMusic(){
        player.playMusic();
    }
}