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


public class Slider extends JSlider implements ChangeListener {

    MusicPlayer player;

    Slider(MusicPlayer player){
        setPreferredSize(new Dimension(400, 80));
        setPaintTicks(false);
        setPaintTrack(true);
        setOpaque(false);
        setMinorTickSpacing(10);
        setMajorTickSpacing(20);
        setPaintLabels(false);
        setBackground(Color.ORANGE);

        addChangeListener(this);
        this.player = player;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        player.changeVolume(getValue());
        Path path = Paths.get("src/main/resources/Preferences.json");
        String content;
        try {
            content = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JSONArray preferences = new JSONArray(content);

        JSONObject list = preferences.getJSONObject(0);
        list.put("volume", getValue());
        preferences.put(0, list);

        try {
            FileWriter writer = new FileWriter(
                    "src/main/resources/Preferences.json", false);
            writer.write(preferences.toString());
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    static class CustomSliderUI extends BasicSliderUI {

        private static final int TRACK_HEIGHT = 8;
        private static final int TRACK_WIDTH = 8;
        private static final int TRACK_ARC = 5;
        private static final Dimension THUMB_SIZE = new Dimension(20, 20);
        private final RoundRectangle2D.Float trackShape = new RoundRectangle2D.Float();

        public CustomSliderUI(final JSlider b) {
            super(b);
        }

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

        @Override
        protected void calculateThumbLocation() {
            super.calculateThumbLocation();
            if (isHorizontal()) {
                thumbRect.y = trackRect.y + (trackRect.height - thumbRect.height) / 2;
            } else {
                thumbRect.x = trackRect.x + (trackRect.width - thumbRect.width) / 2;
            }
        }

        @Override
        protected Dimension getThumbSize() {
            return THUMB_SIZE;
        }

        private boolean isHorizontal() {
            return slider.getOrientation() == JSlider.HORIZONTAL;
        }

        @Override
        public void paint(final Graphics g, final JComponent c) {
            ((Graphics2D) g).setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paint(g, c);
        }

        @Override
        public void paintTrack(final Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Shape clip = g2.getClip();

            boolean horizontal = isHorizontal();
            boolean inverted = slider.getInverted();

            g2.setColor(new Color(170, 170, 170));
            g2.fill(trackShape);

            g2.setColor(new Color(200, 200, 200));
            g2.setClip(trackShape);
            trackShape.y += 1;
            g2.fill(trackShape);
            trackShape.y = trackRect.y;

            g2.setClip(clip);

            if (horizontal) {
                boolean ltr = slider.getComponentOrientation().isLeftToRight();
                if (ltr) {
                    inverted = !inverted;
                }
                int thumbPos = thumbRect.x + thumbRect.width / 2;
                if (inverted) {
                    g2.clipRect(0, 0, thumbPos, slider.getHeight());
                } else {
                    g2.clipRect(thumbPos, 0,
                            slider.getWidth() - thumbPos, slider.getHeight());
                }

            } else {
                int thumbPos = thumbRect.y + thumbRect.height / 2;
                if (inverted) {
                    g2.clipRect(0, 0, slider.getHeight(), thumbPos);
                } else {
                    g2.clipRect(0, thumbPos,
                            slider.getWidth(), slider.getHeight() - thumbPos);
                }
            }
            g2.setColor(Color.decode("0xa1c1ff"));
            g2.fill(trackShape);
            g2.setClip(clip);
        }

        @Override
        public void paintThumb(final Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setColor(Color.decode("0x85A4E4"));
            g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);

            String value = String.valueOf(slider.getValue());
            g2.setFont(new Font("Arial", Font.BOLD, 15));
            g2.setColor(Color.BLACK);

            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                g2.drawString(value,
                        thumbRect.x + thumbRect.width + 5,
                        thumbRect.y + thumbRect.height / 2 - 10);
            } else {
                g2.drawString(value,
                        thumbRect.x + thumbRect.width / 2 - 5, thumbRect.y - 5);
            }

            g2.dispose();
        }

        @Override
        public void paintFocus(final Graphics g) {}
    }
}
