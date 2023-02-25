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

package de.p2tools.mtplayer.controller.data;

import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_boolProp;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.PDataSample;
import javafx.beans.property.*;

public class ProgramDataProps extends PDataSample<ProgramData> {

    public static final String TAG = "Program";
    public static final int PROGRAM_NAME = 0;
    public static final int PROGRAM_DEST_FILENAME = 1;
    public static final int PROGRAM_PROGRAMPATH = 2;
    public static final int PROGRAM_SCHALTER = 3;
    public static final int PROGRAM_PREFIX = 4;
    public static final int PROGRAM_SUFFIX = 5;
    public static final int PROGRAM_RESTART = 6;
    public static final int PROGRAM_DOWNLOADMANAGER = 7;

    public static final int MAX_ELEM = 8;
    public static final String[] COLUMN_NAMES = {"Beschreibung", "Zieldateiname", "Programm",
            "Schalter", "Präfix", "Suffix", "Restart", "Downloadmanager"};
    public static final String[] XML_NAMES = {"Programmname", "Zieldateiname", "Programmpfad",
            "Programmschalter", "Praefix", "Suffix", "Restart", "Downloadmanager"};

    public String[] arr;

    private StringProperty name = new SimpleStringProperty("");
    private StringProperty destName = new SimpleStringProperty("");
    private StringProperty progPath = new SimpleStringProperty("");
    private StringProperty progSwitch = new SimpleStringProperty("");
    private StringProperty prefix = new SimpleStringProperty("");
    private StringProperty suffix = new SimpleStringProperty("");
    private BooleanProperty restart = new SimpleBooleanProperty(false);
    private BooleanProperty downManager = new SimpleBooleanProperty(false);

    public final Property[] properties = {name, destName, progPath, progSwitch, prefix, suffix, restart, downManager};

    public ProgramDataProps() {
        makeArr();
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public Config[] getConfigsArr() {
        return new Config[]{
                new Config_stringProp("name", ProgramDataFieldNames.PROGRAM_NAME, name),
                new Config_stringProp("destName", ProgramDataFieldNames.PROGRAM_DEST_FILENAME, destName),
                new Config_stringProp("progPath", ProgramDataFieldNames.PROGRAM_PROGRAM_PATH, progPath),
                new Config_stringProp("progSwitch", ProgramDataFieldNames.PROGRAM_SWITCH, progSwitch),
                new Config_stringProp("prefix", ProgramDataFieldNames.PROGRAM_PREFIX, prefix),
                new Config_stringProp("suffix", ProgramDataFieldNames.PROGRAM_SWITCH, suffix),
                new Config_boolProp("restart", ProgramDataFieldNames.PROGRAM_RESTART, restart),
                new Config_boolProp("downManager", ProgramDataFieldNames.PROGRAM_DOWNLOADMANAGER, downManager),
        };
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDestName() {
        return destName.get();
    }

    public StringProperty destNameProperty() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName.set(destName);
    }

    public String getProgPath() {
        return progPath.get();
    }

    public StringProperty progPathProperty() {
        return progPath;
    }

    public void setProgPath(String progPath) {
        this.progPath.set(progPath);
    }

    public String getProgSwitch() {
        return progSwitch.get();
    }

    public StringProperty progSwitchProperty() {
        return progSwitch;
    }

    public void setProgSwitch(String progSwitch) {
        this.progSwitch.set(progSwitch);
    }

    public String getPrefix() {
        return prefix.get();
    }

    public StringProperty prefixProperty() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix.set(prefix);
    }

    public String getSuffix() {
        return suffix.get();
    }

    public StringProperty suffixProperty() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix.set(suffix);
    }

    public boolean isRestart() {
        return restart.get();
    }

    public BooleanProperty restartProperty() {
        return restart;
    }

    public void setRestart(boolean restart) {
        this.restart.set(restart);
    }

    public boolean isDownManager() {
        return downManager.get();
    }

    public BooleanProperty downManagerProperty() {
        return downManager;
    }

    public void setDownManager(boolean downManager) {
        this.downManager.set(downManager);
    }

    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < properties.length; ++i) {
            if (i == 0) {
                ret += "| ***|";
            } else {
                ret += "|    |";
            }
            ret += properties[i].getName() + ": " + properties[i].getValue() + P2LibConst.LINE_SEPARATOR;
        }
        return ret;
    }

    //===================================
    // Private
    //===================================
    private void makeArr() {
        arr = new String[MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }

    public void setPropsFromXml() {
        setName(arr[PROGRAM_NAME]);
        setDestName(arr[PROGRAM_DEST_FILENAME]);
        setProgPath(arr[PROGRAM_PROGRAMPATH]);
        setProgSwitch(arr[PROGRAM_SCHALTER]);
        setPrefix(arr[PROGRAM_PREFIX]);
        setSuffix(arr[PROGRAM_SUFFIX]);
        setRestart(Boolean.parseBoolean(arr[PROGRAM_RESTART]));
        setDownManager(Boolean.parseBoolean(arr[PROGRAM_DOWNLOADMANAGER]));
    }
}