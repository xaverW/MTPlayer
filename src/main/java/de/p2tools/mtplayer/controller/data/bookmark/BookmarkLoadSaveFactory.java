package de.p2tools.mtplayer.controller.data.bookmark;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.configfile.ConfigReadFile;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;

import java.nio.file.Files;
import java.nio.file.Path;

public class BookmarkLoadSaveFactory {
    private BookmarkLoadSaveFactory() {
    }

    public static void updateOldBookmarks() {
        String settingsDir = ProgInfos.getSettingsDirectory_String();
        BookmarkList list = new BookmarkList();
        String fileNameXml = ProgConst.FILE_BOOKMARKS_XML;
        String fileNameTxt = ProgConst.FILE_BOOKMARKS_TXT;

        final Path txtFilePath = Path.of(settingsDir, fileNameTxt);
        final Path xmlFilePath = Path.of(settingsDir, fileNameXml);

        // Und jetzt die alten noch löschen
        try {
            if (Files.exists(txtFilePath)) {
                Files.delete(txtFilePath);
            }
        } catch (Exception ex) {
            P2Log.errorLog(623232321, ex.getMessage());
        }


        try {
            if (!Files.exists(xmlFilePath) || xmlFilePath.toFile().length() == 0) {
                return;
            }

            ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), false);
            configFile.addConfigs(list);
            if (!ConfigReadFile.readConfig(configFile)) {
                P2Log.errorLog(959874512, "Bookmarks konnten nicht geladen werden");
            }

        } catch (final Exception ignore) {
        }

        BookmarkReadWriteJsonFactory.write(list);
        list.clearList();

        // Und jetzt die alten noch löschen
        try {
            if (Files.exists(xmlFilePath)) {
                Files.delete(xmlFilePath);
            }
        } catch (Exception ex) {
            P2Log.errorLog(623232321, ex.getMessage());
        }
    }

    public static void loadList() {
        // beim Programmstart laden
        P2Duration.counterStart("loadList");

//        if (!ProgConfig.SYSTEM_USE_NEW_BOOKMARK_FILE.get()) {
//            // dann noch mit dem alten File versuchen
//            ProgConfig.SYSTEM_USE_NEW_BOOKMARK_FILE.set(true);
//            BookmarkFileFactoryOld.readBookmarkDataFromFileOld();
//            saveBookmark();
//            FileFactory.deleteHistoryFile(ProgConst.FILE_BOOKMARKS_TXT);
//
//        } else {
        // dann schon das neue
        loadBookmarks();
//        }

        ProgData.getInstance().bookmarkList.fillUrlHash();
        P2Duration.counterStop("loadList");
    }

    private static void loadBookmarks() {
        BookmarkReadWriteJsonFactory.read();

//        String settingsDir = ProgInfos.getSettingsDirectory_String();
//        String fileName = ProgConst.FILE_BOOKMARKS_XML;
//        final Path xmlFilePath = FileFactory.getUrlFilePath(settingsDir, fileName);
//
//        try {
//            if (!Files.exists(xmlFilePath) || xmlFilePath.toFile().length() == 0) {
//                return;
//            }
//
//            ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), false);
//            configFile.addConfigs(ProgData.getInstance().bookmarkList);
//            if (!ConfigReadFile.readConfig(configFile)) {
//                P2Log.errorLog(959874512, "Bookmarks konnten nicht geladen werden");
//            }
//
//        } catch (final Exception ignore) {
//        }
    }

    public static void saveBookmark() {
        P2Log.sysLog("Bookmarks sichern");
        BookmarkReadWriteJsonFactory.write();

//        String settingsDir = ProgInfos.getSettingsDirectory_String();
//        String fileName = ProgConst.FILE_BOOKMARKS_XML;
//        final Path xmlFilePath = FileFactory.getUrlFilePath(settingsDir, fileName);
//
//        ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), false);
//        configFile.addConfigs(ProgData.getInstance().bookmarkList);
//        ConfigWriteFile.writeConfigFile(configFile);
    }
}
