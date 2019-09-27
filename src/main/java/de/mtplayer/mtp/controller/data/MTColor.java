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


package de.mtplayer.mtp.controller.data;

import de.mtplayer.mLib.tools.MLC;
import de.mtplayer.mtp.controller.config.ProgConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class MTColor {

    // Tabelle Filme
    public static final MLC FILM_LIVESTREAM = new MLC(ProgConfig.COLOR__FILM_LIVESTREAM, Color.rgb(130, 0, 0), "Tabelle Filme, Livestreams");
    public static final MLC FILM_HISTORY = new MLC(ProgConfig.COLOR__FILM_HISTORY, Color.rgb(223, 223, 223), "Tabelle Filme, gesehen");
    public static final MLC FILM_NEW = new MLC(ProgConfig.COLOR__FILM_NEW, Color.rgb(0, 0, 240), "Tabelle Filme, neue");
    public static final MLC FILM_BOOKMARK = new MLC(ProgConfig.COLOR__FILM_BOOKMARK, Color.rgb(255, 236, 151), "Tabelle Filme, Bookmarks");
    public static final MLC FILM_GEOBLOCK = new MLC(ProgConfig.COLOR__FILM_GEOBLOCK_BACKGROUND, Color.rgb(255, 168, 0), "Tabelle Film, geogeblockt");

    // Tabelle Downloads
    public static final MLC DOWNLOAD_WAIT = new MLC(ProgConfig.COLOR__DOWNLOAD_WAIT, Color.rgb(239, 244, 255), "Tabelle Download, noch nicht gestartet");
    public static final MLC DOWNLOAD_RUN = new MLC(ProgConfig.COLOR__DOWNLOAD_RUN, Color.rgb(255, 245, 176), "Tabelle Download, läuft");
    public static final MLC DOWNLOAD_FINISHED = new MLC(ProgConfig.COLOR__DOWNLOAD_FINISHED, Color.rgb(206, 255, 202), "Tabelle Download, fertig");
    public static final MLC DOWNLOAD_ERROR = new MLC(ProgConfig.COLOR__DOWNLOAD_ERROR, Color.rgb(255, 233, 233), "Tabelle Download, fehlerhaft");

    // Tabelle Abos
    public static final MLC ABO_SWITCHED_OFF = new MLC(ProgConfig.COLOR__ABO_SWITCHED_OFF, Color.rgb(225, 225, 225), "Tabelle Abo, ausgeschaltet");

    // Filter wenn RegEx
    public static final MLC FILTER_REGEX = new MLC(ProgConfig.COLOR__FILTER_REGEX, Color.rgb(153, 214, 255), "Filter ist RegEx");
    public static final MLC FILTER_REGEX_ERROR = new MLC(ProgConfig.COLOR__FILTER_REGEX_ERROR, Color.RED, "Filter ist Regex, fehlerhaft");

    // DialogDownload
    public static final MLC DOWNLOAD_NAME_ERROR = new MLC(ProgConfig.COLOR__DOWNLOAD_NAME_ERROR, Color.rgb(255, 233, 233), "Download, Dateiname ist fehlerhaft");

    private static ObservableList<MLC> colorList = FXCollections.observableArrayList();

    public MTColor() {
        colorList.add(FILM_LIVESTREAM);
        colorList.add(FILM_HISTORY);
        colorList.add(FILM_NEW);
        colorList.add(FILM_BOOKMARK);
        colorList.add(FILM_GEOBLOCK);
        colorList.add(DOWNLOAD_WAIT);
        colorList.add(DOWNLOAD_RUN);
        colorList.add(DOWNLOAD_FINISHED);
        colorList.add(DOWNLOAD_ERROR);
        colorList.add(ABO_SWITCHED_OFF);
        colorList.add(FILTER_REGEX);
        colorList.add(FILTER_REGEX_ERROR);
        colorList.add(DOWNLOAD_NAME_ERROR);
    }

    public static synchronized ObservableList<MLC> getColorList() {
        return colorList;
    }

    public final void loadStoredColors() {
        colorList.stream().forEach(MLC::initFromStoredColor);
    }

    public void resetAllColors() {
        colorList.forEach(MLC::resetColor);
    }
}
