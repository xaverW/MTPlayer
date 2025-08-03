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
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;

import java.util.Optional;

public final class AudioFilterWorker {

    // ist der aktuell angezeigte Filter
    public static final String SELECTED_FILTER_NAME = "aktuelle Einstellung"; // dient nur der Info im Config-File
    public static final String STORED_FILTER_NAME = "gespeicherte aktuelle Einstellung"; // dient nur der Info im Config-File
    public static final String STORED_SMALL_FILTER_NAME = "gespeicherte aktuelle Einstellung, kleiner Filter"; // dient nur der Info im Config-File
    // ist der "aktuelle" Filter im Programm
    private final AudioFilter actFilterSettings = new AudioFilter(SELECTED_FILTER_NAME);
    private final AudioFilter storedFilterSettings = new AudioFilter(STORED_FILTER_NAME);
    private final AudioFilter storedSmallFilterSettings = new AudioFilter(STORED_SMALL_FILTER_NAME);

    // ist die Liste der gespeicherten Filter
    private final AudioFilterList filmFilterList = new AudioFilterList();
    // ist die Liste der BACK Filter
    private final AudioFilterList backwardFilterList = new AudioFilterList("BackwardFilterList");
    // ist die Liste der FORWARD Filter
    private final AudioFilterList forwardFilterList = new AudioFilterList("ForwardFilterList");

    // ist der FastFilter
    private final AudioFastFilter fastFilter = new AudioFastFilter();
    // da werden die Forward/Backward Filter verwaltet
    private final AudioFilterBackward backwardFilmFilter = new AudioFilterBackward();

    public AudioFilterWorker() {
        getActFilterSettings().channelProperty().addListener(l -> {
            ThemeListFactory.createThemeList(ProgData.getInstance(), getActFilterSettings().channelProperty().getValueSafe());
        });
    }

    public AudioFilter getActFilterSettings() {
        // liefert den aktuell angezeigten Filter
        return actFilterSettings;
    }

    public AudioFilter getStoredFilterSettings() {
        // liefert den gespeicherten Filter vom SmallFilterWechsel
        return storedFilterSettings;
    }

    public AudioFilter getStoredSmallFilterSettings() {
        // liefert den gespeicherten Filter vom SmallFilterWechsel
        return storedSmallFilterSettings;
    }

    public AudioFilterList getFilmFilterList() {
        // sind die gesicherten Filterprofile
        return filmFilterList;
    }

    public AudioFilterList getBackwardFilterList() {
        return backwardFilterList;
    }

    public AudioFilterList getForwardFilterList() {
        return forwardFilterList;
    }

    public AudioFastFilter getFastFilterSettings() {
        return fastFilter;
    }

    public AudioFilterBackward getBackwardFilmFilter() {
        return backwardFilmFilter;
    }

    public synchronized void setActFilterSettings(AudioFilter sf) {
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
        backwardFilmFilter.addBackward();
//        PListener.notify(PListener.EVENT_FILTER_CHANGED, FilterWorker.class.getSimpleName());
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_AUDIO_CHANGED);
    }

    private void postBlacklistChange() {
        // dann hat sich auch Blacklist-ein/aus geändert
        BlacklistFilterFactory.makeBlackFilteredFilmlist();
//        PListener.notify(PListener.EVENT_FILTER_CHANGED, FilterWorker.class.getSimpleName());
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_AUDIO_CHANGED);
    }

    public static void setSmallFilter(AudioFilter filmFilter) {
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
