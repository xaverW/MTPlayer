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
import de.p2tools.mtplayer.controller.filmfilter.FilmFilterSamples;
import de.p2tools.mtplayer.controller.update.ProgConfigUpdate;
import de.p2tools.mtplayer.controller.update.WhatsNewFactory;
import de.p2tools.mtplayer.controller.worker.ImportStandardSet;
import de.p2tools.mtplayer.gui.startdialog.StartDialogController;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.configfile.ConfigReadFile;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import de.p2tools.p2lib.tools.log.P2Logger;
import javafx.application.Platform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ProgStartBeforeGui {

    private ProgStartBeforeGui() {
    }

    public static void workBeforeGui() {
        if (!loadAll()) {
            // dann ist der erste Start
            P2Duration.onlyPing("Erster Start");
            ProgData.firstProgramStart = true;

            ProgConfigUpdate.setUpdateDone(); // dann ist's ja kein Programmupdate
            WhatsNewFactory.setLastShown(); // muss dann ja nicht angezeigt werden

            ProgData.getInstance().replaceList.init(); // einmal ein Muster anlegen, für Linux ist es bereits aktiv!
            ProgData.getInstance().utDataList.init(); // einmal ein Muster anlegen, für Linux ist es bereits aktiv!

            StartDialogController startDialogController = new StartDialogController();
            if (!startDialogController.isOk()) {
                // dann jetzt beenden -> Tschüs
                Platform.exit();
                System.exit(0);
            }

            P2Duration.onlyPing("Erster Start: PSet");
            ImportStandardSet.getStandardSet();
            P2Duration.onlyPing("Erster Start: PSet geladen");
            FilmFilterSamples.addStandardFilter();
            P2Duration.onlyPing("Erster Start: CleaningList init");
            ProgData.getInstance().cleaningDataListMedia.initList();
            ProgData.getInstance().cleaningDataListPropose.initList();
        }

        P2Log.sysLog("History-Listen laden");
        ProgData.getInstance().historyList.loadList();
        ProgData.getInstance().historyListAbos.loadList();
        ProgData.getInstance().historyListBookmarks.loadList();
    }

    /**
     * Config beim  Programmstart laden
     */
    private static boolean loadAll() {
        ArrayList<String> logList = new ArrayList<>();
        boolean ret = load(logList);

        if (ProgConfig.SYSTEM_LOG_ON.getValue()) {
            // dann für den evtl. geänderten LogPfad
            P2Logger.setFileHandler(ProgInfos.getLogDirectory_String());
        }
        P2Log.sysLog(logList);

        if (!ret) {
            P2Log.sysLog("Weder Konfig noch Backup konnte geladen werden!");
            // teils geladene Reste entfernen
            clearTheConfigs();
        }

        return ret;
    }

    private static boolean load(ArrayList<String> logList) {
        final Path xmlFilePath = ProgInfos.getSettingsFile();
        P2Duration.onlyPing("ProgStartFactory.loadProgConfigData");
        try {
            if (!Files.exists(xmlFilePath)) {
                //dann gibts das Konfig-File gar nicht
                logList.add("Konfig existiert nicht!");
                return false;
            }

            logList.add("Programmstart und ProgConfig laden von: " + xmlFilePath);
            ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), true) {
                @Override
                public void clearConfigFile() {
                    clearTheConfigs();
                }
            };
            ProgConfig.addConfigData(configFile);
            if (ConfigReadFile.readConfig(configFile)) {
                initAfterLoad();
                logList.add("Konfig wurde geladen!");
                return true;

            } else {
                // dann hat das Laden nicht geklappt
                logList.add("Konfig konnte nicht geladen werden!");
                return false;
            }
        } catch (final Exception ex) {
            logList.add(ex.getLocalizedMessage());
        }
        return false;
    }

    private static void clearTheConfigs() {
        ProgData progData = ProgData.getInstance();
        progData.setDataList.clear();
        progData.replaceList.clear();
        progData.utDataList.clear();
        progData.aboList.clear();
        progData.downloadList.clear();
        progData.blackList.clear();
        progData.cleaningDataListMedia.clear();
        progData.cleaningDataListPropose.clear();
    }

    private static void initAfterLoad() {
        ProgData.getInstance().downloadList.initDownloads();
        ProgData.getInstance().aboList.initAboList();
        ProgData.getInstance().cleaningDataListMedia.initList();
        ProgData.getInstance().cleaningDataListPropose.initList();

        //Filter einrichten
        if (ProgData.getInstance().filmFilterWorker.getStoredFilterList().isEmpty()) {
            FilmFilterSamples.addStandardFilter();
        }

        ProgConfigUpdate.update(); // falls es ein Programmupdate gab, Configs anpassen
        ProgColorList.setColorTheme(); // Farben einrichten
        ProxyFactory.initProxy(); // wenn gewollt, einen Proxy einrichten
    }
}
