/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

package de.mtplayer.mtp.controller.data.abo;

import de.mtplayer.mLib.tools.Data;

public class AboXml extends Data<AboXml> {

    public static final int ABO_NR = 0;
    public static final int ABO_ON = 1;
    public static final int ABO_NAME = 2;
    public static final int ABO_AUFLOESUNG = 3;
    public static final int ABO_SENDER = 4;
    public static final int ABO_SENDER_EXAKT = 5;
    public static final int ABO_THEMA = 6;
    public static final int ABO_THEMA_EXAKT = 7;
    public static final int ABO_TITEL = 8;
    public static final int ABO_THEMA_TITEL = 9;
    public static final int ABO_IRGENDWO = 10;
    public static final int ABO_MINDESTDAUER = 11;
    public static final int ABO_MAXDESTDAUER = 12;
    public static final int ABO_ZIELPFAD = 13;
    public static final int ABO_DOWN_DATUM = 14;
    public static final int ABO_PSET = 15;

    public static final String[] COLUMN_NAMES = {"Nr",
            "aktiv",
            "Name",
            "Auflösung",
            "Sender",
            "exakt",
            "Thema",
            "exakt",
            "Titel",
            "Thema-Titel",
            "Irgendwo",
            "min Dauer",
            "max Dauer",
            "Zielpfad",
            "letztes Abo",
            "Programmset"};

    public static final String[] XML_NAMES = {"Nr",
            "aktiv",
            "Name",
            "Aufloesung",
            "Sender",
            "Sender-exakt",
            "Thema",
            "Thema-exakt",
            "Titel",
            "Thema-Titel",
            "Irgendwo",
            "Mindestdauer",
            "Maxdauer",
            "Zielpfad",
            "letztes_Abo",
            "Programmset"};

    public static int MAX_ELEM = XML_NAMES.length;
    public static final String TAG = "Abonnement";

    public AboXml() {
        arr = makeArr(MAX_ELEM);
    }

    @Override
    public int compareTo(AboXml arg0) {
        return sorter.compare(arr[ABO_NAME], arg0.arr[ABO_NAME]);
    }
}
