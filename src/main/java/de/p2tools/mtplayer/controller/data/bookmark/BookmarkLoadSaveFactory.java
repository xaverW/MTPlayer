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

        // Und jetzt die ganz alten löschen
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

    public static void loadBookmark() {
        // beim Programmstart laden
        BookmarkReadWriteJsonFactory.read();
        ProgData.getInstance().bookmarkList.fillUrlHash();
        P2Duration.counterStop("loadList");
    }

    public static void saveBookmark() {
        P2Log.sysLog("Bookmarks sichern");
        BookmarkReadWriteJsonFactory.write();
    }
}
