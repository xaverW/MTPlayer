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

package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.gui.tools.PListener;
import de.p2tools.p2lib.alert.PAlert;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Optional;

public final class FilmFilterWorker {
    final int MAX_FILTER_GO_BACK = 5;
    private final BooleanProperty backward = new SimpleBooleanProperty(false);
    private final BooleanProperty forward = new SimpleBooleanProperty(false);

    // ist der aktuell angezeigte Filter
    public static final String SELECTED_FILTER_NAME = "aktuelle Einstellung"; // dient nur der Info im Config-File
    private final FastFilmFilter fastFilter = new FastFilmFilter(); // ist der FastFilter
    private final FilmFilter actFilterSettings = new FilmFilter(SELECTED_FILTER_NAME); // ist der "aktuelle" Filter im Programm

    // ist die Liste der gespeicherten Filter
    private final FilmFilterList filmFilterList = new FilmFilterList();

    // ist die Liste der zuletzt verwendeten Filter
    private final ObservableList<FilmFilter> filmFilterBackward =
            FXCollections.observableList(new ArrayList<>() {
                @Override
                public void add(int index, FilmFilter e) {
                    while (this.size() > MAX_FILTER_GO_BACK) {
                        remove(0);
                    }
                    super.add(e);
                }
            }, (FilmFilter tp) -> new Observable[]{tp.nameProperty()});
    private final ObservableList<FilmFilter> filmFilterForward =
            FXCollections.observableList(new ArrayList<>() {
                @Override
                public void add(int index, FilmFilter e) {
                    while (this.size() > MAX_FILTER_GO_BACK) {
                        remove(0);
                    }
                    super.add(e);
                }
            }, (FilmFilter tp) -> new Observable[]{tp.nameProperty()});

    private boolean thema = false, themaTitle = false, title = false, somewhere = false, url = false;

    public FilmFilterWorker() {
        filmFilterBackward.addListener((ListChangeListener<FilmFilter>) c -> {
            if (filmFilterBackward.size() > 1) {
                backward.setValue(true);
            } else {
                backward.setValue(false);
            }
        });
        filmFilterForward.addListener((ListChangeListener<FilmFilter>) c -> {
            if (!filmFilterForward.isEmpty()) {
                forward.setValue(true);
            } else {
                forward.setValue(false);
            }
        });
    }

    public BooleanProperty backwardProperty() {
        return backward;
    }

    public BooleanProperty forwardProperty() {
        return forward;
    }

    /**
     * liefert den aktuell angezeigten Filter
     *
     * @return
     */
    public FilmFilter getActFilterSettings() {
        return actFilterSettings;
    }

    public FastFilmFilter getFastFilterSettings() {
        return fastFilter;
    }

    /**
     * setzt die aktuellen Filtereinstellungen aus einem Filter (gespeicherten Filter)
     *
     * @param sf
     */
    public synchronized void setActFilterSettings(FilmFilter sf) {
        if (sf == null) {
            return;
        }
        actFilterSettings.switchFilterOff(true);
        int black = actFilterSettings.blacklistOnOffProperty().getValue();
        sf.copyTo(actFilterSettings);
        actFilterSettings.switchFilterOff(false);

        if (actFilterSettings.blacklistOnOffProperty().getValue() == black) {
            // Black hat sich nicht geändert
            postFilterChange();
        } else {
            postBlacklistChange();
        }
    }

    /**
     * sind die gesicherten Filterprofile
     *
     * @return
     */
    public FilmFilterList getStoredFilterList() {
        return filmFilterList;
    }

    /**
     * einen neuen Filter zu den gespeicherten hinzufügen
     *
     * @return
     */
    public String addNewStoredFilter(String name) {
        final FilmFilter sf = new FilmFilter();
        actFilterSettings.copyTo(sf);
        sf.setName(name.isEmpty() ? getNextName() : name);
        filmFilterList.add(sf);
        return sf.getName();
    }

