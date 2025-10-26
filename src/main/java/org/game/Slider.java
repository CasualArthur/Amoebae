package org.game;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A custom slider component for controlling music volume.
 * Automatically updates the music player and saves volume preferences when changed.
 */
public class Slider extends JSlider implements ChangeListener {

    MusicPlayer musicPlayer;

    /**
     * Constructs a Slider for volume control.
     *
     * @param musicPlayer The music player instance to control
     */
    Slider(MusicPlayer musicPlayer) {
        setPreferredSize(new Dimension(400, 80));
        setPaintTicks(false);
        setPaintTrack(true);
        setOpaque(false);
        setMinorTickSpacing(10);
        setMajorTickSpacing(20);
        setPaintLabels(false);
        setBackground(Color.ORANGE);

        addChangeListener(this);
        this.musicPlayer = musicPlayer;
    }

    /**
     * Handles slider value changes.
     * Updates the music player volume and saves the new value to preferences.
     *
     * @param event The change event from the slider
     */
    @Override
    public void stateChanged(ChangeEvent event) {
        // Update music player volume
        musicPlayer.changeVolume(getValue());

        // Load current preferences
        Path preferencesPath = Paths.get("src/main/resources/Preferences.json");
        String preferencesContent;
        try {
            preferencesContent = Files.readString(preferencesPath, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JSONArray preferences = new JSONArray(preferencesContent);

        // Update volume in preferences
        JSONObject preferencesObject = preferences.getJSONObject(0);
        preferencesObject.put("volume", getValue());
        preferences.put(0, preferencesObject);

        // Save updated preferences to file
        try {
            FileWriter writer = new FileWriter(
                    "src/main/resources/Preferences.json", false);
            writer.write(preferences.toString());
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Custom UI for the slider with rounded track and styled thumb.
     * Provides a modern look with color-coded progress indication.
     */
    static class CustomSliderUI extends BasicSliderUI {

        private static final int TRACK_HEIGHT = 8;
        private static final int TRACK_WIDTH = 8;
        private static final int TRACK_ARC = 5;
        private static final Dimension THUMB_SIZE = new Dimension(20, 20);
        private final RoundRectangle2D.Float trackShape = new RoundRectangle2D.Float();

        /**
         * Constructs the custom slider UI.
         *
         * @param slider The JSlider to apply this UI to
         */
        public CustomSliderUI(final JSlider slider) {
            super(slider);
        }

        /**
         * Calculates the track rectangle dimensions and position.
         * Centers the track within the component bounds.
         */
        @Override
        protected void calculateTrackRect() {
            super.calculateTrackRect();
            if (isHorizontal()) {
                trackRect.y = trackRect.y + (trackRect.height - TRACK_HEIGHT) / 2;
                trackRect.height = TRACK_HEIGHT;
            } else {
                trackRect.x = trackRect.x + (trackRect.width - TRACK_WIDTH) / 2;
                trackRect.width = TRACK_WIDTH;
            }
            trackShape.setRoundRect(trackRect.x, trackRect.y,
                    trackRect.width, trackRect.height, TRACK_ARC, TRACK_ARC);
        }

        /**
         * Calculates the thumb position.
         * Centers the thumb vertically (for horizontal sliders) or horizontally (for vertical).
         */
        @Override
        protected void calculateThumbLocation() {
            super.calculateThumbLocation();
            if (isHorizontal()) {
                thumbRect.y = trackRect.y + (trackRect.height - thumbRect.height) / 2;
            } else {
                thumbRect.x = trackRect.x + (trackRect.width - thumbRect.width) / 2;
            }
        }

        /**
         * Returns the size of the thumb (draggable part).
         *
         * @return The dimensions of the thumb
         */
        @Override
        protected Dimension getThumbSize() {
            return THUMB_SIZE;
        }

        /**
         * Checks if the slider is oriented horizontally.
         *
         * @return true if horizontal, false if vertical
         */
        private boolean isHorizontal() {
            return slider.getOrientation() == JSlider.HORIZONTAL;
        }

        /**
         * Paints the entire slider with antialiasing enabled.
         *
         * @param g Graphics context
         * @param component The component being painted
         */
        @Override
        public void paint(final Graphics g, final JComponent component) {
            ((Graphics2D) g).setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paint(g, component);
        }

        /**
         * Paints the slider track with colour-coded progress.
         * The filled portion shows progress in blue, unfilled in grey.
         *
         * @param g Graphics context
         */
        @Override
        public void paintTrack(final Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Shape originalClip = g2.getClip();

            boolean horizontal = isHorizontal();
            boolean inverted = slider.getInverted();

            // Draw base track (grey)
            g2.setColor(new Color(170, 170, 170));
            g2.fill(trackShape);

            // Draw subtle highlight
            g2.setColor(new Color(200, 200, 200));
            g2.setClip(trackShape);
            trackShape.y += 1;
            g2.fill(trackShape);
            trackShape.y = trackRect.y;

            g2.setClip(originalClip);

            // Draw filled portion of track
            if (horizontal) {
                boolean leftToRight = slider.getComponentOrientation().isLeftToRight();
                if (leftToRight) {
                    inverted = !inverted;
                }
                int thumbPosition = thumbRect.x + thumbRect.width / 2;
                if (inverted) {
                    g2.clipRect(0, 0, thumbPosition, slider.getHeight());
                } else {
                    g2.clipRect(thumbPosition, 0,
                            slider.getWidth() - thumbPosition, slider.getHeight());
                }
            } else {
                int thumbPosition = thumbRect.y + thumbRect.height / 2;
                if (inverted) {
                    g2.clipRect(0, 0, slider.getHeight(), thumbPosition);
                } else {
                    g2.clipRect(0, thumbPosition,
                            slider.getWidth(), slider.getHeight() - thumbPosition);
                }
            }

            // Draw coloured progress portion
            g2.setColor(Color.decode("0xa1c1ff"));
            g2.fill(trackShape);
            g2.setClip(originalClip);
        }

        /**
         * Paints the thumb (draggable knob) with current value displayed.
         *
         * @param g Graphics context
         */
        @Override
        public void paintThumb(final Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Draw circular thumb
            g2.setColor(Color.decode("0x85A4E4"));
            g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);

            // Draw current value next to thumb
            String valueText = String.valueOf(slider.getValue());
            g2.setFont(new Font("Arial", Font.BOLD, 15));
            g2.setColor(Color.BLACK);

            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                g2.drawString(valueText,
                        thumbRect.x + thumbRect.width + 5,
                        thumbRect.y + thumbRect.height / 2 - 10);
            } else {
                g2.drawString(valueText,
                        thumbRect.x + thumbRect.width / 2 - 5, thumbRect.y - 5);
            }

            g2.dispose();
        }

        /**
         * Overrides focus painting (not needed for this slider).
         *
         * @param g Graphics context
         */
        @Override
        public void paintFocus(final Graphics g) {
            // No focus indicator needed
        }
    }
}