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

package de.mtplayer.mtp.controller.data.download;

import de.mtplayer.mLib.tools.FileUtils;
import de.mtplayer.mLib.tools.MLProperty;
import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.controller.starter.Start;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.p2tools.p2Lib.tools.SysTools;
import javafx.application.Platform;

import java.io.File;
import java.util.Date;

public final class Download extends DownloadProps {

    private Start start = new Start(this);
    private final DownloadProg downloadProg = new DownloadProg(this);

    private Film film = null;
    private SetData pSet = null;
    private Abo abo = null;

    public Download() {
    }

    public Download(SetData pSet,
                    Film film,
                    String quelle,
                    Abo abo,
                    String name,
                    String pfad,
                    String aufloesung) {

        setFilm(film);
        setPset(pSet);
        setAbo(abo);
        setSource(quelle);

        if (aufloesung.isEmpty()) {
            setUrl(film.getUrlFuerAufloesung(abo != null ? abo.getResolution() : pSet.getResolution()));
            setUrlRtmp(film.getUrlFlvstreamerFuerAufloesung(abo != null ? abo.getResolution() : pSet.getResolution()));
        } else {
            setUrl(film.getUrlFuerAufloesung(aufloesung));
            setUrlRtmp(film.getUrlFlvstreamerFuerAufloesung(aufloesung));
        }

        // und jetzt noch die Dateigröße für die entsp. URL
        setSizeDownloadFromFilm();
        // und endlich Aufruf bauen :)
        downloadProg.aufrufBauen(pSet, film, abo, name, pfad);
    }


    //==============================================
    // Downloadstatus
    //==============================================

    public boolean isStateInit() {
        return getState() == DownloadInfos.STATE_INIT;
    }

    public boolean isStateStoped() {
        return getState() == DownloadInfos.STATE_STOPED;
    }

    public boolean isStateStartedWaiting() {
        return getState() == DownloadInfos.STATE_STARTED_WAITING;
    }

    public boolean isStateStartedRun() {
        return getState() == DownloadInfos.STATE_STARTED_RUN;
    }

    public boolean isStateFinished() {
        return getState() == DownloadInfos.STATE_FINISHED;
    }

    public boolean isStateError() {
        return getState() == DownloadInfos.STATE_ERROR;
    }

    public void setStateStartedWaiting() {
        MLProperty.setProperty(stateProperty(), DownloadInfos.STATE_STARTED_WAITING);
    }

    public void setStateStartedRun() {
        MLProperty.setProperty(stateProperty(), DownloadInfos.STATE_STARTED_RUN);
    }

    public void setStateFinished() {
        MLProperty.setProperty(stateProperty(), DownloadInfos.STATE_FINISHED);
    }

    public void setStateError() {
        MLProperty.setProperty(stateProperty(), DownloadInfos.STATE_ERROR);
    }

    //=======================================

    public boolean isStarted() {
        return getState() > DownloadInfos.STATE_STOPED && !isStateFinished();
    }

    public boolean isFinishedOrError() {
        return getState() >= DownloadInfos.STATE_FINISHED;
    }


    //==============================================
    //==============================================

    public boolean isAbo() {
        return !getAboName().isEmpty();
    }

    public void initStartDownload() {
        getStart().setRestartCounter(0);
        setStateStartedWaiting();
    }

    public void putBack() {
        // download resetten, und als "zurückgestelt" markieren
        setZurueckgestellt(true);
        resetDownload();
    }

    public void resetDownload() {
        // stoppen und alles zurücksetzen
        stopDownload();
        MLProperty.setProperty(stateProperty(), DownloadInfos.STATE_INIT);
    }

    public void restartDownload() {
        // stoppen und alles zurücksetzen

        final DownloadSize downSize = getDownloadSize();
        downSize.reset();
        setRestzeit("");
        setBandbreite("");
        setNr(DownloadInfos.DOWNLOAD_NUMBER_NOT_STARTED);

        MLProperty.setProperty(stateProperty(), DownloadInfos.STATE_INIT);
        MLProperty.setProperty(progressProperty(), DownloadInfos.PROGRESS_NICHT_GESTARTET);
    }

    public void stopDownload() {
        if (isStateError()) {
            // damit fehlerhafte nicht wieder starten
            getStart().setRestartCounter(ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getInt());
        } else {
            MLProperty.setProperty(stateProperty(), DownloadInfos.STATE_STOPED);
            MLProperty.setProperty(progressProperty(), DownloadInfos.PROGRESS_NICHT_GESTARTET);
        }

        final DownloadSize downSize = getDownloadSize();
        downSize.reset();
        setRestzeit("");
        setBandbreite("");
        setNr(DownloadInfos.DOWNLOAD_NUMBER_NOT_STARTED);
    }

