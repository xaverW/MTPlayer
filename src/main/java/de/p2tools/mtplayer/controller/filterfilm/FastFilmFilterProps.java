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

package de.p2tools.mtplayer.controller.filterfilm;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.config.Config_stringProp;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class FastFilmFilterProps extends P2DataSample<FastFilmFilter> implements Comparable<FastFilmFilter> {

    public static String TAG = "FastFilmFilterProps";

    private final StringProperty filterTerm = new SimpleStringProperty(); // ist der Filter-Wert
    public StringProperty[] sfStringPropArr = {filterTerm};

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new Config_stringProp("filterTerm", filterTerm));
        return list.toArray(new Config[]{});
    }

    public String getFilterTerm() {
        return filterTerm.get();
    }

    public StringProperty filterTermProperty() {
        return filterTerm;
    }

    public void setFilterTerm(String filterTerm) {
        this.filterTerm.set(filterTerm);
    }

    public FilmFilter getCopy() {
        FilmFilter sf = new FilmFilter();
        this.copyTo(sf);
        return sf;
    }

    public void copyTo(FilmFilter sf) {
        for (int i = 0; i < sfStringPropArr.length; ++i) {
            sf.sfStringPropArr[i].setValue(this.sfStringPropArr[i].getValue());
        }
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String toString() {
        return filterTerm.getValue();
    }

    @Override
    public int compareTo(FastFilmFilter o) {
        return filterTerm.getValue().compareTo(o.getFilterTerm());
    }
}
