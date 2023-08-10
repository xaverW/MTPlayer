/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.data.cleaningdata;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.history.HistoryData;

import java.util.Hashtable;

public class CleaningProposeFactory {

    private CleaningProposeFactory() {
    }

    public static void cleanSearchText(HistoryData searchTitel, Hashtable<String, Integer> hashtable) {
        String searchString = searchTitel.getTheme().toLowerCase() + " " + searchTitel.getTitle().toLowerCase();

        //dann reinigen
        searchString = cleanSearchString(searchString, "(", ")");
        searchString = cleanSearchString(searchString, "[", "]");
        searchString = cleanSearchString(searchString, "{", "}");

        //29.03.2023
        searchString = searchString.replaceAll("(0[1-9]|[12][0-9]|3[01])(\\.|\\/|-)(0[1-9]|1[0-2])(\\.|\\/|-)((?:19|20)\\d{2})",
                " ");
        //30. März 2023
        searchString = searchString.replaceAll("(0[1-9]|[12][0-9]|3[01])(\\.|\\/|-|)(\\s?)" +
                "(januar|februar|märz|april|mai|juni|juli|augist|september|oktober|november|dezember)" +
                "(\\s?)((?:19|20)\\d{2})", " ");
        //30. März
        searchString = searchString.replaceAll("(0[1-9]|[12][0-9]|3[01])(\\.|\\/|-|)(\\s?)" +
                "(januar|februar|märz|april|mai|juni|juli|augist|september|oktober|november|dezember)", " ");

        // 29.
        searchString = searchString.replaceAll("([0-9]+)(\\.|\\/|-)", " ");
        // 29
        searchString = searchString.replaceAll("([0-9])", " ");

        while (searchString.contains("  ")) {
            searchString = searchString.replaceAll("  ", " ");
        }

        String[] arr = searchString.split(" ");
        for (String search : arr) {
            search = search.trim();
            for (CleaningData cleaningData : ProgData.getInstance().cleaningDataListPropose) {
                if (cleaningData.getAlways()) {
                    search = search.replace(cleaningData.getCleaningString(), "");

                } else {
                    if (search.equals(cleaningData.getCleaningString())) {
                        // dann ists ein Treffer
                        search = "";
                    }
                }
            }
            if (search.length() > 2) {
                Integer i = hashtable.get(search);
                if (i == null) {
                    hashtable.put(search, 1);
                } else {
                    hashtable.put(search, ++i);
                }
            }
        }
    }

    private static String cleanSearchString(String str, String key1, String key2) {
        String ret = str;
        while (ret.contains(key1) && ret.contains(key2) && ret.indexOf(key1) < ret.indexOf(key2)) {
            ret = ret.substring(0, ret.indexOf(key1)) + ret.substring(ret.indexOf(key2) + 1);
        }
        return ret;
    }
}
