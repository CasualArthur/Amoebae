package org.game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.io.*;

class MusicPlayer {

    Clip clip;
    void playMusic(){
        try  {
            InputStream is = GameFrame.class.getClassLoader().getResourceAsStream("PlaceholderSong.wav");
            if (is == null) {
                System.err.println("PlaceholderSong.wav not found");
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(is);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-80.0f);
            clip.start();
            System.out.println(clip.getMicrosecondLength());
            Thread.sleep(clip.getMicrosecondLength() / 1000);
            clip.addLineListener(_ -> {
                try {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    clip.start();
                    Thread.sleep(clip.getMicrosecondLength() / 1000);
                }
                catch (Exception e)  {
                    e.printStackTrace();
                }
            });

        }
        catch (Exception e)  {
            e.printStackTrace();
        }
    }
    void changeVolume(float volumePercent){
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        volumePercent = Math.max(0, Math.min(volumePercent, 100));

        if (volumePercent == 0) {
            gainControl.setValue(gainControl.getMinimum()); // Silence
        } else {
            float dB = (float) (20.0 * Math.log10(volumePercent / 100.0));

            dB = Math.max(dB, gainControl.getMinimum());
            gainControl.setValue(dB);
        }
    }
}