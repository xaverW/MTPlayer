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

package de.p2tools.mtplayer.controller.data.cleaningdata;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_boolProp;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.PDataSample;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class CleaningDataProps extends PDataSample<CleaningDataProps> {

    public static final String TAG = "CleaningData";

    private final StringProperty cleaningString = new SimpleStringProperty("");
    private final BooleanProperty always = new SimpleBooleanProperty(true);
    private final StringProperty codePoint = new SimpleStringProperty("");

    public CleaningDataProps() {
        cleaningString.addListener((u, o, n) -> makeUtfCodePoint());
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("cleaningString", cleaningString));
        list.add(new Config_boolProp("always", always));
        return list.toArray(new Config[]{});
    }

    public CleaningData getCopy() {
        CleaningData data = new CleaningData();
        data.setCleaningString(cleaningString.getValueSafe());
        data.setCodePoint(codePoint.getValueSafe());
        return data;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public String getCleaningString() {
        return cleaningString.get();
    }

    public StringProperty cleaningStringProperty() {
        return cleaningString;
    }

    public void setCleaningString(String cleaningString) {
        this.cleaningString.set(cleaningString);
        makeUtfCodePoint();
    }

    public boolean getAlways() {
        return always.get();
    }

    public BooleanProperty alwaysProperty() {
        return always;
    }

    public void setAlways(boolean always) {
        this.always.set(always);
    }

    public String getCodePoint() {
        return codePoint.get();
    }

    public StringProperty codePointProperty() {
        return codePoint;
    }

    public void setCodePoint(String codePoint) {
        this.codePoint.set(codePoint);
    }

    public void makeUtfCodePoint() {
        StringBuilder s = new StringBuilder();
        char[] chars = cleaningString.getValueSafe().toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(Character.codePointAt(chars, i));
        }
        codePoint.setValue(s.toString());
    }
}
