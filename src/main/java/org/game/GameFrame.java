package org.game;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.LineBorder;
import org.json.*;

public class GameFrame extends JFrame {
    MusicPlayer player;
    ArrayList<RoundedButton> buttons;
    int themeId;
    int volume;
    boolean isPlaying;
    Slider slider;

    GameFrame(MusicPlayer player) {
        this.setTitle("Amoebae");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(576,576);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.player = player;

        slider = new Slider(player) {
            @Override
            public void updateUI() {
                setUI(new Slider.CustomSliderUI(this));
            }
        };
    }

    void displayMenu() throws IOException {
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        RoundedButton playButton = new RoundedButton();
        playButton.setPreferredSize(new Dimension(200, 100));
        playButton.setRadius(20);
        playButton.setText("Play");
        playButton.setBackground(Color.decode("0x85A4E4"));
        playButton.bgColour = Color.decode("0x85A4E4");
        playButton.addActionListener(_ -> this.displayLevels());

        RoundedButton settingsButton = new RoundedButton();
        settingsButton.setPreferredSize(new Dimension(200, 100));
        settingsButton.setRadius(20);
        settingsButton.setText("Settings");
        settingsButton.addActionListener(_ -> {
            try {
                this.displaySettings();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        RoundedButton rulesButton = new RoundedButton();
        rulesButton.setPreferredSize(new Dimension(200, 100));
        rulesButton.setRadius(20);
        rulesButton.setText("Rules");
        rulesButton.setBackground(Color.decode("0x85A4E4"));
        rulesButton.bgColour = Color.decode("0x85A4E4");
        rulesButton.addActionListener(_ -> {
            try {
                this.displayRules();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        JLabel label = new JLabel("Amoebae");
        label.setFont(new Font("Algerian", Font.BOLD, 60));
        label.setBounds(128, 90, 300, 50);
        label.setHorizontalAlignment(JLabel.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(128, 168, 300, 300);
        buttonPanel.setLayout(new GridLayout(3, 1, 30, 30));
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

        if (!isPlaying) {
            isPlaying = true;

            player.playMusic();
        }
    }

    void displayLevels(){
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        try {
            Path path = Paths.get("src/main/resources/Preferences.json");
            String content = Files.readString(path, StandardCharsets.UTF_8);
            JSONArray preferences = new JSONArray(content);

            volume = preferences.getJSONObject(0).getInt("volume");
            slider.setValue(volume);

            path = Paths.get("src/main/resources/Levels.json");
            content = Files.readString(path, StandardCharsets.UTF_8);
            JSONArray levelsArray = new JSONArray(content);

            JPanel backPanel = new JPanel();
            backPanel.setLayout(new GridBagLayout());
            backPanel.setBounds(107, 460, 350, 58);
            backPanel.setOpaque(false);
            RoundedButton backButton = new RoundedButton();
            backButton.setRadius(20);
            backButton.setPreferredSize(new Dimension(140, 58));
            backButton.setText("Back");
            backButton.bgColour = Color.WHITE;
            backButton.addActionListener(_ -> {
                try {
                    this.displayMenu();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            backPanel.add(backButton);

            JPanel levelPanel = new JPanel();
            levelPanel.setLayout(new GridLayout(levelsArray.length(), 1, 10, 10));
            levelPanel.setBounds(80, 80, 200, 110 * levelsArray.length());
            levelPanel.setOpaque(false);

            JPanel extraPanel = new JPanel();
            extraPanel.setLayout(new GridLayout(levelsArray.length(), 1, 10, 10));
            extraPanel.setBounds(320, 80, 200, 110 * levelsArray.length());
            extraPanel.setOpaque(false);

            for (int i = 0; i < levelsArray.length(); i++) {
                JSONObject level = levelsArray.getJSONObject(i);
                RoundedButton levelButton = new RoundedButton();
                levelButton.setRadius(20);
                levelButton.setPreferredSize(new Dimension(200, 100));
                levelButton.setText("Level " + (i + 1));
                if (level.getBoolean("completed")) {
                    levelButton.setBgColour(Color.decode("0xFFD733"));
                } else {
                    if (i % 2 == 0) {
                        levelButton.setBgColour(Color.decode("0x85A4E4"));
                    } else {
                        levelButton.setBgColour(Color.WHITE);
                    }
                }
                levelButton.setBorder(new LineBorder(Color.BLACK));
                final int id = i;
                levelButton.addActionListener(_ -> {
                    try {
                        this.displayLevel(id);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                levelPanel.add(levelButton);

                String size = "Board size: " +
                        level.getInt("size") + "x" + level.getInt("size");
                String difficulty = "Difficulty: " + level.getString("difficulty");
                String completed = "Completed: " + level.getBoolean("completed");
                JLabel extra = new JLabel(
                        "<html>"+size+"<br/>"+difficulty+"<br/>"+completed+"<br/></html>");
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

    void displayLevel(int id) throws IOException {
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        Level level = new Level(id);
        level.gameFrame = this;
        level.display();
    }

    void displaySettings() throws IOException {
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
        backButton.bgColour = Color.WHITE;
        backButton.addActionListener(_ -> {
            try {
                this.displayMenu();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        backPanel.add(backButton);

        Path path = Paths.get("src/main/resources/Preferences.json");
        String content = Files.readString(path, StandardCharsets.UTF_8);
        JSONArray preferences = new JSONArray(content);

        path = Paths.get("src/main/resources/Themes.json");
        content = Files.readString(path, StandardCharsets.UTF_8);
        JSONArray themes = new JSONArray(content);


        JPanel settingsPanel = new JPanel();
        settingsPanel.setBounds(88, 100, 400, 400);
        settingsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JPanel themePanel = new JPanel();
        themePanel.setBounds(88, 270, 400, 400);
        themePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        themeId = preferences.getJSONObject(0).getInt("theme");
        volume = preferences.getJSONObject(0).getInt("volume");
        slider.setValue(volume);
        JSONObject theme = themes.getJSONObject(themeId);
        buttons = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            RoundedButton palette = new RoundedButton();
            palette.setEnabled(false);
            palette.setRadius(500);
            palette.setPreferredSize(new Dimension(40, 40));
            palette.setBgColour(Color.decode(theme.getJSONArray("colours").getString(i)));
            buttons.add(palette);
        }
        RoundedButton switchTheme = new RoundedButton();
        switchTheme.setText("Switch theme");
        switchTheme.setRadius(20);
        switchTheme.setPreferredSize(new Dimension(200, 60));
        switchTheme.addActionListener(_ -> switchTheme(themes.length()));

        settingsPanel.add(new JLabel("Volume")).setFont(
                new Font("Calibri", Font.BOLD, 30));
        settingsPanel.add(slider);
        settingsPanel.add(new JLabel("Theme")).setFont(
                new Font("Calibri", Font.BOLD, 30));
        for (int i = 0; i < 3; i++) {
            themePanel.add(buttons.get(i));
        }
        themePanel.add(switchTheme);

        settingsPanel.setOpaque(false);
        backPanel.setOpaque(false);
        themePanel.setOpaque(false);

        this.add(settingsPanel);
        this.add(backPanel);
        this.add(themePanel);
        this.setVisible(true);

    }

    void displayRules() throws IOException {
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        Path path = Paths.get("src/main/resources/Preferences.json");
        String content = Files.readString(path, StandardCharsets.UTF_8);
        JSONArray preferences = new JSONArray(content);

        volume = preferences.getJSONObject(0).getInt("volume");
        slider.setValue(volume);

        JPanel backPanel = new JPanel();
        backPanel.setLayout(new GridBagLayout());
        backPanel.setBounds(107, 460, 350, 58);
        backPanel.setOpaque(false);
        RoundedButton backButton = new RoundedButton();
        backButton.setRadius(20);
        backButton.setPreferredSize(new Dimension(140, 58));
        backButton.setText("Back");
        backButton.bgColour = Color.WHITE;
        backButton.addActionListener(_ -> {
            try {
                this.displayMenu();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        backPanel.add(backButton);

        JLabel rules = new JLabel("Rules");
        rules.setFont(new Font("Calibri", Font.PLAIN, 100));
        rules.setBounds(170, 0, 300, 100);

        JLabel explanation = getJLabel();

        this.add(rules);
        this.add(explanation);
        this.add(backPanel);
        this.setVisible(true);
    }

    private static JLabel getJLabel() {
        JLabel explanation = new JLabel();
        explanation.setText(
                "<html> • In each row, column and region all numbers must be distinct. " +
                "<br> • Place a number in each cell to complete the board, " +
                "by selecting one on the right hand side and applying it to a cell. " +
                "<br> • There is exactly one possible solution per board. " +
                "<br> • You can reset any board by pressing the reset button.  </html>");
        explanation.setFont(new Font("Calibri", Font.BOLD, 27));

        explanation.setBounds(100, 100, 400, 350);
        explanation.setVerticalAlignment(JLabel.TOP);
        return explanation;
    }

    void switchTheme(int numberOfThemes){
        try {
            themeId++;
            if (themeId == numberOfThemes) {
                themeId = 0;
            }
            Path path = Paths.get("src/main/resources/Preferences.json");
            String content = Files.readString(path, StandardCharsets.UTF_8);
            JSONArray preferences = new JSONArray(content);

            JSONObject list = preferences.getJSONObject(0);
            list.put("theme", themeId);
            preferences.put(0, list);

            FileWriter writer = new FileWriter("src/main/resources/Preferences.json", false);
            writer.write(preferences.toString());
            writer.close();

            path = Paths.get("src/main/resources/Themes.json");
            content = Files.readString(path, StandardCharsets.UTF_8);
            JSONArray themes = new JSONArray(content);

            JSONObject theme = themes.getJSONObject(themeId);

            for (int i = 0; i < 3; i++){
                buttons.get(i).setBgColour(Color.decode(theme.getJSONArray("colours").getString(i)));
            }
        }

        catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}
