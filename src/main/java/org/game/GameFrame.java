package org.game;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


import org.json.*;

public class GameFrame extends JFrame {
        MusicPlayer play;


    GameFrame(MusicPlayer play){
        this.setTitle("Amoebae");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(576,576);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.play = play;
        
        

        //displayMenu();
    }

    void displayMenu(){

        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        RoundedButton playButton = new RoundedButton();
        playButton.setPreferredSize(new Dimension(200, 100));
        playButton.setRadius(20);
        playButton.setText("Play");
        playButton.setBackground(Color.decode("0x85A4E4"));
        playButton.bgColour=Color.decode("0x85A4E4");
        playButton.addActionListener(e -> this.displayLevels());

        RoundedButton settingsButton = new RoundedButton();
        settingsButton.setPreferredSize(new Dimension(200, 100));
        settingsButton.setRadius(20);
        settingsButton.setText("Settings");
        settingsButton.addActionListener(e -> this.displaySettings());

        RoundedButton rulesButton = new RoundedButton();
        rulesButton.setPreferredSize(new Dimension(200, 100));
        rulesButton.setRadius(20);
        rulesButton.setText("Rules");
        rulesButton.setBackground(Color.decode("0x85A4E4"));
        rulesButton.bgColour=Color.decode("0x85A4E4");
        rulesButton.addActionListener(e -> this.displayRules());

        JLabel label = new JLabel("Amoebae");
        label.setFont(new Font("Calibri", Font.PLAIN, 50));
        label.setBounds(128, 84, 300, 50);
        label.setHorizontalAlignment(JLabel.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(128,168,300,300);
        buttonPanel.setLayout(new GridLayout(3,1, 30, 30));
        buttonPanel.setOpaque(false);

        String resourcePath = "/images/planks.jpg";
        java.net.URL imgURL = getClass().getResource(resourcePath);

        assert imgURL != null;
        setContentPane(new JLabel(new ImageIcon(imgURL)));

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

            JPanel backPanel = new JPanel();
            backPanel.setLayout(new GridBagLayout());
            backPanel.setBounds(107, 460, 350, 58);
            backPanel.setOpaque(false);
            RoundedButton backButton = new RoundedButton();
            backButton.setRadius(20);
            backButton.setPreferredSize(new Dimension(140, 58));
            backButton.setText("Back");
            backButton.bgColour=Color.WHITE;
            backButton.addActionListener(e -> this.displayMenu());
            backPanel.add(backButton);

            JPanel levelPanel = new JPanel();
            levelPanel.setLayout(new GridLayout(levelsArray.length(),1, 10, 10));
            levelPanel.setBounds(80, 80, 200, 110*levelsArray.length());
            levelPanel.setOpaque(false);

            JPanel extraPanel = new JPanel();
            extraPanel.setLayout(new GridLayout(levelsArray.length(),1, 10, 10));
            extraPanel.setBounds(320, 80, 200, 110*levelsArray.length());
            extraPanel.setOpaque(false);

            for (int i = 0; i < levelsArray.length(); i++) {
                JSONObject level = levelsArray.getJSONObject(i);
                RoundedButton levelButton = new RoundedButton();
                levelButton.setRadius(20);
                levelButton.setPreferredSize(new Dimension(200, 100));
                levelButton.setText("Level " + (i+1));
                if(level.getBoolean("completed")) levelButton.setBgColour(Color.decode("0x88F18B"));
                else{
                    if(i%2==0) levelButton.setBgColour(Color.decode("0x85A4E4"));
                    else levelButton.setBgColour(Color.WHITE);
                }
                levelButton.setBorder(new LineBorder(Color.BLACK));
                final int id=i;
                levelButton.addActionListener(e -> this.displayLevel(id));

                levelPanel.add(levelButton);

                String size = "Board size: " + level.getInt("size") + "x" + level.getInt("size");
                String difficulty = "Difficulty: " + level.getString("difficulty");
                String completed = "Completed: " + level.getBoolean("completed");
                JLabel extra = new JLabel("<html>"+size+"<br/>"+difficulty+"<br/>"+completed+"<br/></html>");
                extra.setFont(new Font("Calibri", Font.BOLD, 20));
                extraPanel.add(extra);
            }
            this.add(extraPanel);
            this.add(levelPanel);
            this.add(backPanel);
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

        JPanel backPanel = new JPanel();
        backPanel.setLayout(new GridBagLayout());
        backPanel.setBounds(107, 460, 350, 58);
        backPanel.setOpaque(false);
        RoundedButton backButton = new RoundedButton();
        backButton.setRadius(20);
        backButton.setPreferredSize(new Dimension(140, 58));
        backButton.setText("Back");
        backButton.bgColour=Color.WHITE;
        backButton.addActionListener(e -> this.displayMenu());
        backPanel.add(backButton);

        
        RoundedButton volume0 = new RoundedButton();
        volume0.setRadius(20);
        volume0.setPreferredSize(new Dimension(140, 58));
        volume0.setText("Volume: 0%");
        volume0.bgColour=Color.WHITE;
        volume0.addActionListener(e -> play.changeVolume(0));
        RoundedButton volume50 = new RoundedButton();
        volume50.setRadius(20);
        volume50.setPreferredSize(new Dimension(140, 58));
        volume50.setText("volume: 50%");
        volume50.bgColour=Color.WHITE;
        volume50.addActionListener(e -> play.changeVolume(50));
        RoundedButton volume100 = new RoundedButton();
        volume100.setRadius(20);
        volume100.setPreferredSize(new Dimension(140, 58));
        volume100.setText("volume: 100%");
        volume100.bgColour=Color.WHITE;
        volume100.addActionListener(e -> play.changeVolume(100));
        
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBounds(128,168,300,300);
        settingsPanel.setLayout(new GridLayout(3,1, 30, 30));
        backPanel.setOpaque(false);

        settingsPanel.add(volume0);
        settingsPanel.add(volume50);
        settingsPanel.add(volume100);

        this.add(settingsPanel);
        this.add(backPanel);
        this.setVisible(true);
        

        //Should add some volume, reset progress button?, maybe different themes (if there's enough time)
        //idk what else
    }

    void displayRules(){
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        JPanel backPanel = new JPanel();
        backPanel.setLayout(new GridBagLayout());
        backPanel.setBounds(107, 460, 350, 58);
        backPanel.setOpaque(false);
        RoundedButton backButton = new RoundedButton();
        backButton.setRadius(20);
        backButton.setPreferredSize(new Dimension(140, 58));
        backButton.setText("Back");
        backButton.bgColour=Color.WHITE;
        backButton.addActionListener(e -> this.displayMenu());
        backPanel.add(backButton);


        JLabel rules = new JLabel("Rules");
        rules.setFont(new Font("Calibri", Font.PLAIN, 100));
        //rules.setForeground(Color.decode("#d9f3fc"));
        rules.setBounds(170,0,300,100);

        JLabel explanation = new JLabel();
        explanation.setText( "<html> • In each row, column and region all numbers must be distinct. <br> • Place a number in each cell to complete the board, by selecting one on the right hand side and applying it to a cell. <br> • Multiple valid solutions might be possible. <br> • You can reset any board by pressing the reset button.  </html>");
        explanation.setFont(new Font("Calibri", Font.BOLD, 27));
        //explanation.setForeground(Color.decode("#d9f3fc"));

        explanation.setBounds(100, 100, 400, 350);
        explanation.setVerticalAlignment(JLabel.TOP);
        

        this.add(rules);
        this.add(explanation);
        this.add(backPanel);
        this.setVisible(true);

    }





}

