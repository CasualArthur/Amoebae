package org.game;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        UIManager.put("Button.disabledText", new ColorUIResource(Color.BLACK));

        GameFrame gameFrame = new GameFrame();
        gameFrame.displayMenu();
    }
}