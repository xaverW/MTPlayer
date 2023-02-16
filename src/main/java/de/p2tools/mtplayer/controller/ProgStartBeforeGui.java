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

import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.mtplayer.controller.filmFilter.FilmFilterFactory;
import de.p2tools.mtplayer.controller.worker.ImportStandardSet;
import de.p2tools.mtplayer.gui.startDialog.StartDialogController;
import de.p2tools.p2Lib.configFile.ConfigFile;
import de.p2tools.p2Lib.configFile.ConfigFileRead;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;
import javafx.application.Platform;

import java.nio.file.Files;
import java.nio.file.Path;

public class ProgStartBeforeGui {
    public static boolean firstProgramStart = false; // ist der allererste Programmstart: Init wird gemacht

    private ProgStartBeforeGui() {
    }

    public static void workBeforeGui() {
        if (!ProgStartBeforeGui.loadAll()) {
            PDuration.onlyPing("Erster Start");
            firstProgramStart = true;

            UpdateConfig.setUpdateDone(); // dann ists ja kein Programmupdate
            ProgData.getInstance().replaceList.init(); // einmal ein Muster anlegen, für Linux ist es bereits aktiv!

            StartDialogController startDialogController = new StartDialogController();
            if (!startDialogController.isOk()) {
                // dann jetzt beenden -> Thüss
                Platform.exit();
                System.exit(0);
            }

            //todo das ist noch nicht ganz klar ob dahin
//            Platform.runLater(() -> {
            PDuration.onlyPing("Erster Start: PSet");
            ImportStandardSet.getStandardSet();
            PDuration.onlyPing("Erster Start: PSet geladen");
//            });

            FilmFilterFactory.addStandardFilter();
        }
    }

    /**
     * Config beim  Programmstart laden
     *
     * @return
     */
    public static boolean loadAll() {
        boolean loadOk = load();
        if (ProgConfig.SYSTEM_LOG_ON.getValue()) {
            PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
        }

        if (!loadOk) {
            PLog.sysLog("Weder Konfig noch Backup konnte geladen werden!");
            // teils geladene Reste entfernen
            clearConfig();
            return false;
        }
        PLog.sysLog("Konfig wurde gelesen!");
        UpdateConfig.update(); // falls es ein Programmupdate gab, Configs anpassen
        ProgColorList.setColorTheme(); // Farben einrichten
//        ProgData.getInstance().progTray.setIcon();//wenn eigenes Icon gewählt, dann notwendig
        return true;
    }

    private static void clearConfig() {
        ProgData progData = ProgData.getInstance();
        progData.setDataList.clear();
        progData.replaceList.clear();
        progData.aboList.clear();
        progData.downloadList.clear();
        progData.blackList.clear();
    }

    private static boolean load() {
        boolean ret = false;
        final Path xmlFilePath = new ProgInfos().getSettingsFile();

        try {
            if (Files.exists(xmlFilePath)) {
                if (loadProgConfigData(xmlFilePath)) {
                    return true;
                } else {
                    // dann hat das Laden nicht geklappt
                    PLog.sysLog("Konfig konnte nicht gelesen werden!");
                }
            } else {
                // dann hat das Laden nicht geklappt
                PLog.sysLog("Konfig existiert nicht!");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

//        // versuchen das Backup zu laden
//        if (loadBackup()) {
//            ret = true;
//        }
        return ret;
    }

//    private static boolean loadBackup() {
//        boolean ret = false;
//        final ArrayList<Path> path = new ArrayList<>();
//        new ProgInfos().getMTPlayerXmlCopyFilePath(path);
//        if (path.isEmpty()) {
//            PLog.sysLog("Es gibt kein Backup");
//            return false;
//        }
//
//        // dann gibts ein Backup
//        PLog.sysLog("Es gibt ein Backup");
//
//        // stage bzw. scene gibts noch nicht
//        if (PAlert.BUTTON.YES != PAlert.showAlert_yes_no(null, "Gesicherte Einstellungen laden?",
//                "Die Einstellungen sind beschädigt" + P2LibConst.LINE_SEPARATOR +
//                        "und können nicht geladen werden.",
//                "Soll versucht werden, mit gesicherten" + P2LibConst.LINE_SEPARATOR
//                        + "Einstellungen zu starten?" + P2LibConst.LINE_SEPARATORx2
//                        + "(ansonsten startet das Programm mit" + P2LibConst.LINE_SEPARATOR
//                        + "Standardeinstellungen)")) {
//
//            PLog.sysLog("User will kein Backup laden.");
//            return false;
//        }
//
//        for (final Path p : path) {
//            // teils geladene Reste entfernen
//            clearConfig();
//            PLog.sysLog(new String[]{"Versuch Backup zu laden:", p.toString()});
//            try {
//                if (loadProgConfigData(p)) {
//                    PLog.sysLog(new String[]{"Backup hat geklappt:", p.toString()});
//                    ret = true;
//                    break;
//                }
//            } catch (final Exception ex) {
//                ex.printStackTrace();
//            }
//
//        }
//        return ret;
//    }

    private static boolean loadProgConfigData(Path path) {
        PDuration.onlyPing("ProgStartFactory.loadProgConfigData");
        boolean loadOk = loadProgConfig(path);

        if (ProgConfig.SYSTEM_LOG_ON.getValue()) {
            PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
        }

        if (!loadOk) {
            return false;
        }

        initAfterLoad();
        PLog.sysLog("Konfig wurde gelesen!");
        return true;
    }

    private static boolean loadProgConfig(Path path) {
        PLog.sysLog("Programmstart und ProgConfig laden von: " + path);

        ConfigFile configFile = new ConfigFile(path.toString(), true);
        ProgConfig.addConfigData(configFile);
        ConfigFileRead configFileRead = new ConfigFileRead();
        return configFileRead.readConfig(configFile);
    }

    private static void initAfterLoad() {
        ProgData.getInstance().downloadList.initDownloads();
        ProgData.getInstance().aboList.initAboList();
        ProgData.getInstance().aboList.sort();

        // ListeFilmUpdateServer aufbauen
        if (ProgData.getInstance().actFilmFilterWorker.getStoredFilterList().isEmpty()) {
            FilmFilterFactory.addStandardFilter();
        }
    }
}
