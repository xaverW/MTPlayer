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
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public final class FilmFilter extends FilmFilterProps implements Filter {

    private boolean filterIsOff = true; // Filter ist EIN - meldet Änderungen
    private final PauseTransition pause = new PauseTransition(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue())); // nach Ablauf wird Änderung gemeldet - oder nach Return

    public FilmFilter() {
        initFilter();
        setName("Filter");
    }

    public FilmFilter(String name) {
        initFilter();
        setName(name);
    }

    public void reportFilterReturn() {
        // sind die ComboBoxen wenn return gedrückt wird
        P2Log.debugLog("reportFilterReturn");
        pause.stop();
        ProgData.getInstance().filterWorker.getBackwardFilmFilter().addBackward();
        PListener.notify(PListener.EVENT_FILTER_CHANGED, FilmFilter.class.getSimpleName());
    }

    private void reportFilterChange() {
        // sind die anderen Filter (ändern, ein-ausschalten), wenn Pause abgelaufen ist / gestoppt ist
        if (!filterIsOff) {
            ProgData.getInstance().filterWorker.getBackwardFilmFilter().addBackward();
            PListener.notify(PListener.EVENT_FILTER_CHANGED, FilmFilter.class.getSimpleName());
        }
    }

    public void switchFilterOff(boolean switchOff) {
        pause.stop();
        this.filterIsOff = switchOff;
    }

    public void turnOffFilter() {
        // alle Filter "abschalten" und löschen
        clearFilter();

        setChannelVis(false);
        setThemeVis(false);
        setThemeTitleVis(false);
        setTitleVis(false);
        setSomewhereVis(false);
        setUrlVis(false);

        setTimeRangeVis(false);
        setMinMaxDurVis(false);
        setMinMaxTimeVis(false);

        setShowDateVis(false);

        setOnlyVis(false);
        setNotVis(false);
    }

    public void clearTxtFilter() {
        pause.setDuration(Duration.millis(0));
        setChannel("");
        setTheme("");
        setExactTheme("");
        setThemeTitle("");
        setTitle("");
        setSomewhere("");
        setUrl("");
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
    }

    public void clearFilter() {
        // alle Filter löschen, Button Black bleibt, wie er ist
        setChannel("");
        setTheme("");
        setExactTheme("");
        setThemeTitle("");
        setTitle("");
        setSomewhere("");
        setUrl("");

        setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);

        setMinDur(0);
        setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        setMinTime(0);
        setMaxTime(FilterCheck.FILTER_TIME_MAX_SEC);

        setShowDate(FilterCheck.FILTER_SHOW_DATE_ALL);

        setOnlyBookmark(false);
        setOnlyHd(false);
        setOnlyNew(false);
        setOnlyUt(false);
        setOnlySignLanguage(false);
        setOnlyLive(false);
        setOnlyActHistory(false);

        setNotAbo(false);
        setNotHistory(false);
        setNotDouble(false);
        setNotGeo(false);
        setNotFuture(false);
    }

    public boolean isTextFilterEmpty() {
        return getChannel().isEmpty() &&
                getResTheme().isEmpty() &&
                getThemeTitle().isEmpty() &&
                getTitle().isEmpty() &&
                getSomewhere().isEmpty() &&
                getUrl().isEmpty();
    }

    public void setChannelAndVis(String set) {
        setChannel(set);
        setChannelVis(true);
    }

    public void setThemeAndVis(String set, boolean exact) {
        if (exact) {
            setExactTheme(set);
        } else {
            setTheme(set);
        }
        setThemeIsExact(exact);
        setThemeVis(true);
    }

    public void setThemeTitleAndVis(String set) {
        setThemeTitle(set);
        setThemeTitleVis(true);
    }

    public void setTitleAndVis(String set) {
        setTitle(set);
        setTitleVis(true);
    }

    private void initFilter() {
        pause.setOnFinished(event -> reportFilterChange());
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        ProgConfig.SYSTEM_FILTER_WAIT_TIME.addListener((observable, oldValue, newValue) -> {
            P2Log.debugLog("SYSTEM_FILTER_WAIT_TIME: " + ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
            pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        });

        clearFilter();
        setChannelVis(true);
        setThemeTitleVis(true);

        setThemeVis(false);
        setThemeIsExact(false);
        setTitleVis(false);
        setSomewhereVis(false);
        setUrlVis(false);

        setTimeRangeVis(true);
        setMinMaxDurVis(true);
        setMinMaxTimeVis(false);
        setMinMaxTimeInvert(false);

        setShowDateVis(false);

        setNotVis(false);
        setOnlyVis(false);

        nameProperty().addListener(l -> setFilterChange(true));

        channelVisProperty().addListener(l -> setFilterChange(true));
        channelProperty().addListener(l -> setFilterChange(true));

        themeVisProperty().addListener(l -> setFilterChange(true));
        themeIsExactProperty().addListener(l -> setFilterChange(true));
        exactThemeProperty().addListener(l -> setFilterChange(true));
        themeProperty().addListener(l -> setFilterChange(false));

        themeTitleVisProperty().addListener(l -> setFilterChange(true));
        themeTitleProperty().addListener(l -> setFilterChange(false));

        titleVisProperty().addListener(l -> setFilterChange(true));
        titleProperty().addListener(l -> setFilterChange(false));

        somewhereVisProperty().addListener(l -> setFilterChange(true));
        somewhereProperty().addListener(l -> setFilterChange(false));

        urlVisProperty().addListener(l -> setFilterChange(true));
        urlProperty().addListener(l -> setFilterChange(false));

        timeRangeVisProperty().addListener(l -> setFilterChange(true));
        timeRangeProperty().addListener(l -> setFilterChange(true));

        minMaxDurVisProperty().addListener((observable, oldValue, newValue) -> setFilterChange(true));
        minDurProperty().addListener(l -> setFilterChange(true));
        maxDurProperty().addListener(l -> setFilterChange(true));

        minMaxTimeVisProperty().addListener((observable, oldValue, newValue) -> setFilterChange(true));
        minMaxTimeInvertProperty().addListener((observable, oldValue, newValue) -> setFilterChange(true));
        minTimeProperty().addListener(l -> setFilterChange(true));
        maxTimeProperty().addListener(l -> setFilterChange(true));

        showDateVisProperty().addListener(l -> setFilterChange(true));
        showDateProperty().addListener(l -> setFilterChange(true));

        onlyVisProperty().addListener(l -> setFilterChange(true));
        onlyBookmarkProperty().addListener(l -> setFilterChange(true));
        onlyHdProperty().addListener(l -> setFilterChange(true));
        onlyNewProperty().addListener(l -> setFilterChange(true));
        onlyUtProperty().addListener(l -> setFilterChange(true));
        onlySignLanguageProperty().addListener(l -> setFilterChange(true));
        onlyLiveProperty().addListener(l -> setFilterChange(true));
        onlyActHistoryProperty().addListener(l -> setFilterChange(true));

        notVisProperty().addListener(l -> setFilterChange(true));
        notAboProperty().addListener(l -> setFilterChange(true));
        notHistoryProperty().addListener(l -> setFilterChange(true));
        notDoubleProperty().addListener(l -> setFilterChange(true));
        notGeoProperty().addListener(l -> setFilterChange(true));
        notFutureProperty().addListener(l -> setFilterChange(true));

        blacklistOnOffProperty().addListener(l -> reportBlacklistChange());
    }

    private void reportBlacklistChange() {
        if (!filterIsOff) { // todo ??
            BlacklistFilterFactory.makeBlackFilteredFilmlist();
            PListener.notify(PListener.EVENT_FILTER_CHANGED, FilmFilter.class.getSimpleName());
        }
    }

    private void setFilterChange(boolean startNow) {
        // wird ausgelöst, wenn ein Filter ein/ausgeschaltet wird oder was eingetragen wird
        if (!startNow && ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
            //dann wird erst nach "RETURN" gestartet
            pause.stop();

        } else {
            // dann wird sofort gestartet (nach Pause)
            pause.playFromStart();
        }
    }
}
