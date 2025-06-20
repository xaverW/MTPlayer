/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */


package de.p2tools.mtplayer.controller.data.bookmark;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.tools.FileFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class BookmarkWriteToFile implements Runnable {

    public static final BooleanProperty isWorking = new SimpleBooleanProperty(false);

    private final List<BookmarkData> list;
    private final boolean append;
    private final String settingsDir;
    private final String fileName;

    public BookmarkWriteToFile(List<BookmarkData> list, boolean append) {
        this.settingsDir = ProgInfos.getSettingsDirectory_String();
        this.fileName = ProgConst.FILE_BOOKMARKS_TXT;
        this.list = list;
        this.append = append;
    }

    @Override
    public void run() {
        doWork();
        isWorking.setValue(false);
    }

    public static void waitWhileWorking() {
        while (isWorking.get()) {
            // sollte nicht passieren, aber wenn ..
            P2Log.errorLog(745845895, "waitWhileWorking: write to bookmark file");

            try {
                Thread.sleep(100);
            } catch (final Exception ex) {
                P2Log.errorLog(402154895, ex, "waitWhileWorking");
                isWorking.setValue(false);
            }
        }
    }

    private void doWork() {
        final Path urlPath = FileFactory.getUrlFilePath(settingsDir, fileName);
        if (Files.notExists(urlPath)) {
            return;
        }

        P2Duration.counterStart("doWork");
        if (append) {
            P2Log.sysLog("Bookmarks: An Liste anfügen: " + list.size() + ", Datei: " + fileName);
        } else {
            P2Log.sysLog("Ganze Bookmark-Liste schreiben: " + list.size() + ", Datei: " + fileName);
        }

        // und jetzt schreiben
        writeDataToFile(list, append);
        list.clear(); // wird nicht mehr gebraucht

        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_HISTORY_CHANGED);
        P2Duration.counterStop("doWork");
    }


    private void writeDataToFile(List<BookmarkData> list, boolean append) {
        try (BufferedWriter bufferedWriter = (append ?
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(FileFactory.getUrlFilePath(settingsDir, fileName), StandardOpenOption.APPEND))) :
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(FileFactory.getUrlFilePath(settingsDir, fileName)))))
        ) {
            for (final BookmarkData bookmarkData : list) {
                final String line = BookmarkFileFactory.getLine(bookmarkData);
                bufferedWriter.write(line);
            }
        } catch (final Exception ex) {
            P2Log.errorLog(420312459, ex);
        }
    }
}
