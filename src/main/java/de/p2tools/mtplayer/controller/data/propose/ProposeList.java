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

package de.p2tools.mtplayer.controller.data.propose;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.configfile.pdata.PDataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

@SuppressWarnings("serial")
public class ProposeList extends SimpleListProperty<ProposeData> implements PDataList<ProposeData> {

    public String TAG = "ProposeList";
    private FilteredList<ProposeData> filteredList = null;
    private SortedList<ProposeData> sortedList = null;
    private final SimpleListProperty<FilmDataMTP> filmDataList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final FilteredList<FilmDataMTP> filmFilteredList;
    private final SortedList<FilmDataMTP> filmSortedList;

    public ProposeList(ProgData progData) {
        super(FXCollections.observableArrayList());

        filmFilteredList = new FilteredList<>(filmDataList, p -> true);
        filmSortedList = new SortedList<>(filmFilteredList);
    }

    public SortedList<ProposeData> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<ProposeData>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<ProposeData> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<ProposeData>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    public SimpleListProperty<FilmDataMTP> getFilmDataList() {
        return filmDataList;
    }

    public SortedList<FilmDataMTP> getFilmSortedList() {
        return filmSortedList;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Vorschläge";
    }

    @Override
    public ProposeData getNewItem() {
        return new ProposeData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(ProposeData.class)) {
            add((ProposeData) obj);
        }
    }
}
