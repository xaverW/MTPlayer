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

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.p2lib.alert.PAlert;

import java.util.Optional;

public final class FilmFilterWorker {

    // ist der aktuell angezeigte Filter
    public static final String SELECTED_FILTER_NAME = "aktuelle Einstellung"; // dient nur der Info im Config-File
    // ist der "aktuelle" Filter im Programm
    private final FilmFilter actFilterSettings = new FilmFilter(SELECTED_FILTER_NAME);
    // ist die Liste der gespeicherten Filter
    private final FilmFilterList filmFilterList = new FilmFilterList();
    // ist der FastFilter
    private final FastFilmFilter fastFilter = new FastFilmFilter();
    // da werden die Forward/Backward Filter verwaltet
    private final BackwardFilmFilter backwardFilmFilter = new BackwardFilmFilter();

    public FilmFilterWorker() {
        getActFilterSettings().channelProperty().addListener(l -> {
            ThemeListFactory.createThemeList(ProgData.getInstance(), getActFilterSettings().channelProperty().getValueSafe());
        });
    }

    public FilmFilter getActFilterSettings() {
        // liefert den aktuell angezeigten Filter
        return actFilterSettings;
    }

    public FastFilmFilter getFastFilterSettings() {
        return fastFilter;
    }

    public BackwardFilmFilter getBackwardFilmFilter() {
        return backwardFilmFilter;
    }

    public synchronized void setActFilterSettings(FilmFilter sf) {
        // da wird ein gespeicherter Filter / Forward / Backward gesetzt
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

    public FilmFilterList getStoredFilterList() {
        // sind die gesicherten Filterprofile
        return filmFilterList;
    }

    public void addNewStoredFilter(String name) {
        // einen neuen Filter zu den gespeicherten hinzufügen
        final FilmFilter sf = new FilmFilter();
        actFilterSettings.copyTo(sf);
        sf.setName(name.isEmpty() ? getNextName() : name);
        filmFilterList.add(sf);
    }

    public String getNextName() {
        String ret = "";
        int id = 1;
        boolean found = false;
        while (!found) {
            final String name = "Filter " + id;
            if (filmFilterList.stream().noneMatch(f -> name.equalsIgnoreCase(f.getName()))) {
                ret = name;
                found = true;
            }
            ++id;
        }
        return ret;
    }

    public boolean removeStoredFilter(FilmFilter sf) {
        // delete stored filter
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

    public void removeAllStoredFilter() {
        // delete all stored Filter
        if (PAlert.showAlertOkCancel("Löschen", "Filterprofile löschen",
                "Sollen alle Filterprofile gelöscht werden?")) {
            filmFilterList.clear();
        }
    }

    public void saveStoredFilter(FilmFilter sf) {
        // gesicherten Filter mit den aktuellen Einstellungen überschreiben
        if (sf == null) {
            return;
        }

        final String name = sf.getName();
        actFilterSettings.copyTo(sf);
        sf.setName(name);
    }

    public synchronized void loadStoredFilterFromAbo(Optional<AboData> oAbo) {
        // Filter nach einem Abo einstellen
        if (oAbo.isEmpty()) {
            return;
        }

        final AboData abo = oAbo.get();
        actFilterSettings.switchFilterOff(true);
        actFilterSettings.turnOffFilter(); // Filter erstmal löschen und dann alle abschalten

        actFilterSettings.setChannelAndVis(abo.getChannel());
        actFilterSettings.setThemeAndVis(abo.getTheme(), abo.isThemeExact());
        actFilterSettings.setThemeTitleAndVis(abo.getThemeTitle());
        actFilterSettings.setTitleAndVis(abo.getTitle());

        actFilterSettings.setSomewhereVis(true);
        actFilterSettings.setSomewhere(abo.getSomewhere());

        actFilterSettings.setMinMaxDurVis(true);
        actFilterSettings.setMinDur(abo.getMinDurationMinute());
        actFilterSettings.setMaxDur(abo.getMaxDurationMinute());

        actFilterSettings.setTimeRangeVis(true);
        actFilterSettings.setTimeRange(abo.getTimeRange());

        backwardFilmFilter.clearForward();
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

        backwardFilmFilter.clearForward();
        actFilterSettings.switchFilterOff(false);
        postFilterChange();
    }

    private void postFilterChange() {
        backwardFilmFilter.addBackward();
        PListener.notify(PListener.EVENT_FILTER_CHANGED, FilmFilterWorker.class.getSimpleName());
    }

    private void postBlacklistChange() {
        // dann hat sich auch Blacklist-ein/aus geändert
        BlacklistFilterFactory.getBlackFilteredFilmlist();
        PListener.notify(PListener.EVENT_FILTER_CHANGED, FilmFilterWorker.class.getSimpleName());
    }
}
