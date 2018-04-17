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

package de.mtplayer.mtp.gui.mediaDb;

import de.mtplayer.mtp.controller.config.Daten;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.Collection;
import java.util.function.Predicate;

@SuppressWarnings("serial")
public class MediaDbList extends SimpleListProperty<MediaDbData> {

    private final Daten daten;
    private FilteredList<MediaDbData> filteredList = null;
    private SortedList<MediaDbData> sortedList = null;

    private BooleanProperty propSearch = new SimpleBooleanProperty(false);
    private MediaDbListExtern externList;


    public MediaDbList(Daten daten) {
        super(FXCollections.observableArrayList());
        this.daten = daten;
        externList = new MediaDbListExtern(this.daten);
    }

    public boolean isPropSearch() {
        return propSearch.get();
    }

    public BooleanProperty propSearchProperty() {
        return propSearch;
    }

    public void setPropSearch(boolean propSearch) {
        this.propSearch.set(propSearch);
    }

    public SortedList<MediaDbData> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<MediaDbData> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    public synchronized void filterdListSetPred(Predicate<MediaDbData> predicate) {
        filteredList.setPredicate(predicate);
    }

    public synchronized void filterdListClearPred() {
        filteredList.setPredicate(p -> false);
    }

    public synchronized void filterdListClearPred(boolean pred) {
        filteredList.setPredicate(p -> pred);
    }

    public MediaDbListExtern getExternList() {
        return externList;
    }

    public synchronized boolean setAll(Collection<? extends MediaDbData> mediaDbData) {
        externList.mediaDbDataSetAll(mediaDbData);
        return super.setAll(mediaDbData);
    }

    public synchronized boolean addAll(Collection<? extends MediaDbData> mediaDbData) {
        externList.mediaDbDataAddAll(mediaDbData);
        return super.addAll(mediaDbData);
    }

    public synchronized boolean add(MediaDbData mediaDbData) {
        externList.mediaDbDataAdd(mediaDbData);
        return super.add(mediaDbData);
    }

    public synchronized void createMediaDb() {
        if (isPropSearch()) {
            // dann mach mers gerade schon :)
            return;
        }

        Thread th = new Thread(new CreateMediaDb(this));
        th.setName("createMediaDb");
        th.start();
    }

    public synchronized void removeCollectionFromMediaDb(String collection) {
        MediaDb.removeCollection(this, collection);
    }
}
