package org.game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.*;

/**
 * Manages background music playback for the game.
 * Handles loading, playing, looping, and volume control of audio.
 */
class MusicPlayer {

    private Clip audioClip;

    /**
     * Loads and plays the background music track.
     * The music will loop continuously after the initial playthrough.
     * Starts with volume set to minimum (-80dB) by default.
     */
    void playMusic() {
        try {
            // Load audio file from resources
            InputStream audioInputStream = GameFrame.class.getClassLoader()
                    .getResourceAsStream("PlaceholderSong.wav");

            if (audioInputStream == null) {
                System.err.println("PlaceholderSong.wav not found");
                return;
            }

            // Create audio input stream and clip
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioInputStream);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);

            // Set initial volume to minimum
            FloatControl volumeControl =
                    (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-80.0f);

            // Start playback
            audioClip.start();
            System.out.println(audioClip.getMicrosecondLength());
            Thread.sleep(audioClip.getMicrosecondLength() / 1000);

            // Add listener to loop music continuously
            audioClip.addLineListener(e -> {
                try {
                    audioClip.loop(Clip.LOOP_CONTINUOUSLY);
                    audioClip.start();
                    Thread.sleep(audioClip.getMicrosecondLength() / 1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Adjusts the volume of the currently playing music.
     * Converts percentage-based volume to decibel scale for audio control.
     *
     * @param volumePercentage The desired volume level (0-100, where 0 is mute and 100 is maximum)
     */
    void changeVolume(float volumePercentage) {
        FloatControl volumeControl =
                (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);

        // Clamp volume to valid range
        volumePercentage = Math.max(0, Math.min(volumePercentage, 100));

        if (volumePercentage == 0) {
            // Mute the audio
            volumeControl.setValue(volumeControl.getMinimum());
        } else {
            // Convert percentage to decibels (logarithmic scale)
            float volumeInDecibels = (float) (20.0 * Math.log10(volumePercentage / 100.0));

            // Ensure decibel value is within valid range
            volumeInDecibels = Math.max(volumeInDecibels, volumeControl.getMinimum());
            volumeControl.setValue(volumeInDecibels);
        }
    }
}