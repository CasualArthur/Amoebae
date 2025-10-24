package org.game;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class RoundedButton extends JButton {

    boolean selected;
    private Color borderColour;
    private int radius = 0;
    Color bgColour;

    public void setBorderColour(Color borderColour) {
        this.borderColour = borderColour;
    }

    public void setBgColour(Color colour){
        setBackground(colour);
        this.bgColour = colour;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public RoundedButton() {
        setFont(new Font("Calibri", Font.PLAIN, 25));
        setHorizontalTextPosition(JButton.CENTER);
        setVerticalTextPosition(JButton.CENTER);
        setPreferredSize(new Dimension(58, 58));
        setBackground(Color.WHITE);
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);
        selected = false;

        borderColour = Color.ORANGE;
        setContentAreaFilled(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {

                setBackground(Color.decode("0x88F18B"));
                repaint();

            }

            @Override
            public void mouseExited(MouseEvent me) {
                if (selected) {
                    borderColour = Color.ORANGE;
                }
                setBackground(bgColour);
                repaint();

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