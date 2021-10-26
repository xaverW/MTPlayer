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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.tools.FilmDate;
import de.p2tools.p2Lib.tools.date.PDate;
import de.p2tools.p2Lib.tools.log.PLog;

public class FilmProps extends FilmXml {

    public int no;
    FilmSize filmSize = new FilmSize(); // Dateigröße in MByte
    public FilmDate filmDate = new FilmDate(0);

    //    public long dauerL = 0; // Sekunden
    private int durationMinute = 0; //
    public int filmTime = 0; // Zeit -> Minuten ab 0:00 Uhr

    private boolean live = false; // Film ist Livestream
    private boolean small = false; // Film hat "small"-URL
    private boolean hd = false; // Film hat HD-URL
    private boolean ut = false;
    private boolean geoBlocked = false;
    private boolean inFuture = false;
    private boolean doubleUrl = false;

    // todo?? die Property brauchts nicht alle aber dann müssen die checkboxen in der Tabelle
    // ersetzt werden
    private boolean newFilm = false;
    private boolean shown = false;
    private boolean actHist = false;
    private boolean bookmark = false;
    private boolean blackBlocked = false;

    public int getDurationMinute() {
        return durationMinute;
    }

    public void setDurationMinute(int durationMinute) {
        this.durationMinute = durationMinute;
    }

    public int getFilmTime() {
        return filmTime;
    }

    public void setFilmTime(int filmTime) {
        this.filmTime = filmTime;
    }

    public boolean isGeoBlocked() {
        return geoBlocked;
    }

    public void setGeoBlocked() {
        geoBlocked = !getGeo().isEmpty() && !getGeo().contains(ProgConfig.SYSTEM_GEO_HOME_PLACE.get());
    }

    public boolean isInFuture() {
        return inFuture;
    }

    public void setInFuture(boolean inFuture) {
        this.inFuture = inFuture;
    }

    public void setInFuture() {
        // bezieht sich immer auf den Zeitpunkt, an dem die Filmliste geladen wurde
        try {
            if (filmDate.getTime() > System.currentTimeMillis()) {
                inFuture = true;
            } else {
                inFuture = false;
            }
        } catch (final Exception ex) {
            PLog.errorLog(915236478, ex);
            inFuture = false;
        }
    }

    public boolean isDoubleUrl() {
        return doubleUrl;
    }

    public void setDoubleUrl(boolean doubleUrl) {
        this.doubleUrl = doubleUrl;
    }

    public int getNo() {
        return no;
    }

    public String getChannel() {
        return arr[FILM_CHANNEL];
    }

    public String getTheme() {
        return arr[FILM_THEME];
    }

    public String getTitle() {
        return arr[FILM_TITLE];
    }

    public PDate getDate() {
        return filmDate;
    }

    public String getTime() {
        return arr[FILM_TIME];
    }

    public String getDuration() {
        return arr[FILM_DURATION];
    }

    public FilmSize getFilmSize() {
        return filmSize;
    }

    public boolean isHd() {
        return hd;
    }

    public void setHd(boolean b) {
        hd = b;
    }


    //    public BooleanProperty hdProperty() {
//        return hd;
//    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isSmall() {
        return small;
    }

    public void setSmall(boolean b) {
        small = b;
    }

    public boolean isUt() {
        return ut;
    }

    public void setUt(boolean b) {
        ut = b;
    }


    //    public BooleanProperty utProperty() {
//        return ut;
//    }

    public String getDescription() {
        return arr[FILM_DESCRIPTION];
    }

    public void setDescription(String text) {
        arr[FILM_DESCRIPTION] = text;
    }

    public String getGeo() {
        return arr[FILM_GEO];
    }

    public String getUrl() {
        return arr[FILM_URL];
    }

    public String getWebsite() {
        return arr[FILM_WEBSITE];
    }

    public String getAboName() {
        return arr[FILM_ABO_NAME];
    }

    public String getUrlSubtitle() {
        return arr[FILM_URL_SUBTITLE];
    }

    public String getUrlRtmp() {
        return arr[FILM_URL_RTMP];
    }

    public String getUrlAuth() {
        return arr[FILM_URL_AUTH];
    }

    public String getUrlHistory() {
        if (arr[FILM_URL_HISTORY].isEmpty()) {
            return arr[FILM_URL];
        } else {
            return arr[FILM_URL_HISTORY];
        }
    }

    public boolean isNewFilm() {
        return newFilm;
    }

    public void setNewFilm(final boolean newFilm) {
        this.newFilm = newFilm;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        if (!isLive()) {
            this.shown = shown;
        }
    }

    public boolean getActHist() {
        return actHist;
    }

    public void setActHist(boolean actHist) {
        this.actHist = actHist;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    public boolean isBlackBlocked() {
        return blackBlocked;
    }

    public void setBlackBlocked(boolean blackBlocked) {
        this.blackBlocked = blackBlocked;
    }

    public String getFilmDateLong() {
        // beschleunigt etwas das Laden der Filmliste
        return arr[FILM_DATE_LONG];
    }
}
