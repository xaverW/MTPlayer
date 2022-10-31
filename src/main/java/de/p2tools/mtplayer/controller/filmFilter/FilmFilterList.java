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

package de.p2tools.mtplayer.controller.filmFilter;

import de.p2tools.p2Lib.configFile.pData.PDataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public final class FilmFilterList extends SimpleListProperty<FilmFilter> implements PDataList<FilmFilter> {
    public static final String TAG = "SelectedFilterList";

    public FilmFilterList() {
        super(FXCollections.observableArrayList());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Filter";
    }

    @Override
    public FilmFilter getNewItem() {
        return new FilmFilter();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(FilmFilter.class)) {
            add((FilmFilter) obj);
        }
    }

    public int top(int idx, boolean up) {
        FilmFilter filmFilter = remove(idx);
        int ret;
        if (up) {
            add(0, filmFilter);
            ret = 0;
        } else {
            add(filmFilter);
            ret = getSize() - 1;
        }
        return ret;
    }

    public int up(int idx, boolean up) {
        FilmFilter filmFilter = remove(idx);
        int neu = idx;
        if (up) {
            if (neu > 0) {
                --neu;
            }
        } else if (neu < size()) {
            ++neu;
        }
        add(neu, filmFilter);
        return neu;
    }
}
