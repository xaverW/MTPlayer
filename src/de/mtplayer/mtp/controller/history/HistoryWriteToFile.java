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


package de.mtplayer.mtp.controller.history;

import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
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
    private final HistoryWorker historyWorker;

    public HistoryWriteToFile(List<HistoryData> list, boolean append, BooleanProperty isWorking, HistoryWorker historyWorker) {
        this.list = list;
        this.append = append;
        this.isWorking = isWorking;
        this.historyWorker = historyWorker;
    }

    public void run() {
        doWork();
        isWorking.setValue(false);
    }

    private void doWork() {
        final Path urlPath = historyWorker.getUrlFilePath();
        if (Files.notExists(urlPath)) {
            return;
        }

        PDuration.counterStart("History: Thread: HistoryWriteToFile");
        if (append) {
            PLog.sysLog("An Historyliste anfügen: " + list.size() + ", Datei: " + historyWorker.getFileName());
        } else {
            PLog.sysLog("Ganze Historyliste schreiben: " + list.size() + ", Datei: " + historyWorker.getFileName());
        }

        // und jetzt schreiben
        writeHistoryDataToFile(list, append);

        Listener.notify(Listener.EREIGNIS_GUI_HISTORY_CHANGED, HistoryWriteToFile.class.getSimpleName());
        PDuration.counterStop("History: Thread: HistoryWriteToFile");
    }

    private boolean writeHistoryDataToFile(List<HistoryData> list, boolean append) {
        boolean ret = false;
        try (BufferedWriter bufferedWriter = (append ?
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(historyWorker.getUrlFilePath(), StandardOpenOption.APPEND))) :
                new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(historyWorker.getUrlFilePath()))))
        ) {

            for (final HistoryData historyData : list) {
                final String line = historyData.getLine();
                bufferedWriter.write(line);
            }
            ret = true;

        } catch (final Exception ex) {
            PLog.errorLog(420312459, ex);
        }

        return ret;
    }
}
