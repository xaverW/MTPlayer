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

package de.p2tools.mtplayer.controller.filter;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;

import java.util.Optional;

public class FilterWorker {

    // ist der "aktuelle" Filter im Programm
    public final FilmFilter actFilterSettings;
    public final FilmFilter storedFilterSettings;
    public final FilmFilter storedSmallFilterSettings;

    // sind  die Liste der gespeicherten Filter, BACKWARD, FORWARD
    public final FilmFilterList filmFilterList;
    public final FilmFilterList backwardFilterList;
    public final FilmFilterList forwardFilterList;

    // ist der FastFilter
    public final FastFilter fastFilter;
    // da werden die Forward/Backward Filter verwaltet
    private final FilterBackward filterBackward = new FilterBackward(false);
    private final boolean audio;

    public FilterWorker(boolean audio) {
        this.audio = audio;
        actFilterSettings = new FilmFilter("ActFilterSettings" + (audio ? "Audio" : "Film"), audio);
        storedFilterSettings = new FilmFilter("StoredFilterSettings" + (audio ? "Audio" : "Film"), audio);
        storedSmallFilterSettings = new FilmFilter("StoredSmallFilterSettings" + (audio ? "Audio" : "Film"), audio);

        if (audio) {
            filmFilterList = new FilmFilterList("FilmFilterList" + "Audio");
            backwardFilterList = new FilmFilterList("FilmFilterListBackward" + "Audio");
            forwardFilterList = new FilmFilterList("FilmFilterListForward" + "Audio");
        } else {
            filmFilterList = new FilmFilterList("FilmFilterList" + "Film" + ":SelectedFilterList");
            backwardFilterList = new FilmFilterList("FilmFilterListBackward" + "Film");
            forwardFilterList = new FilmFilterList("FilmFilterListForward" + "Film");
        }

        fastFilter = new FastFilter("FastFilter" + (audio ? "Audio" : "Film"), audio);

        getActFilterSettings().channelProperty().addListener(l -> {
            ThemeListFactory.createThemeList(audio, ProgData.getInstance(), getActFilterSettings().channelProperty().getValueSafe());
        });
    }

    public FilmFilter getActFilterSettings() {
        // liefert den aktuell angezeigten Filter
        return actFilterSettings;
    }

    public FilmFilter getStoredFilterSettings() {
        // liefert den gespeicherten Filter vom SmallFilterWechsel
        return storedFilterSettings;
    }

    public FilmFilter getStoredSmallFilterSettings() {
        // liefert den gespeicherten Filter vom SmallFilterWechsel
        return storedSmallFilterSettings;
    }

    public FilmFilterList getFilmFilterList() {
        // sind die gesicherten Filterprofile
        return filmFilterList;
    }

    public FilmFilterList getBackwardFilterList() {
        return backwardFilterList;
    }

    public FilmFilterList getForwardFilterList() {
        return forwardFilterList;
    }

    public FastFilter getFastFilterSettings() {
        return fastFilter;
    }

    public FilterBackward getBackwardFilmFilter() {
        return filterBackward;
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

    public synchronized void setFilterFromAbo(Optional<AboData> oAbo) {
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

        forwardFilterList.clear();
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

        forwardFilterList.clear();
        actFilterSettings.switchFilterOff(false);
        postFilterChange();
    }

    private void postFilterChange() {
        filterBackward.addBackward();
        if (audio) {
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_AUDIO_CHANGED);
        } else {
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_FILM_CHANGED);
        }
    }

    private void postBlacklistChange() {
        // dann hat sich auch Blacklist-ein/aus geändert
        System.out.println("=====> FilterWorker");
        BlacklistFilterFactory.makeBlackFilteredFilmlist(audio);
        if (audio) {
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_AUDIO_CHANGED);
        } else {
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_FILM_CHANGED);
        }
    }

    public static void setSmallFilter(FilmFilter filmFilter) {
        filmFilter.setChannelVis(true);
        filmFilter.setThemeTitleVis(true);
        filmFilter.setThemeVis(true);
        filmFilter.setThemeIsExact(true);
        filmFilter.setTimeRangeVis(true);
        filmFilter.setMinMaxDurVis(true);

        filmFilter.setTitleVis(false);
        filmFilter.setSomewhereVis(false);
        filmFilter.setUrlVis(false);
        filmFilter.setMinMaxTimeVis(false);
        filmFilter.setMinMaxTimeInvert(false);
        filmFilter.setShowDateVis(false);

        filmFilter.setNotVis(false);
        filmFilter.setOnlyVis(false);
    }
}
