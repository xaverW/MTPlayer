package de.p2tools.mtplayer.controller.data.bookmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.tools.FileFactory;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.configfile.ConfigReadFile;
import de.p2tools.p2lib.configfile.ConfigWriteFile;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BookmarkLoadSave {
    private BookmarkLoadSave() {
    }

    public static void loadList() {
        // beim Programmstart laden
        P2Duration.counterStart("loadList");

        if (!ProgConfig.SYSTEM_USE_NEW_BOOKMARK_FILE.get()) {
            // dann noch mit dem alten File versuchen
            ProgConfig.SYSTEM_USE_NEW_BOOKMARK_FILE.set(true);
            BookmarkFileFactory.readBookmarkDataFromFileOld();
            saveBookmark();
            FileFactory.deleteHistoryFile(ProgConst.FILE_BOOKMARKS_TXT);

        } else {
            // dann schon das neue
            loadBookmarks();
        }

        ProgData.getInstance().bookmarkList.fillUrlHash();
        P2Duration.counterStop("loadList");
    }

    private static boolean loadBookmarks() {
        String settingsDir = ProgInfos.getSettingsDirectory_String();
        String fileName = ProgConst.FILE_BOOKMARKS_XML;
        final Path xmlFilePath = FileFactory.getUrlFilePath(settingsDir, fileName);

        try {
            if (!Files.exists(xmlFilePath)) {
                //dann gibts das File gar nicht
                return false;
            }

            ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), true);
            configFile.addConfigs(ProgData.getInstance().bookmarkList);
            boolean ok = ConfigReadFile.readConfig(configFile);

            if (ok) {
                return true;

            } else {
                // dann hat das Laden nicht geklappt
                return false;
            }
        } catch (final Exception ex) {
        }
        return false;
    }

    public static void saveBookmark() {
//        saveBookmarkJson();

        P2Log.sysLog("Bookmarks sichern");

        String settingsDir = ProgInfos.getSettingsDirectory_String();
        String fileName = ProgConst.FILE_BOOKMARKS_XML;
        final Path xmlFilePath = FileFactory.getUrlFilePath(settingsDir, fileName);

        ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), true);
        configFile.addConfigs(ProgData.getInstance().bookmarkList);
        ConfigWriteFile.writeConfigFile(configFile);
    }

    public static void saveBookmarkJson() {
        ObjectMapper om = new ObjectMapper();
        try {
            // create a file object
            String settingsDir = ProgInfos.getSettingsDirectory_String();
            String fileName = ProgConst.FILE_BOOKMARKS_XML;

            File file = FileFactory.getUrlFilePath(settingsDir, fileName + ".json").toFile();
            om.writeValue(file, new BookmarkData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
