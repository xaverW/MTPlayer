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

import de.p2tools.p2Lib.configFile.config.Config;
import de.p2tools.p2Lib.configFile.config.Config_stringProp;
import de.p2tools.p2Lib.configFile.pData.PDataSample;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class ReplaceData extends PDataSample<ReplaceData> {

    public final static String REPLACE_FROM = "von";
    public final static int REPLACE_FROM_NR = 0;
    public final static String REPLACE_TO = "to";
    public final static int REPLACE_TO_NR = 1;

    public static final String TAG = "ReplaceData";
    public static final int MAX_ELEM = 2;
    public String[] arr;

    StringProperty from = new SimpleStringProperty();
    StringProperty to = new SimpleStringProperty();

    public ReplaceData() {
        makeArray();
    }

    public ReplaceData(String from, String to) {
        makeArray();
        arr[REPLACE_FROM_NR] = from;
        arr[REPLACE_TO_NR] = to;
        setPropsFromXml();
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "ReplaceData";
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("from", REPLACE_FROM, from));
        list.add(new Config_stringProp("to", REPLACE_TO, to));

        return list.toArray(new Config[]{});
    }


    void makeArray() {
        arr = new String[MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }

    public String getFrom() {
        return from.get();
    }

    public StringProperty fromProperty() {
        return from;
    }

    public void setFrom(String from) {
        this.from.set(from);
    }

    public String getTo() {
        return to.get();
    }

    public StringProperty toProperty() {
        return to;
    }

    public void setTo(String to) {
        this.to.set(to);
    }

    public void setPropsFromXml() {
        setFrom(arr[REPLACE_FROM_NR]);
        setTo(arr[REPLACE_TO_NR]);
    }
}
