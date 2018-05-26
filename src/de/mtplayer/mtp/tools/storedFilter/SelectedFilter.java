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

package de.mtplayer.mtp.tools.storedFilter;

import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.tools.filmListFilter.FilmFilter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.function.Predicate;

public final class SelectedFilter extends SelectedFilterProps {

    private final BooleanProperty filterChange = new SimpleBooleanProperty(false);
    private final BooleanProperty blacklistChange = new SimpleBooleanProperty(false);

    public SelectedFilter() {
        initFilter();
        setName("Filter");
    }

    public SelectedFilter(String name) {
        initFilter();
        setName(name);
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

    public void setTitleAndVis(String set) {
        setTitle(set);
        setTitleVis(true);
    }

    public static void copyFilter(SelectedFilter sfVon, SelectedFilter sfNach) {
        sfNach.setName(sfVon.getName());

        sfNach.setChannelVis(sfVon.getChannelVis());
        sfNach.setChannelExact(sfVon.getChannelExact());
        sfNach.setChannel(sfVon.getChannel());
        sfNach.setThemeVis(sfVon.isThemeVis());
        sfNach.setThemeExact(sfVon.isThemeExact());
        sfNach.setTheme(sfVon.getTheme());
        sfNach.setThemeTitleVis(sfVon.isThemeTitleVis());
        sfNach.setThemeTitle(sfVon.getThemeTitle());
        sfNach.setTitleVis(sfVon.isTitleVis());
        sfNach.setTitle(sfVon.getTitle());
        sfNach.setSomewhereVis(sfVon.isSomewhereVis());
        sfNach.setSomewhere(sfVon.getSomewhere());
        sfNach.setUrlVis(sfVon.isUrlVis());
        sfNach.setUrl(sfVon.getUrl());

        sfNach.setDaysVis(sfVon.isDaysVis());
        sfNach.setDays(sfVon.getDays());

        sfNach.setMinMaxDurVis(sfVon.getMinMaxDurVis());
        sfNach.setMinDur(sfVon.getMinDur());
        sfNach.setMaxDur(sfVon.getMaxDur());

        sfNach.setMinMaxTimeVis(sfVon.isMinMaxTimeVis());
        sfNach.setMinMaxTimeInvert(sfVon.getMinMaxTimeInvert());
        sfNach.setMinTime(sfVon.getMinTime());
        sfNach.setMaxTime(sfVon.getMaxTime());

        sfNach.setOnlyVis(sfVon.isOnlyVis());
        sfNach.setOnlyHd(sfVon.isOnlyHd());
        sfNach.setOnlyNew(sfVon.isOnlyNew());
        sfNach.setOnlyUt(sfVon.isOnlyUt());
        sfNach.setOnlyLive(sfVon.isOnlyLive());
        sfNach.setOnlyAktHistory(sfVon.isOnlyAktHistory());

        sfNach.setNotVis(sfVon.isNotVis());
        sfNach.setNotAbo(sfVon.isNotAbo());
        sfNach.setNotHistory(sfVon.isNotHistory());
        sfNach.setNotDouble(sfVon.isNotDouble());
        sfNach.setNotGeo(sfVon.isNotGeo());
        sfNach.setNotFuture(sfVon.isNotFuture());

        sfNach.setBlacklistOn(sfVon.isBlacklistOn());
    }

    public void initFilter() {
        clearFilter();

        setChannelVis(true);
        setChannelExact(true);
        setThemeVis(false);
        setThemeExact(false);
        setThemeTitleVis(true);
        setTitleVis(false);
        setSomewhereVis(false);
        setUrlVis(false);

        setDaysVis(true);
        setMinMaxDurVis(true);
        setMinMaxTimeVis(false);
        setMinMaxTimeInvert(false);

        setNotVis(false);
        setOnlyVis(false);

        nameProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        channelVisProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        channelExactProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        channelProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        themeVisProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        themeExactProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        themeProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        themeTitleVisProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        themeTitleProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        titleVisProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        titleProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        somewhereVisProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        somewhereProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        urlVisProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        urlProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        daysVisProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        daysProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        minMaxDurVisProperty().addListener((observable, oldValue, newValue) -> filterChange.setValue(!filterChange.getValue()));
        minDurProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        maxDurProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        minMaxTimeVisProperty().addListener((observable, oldValue, newValue) -> filterChange.setValue(!filterChange.getValue()));
        minMaxTimeInvertProperty().addListener((observable, oldValue, newValue) -> filterChange.setValue(!filterChange.getValue()));
        minTimeProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        maxTimeProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        onlyVisProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        onlyHdProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        onlyNewProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        onlyUtProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        onlyLiveProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        onlyAktHistoryProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        notVisProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        notAboProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        notHistoryProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        notDoubleProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        notGeoProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));
        notFutureProperty().addListener(l -> filterChange.setValue(!filterChange.getValue()));

