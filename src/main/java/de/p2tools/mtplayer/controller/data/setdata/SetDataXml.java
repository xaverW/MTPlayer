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

package de.p2tools.mtplayer.controller.data.setdata;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;

public class SetDataXml extends P2DataSample<SetData> {

    //Tags Programmgruppen
    public static final int PROGRAMSET_ID = 0;
    public static final int PROGRAMSET_VISIBLE_NAME = 1;
    public static final int PROGRAMSET_PREFIX_DIRECT = 2;
    public static final int PROGRAMSET_SUFFIX_DIRECT = 3;
    public static final int PROGRAMSET_COLOR = 4;
    public static final int PROGRAMSET_ABO_UNTERORDNER = 5;
    public static final int PROGRAMSET_ZIEL_PFAD = 6;
    public static final int PROGRAMSET_ZIEL_DATEINAME = 7;
    public static final int PROGRAMSET_ABO_SUBDIR_ANLEGEN = 8;
    public static final int PROGRAMSET_IST_ABSPIELEN = 9;
    public static final int PROGRAMSET_IST_SPEICHERN = 10;
    public static final int PROGRAMSET_IST_BUTTON = 11;
    public static final int PROGRAMSET_IST_ABO = 12;
    public static final int PROGRAMSET_MAX_LAENGE = 13;
    public static final int PROGRAMSET_MAX_LAENGE_FIELD = 14;
    public static final int PROGRAMSET_AUFLOESUNG = 15;
    public static final int PROGRAMSET_ADD_ON = 16;
    public static final int PROGRAMSET_BESCHREIBUNG = 17;
    public static final int PROGRAMSET_INFO_URL = 18;
    public static final int PROGRAMSET_INFODATEI = 19;
    public static final int PROGRAMSET_SUBTITLE = 20;

    public static final int MAX_ELEM = 21;

    public static final String[] COLUMN_NAMES = {"Name", "Setname", "Präfix", "Suffix", "Farbe",
            "Abo Unterordner", "Zielpfad", "Zieldateiname", "Thema anlegen",
            "Abspielen", "Speichern", "Button", "Abo", "max Länge", "max Länge Feld", "Auflösung", "AddOn",
            "Beschreibung", "Url Info", "Infodatei", "Untertitel"};
    public static final String[] XML_NAMES = {"Name", "Setname", "Praefix", "Suffix", "Farbe",
            "AboUnterordner", "Zielpfad", "Zieldateiname", "Thema-anlegen",
            "Abspielen", "Speichern", "Button", "Abo", "maxLaenge", "maxLaengeFeld", "Aufloesung", "AddOn",
            "Beschreibung", "Info-URL", "Infodatei", "Untertitel"};


    public String[] arr;


    public SetDataXml() {
        makeArray();
    }

    void makeArray() {
        arr = new String[MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
        arr[PROGRAMSET_ABO_SUBDIR_ANLEGEN] = Boolean.toString(true);
        arr[PROGRAMSET_IST_ABSPIELEN] = Boolean.toString(false);
        arr[PROGRAMSET_IST_SPEICHERN] = Boolean.toString(false);
        arr[PROGRAMSET_IST_BUTTON] = Boolean.toString(false);
        arr[PROGRAMSET_IST_ABO] = Boolean.toString(false);

        arr[PROGRAMSET_ABO_UNTERORDNER] = "0";
        arr[PROGRAMSET_MAX_LAENGE] = "0";
        arr[PROGRAMSET_MAX_LAENGE_FIELD] = "0";

        arr[PROGRAMSET_INFODATEI] = Boolean.toString(false);
        arr[PROGRAMSET_SUBTITLE] = Boolean.toString(false);
        arr[PROGRAMSET_AUFLOESUNG] = FilmDataMTP.RESOLUTION_NORMAL;
    }
}
