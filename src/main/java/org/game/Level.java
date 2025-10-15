package org.game;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
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
    int currentCell;
    ColourTheme colourTheme;
    JSONArray tiles;
    JSONArray regions;
    JSONArray fixed;
    public GameFrame gameFrame;
    JPanel fieldPanel;
    JPanel colourPanel;
    JPanel backPanel;
    boolean completed;


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
        completed = level.getBoolean("completed");
        filledTiles = 0;
        currentCell = 0;
    }

    void display(){
        //gameFrame.setLayout(new GridLayout(size,size));
        gameFrame.setLayout(null);
        fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(size, size));
        fieldPanel.setBounds(107,89,350,350);

        colourPanel = new JPanel();
        colourPanel.setLayout(new GridLayout(size, 1));
        colourPanel.setBounds(480, 89, 50, 350);

        backPanel = new JPanel();
        backPanel.setLayout(new GridLayout(size, 1));
        backPanel.setBounds(50, 20, 100, 220);

        colourTheme = new ColourTheme();

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Calibri", Font.PLAIN, 30));
        backButton.addActionListener(e -> gameFrame.displayMenu());
        backPanel.add(backButton);

        for(int id = 0; id < size; id++){
            JButton palate = new JButton();
            Color colour = colourTheme.idToColour(id);
            palate.setBackground(Color.WHITE);
            palate.setText(String.valueOf(id));
            final int idx=id;
            palate.addActionListener(e -> currentCell = idx);
            colourPanel.add(palate);
        }

        for (int r = 0; r < size; r++) {
            JSONArray row = tiles.getJSONArray(r);
            for(int c = 0; c < size; c++){
                JButton cell = new JButton();
                cell.setFont(new Font("Calibri", Font.PLAIN, 20));
                //cell.setText(html);
                cell.setForeground(Color.WHITE);


                if(fixed.getJSONArray(r).getInt(c)==1){
                    filledTiles++;
                    //cell.setBackground(colourTheme.idToColour(tiles.getJSONArray(r).getInt(c)));
                    cell.setEnabled(false);
                    cell.setText(String.valueOf(row.getInt(c)));
                }else{
                    //cell.setBackground(Color.WHITE);
                    int finalR = r;
                    int finalC = c;
                    cell.addActionListener(e -> changeCell(cell, finalR, finalC));
                }
                cell.setBackground(colourTheme.idToColour(regions.getJSONArray(r).getInt(c)));
                fieldPanel.add(cell);
            }
        }



        gameFrame.add(colourPanel);
        gameFrame.add(fieldPanel);
        gameFrame.add(backPanel);
        gameFrame.setVisible(true);
    }

    void changeCell(JButton cell, int r, int c){
        if(!cell.getText().equals(String.valueOf(currentCell))){
            if(cell.getText().isEmpty()) filledTiles++;
            cell.setText(String.valueOf(currentCell));
            tiles.getJSONArray(r).put(c, currentCell);
            if(filledTiles==size*size){
                checkSolution();
            }
        }else{
            cell.setBackground(Color.WHITE);
            filledTiles--;
            tiles.getJSONArray(r).put(c, -1);
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

    void markCompleted() {

        try {
            int id = Integer.parseInt(name.substring(6)) - 1;
            level.put("completed", true);
            levelsArray.put(id, level);
            FileWriter writer = new FileWriter("src/main/resources/Levels.json", false);
            writer.write(levelsArray.toString());
            writer.close();
        }

        catch(IOException ie) {
            ie.printStackTrace();
        }

        System.out.println("YOU WOOOOON!!!");
    }
}
