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

package de.p2tools.mtplayer.controller.data.compdata;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_pDataList;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class SortDataProps extends P2DataSample<SortData> {

    public static final String TAG = "SortData";

    private final StringProperty name = new SimpleStringProperty("");
    private final CompList compList = new CompList();

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> configList = new ArrayList<>();
        configList.add(new Config_stringProp("name", name));
        configList.add(new Config_pDataList(compList));
        return configList.toArray(new Config[]{});
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public CompList getCompList() {
        return compList;
    }

    @Override
    public String toString() {
        return name.getValueSafe();
    }
}
