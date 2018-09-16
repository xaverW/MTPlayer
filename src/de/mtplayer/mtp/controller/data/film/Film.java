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

package de.mtplayer.mtp.controller.data.film;

import de.mtplayer.mLib.tools.FilmDate;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.p2tools.p2Lib.tools.log.PLog;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Film extends FilmProps {

    public static final String RESOLUTION_NORMAL = "normal";
    public static final String RESOLUTION_HD = "hd";
    public static final String RESOLUTION_SMALL = "klein";
    public static final int FILM_TIME_EMPTY = -1;
    public static final String GEO_DE = "DE";
    public static final String GEO_AT = "AT";
    public static final String GEO_CH = "CH";
    public static final String GEO_EU = "EU";
    public static final String GEO_WELT = "WELT";
    private Abo abo = null;


    public Film() {
        filmSize = new FilmSize(0); // Dateigröße in MByte
    }

    public void init() {
        setHd(!arr[FilmXml.FILM_URL_HD].isEmpty() || !arr[FilmXml.FILM_URL_RTMP_HD].isEmpty());
        setSmall(!arr[FilmXml.FILM_URL_SMALL].isEmpty() || !arr[FilmXml.FILM_URL_RTMP_SMALL].isEmpty());
        setUt(!arr[FilmXml.FILM_URL_SUBTITLE].isEmpty());
        setShown(ProgData.getInstance().history.checkIfUrlAlreadyIn(getUrlHistory()));
        preserveMemory();

        // ================================
        // Dateigröße
        filmSize = new FilmSize(this);

        // ================================
        // Filmdauer
        setFilmLength();

        // ================================
        // Datum
        setDatum();

        //=================================
        // Filmzeit
        setFilmTime();
    }

    public void initDate() {
        // ActList braucht nur das, geht schneller
        setDatum();
    }

    private void setFilmTime() {
        if (!arr[FilmXml.FILM_TIME].isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime time = LocalTime.parse(arr[FilmXml.FILM_TIME], formatter);
            setFilmTime(time.toSecondOfDay());
        } else {
            setFilmTime(FILM_TIME_EMPTY);
        }
    }

    public String getUrlForResolution(String resolution) {
        if (resolution.equals(RESOLUTION_SMALL)) {
            return getUrlNormalKlein();
        }
        if (resolution.equals(RESOLUTION_HD)) {
            return getUrlNormalHd();
        }
        return arr[FilmXml.FILM_URL];
    }

    public String getUrlFlvstreamerForResolution(String resolution) {
        if (resolution.equals(RESOLUTION_SMALL)) {
            return getUrlFlvstreamerSmall();
        }
        if (resolution.equals(RESOLUTION_HD)) {
            return getUrlFlvstreamerHd();
        }
        return getUrlFlvstreamer();
    }

    public String getIndex() {
        // liefert einen eindeutigen Index für die Filmliste (update der Filmliste mit Diff-Liste)
        // URL beim KiKa und ORF ändern sich laufend!
        return (arr[FILM_CHANNEL] + arr[FILM_THEME]).toLowerCase() + getUrlForHash();
    }


    public String getUrlForHash() {
        // liefert die URL zum VERGLEICHEN!!
        String url = "";
        if (arr[FilmXml.FILM_CHANNEL].equals(ProgConst.ORF)) {
            final String uurl = arr[FilmXml.FILM_URL];
            try {
                final String online = "/online/";
                url = uurl.substring(uurl.indexOf(online) + online.length());
                if (!url.contains("/")) {
                    PLog.errorLog(915230478, "Url: " + uurl);
                    return "";
                }
                url = url.substring(url.indexOf('/') + 1);
                if (!url.contains("/")) {
                    PLog.errorLog(915230478, "Url: " + uurl);
                    return "";
                }
                url = url.substring(url.indexOf('/') + 1);
                if (url.isEmpty()) {
                    PLog.errorLog(915230478, "Url: " + uurl);
                    return "";
                }
            } catch (final Exception ex) {
                PLog.errorLog(915230478, ex, "Url: " + uurl);
            }
            return ProgConst.ORF + "----" + url;
        } else {
            return arr[FilmXml.FILM_URL];
        }

    }

    private void preserveMemory() {
        // ================================
        // Speicher sparen
        if (arr[FilmXml.FILM_SIZE].length() < 3) { //todo brauchts das überhaupt??
            arr[FilmXml.FILM_SIZE] = arr[FilmXml.FILM_SIZE].intern();
        }
        if (arr[FilmXml.FILM_URL_SMALL].length() < 15) {
            arr[FilmXml.FILM_URL_SMALL] = arr[FilmXml.FILM_URL_SMALL].intern();
        }

        arr[FilmXml.FILM_DATE] = arr[FilmXml.FILM_DATE].intern();
        arr[FilmXml.FILM_TIME] = arr[FilmXml.FILM_TIME].intern();
    }

    private String fillString(int anz, String s) {
        while (s.length() < anz) {
            s = '0' + s;
        }
        return s;
    }

    private void setFilmLength() {
        try {
            if (!arr[FilmXml.FILM_DURATION].contains(":") && !arr[FilmXml.FILM_DURATION].isEmpty()) {
                // nur als Übergang bis die Liste umgestellt ist
                long l = Long.parseLong(arr[FilmXml.FILM_DURATION]);
                dauerL = l;
                if (l > 0) {
                    final long hours = l / 3600;
                    l = l - (hours * 3600);
                    final long min = l / 60;
                    l = l - (min * 60);
                    final long seconds = l;
                    arr[FilmXml.FILM_DURATION] = fillString(2, String.valueOf(hours)) + ':'
                            + fillString(2, String.valueOf(min))
                            + ':'
                            + fillString(2, String.valueOf(seconds));
                } else {
                    arr[FilmXml.FILM_DURATION] = "";
                }
            } else {
                dauerL = 0;
                if (!arr[FilmXml.FILM_DURATION].isEmpty()) {
                    final String[] parts = arr[FilmXml.FILM_DURATION].split(":");
                    long power = 1;
                    for (int i = parts.length - 1; i >= 0; i--) {
                        dauerL += Long.parseLong(parts[i]) * power;
                        power *= 60;
                    }
                }
            }
        } catch (final Exception ex) {
            dauerL = 0;
            PLog.errorLog(468912049, "Dauer: " + arr[FilmXml.FILM_DURATION]);
        }
    }

    private void setDatum() {
        filmDate.setTime(0);

        if (!arr[FilmXml.FILM_DATE].isEmpty()) {
            // nur dann gibts ein Datum
            try {
                if (arr[FilmXml.FILM_DATE_LONG].isEmpty()) {
                    if (arr[FilmXml.FILM_TIME].isEmpty()) {
                        filmDate = new FilmDate(sdf_date.parse(arr[FilmXml.FILM_DATE]).getTime());
                    } else {
                        filmDate = new FilmDate(sdf_date_time.parse(arr[FilmXml.FILM_DATE] + arr[FilmXml.FILM_TIME]).getTime());
                    }
                    arr[FILM_DATE_LONG] = String.valueOf(filmDate.getTime() / 1000);
                } else {
                    final long l = Long.parseLong(arr[FilmXml.FILM_DATE_LONG]);
                    filmDate = new FilmDate(l * 1000 /* sind SEKUNDEN!! */);
                }
            } catch (final Exception ex) {
                PLog.errorLog(915236701, ex, new String[]{"Datum: " + arr[FilmXml.FILM_DATE], "Zeit: " + arr[FilmXml.FILM_TIME]});
                filmDate = new FilmDate(0);
                arr[FilmXml.FILM_DATE] = "";
                arr[FilmXml.FILM_TIME] = "";
            }
        }

    }


    private String getUrlNormalKlein() {
        // liefert die kleine normale URL
        if (!arr[FilmXml.FILM_URL_SMALL].isEmpty()) {
            try {
                final int i = Integer.parseInt(arr[FilmXml.FILM_URL_SMALL].substring(0, arr[FilmXml.FILM_URL_SMALL].indexOf('|')));
                return arr[FilmXml.FILM_URL].substring(0, i)
                        + arr[FilmXml.FILM_URL_SMALL].substring(arr[FilmXml.FILM_URL_SMALL].indexOf('|') + 1);
            } catch (final Exception ignored) {
            }
        }
        return arr[FilmXml.FILM_URL];
    }

    private String getUrlNormalHd() {
        // liefert die HD normale URL
        if (!arr[FilmXml.FILM_URL_HD].isEmpty()) {
            try {
                final int i = Integer.parseInt(arr[FilmXml.FILM_URL_HD].substring(0, arr[FilmXml.FILM_URL_HD].indexOf('|')));
                return arr[FilmXml.FILM_URL].substring(0, i)
                        + arr[FilmXml.FILM_URL_HD].substring(arr[FilmXml.FILM_URL_HD].indexOf('|') + 1);
            } catch (final Exception ignored) {
            }
        }
        return arr[FilmXml.FILM_URL];
    }

    private String getUrlFlvstreamer() {
        String ret;
        if (!arr[FilmXml.FILM_URL_RTMP].isEmpty()) {
            ret = arr[FilmXml.FILM_URL_RTMP];
        } else if (arr[FilmXml.FILM_URL].startsWith(ProgConst.RTMP_PRTOKOLL)) {
            ret = ProgConst.RTMP_FLVSTREAMER + arr[FilmXml.FILM_URL];
        } else {
            ret = arr[FilmXml.FILM_URL];
        }
        return ret;
    }

    private String getUrlFlvstreamerSmall() {
        // liefert die kleine flvstreamer URL
        String ret;
        if (!arr[FilmXml.FILM_URL_RTMP_SMALL].isEmpty()) {
            // es gibt eine kleine RTMP
            try {
                final int i =
                        Integer.parseInt(arr[FilmXml.FILM_URL_RTMP_SMALL].substring(0, arr[FilmXml.FILM_URL_RTMP_SMALL].indexOf('|')));
                return arr[FilmXml.FILM_URL_RTMP].substring(0, i)
                        + arr[FilmXml.FILM_URL_RTMP_SMALL].substring(arr[FilmXml.FILM_URL_RTMP_SMALL].indexOf('|') + 1);
            } catch (final Exception ignored) {
            }
        }
        // es gibt keine kleine RTMP
        if (!arr[FilmXml.FILM_URL_RTMP].isEmpty()) {
            // dann gibts keine kleine
            ret = arr[FilmXml.FILM_URL_RTMP];
        } else {
            // dann gibts überhaupt nur die normalen URLs
            ret = getUrlNormalKlein();
            // und jetzt noch "-r" davorsetzten wenn nötig
            if (ret.startsWith(ProgConst.RTMP_PRTOKOLL)) {
                ret = ProgConst.RTMP_FLVSTREAMER + ret;
            }
        }
        return ret;
    }

    private String getUrlFlvstreamerHd() {
        // liefert die HD flvstreamer URL
        if (!arr[FilmXml.FILM_URL_RTMP_HD].isEmpty()) {
            // es gibt eine HD RTMP
            try {
                final int i =
                        Integer.parseInt(arr[FilmXml.FILM_URL_RTMP_HD].substring(0, arr[FilmXml.FILM_URL_RTMP_HD].indexOf('|')));
                return arr[FilmXml.FILM_URL_RTMP].substring(0, i)
                        + arr[FilmXml.FILM_URL_RTMP_HD].substring(arr[FilmXml.FILM_URL_RTMP_HD].indexOf('|') + 1);
            } catch (final Exception ignored) {
            }
        }
        // es gibt keine HD RTMP
        return getUrlFlvstreamer();
    }

    public Film getCopy() {
        final Film ret = new Film();
        System.arraycopy(arr, 0, ret.arr, 0, arr.length);
        ret.filmDate = filmDate;
        ret.nr = nr;
        ret.filmSize = filmSize;
        ret.dauerL = dauerL;
        ret.abo = abo;
        ret.setHd(isHd());
        ret.setSmall(isSmall());
        ret.setUt(isUt());
        return ret;
    }


    public synchronized Abo getAbo() {
        return abo;
    }

    public synchronized void setAbo(Abo abo) {
        this.abo = abo;
    }
}
