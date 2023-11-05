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


package de.p2tools.mtplayer.controller.history;

import de.p2tools.mtplayer.controller.PListener;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.beans.property.BooleanProperty;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class HistoryWriteToFile implements Runnable {


    private final List<HistoryData> list;
    private final boolean append;
    private final BooleanProperty isWorking;
    private final String settingsDir;
    private final String fileName;

    public HistoryWriteToFile(String settingsDir, String fileName, List<HistoryData> list,
                              boolean append, BooleanProperty isWorking) {
        this.settingsDir = settingsDir;
        this.fileName = fileName;
        this.list = list;
        this.append = append;
        this.isWorking = isWorking;
    }

    @Override
    public void run() {
        doWork();
        isWorking.setValue(false);
    }

    private void doWork() {
        final Path urlPath = HistoryFactory.getUrlFilePath(settingsDir, fileName);
        if (Files.notExists(urlPath)) {
            return;
        }

        PDuration.counterStart("doWork");
        if (append) {
            PLog.sysLog("An Historyliste anfügen: " + list.size() + ", Datei: " + fileName);
        } else {
            PLog.sysLog("Ganze Historyliste schreiben: " + list.size() + ", Datei: " + fileName);
        }

        // und jetzt schreiben
        writeHistoryDataToFile(list, append);
        list.clear(); // wird nicht mehr gebraucht

        PListener.notify(PListener.EVENT_HISTORY_CHANGED, HistoryWriteToFile.class.getSimpleName());
        PDuration.counterStop("doWork");
    }

    private boolean writeHistoryDataToFile(List<HistoryData> list, boolean append) {
        boolean ret = false;
        try (BufferedWriter bufferedWriter = (append ?
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(HistoryFactory.getUrlFilePath(settingsDir, fileName), StandardOpenOption.APPEND))) :
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(HistoryFactory.getUrlFilePath(settingsDir, fileName)))))
        ) {
            for (final HistoryData historyData : list) {
                final String line = HistoryFactory.getLine(historyData);
                bufferedWriter.write(line);
            }
            ret = true;
        } catch (final Exception ex) {
            PLog.errorLog(420312459, ex);
        }

        return ret;
    }
}
