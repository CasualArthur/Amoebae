Amoebae - Sudoku with Arbitrary Regions
A puzzle game similar to Sudoku, but with customizable board sizes and arbitrary region shapes instead of fixed 3x3 blocks.

Ensure you have Java Development Kit (JDK) 17 or higher and Gradle installed.

Below is a list of dependencies
- org.json - JSON parsing library
- javax.sound.sampled - Audio playback (included in JDK)
- javax.swing - GUI components (included in JDK)

To start the game, run the Main.java file from your IDE

Below is a list of features to test

1. Main Menu
- Click Play to view available levels
- Click Settings to adjust volume and theme
- Click Rules to view game instructions

2. Level Selection
- View all available levels with their:
  - Board size (e.g., 5x5, 6x6)
  - Difficulty rating
  - Completion status
- Completed levels appear in gold
- Uncompleted levels alternate between blue and white
- Click any level to start playing

3. Gameplay
- Placing and removing Numbers:
  1. Select a number from the panel on the right
  2. Click an empty cell to place the number
  3. Click a filled cell with the same number to clear it
- Regions: Each coloured area must contain all numbers 1 through N (where N is the board size)
- Victory Condition: All rows, columns, and regions must contain distinct numbers
- Buttons:
  - Back - Return to level selection
  - Reset - Clear the board and start over

4. Settings
- Volume Control: Drag the slider to adjust background music volume (0-100)
- Theme Switching: Click "Switch theme" to cycle through colour palettes
  - Preview shows the three main colours in the selected theme
  - Changes apply immediately to the game board

5. Rules Screen
- View game rules and instructions
- Learn about win conditions

Game Rules

- Each row must contain all numbers from 1 to N (no duplicates)
- Each column must contain all numbers from 1 to N (no duplicates)
- Each region (coloured area) must contain all numbers from 1 to N (no duplicates)
- There is exactly one solution per puzzle

Enjoy playing Amoebae!
