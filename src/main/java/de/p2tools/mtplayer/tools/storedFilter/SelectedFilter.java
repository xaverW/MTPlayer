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
import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.controller.data.film.FilmDataXml;
import de.p2tools.mtplayer.tools.filmListFilter.FilmFilter;
import de.p2tools.p2Lib.tools.log.PDebugLog;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;

import java.util.function.Predicate;

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

    public void initFilter() {
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        filterWaitTime.addListener((observable, oldValue, newValue) -> {
            PDebugLog.sysLog("SYSTEM_FILTER_WAIT_TIME: " + ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
            pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        });

        clearFilter();
        PDebugLog.sysLog("SYSTEM_FILTER_WAIT_TIME: " + ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
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
//          reportFilterChange();
        });


        titleVisProperty().addListener(l -> reportFilterChange());
        titleProperty().addListener(l -> {
            PDebugLog.sysLog("Pause titleProperty");
            pause.setOnFinished(event -> reportFilterChange());
            pause.playFromStart();
//            reportFilterChange();
        });

        somewhereVisProperty().addListener(l -> reportFilterChange());
        somewhereProperty().addListener(l -> {
            PDebugLog.sysLog("Pause somewhereProperty: " + pause.getDuration().toString());
            pause.setOnFinished(event -> reportFilterChange());
            pause.playFromStart();
//            reportFilterChange();
        });

        urlVisProperty().addListener(l -> reportFilterChange());
        urlProperty().addListener(l -> {
            PDebugLog.sysLog("Pause urlProperty");
            pause.setOnFinished(event -> reportFilterChange());
            pause.playFromStart();
//            reportFilterChange();
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
            PDebugLog.sysLog("reportFilterChange");
            filterChange.setValue(!filterChange.getValue());
        }
    }

    private void reportBlacklistChange() {
        if (reportChange) {
            PDebugLog.sysLog("reportBlacklistChange");
            blacklistChange.setValue(!blacklistChange.getValue());
        }
    }

    public void clearFilter() {
        // alle Filter löschen, Button Black bleibt wie er ist
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

    public Predicate<FilmData> getPredicate() {
        SelectedFilter selectedFilter = this;

        Filter fChannel;
        Filter fTheme;
        Filter fThemeTitle;
        Filter fTitle;
        Filter fSomewhere;
        Filter fUrl;
        Filter fShowDate;

        String filterChannel = selectedFilter.isChannelVis() ? selectedFilter.getChannel() : "";
        String filterTheme = selectedFilter.isThemeVis() ? selectedFilter.getTheme() : "";
        String filterThemeTitle = selectedFilter.isThemeTitleVis() ? selectedFilter.getThemeTitle() : "";
        String filterTitle = selectedFilter.isTitleVis() ? selectedFilter.getTitle() : "";
        String filterSomewhere = selectedFilter.isSomewhereVis() ? selectedFilter.getSomewhere() : "";
        String filterUrl = selectedFilter.isUrlVis() ? selectedFilter.getUrl() : "";
        String filterShowDate = selectedFilter.isShowDateVis() ? selectedFilter.getShowDate() : "";

        final boolean themeExact = selectedFilter.isThemeExact();
        // Sender
        fChannel = new Filter(filterChannel, true);
        // Thema
        fTheme = new Filter(filterTheme, themeExact, true);
        // ThemaTitel
        fThemeTitle = new Filter(filterThemeTitle, true);
        // Titel
        fTitle = new Filter(filterTitle, true);
        // Irgendwo
        fSomewhere = new Filter(filterSomewhere, true);
        // URL
        fUrl = new Filter(filterUrl, false); // gibt URLs mit ",", das also nicht trennen
        //ShowDate
        fShowDate = new Filter(filterShowDate, false);

        //Sendedatum

        final boolean onlyBookmark = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyBookmark() : false;
        final boolean onlyHd = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyHd() : false;
        final boolean onlyUt = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyUt() : false;
        final boolean onlyLive = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyLive() : false;
        final boolean onlyNew = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyNew() : false;
        final boolean onlyAktHist = selectedFilter.isOnlyVis() ? selectedFilter.getOnlyActHistory() : false;

        final boolean noAbos = selectedFilter.isNotVis() ? selectedFilter.isNotAbo() : false;
        final boolean noShown = selectedFilter.isNotVis() ? selectedFilter.isNotHistory() : false;
        final boolean noDouble = selectedFilter.isNotVis() ? selectedFilter.isNotDouble() : false;
        final boolean noGeo = selectedFilter.isNotVis() ? selectedFilter.isNotGeo() : false;
        final boolean noFuture = selectedFilter.isNotVis() ? selectedFilter.isNotFuture() : false;

        final boolean onlyBlack = selectedFilter.isBlacklistOnly();

        // Länge am Slider in Min
        final int minLengthMinute = selectedFilter.isMinMaxDurVis() ? selectedFilter.getMinDur() : 0;
        final int maxLengthMinute = selectedFilter.isMinMaxDurVis() ? selectedFilter.getMaxDur() : FilmFilter.FILTER_DURATION_MAX_MINUTE;

        // FilmUhrZeit in Sek. von 0:00 Uhr
        final int minTimeSec = selectedFilter.isMinMaxTimeVis() ? selectedFilter.getMinTime() : 0;
        final int maxTimeSec = selectedFilter.isMinMaxTimeVis() ? selectedFilter.getMaxTime() : FilmFilter.FILTER_FILMTIME_MAX_SEC;
        final boolean minMaxTimeInvert = selectedFilter.isMinMaxTimeInvert();

        long days;
        try {
            if (selectedFilter.getTimeRange() == FilmFilter.FILTER_TIME_RANGE_ALL_VALUE) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * selectedFilter.getTimeRange();
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }
        if (!selectedFilter.isTimeRangeVis()) {
            days = 0;
        }

        Predicate<FilmData> predicate = film -> true;

        if (onlyBookmark) {
            predicate = predicate.and(f -> f.isBookmark());
        }
        if (onlyHd) {
            predicate = predicate.and(f -> f.isHd());
        }
        if (onlyUt) {
            predicate = predicate.and(f -> f.isUt());
        }
        if (onlyLive) {
            predicate = predicate.and(f -> f.isLive());
        }
        if (onlyAktHist) {
            predicate = predicate.and(f -> f.getActHist());
        }
        if (onlyNew) {
            predicate = predicate.and(f -> f.isNewFilm());
        }

        if (noAbos) {
            predicate = predicate.and(f -> f.arr[FilmDataXml.FILM_ABO_NAME].isEmpty());
        }
        if (noShown) {
            predicate = predicate.and(f -> !f.isShown());
        }
        if (noDouble) {
            predicate = predicate.and(f -> !f.isDoubleUrl());
        }
        if (noGeo) {
            predicate = predicate.and(f -> !f.isGeoBlocked());
        }

        if (noFuture) {
            predicate = predicate.and(f -> !f.isInFuture());
        }

        if (onlyBlack) {
            predicate = predicate.and(f -> !f.isBlackBlocked());
        }

        //anz Tage Sendezeit
        if (days != 0) {
            final long d = days;
            predicate = predicate.and(f -> FilmFilter.checkDays(d, f));
        }

        // Filmlänge
        if (minLengthMinute != 0) {
            predicate = predicate.and(f -> FilmFilter.checkLengthMin(minLengthMinute, f.getDurationMinute()));
        }
        if (maxLengthMinute != FilmFilter.FILTER_DURATION_MAX_MINUTE) {
            predicate = predicate.and(f -> FilmFilter.checkLengthMax(maxLengthMinute, f.getDurationMinute()));
        }

        // Film-Uhrzeit
        if (minTimeSec != 0 || maxTimeSec != FilmFilter.FILTER_FILMTIME_MAX_SEC) {
            predicate = predicate.and(f -> FilmFilter.checkFilmTime(minTimeSec, maxTimeSec, minMaxTimeInvert, f.filmTime));
        }


        if (!fChannel.empty) {
            predicate = predicate.and(f -> FilmFilter.checkChannelSmart(fChannel, f));
        }

        if (!fTheme.empty) {
//            if (ProgData.filterDiacritic) {
//                predicate = predicate.and(f -> FilmFilter.checkThemeDiacritic(fTheme, f));
//            } else {
            predicate = predicate.and(f -> FilmFilter.checkTheme(fTheme, f));
//            }
        }

        if (!fThemeTitle.empty) {
//            if (ProgData.filterDiacritic) {
//                predicate = predicate.and(f -> FilmFilter.checkThemeTitleDiacritic(fThemeTitle, f));
//            } else {
            predicate = predicate.and(f -> FilmFilter.checkThemeTitle(fThemeTitle, f));
//            }
        }

        if (!fTitle.empty) {
//            if (ProgData.filterDiacritic) {
//                predicate = predicate.and(f -> FilmFilter.checkTitleDiacritic(fTitle, f));
//            } else {
            predicate = predicate.and(f -> FilmFilter.checkTitle(fTitle, f));
//            }
        }

        if (!fSomewhere.empty) {
//            if (ProgData.filterDiacritic) {
//                predicate = predicate.and(f -> FilmFilter.checkSomewhereDiacritic(fSomewhere, f));
//            } else {
            predicate = predicate.and(f -> FilmFilter.checkSomewhere(fSomewhere, f));
//            }
        }

        if (!fUrl.empty) {
            predicate = predicate.and(f -> FilmFilter.checkUrl(fUrl, f));
        }

        //Sendetag
        if (!fShowDate.filter.equals(FilmFilter.FILTER_SHOW_DATE_ALL)) {
            predicate = predicate.and(f -> FilmFilter.checkShowDate(getShowDate(), f));
        }

        return predicate;
    }
}
