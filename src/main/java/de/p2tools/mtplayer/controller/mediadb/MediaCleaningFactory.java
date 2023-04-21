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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.mediacleaningdata.MediaCleaningData;
import de.p2tools.mtplayer.controller.data.mediacleaningdata.MediaCleaningList;

public class MediaCleaningFactory {
    private static final String TRENNER_OR = ",";
    private static final String TRENNER_AND = ":";

    public static String[] CLEAN_LIST = {",", "·", ".", ";", "-", "–", "˗",
            "/", ":", "&", "!", "?", "°", "=", "\"", "|",
            " am ", " auf ",
            " du ", " der ", " die ", " das ", " den ", " dem ", " er ", " es ",
            " einen ", " eine ", " ein ", " für ", " ist ", " in ", " ich ", " mit ",
            " sie ", " und ", " über ", " vom "};

    private MediaCleaningFactory() {
    }

    public static void initMediaCleaningList(MediaCleaningList list) {
        for (String s : CLEAN_LIST) {
            list.add(new MediaCleaningData(s));
        }
    }

    public static String cleanSearchText(String searchTheme, String searchTitel, boolean media) {
        String searchString;
        switch (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_BUILD_SEARCH_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_BUILD_SEARCH_ABO.getValue()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEME:
                searchString = searchTheme.toLowerCase();
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_TITEL:
                searchString = searchTitel.toLowerCase();
                break;
            default:
                searchString = searchTheme.toLowerCase() + " " + searchTitel.toLowerCase();
        }

        if (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_EXACT_MEDIA.getValue() :
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_EXACT_ABO.getValue()) {
            //dann wird nicht gereinigt, aber EXACT
            return "\"" + searchString + "\"";
        }

        if (media ? !ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_MEDIA.getValue() :
                !ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_ABO.getValue()) {
            //dann wird nicht gereinigt
            return searchString;
        }

        //dann reinigen
        if (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_CLIP_MEDIA.getValue() :
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_CLIP_ABO.getValue()) {
            searchString = cleanSearchString(searchString, "(", ")");
            searchString = cleanSearchString(searchString, "[", "]");
            searchString = cleanSearchString(searchString, "{", "}");
        }

        if (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_DATE_MEDIA.getValue() :
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_DATE_ABO.getValue()) {
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
        }

        if (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_NUMBER_MEDIA.getValue() :
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_NUMBER_ABO.getValue()) {
            // 29.
            searchString = searchString.replaceAll("([0-9]+)(\\.|\\/|-)", " ");

            // 29
            searchString = searchString.replaceAll("([0-9])", " ");
        }

        if (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_LIST_MEDIA.getValue() :
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_LIST_ABO.getValue()) {
            // dann wird die PUTZ-LISTE angewandt
            final String[] searchArr = ProgData.getInstance().mediaCleaningList.getSearchArr();
            for (String s : searchArr) {
                searchString = searchString.replace(s, " ");
            }
        }

        // erst mal putzen
        searchString = searchString.trim();
        while (searchString.contains("  ")) {
            searchString = searchString.replaceAll("  ", " ");
        }

        // und jetzt ersetzten
        if (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_AND_OR_MEDIA.getValue() :
                ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_AND_OR_ABO.getValue()) {
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
