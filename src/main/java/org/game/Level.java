package org.game;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.LineBorder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a single game level with a puzzle board.
 * Manages the game logic, UI rendering, and solution validation for Sudoku-like puzzles
 * with arbitrary regions instead of fixed 3x3 blocks.
 */
public class Level {
    JSONArray levelsArray;
    ArrayList<RoundedButton> numberSelectorButtons;
    ArrayList<ImageIcon> numberImages;
    JSONObject levelData;
    int levelId;
    int boardSize;
    int filledCellCount;
    int selectedNumber;
    JSONArray boardState;
    JSONArray regionAssignments;
    JSONArray fixedCells;
    GameFrame gameFrame;
    JPanel boardPanel;
    JPanel numberSelectorPanel;
    JPanel controlButtonsPanel;
    boolean isCompleted;
    JWindow completionPopup;

    /**
     * Constructs a Level object by loading level data from JSON.
     *
     * @param levelIndex The index of the level to load
     * @throws IOException If there's an error reading the levels file
     */
    Level(int levelIndex) throws IOException {
        Path levelsPath = Paths.get("src/main/resources/Levels.json");
        String levelsContent = Files.readString(levelsPath, StandardCharsets.UTF_8);
        levelsArray = new JSONArray(levelsContent);
        numberImages = new ArrayList<>();

        levelData = levelsArray.getJSONObject(levelIndex);
        levelId = levelData.getInt("id");
        boardSize = levelData.getInt("size");
        boardState = levelData.getJSONArray("tiles");
        regionAssignments = levelData.getJSONArray("regions");
        fixedCells = levelData.getJSONArray("fixed");
        isCompleted = levelData.getBoolean("completed");

        completionPopup = new JWindow(gameFrame);
        filledCellCount = 0;
        selectedNumber = 1;
    }

    /**
     * Displays the level on the game frame.
     * Creates the game board, number selector, and control buttons.
     *
     * @throws IOException If there's an error during display setup
     */
    void display() throws IOException {
        gameFrame.setLayout(null);

        // Create the game board panel
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(boardSize, boardSize));
        boardPanel.setBounds(107, 89, 350, 350);
        boardPanel.setOpaque(false);

        // Create the number selector panel (right side)
        numberSelectorPanel = new JPanel();
        numberSelectorPanel.setLayout(new GridLayout(boardSize, 1, 0, -1));
        numberSelectorPanel.setBounds(480, 89, 350 / boardSize, 350);
        numberSelectorPanel.setOpaque(false);

        // Create control buttons panel (back and reset)
        controlButtonsPanel = new JPanel();
        controlButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        controlButtonsPanel.setBounds(107, 460, 350, 58);
        controlButtonsPanel.setOpaque(false);

        // Create Back button
        RoundedButton backButton = new RoundedButton();
        backButton.setRadius(20);
        backButton.setText("Back");
        backButton.setPreferredSize(new Dimension(140, 58));
        backButton.setBgColour(Color.WHITE);
        backButton.addActionListener(_ -> {
            completionPopup.setVisible(false);
            gameFrame.displayLevels();
        });
        controlButtonsPanel.add(backButton);

