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
    public static final MLC FILM_LIVESTREAM = new MLC(ProgConfig.COLOR__FILM_LIVESTREAM, Color.rgb(130, 0, 0), "Filme, Livestreams");
    public static final MLC FILM_HISTORY = new MLC(ProgConfig.COLOR__FILM_HISTORY, Color.rgb(223, 223, 223), "Filme, gesehen");
    public static final MLC FILM_NEW = new MLC(ProgConfig.COLOR__FILM_NEW, Color.rgb(0, 0, 240), "Filme, neue");
    public static final MLC FILM_BOOKMARK = new MLC(ProgConfig.COLOR__FILM_BOOKMARK, Color.rgb(255, 236, 151), "Filme, Bookmarks");
    public static final MLC FILM_GEOBLOCK = new MLC(ProgConfig.COLOR__FILM_GEOBLOCK_BACKGROUND, Color.rgb(255, 168, 0), "Film, geogeblockt");
    public static final MLC FILM_GEOBLOCK_BACKGROUND = new MLC(ProgConfig.COLOR__FILM_GEOBLOCK_BACKGROUND, Color.rgb(255, 254, 230), "Film, geogeblockt");
    public static final MLC FILM_GEOBLOCK_BACKGROUND_SEL = new MLC(ProgConfig.COLOR__FILM_GEOBLOCK_BACKGROUND_SEL, Color.rgb(255, 251, 179), "Film, geogeblockt, selektiert");

    // Tabelle Downloads
    public static final MLC DOWNLOAD_IS_ABO = new MLC(ProgConfig.COLOR__DOWNLOAD_IS_ABO, Color.rgb(138, 67, 0), "Download ist ein Abo");
    public static final MLC DOWNLOAD_IS_DIREKT_DOWNLOAD = new MLC(ProgConfig.COLOR__DOWNLOAD_IS_DIREKT_DOWNLOAD, Color.rgb(0, 72, 138), "Download ist ein direkter DownloadXml");
    public static final MLC DOWNLOAD_SHOW = new MLC(ProgConfig.COLOR__DOWNLOAD_SHOW, Color.rgb(0, 125, 0), "Download kann schon angesehen werden");
    // status Downloads
    public static final MLC DOWNLOAD_WAIT = new MLC(ProgConfig.COLOR__DOWNLOAD_WAIT, Color.rgb(239, 244, 255), "Download, noch nicht gestartet");
    public static final MLC DOWNLOAD_WAIT_SEL = new MLC(ProgConfig.COLOR__DOWNLOAD_WAIT_SEL, Color.rgb(199, 206, 222), "Download, noch nicht gestartet, selektiert");
    public static final MLC DOWNLOAD_RUN = new MLC(ProgConfig.COLOR__DOWNLOAD_RUN, Color.rgb(255, 245, 176), "Download, läuft");
    public static final MLC DOWNLOAD_RUN_SEL = new MLC(ProgConfig.COLOR__DOWNLOAD_RUN_SEL, Color.rgb(206, 178, 92), "Download, läuft, selektiert");
    public static final MLC DOWNLOAD_FINISHED = new MLC(ProgConfig.COLOR__DOWNLOAD_FINISHED, Color.rgb(206, 255, 202), "Download, fertig");
    public static final MLC DOWNLOAD_FINISHED_SEL = new MLC(ProgConfig.COLOR__DOWNLOAD_FINISHED_SEL, Color.rgb(115, 206, 92), "Download, fertig, selektiert");
    public static final MLC DOWNLOAD_ERROR = new MLC(ProgConfig.COLOR__DOWNLOAD_ERROR, Color.rgb(255, 233, 233), "Download, fehlerhaft");
    public static final MLC DOWNLOAD_ERROR_SEL = new MLC(ProgConfig.COLOR__DOWNLOAD_ERROR_SEL, Color.rgb(206, 92, 128), "Download, fehlerhaft, selektiert");

    // Tabelle Abos
    public static final MLC ABO_SWITCHED_OFF = new MLC(ProgConfig.COLOR__ABO_SWITCHED_OFF, Color.rgb(225, 225, 225), "Abo, ausgeschaltet");
    public static final MLC ABO_SWITCHED_OFF_SEL = new MLC(ProgConfig.COLOR__ABO_SWITCHED_OFF_SEL, Color.rgb(190, 190, 190), "Abo, ausgeschaltet, selektiert");

    // Filter wenn RegEx
    public static final MLC FILTER_REGEX = new MLC(ProgConfig.COLOR__FILTER_REGEX, Color.rgb(153, 214, 255), "Filter ist RegEx");
    public static final MLC FILTER_REGEX_ERROR = new MLC(ProgConfig.COLOR__FILTER_REGEX_ERROR, Color.RED, "Filter ist Regex, fehlerhaft");

    // DialogDownload
    public static final MLC DOWNLOAD_NAME_ERROR = new MLC(ProgConfig.COLOR__DOWNLOAD_NAME_ERROR, Color.rgb(255, 233, 233), "Download, Dateiname ist fehlerhaft");
    public static final MLC DOWNLOAD_NAME_EXISTS = new MLC(ProgConfig.COLOR__DOWNLOAD_NAME_EXISTS, Color.rgb(190, 0, 0), "Download, Dateiname existiert schon");
    public static final MLC DOWNLOAD_NAME_NEW = new MLC(ProgConfig.COLOR__DOWNLOAD_NAME_NEW, Color.rgb(0, 140, 0), "Download, Dateiname ist neu");
    public static final MLC DOWNLOAD_NAME_OLD = new MLC(ProgConfig.COLOR__DOWNLOAD_NAME_OLD, Color.rgb(0, 0, 200), "Download, Dateiname ist der alte");

    private static ObservableList<MLC> colorList = FXCollections.observableArrayList();

    public MTColor() {
        colorList.add(FILM_LIVESTREAM);
        colorList.add(FILM_HISTORY);
        colorList.add(FILM_NEW);
        colorList.add(FILM_BOOKMARK);
        colorList.add(FILM_GEOBLOCK);
//        colorList.add(FILM_GEOBLOCK_BACKGROUND);
//        colorList.add(FILM_GEOBLOCK_BACKGROUND_SEL);
//        colorList.add(DOWNLOAD_IS_ABO);
//        colorList.add(DOWNLOAD_IS_DIREKT_DOWNLOAD);
//        colorList.add(DOWNLOAD_SHOW);
        colorList.add(DOWNLOAD_WAIT);
//        colorList.add(DOWNLOAD_WAIT_SEL);
        colorList.add(DOWNLOAD_RUN);
//        colorList.add(DOWNLOAD_RUN_SEL);
        colorList.add(DOWNLOAD_FINISHED);
//        colorList.add(DOWNLOAD_FINISHED_SEL);
        colorList.add(DOWNLOAD_ERROR);
//        colorList.add(DOWNLOAD_ERROR_SEL);
        colorList.add(ABO_SWITCHED_OFF);
//        colorList.add(ABO_SWITCHED_OFF_SEL);
        colorList.add(FILTER_REGEX);
        colorList.add(FILTER_REGEX_ERROR);
        colorList.add(DOWNLOAD_NAME_ERROR);
//        colorList.add(DOWNLOAD_NAME_EXISTS);
//        colorList.add(DOWNLOAD_NAME_NEW);
//        colorList.add(DOWNLOAD_NAME_OLD);
    }

    public static synchronized ObservableList<MLC> getColorList() {
        return colorList;
    }


    public final void load() {
        colorList.stream().filter(MLC -> !MLC.getMlConfigs().get().isEmpty()).forEach(MLC -> {
            try {
                MLC.setColorFromHex(MLC.getMlConfigs().get());
            } catch (final Exception ignored) {
                MLC.resetColor();
            }
        });
    }

    public final void save() {
        for (final MLC MLC : colorList) {
            MLC.getMlConfigs().setValue(String.valueOf(MLC.getColorToHex()));
        }
    }

    public void reset() {
        colorList.forEach(MLC::resetColor);
        save();
    }
}
