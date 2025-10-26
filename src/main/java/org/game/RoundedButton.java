package org.game;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * A custom JButton with rounded corners and hover effects.
 * Supports selection state with a colored border and customizable corner radius.
 */
public class RoundedButton extends JButton {

    boolean selected;
    Color borderColor;
    int cornerRadius = 0;
    Color backgroundColour;

    /**
     * Sets the border colour for the button.
     * The border is only visible when the button is in selected state.
     *
     * @param borderColour The colour to use for the button border
     */
    public void setBorderColour(Color borderColour) {
        this.borderColor = borderColour;
    }

    /**
     * Sets the background colour of the button.
     * Also stores this as the default color to return to after hover.
     *
     * @param colour The background colour to apply
     */
    public void setBgColour(Color colour) {
        setBackground(colour);
        this.backgroundColour = colour;
    }

    /**
     * Sets the corner radius for rounded edges.
     * Higher values create more rounded corners.
     *
     * @param radius The radius in pixels for corner rounding
     */
    public void setRadius(int radius) {
        this.cornerRadius = radius;
    }

    /**
     * Constructs a RoundedButton with default styling.
     * Sets up font, size, colours, and mouse hover behavior.
     */
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

        borderColor = Color.ORANGE;
        setContentAreaFilled(false);

        // Add hover effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                // Change to green on hover
                setBackground(Color.decode("0x88F18B"));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                // Restore border color if selected
                if (selected) {
                    borderColor = Color.ORANGE;
                }
                // Restore original background colour
                setBackground(backgroundColour);
                repaint();
            }
        });
    }

    /**
     * Custom painting for rounded button with border.
     * Draws a coloured border when selected, then fills the interior.
     *
     * @param g The Graphics context to paint on
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw border if selected
        g2.setColor(borderColor);
        if (selected) {
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }

        // Draw button background (slightly inset to show border)
        g2.setColor(getBackground());
        g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, cornerRadius, cornerRadius);

        super.paintComponent(g);
    }
}