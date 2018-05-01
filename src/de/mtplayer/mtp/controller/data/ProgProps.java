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

package de.mtplayer.mtp.controller.data;

import de.mtplayer.mLib.tools.Data;
import de.mtplayer.mtp.controller.config.ProgConst;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProgProps extends Data<ProgProps> {

    public static final int PROGRAMM_NAME = 0;
    public static final int PROGRAMM_ZIEL_DATEINAME = 1;
    public static final int PROGRAMM_PROGRAMMPFAD = 2;
    public static final int PROGRAMM_SCHALTER = 3;
    public static final int PROGRAMM_PRAEFIX = 4;
    public static final int PROGRAMM_SUFFIX = 5;
    public static final int PROGRAMM_RESTART = 6;
    public static final int PROGRAMM_DOWNLOADMANAGER = 7;

    public static final int MAX_ELEM = 8;
    public static final String TAG = "Programm";
    public static final String[] COLUMN_NAMES = {"Beschreibung", "Zieldateiname", "Programm",
            "Schalter", "Präfix", "Suffix", "Restart", "Downloadmanager"};
    public static final String[] XML_NAMES = {"Programmname", "Zieldateiname", "Programmpfad",
            "Programmschalter", "Praefix", "Suffix", "Restart", "Downloadmanager"};

    public String[] arr;

    private StringProperty name = new SimpleStringProperty("");
    private StringProperty destName = new SimpleStringProperty("");
    private StringProperty progPath = new SimpleStringProperty("");
    private StringProperty progSwitch = new SimpleStringProperty("");
    private StringProperty praefix = new SimpleStringProperty("");
    private StringProperty suffix = new SimpleStringProperty("");
    private BooleanProperty restart = new SimpleBooleanProperty(false);
    private BooleanProperty downManager = new SimpleBooleanProperty(false);

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

    public String getPraefix() {
        return praefix.get();
    }

    public StringProperty praefixProperty() {
        return praefix;
    }

    public void setPraefix(String praefix) {
        this.praefix.set(praefix);
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

    public ProgProps() {
        makeArr();
    }

    @Override
    public String toString() {
        setXmlFromProps();
        String ret = "";
        for (int i = 0; i < MAX_ELEM; ++i) {
            if (i == 0) {
                ret += "| ***|" + COLUMN_NAMES[i] + ": " + arr[i] + ProgConst.LINE_SEPARATOR;
            } else {
                ret += "|    |" + COLUMN_NAMES[i] + ": " + arr[i] + ProgConst.LINE_SEPARATOR;
            }
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
        setName(arr[PROGRAMM_NAME]);
        setDestName(arr[PROGRAMM_ZIEL_DATEINAME]);
        setProgPath(arr[PROGRAMM_PROGRAMMPFAD]);
        setProgSwitch(arr[PROGRAMM_SCHALTER]);
        setPraefix(arr[PROGRAMM_PRAEFIX]);
        setSuffix(arr[PROGRAMM_SUFFIX]);
        setRestart(Boolean.parseBoolean(arr[PROGRAMM_RESTART]));
        setDownManager(Boolean.parseBoolean(arr[PROGRAMM_DOWNLOADMANAGER]));
    }

    public void setXmlFromProps() {
        arr[PROGRAMM_NAME] = getName();
        arr[PROGRAMM_ZIEL_DATEINAME] = getDestName();
        arr[PROGRAMM_PROGRAMMPFAD] = getProgPath();
        arr[PROGRAMM_SCHALTER] = getProgSwitch();
        arr[PROGRAMM_PRAEFIX] = getPraefix();
        arr[PROGRAMM_SUFFIX] = getSuffix();
        arr[PROGRAMM_RESTART] = String.valueOf(isRestart());
        arr[PROGRAMM_DOWNLOADMANAGER] = String.valueOf(isDownManager());
    }

}