        blacklistOnProperty().addListener(l -> blacklistChange.setValue(!blacklistChange.getValue()));

    }

    public void clearFilter() {
        // alle Filter löschen, Button Black bleibt wie er ist
        setChannel("");
        setTheme("");
        setThemeTitle("");
        setTitle("");
        setSomewhere("");
        setUrl("");

        setDays(FILTER_DAYS_MAX);

        setMinDur(0);
        setMaxDur(FILTER_DURATIION_MAX_MIN);

        setMinTime(0);
        setMaxTime(FILTER_FILMTIME_MAX_SEC);

        setOnlyHd(false);
        setOnlyNew(false);
        setOnlyUt(false);
        setOnlyLive(false);
        setOnlyAktHistory(false);

        setNotAbo(false);
        setNotHistory(false);
        setNotDouble(false);
        setNotGeo(false);
        setNotFuture(false);
    }

    public boolean txtFilterIsEmpty() {
        return getChannel().isEmpty() &&
                getTheme().isEmpty() &&
                getThemeTitle().isEmpty() &&
                getTitle().isEmpty() &&
                getSomewhere().isEmpty() &&
                getUrl().isEmpty();
    }


    public boolean clearTxtFilter() {
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
        return ret;
    }

    public Predicate<Film> getPred() {
        SelectedFilter selectedFilter = this;

        Filter fChannel;
        Filter fTheme;
        Filter fThemeTitle;
        Filter fTitle;
        Filter fSomewhere;
        Filter fUrl;

        String filterChannel = selectedFilter.getChannelVis() ? selectedFilter.getChannel() : "";
        String filterTheme = selectedFilter.isThemeVis() ? selectedFilter.getTheme() : "";
        String filterThemeTitle = selectedFilter.isThemeTitleVis() ? selectedFilter.getThemeTitle() : "";
        String filterTitle = selectedFilter.isTitleVis() ? selectedFilter.getTitle() : "";
        String filterSomwhere = selectedFilter.isSomewhereVis() ? selectedFilter.getSomewhere() : "";
        String filterUrl = selectedFilter.isUrlVis() ? selectedFilter.getUrl() : "";

        final boolean channelExact = selectedFilter.getChannelExact();
        final boolean themaExact = selectedFilter.isThemeExact();
        // Sender
        fChannel = new Filter(filterChannel, channelExact);
        fChannel.setArray();

        // Thema
        fTheme = new Filter(filterTheme, themaExact);
        fTheme.setArray();

        // ThemaTitel
        fThemeTitle = new Filter(filterThemeTitle);
        fThemeTitle.setArray();

        // Titel
        fTitle = new Filter(filterTitle);
        fTitle.setArray();

        // Irgendwo
        fSomewhere = new Filter(filterSomwhere);
        fSomewhere.setArray();

        // URL
        fUrl = new Filter(filterUrl);
        fUrl.set();// gibt URLs mit ",", das also nicht trennen

        final boolean noAbos = selectedFilter.isNotVis() ? selectedFilter.isNotAbo() : false;
        final boolean noShown = selectedFilter.isNotVis() ? selectedFilter.isNotHistory() : false;
        final boolean noDouble = selectedFilter.isNotVis() ? selectedFilter.isNotDouble() : false;
        final boolean noGeo = selectedFilter.isNotGeo() ? selectedFilter.isNotGeo() : false;
        final boolean noFuture = selectedFilter.isNotFuture() ? selectedFilter.isNotFuture() : false;

        final boolean onlyHd = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyHd() : false;
        final boolean onlyUt = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyUt() : false;
        final boolean onlyLive = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyLive() : false;
        final boolean onlyNew = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyNew() : false;
        final boolean onlyAktHist = selectedFilter.isOnlyVis() ? selectedFilter.isOnlyAktHistory() : false;

        // Länge am Slider in Min, im Film Sekunden
        final int minLaengeSec = selectedFilter.getMinMaxDurVis() ? selectedFilter.getMinDur() * 60 : 0;
        final int maxLaengeSec = selectedFilter.getMinMaxDurVis() ? selectedFilter.getMaxDur() * 60 : FILTER_DURATIION_MAX_SEC;

        // Filmzeit in Sek. von 0:00 Uhr
        final int minTimeSec = selectedFilter.isMinMaxTimeVis() ? selectedFilter.getMinTime() : 0;
        final int maxTimeSec = selectedFilter.isMinMaxTimeVis() ? selectedFilter.getMaxTime() : FILTER_FILMTIME_MAX_SEC;
        final boolean minMaxTimeInvert = selectedFilter.getMinMaxTimeInvert();

        long days = 0;
        try {
            if (selectedFilter.getDays() == FILTER_DAYS_MAX) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * selectedFilter.getDays();
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }
        if (!selectedFilter.isDaysVis()) {
            days = 0;
        }

        Predicate<Film> predicate = new Predicate<Film>() {
            @Override
            public boolean test(Film film) {
                return true;
            }
        };

        if (onlyHd) {
            predicate = predicate.and(f -> f.isHd());
        }
        if (onlyUt) {
            predicate = predicate.and(f -> f.isUt());
        }
        if (onlyLive) {
            predicate = predicate.and(f -> f.arr[FilmXml.FILM_THEME].equals(FilmTools.THEMA_LIVE));
        }
        if (onlyAktHist) {
            predicate = predicate.and(f -> f.getActHist());
        }
        if (onlyNew) {
            predicate = predicate.and(f -> f.isNewFilm());
        }

        if (noAbos) {
            predicate = predicate.and(f -> f.arr[FilmXml.FILM_ABO_NAME].isEmpty());
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

        // Filmdatum
        if (days != 0) {
            final long d = days;
            predicate = predicate.and(f -> FilmFilter.checkDate(d, f));
        }

        // Filmlänge
        if (minLaengeSec != 0) {
            predicate = predicate.and(f -> FilmFilter.checkLengthMin(minLaengeSec, f.dauerL));
        }
        if (maxLaengeSec != FILTER_DURATIION_MAX_SEC) {
            predicate = predicate.and(f -> FilmFilter.checkLengthMax(maxLaengeSec, f.dauerL));
        }

        // Film-Uhrzeit
        if (minTimeSec != 0 || maxTimeSec != FILTER_FILMTIME_MAX_SEC) {
            predicate = predicate.and(f -> FilmFilter.checkFilmtime(minTimeSec, maxTimeSec, minMaxTimeInvert, f.filmtime));
        }


        if (!fChannel.empty) {
            predicate = predicate.and(f -> FilmFilter.checkChannel(fChannel, f));
        }

        if (!fTheme.empty) {
            predicate = predicate.and(f -> FilmFilter.checkTheme(fTheme, f));
        }

        if (!fThemeTitle.empty) {
            predicate = predicate.and(f -> FilmFilter.checkThemeTitle(fThemeTitle, f));
        }

        if (!fTitle.empty) {
            predicate = predicate.and(f -> FilmFilter.checkTitle(fTitle, f));
        }

        if (!fSomewhere.empty) {
            predicate = predicate.and(f -> FilmFilter.checkSomewhere(fSomewhere, f));
        }

        if (!fUrl.empty) {
            predicate = predicate.and(f -> FilmFilter.checkUrl(fUrl, f));
        }


        return predicate;
    }

}