        // Create Reset button
        RoundedButton resetButton = new RoundedButton();
        resetButton.setRadius(20);
        resetButton.setPreferredSize(new Dimension(140, 58));
        resetButton.setText("Reset");
        resetButton.setBgColour(Color.WHITE);
        resetButton.addActionListener(_ -> {
            try {
                completionPopup.setVisible(false);
                gameFrame.displayLevel(levelId);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        controlButtonsPanel.add(resetButton);

        numberSelectorButtons = new ArrayList<>();

        // Create number selector buttons (1 to boardSize)
        for (int number = 1; number <= boardSize; number++) {
            RoundedButton numberButton = new RoundedButton();
            numberButton.setRadius(500);
            numberButton.setText(String.valueOf(number));

            // First button is selected by default
            if (number == 1) {
                numberButton.selected = true;
                numberButton.setBgColour(Color.ORANGE);
            }

            final int buttonIndex = number - 1;
            if (buttonIndex % 2 == 0) {
                numberButton.setBgColour(Color.decode("0x85A4E4"));
            } else {
                numberButton.setBgColour(Color.WHITE);
            }

            numberButton.setPreferredSize(new Dimension(350 / boardSize, 350 / boardSize));
            numberButton.addActionListener(_ -> selectNumber(buttonIndex));
            numberSelectorPanel.add(numberButton);
            numberSelectorButtons.add(numberButton);
        }

        // Create the board cells
        for (int row = 0; row < boardSize; row++) {
            JSONArray rowData = boardState.getJSONArray(row);
            for (int col = 0; col < boardSize; col++) {
                JButton cellButton = new JButton();
                cellButton.setFont(new Font("Calibri", Font.PLAIN, 25));
                cellButton.setFocusable(false);
                cellButton.setForeground(Color.WHITE);

                // Check if this cell is fixed (pre-filled)
                if (fixedCells.getJSONArray(row).getInt(col) == 1) {
                    filledCellCount++;
                    cellButton.setEnabled(false);
                    cellButton.setText(String.valueOf(rowData.getInt(col)));
                } else {
                    // Make cell editable
                    int finalRow = row;
                    int finalCol = col;
                    cellButton.addActionListener(_ -> updateCell(cellButton, finalRow, finalCol));
                }

                // Set cell background color based on region
                cellButton.setBackground(ColourTheme.idToColour(
                        regionAssignments.getJSONArray(row).getInt(col)));
                cellButton.setBorder(new LineBorder(Color.BLACK));
                boardPanel.add(cellButton);
            }
        }

        // Add all panels to game frame
        gameFrame.add(numberSelectorPanel);
        gameFrame.add(boardPanel);
        gameFrame.add(controlButtonsPanel);
        gameFrame.setVisible(true);
    }

    /**
     * Handles selection of a number from the number selector panel.
     * Updates the visual state to highlight the selected number.
     *
     * @param numberIndex The index of the selected number button (0-based)
     */
    void selectNumber(int numberIndex) {
        // Deselect all buttons
        for (RoundedButton button : numberSelectorButtons) {
            button.selected = false;
            button.setBorderColour(Color.GRAY);
        }

        // Select the clicked button
        numberSelectorButtons.get(numberIndex).selected = true;
        numberSelectorButtons.get(numberIndex).setBorderColour(Color.ORANGE);
        numberSelectorPanel.repaint();
        selectedNumber = numberIndex + 1;
    }

    /**
     * Updates a cell on the board when clicked by the user.
     * If the cell already contains the selected number, it clears the cell.
     * Otherwise, it places the selected number in the cell.
     * Checks for solution completion after each update.
     *
     * @param cellButton The button representing the cell
     * @param row The row index of the cell
     * @param col The column index of the cell
     */
    void updateCell(JButton cellButton, int row, int col) {
        if (!cellButton.getText().equals(String.valueOf(selectedNumber))) {
            // Cell is empty or contains a different number
            if (cellButton.getText().isEmpty()) {
                filledCellCount++;
            }
            cellButton.setText(String.valueOf(selectedNumber));
            boardState.getJSONArray(row).put(col, selectedNumber);

            // Check if board is completely filled
            if (filledCellCount == boardSize * boardSize) {
                validateSolution();
            }
        } else {
            // Cell already contains the selected number, so clear it
            cellButton.setText("");
            filledCellCount--;
            boardState.getJSONArray(row).put(col, 0);
        }
    }

    /**
     * Validates the current board state to check if the puzzle is solved correctly.
     * Checks that all rows, columns, and regions contain distinct numbers.
     * If valid, marks the level as completed.
     */
    void validateSolution() {
        // Check all rows for distinct numbers
        for (int row = 0; row < boardSize; row++) {
            JSONArray rowData = boardState.getJSONArray(row);
            Set<Object> numbersInRow = new HashSet<>();
            for (int col = 0; col < boardSize; col++) {
                numbersInRow.add(rowData.getInt(col));
            }
            if (numbersInRow.size() < boardSize) {
                return; // Row has duplicate numbers
            }
        }

        // Check all columns for distinct numbers
        for (int col = 0; col < boardSize; col++) {
            Set<Object> numbersInColumn = new HashSet<>();
            for (int row = 0; row < boardSize; row++) {
                numbersInColumn.add(boardState.getJSONArray(row).getInt(col));
            }
            if (numbersInColumn.size() < boardSize) {
                return; // Column has duplicate numbers
            }
        }

        // Check all regions for distinct numbers
        ArrayList<Set<Object>> numbersInRegions = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            numbersInRegions.add(new HashSet<>());
        }

        for (int row = 0; row < boardSize; row++) {
            JSONArray rowData = boardState.getJSONArray(row);
            JSONArray regionRow = regionAssignments.getJSONArray(row);
            for (int col = 0; col < boardSize; col++) {
                int cellValue = rowData.getInt(col);
                int regionId = regionRow.getInt(col);
                numbersInRegions.get(regionId).add(cellValue);
            }
        }

        for (int regionId = 0; regionId < boardSize; regionId++) {
            if (numbersInRegions.get(regionId).size() != boardSize) {
                return; // Region has duplicate numbers
            }
        }

        // All checks passed - puzzle is solved!
        markLevelAsCompleted();
    }

    /**
     * Marks the level as completed in the levels data file and displays a completion popup.
     * Updates the JSON file to persist the completion status.
     */
    void markLevelAsCompleted() {
        try {
            // Update level completion status
            levelData.put("completed", true);
            levelsArray.put(levelId, levelData);

            // Save to file
            FileWriter writer = new FileWriter(
                    "src/main/resources/Levels.json", false);
            writer.write(levelsArray.toString());
            writer.close();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        // Create and display completion popup
        JPanel popupPanel = new JPanel();
        JLabel completionMessage = new JLabel("Level complete!");
        completionMessage.setFont(new Font("Calibri", Font.PLAIN, 25));
        popupPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        popupPanel.setBackground(Color.WHITE);
        popupPanel.add(completionMessage);
        completionPopup.add(popupPanel);

        completionPopup.setVisible(true);
        completionPopup.setBounds(150, 188, 276, 200);
        completionPopup.setLocationRelativeTo(gameFrame);
    }
}