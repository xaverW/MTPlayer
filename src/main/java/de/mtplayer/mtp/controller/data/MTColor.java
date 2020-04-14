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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class MTColor {

    private BooleanProperty changed = new SimpleBooleanProperty();

    // Tabelle Filme
    public static final MLC FILM_LIVESTREAM =
            new MLC(ProgConfig.COLOR__FILM_LIVESTREAM, Color.rgb(130, 0, 0),
                    ProgConfig.COLOR__FILM_LIVESTREAM_DARK, Color.rgb(100, 0, 0), "Tabelle Filme, Livestreams");
    public static final MLC FILM_HISTORY =
            new MLC(ProgConfig.COLOR__FILM_HISTORY, Color.rgb(223, 223, 223),
                    ProgConfig.COLOR__FILM_HISTORY_DARK, Color.rgb(100, 100, 100), "Tabelle Filme, gesehen");
    public static final MLC FILM_NEW =
            new MLC(ProgConfig.COLOR__FILM_NEW, Color.rgb(0, 0, 240),
                    ProgConfig.COLOR__FILM_NEW_DARK, Color.rgb(0, 0, 240), "Tabelle Filme, neue");
    public static final MLC FILM_BOOKMARK =
            new MLC(ProgConfig.COLOR__FILM_BOOKMARK, Color.rgb(255, 236, 151),
                    ProgConfig.COLOR__FILM_BOOKMARK_DARK, Color.rgb(177, 164, 105), "Tabelle Filme, Bookmarks");
    public static final MLC FILM_GEOBLOCK =
            new MLC(ProgConfig.COLOR__FILM_GEOBLOCK_BACKGROUND, Color.rgb(255, 168, 0),
                    ProgConfig.COLOR__FILM_GEOBLOCK_BACKGROUND_DARK, Color.rgb(236, 153, 0), "Tabelle Film, geogeblockt");

    // Tabelle Downloads
    public static final MLC DOWNLOAD_WAIT =
            new MLC(ProgConfig.COLOR__DOWNLOAD_WAIT, Color.rgb(239, 244, 255),
                    ProgConfig.COLOR__DOWNLOAD_WAIT_DARK, Color.rgb(99, 100, 105), "Tabelle Download, noch nicht gestartet");
    public static final MLC DOWNLOAD_RUN =
            new MLC(ProgConfig.COLOR__DOWNLOAD_RUN, Color.rgb(255, 245, 176),
                    ProgConfig.COLOR__DOWNLOAD_RUN_DARK, Color.rgb(174, 150, 85), "Tabelle Download, läuft");
    public static final MLC DOWNLOAD_FINISHED =
            new MLC(ProgConfig.COLOR__DOWNLOAD_FINISHED, Color.rgb(206, 255, 202),
                    ProgConfig.COLOR__DOWNLOAD_FINISHED_DARK, Color.rgb(79, 129, 74), "Tabelle Download, fertig");
    public static final MLC DOWNLOAD_ERROR =
            new MLC(ProgConfig.COLOR__DOWNLOAD_ERROR, Color.rgb(255, 233, 233),
                    ProgConfig.COLOR__DOWNLOAD_ERROR_DARK, Color.rgb(163, 82, 82), "Tabelle Download, fehlerhaft");

    // Tabelle Abos
    public static final MLC ABO_SWITCHED_OFF =
            new MLC(ProgConfig.COLOR__ABO_SWITCHED_OFF, Color.rgb(225, 225, 225),
                    ProgConfig.COLOR__ABO_SWITCHED_OFF_DARK, Color.rgb(109, 109, 109), "Tabelle Abo, ausgeschaltet");

    // Filter wenn RegEx
    public static final MLC FILTER_REGEX =
            new MLC(ProgConfig.COLOR__FILTER_REGEX, Color.rgb(225, 255, 225),
                    ProgConfig.COLOR__FILTER_REGEX_DARK, Color.rgb(128, 179, 213), "Filter ist RegEx");
    public static final MLC FILTER_REGEX_ERROR =
            new MLC(ProgConfig.COLOR__FILTER_REGEX_ERROR, Color.rgb(255, 230, 230),
                    ProgConfig.COLOR__FILTER_REGEX_ERROR_DARK, Color.rgb(170, 0, 0), "Filter ist Regex, fehlerhaft");

    // DialogDownload
    public static final MLC DOWNLOAD_NAME_ERROR =
            new MLC(ProgConfig.COLOR__DOWNLOAD_NAME_ERROR, Color.rgb(255, 233, 233),
                    ProgConfig.COLOR__DOWNLOAD_NAME_ERROR_DARK, Color.rgb(200, 183, 183), "Download, Dateiname ist fehlerhaft");


    private static ObservableList<MLC> colorList = FXCollections.observableArrayList();

    public MTColor() {
        ProgConfig.SYSTEM_THEME_CHANGED.getStringProperty().addListener((u, o, n) -> {
            setMLC();
        });

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

    public BooleanProperty changedProperty() {
        return changed;
    }

    public static synchronized ObservableList<MLC> getColorList() {
        return colorList;
    }

    public final void loadStoredColors() {
        setMLC();
    }

    public void resetAllColors() {
        colorList.forEach(MLC::resetColor);
    }

    private void setMLC() {
        for (int i = 0; i < colorList.size(); ++i) {
            colorList.get(i).setColorTheme(ProgConfig.SYSTEM_DARK_THEME.getBool());
        }
        changed.setValue(!changed.get());
    }
}
