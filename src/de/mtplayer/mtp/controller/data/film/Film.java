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
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.p2tools.p2Lib.tools.log.PLog;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Film extends FilmProps {

    private Abo abo = null;


    public Film() {
        filmSize = new FilmSize(0); // Dateigröße in MByte
    }

    public void init() {
        setHd(!arr[FilmXml.FILM_URL_HD].isEmpty() || !arr[FilmXml.FILM_URL_RTMP_HD].isEmpty());
        setSmall(!arr[FilmXml.FILM_URL_KLEIN].isEmpty() || !arr[FilmXml.FILM_URL_RTMP_KLEIN].isEmpty());
        setUt(!arr[FilmXml.FILM_URL_SUBTITLE].isEmpty());
        setShown(Daten.getInstance().history.checkIfExists(getUrlHistory()));
        preserveMemory();

        // ================================
        // Dateigröße
        filmSize = new FilmSize(this);

        // ================================
        // Filmdauer
        setFilmdauer();

        // ================================
        // Datum
        setDatum();

        //=================================
        // Filmzeit
        setFilmTime();
    }

    private void setFilmTime() {
        if (!arr[FilmXml.FILM_ZEIT].isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime time = LocalTime.parse(arr[FilmXml.FILM_ZEIT], formatter);
            setFilmtime(time.toSecondOfDay());
        } else {
            setFilmtime(FILMTIME_EMPTY);
        }
    }

    public String getUrlFuerAufloesung(String aufloesung) {
        if (aufloesung.equals(AUFLOESUNG_KLEIN)) {
            return getUrlNormalKlein();
        }
        if (aufloesung.equals(AUFLOESUNG_HD)) {
            return getUrlNormalHd();
        }
        return arr[FilmXml.FILM_URL];
    }

    public String getUrlFlvstreamerFuerAufloesung(String aufloesung) {
        if (aufloesung.equals(AUFLOESUNG_KLEIN)) {
            return getUrlFlvstreamerKlein();
        }
        if (aufloesung.equals(AUFLOESUNG_HD)) {
            return getUrlFlvstreamerHd();
        }
        return getUrlFlvstreamer();
    }

    public String getIndex() {
        // liefert einen eindeutigen Index für die Filmliste (update der Filmliste mit Diff-Liste)
        // URL beim KiKa und ORF ändern sich laufend!
        return (arr[FILM_SENDER] + arr[FILM_THEMA]).toLowerCase() + getUrlForHash();
    }


    public String getUrlForHash() {
        // liefert die URL zum VERGLEICHEN!!
        String url = "";
        if (arr[FilmXml.FILM_SENDER].equals(Const.ORF)) {
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
            return Const.ORF + "----" + url;
        } else {
            return arr[FilmXml.FILM_URL];
        }

    }

    private void preserveMemory() {
        // ================================
        // Speicher sparen
        if (arr[FilmXml.FILM_GROESSE].length() < 3) { //todo brauchts das überhaupt??
            arr[FilmXml.FILM_GROESSE] = arr[FilmXml.FILM_GROESSE].intern();
        }
        if (arr[FilmXml.FILM_URL_KLEIN].length() < 15) {
            arr[FilmXml.FILM_URL_KLEIN] = arr[FilmXml.FILM_URL_KLEIN].intern();
        }

        arr[FilmXml.FILM_DATUM] = arr[FilmXml.FILM_DATUM].intern();
        arr[FilmXml.FILM_ZEIT] = arr[FilmXml.FILM_ZEIT].intern();
    }

    private String fuellen(int anz, String s) {
        while (s.length() < anz) {
            s = '0' + s;
        }
        return s;
    }

    private void setFilmdauer() {
        try {
            if (!arr[FilmXml.FILM_DAUER].contains(":") && !arr[FilmXml.FILM_DAUER].isEmpty()) {
                // nur als Übergang bis die Liste umgestellt ist
                long l = Long.parseLong(arr[FilmXml.FILM_DAUER]);
                dauerL = l;
                if (l > 0) {
                    final long hours = l / 3600;
                    l = l - (hours * 3600);
                    final long min = l / 60;
                    l = l - (min * 60);
                    final long seconds = l;
                    arr[FilmXml.FILM_DAUER] = fuellen(2, String.valueOf(hours)) + ':'
                            + fuellen(2, String.valueOf(min))
                            + ':'
                            + fuellen(2, String.valueOf(seconds));
                } else {
                    arr[FilmXml.FILM_DAUER] = "";
                }
            } else {
                dauerL = 0;
                if (!arr[FilmXml.FILM_DAUER].isEmpty()) {
                    final String[] parts = arr[FilmXml.FILM_DAUER].split(":");
                    long power = 1;
                    for (int i = parts.length - 1; i >= 0; i--) {
                        dauerL += Long.parseLong(parts[i]) * power;
                        power *= 60;
                    }
                }
            }
        } catch (final Exception ex) {
            dauerL = 0;
            PLog.errorLog(468912049, "Dauer: " + arr[FilmXml.FILM_DAUER]);
        }
    }

    private void setDatum() {
        if (!arr[FilmXml.FILM_DATUM].isEmpty()) {
            // nur dann gibts ein Datum
            try {
                if (arr[FilmXml.FILM_DATUM_LONG].isEmpty()) {
                    if (arr[FilmXml.FILM_ZEIT].isEmpty()) {
                        filmDate = new FilmDate(sdf_datum.parse(arr[FilmXml.FILM_DATUM]).getTime());
                    } else {
                        filmDate = new FilmDate(sdf_datum_zeit.parse(arr[FilmXml.FILM_DATUM] + arr[FilmXml.FILM_ZEIT]).getTime());
                    }
                    arr[FILM_DATUM_LONG] = String.valueOf(filmDate.getTime() / 1000);
                } else {
                    final long l = Long.parseLong(arr[FilmXml.FILM_DATUM_LONG]);
                    filmDate = new FilmDate(l * 1000 /* sind SEKUNDEN!! */);
                }
            } catch (final Exception ex) {
                PLog.errorLog(915236701, ex, new String[]{"Datum: " + arr[FilmXml.FILM_DATUM], "Zeit: " + arr[FilmXml.FILM_ZEIT]});
                filmDate = new FilmDate(0);
                arr[FilmXml.FILM_DATUM] = "";
                arr[FilmXml.FILM_ZEIT] = "";
            }
        }
    }


    private String getUrlNormalKlein() {
        // liefert die kleine normale URL
        if (!arr[FilmXml.FILM_URL_KLEIN].isEmpty()) {
            try {
                final int i = Integer.parseInt(arr[FilmXml.FILM_URL_KLEIN].substring(0, arr[FilmXml.FILM_URL_KLEIN].indexOf('|')));
                return arr[FilmXml.FILM_URL].substring(0, i)
                        + arr[FilmXml.FILM_URL_KLEIN].substring(arr[FilmXml.FILM_URL_KLEIN].indexOf('|') + 1);
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
        } else if (arr[FilmXml.FILM_URL].startsWith(Const.RTMP_PRTOKOLL)) {
            ret = Const.RTMP_FLVSTREAMER + arr[FilmXml.FILM_URL];
        } else {
            ret = arr[FilmXml.FILM_URL];
        }
        return ret;
    }

    private String getUrlFlvstreamerKlein() {
        // liefert die kleine flvstreamer URL
        String ret;
        if (!arr[FilmXml.FILM_URL_RTMP_KLEIN].isEmpty()) {
            // es gibt eine kleine RTMP
            try {
                final int i =
                        Integer.parseInt(arr[FilmXml.FILM_URL_RTMP_KLEIN].substring(0, arr[FilmXml.FILM_URL_RTMP_KLEIN].indexOf('|')));
                return arr[FilmXml.FILM_URL_RTMP].substring(0, i)
                        + arr[FilmXml.FILM_URL_RTMP_KLEIN].substring(arr[FilmXml.FILM_URL_RTMP_KLEIN].indexOf('|') + 1);
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
            if (ret.startsWith(Const.RTMP_PRTOKOLL)) {
                ret = Const.RTMP_FLVSTREAMER + ret;
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
