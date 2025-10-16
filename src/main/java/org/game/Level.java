package org.game;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
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
    ArrayList<JButton> colourArray;
    ArrayList<ImageIcon> numbers;
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
        numbers = new ArrayList<>();

        level = levelsArray.getJSONObject(id);
        name = level.getString("name");
        size = level.getInt("size");
        tiles = level.getJSONArray("tiles");
        regions = level.getJSONArray("regions");
        fixed = level.getJSONArray("fixed");
        completed = level.getBoolean("completed");

        for (int i = 1; i <= 7; i++) {
            String resourcePath = "/images/" + i + ".png";
            java.net.URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                numbers.add(new ImageIcon(imgURL));
            } else {
                System.err.println("Missing resource: " + resourcePath);
            }
        }

        filledTiles = 0;
        currentCell = 1;
    }

    void display(){
        //gameFrame.setLayout(new GridLayout(size,size));
        gameFrame.setLayout(null);
        fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(size, size));
        fieldPanel.setBounds(107,89,350,350);

        colourPanel = new JPanel();
        colourPanel.setLayout(new GridLayout(size, 1, 0, -1));
        colourPanel.setBounds(480, 89, 58, 350);

        backPanel = new JPanel();
        backPanel.setLayout(new GridLayout(size, 1));
        backPanel.setBounds(50, 20, 100, 220);

        colourTheme = new ColourTheme();

        JButton backButton = new JButton("<");
        backButton.setFont(new Font("Calibri", Font.PLAIN, 30));
        //backButton.setContentAreaFilled(false);
        //backButton.setBorderPainted(false);
        backButton.setBorder(new LineBorder(Color.BLACK));
        backButton.setBackground(Color.ORANGE);
        backButton.setFocusable(false);
        backButton.addActionListener(e -> gameFrame.displayLevels());
        backPanel.add(backButton);

        colourArray = new ArrayList<>();

        for(int id = 1; id <= size; id++){
            JButton palette = new JButton();
            if(id>1) palette.setBackground(Color.WHITE);
            else palette.setBackground(Color.YELLOW);
            palette.setText(String.valueOf(id));
            palette.setFont(new Font("Calibri", Font.PLAIN, 25));
            palette.setFocusable(false);
            //palate.setIcon(numbers.get(id));
            final int idx=id-1;
            palette.addActionListener(e -> chooseColour(idx));
            colourPanel.add(palette);
            colourArray.add(palette);
        }

        for (int r = 0; r < size; r++) {
            JSONArray row = tiles.getJSONArray(r);
            for(int c = 0; c < size; c++){
                JButton cell = new JButton();
                cell.setFont(new Font("Calibri", Font.PLAIN, 25));
                cell.setFocusable(false);
                //cell.setText(html);
                cell.setForeground(Color.WHITE);


                if(fixed.getJSONArray(r).getInt(c)==1){
                    filledTiles++;
                    //cell.setBackground(colourTheme.idToColour(tiles.getJSONArray(r).getInt(c)));
                    cell.setEnabled(false);
                    //cell.setDis
                    cell.setText(String.valueOf(row.getInt(c)));
                    //cell.setText("<html>"+row.getInt(c)+"</html>");
                }else{
                    //cell.setBackground(Color.WHITE);
                    int finalR = r;
                    int finalC = c;
                    cell.addActionListener(e -> changeCell(cell, finalR, finalC));
                }
                cell.setBackground(colourTheme.idToColour(regions.getJSONArray(r).getInt(c)));
                cell.setBorder(new LineBorder(Color.BLACK));
                fieldPanel.add(cell);
            }
        }

        gameFrame.add(colourPanel);
        gameFrame.add(fieldPanel);
        gameFrame.add(backPanel);
        gameFrame.setVisible(true);
    }

    void chooseColour(int id){
        //colourPanel.
        for(JButton button : colourArray){
            button.setBackground(Color.WHITE);
        }
        colourArray.get(id).setBackground(Color.YELLOW);
        currentCell=id+1;
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
            //cell.setBackground(Color.WHITE);
            cell.setText("");
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
