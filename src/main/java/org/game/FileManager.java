package org.game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    static Path getUserDataPath(String filename) {
        String userHome = System.getProperty("user.home");
        Path gameDataDir = Paths.get(userHome, ".amoebae"); // Hidden folder

        try {
            Files.createDirectories(gameDataDir);
            Path targetFile = gameDataDir.resolve(filename);

            // Copy default from resources if doesn't exist
            if (!Files.exists(targetFile)) {
                try (InputStream is = ColourTheme.class.getResourceAsStream("/" + filename)) {
                    if (is != null) {
                        Files.copy(is, targetFile);
                    }
                }
            }
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
            return Paths.get("src/main/resources/" + filename); // Fallback
        }
    }
}
