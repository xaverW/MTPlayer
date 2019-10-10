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

import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.controller.starter.Start;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.tools.PSystemUtils;
import de.p2tools.p2Lib.tools.file.PFileUtils;
import de.p2tools.p2Lib.tools.net.PUrlTools;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.util.Date;

public final class Download extends DownloadProps {

    private Start start = new Start(this);
    private final DownloadProgram downloadProgram = new DownloadProgram(this);

    private Film film = null;
    private SetData setData = null;
    private Abo abo = null;

    private XYChart.Series<Number, Number> cSeries = null;

    public Download() {
    }

    public Download(SetData setData,
                    Film film,
                    String source,
                    Abo abo,
                    String name,
                    String path,
                    String resolution) {

        setFilm(film);
        setSetData(setData);
        setAbo(abo);
        setSource(source);

        if (resolution.isEmpty()) {
            setUrl(film.getUrlForResolution(abo != null ? abo.getResolution() : setData.getResolution()));
            setUrlRtmp(film.getUrlFlvstreamerForResolution(abo != null ? abo.getResolution() : setData.getResolution()));
        } else {
            setUrl(film.getUrlForResolution(resolution));
            setUrlRtmp(film.getUrlFlvstreamerForResolution(resolution));
        }

        // und jetzt noch die Dateigröße für die entsp. URL
        setSizeDownloadFromFilm();
        // und endlich Aufruf bauen :)
        downloadProgram.makeProgParameter(film, abo, name, path);
        nrProperty().addListener((observable, oldValue, newValue) -> {
            if (cSeries == null) {
                return;
            }
            if (getNr() < DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED) {
                Platform.runLater(() -> cSeries.setName(getNr() + ""));
            } else {
                // Download ohne Nummer, dann auch im Chart löschen
                Platform.runLater(() -> cSeries.setName(" "));
            }
        });
    }


    //==============================================
    // Downloadstatus
    //==============================================

    public boolean isStateInit() {
        return getState() == DownloadConstants.STATE_INIT;
    }

    public boolean isStateStoped() {
        return getState() == DownloadConstants.STATE_STOPPED;
    }

    public boolean isStateStartedWaiting() {
        return getState() == DownloadConstants.STATE_STARTED_WAITING;
    }

    public boolean isStateStartedRun() {
        return getState() == DownloadConstants.STATE_STARTED_RUN;
    }

    public boolean isStateFinished() {
        return getState() == DownloadConstants.STATE_FINISHED;
    }

    public boolean isStateError() {
        return getState() == DownloadConstants.STATE_ERROR;
    }

    public void setStateStartedWaiting() {
        setState(DownloadConstants.STATE_STARTED_WAITING);
//        MLProperty.setProperty(stateProperty(), DownloadConstants.STATE_STARTED_WAITING);
    }

    public void setStateStartedRun() {
        setState(DownloadConstants.STATE_STARTED_RUN);
//        MLProperty.setProperty(stateProperty(), DownloadConstants.STATE_STARTED_RUN);
    }

    public void setStateFinished() {
        setState(DownloadConstants.STATE_FINISHED);
//        MLProperty.setProperty(stateProperty(), DownloadConstants.STATE_FINISHED);
    }

    public void setStateError() {
        setState(DownloadConstants.STATE_ERROR);
//        MLProperty.setProperty(stateProperty(), DownloadConstants.STATE_ERROR);
    }

    //=======================================

    public boolean isStarted() {
        return getState() > DownloadConstants.STATE_STOPPED && !isStateFinished();
    }

    public boolean isFinishedOrError() {
        return getState() >= DownloadConstants.STATE_FINISHED;
    }

    public XYChart.Series<Number, Number> getCSeries() {
        return cSeries;
    }

    public void setCSeries(XYChart.Series<Number, Number> cSeries) {
        this.cSeries = cSeries;
    }

    //==============================================
    //==============================================

    public boolean isAbo() {
        return !getAboName().isEmpty();
    }

    public void initStartDownload() {
        getStart().setRestartCounter(0);
        getStart().setBandwidth(0);
        setStateStartedWaiting();
    }

    public void putBack() {
        // download resetten, und als "zurückgestelt" markieren
        setPlacedBack(true);
        resetDownload();
    }

    // todo: reset, restart, stop????
    public void resetDownload() {
        // stoppen und alles zurücksetzen
        stopDownload();
        setState(DownloadConstants.STATE_INIT);
//        MLProperty.setProperty(stateProperty(), DownloadConstants.STATE_INIT);
    }

