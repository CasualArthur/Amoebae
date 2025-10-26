package org.game;

import org.json.JSONArray;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for managing color themes in the game.
 * Loads theme configurations from JSON files and maps region IDs to colours.
 */
public class ColourTheme {

    /**
     * Converts a region ID to its corresponding colour based on the current theme.
     * Loads the active theme from user preferences and returns the colour for the specified region.
     *
     * @param regionId The ID of the region (0-based index into theme colours array)
     * @return The Colour object corresponding to the region ID in the current theme
     * @throws IOException If there's an error reading the theme or preferences files
     */
    public static Color idToColour(int regionId) throws IOException {
        // Load available themes
        Path themesPath = Paths.get("src/main/resources/Themes.json");
        String themesContent = Files.readString(themesPath, StandardCharsets.UTF_8);
        JSONArray themes = new JSONArray(themesContent);

        // Load user preferences to get current theme
        Path preferencesPath = Paths.get("src/main/resources/Preferences.json");
        String preferencesContent = Files.readString(preferencesPath, StandardCharsets.UTF_8);
        JSONArray preferences = new JSONArray(preferencesContent);
        int currentThemeId = preferences.getJSONObject(0).getInt("theme");

        // Get colors array for the current theme
        JSONArray themeColours = themes.getJSONObject(currentThemeId).getJSONArray("colours");
        return Color.decode(themeColours.getString(regionId));
    }
}