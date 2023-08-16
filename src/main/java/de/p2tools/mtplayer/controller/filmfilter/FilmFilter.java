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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.gui.tools.MTListener;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public final class FilmFilter extends FilmFilterProps {

    private boolean filterIsOff = true; // Filter ist EIN - meldet Änderungen
    private final PauseTransition pause = new PauseTransition(Duration.millis(200)); // nach Ablauf wird Änderung gemeldet - oder nach Return

    public FilmFilter() {
        initFilter();
        setName("Filter");
    }

    public FilmFilter(String name) {
        initFilter();
        setName(name);
    }

    public void reportFilterReturn() {
        if (getName().equals(FilmFilterWorker.SELECTED_FILTER_NAME)) {
            System.out.println("----------------------");
            System.out.println("reportFilterReturn");
            System.out.println(filterIsOff ? "filter off" : "filter on");
            System.out.println("     -----------------");
        }
        PLog.debugLog("reportFilterReturn");
        pause.stop();
        MTListener.notify(MTListener.EVENT_FILTER_CHANGED, FilmFilterWorker.class.getSimpleName());
    }

    private void reportFilterChange() {
        // da wird die Änderung gemeldet
        if (getName().equals(FilmFilterWorker.SELECTED_FILTER_NAME)) {
            System.out.println("----------------------");
            System.out.println("reportFilterChange");
            System.out.println(filterIsOff ? "filter off" : "filter on");
            System.out.println("     -----------------");
        }
        if (!filterIsOff) {
            System.out.println("->reportFilterChange");
            MTListener.notify(MTListener.EVENT_FILTER_CHANGED, FilmFilterWorker.class.getSimpleName());
        }
    }

    private void reportBlacklistChange() {
        if (getName().equals(FilmFilterWorker.SELECTED_FILTER_NAME)) {
            System.out.println("----------------------");
            System.out.println("reportBlacklistChange");
            System.out.println(filterIsOff ? "filter off" : "filter on");
            System.out.println("     -----------------");
        }
        if (!filterIsOff) { // todo ??
            BlacklistFilterFactory.getBlackFilteredFilmlist();
            MTListener.notify(MTListener.EVENT_FILTER_CHANGED, FilmFilterWorker.class.getSimpleName());
        }
    }

    private void setFilterChange() {
        //wird auch ausgelöst durch Eintrag in die FilterHistory, da wird ein neuer SelectedFilter angelegt
        if (getName().equals(FilmFilterWorker.SELECTED_FILTER_NAME)) {
            System.out.println("----------------------");
            System.out.println("setFilterChange");
            System.out.println(filterIsOff ? "filter off" : "filter on");
            System.out.println("     -----------------");
        }
        pause.playFromStart();
    }

    private void setTxtFilterChange() {
        System.out.println("----------------------");
        System.out.println("setTxtFilterChange");
        System.out.println(filterIsOff ? "filter off" : "filter on");
        System.out.println("     -----------------");
        //wird auch ausgelöst durch Eintrag in die FilterHistory, da wird ein neuer SelectedFilter angelegt
        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
            //dann wird erst nach "RETURN" gestartet
            pause.stop();

        } else {
            pause.playFromStart();
        }
    }

    public void switchFilterOff(boolean switchOff) {
        pause.stop();
        this.filterIsOff = switchOff;
        if (getName().equals(FilmFilterWorker.SELECTED_FILTER_NAME)) {
            System.out.println("----------------------");
            System.out.println("switchFilterOff");
            System.out.println(filterIsOff ? "filter off" : "filter on");
            System.out.println("     -----------------");
        }
    }

    public void turnOffFilter() {
        if (getName().equals(FilmFilterWorker.SELECTED_FILTER_NAME)) {
            System.out.println("----------------------");
            System.out.println("turnOffFilter");
            System.out.println(filterIsOff ? "filter off" : "filter on");
            System.out.println("     -----------------");
        }        // alle Filter "abschalten" und löschen
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
        if (getName().equals(FilmFilterWorker.SELECTED_FILTER_NAME)) {
            System.out.println("----------------------");
            System.out.println("clearTxtFilter");
            System.out.println(filterIsOff ? "filter off" : "filter on");
            System.out.println("     -----------------");
        }
        pause.setDuration(Duration.millis(0));
        boolean ret = false;
        if (!getChannel().isEmpty()) {
            ret = true;
            setChannel("");
        }
        if (!getTheme().isEmpty()) {
            ret = true;
            setTheme("");
        }
        if (!getThemeTitle().isEmpty()) {
            ret = true;
            setThemeTitle("");
        }
        if (!getTitle().isEmpty()) {
            ret = true;
            setTitle("");
        }
        if (!getSomewhere().isEmpty()) {
            ret = true;
            setSomewhere("");
        }
        if (!getUrl().isEmpty()) {
            ret = true;
            setUrl("");
        }
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
    }

    public void clearFilter() {
        if (getName().equals(FilmFilterWorker.SELECTED_FILTER_NAME)) {
            System.out.println("----------------------");
            System.out.println("clearFilter");
            System.out.println(filterIsOff ? "filter off" : "filter on");
            System.out.println("     -----------------");
        }
        // alle Filter löschen, Button Black bleibt, wie er ist
        setChannel("");
        setTheme("");
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
                getTheme().isEmpty() &&
                getThemeTitle().isEmpty() &&
                getTitle().isEmpty() &&
                getSomewhere().isEmpty() &&
                getUrl().isEmpty();
    }

    public void setChannelAndVis(String set) {
        setChannel(set);
        setChannelVis(true);
    }

    public void setThemeAndVis(String set) {
        setTheme(set);
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
            PLog.debugLog("SYSTEM_FILTER_WAIT_TIME: " + ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
            pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        });

        clearFilter();
        setChannelVis(true);
        setThemeTitleVis(true);

        setThemeVis(false);
        setThemeExact(false);
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

        nameProperty().addListener(l -> setFilterChange());

        channelVisProperty().addListener(l -> setFilterChange());
        channelProperty().addListener(l -> setFilterChange());

        themeVisProperty().addListener(l -> setFilterChange());
        themeExactProperty().addListener(l -> setFilterChange());
        themeProperty().addListener(l -> {
            if (themeExactProperty().getValue()) {
                setFilterChange();
            } else {
                setTxtFilterChange();
            }

//            // todo -> beim Ändern der "Thema" liste wird das 3xaufgerufen
//            if (!themeExactProperty().getValue()) {
//                PDebugLog.sysLog("Pause themeProperty");
//                pause.setOnFinished(event -> reportFilterChange());
//                pause.playFromStart();
//
//            } else {
//                if (themeVisProperty().get()) {
//                    reportFilterChange();
//                }
//            }
        });

        themeTitleVisProperty().addListener(l -> setFilterChange());
        themeTitleProperty().addListener(l -> setTxtFilterChange());

        titleVisProperty().addListener(l -> setFilterChange());
        titleProperty().addListener(l -> setTxtFilterChange());

        somewhereVisProperty().addListener(l -> setFilterChange());
        somewhereProperty().addListener(l -> setTxtFilterChange());

        urlVisProperty().addListener(l -> setFilterChange());
        urlProperty().addListener(l -> setTxtFilterChange());

        timeRangeVisProperty().addListener(l -> setFilterChange());
        timeRangeProperty().addListener(l -> setFilterChange());

        minMaxDurVisProperty().addListener((observable, oldValue, newValue) -> setFilterChange());
        minDurProperty().addListener(l -> setFilterChange());
        maxDurProperty().addListener(l -> setFilterChange());

        minMaxTimeVisProperty().addListener((observable, oldValue, newValue) -> setFilterChange());
        minMaxTimeInvertProperty().addListener((observable, oldValue, newValue) -> setFilterChange());
        minTimeProperty().addListener(l -> setFilterChange());
        maxTimeProperty().addListener(l -> setFilterChange());

        showDateVisProperty().addListener(l -> setFilterChange());
        showDateProperty().addListener(l -> setFilterChange());

        onlyVisProperty().addListener(l -> setFilterChange());
        onlyBookmarkProperty().addListener(l -> setFilterChange());
        onlyHdProperty().addListener(l -> setFilterChange());
        onlyNewProperty().addListener(l -> setFilterChange());
        onlyUtProperty().addListener(l -> setFilterChange());
        onlyLiveProperty().addListener(l -> setFilterChange());
        onlyActHistoryProperty().addListener(l -> setFilterChange());

        notVisProperty().addListener(l -> setFilterChange());
        notAboProperty().addListener(l -> setFilterChange());
        notHistoryProperty().addListener(l -> setFilterChange());
        notDoubleProperty().addListener(l -> setFilterChange());
        notGeoProperty().addListener(l -> setFilterChange());
        notFutureProperty().addListener(l -> setFilterChange());

        blacklistOnOffProperty().addListener(l -> reportBlacklistChange());
    }
}
