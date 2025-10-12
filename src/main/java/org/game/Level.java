package org.game;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Level {
    JSONArray levelsArray;
    JSONObject level;
    String name;
    int size;
    int filledTiles;
    Color currentColour;
    ColourTheme colourTheme;
    JSONArray tiles;
    JSONArray regions;
    JSONArray fixed;
    public GameFrame gameFrame;
    JPanel fieldPanel;
    JPanel colourPanel;


    Level(int id){
        InputStream is = GameFrame.class.getClassLoader().getResourceAsStream("Levels.json");
        if (is == null) {
            System.err.println("Levels.json not found");
            return;
        }

        String content = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        levelsArray = new JSONArray(content);

        level = levelsArray.getJSONObject(id);
        name = level.getString("name");
        size = level.getInt("size");
        tiles = level.getJSONArray("tiles");
        regions = level.getJSONArray("regions");
        fixed = level.getJSONArray("fixed");
        filledTiles = 0;
        currentColour=Color.BLUE;
    }

    void display(){
        //gameFrame.setLayout(new GridLayout(size,size));
        gameFrame.setLayout(null);
        fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(size, size));
        fieldPanel.setBounds(107,89,350,350);

        for (int r = 0; r < size; r++) {
            JSONArray row = tiles.getJSONArray(r);
            for(int j = 0; j < size; j++){
                JButton cell = new JButton(String.valueOf(row.getInt(j)));

                if(fixed.getJSONArray(r).getInt(j)==1){
                    filledTiles++;
                    cell.setBackground(Color.BLACK);
                    cell.setEnabled(false);
                }else{
                    cell.setBackground(Color.WHITE);
                    cell.addActionListener(e -> changeColour(cell));
                }
                fieldPanel.add(cell);
            }
        }

        colourPanel = new JPanel();
        colourPanel.setLayout(new GridLayout(size, 1));
        colourPanel.setBounds(480, 89, 50, 350);

        colourTheme = new ColourTheme();

        for(int id = 0; id < size; id++){
            JButton palate = new JButton();
            Color colour = colourTheme.idToColour(id);
            palate.setBackground(colour);
            final int idx=id;
            palate.addActionListener(e -> currentColour=colourTheme.idToColour(idx));
            colourPanel.add(palate);
        }

        gameFrame.add(colourPanel);
        gameFrame.add(fieldPanel);
        gameFrame.setVisible(true);
    }

    void changeColour(JButton cell){
        if(!cell.getBackground().equals(currentColour)){
            cell.setBackground(currentColour);
            filledTiles++;
            if(filledTiles==size*size){
                checkSolution();
            }
        }else{
            cell.setBackground(Color.WHITE);
            filledTiles--;
        }

    }

    void checkSolution(){

        for(int r = 0; r < size; r++){
            JSONArray row = tiles.getJSONArray(r);
            Set<Object> colours = new HashSet<>();
            for(int c = 0; c < size; c++){
                colours.add(row.getInt(c));
            }
            if(colours.size() < size){
                return;
            }
        }

        for(int c = 0; c < size; c++){
            Set<Object> colours = new HashSet<>();
            for(int r = 0; r < size; r++){
                colours.add(tiles.getJSONArray(r).getInt(c));
            }
            if(colours.size() < size){
                return;
            }
        }

        ArrayList<Set<Object>> colours = new ArrayList<>();
        for(int i = 0; i < size; i++){
            colours.add(new HashSet<>());
        }

        for(int r = 0; r < size; r++){
            JSONArray row = tiles.getJSONArray(r);
            JSONArray regionRow = regions.getJSONArray(r);
            for(int c = 0; c < size; c++){
                int colour = row.getInt(c);
                int region = regionRow.getInt(c);
                colours.get(region).add(colour);
            }
        }
        for(int region = 0; region < size; region++){
            if(colours.get(region).size()!=size){
                return;
            }
        }

        markCompleted();
    }

    void markCompleted(){
        //Do stuff
    }
}
