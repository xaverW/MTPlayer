package de.p2tools.mtplayer.controller.data.bookmark;

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

import java.nio.file.Files;
import java.nio.file.Path;

public class BookmarkLoadSaveFactory {
    private BookmarkLoadSaveFactory() {
    }

    public static void loadList() {
        // beim Programmstart laden
        P2Duration.counterStart("loadList");

        if (!ProgConfig.SYSTEM_USE_NEW_BOOKMARK_FILE.get()) {
            // dann noch mit dem alten File versuchen
            ProgConfig.SYSTEM_USE_NEW_BOOKMARK_FILE.set(true);
            BookmarkFileFactoryOld.readBookmarkDataFromFileOld();
            saveBookmark();
            FileFactory.deleteHistoryFile(ProgConst.FILE_BOOKMARKS_TXT);

        } else {
            // dann schon das neue
            loadBookmarks();
        }

        ProgData.getInstance().bookmarkList.fillUrlHash();
        P2Duration.counterStop("loadList");
    }

    private static void loadBookmarks() {
        String settingsDir = ProgInfos.getSettingsDirectory_String();
        String fileName = ProgConst.FILE_BOOKMARKS_XML;
        final Path xmlFilePath = FileFactory.getUrlFilePath(settingsDir, fileName);

        try {
            if (!Files.exists(xmlFilePath)) {
                //dann gibts das File gar nicht
                return;
            }

            ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), false);
            configFile.addConfigs(ProgData.getInstance().bookmarkList);
            if (!ConfigReadFile.readConfig(configFile)) {
                P2Log.errorLog(959874512, "Bookmarks konnten nicht geladen werden");
            }

        } catch (final Exception ignore) {
        }
    }

    public static void saveBookmark() {
        P2Log.sysLog("Bookmarks sichern");

        String settingsDir = ProgInfos.getSettingsDirectory_String();
        String fileName = ProgConst.FILE_BOOKMARKS_XML;
        final Path xmlFilePath = FileFactory.getUrlFilePath(settingsDir, fileName);

        ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), false);
        configFile.addConfigs(ProgData.getInstance().bookmarkList);
        ConfigWriteFile.writeConfigFile(configFile);
    }
}
