/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2Lib.mtFilm.film.FilmData;
import de.p2tools.p2Lib.mtFilm.film.FilmDataXml;

public class CheckFilmFilter {

    public static final String FILTER_SHOW_DATE_ALL = "";
    public static final int FILTER_ALL_OR_MIN = 0;
    public static final int FILTER_DURATION_MAX_MINUTE = 150;//Filmlänge [Minuten]
    public static final int FILTER_TIME_MAX_SEC = 24 * 60 * 60;//Sendezeit [Minuten], das ist eigentlich bereits 00:00 vom nächsten Tag!!
    public static final int FILTER_TIME_RANGE_MAX_VALUE = 50;//Zeitraum zurück [Tag]

    private CheckFilmFilter() {
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

    public static boolean checkMaxDays(int maxDays, long filmTime) {
        long days = 0;
        try {
            if (maxDays == FILTER_ALL_OR_MIN) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * maxDays;
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }

        return checkDays(days, filmTime);
    }

    public static boolean checkUrl(Filter url, FilmData film) {
        if (!check(url, film.arr[FilmDataXml.FILM_WEBSITE])
                && !check(url, film.arr[FilmDataXml.FILM_URL])) {
            return false;
        }
        return true;
    }

    public static boolean checkDays(long days, long filmTime) {
        if (days == 0) {
            return true;
        }

        if (filmTime != 0 && filmTime < days) {
            return false;
        }

        return true;
    }

    public static boolean checkLengthMin(int filterLangth, long filmLength) {
        return filterLangth == 0 || filmLength == 0 || filmLength >= filterLangth;
    }

    public static boolean checkLengthMax(int filterLaenge, long filmLength) {
        return filterLaenge == FILTER_DURATION_MAX_MINUTE || filmLength == 0
                || filmLength <= filterLaenge;
    }

    public static boolean checkLength(int filterLeangth_minute_min, int filterLength_minute_max, long filmLength) {
        return checkLengthMin(filterLeangth_minute_min, filmLength)
                && checkLengthMax(filterLength_minute_max, filmLength);
    }

    public static boolean checkFilmTime(int timeMin, int timeMax, boolean invert, int filmTime) {
        if (filmTime == FilmDataMTP.FILM_TIME_EMPTY) {
            return true;
        }

        boolean ret = (timeMin == 0 || filmTime >= timeMin) &&
                (timeMax == FILTER_TIME_MAX_SEC || filmTime <= timeMax);

        if (invert) {
            return !ret;
        } else {
            return ret;
        }
    }

    private static boolean check(Filter filter, String im) {
        // wenn einer passt, dann ists gut
        if (filter.pattern != null) {
            // dann ists eine RegEx
            return (filter.pattern.matcher(im).matches());
        }
        
        if (filter.exclude) {
            //dann werden die Begriffe ausgeschlossen
            return !checkInclude(filter, im.toLowerCase());
        } else {
            //dann müssen die Begriffe enthalten sein
            return checkInclude(filter, im.toLowerCase());
        }
    }

    private static boolean checkInclude(Filter filter, String im) {
        if (filter.filterAnd) {
            //Suchbegriffe müssen alle passen
            for (final String s : filter.filterArr) {
                //dann jeden Suchbegriff checken
                if (!im.contains(s)) {
                    return false;
                }
            }
            return true;

        } else {
            //nur ein Suchbegriff muss passen
            for (final String s : filter.filterArr) {
                //dann jeden Suchbegriff checken
                if (im.contains(s)) {
                    return true;
                }
            }
        }
        return false;
    }
}