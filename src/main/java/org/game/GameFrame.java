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

/**
 * Main game window that manages the UI and navigation between different screens.
 * Handles the menu, level selection, settings, and rules displays.
 */
public class GameFrame extends JFrame {
    MusicPlayer musicPlayer;
    ArrayList<RoundedButton> themeColourButtons;
    int selectedThemeId;
    int musicVolume;
    boolean isMusicPlaying;
    Slider volumeSlider;

    /**
     * Constructs the main game frame.
     *
     * @param musicPlayer The music player instance for background music
     */
    public GameFrame(MusicPlayer musicPlayer) {
        this.setTitle("Amoebae");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(576, 576);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.musicPlayer = musicPlayer;

        volumeSlider = new Slider(musicPlayer) {
            @Override
            public void updateUI() {
                setUI(new Slider.CustomSliderUI(this));
            }
        };
    }

    /**
     * Displays the main menu with play, settings, and rules buttons.
     * Starts background music if not already playing.
     *
     * @throws IOException If there's an error loading resources
     */
    void displayMenu() throws IOException {
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        // Create Play button
        RoundedButton playButton = new RoundedButton();
        playButton.setPreferredSize(new Dimension(200, 100));
        playButton.setRadius(20);
        playButton.setText("Play");
        playButton.setBackground(Color.decode("0x85A4E4"));
        playButton.backgroundColour = Color.decode("0x85A4E4");
        playButton.addActionListener(e -> this.displayLevels());

        // Create Settings button
        RoundedButton settingsButton = new RoundedButton();
        settingsButton.setPreferredSize(new Dimension(200, 100));
        settingsButton.setRadius(20);
        settingsButton.setText("Settings");
        settingsButton.addActionListener(e -> {
            try {
                this.displaySettings();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Create Rules button
        RoundedButton rulesButton = new RoundedButton();
        rulesButton.setPreferredSize(new Dimension(200, 100));
        rulesButton.setRadius(20);
        rulesButton.setText("Rules");
        rulesButton.setBackground(Color.decode("0x85A4E4"));
        rulesButton.backgroundColour = Color.decode("0x85A4E4");
        rulesButton.addActionListener(e -> {
            try {
                this.displayRules();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Create title label
        JLabel titleLabel = new JLabel("Amoebae");
        titleLabel.setFont(new Font("Algerian", Font.BOLD, 60));
        titleLabel.setBounds(128, 90, 300, 50);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create button panel with grid layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(128, 168, 300, 300);
        buttonPanel.setLayout(new GridLayout(3, 1, 30, 30));
        buttonPanel.setOpaque(false);

        // Set background image
        String resourcePath = "/images/planks.jpg";
        java.net.URL imgURL = getClass().getResource(resourcePath);
        assert imgURL != null;
        setContentPane(new JLabel(new ImageIcon(imgURL)));

        // Add buttons to panel
        buttonPanel.add(playButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(rulesButton);

        this.add(titleLabel);
        this.add(buttonPanel);
        this.setVisible(true);

        // Start music if not already playing
        if (!isMusicPlaying) {
            isMusicPlaying = true;
            musicPlayer.playMusic();
        }
    }

    /**
     * Displays the level selection screen showing all available levels.
     * Each level shows its size, difficulty, and completion status.
     */
    void displayLevels() {
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        try {
            // Load user preferences
            Path preferencesPath = Paths.get("src/main/resources/Preferences.json");
            String preferencesContent = Files.readString(preferencesPath, StandardCharsets.UTF_8);
            JSONArray preferences = new JSONArray(preferencesContent);

            musicVolume = preferences.getJSONObject(0).getInt("volume");
            volumeSlider.setValue(musicVolume);

            // Load levels data
            Path levelsPath = Paths.get("src/main/resources/Levels.json");
            String levelsContent = Files.readString(levelsPath, StandardCharsets.UTF_8);
            JSONArray levelsArray = new JSONArray(levelsContent);

            // Create back button
            JPanel backPanel = new JPanel();
            backPanel.setLayout(new GridBagLayout());
            backPanel.setBounds(107, 460, 350, 58);
            backPanel.setOpaque(false);
            RoundedButton backButton = new RoundedButton();
            backButton.setRadius(20);
            backButton.setPreferredSize(new Dimension(140, 58));
            backButton.setText("Back");
            backButton.backgroundColour = Color.WHITE;
            backButton.addActionListener(e -> {
                try {
                    this.displayMenu();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            backPanel.add(backButton);

            // Create panel for level buttons
            JPanel levelButtonsPanel = new JPanel();
            levelButtonsPanel.setLayout(
                    new GridLayout(levelsArray.length(), 1, 10, 10));
            levelButtonsPanel.setBounds(80, 80, 200, 110 * levelsArray.length());
            levelButtonsPanel.setOpaque(false);

            // Create panel for level details
            JPanel levelDetailsPanel = new JPanel();
            levelDetailsPanel.setLayout(
                    new GridLayout(levelsArray.length(), 1, 10, 10));
            levelDetailsPanel.setBounds(320, 80, 200, 110 * levelsArray.length());
            levelDetailsPanel.setOpaque(false);

            // Create button and details for each level
            for (int i = 0; i < levelsArray.length(); i++) {
                JSONObject levelData = levelsArray.getJSONObject(i);

                // Create level button
                RoundedButton levelButton = new RoundedButton();
                levelButton.setRadius(20);
                levelButton.setPreferredSize(new Dimension(200, 100));
                levelButton.setText("Level " + (i + 1));

                // Set button colour based on completion status
                if (levelData.getBoolean("completed")) {
                    levelButton.setBgColour(Color.decode("0xFFD733")); // Gold for completed
                } else {
                    if (i % 2 == 0) {
                        levelButton.setBgColour(Color.decode("0x85A4E4")); // Blue
                    } else {
                        levelButton.setBgColour(Color.WHITE);
                    }
                }
                levelButton.setBorder(new LineBorder(Color.BLACK));

                final int levelIndex = i;
                levelButton.addActionListener(e -> {
                    try {
                        this.displayLevel(levelIndex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                levelButtonsPanel.add(levelButton);

                // Create level details label
                String boardSize = "Board size: " +
                        levelData.getInt("size") + "x" + levelData.getInt("size");
                String difficulty = "Difficulty: " + levelData.getString("difficulty");
                String completionStatus = "Completed: " + levelData.getBoolean("completed");
                JLabel detailsLabel = new JLabel(
                        "<html>" + boardSize + "<br/>" +
                                difficulty + "<br/>" + completionStatus + "<br/></html>");
                detailsLabel.setFont(new Font("Calibri", Font.BOLD, 20));
                levelDetailsPanel.add(detailsLabel);
            }

            this.add(levelDetailsPanel);
            this.add(levelButtonsPanel);
            this.add(backPanel);
            this.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Displays a specific level's game board.
     *
     * @param levelIndex The index of the level to display
     * @throws IOException If there's an error loading the level data
     */
    void displayLevel(int levelIndex) throws IOException {
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        Level level = new Level(levelIndex);
        level.gameFrame = this;
        level.display();
    }

    /**
     * Displays the settings screen where users can adjust volume and theme.
     *
     * @throws IOException If there's an error loading settings data
     */
    void displaySettings() throws IOException {
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        // Create back button
        JPanel backPanel = new JPanel();
        backPanel.setLayout(new GridBagLayout());
        backPanel.setBounds(107, 460, 350, 58);
        backPanel.setOpaque(false);
        RoundedButton backButton = new RoundedButton();
        backButton.setRadius(20);
        backButton.setPreferredSize(new Dimension(140, 58));
        backButton.setText("Back");
        backButton.backgroundColour = Color.WHITE;
        backButton.addActionListener(e -> {
            try {
                this.displayMenu();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        backPanel.add(backButton);

        // Load preferences and themes
        Path preferencesPath = Paths.get("src/main/resources/Preferences.json");
        String preferencesContent = Files.readString(preferencesPath, StandardCharsets.UTF_8);
        JSONArray preferences = new JSONArray(preferencesContent);

        Path themesPath = Paths.get("src/main/resources/Themes.json");
        String themesContent = Files.readString(themesPath, StandardCharsets.UTF_8);
        JSONArray themes = new JSONArray(themesContent);

        // Create settings panels
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBounds(88, 100, 400, 400);
        settingsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JPanel themePanel = new JPanel();
        themePanel.setBounds(88, 270, 400, 400);
        themePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        // Load current theme and volume settings
        selectedThemeId = preferences.getJSONObject(0).getInt("theme");
        musicVolume = preferences.getJSONObject(0).getInt("volume");
        volumeSlider.setValue(musicVolume);
        JSONObject currentTheme = themes.getJSONObject(selectedThemeId);

        // Create theme colour preview buttons
        themeColourButtons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RoundedButton colorPreviewButton = new RoundedButton();
            colorPreviewButton.setEnabled(false);
            colorPreviewButton.setRadius(500);
            colorPreviewButton.setPreferredSize(new Dimension(40, 40));
            colorPreviewButton.setBgColour(
                    Color.decode(currentTheme.getJSONArray("colours").getString(i)));
            themeColourButtons.add(colorPreviewButton);
        }

        // Create theme switch button
        RoundedButton switchThemeButton = new RoundedButton();
        switchThemeButton.setText("Switch theme");
        switchThemeButton.setRadius(20);
        switchThemeButton.setPreferredSize(new Dimension(200, 60));
        switchThemeButton.addActionListener(e -> switchTheme(themes.length()));

        // Add volume controls
        settingsPanel.add(new JLabel("Volume")).setFont(
                new Font("Calibri", Font.BOLD, 30));
        settingsPanel.add(volumeSlider);
        settingsPanel.add(new JLabel("Theme")).setFont(
                new Font("Calibri", Font.BOLD, 30));

        // Add theme preview colours and switch button
        for (int i = 0; i < 3; i++) {
            themePanel.add(themeColourButtons.get(i));
        }
        themePanel.add(switchThemeButton);

        settingsPanel.setOpaque(false);
        backPanel.setOpaque(false);
        themePanel.setOpaque(false);

        this.add(settingsPanel);
        this.add(backPanel);
        this.add(themePanel);
        this.setVisible(true);
    }

    /**
     * Displays the rules screen explaining how to play the game.
     *
     * @throws IOException If there's an error loading preferences
     */
    void displayRules() throws IOException {
        this.getContentPane().removeAll();
        this.repaint();
        this.setLayout(null);

        // Load volume preference
        Path preferencesPath = Paths.get("src/main/resources/Preferences.json");
        String preferencesContent = Files.readString(preferencesPath, StandardCharsets.UTF_8);
        JSONArray preferences = new JSONArray(preferencesContent);

        musicVolume = preferences.getJSONObject(0).getInt("volume");
        volumeSlider.setValue(musicVolume);

        // Create back button
        JPanel backPanel = new JPanel();
        backPanel.setLayout(new GridBagLayout());
        backPanel.setBounds(107, 460, 350, 58);
        backPanel.setOpaque(false);
        RoundedButton backButton = new RoundedButton();
        backButton.setRadius(20);
        backButton.setPreferredSize(new Dimension(140, 58));
        backButton.setText("Back");
        backButton.backgroundColour = Color.WHITE;
        backButton.addActionListener(e -> {
            try {
                this.displayMenu();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        backPanel.add(backButton);

        // Create rules title
        JLabel rulesTitle = new JLabel("Rules");
        rulesTitle.setFont(new Font("Calibri", Font.PLAIN, 100));
        rulesTitle.setBounds(170, 0, 300, 100);

        JLabel rulesExplanation = createRulesExplanationLabel();

        this.add(rulesTitle);
        this.add(rulesExplanation);
        this.add(backPanel);
        this.setVisible(true);
    }

    /**
     * Creates a label with the game rules explanation.
     *
     * @return JLabel containing the rules text
     */
    private static JLabel createRulesExplanationLabel() {
        JLabel explanation = new JLabel();
        explanation.setText(
                "<html> • In each row, column and region all numbers must be distinct. " +
                        "<br> • Place a number in each cell to complete the board, " +
                        "by selecting one on the right hand side and applying it to a cell. " +
                        "<br> • There is exactly one possible solution per board. " +
                        "<br> • You can reset any board by pressing the reset button. </html>");
        explanation.setFont(new Font("Calibri", Font.BOLD, 27));
        explanation.setBounds(100, 100, 400, 350);
        explanation.setVerticalAlignment(JLabel.TOP);
        return explanation;
    }

    /**
     * Switches to the next available theme and updates the preview.
     * Saves the new theme selection to preferences.
     *
     * @param totalThemeCount The total number of available themes
     */
    void switchTheme(int totalThemeCount) {
        try {
            // Cycle to next theme
            selectedThemeId++;
            if (selectedThemeId == totalThemeCount) {
                selectedThemeId = 0;
            }

            // Load and update preferences
            Path preferencesPath = Paths.get("src/main/resources/Preferences.json");
            String preferencesContent = Files.readString(preferencesPath, StandardCharsets.UTF_8);
            JSONArray preferences = new JSONArray(preferencesContent);

            JSONObject preferencesObject = preferences.getJSONObject(0);
            preferencesObject.put("theme", selectedThemeId);
            preferences.put(0, preferencesObject);

            // Save updated preferences
            FileWriter writer = new FileWriter("src/main/resources/Preferences.json", false);
            writer.write(preferences.toString());
            writer.close();

            // Load new theme and update colour preview buttons
            Path themesPath = Paths.get("src/main/resources/Themes.json");
            String themesContent = Files.readString(themesPath, StandardCharsets.UTF_8);
            JSONArray themes = new JSONArray(themesContent);

            JSONObject newTheme = themes.getJSONObject(selectedThemeId);

            for (int i = 0; i < 3; i++) {
                themeColourButtons.get(i).setBgColour(
                        Color.decode(newTheme.getJSONArray("colours").getString(i)));
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}