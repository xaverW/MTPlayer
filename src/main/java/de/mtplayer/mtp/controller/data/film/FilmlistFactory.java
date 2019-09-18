/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.controller.data.film;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.history.HistoryList;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;

import java.util.HashMap;
import java.util.Map;

public class FilmlistFactory {
    private static Map<Character, Integer> counterMap = new HashMap<>(25);

    private FilmlistFactory() {
    }

    public static void markBookmarks() {
        Filmlist filmlist = ProgData.getInstance().filmlist;
        HistoryList bookmarks = ProgData.getInstance().bookmarks;

        filmlist.stream().forEach(film -> {
            if (bookmarks.checkIfUrlAlreadyIn(film.getUrlHistory())) {
                film.setBookmark(true);
            }
        });
    }

    public static void clearAllBookmarks() {
        Filmlist filmlist = ProgData.getInstance().filmlist;
        filmlist.stream().forEach(film -> film.setBookmark(false));
    }

    public static void cleanFaultyCharacterFilmlist() {
        // damit werden Unicode-Zeichen korrigiert
        // gibt da einen Java-Bug
        // https://github.com/javafxports/openjdk-jfx/issues/287

        PDuration.counterStart("cleanFaultyCharacter");

        Filmlist filmlist = ProgData.getInstance().filmlist;
        filmlist.stream().forEach(film -> {
            film.arr[Film.FILM_TITLE] = cleanUnicode_1(film.getTitle());
            film.arr[Film.FILM_THEME] = cleanUnicode_1(film.getTheme());
            film.setDescription(cleanUnicode_1(film.getDescription()));

            film.arr[Film.FILM_TITLE] = cleanUnicode_2(film.getTitle());
            film.arr[Film.FILM_THEME] = cleanUnicode_2(film.getTheme());
            film.setDescription(cleanUnicode_2(film.getDescription()));

            // U+3000 (12288)	　	Trenn- (Leer-) Zeichen	Whitespace	IDEOGRAPHIC SPACE	Ideographisches Leerzeichen
            // das hat die Probleme gemacht, Film: Weltbilder
        });

        for (Map.Entry<Character, Integer> entry : counterMap.entrySet()) {
            Character key = entry.getKey();
            Integer value = entry.getValue();
            PLog.sysLog("Key: " + (int) key + "  Key: " + key + "  Anz: " + value);

        }

        PDuration.counterStop("cleanFaultyCharacter");
    }


    final static String regEx = "[\\p{Cc}&&[^\n,\r,\t]]";

    public static String cleanUnicode_1(String ret) {
        // damit werden Unicode-Zeichen korrigiert
        // gibt da eine Java-Bug
        // https://github.com/javafxports/openjdk-jfx/issues/287

        ret = ret.replaceAll(regEx, "");
        return ret;
    }

    public static String cleanUnicode_2(String test) {
        // damit werden Unicode-Zeichen korrigiert
        // gibt da eine Java-Bug, auch Probleme bei Linux mit fehlenden Zeichen in den code tablen
        // https://github.com/javafxports/openjdk-jfx/issues/287

        char[] c = test.toCharArray();

        for (int i = 0; i < c.length; ++i) {

            if ((int) c[i] > 11263) { // der Wert ist jetzt einfach mal geschätzt und kommt ~ 20x vor
                counterMap.merge(c[i], 1, Integer::sum);
                c[i] = ' ';
                test = String.valueOf(c);
            }

        }

        return test;
    }
}
