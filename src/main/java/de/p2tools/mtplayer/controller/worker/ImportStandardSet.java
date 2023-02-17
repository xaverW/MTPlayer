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

package de.p2tools.mtplayer.controller.worker;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.SetDataList;
import de.p2tools.mtplayer.controller.data.SetReplacePatternFactory;
import de.p2tools.p2Lib.configFile.ConfigFile;
import de.p2tools.p2Lib.configFile.ConfigFileRead;
import de.p2tools.p2Lib.tools.ProgramToolsFactory;
import de.p2tools.p2Lib.tools.log.PLog;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;


@SuppressWarnings("serial")
public class ImportStandardSet extends LinkedList<String[]> {

    public static boolean getStandardSet() {
        SetDataList setDataList = new SetDataList();
        try {
            //liefert das standard Programmset für das entsprechende BS
            switch (ProgramToolsFactory.getOs()) {
                case LINUX:
                    loadSetDataUrl(setDataList, ProgConst.PROGRAM_SET_URL_LINUX);
                    break;
                case MAC:
                    loadSetDataUrl(setDataList, ProgConst.PROGRAM_SET_URL_MAC);
                    break;
                default:
                    loadSetDataUrl(setDataList, ProgConst.PROGRAM_SET_URL_WINDOWS);
            }

            if (setDataList.size() == 0) {
                PLog.sysLog("Sets laden hat nicht geklappt, dann aus dem jar");
                //dann nehmen wir halt die im jar-File
                switch (ProgramToolsFactory.getOs()) {
                    case LINUX:
                        loadSetDataLocalFile(setDataList, ProgConst.PSET_FILE_LINUX);
                        break;
                    case MAC:
                        loadSetDataLocalFile(setDataList, ProgConst.PSET_FILE_MAC);
                        break;
                    default:
                        loadSetDataLocalFile(setDataList, ProgConst.PSET_FILE_WINDOWS);
                }
            }

            if (setDataList.size() > 0) {
                // damit die Variablen ersetzt werden
                SetReplacePatternFactory.progReplacePattern(setDataList);
            } else {
                PLog.sysLog("Sets laden hat nicht geklappt");
            }
        } catch (Exception ex) {
            PLog.errorLog(202014578, ex);
        }

        ProgData.getInstance().setDataList.addSetData(setDataList);
        return !setDataList.isEmpty();
    }

    private static boolean loadSetDataUrl(SetDataList setDataList, String url) {
        PLog.sysLog("Sets laden von: " + url);
        ConfigFile configFile = new ConfigFile(url, false);
        configFile.addConfigs(setDataList);
        return ConfigFileRead.readConfig(configFile);
    }

    private static boolean loadSetDataLocalFile(SetDataList setDataList, String file) throws IOException {
        PLog.sysLog("Sets laden von: " + file);
        InputStreamReader is = new InputStreamReader(ProgConst.class.getResource(file).openStream(), StandardCharsets.UTF_8);
        ConfigFile configFile = new ConfigFile(is, false);
        configFile.addConfigs(setDataList);
        return ConfigFileRead.readConfig(configFile);
    }
}
