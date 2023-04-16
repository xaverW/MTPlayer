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

package de.p2tools.mtplayer.controller.data.mediacleaningdata;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.PDataSample;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class MediaCleaningDataProps extends PDataSample<MediaCleaningDataProps> {

    public static final String TAG = "MediaCleaningData";

    private final StringProperty cleaningData = new SimpleStringProperty("");
    private final StringProperty codePoint = new SimpleStringProperty("");

    public MediaCleaningDataProps() {
        cleaningData.addListener((u, o, n) -> makeUtfCodePoint());
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("cleaningData", cleaningData));
        return list.toArray(new Config[]{});
    }

    public MediaCleaningData getCopy() {
        MediaCleaningData data = new MediaCleaningData();
        data.setCleaningData(cleaningData.getValueSafe());
        data.setCodePoint(codePoint.getValueSafe());
        return data;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public String getCleaningData() {
        return cleaningData.get();
    }

    public StringProperty cleaningDataProperty() {
        return cleaningData;
    }

    public void setCleaningData(String cleaningData) {
        this.cleaningData.set(cleaningData);
        makeUtfCodePoint();
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
        String s = "";
        char[] chars = cleaningData.getValueSafe().toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (i > 0) {
                s += ", ";
            }
            s += Character.codePointAt(chars, i) + "";
        }
        codePoint.setValue(s);
    }
}
