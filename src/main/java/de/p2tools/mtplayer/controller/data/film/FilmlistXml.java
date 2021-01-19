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

package de.p2tools.mtplayer.controller.data.film;

public class FilmlistXml {

    public static final String FILMLIST = "Filmliste";

    public static final String FILMLIST_DATE = "Filmliste-Datum";
    public static final int FILMLIST_DATE_NR = 0;
    public static final String FILMLIST_DATE_GMT = "Filmliste-Datum-GMT";
    public static final int FILMLIST_DATE_GMT_NR = 1;
    public static final String FILMLIST_VERSION = "Filmliste-Version";
    public static final int FILMLIST_VERSION_NR = 2;
    public static final String FILMLIST_PROGRAM = "Filmliste-Programm";
    public static final int FILMLIST_PRGRAM_NR = 3;
    public static final String FILMLIST_ID = "Filmliste-Id";
    public static final int FILMLIST_ID_NR = 4;
    public static final int MAX_ELEM = 5;

    public static final String[] COLUMN_NAMES = {FILMLIST_DATE, FILMLIST_DATE_GMT,
            FILMLIST_VERSION, FILMLIST_PROGRAM, FILMLIST_ID};

}
