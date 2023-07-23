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

package de.p2tools.mtplayer.controller.data.download;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.starter.Start;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.mtfilm.film.FilmFactory;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.date.DateFactory;
import de.p2tools.p2lib.tools.file.PFileUtils;
import de.p2tools.p2lib.tools.net.PUrlTools;
import javafx.application.Platform;

import java.io.File;
import java.util.Date;
import java.util.List;

public final class DownloadData extends DownloadDataProps {

    private Start start = new Start(this);
    //    private FilmDataMTP film = null;
    private SetData setData = null;
    private AboData abo = null;
    private String errorMessage = "";

    public DownloadData() {
    }

    public DownloadData(List<FilmDataMTP> film, SetData setData) {
        // das ist ein Download der über den Button/Menü "Abspielen" gestartet wurde
        // und der wird nicht in die DownloadListe einsortiert, muss also sofort gestartet werden
        this.getFilmList().addAll(film);
        this.setData = setData;
        String resolution;
        if (ProgData.getInstance().actFilmFilterWorker.getActFilterSettings().isOnlyHd()) {
            resolution = FilmDataMTP.RESOLUTION_HD;
        } else {
            resolution = "";
        }
        if (resolution.isEmpty()) {
            setUrl(film.get(0).getUrlForResolution(setData.getResolution()));
        } else {
            setUrl(film.get(0).getUrlForResolution(resolution));
        }
        getFilmList().forEach(f -> {
            getUrlList().add(f.getUrlForResolution(resolution));
        });

        setUrlSubtitle(film.get(0).getUrlSubtitle());
        setInfoFile(setData.isInfoFile());
        setSubtitle(setData.isSubtitle());

        DownloadFactoryProgram.makeProgParameter(this, film.get(0), null, "", "");
    }

    public DownloadData(String source, SetData setData, FilmDataMTP film, AboData abo,
                        String name,
                        String path,
                        String resolution) {
        // da sind "normale" Downloads, die auch in der DownloadListe/Tabelle erscheinen

        addFilm(film);
        setSetData(setData, true);
        setAbo(abo);
        setSource(source);

        if (abo != null && !abo.getStartTime().isEmpty()) {
            setStartTime(abo.getStartTime());
        }

        if (resolution.isEmpty()) {
            setUrl(film.getUrlForResolution(abo != null ? abo.getResolution() : setData.getResolution()));
        } else {
            setUrl(film.getUrlForResolution(resolution));
        }

        // und jetzt noch die Dateigröße für die entsp. URL
        setSizeDownloadFromFilm();
        // und endlich Aufruf bauen :)
        DownloadFactoryProgram.makeProgParameter(this, film, abo, name, path);
    }

    //==============================================
    // Downloadstatus
    //==============================================
    public boolean isStateInit() {
        return getState() == DownloadConstants.STATE_INIT;
    }

    public boolean isStateStopped() {
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
    }

    public void setStateStartedRun() {
        setState(DownloadConstants.STATE_STARTED_RUN);
    }

    public void setStateFinished() {
        setState(DownloadConstants.STATE_FINISHED);
    }

    public void setStateError() {
        setState(DownloadConstants.STATE_ERROR);
    }

    //=======================================
    public boolean isStarted() {
        return getState() > DownloadConstants.STATE_STOPPED && !isStateFinished();
    }

    public boolean isNotStartedOrFinished() {
        return isStateInit() || isStateStopped();
    }

    public boolean isFinishedOrError() {
        return getState() >= DownloadConstants.STATE_FINISHED;
    }

    //==============================================
    //==============================================
    public boolean isAbo() {
        return !getAboName().isEmpty();
    }

    public void initStartDownload() {
        // Download zum Start vorbereiten
        getStart().setRestartCounter(0);
        getStart().setBandwidth(0);
        setStateStartedWaiting();
        setErrorMessage("");
    }

    public void putBack() {
        // Download resetten, und als "zurückgestellt" markieren
        setPlacedBack(true);
        resetDownload();
    }

    public void resetDownload() {
        // stoppen und alles zurücksetzen
        stopDownload();
        setProgress(DownloadConstants.PROGRESS_NOT_STARTED); // damit auch fehlerhafte zurückgesetzt werden
        setState(DownloadConstants.STATE_INIT);
    }

