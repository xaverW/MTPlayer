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


import de.p2tools.p2lib.data.PColorData;
import de.p2tools.p2lib.data.PColorList;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.List;

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
            Color.rgb(255, 168, 0), Color.rgb(236, 153, 0), "Tabelle Filme, geogeblockt");
    public static final PColorData FILTER_PROFILE_SEPARATOR = addNewKey("COLOR_FILTER_PROFILE_SEPARATOR",
            Color.rgb(180, 180, 255), "Filterprofile Filme, Trenner");

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

    // Tabelle Blacklist
    public static final PColorData BLACK_DATA_SWITCHED_OFF = addNewKey("BLACK_DATA_SWITCHED_OFF",
            Color.rgb(225, 225, 225), Color.rgb(109, 109, 109), "Tabelle Blacklist, ausgeschaltet");

    // DialogDownload
    public static final PColorData DOWNLOAD_NAME_ERROR = addNewKey("COLOR_DOWNLOAD_NAME_ERROR",
            Color.rgb(255, 233, 233), Color.rgb(200, 183, 183), "Download, Dateiname ist fehlerhaft");

    //=======================================================================================
    //Liste der Schrift-Farben -> Rest sind Hintergrundfarben
    public static final List<PColorData> FRONT_COLOR = List.of(FILM_LIVESTREAM, FILM_NEW, FILM_GEOBLOCK);

    public synchronized static PColorList getInstance() {
        return PColorList.getInst();
    }

    public static void setColorTheme() {
        final boolean dark = ProgConfig.SYSTEM_DARK_THEME.get();
        for (int i = 0; i < getInstance().size(); ++i) {
            getInstance().get(i).setColorTheme(dark);
        }
    }

    public static ObservableList<PColorData> getColorListFront() {
        ObservableList<PColorData> list = FXCollections.observableArrayList();
        ObservableList<PColorData> pColorData = getInstance();
        pColorData.stream().filter(pc -> FRONT_COLOR.contains(pc)).forEach(pc -> list.add(pc));

        Comparator<PColorData> comparator = Comparator.comparing(PColorData::getText);
        FXCollections.sort(list, comparator);

        return list;
    }

    public static ObservableList<PColorData> getColorListBackground() {
        ObservableList<PColorData> list = FXCollections.observableArrayList();
        ObservableList<PColorData> pColorData = getInstance();
        pColorData.stream().filter(pc -> !FRONT_COLOR.contains(pc)).forEach(pc -> list.add(pc));

        Comparator<PColorData> comparator = Comparator.comparing(PColorData::getText);
        FXCollections.sort(list, comparator);

        return list;
    }

    public static void setColorData(String key, String value) {
        try {
            ObservableList<PColorData> list = getInstance();
            list.stream().forEach(pColorData -> {
                if (pColorData.getKey().equals(key)) {
                    Color c = Color.web(value);
                    if (value.endsWith("_DARK")) {
                        pColorData.setColorDark(c);
                    } else {
                        pColorData.setColorLight(c);
                    }
                }
            });
        } catch (Exception ex) {
            P2Log.errorLog(956410210, "setColorData");
        }
    }
}
