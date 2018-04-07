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

import de.mtplayer.mLib.tools.FilmDate;
import de.mtplayer.mLib.tools.Functions;
import de.p2tools.p2Lib.tools.GermanStringSorter;
import de.p2tools.p2Lib.tools.log.PLog;
import org.apache.commons.lang3.time.FastDateFormat;

public class HistoryData implements Comparable<HistoryData> {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String fName) {
        url = fName;
    }

    public FilmDate getDate() {
        return date;
    }

    public void setDate(FilmDate fName) {
        date = fName;
    }

    private static final FastDateFormat sdf_datum = FastDateFormat.getInstance("dd.MM.yyyy");
    private static final GermanStringSorter sorter = GermanStringSorter.getInstance();
    private final static String TRENNER_2 = "  |###|  ";
    private final static String TRENNER_1 = " |#| ";

    public String title = new String("");
    public String theme = new String("");
    public String url = new String("");
    public FilmDate date = new FilmDate();

    public HistoryData(String date, String thema, String title, String url) {
        setTitle(title);
        setTheme(thema);
        setUrl(url);
        try {
            setDate(new FilmDate(sdf_datum.parse(date).getTime()));
        } catch (final Exception ignore) {
            setDate(new FilmDate(0));
        }
    }

    public static String getLine(String date, String thema, String title, String url) {
        final int MAX_TITLE = 25;
        if (thema.length() < MAX_TITLE) {
            // nur wenn zu kurz, dann anpassen, so bleibt das Log ~lesbar
            // und Titel werden nicht abgeschnitten
            thema = Functions.textLaenge(MAX_TITLE, thema, false /* mitte */, false /*addVorne*/);
        }

        return date + TRENNER_1
                + putzen(thema) + TRENNER_1
                + putzen(title) + TRENNER_2
                + url + '\n';
    }

    public String getLine() {
        return getLine(getDate().toString(), getTheme(), getTitle(), getUrl());
    }

    public static HistoryData getUrlAusZeile(String zeile) {
        // 29.05.2014 |#| Abendschau                |#| Patenkind trifft Groß                     |###|  http://cdn-storage.br.de/iLCpbHJGNLT6NK9HsLo6s61luK4C_2rc5U1S/_-OS/5-8y9-NP/5bb33365-038d-46f7-914b-eb83fab91448_E.mp4
        String url = "", thema = "", titel = "", datum = "";
        int a1;
        try {
            if (zeile.contains(TRENNER_2)) {
                //neues Logfile-Format
                a1 = zeile.lastIndexOf(TRENNER_2);
                a1 += TRENNER_2.length();
                url = zeile.substring(a1).trim();
                // titel
                titel = zeile.substring(zeile.lastIndexOf(TRENNER_1) + TRENNER_1.length(), zeile.lastIndexOf(TRENNER_2)).trim();
                datum = zeile.substring(0, zeile.indexOf(TRENNER_1)).trim();
                thema = zeile.substring(zeile.indexOf(TRENNER_1) + TRENNER_1.length(), zeile.lastIndexOf(TRENNER_1)).trim();
            } else {
                url = zeile;
            }
        } catch (final Exception ex) {
            PLog.errorLog(398853224, ex);
        }
        return new HistoryData(datum, thema, titel, url);
    }

    private static String putzen(String s) {
        s = s.replace("\n", "");
        s = s.replace("|", "");
        s = s.replace(TRENNER_1, "");
        s = s.replace(TRENNER_2, "");
        return s;
    }

    @Override
    public int compareTo(HistoryData arg0) {
        return sorter.compare(getTitle(), arg0.getTitle());
    }
}
