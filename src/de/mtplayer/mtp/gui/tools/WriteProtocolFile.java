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

package de.mtplayer.mtp.gui.tools;

import de.mtplayer.mLib.tools.FileUtils;
import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.SysMsg;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import static de.mtplayer.mLib.tools.Functions.*;

public class WriteProtocolFile {
    private static final String TITEL = "Protokoll erstellen";

    public void write() {
        String logFile = "";
        logFile = new MTAlert().showAlertFileCooser(TITEL,
                "Speicherort auswählen",
                "Das Log wird in die gewählte Datei geschrieben.", false, false,
                FileUtils.concatPaths(System.getProperty("user.home"), "Logfile.txt"));

        if (logFile.isEmpty()) {
            return;
        }

        File file = new File(logFile);
        if (file.exists() &&
                new MTAlert().showAlert_yes_no_cancel("Logfile", "Datei existiert",
                        "Die Datei ist schon vorhanden,\n" +
                                "soll sie überschrieben werden?", false) != MTAlert.BUTTON.YES) {
            return;
        }

        try {
            final Path logFilePath = Paths.get(logFile);
            writeLogFile(logFilePath, ProgInfos.getSettingsDirectory_String(), Daten.getInstance().setList.getListProg(), Config.getAll());
            new MTAlert().showInfoAlert(TITEL, "Logfile schreiben", "Die Datei wurde erfolgreich geschrieben.", false);
        } catch (final IOException ex) {
            new MTAlert().showErrorAlert(TITEL, "Logfile schreiben", "Die Datei konnte nicht geschrieben werden.");
        }

    }


    private void writeLogFile(Path logFilePath, String settingsDir, ArrayList<String> progs, String[][] configs) throws IOException {
        try (OutputStream os = Files.newOutputStream(logFilePath);
             OutputStreamWriter osw = new OutputStreamWriter(os);
             BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write("");
            bw.newLine();
            bw.write(PLog.LOGO);
            bw.write("");
            bw.newLine();
            bw.write("");
            bw.newLine();

            bw.write("#####################################################");
            bw.newLine();
            bw.write("Erstellt: " + StringFormatters.FORMATTER_ddMMyyyyHHmm.format(new Date()));
            bw.newLine();
            bw.write("#####################################################");
            bw.newLine();
            bw.newLine();
            bw.write(Const.PROGRAMMNAME + ' ' + getProgVersion());
            bw.newLine();
            bw.write("Compiled: " + getCompileDate());
            bw.newLine();
            bw.newLine();
            bw.write("=====================================================");
            bw.newLine();
            bw.write("Java");
            bw.newLine();
            final String[] java = getJavaVersion();
            for (final String ja : java) {
                bw.write(ja);
                bw.newLine();
            }
            bw.newLine();
            bw.write("=====================================================");
            bw.newLine();
            bw.write("Betriebssystem: " + System.getProperty("os.name"));
            bw.newLine();
            bw.write("Bs-Version:     " + System.getProperty("os.version"));
            bw.newLine();
            bw.write("Bs-Architektur: " + System.getProperty("os.arch"));
            bw.newLine();
            bw.newLine();
            bw.write("=====================================================");
            bw.newLine();
            bw.write("Programmpfad: " + ProgInfos.getPathJar());
            bw.newLine();
            bw.write("Verzeichnis Einstellungen: " + settingsDir);
            bw.newLine();
            bw.newLine();
            bw.write("=====================================================");
            bw.newLine();
            final long totalMem = Runtime.getRuntime().totalMemory();
            bw.write("totalMemory: " + totalMem / (1000L * 1000L) + " MB");
            bw.newLine();
            final long maxMem = Runtime.getRuntime().maxMemory();
            bw.write("maxMemory: " + maxMem / (1000L * 1000L) + " MB");
            bw.newLine();
            final long freeMem = Runtime.getRuntime().freeMemory();
            bw.write("freeMemory: " + freeMem / (1000L * 1000L) + " MB");
            bw.newLine();
            bw.newLine();
            bw.newLine();

            //
            bw.write("#####################################################");
            bw.newLine();
            bw.write("## Programmeinstellungen ##########################");
            bw.newLine();
            bw.write("#####################################################");
            bw.newLine();
            bw.newLine();
            for (final String[] s : configs) {
                if (!s[1].isEmpty()) {
                    bw.write(s[0] + '\t' + s[1]);
                    bw.newLine();
                }
            }
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            //
            bw.write("#####################################################");
            bw.newLine();
            bw.write("## Programmsets ##################################");
            bw.newLine();
            bw.write("#####################################################");
            bw.newLine();
            bw.newLine();
            for (final String s : progs) {
                bw.write(s);
                bw.newLine();
            }
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            //
            bw.write("#####################################################");
            bw.newLine();
            bw.write("## Systemmeldungen ##################################");
            bw.newLine();
            bw.write("#####################################################");
            bw.newLine();
            bw.newLine();
            bw.write(SysMsg.textSystem.toString());
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            //
            bw.write("#####################################################");
            bw.newLine();
            bw.write("## Programmausgabe ##################################");
            bw.newLine();
            bw.write("#####################################################");
            bw.newLine();
            bw.newLine();
            bw.write(SysMsg.textProgramm.toString());
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            //
            bw.write("#########################################################");
            bw.newLine();
            bw.write("## Fehlermeldungen                                       ");
            bw.newLine();
            final ArrayList<String> retList = PLog.printErrorMsg();
            for (final String s : retList) {
                bw.write(s);
                bw.newLine();
            }
            retList.clear();
            bw.newLine();
            bw.newLine();
        } catch (final IOException ex) {
            PLog.errorLog(319865493, ex);
            throw ex;
        }
    }
}
