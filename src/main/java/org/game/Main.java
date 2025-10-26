package org.game;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

/**
 * Entry point for the Amoebae game application.
 * Initializes the music player and game frame, then displays the main menu.
 */
public class Main {
    static MusicPlayer musicPlayer;

    /**
     * Main method that launches the game.
     * Sets up UI defaults and creates the game window.
     *
     * @param args Command line arguments (not used)
     * @throws IOException If there's an error initializing the game
     */
    public static void main(String[] args) throws IOException {
        // Ensure disabled button text remains black
        UIManager.put("Button.disabledText", new ColorUIResource(Color.BLACK));

        musicPlayer = new MusicPlayer();
        GameFrame gameFrame = new GameFrame(musicPlayer);
        gameFrame.displayMenu();
    }
}