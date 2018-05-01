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

package de.mtplayer.mtp.tools.storedFilter;

import de.mtplayer.mtp.controller.config.ProgData;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public final class StoredFilter {

    private final BooleanProperty filterChange = new SimpleBooleanProperty(true);

    private final ProgData progData;
    private final ChangeListener<Boolean> filterChangeListener;
    private final ChangeListener<Boolean> blacklistChangeListener;

    // dient nur der Info im Config-File
    public static final String SELECTED_FILTER_NAME = "aktuelle Einstellung";

    // ist der aktuell angezeigte Filter
    private SelectedFilter selectedFilter = new SelectedFilter(SELECTED_FILTER_NAME);

    // ist die Liste der gespeicherten Filter
    private final ObservableList<SelectedFilter> filterList =
            FXCollections.observableList(new ArrayList<>(), (SelectedFilter tp) -> new Observable[]{tp.nameProperty()});

    public StoredFilter(ProgData progData) {
        this.progData = progData;

        filterChangeListener = (observable, oldValue, newValue) -> postFilterChange();
        blacklistChangeListener = (observable, oldValue, newValue) -> postBlacklistChange();

        getSelectedFilter().filterChangeProperty().addListener(filterChangeListener);
        getSelectedFilter().blacklistChangeProperty().addListener(blacklistChangeListener);
    }

    public BooleanProperty filterChangeProperty() {
        return filterChange;
    }

    private boolean isFilterChange() {
        return filterChange.get();
    }

    private void setFilterChange(boolean filterChange) {
        this.filterChange.set(filterChange);
    }

    /**
     * ist der aktuell angezeigte Filter
     *
     * @return
     */
    public SelectedFilter getSelectedFilter() {
        return selectedFilter;
    }

    /**
     * sind die gesicherten Filterprofile
     *
     * @return
     */
    public ObservableList<SelectedFilter> getStordeFilterList() {
        return filterList;
    }

    /**
     * einen neuen Filter zu den gespeicherten hinzufügen
     *
     * @return
     */
    public String addNewStoredFilter(String name) {
        final SelectedFilter sf = new SelectedFilter();
        SelectedFilter.copyFilter(selectedFilter, sf);

        sf.setName(name.isEmpty() ? getNextName() : name);
        filterList.add(sf);

        return sf.getName();
    }

    public String getNextName() {
        String ret = "";
        int id = 1;
        boolean found = false;
        while (!found) {

            final String name = "Filter " + id;
            if (!filterList.stream().filter(f -> name.equalsIgnoreCase(f.getName())).findAny().isPresent()) {
                ret = name;
                found = true;
            }
            ++id;
        }

        return ret;
    }

    /**
     * delete filter
     *
     * @param sf
     */
    public void removeStoredFilter(SelectedFilter sf) {
        if (sf == null) {
            return;
        }
        filterList.remove(sf);
    }


    /**
     * delete all Filter
     */
    public void removeAllStoredFilter() {
        filterList.clear();
    }

    /**
     * gesicherten Filter mit den aktuellen Einstellungen überschreiben
     *
     * @param sf
     */
    public void saveStoredFilter(SelectedFilter sf) {
        if (sf == null) {
            return;
        }
        final String name = sf.getName();
        SelectedFilter.copyFilter(selectedFilter, sf);
        sf.setName(name);
    }

    /**
     * gespeicherten Filter einstellen
     *
     * @param sf
     */
    public synchronized void loadStoredFilter(SelectedFilter sf) {
        if (sf == null) {
            return;
        }
        getSelectedFilter().filterChangeProperty().removeListener(filterChangeListener);
        getSelectedFilter().blacklistChangeProperty().removeListener(blacklistChangeListener);
        Boolean black = selectedFilter.blacklistOnProperty().getValue();
        SelectedFilter.copyFilter(sf, selectedFilter);
        if (selectedFilter.blacklistOnProperty().getValue() == black) {
            // Black hat sich nicht geändert
            postFilterChange();
        } else {
            postBlacklistChange();
        }
        getSelectedFilter().filterChangeProperty().addListener(filterChangeListener);
        getSelectedFilter().blacklistChangeProperty().addListener(blacklistChangeListener);
    }

    public synchronized boolean txtFilterIsEmpty() {
        return txtFilterIsEmpty(selectedFilter);
    }

    public synchronized boolean txtFilterIsEmpty(SelectedFilter sf) {
        return sf.txtFilterIsEmpty();
    }

    public synchronized boolean clearTxtFilter() {
        return clearTxtFilter(selectedFilter);
    }

    public synchronized boolean clearTxtFilter(SelectedFilter sf) {
        boolean ret;
        getSelectedFilter().filterChangeProperty().removeListener(filterChangeListener);
        getSelectedFilter().blacklistChangeProperty().removeListener(blacklistChangeListener);
        ret = sf.clearTxtFilter();
        postFilterChange();
        getSelectedFilter().filterChangeProperty().addListener(filterChangeListener);
        getSelectedFilter().blacklistChangeProperty().addListener(blacklistChangeListener);
        return ret;
    }

    public synchronized void clearFilter() {
        clearFilter(selectedFilter);
    }

    public synchronized void clearFilter(SelectedFilter sf) {
        getSelectedFilter().filterChangeProperty().removeListener(filterChangeListener);
        getSelectedFilter().blacklistChangeProperty().removeListener(blacklistChangeListener);
        sf.clearFilter(); // Button Black wird nicht verändert
        postFilterChange();
        getSelectedFilter().filterChangeProperty().addListener(filterChangeListener);
        getSelectedFilter().blacklistChangeProperty().addListener(blacklistChangeListener);
    }


    private void postFilterChange() {
        setFilterChange(!isFilterChange());
    }

    private void postBlacklistChange() {
        // dann hat sich auch Blacklist-ein/aus geändert
        progData.filmlist.filterList();
        setFilterChange(!isFilterChange());
    }

}
