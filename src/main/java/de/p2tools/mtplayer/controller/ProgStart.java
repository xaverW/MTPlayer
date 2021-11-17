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
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.mtplayer.controller.mediaDb.MediaDataWorker;
import de.p2tools.mtplayer.tools.update.SearchProgramUpdate;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.icon.GetIcon;
import de.p2tools.p2Lib.tools.ProgramTools;
import de.p2tools.p2Lib.tools.date.PDateFactory;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.LogMessage;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgStart {
    private boolean doneAtProgramstart = false;

    public ProgStart() {
    }

    /**
     * alles was nach der GUI gemacht werden soll z.B.
     * Filmliste beim Programmstart!! laden
     *
     * @param firstProgramStart
     */
    public void doWorkAfterGui(ProgData progData, boolean firstProgramStart) {
        GetIcon.addWindowP2Icon(progData.primaryStage);
        startMsg();
        setTitle(progData);

        progData.startTimer();
        progData.loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                if (!doneAtProgramstart) {
                    doneAtProgramstart = true;
                    MediaDataWorker.createMediaDb();
                    checkProgUpdate(progData);
                    new ProgTipOfDay().showDialog(progData, false);
                }
            }
        });

        progData.loadFilmlist.loadFilmlistProgStart(firstProgramStart);
    }

    public static void shortStartMsg() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Verzeichnisse:");
        list.add("Programmpfad: " + ProgInfos.getPathJar());
        list.add("Verzeichnis Einstellungen: " + ProgInfos.getSettingsDirectory_String());

        LogMessage.startMsg(ProgConst.PROGRAM_NAME, list);

        list = new ArrayList<>();
        list.add(PLog.LILNE2);
        list.add("|  Programmsets:");
        list.addAll(ProgData.getInstance().setDataList.getStringListSetData());
        PLog.sysLog(list);
    }

    public static void startMsg() {
        shortStartMsg();
        ProgConfig.logAllConfigs();
    }


    /**
     * Config beim  Programmstart laden
     *
     * @return
     */
    public boolean loadAll() {
        boolean loadOk = load();
        if (!loadOk) {
            //dann mit der alten Verison versuchen
            loadOk = load_oldVersion();
        }

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
        ProgData.getInstance().mTColor.setColorTheme(); // Farben einrichten
        return true;
    }

    private void clearConfig() {
        ProgData progData = ProgData.getInstance();
        progData.setDataList.clear();
        progData.replaceList.clear();
        progData.aboList.clear();
        progData.downloadList.clear();
        progData.blackList.clear();
    }

    private boolean load() {
        boolean ret = false;
        ProgData progData = ProgData.getInstance();
        final Path xmlFilePath = new ProgInfos().getSettingsFile();

        try {
            if (Files.exists(xmlFilePath)) {
                if (ProgLoadFactory.loadProgConfigData(progData, xmlFilePath)) {
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

        // versuchen das Backup zu laden
        if (loadBackup()) {
            ret = true;
        }
        return ret;
    }

    private boolean load_oldVersion() {
        ProgData progData = ProgData.getInstance();

        boolean ret = false;
        final Path xmlFilePath = new ProgInfos().getSettingsFileOld();

        try (IoReadXml reader = new IoReadXml(progData)) {
            if (Files.exists(xmlFilePath)) {
                if (reader.readConfiguration(xmlFilePath)) {
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

    private boolean loadBackup() {
        ProgData progData = ProgData.getInstance();
        boolean ret = false;
        final ArrayList<Path> path = new ArrayList<>();
        new ProgInfos().getMTPlayerXmlCopyFilePath(path);
        if (path.isEmpty()) {
            PLog.sysLog("Es gibt kein Backup");
            return false;
        }

        // dann gibts ein Backup
        PLog.sysLog("Es gibt ein Backup");


        // stage bzw. scene gibts noch nicht
        if (PAlert.BUTTON.YES != PAlert.showAlert_yes_no(null, "Gesicherte Einstellungen laden?",
                "Die Einstellungen sind beschädigt" + P2LibConst.LINE_SEPARATOR +
                        "und können nicht geladen werden.",
                "Soll versucht werden, mit gesicherten" + P2LibConst.LINE_SEPARATOR
                        + "Einstellungen zu starten?" + P2LibConst.LINE_SEPARATORx2
                        + "(ansonsten startet das Programm mit" + P2LibConst.LINE_SEPARATOR
                        + "Standardeinstellungen)")) {

            PLog.sysLog("User will kein Backup laden.");
            return false;
        }

        for (final Path p : path) {
            // teils geladene Reste entfernen
            clearConfig();
            PLog.sysLog(new String[]{"Versuch Backup zu laden:", p.toString()});
            try {
                if (ProgLoadFactory.loadProgConfigData(progData, p)) {
                    PLog.sysLog(new String[]{"Backup hat geklappt:", p.toString()});
                    ret = true;
                    break;
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }

        }
        return ret;
    }

    private void checkProgUpdate(ProgData progData) {
        // Prüfen obs ein Programmupdate gibt
        PDuration.onlyPing("checkProgUpdate");

        if (ProgData.debug) {
            // damits bei jedem Start gemacht wird
            PLog.sysLog("DEBUG: Update-Check");
            runUpdateCheck(progData, true);

        } else if (ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue() &&
                !updateCheckTodayDone()) {
            // nach Updates suchen
            runUpdateCheck(progData, false);

        } else {
            // will der User nicht --oder-- wurde heute schon gemacht
            List list = new ArrayList(5);
            list.add("Kein Update-Check:");
            if (!ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue()) {
                list.add("  der User will nicht");
            }
            if (updateCheckTodayDone()) {
                list.add("  heute schon gemacht");
            }
            PLog.sysLog(list);
        }
    }

    private static boolean updateCheckTodayDone() {
        return ProgConfig.SYSTEM_UPDATE_DATE.get().equals(PDateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
    }

    private void runUpdateCheck(ProgData progData, boolean showAlways) {
        ProgConfig.SYSTEM_UPDATE_DATE.setValue(PDateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
        new SearchProgramUpdate(progData).searchNewProgramVersion(showAlways);
    }

    private void setTitle(ProgData progData) {
        if (ProgData.debug) {
            progData.primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + ProgramTools.getProgVersion() + " / DEBUG");
        } else {
            progData.primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + ProgramTools.getProgVersion());
        }
    }
}