    public String getNextName() {
        String ret = "";
        int id = 1;
        boolean found = false;
        while (!found) {
            final String name = "Filter " + id;
            if (!filmFilterList.stream().filter(f -> name.equalsIgnoreCase(f.getName())).findAny().isPresent()) {
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
    public boolean removeStoredFilter(FilmFilter sf) {
        if (sf == null) {
            return false;
        }
        if (PAlert.showAlertOkCancel("Löschen", "Filterprofil löschen",
                "Soll das Filterprofil: " +
                        sf.getName() + "\n" +
                        "gelöscht werden?")) {
            filmFilterList.remove(sf);
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
            filmFilterList.clear();
            return true;
        }
        return false;
    }

    /**
     * gesicherten Filter mit den aktuellen Einstellungen überschreiben
     *
     * @param sf
     */
    public void saveStoredFilter(FilmFilter sf) {
        if (sf == null) {
            return;
        }
        final String name = sf.getName();
        actFilterSettings.copyTo(sf);
        sf.setName(name);
    }

    /**
     * Filter nach einem Abo einstellen
     *
     * @param oAbo
     */
    public synchronized void loadStoredFilterFromAbo(Optional<AboData> oAbo) {
        if (oAbo.isEmpty()) {
            return;
        }

        final AboData abo = oAbo.get();
        actFilterSettings.switchFilterOff(true);
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

        filmFilterForward.clear();
        actFilterSettings.switchFilterOff(false);

        postFilterChange();
    }

    public synchronized void clearFilter() {
        actFilterSettings.switchFilterOff(true);

        if (actFilterSettings.isTextFilterEmpty()) {
            actFilterSettings.clearFilter(); // Button Black wird nicht verändert
        } else {
            actFilterSettings.clearTxtFilter();
        }

        filmFilterForward.clear();
        filmFilterBackward.clear();

        actFilterSettings.switchFilterOff(false);
        postFilterChange();
    }

    public void goBackward() {
        if (filmFilterBackward.size() <= 1) {
            // dann gibts noch keine oder ist nur die aktuelle Einstellung drin
            return;
        }

        FilmFilter sf = filmFilterBackward.remove(filmFilterBackward.size() - 1); // ist die aktuelle Einstellung
        filmFilterForward.add(sf);
        sf = filmFilterBackward.remove(filmFilterBackward.size() - 1); // ist die davor
        setActFilterSettings(sf);
    }

    public void goForward() {
        if (filmFilterForward.isEmpty()) {
            // dann gibts keine
            return;
        }

        final FilmFilter sf = filmFilterForward.remove(filmFilterForward.size() - 1);
        setActFilterSettings(sf);
    }

    public void addBackward() {
        final FilmFilter sf = new FilmFilter();
        actFilterSettings.copyTo(sf);
        if (filmFilterBackward.isEmpty()) {
            filmFilterBackward.add(sf);
            return;
        }

        FilmFilter sfB = filmFilterBackward.get(filmFilterBackward.size() - 1);
        if (sf.isSame(sfB, false)) {
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
        filmFilterBackward.add(sf);
    }

    private void setFalse() {
        thema = false;
        themaTitle = false;
        title = false;
        somewhere = false;
        url = false;
    }

    private boolean checkText(StringProperty old, StringProperty nnew, FilmFilter oldSf, FilmFilter newSf,
                              boolean check) {
        if (old.get().equals(nnew.get())) {
            return false;
        }
        if (check && !old.get().isEmpty() && !nnew.get().isEmpty() &&
                (old.get().contains(nnew.get()) || nnew.get().contains(old.get()))) {
            // dann hat sich nur ein Teil geändert und wird ersetzt
            old.setValue(nnew.getValue());
        } else {
            filmFilterBackward.add(newSf);
        }
        return true;
    }

    public void postFilterChange() {
        addBackward();
        PListener.notify(PListener.EVENT_FILTER_CHANGED, FilmFilterWorker.class.getSimpleName());
    }

    private void postBlacklistChange() {
        // dann hat sich auch Blacklist-ein/aus geändert
        BlacklistFilterFactory.getBlackFilteredFilmlist();
        PListener.notify(PListener.EVENT_FILTER_CHANGED, FilmFilterWorker.class.getSimpleName());
    }
}
