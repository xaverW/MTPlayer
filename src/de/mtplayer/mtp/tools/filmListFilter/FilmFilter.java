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

package de.mtplayer.mtp.tools.filmListFilter;

import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.tools.storedFilter.Filter;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;

import java.util.regex.Pattern;

public class FilmFilter {

    public static boolean aboExistiertBereits(Abo aboExistiert, Abo aboPruefen) {
        // prüfen ob "aboExistiert" das "aboPrüfen" mit abdeckt, also die gleichen (oder mehr)
        // Filme findet, dann wäre das neue Abo hinfällig

        if (!checkAboExistArr(aboExistiert.getSender(), aboPruefen.getSender(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExistiert.getTheme(), aboPruefen.getTheme(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExistiert.getTitle(), aboPruefen.getTitle(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExistiert.getThemeTitle(), aboPruefen.getThemeTitle(), true)) {
            return false;
        }

        if (!checkAboExistArr(aboExistiert.getSomewhere(), aboPruefen.getSomewhere(), true)) {
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

    public static boolean filterAufFilmPruefen(Filter sender,
                                               Filter thema,
                                               Filter themaTitel,
                                               Filter titel,
                                               Filter somewhere,
                                               int laengeSekundenSuchen_min,
                                               int laengeSekundenSuchen_max,
                                               Film film,
                                               boolean mitLaenge) {


        if (!sender.empty && !checkSender(sender, film)) {
            return false;
        }

        if (!thema.empty && !checkTheme(thema, film)) {
            return false;
        }

        if (!themaTitel.empty && !checkThemaTitle(themaTitel, film)) {
            return false;
        }

        if (!titel.empty && !checkTitel(titel, film)) {
            return false;
        }

        if (!somewhere.empty && !checkSomewhere(somewhere, film)) {
            return false;
        }

        if (mitLaenge && !laengePruefen(laengeSekundenSuchen_min, laengeSekundenSuchen_max, film.dauerL)) {
            return false;
        }

        return true;
    }

    public static boolean checkSender(Filter sender, Film film) {
        if (sender.exakt) {
            if (!sender.filter.equalsIgnoreCase(film.arr[FilmXml.FILM_SENDER])) {
                return false;
            }
        } else {
            if (!pruefen(sender, film.arr[FilmXml.FILM_SENDER])) {
                return false;
            }
        }
        return true;
    }


    public static boolean checkTheme(Filter thema, Film film) {
        if (thema.exakt) {
            // da ist keine Form optimal?? aber so passt es zur Sortierung der Themenliste
            if (!thema.filter.equalsIgnoreCase(film.arr[FilmXml.FILM_THEMA])) {
                return false;
            }
        } else {
            if (!pruefen(thema, film.arr[FilmXml.FILM_THEMA])) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkThemaTitle(Filter themaTitel, Film film) {
        if (!pruefen(themaTitel, film.arr[FilmXml.FILM_THEMA])
                && !pruefen(themaTitel, film.arr[FilmXml.FILM_TITEL])) {
            return false;
        }
        return true;
    }

    public static boolean checkTitel(Filter titel, Film film) {
        if (!pruefen(titel, film.arr[FilmXml.FILM_TITEL])) {
            return false;
        }
        return true;
    }

    public static boolean checkSomewhere(Filter somewhere, Film film) {
        if (!pruefen(somewhere, film.arr[FilmXml.FILM_DATUM])
                && !pruefen(somewhere, film.arr[FilmXml.FILM_THEMA])
                && !pruefen(somewhere, film.arr[FilmXml.FILM_TITEL])
                && !pruefen(somewhere, film.arr[FilmXml.FILM_BESCHREIBUNG])) {
            return false;
        }
        return true;
    }

    public static boolean checkUrl(Filter url, Film film) {
        if (!pruefen(url, film.arr[FilmXml.FILM_WEBSEITE])
                && !pruefen(url, film.arr[FilmXml.FILM_URL])) {
            return false;
        }
        return true;
    }

    public static boolean checkDate(long days, Film film) {
        if (days == 0) {
            return true;
        }

        final long filmTime = film.filmDate.getTime();
        if (filmTime != 0 && filmTime < days) {
            return false;
        }

        return true;
    }


    public static boolean checkLengthMin(int filterLaengeInSekunden, long filmLaenge) {
        return filterLaengeInSekunden == 0 || filmLaenge == 0 || filmLaenge >= filterLaengeInSekunden;
    }

    public static boolean checkLengthMax(int filterLaengeInSekunden, long filmLaenge) {
        return filterLaengeInSekunden == SelectedFilter.FILTER_DURATIION_MAX_SEC || filmLaenge == 0
                || filmLaenge <= filterLaengeInSekunden;
    }

    public static boolean checkFilmtime(int timeMin, int timeMax, boolean invert, int filmTime) {
        if (filmTime == Film.FILMTIME_EMPTY) {
            return true;
        }

        boolean ret = (timeMin == 0 || filmTime >= timeMin) &&
                (timeMax == SelectedFilter.FILTER_FILMTIME_MAX_SEC || filmTime <= timeMax);

        if (invert) {
            return !ret;
        } else {
            return ret;
        }
    }

    public static boolean laengePruefen(int filterLaengeInSekunden_min, int filterLaengeInSekunden_max, long filmLaenge) {
        return checkLengthMin(filterLaengeInSekunden_min, filmLaenge)
                && checkLengthMax(filterLaengeInSekunden_max, filmLaenge);
    }

    private static boolean pruefen(Filter filter, String im) {
        // wenn einer passt, dann ists gut
        if (filter.filterArr.length == 1) {
            return pruefen(filter.filterArr[0], filter.pattern, im);
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

    private static boolean pruefen(String filter, Pattern pattern, String im) {
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