    public void restartDownload() {
        // stoppen und alles zurücksetzen

        final DownloadSize downSize = getDownloadSize();
        downSize.reset();
        setRemaining("");
        setBandwidth("");
        getStart().setBandwidth(0);
        setCSeries(null);
        setNr(DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED);

        setState(DownloadConstants.STATE_INIT);
        setProgress(DownloadConstants.PROGRESS_NOT_STARTED);

//        MLProperty.setProperty(stateProperty(), DownloadConstants.STATE_INIT);
//        MLProperty.setProperty(progressProperty(), DownloadConstants.PROGRESS_NOT_STARTED);
    }

    public void stopDownload() {
        if (isStateError()) {
            // damit fehlerhafte nicht wieder starten
            getStart().setRestartCounter(ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getInt());
        } else {
            setState(DownloadConstants.STATE_STOPPED);
            setProgress(DownloadConstants.PROGRESS_NOT_STARTED);

//            MLProperty.setProperty(stateProperty(), DownloadConstants.STATE_STOPPED);
//            MLProperty.setProperty(progressProperty(), DownloadConstants.PROGRESS_NOT_STARTED);
        }

        final DownloadSize downSize = getDownloadSize();
        downSize.reset();
        setRemaining("");
        setBandwidth("");
        getStart().setBandwidth(0);
        setCSeries(null);
        setNr(DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED);
    }

    public void makeProgParameter() {
        downloadProgram.makeProgParameter(film, abo, getDestFileName(), getDestPath());
    }

    public String getFileNameWithoutSuffix() {
        return PUrlTools.getFileNameWithoutSuffix(getDestPathFile());
    }


    public String getFileNameSuffix() {
        return PFileUtils.getFileNameSuffix(getDestPathFile());
    }

    public void setSizeDownloadFromWeb(String size) {
        if (!size.isEmpty()) {
            getDownloadSize().setSize(size);
        } else if (film != null) {
            getDownloadSize().setSize(FilmTools.getSizeFromWeb(film, getUrl()));
        }
    }

    public void setSizeDownloadFromFilm() {
        if (film != null) {
            if (film.arr[Film.FILM_URL].equals(getUrl())) {
                getDownloadSize().setSize(film.arr[Film.FILM_SIZE]);
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
            setFilmNr(DownloadConstants.FILM_NUMBER_NOT_FOUND);
            return;
        }

        this.film = film;
        setFilmNr(film.getNr());
        setChannel(film.arr[FilmXml.FILM_CHANNEL]);
        setTheme(film.arr[FilmXml.FILM_THEME]);
        setTitle(film.arr[FilmXml.FILM_TITLE]);
        setFilmUrl(film.arr[FilmXml.FILM_URL]);
        setUrlSubtitle(film.getUrlSubtitle());

        setFilmDate(film.arr[FilmXml.FILM_DATE], film.arr[FilmXml.FILM_TIME]);
        setTime(film.arr[FilmXml.FILM_TIME]);
        setDurationMinute(film.getDurationMinute());

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

    public SetData getSetData() {
        return setData;
    }

    public void setSetData(SetData setData) {
        this.setData = setData;

        setInfoFile(setData.isInfoFile());
        setSubtitle(setData.isSubtitle());
        setSetDataId(setData.getId());
    }

    public void setPathName(String path, String name) {
        // setzt den neuen Namen/Pfad und kontrolliert nochmal

        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }

        //=====================================================
        // zur Sicherheit
        if (path.isEmpty()) {
            path = PSystemUtils.getStandardDownloadPath();
        }
        if (name.isEmpty()) {
            name = StringFormatters.FORMATTER_yyyyMMdd.format(new Date()) + '_' + getTheme() + '-' + getTitle() + ".mp4";
        }
        final String[] pathName = {path, name};
        PFileUtils.checkLengthPath(pathName);
        if (!pathName[0].equals(path) || !pathName[1].equals(name)) {
            Platform.runLater(() ->
                    new PAlert().showInfoAlert("Pfad zu lang!", "Pfad zu lang!",
                            "Dateiname war zu lang und wurde gekürzt!")
            );
            path = pathName[0];
            name = pathName[1];
        }

        //=====================================================
        setDestFileName(name);
        setDestPath(path);
        setDestPathFile(PFileUtils.addsPath(path, name));
    }

    public Download getCopy() {
        final Download ret = new Download();
        for (int i = 0; i < properties.length; ++i) {
            ret.properties[i].setValue(this.properties[i].getValue());
        }
        ret.setXmlFromProps();

        ret.film = film;
        ret.setStart(getStart());
        ret.setData = setData;
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
        setData = download.setData;
        abo = download.abo;
    }
}
