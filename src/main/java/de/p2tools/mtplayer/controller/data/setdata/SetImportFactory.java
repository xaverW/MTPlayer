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

package de.p2tools.mtplayer.controller.data.setdata;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.configfile.ConfigReadFile;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class SetImportFactory extends LinkedList<String[]> {

    public static boolean getStandardSet(Stage stage) {
        // ProgStartBeforeGui.workBeforeGui
        // PaneSetList -> Button
        // ImportSetDialogController -> Anlegen von Abos, Downloads, .. , ResetDialog
        SetDataList setDataList = new SetDataList();
        try {
            //liefert das standard Programmset für das entsprechende BS
            switch (P2InfoFactory.getOs()) {
                case LINUX:
                    loadSetDataUrl(setDataList, ProgConst.PROGRAM_SET_URL_LINUX);
                    break;
                case MAC:
                    loadSetDataUrl(setDataList, ProgConst.PROGRAM_SET_URL_MAC);
                    break;
                default:
                    loadSetDataUrl(setDataList, ProgConst.PROGRAM_SET_URL_WINDOWS);
            }

            if (setDataList.isEmpty()) {
                P2Log.sysLog("Sets laden hat nicht geklappt, dann aus dem jar");
                //dann nehmen wir halt die im jar-File
                switch (P2InfoFactory.getOs()) {
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

            if (!setDataList.isEmpty()) {
                // damit die Variablen ersetzt werden
                SetReplacePatternFactory.progReplacePattern(stage, setDataList);
            } else {
                P2Log.sysLog("Sets laden hat nicht geklappt");
            }
        } catch (Exception ex) {
            P2Log.errorLog(202014578, ex);
        }

        ProgData.getInstance().setDataList.addSetData(setDataList);
        return !setDataList.isEmpty();
    }

    private static void loadSetDataUrl(SetDataList setDataList, String url) {
        P2Log.sysLog("Sets laden von: " + url);
        ConfigFile configFile = new ConfigFile(url, false);
        configFile.addConfigs(setDataList);
        ConfigReadFile.readConfig(configFile);
    }

    private static void loadSetDataLocalFile(SetDataList setDataList, String file) throws IOException {
        P2Log.sysLog("Sets laden von: " + file);
        InputStreamReader is = new InputStreamReader(ClassLoader.getSystemResource(file).openStream(), StandardCharsets.UTF_8);
        ConfigFile configFile = new ConfigFile(is, false);
        configFile.addConfigs(setDataList);
        ConfigReadFile.readConfig(configFile);
    }
}
