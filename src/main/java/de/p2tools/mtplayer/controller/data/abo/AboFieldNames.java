/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.data.abo;

public class AboFieldNames {

    public static final String ABO_NO = "Nr";
    public static final String ABO_ACTIV = "aktiv";
    public static final String ABO_NAME = "Name";
    public static final String ABO_DESCRIPTION = "Beschreibung";
    public static final String ABO__RESOLUTION = "Aufloesung";
    public static final String ABO_CHANNEL = "Sender";
    public static final String ABO_THEME = "Thema";
    public static final String ABO_THEME_EXACT = "exakt";
    public static final String ABO_THEME_TITLE = "Thema-Titel";
    public static final String ABO_TITLE = "Titel";
    public static final String ABO_SOMEWHERE = "Irgendwo";
    public static final String ABO_TIME_RANGE = "Zeitraum";
    public static final String ABO_MIN_DURATION = "min. Dauer";
    public static final String ABO_MAX_DURATION = "max. Dauer";
    public static final String ABO_START_TIME = "Startzeit";
    public static final String ABO_SUB_DUR = "Zielpfad";
    public static final String ABO_DATE = "letztes Abo";
    public static final String ABO_SET_DATA_ID = "Programmset";

    public static final int ABO_THEME_EXACT_NO = 7;
    public static final int ABO_NO_NO = 0;
    public static final int ABO_ON_NO = 1;
    public static final int ABO_NAME_NO = 2;
    public static final int ABO_DESCRIPTION_NO = 3;
    public static final int ABO_RESOLUTION_NO = 4;
    public static final int ABO_CHANNEL_NO = 5;
    public static final int ABO_THEME_NO = 6;
    public static final int ABO_THEME_TITLE_NO = 8;
    public static final int ABO_TITLE_NO = 9;
    public static final int ABO_SOMEWHERE_NO = 10;
    public static final int ABO_TIME_RANGE_NO = 11;
    public static final int ABO_MIN_DURATION_NO = 12;
    public static final int ABO_MAX_DURATION_NO = 13;
    public static final int ABO_START_TIME_NO = 14;
    public static final int ABO_DEST_PATH_NO = 15;
    public static final int ABO_DOWN_DATE_NO = 16;
    public static final int ABO_SET_DATA_ID_NO = 17;

    public static final String[] COLUMN_NAMES = {ABO_NO,
            ABO_ACTIV,
            ABO_NAME,
            ABO_DESCRIPTION,
            ABO__RESOLUTION,
            ABO_CHANNEL,
            ABO_THEME,
            ABO_THEME_EXACT,
            ABO_THEME_TITLE,
            ABO_TITLE,
            ABO_SOMEWHERE,
            ABO_TIME_RANGE,
            ABO_MIN_DURATION,
            ABO_MAX_DURATION,
            ABO_START_TIME,
            ABO_SUB_DUR,
            ABO_DATE,
            ABO_SET_DATA_ID};
    
    public static final String[] XML_NAMES = {"Nr",
            "aktiv",
            "Name",
            "Beschreibung",
            "Aufloesung",
            "Sender",
            "Thema",
            "Thema-exakt",
            "Thema-Titel",
            "Titel",
            "Irgendwo",
            "Zeitraum",
            "Mindestdauer",
            "Maxdauer",
            "Startzeit",
            "Zielpfad",
            "letztes_Abo",
            "Programmset"};

    public static int MAX_ELEM = XML_NAMES.length;
}