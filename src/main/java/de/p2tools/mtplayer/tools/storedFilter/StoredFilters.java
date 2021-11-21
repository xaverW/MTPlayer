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

package de.p2tools.mtplayer.tools.storedFilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.p2Lib.alert.PAlert;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Optional;

public final class StoredFilters {

    private final ProgData progData;

    private final BooleanProperty filterChange = new SimpleBooleanProperty(true);
    private final BooleanProperty backward = new SimpleBooleanProperty(false);
    private final BooleanProperty forward = new SimpleBooleanProperty(false);

    private final ChangeListener<Boolean> filterChangeListener;
    private final ChangeListener<Boolean> blacklistChangeListener;

    // ist der aktuell angezeigte Filter
    public static final String SELECTED_FILTER_NAME = "aktuelle Einstellung"; // dient nur der Info im Config-File
    private SelectedFilter actFilterSettings = new SelectedFilter(SELECTED_FILTER_NAME); // ist der "aktuelle" Filter im Programm

    // ist die Liste der gespeicherten Filter
    private final SelectedFilterList selectedFilterList = new SelectedFilterList();

    // ist die Liste der zuletzt verwendeten Filter
    private final ObservableList<SelectedFilter> selectedFilterBackward =
            FXCollections.observableList(new ArrayList<>(), (SelectedFilter tp) -> new Observable[]{tp.nameProperty()});
    private final ObservableList<SelectedFilter> selectedFilterForward =
            FXCollections.observableList(new ArrayList<>(), (SelectedFilter tp) -> new Observable[]{tp.nameProperty()});

    private boolean thema = false, themaTitle = false, title = false, somewhere = false, url = false;

    public StoredFilters(ProgData progData) {
        this.progData = progData;

        filterChangeListener = (observable, oldValue, newValue) -> {
            postFilterChange();
            selectedFilterForward.clear();
        };
        blacklistChangeListener = (observable, oldValue, newValue) -> {
            postBlacklistChange();
            selectedFilterForward.clear();
        };
        actFilterSettings.filterChangeProperty().addListener(filterChangeListener); // wenn der User den Filter ändert
        actFilterSettings.blacklistChangeProperty().addListener(blacklistChangeListener); // wenn der User die Blackl. ein-/ausschaltet

//        addBackward();
        selectedFilterBackward.addListener((ListChangeListener<SelectedFilter>) c -> {
            if (selectedFilterBackward.size() > 1) {
                backward.setValue(true);
            } else {
                backward.setValue(false);
            }
        });
        selectedFilterForward.addListener((ListChangeListener<SelectedFilter>) c -> {
            if (selectedFilterForward.size() > 0) {
                forward.setValue(true);
            } else {
                forward.setValue(false);
            }
        });
    }

    public void initFilter() {
        addBackward();
    }

    /**
     * liefert den aktuell angezeigten Filter
     *
     * @return
     */
    public SelectedFilter getActFilterSettings() {
        return actFilterSettings;
    }

    /**
     * setzt die aktuellen Filtereinstellungen aus einen Filter (gespeicherten Filter)
     *
     * @param sf
     */
    public synchronized void setActFilterSettings(SelectedFilter sf) {
        if (sf == null) {
            return;
        }
        actFilterSettings.filterChangeProperty().removeListener(filterChangeListener);
        actFilterSettings.blacklistChangeProperty().removeListener(blacklistChangeListener);
        boolean black = actFilterSettings.blacklistOnProperty().getValue();
        boolean blackOnly = actFilterSettings.blacklistOnlyProperty().getValue();

        SelectedFilterFactory.copyFilter(sf, actFilterSettings);
        if (actFilterSettings.blacklistOnProperty().getValue() == black &&
                actFilterSettings.blacklistOnlyProperty().getValue() == blackOnly) {
            // Black hat sich nicht geändert
            postFilterChange();
        } else {
            postBlacklistChange();
        }

        actFilterSettings.filterChangeProperty().addListener(filterChangeListener);
        actFilterSettings.blacklistChangeProperty().addListener(blacklistChangeListener);
    }

    /**
     * sind die gesicherten Filterprofile
     *
     * @return
     */
    public SelectedFilterList getStoredFilterList() {
        return selectedFilterList;
    }

    /**
     * einen neuen Filter zu den gespeicherten hinzufügen
     *
     * @return
     */
    public String addNewStoredFilter(String name) {
        final SelectedFilter sf = new SelectedFilter();
        SelectedFilterFactory.copyFilter(actFilterSettings, sf);

        sf.setName(name.isEmpty() ? getNextName() : name);
        selectedFilterList.add(sf);

        return sf.getName();
    }

