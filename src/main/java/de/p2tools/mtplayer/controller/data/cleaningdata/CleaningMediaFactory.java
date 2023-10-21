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

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;

public class CleaningMediaFactory {
    private static final String TRENNER_OR = ",";
    private static final String TRENNER_AND = ":";


    private CleaningMediaFactory() {
    }

    public static String cleanSearchText(MediaDataDto mediaDataDto) {
        String searchString;

        // erst mal den Suchbegriff bauen
        switch (mediaDataDto.buildSearchFrom.getValue()) {
            case ProgConst.MEDIA_SEARCH_THEME_OR_PATH:
                searchString = mediaDataDto.searchTheme.toLowerCase();
                break;
            case ProgConst.MEDIA_SEARCH_TITEL_OR_NAME:
                searchString = mediaDataDto.searchTitle.toLowerCase();
                break;
            default:
                searchString = mediaDataDto.searchTheme.toLowerCase() + " " + mediaDataDto.searchTitle.toLowerCase();
        }

        // dann den Suchbegriff putzen
        if (mediaDataDto.cleaningExact.getValue()) {
            //dann wird nicht gereinigt, aber EXACT
            return "\"" + searchString + "\"";
        }

        if (!mediaDataDto.cleaning.getValue()) {
            //dann wird nicht gereinigt
            return searchString;
        }

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

        // dann wird die PUTZ-LISTE angewandt
        while (searchString.contains("  ")) {
            searchString = searchString.replaceAll("  ", " ");
        }

        if (mediaDataDto.cleaningList.getValue()) {
            // wenn die Cleaning-Liste angewendet werden soll
            String[] arr = searchString.split(" ");
            searchString = "";
            for (String search : arr) {

                for (CleaningData cleaningData : ProgData.getInstance().cleaningDataListMedia) {
                    if (cleaningData.getAlways()) {
                        search = search.replace(cleaningData.getCleaningString(), "");

                    } else {
                        if (search.equals(cleaningData.getCleaningString())) {
                            // dann ists ein Treffer
                            search = "";
                        }
                    }
                }
                if (search.length() > 2 && !searchString.contains(" " + search)) {
                    searchString = searchString + " " + search;
                }
            }
        }

        searchString = searchString.trim();
        if (mediaDataDto.cleaningAndOr.getValue()) {
            searchString = searchString.replaceAll(" ", TRENNER_AND);
        } else {
            searchString = searchString.replaceAll(" ", TRENNER_OR);
        }

        return searchString;
    }

    private static String cleanSearchString(String str, String key1, String key2) {
        String ret = str;
        while (ret.contains(key1) && ret.contains(key2) && ret.indexOf(key1) < ret.indexOf(key2)) {
            ret = ret.substring(0, ret.indexOf(key1)) + ret.substring(ret.indexOf(key2) + 1);
        }
        return ret;
    }
}
