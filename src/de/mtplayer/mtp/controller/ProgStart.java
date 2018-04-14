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

package de.mtplayer.mtp.controller;

import de.mtplayer.mLib.MLInit;
import de.mtplayer.mLib.tools.MLAlert;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.p2tools.p2Lib.tools.log.LogMsg;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ProgStart {
    Daten daten;

    public ProgStart(Daten daten) {
        this.daten = daten;
    }

    // #########################################################
    // Filmliste beim Programmstart!! laden
    // #########################################################
    public void loadDataProgStart() {
        daten.loadFilmlist.loadFilmlistProgStart();
    }

    public static void shortStartMsg() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Verzeichnisse:");
        list.add("Programmpfad: " + ProgInfos.getPathJar());
        list.add("Verzeichnis Einstellungen: " + ProgInfos.getSettingsDirectory_String());

        LogMsg.startMsg(Const.PROGRAMMNAME, list);

        list = new ArrayList<>();
        list.add(PLog.LILNE2);
        list.add("|  Programmsets:");
        list.addAll(Daten.getInstance().setList.getListProg());
        PLog.sysLog(list);
    }

    public static void startMsg() {
        shortStartMsg();
        Config.logAllConfigs();
    }


    /**
     * Config beim  Programmstart laden
     *
     * @return
     */
    public boolean allesLaden() {
        boolean load = load();
        if (Config.SYSTEM_LOG_ON.getBool()) {
            PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
        }

        if (!load) {
            PLog.userLog("Weder Konfig noch Backup konnte geladen werden!");
            // teils geladene Reste entfernen
            clearKonfig();
            return false;
        }
        PLog.userLog("Konfig wurde gelesen!");
        MLInit.initLib(Daten.debug, Const.PROGRAMMNAME, ProgInfos.getUserAgent());
        Daten.mTColor.load(); // Farben einrichten
        return true;
    }

    private void clearKonfig() {
        Daten daten = Daten.getInstance();
        daten.setList.clear();
        daten.replaceList.clear();
        daten.aboList.clear();
        daten.downloadList.clear();
        daten.blackList.clear();
    }

    private boolean load() {
        Daten daten = Daten.getInstance();

        boolean ret = false;
        final Path xmlFilePath = new ProgInfos().getXmlFilePath();

        try (IoXmlLesen reader = new IoXmlLesen(daten)) {
            if (Files.exists(xmlFilePath)) {
                if (reader.readConfiguration(xmlFilePath)) {
                    return true;
                } else {
                    // dann hat das Laden nicht geklappt
                    PLog.userLog("Konfig konnte nicht gelesen werden!");
                }
            } else {
                // dann hat das Laden nicht geklappt
                PLog.userLog("Konfig existiert nicht!");
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

    private boolean loadBackup() {
        Daten daten = Daten.getInstance();
        boolean ret = false;
        final ArrayList<Path> path = new ArrayList<>();
        new ProgInfos().getMTPlayerXmlCopyFilePath(path);
        if (path.isEmpty()) {
            PLog.userLog("Es gibt kein Backup");
            return false;
        }

        // dann gibts ein Backup
        PLog.userLog("Es gibt ein Backup");


        if (MLAlert.BUTTON.YES != new MTAlert().showAlert_yes_no("Gesicherte Einstellungen laden?",
                "Die Einstellungen sind beschädigt\n" +
                        "und können nicht geladen werden.",
                "Soll versucht werden, mit gesicherten\n"
                        + "Einstellungen zu starten?\n\n"
                        + "(ansonsten startet das Programm mit\n"
                        + "Standardeinstellungen)")) {

            PLog.userLog("User will kein Backup laden.");
            return false;
        }

        for (final Path p : path) {
            // teils geladene Reste entfernen
            clearKonfig();
            PLog.userLog(new String[]{"Versuch Backup zu laden:", p.toString()});
            try (IoXmlLesen reader = new IoXmlLesen(daten)) {
                if (reader.readConfiguration(p)) {
                    PLog.userLog(new String[]{"Backup hat geklappt:", p.toString()});
                    ret = true;
                    break;
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }

        }
        return ret;
    }
}
