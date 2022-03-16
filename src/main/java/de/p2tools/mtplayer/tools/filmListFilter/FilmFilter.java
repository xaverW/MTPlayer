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

package de.p2tools.mtplayer.tools.filmListFilter;

import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.controller.data.film.FilmDataXml;
import de.p2tools.mtplayer.tools.storedFilter.Filter;

import java.util.regex.Pattern;

public class FilmFilter {

    public static final String FILTER_SHOW_DATE_ALL = "";
    public static final int FILTER_DURATION_MIN_MINUTE = 0;
    public static final int FILTER_DURATION_MAX_MINUTE = 150;
    public static final int FILTER_FILMTIME_MIN_SEC = 0;
    public static final int FILTER_FILMTIME_MAX_SEC = 24 * 60 * 60; // das ist eigentlich bereits 00:00 vom nächsten Tag!!
    public static final int FILTER_TIME_RANGE_ALL_VALUE = 0;
    public static final int FILTER_TIME_RANGE_MIN_VALUE = 0;
    public static final int FILTER_TIME_RANGE_MAX_VALUE = 50;

    public static boolean aboExistsAlready(AboData aboExits, AboData checkAbo) {
        // prüfen ob "aboExistiert" das "aboPrüfen" mit abdeckt, also die gleichen (oder mehr)
        // Filme findet, dann wäre das neue Abo hinfällig

        if (!checkAboExistArr(aboExits.getChannel(), checkAbo.getChannel(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExits.getTheme(), checkAbo.getTheme(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExits.getTitle(), checkAbo.getTitle(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExits.getThemeTitle(), checkAbo.getThemeTitle(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExits.getSomewhere(), checkAbo.getSomewhere(), true)) {
            return false;
        }

        return true;
    }

    private static boolean checkAboExistArr(String aboExist, String aboCheck, boolean arr) {
        // da wird man immer eine Variante bauen können, die Filme eines bestehenden Abos
        // mit abdeckt -> nur eine einfache offensichtliche Prüfung

        aboCheck = aboCheck.trim();
        aboExist = aboExist.trim();

        if (aboCheck.isEmpty() && aboExist.isEmpty()) {
            return true;
        }

        if (aboCheck.toLowerCase().equals(aboExist.toLowerCase())) {
            return true;
        }

        return false;
    }

    /**
     * Abo und Blacklist prüfen
     *
     * @param sender
     * @param theme
     * @param themeTitle
     * @param title
     * @param somewhere
     * @param searchLengthMinute_min
     * @param searchLengthMinute_max
     * @param film
     * @param withLength
     * @return
     */
    public static boolean checkFilmWithFilter(Filter sender,
                                              Filter theme,
                                              Filter themeTitle,
                                              Filter title,
                                              Filter somewhere,

                                              int timeRange,
                                              int searchLengthMinute_min,
                                              int searchLengthMinute_max,

                                              FilmData film,
                                              boolean withLength) {


        // geht am schnellsten
        if (timeRange != FilmFilter.FILTER_TIME_RANGE_ALL_VALUE && !checkMaxDays(timeRange, film)) {
            return false;
        }

        if (withLength && !checkLength(searchLengthMinute_min, searchLengthMinute_max, film.getDurationMinute())) {
            return false;
        }

        // brauchen länger
        if (!sender.empty && !checkChannelSmart(sender, film)) {
            return false;
        }

        if (!theme.empty && !checkTheme(theme, film)) {
            return false;
        }

        if (!themeTitle.empty && !checkThemeTitle(themeTitle, film)) {
            return false;
        }

        if (!title.empty && !checkTitle(title, film)) {
            return false;
        }

        if (!somewhere.empty && !checkSomewhere(somewhere, film)) {
            return false;
        }

        return true;
    }

    public static boolean checkChannelSmart(Filter sender, FilmData film) {
        // nur ein Suchbegriff muss passen
        for (final String s : sender.filterArr) {
            // dann jeden Suchbegriff checken
            if (s.equalsIgnoreCase(film.arr[FilmDataXml.FILM_CHANNEL])) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkChannel(Filter sender, FilmData film) {
        if (sender.exact) {
            if (!sender.filter.equalsIgnoreCase(film.arr[FilmDataXml.FILM_CHANNEL])) {
                return false;
            }
        } else {
            if (!check(sender, film.arr[FilmDataXml.FILM_CHANNEL])) {
                return false;
            }
        }
        return true;
    }


    public static boolean checkTheme(Filter theme, FilmData film) {
        if (theme.exact) {
            // da ist keine Form optimal?? aber so passt es zur Sortierung der Themenliste
            if (!theme.filter.equalsIgnoreCase(film.arr[FilmDataXml.FILM_THEME])) {
                return false;
            }
        } else {
            if (!check(theme, film.arr[FilmDataXml.FILM_THEME])) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkThemeTitle(Filter themeTitle, FilmData film) {
        if (!check(themeTitle, film.arr[FilmDataXml.FILM_THEME])
                && !check(themeTitle, film.arr[FilmDataXml.FILM_TITLE])) {
            return false;
        }
        return true;
    }

    public static boolean checkTitle(Filter title, FilmData film) {
        if (!check(title, film.arr[FilmDataXml.FILM_TITLE])) {
            return false;
        }
        return true;
    }

    public static boolean checkSomewhere(Filter somewhere, FilmData film) {
        if (!check(somewhere, film.arr[FilmDataXml.FILM_DATE])
                && !check(somewhere, film.arr[FilmDataXml.FILM_THEME])
                && !check(somewhere, film.arr[FilmDataXml.FILM_TITLE])
                && !check(somewhere, film.arr[FilmDataXml.FILM_DESCRIPTION])) {
            return false;
        }
        return true;
    }

    private static boolean checkMaxDays(int maxDays, FilmData film) {
        long days = 0;
        try {
            if (maxDays == FilmFilter.FILTER_TIME_RANGE_ALL_VALUE) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * maxDays;
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }

        return checkDays(days, film);
    }

    public static boolean checkUrl(Filter url, FilmData film) {
        if (!check(url, film.arr[FilmDataXml.FILM_WEBSITE])
                && !check(url, film.arr[FilmDataXml.FILM_URL])) {
            return false;
        }
        return true;
    }

    public static boolean checkDays(long days, FilmData film) {
        if (days == 0) {
            return true;
        }

        final long filmTime = film.filmDate.getTime();
        if (filmTime != 0 && filmTime < days) {
            return false;
        }

        return true;
    }

    public static boolean checkLengthMin(int filterLaenge, long filmLength) {
        return filterLaenge == 0 || filmLength == 0 || filmLength >= filterLaenge;
    }

    public static boolean checkLengthMax(int filterLaenge, long filmLength) {
        return filterLaenge == FILTER_DURATION_MAX_MINUTE || filmLength == 0
                || filmLength <= filterLaenge;
    }

    public static boolean checkFilmTime(int timeMin, int timeMax, boolean invert, int filmTime) {
        if (filmTime == FilmData.FILM_TIME_EMPTY) {
            return true;
        }

        boolean ret = (timeMin == 0 || filmTime >= timeMin) &&
                (timeMax == FILTER_FILMTIME_MAX_SEC || filmTime <= timeMax);

        if (invert) {
            return !ret;
        } else {
            return ret;
        }
    }

    public static boolean checkLength(int filterLeangth_minute_min, int filterLength_minute_max, long filmLength) {
        return checkLengthMin(filterLeangth_minute_min, filmLength)
                && checkLengthMax(filterLength_minute_max, filmLength);
    }

    private static boolean check(Filter filter, String im) {
        // wenn einer passt, dann ists gut
        if (filter.filterArr.length == 1) {
            return check(filter.filterArr[0], filter.pattern, im);
        }

        if (filter.filterAnd) {
            // Suchbegriffe müssen alle passen
            for (final String s : filter.filterArr) {
                // dann jeden Suchbegriff checken
                if (!im.toLowerCase().contains(s)) {
                    return false;
                }
            }
            return true;

        } else {
            // nur ein Suchbegriff muss passen
            for (final String s : filter.filterArr) {
                // dann jeden Suchbegriff checken
                if (im.toLowerCase().contains(s)) {
                    return true;
                }
            }
        }

        // nix wars
        return false;
    }

    private static boolean check(String filter, Pattern pattern, String im) {
        if (pattern != null) {
            // dann ists eine RegEx
            return (pattern.matcher(im).matches());
        }
        if (im.toLowerCase().contains(filter)) {
            // wenn einer passt, dann ists gut
            return true;
        }

        // nix wars
        return false;
    }
}
