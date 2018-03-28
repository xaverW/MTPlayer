/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReplaceData extends Data<ReplaceData> {

    public final static String REPLACE_FROM = "von";
    public final static int REPLACE_FROM_NR = 0;
    public final static String REPLACE_TO = "to";
    public final static int REPLACE_TO_NR = 1;

    public static final String TAG = "Ersetzungstabelle";

    public final static String[] COLUMN_NAMES = {REPLACE_FROM, REPLACE_TO};
    public static final int MAX_ELEM = 2;
    public String[] arr;

    StringProperty from = new SimpleStringProperty();
    StringProperty to = new SimpleStringProperty();

    public ReplaceData() {
        arr = super.makeArr(MAX_ELEM);
    }

    public ReplaceData(String from, String to) {
        arr = super.makeArr(MAX_ELEM);
        arr[REPLACE_FROM_NR] = from;
        arr[REPLACE_TO_NR] = to;
        setPropsFromXml();
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

    public void setXmlFromProps() {
        arr[REPLACE_FROM_NR] = getFrom();
        arr[REPLACE_TO_NR] = getTo();
    }
}
