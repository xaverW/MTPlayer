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

import de.mtplayer.mLib.tools.Data;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.data.film.FilmXml;

public class SetXml extends Data<SetXml> {

    //Tags Programmgruppen
    public static final int PROGRAMMSET_NAME = 0;
    public static final int PROGRAMMSET_PRAEFIX_DIREKT = 1;
    public static final int PROGRAMMSET_SUFFIX_DIREKT = 2;
    public static final int PROGRAMMSET_FARBE = 3;
    public static final int PROGRAMMSET_ZIEL_PFAD = 4;
    public static final int PROGRAMMSET_ZIEL_DATEINAME = 5;
    public static final int PROGRAMMSET_THEMA_ANLEGEN = 6;
    public static final int PROGRAMMSET_IST_ABSPIELEN = 7;
    public static final int PROGRAMMSET_IST_SPEICHERN = 8;
    public static final int PROGRAMMSET_IST_BUTTON = 9;
    public static final int PROGRAMMSET_IST_ABO = 10;
    public static final int PROGRAMMSET_MAX_LAENGE = 11;
    public static final int PROGRAMMSET_MAX_LAENGE_FIELD = 12;
    public static final int PROGRAMMSET_AUFLOESUNG = 13;
    public static final int PROGRAMMSET_ADD_ON = 14;
    public static final int PROGRAMMSET_BESCHREIBUNG = 15;
    public static final int PROGRAMMSET_INFO_URL = 16;
    public static final int PROGRAMMSET_INFODATEI = 17;
    public static final int PROGRAMMSET_SUBTITLE = 18;

    public static final String TAG = "Programmset";
    public static final int MAX_ELEM = 19;

    public static final String[] COLUMN_NAMES = {"Setname", "Präfix", "Suffix", "Farbe", "Zielpfad", "Zieldateiname", "Thema anlegen",
            "Abspielen", "Speichern", "Button", "Abo", "max Länge", "max Länge Feld", "Auflösung", "AddOn",
            "Beschreibung", "Url Info", "Infodatei", "Untertitel"};
    public static final String[] XML_NAMES = {"Name", "Praefix", "Suffix", "Farbe", "Zielpfad", "Zieldateiname", "Thema-anlegen",
            "Abspielen", "Speichern", "Button", "Abo", "maxLaenge", "maxLaengeFeld", "Aufloesung", "AddOn",
            "Beschreibung", "Info-URL", "Infodatei", "Untertitel"};

    private final ProgList progList = new ProgList();
    public String[] arr;


    public SetXml() {
        makeArray();
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "================================================" + Const.LINE_SEPARATOR;
        ret += "| Programmset" + Const.LINE_SEPARATOR;
        for (int i = 0; i < MAX_ELEM; ++i) {
            ret += "| " + COLUMN_NAMES[i] + ": " + arr[i] + Const.LINE_SEPARATOR;
        }
        for (final Object aListeProg : progList) {
            ret += "|" + Const.LINE_SEPARATOR;
            ret += aListeProg.toString();
        }
        ret += "|_______________________________________________" + Const.LINE_SEPARATOR;
        return ret;
    }

    void makeArray() {
        arr = new String[MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
        arr[PROGRAMMSET_THEMA_ANLEGEN] = Boolean.toString(true);
        arr[PROGRAMMSET_IST_ABSPIELEN] = Boolean.toString(false);
        arr[PROGRAMMSET_IST_SPEICHERN] = Boolean.toString(false);
        arr[PROGRAMMSET_IST_BUTTON] = Boolean.toString(false);
        arr[PROGRAMMSET_IST_ABO] = Boolean.toString(false);

        arr[PROGRAMMSET_MAX_LAENGE] = "0";
        arr[PROGRAMMSET_MAX_LAENGE_FIELD] = "0";

        arr[PROGRAMMSET_INFODATEI] = Boolean.toString(false);
        arr[PROGRAMMSET_SUBTITLE] = Boolean.toString(false);
        arr[PROGRAMMSET_AUFLOESUNG] = FilmXml.AUFLOESUNG_NORMAL;
    }
}
