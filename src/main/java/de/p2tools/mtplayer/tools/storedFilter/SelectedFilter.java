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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.tools.filmListFilter.FilmFilter;
import de.p2tools.p2Lib.tools.log.PDebugLog;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;

public final class SelectedFilter extends SelectedFilterProps {

    private final BooleanProperty filterChange = new SimpleBooleanProperty(false);
    private final BooleanProperty blacklistChange = new SimpleBooleanProperty(false);
    private boolean reportChange = true;
    private final PauseTransition pause = new PauseTransition(Duration.millis(200));
    IntegerProperty filterWaitTime = ProgConfig.SYSTEM_FILTER_WAIT_TIME;


    public SelectedFilter() {
        initFilter();
        setName("Filter");
    }

    public SelectedFilter(String name) {
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

        setTimeRange(FilmFilter.FILTER_TIME_RANGE_ALL_VALUE);

        setMinDur(0);
        setMaxDur(FilmFilter.FILTER_DURATION_MAX_MINUTE);

        setMinTime(0);
        setMaxTime(FilmFilter.FILTER_FILMTIME_MAX_SEC);

        setShowDate(FilmFilter.FILTER_SHOW_DATE_ALL);

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
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        filterWaitTime.addListener((observable, oldValue, newValue) -> {
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

        nameProperty().addListener(l -> reportFilterChange());

        channelVisProperty().addListener(l -> reportFilterChange());
        channelProperty().addListener(l -> {
            PDebugLog.sysLog("channelProperty: " + channelProperty().getValue());
            reportFilterChange();
        });

        themeVisProperty().addListener(l -> reportFilterChange());
        themeExactProperty().addListener(l -> reportFilterChange());
        themeProperty().addListener(l -> {
            PDebugLog.sysLog("themeProperty: " + themeProperty().getValue());
            // todo -> beim Ändern der "Thema" liste wird das 3xaufgerufen
            if (!themeExactProperty().getValue()) {
                PDebugLog.sysLog("Pause themeProperty");
                pause.setOnFinished(event -> reportFilterChange());
                pause.playFromStart();

            } else {
                if (themeVisProperty().get()) {
                    reportFilterChange();
                }
            }
        });

        themeTitleVisProperty().addListener(l -> reportFilterChange());
        themeTitleProperty().addListener(l -> {
            PDebugLog.sysLog("Pause themeTitleProperty");
            pause.setOnFinished(event -> reportFilterChange());
            pause.playFromStart();
        });


        titleVisProperty().addListener(l -> reportFilterChange());
        titleProperty().addListener(l -> {
            PDebugLog.sysLog("Pause titleProperty");
            pause.setOnFinished(event -> reportFilterChange());
            pause.playFromStart();
        });

        somewhereVisProperty().addListener(l -> reportFilterChange());
        somewhereProperty().addListener(l -> {
            PDebugLog.sysLog("Pause somewhereProperty: " + pause.getDuration().toString());
            pause.setOnFinished(event -> reportFilterChange());
            pause.playFromStart();
        });

        urlVisProperty().addListener(l -> reportFilterChange());
        urlProperty().addListener(l -> {
            PDebugLog.sysLog("Pause urlProperty");
            pause.setOnFinished(event -> reportFilterChange());
            pause.playFromStart();
        });

        timeRangeVisProperty().addListener(l -> reportFilterChange());
        timeRangeProperty().addListener(l -> reportFilterChange());

        minMaxDurVisProperty().addListener((observable, oldValue, newValue) -> reportFilterChange());
        minDurProperty().addListener(l -> reportFilterChange());
        maxDurProperty().addListener(l -> reportFilterChange());

        minMaxTimeVisProperty().addListener((observable, oldValue, newValue) -> reportFilterChange());
        minMaxTimeInvertProperty().addListener((observable, oldValue, newValue) -> reportFilterChange());
        minTimeProperty().addListener(l -> reportFilterChange());
        maxTimeProperty().addListener(l -> reportFilterChange());

        showDateVisProperty().addListener(l -> reportFilterChange());
        showDateProperty().addListener(l -> reportFilterChange());

        onlyVisProperty().addListener(l -> reportFilterChange());
        onlyBookmarkProperty().addListener(l -> reportFilterChange());
        onlyHdProperty().addListener(l -> reportFilterChange());
        onlyNewProperty().addListener(l -> reportFilterChange());
        onlyUtProperty().addListener(l -> reportFilterChange());
        onlyLiveProperty().addListener(l -> reportFilterChange());
        onlyActHistoryProperty().addListener(l -> reportFilterChange());

        notVisProperty().addListener(l -> reportFilterChange());
        notAboProperty().addListener(l -> reportFilterChange());
        notHistoryProperty().addListener(l -> reportFilterChange());
        notDoubleProperty().addListener(l -> reportFilterChange());
        notGeoProperty().addListener(l -> reportFilterChange());
        notFutureProperty().addListener(l -> reportFilterChange());

        blacklistOnProperty().addListener(l -> reportBlacklistChange());
        blacklistOnlyProperty().addListener(l -> reportBlacklistChange());
    }

    private void reportFilterChange() {
        if (reportChange) {
//            PDebugLog.sysLog("reportFilterChange");
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
