package org.game;

import org.json.JSONArray;

import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ColourTheme {
    int currentTheme = 0;
    JSONArray themes;
    JSONArray theme;


    public Color idToColour(int id){
        InputStream is = GameFrame.class.getClassLoader().getResourceAsStream("Themes.json");
        if (is == null) {
            System.err.println("Themes.json not found");
            return Color.WHITE;
        }

        String content = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        themes = new JSONArray(content);

        theme = themes.getJSONObject(currentTheme).getJSONArray("colours");
        return Color.decode(theme.getString(id));
    }

    public int colourToId(Color colour, int size){
        for(int c = 0; c < size; c++){
            if(Color.decode(theme.getString(c)).equals(colour)){
                return c;
            }
        }
        return -1;
    }
}
