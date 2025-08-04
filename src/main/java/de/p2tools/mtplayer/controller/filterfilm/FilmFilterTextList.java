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

import de.p2tools.p2lib.configfile.pdata.P2DataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public final class FilmFilterTextList extends SimpleListProperty<FilmFilterText> implements P2DataList<FilmFilterText> {
    public String TAG = "TextFilterList";

    public FilmFilterTextList(String tag) {
        super(FXCollections.observableArrayList());
        this.TAG = tag;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller TextFilter";
    }

    @Override
    public FilmFilterText getNewItem() {
        return new FilmFilterText();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(FilmFilterText.class)) {
            add((FilmFilterText) obj);
        }
    }
}