    public String getNextName() {
        String ret = "";
        int id = 1;
        boolean found = false;
        while (!found) {

            final String name = "Filter " + id;
            if (!selectedFilterList.stream().filter(f -> name.equalsIgnoreCase(f.getName())).findAny().isPresent()) {
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
    public boolean removeStoredFilter(SelectedFilter sf) {
        if (sf == null) {
            return false;
        }

        if (PAlert.showAlertOkCancel("Löschen", "Filterprofil löschen",
                "Soll das Filterprofil: " +
                        sf.getName() + "\n" +
                        "gelöscht werden?")) {
            selectedFilterList.remove(sf);
            return true;
        }

        return false;
    }


    /**
     * delete all Filter
     */
    public boolean removeAllStoredFilter() {
        if (PAlert.showAlertOkCancel("Löschen", "Filterprofile löschen",
                "Sollen alle Filterprofile gelöscht werden?")) {
            selectedFilterList.clear();
            return true;
        }
        return false;
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
        SelectedFilterFactory.copyFilter(actFilterSettings, sf);
        sf.setName(name);
    }

    /**
     * Filter nach einem Abo einstellen
     *
     * @param oAbo
     */
    public synchronized void loadStoredFilterFromAbo(Optional<AboData> oAbo) {
        if (!oAbo.isPresent()) {
            return;
        }

        final AboData abo = oAbo.get();
        actFilterSettings.filterChangeProperty().removeListener(filterChangeListener);
        actFilterSettings.blacklistChangeProperty().removeListener(blacklistChangeListener);
        actFilterSettings.turnOffFilter(); // Filter erstmal löschen und dann alle abschalten

        actFilterSettings.setChannelAndVis(abo.getChannel());
        actFilterSettings.setThemeAndVis(abo.getTheme());
        actFilterSettings.setThemeExact(abo.isThemeExact());
        actFilterSettings.setThemeTitleAndVis(abo.getThemeTitle());
        actFilterSettings.setTitleAndVis(abo.getTitle());

        actFilterSettings.setSomewhereVis(true);
        actFilterSettings.setSomewhere(abo.getSomewhere());

        actFilterSettings.setMinMaxDurVis(true);
        actFilterSettings.setMinDur(abo.getMinDurationMinute());
        actFilterSettings.setMaxDur(abo.getMaxDurationMinute());

        actFilterSettings.setTimeRangeVis(true);
        actFilterSettings.setTimeRange(abo.getTimeRange());

        selectedFilterForward.clear();
        postFilterChange();
        actFilterSettings.filterChangeProperty().addListener(filterChangeListener);
        actFilterSettings.blacklistChangeProperty().addListener(blacklistChangeListener);
    }

    public synchronized void clearFilter() {
        actFilterSettings.filterChangeProperty().removeListener(filterChangeListener);
        actFilterSettings.blacklistChangeProperty().removeListener(blacklistChangeListener);

        if (actFilterSettings.isTextFilterEmpty()) {
            actFilterSettings.clearFilter(); // Button Black wird nicht verändert
        } else {
            actFilterSettings.clearTxtFilter();
        }

        selectedFilterForward.clear();
        postFilterChange();
        actFilterSettings.filterChangeProperty().addListener(filterChangeListener);
        actFilterSettings.blacklistChangeProperty().addListener(blacklistChangeListener);
    }

    public void goBackward() {
        if (selectedFilterBackward.size() <= 1) {
            // dann gibts noch keine oder ist nur die aktuelle Einstellung drin
            return;
        }

        SelectedFilter sf = selectedFilterBackward.remove(selectedFilterBackward.size() - 1); // ist die aktuelle Einstellung
        selectedFilterForward.add(sf);
        sf = selectedFilterBackward.remove(selectedFilterBackward.size() - 1); // ist die davor
        setActFilterSettings(sf);
    }

    public void goForward() {
        if (selectedFilterForward.isEmpty()) {
            // dann gibts keine
            return;
        }

        final SelectedFilter sf = selectedFilterForward.remove(selectedFilterForward.size() - 1);
        setActFilterSettings(sf);
    }

    private void setFilterChange() {
        this.filterChange.set(!filterChange.get());
    }

    private void addBackward() {
        final SelectedFilter sf = new SelectedFilter();
        SelectedFilterFactory.copyFilter(actFilterSettings, sf);
        if (selectedFilterBackward.isEmpty()) {
            selectedFilterBackward.add(sf);
            return;
        }

        SelectedFilter sfB = selectedFilterBackward.get(selectedFilterBackward.size() - 1);
        if (SelectedFilterFactory.compareFilterWithoutNameOfFilter(sf, sfB)) {
            // dann hat sich nichts geändert (z.B. mehrmals gelöscht)
            return;
        }

        if (!sf.isThemeExact() && checkText(sfB.themeProperty(), sf.themeProperty(), sfB, sf, thema)) {
            setFalse();
            thema = true;
            return;
        }
        if (checkText(sfB.themeTitleProperty(), sf.themeTitleProperty(), sfB, sf, themaTitle)) {
            setFalse();
            themaTitle = true;
            return;
        }
        if (checkText(sfB.titleProperty(), sf.titleProperty(), sfB, sf, title)) {
            setFalse();
            title = true;
            return;
        }
        if (checkText(sfB.somewhereProperty(), sf.somewhereProperty(), sfB, sf, somewhere)) {
            setFalse();
            somewhere = true;
            return;
        }
        if (checkText(sfB.urlProperty(), sf.urlProperty(), sfB, sf, url)) {
            setFalse();
            url = true;
            return;
        }

        // dann wars kein Textfilter
        selectedFilterBackward.add(sf);
    }

    private void setFalse() {
        thema = false;
        themaTitle = false;
        title = false;
        somewhere = false;
        url = false;
    }

    private boolean checkText(StringProperty old, StringProperty nnew, SelectedFilter oldSf, SelectedFilter newSf,
                              boolean check) {
        if (old.get().equals(nnew.get())) {
            return false;
        }
        if (check && !old.get().isEmpty() && !nnew.get().isEmpty() &&
                (old.get().contains(nnew.get()) || nnew.get().contains(old.get()))) {
            // dann hat sich nur ein Teil geändert und wird ersetzt
            old.setValue(nnew.getValue());
        } else {
            selectedFilterBackward.add(newSf);
        }
        return true;
    }

    private void postFilterChange() {
        addBackward();
        setFilterChange();
    }

    private void postBlacklistChange() {
        // dann hat sich auch Blacklist-ein/aus geändert
        progData.filmlist.filterListWithBlacklist(false);
        setFilterChange();
    }

    public BooleanProperty filterChangeProperty() {
        return filterChange;
    }

    public BooleanProperty backwardProperty() {
        return backward;
    }

    public BooleanProperty forwardProperty() {
        return forward;
    }
}
