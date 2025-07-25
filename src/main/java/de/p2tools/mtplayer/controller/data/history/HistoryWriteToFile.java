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


package de.p2tools.mtplayer.controller.data.history;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.tools.FileFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HistoryWriteToFile implements Runnable {


    private final List<HistoryData> list;
    private final boolean append;
    private final AtomicBoolean isWorking;
    private final String settingsDir;
    private final String fileName;

    public HistoryWriteToFile(String settingsDir, String fileName, List<HistoryData> list,
                              boolean append, AtomicBoolean isWorking) {
        this.settingsDir = settingsDir;
        this.fileName = fileName;
        this.list = list;
        this.append = append;
        this.isWorking = isWorking;
    }

    @Override
    public void run() {
        try {
            doWork();
        } catch (Exception ignore) {
        }
        isWorking.set(false);
    }

    private void doWork() {
        final Path urlPath = FileFactory.getUrlFilePath(settingsDir, fileName);
        if (Files.notExists(urlPath)) {
            return;
        }

        P2Duration.counterStart("doWork");
        if (append) {
            P2Log.sysLog("An Historyliste anfügen: " + list.size() + ", Datei: " + fileName);
        } else {
            P2Log.sysLog("Ganze Historyliste schreiben: " + list.size() + ", Datei: " + fileName);
        }

        // und jetzt schreiben
        writeHistoryDataToFile(list, append);
        list.clear(); // wird nicht mehr gebraucht

        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_HISTORY_CHANGED);
        P2Duration.counterStop("doWork");
    }

    private void writeHistoryDataToFile(List<HistoryData> list, boolean append) {
        try (BufferedWriter bufferedWriter = (append ?
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(FileFactory.getUrlFilePath(settingsDir, fileName), StandardOpenOption.APPEND))) :
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(FileFactory.getUrlFilePath(settingsDir, fileName)))))
        ) {
            for (final HistoryData historyData : list) {
                final String line = HistoryFactory.getLine(historyData);
                bufferedWriter.write(line);
            }
        } catch (final Exception ex) {
            P2Log.errorLog(420312459, ex);
        }
    }
}
