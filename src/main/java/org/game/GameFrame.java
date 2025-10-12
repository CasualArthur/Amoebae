package org.game;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.*;

public class GameFrame extends JFrame {

    GameFrame(){
        this.setTitle("Amoebae");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(576,576);
        this.setResizable(false);

        //displayMenu();
    }

    void displayMenu(){
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Calibri", Font.PLAIN, 30));
        playButton.addActionListener(e -> this.displayLevels());

        JButton settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font("Calibri", Font.PLAIN, 30));
        settingsButton.addActionListener(e -> this.displaySettings());

        JButton rulesButton = new JButton("Rules");
        rulesButton.setFont(new Font("Calibri", Font.PLAIN, 30));
        rulesButton.addActionListener(e -> this.displayRules());

        JLabel label = new JLabel("Amoebae");
        label.setFont(new Font("Calibri", Font.PLAIN, 50));
        label.setBounds(128, 84, 300, 50);
        label.setHorizontalAlignment(JLabel.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(128,168,300,300);
        buttonPanel.setLayout(new GridLayout(3,1, 30, 30));

        buttonPanel.add(playButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(rulesButton);

        this.add(label);
        this.add(buttonPanel);
        this.setVisible(true);
    }

    void displayLevels(){
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        try {
            InputStream is = GameFrame.class.getClassLoader().getResourceAsStream("Levels.json");
            if (is == null) {
                System.err.println("Levels.json not found");
                return;
            }

            String content = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            JSONArray levelsArray = new JSONArray(content);

            this.setLayout(new FlowLayout());

            for (int i = 0; i < levelsArray.length(); i++) {
                JSONObject level = levelsArray.getJSONObject(i);

                String name = level.getString("name");
                int size = level.getInt("size");
                JSONArray tiles = level.getJSONArray("tiles");
                JSONArray regions = level.getJSONArray("regions");


                JButton levelButton = new JButton(name);
                final int id=i;
                levelButton.addActionListener(e -> this.displayLevel(id));

                this.add(levelButton); //change to a panel later

                System.out.println("Level: " + name + ", size: " + size);


            }
            this.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void displayLevel(int id){
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        Level level = new Level(id);
        level.gameFrame = this;
        level.display();

    }

    void displaySettings(){
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        //Should add some volume, reset progress button?, maybe different themes (if there's enough time)
        //idk what else
    }

    void displayRules(){
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);


        JLabel rules = new JLabel("Rules");
        rules.setFont(new Font("Calibri", Font.PLAIN, 50));
        rules.setBounds(0,0,300,100);

        JLabel explanation = new JLabel();
        explanation.setText("In each row, column and region all colours must be distinct.");
        //maybe make a new file with full text, also fix the cutoff
        explanation.setBounds(0, 100, 300, 300);
        explanation.setVerticalAlignment(JLabel.TOP);

        //Should add some example image
        this.add(rules);
        this.add(explanation);
        this.setVisible(true);

    }

}

