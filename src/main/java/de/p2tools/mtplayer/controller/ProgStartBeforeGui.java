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
import de.p2tools.mtplayer.controller.worker.ImportStandardSet;
import de.p2tools.mtplayer.gui.startdialog.StartDialogController;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.configfile.ConfigReadFile;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import de.p2tools.p2lib.tools.log.PLogger;
import javafx.application.Platform;

import java.nio.file.Files;
import java.nio.file.Path;

public class ProgStartBeforeGui {

    private ProgStartBeforeGui() {
    }

    public static void workBeforeGui() {
        if (!loadAll()) {
            PDuration.onlyPing("Erster Start");
            ProgData.firstProgramStart = true;

            ProgConfigUpdate.setUpdateDone(); //dann ists ja kein Programmupdate
            ProgData.getInstance().replaceList.init(); //einmal ein Muster anlegen, für Linux ist es bereits aktiv!

            StartDialogController startDialogController = new StartDialogController();
            if (!startDialogController.isOk()) {
                // dann jetzt beenden -> Thüss
                Platform.exit();
                System.exit(0);
            }

            PDuration.onlyPing("Erster Start: PSet");
            ImportStandardSet.getStandardSet();
            PDuration.onlyPing("Erster Start: PSet geladen");
            FilmFilterSamples.addStandardFilter();
            PDuration.onlyPing("Erster Start: CleaningList init");
            ProgData.getInstance().cleaningDataListMedia.initList();
            ProgData.getInstance().cleaningDataListPropose.initList();
        }

        PLog.sysLog("History-Listen laden");
        ProgData.getInstance().historyList.loadList();
        ProgData.getInstance().historyListAbos.loadList();
        ProgData.getInstance().historyListBookmarks.loadList();
    }

    /**
     * Config beim  Programmstart laden
     *
     * @return
     */
    private static boolean loadAll() {
        if (ProgConfig.SYSTEM_LOG_ON.getValue()) {
            PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
        }

        if (!load()) {
            PLog.sysLog("Weder Konfig noch Backup konnte geladen werden!");
            // teils geladene Reste entfernen
            clearTheConfigs();
            return false;
        }

        return true;
    }

    private static boolean load() {
        final Path xmlFilePath = new ProgInfos().getSettingsFile();
        PDuration.onlyPing("ProgStartFactory.loadProgConfigData");
        try {
            if (!Files.exists(xmlFilePath)) {
                //dann gibts das Konfig-File gar nicht
                PLog.sysLog("Konfig existiert nicht!");
                return false;
            }

            PLog.sysLog("Programmstart und ProgConfig laden von: " + xmlFilePath);
            ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), true) {
                @Override
                public void clearConfigFile() {
                    clearTheConfigs();
                }
            };
            ProgConfig.addConfigData(configFile);
            if (ConfigReadFile.readConfig(configFile)) {
                initAfterLoad();
                PLog.sysLog("Konfig wurde geladen!");
                return true;

            } else {
                // dann hat das Laden nicht geklappt
                PLog.sysLog("Konfig konnte nicht geladen werden!");
                return false;
            }
        } catch (final Exception ex) {
            PLog.errorLog(915470101, ex);
        }
        return false;
    }

    private static void clearTheConfigs() {
        ProgData progData = ProgData.getInstance();
        progData.setDataList.clear();
        progData.replaceList.clear();
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

        ProgConfigUpdate.update(); //falls es ein Programmupdate gab, Configs anpassen
        ProgColorList.setColorTheme(); //Farben einrichten
    }
}
