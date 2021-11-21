/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
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

package de.p2tools.mtplayer.controller;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProgSave {
    final ProgData progData;
    private boolean alreadyMadeBackup = false;
    private boolean open = true;

    public ProgSave() {
        progData = ProgData.getInstance();
    }

    public void saveAll() {
        copyConfig();
        ProgSaveFactory.saveProgConfig();

        if (ProgData.reset) {
            reset();
        }
    }

    private void reset() {
        // das Programm soll beim nächsten Start mit den Standardeinstellungen gestartet werden
        // dazu wird den Ordner mit den Einstellungen umbenannt
        try {
            PLog.sysLog("Programm reset: Start Pfad umbenennen");
            PLogger.removeFileHandler(); // sonst mault Windows

            String dir1 = ProgInfos.getSettingsDirectory_String();
            if (dir1.endsWith(File.separator)) {
                dir1 = dir1.substring(0, dir1.length() - 1);
            }

            final Path path1 = Paths.get(dir1);
            final String dir2 = dir1 + "--" + FastDateFormat.getInstance("yyyy.MM.dd__HH.mm.ss").format(new Date());
            PLog.sysLog("Pfad verschieben: " + dir1);
            PLog.sysLog("  nach: " + dir2);

            Files.move(path1, Paths.get(dir2), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(path1);
            PLog.sysLog("  moved :)");

        } catch (final Exception ex) {
            PLog.errorLog(912012014, ex, "Die Einstellungen konnten nicht zurückgesetzt werden.");
            try {
                PAlert.showErrorAlert("Fehler", "Einstellungen zurückgesetzen",
                        "Die Einstellungen konnten nicht zurückgesetzt werden." + P2LibConst.LINE_SEPARATORx2
                                + "Sie müssen jetzt das Programm beenden, dann den Ordner:" + P2LibConst.LINE_SEPARATORx2
                                + ProgInfos.getSettingsDirectory_String()
                                + P2LibConst.LINE_SEPARATORx2
                                + "von Hand löschen und das Programm wieder starten.");
                open = false;
            } catch (Exception ignore) {
                open = false;
            }
            while (open) {
                try {
                    wait(100);
                } catch (final Exception ignored) {
                }
            }
        }

    }

    /**
     * Create backup copies of settings file.
     */
    private void copyConfig() {
        if (alreadyMadeBackup) {
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        // nur einmal pro Programmstart machen
        list.add(PLog.LILNE3);
        list.add("Einstellungen sichern");

        try {
            final Path xmlFilePath = new ProgInfos().getSettingsFile();
            long creatTime = -1;

            Path xmlFilePathCopy_1 = ProgInfos.getSettingsDirectory().resolve(ProgConst.CONFIG_FILE_COPY + 1);
            if (Files.exists(xmlFilePathCopy_1)) {
                final BasicFileAttributes attrs = Files.readAttributes(xmlFilePathCopy_1, BasicFileAttributes.class);
                final FileTime d = attrs.lastModifiedTime();
                creatTime = d.toMillis();
            }

            if (creatTime == -1 || creatTime < getToday_0_0()) {
                // nur dann ist die letzte Kopie älter als einen Tag
                for (int i = ProgConst.MAX_COPY_OF_BACKUPFILE; i > 1; --i) {
                    xmlFilePathCopy_1 = ProgInfos.getSettingsDirectory().resolve(ProgConst.CONFIG_FILE_COPY + (i - 1));
                    final Path xmlFilePathCopy_2 = ProgInfos.getSettingsDirectory().resolve(ProgConst.CONFIG_FILE_COPY + i);
                    if (Files.exists(xmlFilePathCopy_1)) {
                        Files.move(xmlFilePathCopy_1, xmlFilePathCopy_2, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                if (Files.exists(xmlFilePath)) {
                    Files.move(xmlFilePath,
                            ProgInfos.getSettingsDirectory().resolve(ProgConst.CONFIG_FILE_COPY + 1),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                list.add("Einstellungen wurden gesichert");
            } else {
                list.add("Einstellungen wurden heute schon gesichert");
            }
        } catch (final IOException e) {
            list.add("Die Einstellungen konnten nicht komplett gesichert werden!");
            PLog.errorLog(795623147, e);
        }

        alreadyMadeBackup = true;
        list.add(PLog.LILNE3);
        PLog.sysLog(list);
    }


    /**
     * Return the number of milliseconds from today´s midnight.
     *
     * @return Number of milliseconds from today´s midnight.
     */
    private long getToday_0_0() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }
}
