/*
 * P2tools Copyright (C) 2020 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.mediaDb;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.BooleanProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExportMediaDB implements Runnable {

    private final List<MediaData> list;
    private final BooleanProperty isWorking;
    private final Path path;
    private int export;

    public ExportMediaDB(List<MediaData> list, BooleanProperty isWorking, Path path, int export) {
        this.list = list;
        this.isWorking = isWorking;
        this.path = path;
        this.export = export;
    }

    public void run() {
        doWork();
        isWorking.setValue(false);
    }

    private void doWork() {
        PDuration.counterStart("MediaData: Thread: ExportMediaDB");
        PLog.sysLog("MedienDB in Textdatei schreiben: " + list.size() + ", Datei: " + path.toString());

        // und jetzt schreiben
        writeMediaDataToFile(list);

        PDuration.counterStop("MediaData: Thread: ExportMediaDB");
    }

    private boolean writeMediaDataToFile(List<MediaData> list) {
        boolean ret = false;
        try (BufferedWriter bufferedWriter = (new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(path))))) {

            for (final MediaData mediaData : list) {
                final String line = mediaData.getPath() + File.separator + mediaData.getName();

                if (export == ProgConst.MEDIA_COLLECTION_EXPORT_INTERN &&
                        !mediaData.isExternal()) {
                    bufferedWriter.write(line);
                    bufferedWriter.write(P2LibConst.LINE_SEPARATOR);

                } else if (export == ProgConst.MEDIA_COLLECTION_EXPORT_EXTERN &&
                        mediaData.isExternal()) {
                    bufferedWriter.write(line);
                    bufferedWriter.write(P2LibConst.LINE_SEPARATOR);

                } else if (export == ProgConst.MEDIA_COLLECTION_EXPORT_INTERN_EXTERN) {
                    bufferedWriter.write(line);
                    bufferedWriter.write(P2LibConst.LINE_SEPARATOR);
                }
            }
            ret = true;

        } catch (final Exception ex) {
            PLog.errorLog(742590235, ex);
        }

        return ret;
    }

}

