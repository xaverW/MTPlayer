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


package de.p2tools.mtplayer.controller.mediadb;

import de.p2tools.mtplayer.controller.data.download.DownloadData;

public class MediaFactory {
    private static final String TRENNER_OR = ",";
    private static final String TRENNER_AND = ":";


    private MediaFactory() {
    }

    public static String cleanSearchText(DownloadData downloadData, boolean andOr) {
        return cleanSearchText(/*downloadData.getTheme() + " " +*/ downloadData.getTitle(), andOr);
    }

    public static String cleanSearchText(String search, boolean andOr) {
        String searchString = (search).toLowerCase().trim();


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

        //29.
        searchString = searchString.replaceAll("([0-9]+)(\\.|\\/|-)", " ");

        //29
        searchString = searchString.replaceAll("([0-9])", " ");

        searchString = searchString.replace(",", " ");
        searchString = searchString.replace("·", " ");
        searchString = searchString.replace(".", " ");
        searchString = searchString.replace(";", " ");
        searchString = searchString.replace("-", " ");
        searchString = searchString.replace("-", " ");
        searchString = searchString.replace("/", " ");
        searchString = searchString.replace(":", " ");
        searchString = searchString.replace("&", " ");
        searchString = searchString.replace("!", " ");
        searchString = searchString.replace("?", " ");
        searchString = searchString.replace("=", " ");
        searchString = searchString.replace("\"", "");

        searchString = searchString.replace(" am ", " ");
        searchString = searchString.replace(" auf ", " ");
        searchString = searchString.replace(" du ", " ");
        searchString = searchString.replace(" der ", " ");
        searchString = searchString.replace(" die ", " ");
        searchString = searchString.replace(" das ", " ");
        searchString = searchString.replace(" den ", " ");
        searchString = searchString.replace(" dem ", " ");
        searchString = searchString.replace(" er ", " ");
        searchString = searchString.replace(" es ", " ");
        searchString = searchString.replace(" einen ", " ");
        searchString = searchString.replace(" eine ", " ");
        searchString = searchString.replace(" ein ", " ");
        searchString = searchString.replace(" für ", " ");
        searchString = searchString.replace(" ist ", " ");
        searchString = searchString.replace(" in ", " ");
        searchString = searchString.replace(" ich ", " ");
        searchString = searchString.replace(" mit ", " ");
        searchString = searchString.replace(" sie ", " ");
        searchString = searchString.replace(" und ", " ");
        searchString = searchString.replace(" über ", " ");
        searchString = searchString.replace(" vom ", " ");


        searchString = searchString.trim();
        if (andOr) {
            searchString = searchString.replaceAll("   ", TRENNER_AND);
            searchString = searchString.replaceAll("  ", TRENNER_AND);
            searchString = searchString.replaceAll(" ", TRENNER_AND);
        } else {
            searchString = searchString.replaceAll("   ", TRENNER_OR);
            searchString = searchString.replaceAll("  ", TRENNER_OR);
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