    public void aufrufBauen() {
        downloadProg.aufrufBauen(pSet, film, abo, getZielDateiname(), getZielPfad());
    }

    public String getFileNameWithoutSuffix() {
        return FileUtils.getFileNameWithoutSuffix(getZielPfadDatei());
    }


    public String getFileNameSuffix() {
        return FileUtils.getFileNameSuffix(getZielPfadDatei());
    }

    public void setSizeDownloadFromWeb(String groesse) {
        if (!groesse.isEmpty()) {
            getDownloadSize().setSize(groesse);
        } else if (film != null) {
            getDownloadSize().setSize(FilmTools.getSizeFromWeb(film, getUrl()));
        }
    }

    public void setSizeDownloadFromFilm() {
        if (film != null) {
            if (film.arr[Film.FILM_URL].equals(getUrl())) {
                getDownloadSize().setSize(film.arr[Film.FILM_GROESSE]);
            } else {
                getDownloadSize().setSize("");
            }
        }
    }

    //==============================================
    // Get/Set
    //==============================================
    public Start getStart() {
        return start;
    }

    public void setStart(Start start) {
        this.start = start;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        if (film == null) {
            // bei gespeicherten Downloads kann es den Film nicht mehr geben
            setFilmNr(DownloadInfos.FILM_NUMBER_NOT_FOUND);
            return;
        }
        this.film = film;
        setFilmNr(film.getNr());
        setSender(film.arr[FilmXml.FILM_SENDER]);
        setThema(film.arr[FilmXml.FILM_THEMA]);
        setTitel(film.arr[FilmXml.FILM_TITEL]);
        setFilmUrl(film.arr[FilmXml.FILM_URL]);
        setUrlSubtitle(film.getUrlSubtitle());

        setFilmDate(film.arr[FilmXml.FILM_DATUM], film.arr[FilmXml.FILM_ZEIT]);
        setZeit(film.arr[FilmXml.FILM_ZEIT]);
        setDauer(film.arr[FilmXml.FILM_DAUER]);

        setUrlRtmp(film.arr[FilmXml.FILM_URL_RTMP]);
        setHd(film.isHd());
        setUt(film.isUt());
        setHistoryUrl(film.getUrlHistory());
        setGeoBlocked(film.isGeoBlocked());
    }

    public Abo getAbo() {
        return abo;
    }

    public void setAbo(Abo abo) {
        this.abo = abo;
        if (abo != null) {
            setAboName(abo.getName());
        }
    }

    public SetData getpSet() {
        return pSet;
    }

    public void setPset(SetData pSet) {
        this.pSet = pSet;

        setInfodatei(pSet.isInfoFile());
        setSubtitle(pSet.isSubtitle());

        setSet(pSet.getName());
    }

    public void setPathName(String path, String name) {
        // setzt den neuen Namen/Pfad und kontrolliert nochmal

        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }

        //=====================================================
        // zur Sicherheit
        if (path.isEmpty()) {
            path = SysTools.getStandardDownloadPath();
        }
        if (name.isEmpty()) {
            name = StringFormatters.FORMATTER_yyyyMMdd.format(new Date()) + '_' + getThema() + '-' + getTitel() + ".mp4";
        }
        final String[] pathName = {path, name};
        FileUtils.checkLengthPath(pathName);
        if (!pathName[0].equals(path) || !pathName[1].equals(name)) {
            Platform.runLater(() ->
                    new MTAlert().showInfoAlert("Pfad zu lang!", "Pfad zu lang!",
                            "Dateiname war zu lang und wurde gekürzt!")
            );
            path = pathName[0];
            name = pathName[1];
        }
        //=====================================================

        setZielDateiname(name);
        setZielPfad(path);
        setZielPfadDatei(FileUtils.addsPfad(path, name));
    }

    public Download getCopy() {
        final Download ret = new Download();
        for (int i = 0; i < properties.length; ++i) {
            ret.properties[i].setValue(this.properties[i].getValue());
        }
        ret.setXmlFromProps();

        ret.film = film;

        ret.setStart(getStart());
        ret.pSet = pSet;
        ret.abo = abo;

        return ret;
    }

    public void copyToMe(Download download) {
        for (int i = 0; i < properties.length; ++i) {
            properties[i].setValue(download.properties[i].getValue());
        }
        setXmlFromProps();

        film = download.film;
        getDownloadSize().setSize(download.getDownloadSize().getFilmSize());// die Auflösung des Films kann sich ändern

        setStart(download.getStart());
        pSet = download.pSet;
        abo = download.abo;
    }
}
