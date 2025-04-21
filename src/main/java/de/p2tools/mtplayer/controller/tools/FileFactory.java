package de.p2tools.mtplayer.controller.tools;

import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileFactory {
    private FileFactory() {
    }

    public static synchronized Path getUrlFilePath(String settingsDir, String fileName) {
        Path urlPath = null;
        try {
            urlPath = Paths.get(settingsDir).resolve(fileName);
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            P2Log.errorLog(915478960, ex);
        }
        return urlPath;
    }

    public static synchronized void deleteHistoryFile(String fileName) {
        try {
            final Path urlPath = getUrlFilePath(ProgInfos.getSettingsDirectory_String(), fileName);
            Files.deleteIfExists(urlPath);
        } catch (final IOException ignored) {
        }
    }
}
