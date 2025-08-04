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

package de.p2tools.mtplayer.controller.filteraudio;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.filterfilm.FilmFastFilter;
import de.p2tools.mtplayer.controller.filterfilm.FilmFilter;
import de.p2tools.mtplayer.controller.filterfilm.FilmFilterList;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;

public final class AudioFilterWorker {

    // ist der aktuell angezeigte Filter
    public static final String SELECTED_FILTER_NAME = "aktuelle Einstellung: Audio"; // dient nur der Info im Config-File
    public static final String STORED_FILTER_NAME = "gespeicherte aktuelle Einstellung: Audio"; // dient nur der Info im Config-File
    public static final String STORED_SMALL_FILTER_NAME = "gespeicherte aktuelle Einstellung, kleiner Filter: Audio"; // dient nur der Info im Config-File

    // ist der "aktuelle" Filter im Programm
    private final FilmFilter actFilterSettings = new FilmFilter(true, SELECTED_FILTER_NAME);
    private final FilmFilter storedFilterSettings = new FilmFilter(true, STORED_FILTER_NAME);
    private final FilmFilter storedSmallFilterSettings = new FilmFilter(true, STORED_SMALL_FILTER_NAME);

    // ist die Liste der gespeicherten Filter
    private final FilmFilterList filmFilterList = new FilmFilterList("AudioFilterList");
    // ist die Liste der BACK Filter
    private final FilmFilterList backwardFilterList = new FilmFilterList("BackwardAudioFilterList");
    // ist die Liste der FORWARD Filter
    private final FilmFilterList forwardFilterList = new FilmFilterList("ForwardAudioFilterList");

    // ist der FastFilter
    private final FilmFastFilter fastFilter = new FilmFastFilter(true, "AudioFastFilter");

    // da werden die Forward/Backward Filter verwaltet
    private final AudioFilterBackward backwardFilmFilter = new AudioFilterBackward();

    public AudioFilterWorker() {
        getActFilterSettings().channelProperty().addListener(l -> {
            ThemeListFactory.createThemeList(ProgData.getInstance(), getActFilterSettings().channelProperty().getValueSafe());
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

    public FilmFastFilter getFastFilterSettings() {
        return fastFilter;
    }

    public AudioFilterBackward getBackwardFilmFilter() {
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
        backwardFilmFilter.addBackward();
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_AUDIO_CHANGED);
    }

    private void postBlacklistChange() {
        // dann hat sich auch Blacklist-ein/aus geändert
        BlacklistFilterFactory.makeBlackFilteredFilmlist();
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_AUDIO_CHANGED);
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
