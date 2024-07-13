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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.configfile.ConfigWriteFile;
import de.p2tools.p2lib.tools.log.P2Log;
import de.p2tools.p2lib.tools.log.P2Logger;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

public class ProgSave {
    private static boolean open = true;

    private ProgSave() {
    }

    public static void saveAll() {
        saveAll(true);
    }

    public static void saveAll(boolean busy) {
        // nach dem Filmliste laden: Plattform.runLater...
        // aus dem Debug-Menü, nach dem Laden der Filmliste, aus den Einstellungen/ShutDownDialog: Beenden "Testen"
        // DownloadAddDialog, AboAddDialog
        if (busy)
            ProgData.busy.busyOn(Busy.BUSY_SRC.GUI, "Speichern:", -1.0, false);

        P2Log.sysLog("Alle Programmeinstellungen sichern");
        final Path xmlFilePath = ProgInfos.getSettingsFile();
        ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), true);
        ProgConfig.addConfigData(configFile, true);
        ConfigWriteFile.writeConfigFile(configFile);

        if (ProgData.reset) {
            reset();
        }

        if (busy)
            ProgData.busy.busyOffFx();
    }


    private static void reset() {
        // das Programm soll beim nächsten Start mit den Standardeinstellungen gestartet werden
        // dazu wird den Ordner mit den Einstellungen umbenannt
        try {
            P2Log.sysLog("Programm reset: Start Pfad umbenennen");
            P2Logger.removeFileHandler(); // sonst mault Windows

            String dir1 = ProgInfos.getSettingsDirectory_String();
            if (dir1.endsWith(File.separator)) {
                dir1 = dir1.substring(0, dir1.length() - 1);
            }

            final Path path1 = Paths.get(dir1);
            final String dir2 = dir1 + "--" + FastDateFormat.getInstance("yyyy.MM.dd__HH.mm.ss").format(new Date());
            P2Log.sysLog("Pfad verschieben: " + dir1);
            P2Log.sysLog("  nach: " + dir2);

            Files.move(path1, Paths.get(dir2), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(path1);
            P2Log.sysLog("  moved :)");

        } catch (final Exception ex) {
            P2Log.errorLog(912012014, ex, "Die Einstellungen konnten nicht zurückgesetzt werden.");
            try {
                P2Alert.showErrorAlert("Fehler", "Einstellungen zurückgesetzen",
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
                    ProgSave.class.wait(100);
                } catch (final Exception ignored) {
                }
            }
        }
    }
}
