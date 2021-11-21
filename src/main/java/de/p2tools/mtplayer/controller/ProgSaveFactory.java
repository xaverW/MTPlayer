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
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.p2Lib.configFile.ConfigFile;
import de.p2tools.p2Lib.configFile.WriteConfigFile;
import de.p2tools.p2Lib.tools.log.PLog;

import java.nio.file.Path;

public class ProgSaveFactory {

    private ProgSaveFactory() {
    }

    public static void saveProgConfig() {
        //sind die Programmeinstellungen
        PLog.sysLog("save progConfig");

        final Path xmlFilePath = ProgInfos.getSettingsFile();
        ConfigFile configFile = new ConfigFile(ProgConst.XML_START, xmlFilePath);
        ProgConfig.addConfigData(configFile);

        WriteConfigFile writeConfigFile = new WriteConfigFile();
        writeConfigFile.addConfigFile(configFile);
        writeConfigFile.writeConfigFile();
    }
}
