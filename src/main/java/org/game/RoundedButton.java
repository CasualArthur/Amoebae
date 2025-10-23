package org.game;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class RoundedButton extends JButton {

    private boolean over;
    boolean selected;
    private Color colour;
    private Color colourOver;
    private Color colourClick;
    private Color borderColour;
    private int radius = 0;
    Color bgColour;

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
        setBackground(colour);
    }

    public Color getColourOver() {
        return colourOver;
    }

    public void setColourOver(Color colourOver) {
        this.colourOver = colourOver;
    }

    public Color getColourClick() {
        return colourClick;
    }

    public void setColourClick(Color colourClick) {
        this.colourClick = colourClick;
    }

    public Color getBorderColour() {
        return borderColour;
    }

    public void setBorderColour(Color borderColour) {
        this.borderColour = borderColour;
    }

    public void setBgColour(Color colour){
        setBackground(colour);
        this.bgColour = colour;
    }

    public Color getBgColour(){
        return this.bgColour;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public RoundedButton() {
        //  Init Color
        setFont(new Font("Calibri", Font.PLAIN, 25));
        setHorizontalTextPosition(JButton.CENTER);
        setVerticalTextPosition(JButton.CENTER);
        setPreferredSize(new Dimension(58, 58));
        //setColour(Color.LIGHT_GRAY);
        setBackground(Color.WHITE);
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);
        selected = false;

        colourOver = Color.GREEN;
        colourClick = Color.ORANGE;
        borderColour = Color.ORANGE;
        setContentAreaFilled(false);
        //  Add event mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                //borderColour=colourOver;
                setBackground(Color.decode("0x88F18B"));
                repaint();
                over = true;
            }

            @Override
            public void mouseExited(MouseEvent me) {
                if (selected) {
                    borderColour = Color.ORANGE;
                }
                setBackground(bgColour);
                repaint();
                over = false;

            }
        });
    }



    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(borderColour);
        if (selected) {
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
        g2.setColor(getBackground());

        g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, radius, radius);
        super.paintComponent(g);
    }
}