    public void stopDownload() {
        if (isStateError()) {
            // damit fehlerhafte nicht wieder starten
            getStart().setRestartCounter(ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getValue());
        } else {
            setProgress(DownloadConstants.PROGRESS_NOT_STARTED);
            setState(DownloadConstants.STATE_STOPPED);
        }

        getDownloadSize().reset();
        setRemaining(DownloadConstants.REMAINING_NOT_STARTET);
        setBandwidth(0);
        getStart().setBandwidth(0);
        setNo(P2LibConst.NUMBER_NOT_STARTED);
    }

    public void makeProgParameter() {
        DownloadFactoryProgram.makeProgParameter(this, getFilm(), abo, getDestFileName(), getDestPath());
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
        } else if (getFilm() != null) {
            getDownloadSize().setSize(FilmFactory.getSizeFromWeb(getFilm(), getUrl()));
        }
    }

    public void setSizeDownloadFromFilm() {
        if (getFilm() != null) {
            if (getFilm().arr[FilmDataMTP.FILM_URL].equals(getUrl())) {
                getDownloadSize().setSize(getFilm().arr[FilmDataMTP.FILM_SIZE]);
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


    public void addFilm(FilmDataMTP film) {
        if (film == null) {
            // bei gespeicherten Downloads kann es den Film nicht mehr geben
            setFilmNr(P2LibConst.NUMBER_NOT_STARTED);
            return;
        }

        setFilm(film);
        setFilmNr(film.getNo());
        setChannel(film.arr[FilmDataXml.FILM_CHANNEL]);
        setTheme(film.arr[FilmDataXml.FILM_THEME]);
        setTitle(film.arr[FilmDataXml.FILM_TITLE]);
        setFilmUrl(film.arr[FilmDataXml.FILM_URL]);
        setUrlSubtitle(film.getUrlSubtitle());

        setFilmDate(film.arr[FilmDataXml.FILM_DATE], film.arr[FilmDataXml.FILM_TIME]);
        setTime(film.arr[FilmDataXml.FILM_TIME]);
        setDurationMinute(film.getDurationMinute());

        setHd(film.isHd());
        setUt(film.isUt());
        setHistoryUrl(film.getUrlHistory());
        setGeoBlocked(film.isGeoBlocked());
    }

    public AboData getAbo() {
        return abo;
    }

    public void setAbo(AboData abo) {
        this.abo = abo;
        if (abo != null) {
            setAboName(abo.getName());
        }
    }

    public SetData getSetData() {
        return setData;
    }

    public void setSetData(SetData setData, boolean initSetData) {
        this.setData = setData;
        setSetDataId(setData.getId());

        if (initSetData) {
            // wird auch beim Programmstart aufgerufen und da sollen die
            // manuellen Downloads ja nicht geändert werden
            setInfoFile(setData.isInfoFile());
            setSubtitle(setData.isSubtitle());
        }
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
            name = DateFactory.F_FORMAT_yyyyMMdd.format(new Date()) + '_' + getTheme() + '-' + getTitle() + ".mp4";
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        final String s = "Der Download hatte einen Fehler:\n\n";
        this.errorMessage = s + errorMessage;
    }

    public DownloadData getCopy() {
        final DownloadData ret = new DownloadData();
        for (int i = 0; i < properties.length; ++i) {
            ret.properties[i].setValue(this.properties[i].getValue());
        }

        ret.addFilm(getFilm());
        ret.setStart(getStart());
        ret.setData = setData;
        ret.abo = abo;
        ret.getFilmList().addAll(getFilmList());
        ret.getUrlList().addAll(getUrl());
        return ret;
    }

    public void copyToMe(DownloadData download) {
        for (int i = 0; i < properties.length; ++i) {
            properties[i].setValue(download.properties[i].getValue());
        }

        addFilm(download.getFilm());
        getDownloadSize().setSize(download.getDownloadSize().getSize()); // die Auflösung des Films kann sich ändern

        setStart(download.getStart());
        setData = download.setData;
        abo = download.abo;
        getFilmList().addAll(download.getFilmList());
        getUrlList().addAll(download.getUrlList());
    }
}
