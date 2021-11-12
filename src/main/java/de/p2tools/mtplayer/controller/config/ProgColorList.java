/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.config;


import de.p2tools.p2Lib.configFile.pConfData.PColorData;
import de.p2tools.p2Lib.configFile.pConfData.PColorList;
import de.p2tools.p2Lib.configFile.pData.PData;
import javafx.scene.paint.Color;

public class ProgColorList extends PColorList {

    private ProgColorList() {
        super();
    }

    // Tabelle Filme
    public static final PColorData FILM_LIVESTREAM = addNewKey("COLOR_FILM_LIVESTREAM",
            Color.rgb(130, 0, 0), Color.rgb(100, 0, 0), "Tabelle Filme, Livestreams");
    public static final PColorData FILM_HISTORY = addNewKey("COLOR_FILM_HISTORY",
            Color.rgb(223, 223, 223), Color.rgb(100, 100, 100), "Tabelle Filme, gesehen");
    public static final PColorData FILM_NEW = addNewKey("COLOR_FILM_NEW",
            Color.rgb(0, 0, 240), Color.rgb(0, 0, 240), "Tabelle Filme, neue");
    public static final PColorData FILM_BOOKMARK = addNewKey("COLOR_FILM_BOKMARK",
            Color.rgb(255, 236, 151), Color.rgb(177, 164, 105), "Tabelle Filme, Bookmarks");
    public static final PColorData FILM_GEOBLOCK = addNewKey("COLOR_FILM_GEOBLOCK_BACKGROUND",
            Color.rgb(255, 168, 0), Color.rgb(236, 153, 0), "Tabelle Film, geogeblockt");

    // Tabelle Downloads
    public static final PColorData DOWNLOAD_WAIT = addNewKey("COLOR_DOWNLOAD_WAIT",
            Color.rgb(239, 244, 255), Color.rgb(99, 100, 105), "Tabelle Download, noch nicht gestartet");
    public static final PColorData DOWNLOAD_RUN = addNewKey("COLOR_DOWNLOAD_RUN",
            Color.rgb(255, 245, 176), Color.rgb(174, 150, 85), "Tabelle Download, läuft");
    public static final PColorData DOWNLOAD_FINISHED = addNewKey("COLOR_DOWNLOAD_FINISHED",
            Color.rgb(206, 255, 202), Color.rgb(79, 129, 74), "Tabelle Download, fertig");
    public static final PColorData DOWNLOAD_ERROR = addNewKey("COLOR_DOWNLOAD_ERROR", Color.rgb(255, 233, 233), Color.rgb(163, 82, 82), "Tabelle Download, fehlerhaft");


    // Tabelle Abos
    public static final PColorData ABO_SWITCHED_OFF = addNewKey("COLOR_ABO_SWITCHED_OFF",
            Color.rgb(225, 225, 225), Color.rgb(109, 109, 109), "Tabelle Abo, ausgeschaltet");

    // Filter wenn RegEx
    public static final PColorData FILTER_REGEX = addNewKey("COLOR_FILTER_REGEX",
            Color.rgb(225, 255, 225), Color.rgb(128, 179, 213), "Filter ist RegEx");
    public static final PColorData FILTER_REGEX_ERROR = addNewKey("COLOR_FILTER_REGEX_ERROR",
            Color.rgb(255, 230, 230), Color.rgb(170, 0, 0), "Filter ist Regex, fehlerhaft");

    // DialogDownload
    public static final PColorData DOWNLOAD_NAME_ERROR = addNewKey("COLOR_DOWNLOAD_NAME_ERROR",
            Color.rgb(255, 233, 233), Color.rgb(200, 183, 183), "Download, Dateiname ist fehlerhaft");


    public static void setColorTheme() {
        final boolean dark = ProgConfig.SYSTEM_DARK_THEME.get();
        for (int i = 0; i < getColorList().size(); ++i) {
            getColorList().get(i).setColorTheme(dark);
        }
    }

    public static PData getConfigsData() {
        return PColorList.getPData();
    }
}
