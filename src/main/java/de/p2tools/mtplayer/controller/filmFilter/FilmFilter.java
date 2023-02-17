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

package de.p2tools.mtplayer.controller.filmFilter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2Lib.mtFilter.FilterCheck;
import de.p2tools.p2Lib.tools.log.PDebugLog;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;

public final class FilmFilter extends FilmFilterProps {

    private final BooleanProperty filterChange = new SimpleBooleanProperty(false);
    private final BooleanProperty blacklistChange = new SimpleBooleanProperty(false);
    private boolean reportChange = true;
    private final PauseTransition pause = new PauseTransition(Duration.millis(200));


    public FilmFilter() {
        initFilter();
        setName("Filter");
    }

    public FilmFilter(String name) {
        initFilter();
        setName(name);
    }

    public boolean isReportChange() {
        return reportChange;
    }

    public void setReportChange(boolean reportChange) {
        this.reportChange = reportChange;
    }

    public BooleanProperty filterChangeProperty() {
        return filterChange;
    }

    public BooleanProperty blacklistChangeProperty() {
        return blacklistChange;
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

    public void reportFilterReturn() {
        PDebugLog.sysLog("reportFilterReturn");
        pause.stop();
        filterChange.setValue(!filterChange.getValue());
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

    public void clearFilter() {
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

    private void initFilter() {
        pause.setOnFinished(event -> reportFilterChange());
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        ProgConfig.SYSTEM_FILTER_WAIT_TIME.addListener((observable, oldValue, newValue) -> {
            PDebugLog.sysLog("SYSTEM_FILTER_WAIT_TIME: " + ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
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
        channelProperty().addListener(l -> {
            setFilterChange();
        });

        themeVisProperty().addListener(l -> setFilterChange());
        themeExactProperty().addListener(l -> setFilterChange());
        themeProperty().addListener(l -> {
            if (themeExactProperty().getValue()) {
                setFilterChange();
            } else {
                setTxtFilterChange();
            }

//            PDebugLog.sysLog("themeProperty: " + themeProperty().getValue());
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
        themeTitleProperty().addListener(l -> {
            setTxtFilterChange();
        });


        titleVisProperty().addListener(l -> setFilterChange());
        titleProperty().addListener(l -> {
            setTxtFilterChange();
        });

        somewhereVisProperty().addListener(l -> setFilterChange());
        somewhereProperty().addListener(l -> {
            setTxtFilterChange();
        });

        urlVisProperty().addListener(l -> setFilterChange());
        urlProperty().addListener(l -> {
            setTxtFilterChange();
        });

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

        blacklistOnProperty().addListener(l -> reportBlacklistChange());
        blacklistOnlyProperty().addListener(l -> reportBlacklistChange());
    }

    private void setTxtFilterChange() {
        //wird auch ausgelöst durch Eintrag in die FilterHistory, da wird ein neuer SelectedFilter angelegt
//        PDebugLog.sysLog("setTxtFilterChange");
        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
            //dann wird erst nach "RETURN" gestartet
            pause.stop();

        } else {
            pause.playFromStart();
        }
    }

    private void setFilterChange() {
        //wird auch ausgelöst durch Eintrag in die FilterHistory, da wird ein neuer SelectedFilter angelegt
//        PDebugLog.sysLog("setFilterChange");
        pause.playFromStart();
    }

    private void reportFilterChange() {
        if (reportChange) {
            filterChange.setValue(!filterChange.getValue());
        }
    }

    private void reportBlacklistChange() {
        if (reportChange) {
//            PDebugLog.sysLog("reportBlacklistChange");
            blacklistChange.setValue(!blacklistChange.getValue());
        }
    }

    public boolean isTextFilterEmpty() {
        return getChannel().isEmpty() &&
                getTheme().isEmpty() &&
                getThemeTitle().isEmpty() &&
                getTitle().isEmpty() &&
                getSomewhere().isEmpty() &&
                getUrl().isEmpty();
    }


    public boolean clearTxtFilter() {
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
        return ret;
    }
}
