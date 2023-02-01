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

package de.p2tools.mtplayer.controller;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.filmFilter.FilmFilterFactory;
import de.p2tools.p2Lib.configFile.ConfigFile;
import de.p2tools.p2Lib.configFile.ReadConfigFile;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;

import java.nio.file.Path;

public class ProgLoadFactory {

    private ProgLoadFactory() {
    }

    public static boolean loadProgConfigData(ProgData progData, Path path) {
        PDuration.onlyPing("ProgStartFactory.loadProgConfigData");
        boolean loadOk = loadProgConfig(path);

        if (ProgConfig.SYSTEM_LOG_ON.getValue()) {
            PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
        }

        if (!loadOk) {
            return false;
        }

        initAfterLoad(progData);
        PLog.sysLog("Konfig wurde gelesen!");
        return true;
    }

    private static boolean loadProgConfig(Path path) {
        PLog.sysLog("Programmstart und ProgConfig laden von: " + path);

        ConfigFile configFile = new ConfigFile(ProgConst.XML_START, path);
        ProgConfig.addConfigData(configFile);
        ReadConfigFile readConfigFile = new ReadConfigFile();
        readConfigFile.addConfigFile(configFile);

        return readConfigFile.readConfigFile();
    }

    private static void initAfterLoad(ProgData progData) {
//        progData.blackList.sortIncCounter(false);//Kompromiss?? ist schneller
        progData.downloadList.initDownloads();
        progData.aboList.initAboList();
        progData.aboList.sort();

        // ListeFilmUpdateServer aufbauen
        if (progData.actFilmFilterWorker.getStoredFilterList().isEmpty()) {
            FilmFilterFactory.addStandardFilter();
        }
    }
}


