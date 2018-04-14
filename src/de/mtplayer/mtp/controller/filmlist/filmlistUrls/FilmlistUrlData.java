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

package de.mtplayer.mtp.controller.filmlist.filmlistUrls;

import de.p2tools.p2Lib.tools.log.PLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public class FilmlistUrlData implements Comparable<FilmlistUrlData> {

    public static final String SERVER_ART_AKT = "akt";
    public static final String SERVER_ART_DIFF = "diff";

    public static final String FILMLIST_UPDATE_SERVER_PRIO_1 = "1";
    public static final String FILMLIST_UPDATE_SERVER = "filmlist-update-server";
    public static final String FILMLIST_UPDATE_SERVER_NR = "filmlist-update-server-nr";
    public static final int FILMLIST_UPDATE_SERVER_NR_NR = 0;
    public static final String FILMLIST_UPDATE_SERVER_URL = "filmlist-update-server-url";
    public static final int FILMLIST_UPDATE_SERVER_URL_NR = 1;
    public static final String FILMLIST_UPDATE_SERVER_DATUM = "filmlist-update-server-datum"; // Datum in UTC
    public static final int FILMLIST_UPDATE_SERVER_DATUM_NR = 2;
    public static final String FILMLIST_UPDATE_SERVER_ZEIT = "filmlist-update-server-zeit"; // Zeit in UTC
    public static final int FILMLIST_UPDATE_SERVER_ZEIT_NR = 3;
    public static final String FILMLIST_UPDATE_SERVER_PRIO = "filmlist-update-server-prio";
    public static final int FILMLIST_UPDATE_SERVER_PRIO_NR = 4;
    public static final String FILMLIST_UPDATE_SERVER_ART = "filmlist-update-server-art";
    public static final int FILMLIST_UPDATE_SERVER_ART_NR = 5;
    public static final int FILMLIST_UPDATE_SERVER_MAX_ELEM = 6;
    public static final String[] FILMLIST_UPDATE_SERVER_COLUMN_NAMES = {FILMLIST_UPDATE_SERVER_NR, FILMLIST_UPDATE_SERVER_URL,
            FILMLIST_UPDATE_SERVER_DATUM, FILMLIST_UPDATE_SERVER_ZEIT, FILMLIST_UPDATE_SERVER_PRIO, FILMLIST_UPDATE_SERVER_ART};

    public String[] arr;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public FilmlistUrlData() {
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        makeArr();
    }

    public FilmlistUrlData(String url, String prio, String art) {
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        makeArr();
        arr[FILMLIST_UPDATE_SERVER_URL_NR] = url;
        arr[FILMLIST_UPDATE_SERVER_PRIO_NR] = prio;
        arr[FILMLIST_UPDATE_SERVER_DATUM_NR] = "";
        arr[FILMLIST_UPDATE_SERVER_ZEIT_NR] = "";
        arr[FILMLIST_UPDATE_SERVER_ART_NR] = art;
    }

    public FilmlistUrlData(String url, String art) {
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        makeArr();
        arr[FILMLIST_UPDATE_SERVER_URL_NR] = url;
        arr[FILMLIST_UPDATE_SERVER_PRIO_NR] = FilmlistUrlData.FILMLIST_UPDATE_SERVER_PRIO_1;
        arr[FILMLIST_UPDATE_SERVER_DATUM_NR] = "";
        arr[FILMLIST_UPDATE_SERVER_ZEIT_NR] = "";
        arr[FILMLIST_UPDATE_SERVER_ART_NR] = art;
    }

//    public Date getDate() {
//        String date = arr[FILMLIST_UPDATE_SERVER_DATUM_NR] + " " + arr[FILMLIST_UPDATE_SERVER_ZEIT_NR];
//        Date d;
//        try {
//            d = sdf.parse(date);
//        } catch (Exception ex) {
//            d = new Date();
//        }
//        return d;
//    }

    @Override
    public int compareTo(FilmlistUrlData arg0) {
        int ret = 0;
        try {
            //31.10.2010	16:54:17
            String ich = arr[FILMLIST_UPDATE_SERVER_DATUM_NR] + " " + arr[FILMLIST_UPDATE_SERVER_ZEIT_NR];
            String du = arg0.arr[FILMLIST_UPDATE_SERVER_DATUM_NR] + " " + arg0.arr[FILMLIST_UPDATE_SERVER_ZEIT_NR];
            if (ich.equals(du)) {
                return 0;
            }
            Date d_ich = sdf.parse(ich);
            Date d_du = sdf.parse(du);
            ret = d_du.compareTo(d_ich);
        } catch (ParseException ex) {
            PLog.errorLog(936542876, ex);
        }
        return ret;
    }

    private void makeArr() {
        arr = new String[FILMLIST_UPDATE_SERVER_MAX_ELEM];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }
}
