package org.game;

import org.json.JSONArray;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ColourTheme {
    int currentTheme = 0;
    JSONArray theme;


    public Color idToColour(int id) throws IOException {
        Path path = Paths.get("src/main/resources/Themes.json");
        String content = Files.readString(path, StandardCharsets.UTF_8);
        JSONArray themes = new JSONArray(content);

        path = Paths.get("src/main/resources/Preferences.json");
        content = Files.readString(path, StandardCharsets.UTF_8);
        JSONArray preferences = new JSONArray(content);
        currentTheme = preferences.getJSONObject(0).getInt("theme");

        theme = themes.getJSONObject(currentTheme).getJSONArray("colours");
        return Color.decode(theme.getString(id));
    }

}
