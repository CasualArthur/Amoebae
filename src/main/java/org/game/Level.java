package org.game;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Level {
    JSONArray levelsArray;
    ArrayList<RoundedButton> colourArray;
    ArrayList<ImageIcon> numbers;
    JSONObject level;
    int levelId;
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
    JPanel extraPanel;
    boolean completed;
    JWindow window;

    Level(int id) throws IOException {
        Path path = Paths.get("src/main/resources/Levels.json");
        String content = Files.readString(path, StandardCharsets.UTF_8);
        levelsArray = new JSONArray(content);
        numbers = new ArrayList<>();

        level = levelsArray.getJSONObject(id);
        levelId = level.getInt("id");
        size = level.getInt("size");
        tiles = level.getJSONArray("tiles");
        regions = level.getJSONArray("regions");
        fixed = level.getJSONArray("fixed");
        completed = level.getBoolean("completed");

        window = new JWindow(gameFrame);
        filledTiles = 0;
        currentCell = 1;
    }

    void display() throws IOException {
        gameFrame.setLayout(null);
        fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(size, size));
        fieldPanel.setBounds(107,89,350,350);
        fieldPanel.setOpaque(false);

        colourPanel = new JPanel();
        colourPanel.setLayout(new GridLayout(size, 1, 0, -1));
        colourPanel.setBounds(480, 89, 350/size, 350);
        colourPanel.setOpaque(false);

        extraPanel = new JPanel();
        extraPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        extraPanel.setBounds(107, 460, 350, 58);
        extraPanel.setOpaque(false);

        colourTheme = new ColourTheme();

        RoundedButton backButton = new RoundedButton();
        backButton.setRadius(20);
        backButton.setText("Back");

        backButton.setPreferredSize(new Dimension(140, 58));
        backButton.setBgColour(Color.WHITE);

        backButton.addActionListener(e -> {
            window.setVisible(false);
            gameFrame.displayLevels();
        });
        extraPanel.add(backButton);

        RoundedButton resetButton = new RoundedButton();
        resetButton.setRadius(20);
        resetButton.setPreferredSize(new Dimension(140, 58));
        resetButton.setText("Reset");
        resetButton.setBgColour(Color.WHITE);
        resetButton.addActionListener(e -> {
            try {
                window.setVisible(false);
                gameFrame.displayLevel(levelId);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        extraPanel.add(resetButton);


        colourArray = new ArrayList<>();

        for(int id = 1; id <= size; id++){
            RoundedButton palette = new RoundedButton();
            palette.setRadius(500);
            palette.setText(String.valueOf(id));
            if(id==1){
                palette.selected=true;
                palette.setBgColour(Color.ORANGE);

            }
            final int idx=id-1;
            if(idx%2==0) palette.setBgColour(Color.decode("0x85A4E4"));
            else palette.setBgColour(Color.WHITE);
            palette.setPreferredSize(new Dimension(350/size, 350/size));
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
                    cell.setEnabled(false);
                    cell.setText(String.valueOf(row.getInt(c)));
                }else{
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
        gameFrame.add(extraPanel);
        gameFrame.setVisible(true);
    }

    void chooseColour(int id){
        //colourPanel.
        for(RoundedButton button : colourArray){
            button.selected=false;
            button.setBorderColour(Color.GRAY);
        }
        colourArray.get(id).selected=true;
        colourArray.get(id).setBorderColour(Color.ORANGE);
        colourPanel.repaint();
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
            cell.setText("");
            filledTiles--;
            tiles.getJSONArray(r).put(c, 0);
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
            int id = levelId;
            level.put("completed", true);
            levelsArray.put(id, level);
            FileWriter writer = new FileWriter("src/main/resources/Levels.json", false);
            writer.write(levelsArray.toString());
            writer.close();
        }

        catch(IOException ie) {
            ie.printStackTrace();
        }

        JPanel panel = new JPanel();
        JLabel salut = new JLabel("Level complete!");
        salut.setFont(new Font("Calibri", Font.PLAIN, 25));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setBackground(Color.WHITE);
        panel.add(salut);
        window.add(panel);

        window.setVisible(true);
        window.setBounds(150, 188, 276, 200);
        window.setLocationRelativeTo(gameFrame);

    }
}
