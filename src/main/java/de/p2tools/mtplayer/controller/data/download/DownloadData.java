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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.starter.StartDownloadDto;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.file.P2FileUtils;
import de.p2tools.p2lib.tools.net.PUrlTools;
import javafx.application.Platform;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public final class DownloadData extends DownloadDataProps {

    private StartDownloadDto downloadStartDto = new StartDownloadDto(this);
    private FilmDataMTP film = null;
    private SetData setData = null;
    private AboData abo = null;
    private String errorMessage = "";

    public DownloadData() {
    }

    public DownloadData(List<FilmDataMTP> filmList, SetData setData) {
        // das ist ein Download der über den Button/Menü "Abspielen" gestartet wurde
        // und der wird nicht in die DownloadListe einsortiert, muss also sofort gestartet werden

        String resolution;
        if (ProgData.getInstance().filmFilterWorker.getActFilterSettings().isOnlyHd()) {
            resolution = FilmDataMTP.RESOLUTION_HD;
        } else {
            resolution = setData.getResolution();
        }
        setFilm(filmList.get(0));
        setSetData(setData, true);
        setSource(DownloadConstants.SRC_BUTTON);

        setUrlSubtitle(film.getUrlSubtitle());
        setInfoFile(setData.isInfoFile());
        setSubtitle(setData.isSubtitle());

        initResolution(resolution);
        if (filmList.size() > 1) {
            // dass müssen die URLs aller Filme gesetzt werden, dass alle drin sind
            getUrlList().clear();
            for (FilmDataMTP filmDataMTP : filmList) {
                getUrlList().add(filmDataMTP.getUrlForResolution(resolution));
            }
        }

        DownloadFactoryMakeParameter.makeProgParameter(this, null, "", "");
    }

    public DownloadData(String source, SetData setData, FilmDataMTP film, AboData abo,
                        String name, String path, String resolution, boolean setSize) {
        // da sind "normale" Downloads (Abos, manuell), die auch in der DownloadListe/Tabelle erscheinen
        if (resolution.isEmpty()) {
            resolution = abo != null ? abo.getResolution() : setData.getResolution();
        }

        setFilm(film);
        setSetData(setData, true);
        setAbo(abo);
        setSource(source);
        if (abo != null && !abo.getStartTime().isEmpty()) {
            setStartTime(abo.getStartTime());
        }
        if (setSize) {
            DownloadDataFactory.setDownloadSize(this);
        }
        initResolution(resolution);

        // und endlich Aufruf bauen :)
        DownloadFactoryMakeParameter.makeProgParameter(this, abo, name, path);
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

    public void setStateError(String error) {
        if (!error.isEmpty()) {
            getDownloadStartDto().addErrMsg(error);
        }
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

    public void initResolution() {
        initResolution(super.getResolution());
    }

    public void initResolution(String resolution) {
        if (film != null) {
            setHd(film.isHd());
            setSmall(film.isSmall());
            switch (resolution) {
                case FilmDataMTP.RESOLUTION_HD:
                    if (!isHd()) {
                        resolution = FilmDataMTP.RESOLUTION_NORMAL;
                    }
                    break;

                case FilmDataMTP.RESOLUTION_SMALL:
                    if (!isSmall()) {
                        resolution = FilmDataMTP.RESOLUTION_NORMAL;
                    }
                    break;
            }

            super.setResolution(resolution);
            if (isHd()) {
                setFilmUrlHd(film.getUrlForResolution(FilmData.RESOLUTION_HD));
            } else {
                setFilmUrlHd("");
            }

            if (isSmall()) {
                setFilmUrlSmall(film.getUrlForResolution(FilmData.RESOLUTION_SMALL));
            } else {
                setFilmUrlSmall("");
            }

            setFilmUrlNormal(film.getUrlForResolution(FilmData.RESOLUTION_NORMAL));
            setFilmSizeNormal(film.getFilmSize().toString());

        } else {
            // dann gibts keinen Film mehr dazu
            resolution = FilmDataMTP.RESOLUTION_NORMAL;
            super.setResolution(resolution);
            setHd(false);
            setSmall(false);
        }

        switch (resolution) {
            case FilmDataMTP.RESOLUTION_HD:
                setUrl(getFilmUrlHd());
                getDownloadSize().setTargetSize(getFilmSizeHd());
                break;

            case FilmDataMTP.RESOLUTION_SMALL:
                setUrl(getFilmUrlSmall());
                getDownloadSize().setTargetSize(getFilmSizeSmall());
                break;

            case FilmDataMTP.RESOLUTION_NORMAL:
            default:
                setUrl(getFilmUrlNormal());
                getDownloadSize().setTargetSize(getFilmSizeNormal());
                break;
        }
    }

    public void setResolution() {
        setResolution(super.getResolution());
    }

    public void setResolution(String resolution) {
        super.setResolution(resolution);
        switch (resolution) {
            case FilmDataMTP.RESOLUTION_HD:
                setUrl(getFilmUrlHd());
                getDownloadSize().setTargetSize(getFilmSizeHd());
                break;

            case FilmDataMTP.RESOLUTION_SMALL:
                setUrl(getFilmUrlSmall());
                getDownloadSize().setTargetSize(getFilmSizeSmall());
                break;
            case FilmDataMTP.RESOLUTION_NORMAL:
            default:
                setUrl(getFilmUrlNormal());
                getDownloadSize().setTargetSize(getFilmSizeNormal());
        }
    }

    public String getUrlForResolution(String resolution) {
        if (resolution.equals(FilmData.RESOLUTION_HD)) {
            return getFilmUrlHd().isEmpty() ? getFilmUrlNormal() : getFilmUrlHd();
        }
        if (resolution.equals(FilmData.RESOLUTION_SMALL)) {
            return getFilmUrlSmall().isEmpty() ? getFilmUrlNormal() : getFilmUrlSmall();
        }
        return getFilmUrlNormal();
    }

    public void initStartDownload() {
        // Download zum Start vorbereiten
        getDownloadStartDto().setDeleteAfterStop(false);
        getDownloadStartDto().setStartCounter(0);
        setBandwidth(0);
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
        stopDownload(false);
        setProgress(DownloadConstants.PROGRESS_NOT_STARTED); // damit auch fehlerhafte zurückgesetzt werden
        setState(DownloadConstants.STATE_INIT);
    }

    public void stopDownload(boolean deleteAfterStop) {
        getDownloadStartDto().setDeleteAfterStop(deleteAfterStop);
        stopDownload();
    }

    public void stopDownload() {
        if (!isStateError()) {
            setProgress(DownloadConstants.PROGRESS_NOT_STARTED);
            setState(DownloadConstants.STATE_STOPPED);
        }

        getDownloadSize().resetActFileSize();
        setRemaining(DownloadConstants.REMAINING_NOT_STARTET);
        setBandwidth(0);
        setNo(P2LibConst.NUMBER_NOT_STARTED);
    }

    public void makeProgParameter() {
        DownloadFactoryMakeParameter.makeProgParameter(this, abo, getDestFileName(), getDestPath());
    }

    //==============================================
    // Get/Set
    //==============================================
    public StartDownloadDto getDownloadStartDto() {
        return downloadStartDto;
    }

    public String getFileNameWithoutSuffix() {
        return PUrlTools.getFileNameWithoutSuffix(getDestFileName());
    }

    public String getPathFileNameWithoutSuffix() {
        return PUrlTools.getFileNameWithoutSuffix(getDestPathFile());
    }

    public String getFileNameSuffix() {
        return P2FileUtils.getFileNameSuffix(getDestPathFile());
    }

    public void setDownloadStartDto(StartDownloadDto startDownloadDto) {
        this.downloadStartDto = startDownloadDto;
    }

    public FilmDataMTP getFilm() {
        return film;
    }

    public void setFilm(FilmDataMTP film) {
        // url muss VORHER eingetragen sein, wegen der Dateigröße
        if (film == null) {
            // bei gespeicherten Downloads kann es den Film nicht mehr geben
            setFilmNo(P2LibConst.NUMBER_NOT_STARTED);
            return;
        }

        this.film = film;
        setFilmNo(film.getNo());
        setChannel(film.arr[FilmDataXml.FILM_CHANNEL]);
        setTheme(film.arr[FilmDataXml.FILM_THEME]);
        setTitle(film.arr[FilmDataXml.FILM_TITLE]);
        setDescription(film.arr[FilmDataXml.FILM_DESCRIPTION]);

//        setFilmSizeNormal(film.arr[FilmDataXml.FILM_SIZE]);
//        if (getUrl().equals(film.getUrlForResolution(FilmData.RESOLUTION_HD))) {
//            getDownloadSize().setTargetSize(getFilmSizeHd());
//        } else if (getUrl().equals(film.getUrlForResolution(FilmData.RESOLUTION_SMALL))) {
//            getDownloadSize().setTargetSize(getFilmSizeSmall());
//        } else {
//            getDownloadSize().setTargetSize(getFilmSizeNormal());
//        }

        setFilmUrlNormal(film.arr[FilmDataXml.FILM_URL]);
        setFilmUrlHd(film.isHd() ? film.getUrlForResolution(FilmData.RESOLUTION_HD) : "");
        setFilmUrlSmall(film.isSmall() ? film.getUrlForResolution(FilmData.RESOLUTION_SMALL) : "");
        setHd(film.isHd());
        setSmall(film.isSmall());

        setUrlWebsite(film.arr[FilmDataXml.FILM_WEBSITE]);
        setUrlSubtitle(film.getUrlSubtitle());
        setUt(film.isUt());

        setFilmDate(film.arr[FilmDataXml.FILM_DATE], film.arr[FilmDataXml.FILM_TIME]);
        setFilmDateStr(film.arr[FilmDataXml.FILM_DATE]);
        setFilmTime(film.arr[FilmDataXml.FILM_TIME]);
        setDurationMinute(film.getDurationMinute());

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

    public File getFile() {
        return downloadStartDto.getFile();
    }

    public void setFile(File file) {
        this.downloadStartDto.setFile(file);
        destFileNameProperty().setValue(file.getName());
        destPathProperty().setValue(file.getParent());
        destPathFileProperty().setValue(file.getAbsolutePath());
    }

    public void setFile(String path, String name) {
        setFile(P2FileUtils.addsPath(path, name));
    }

    public void setFile(String file) {
        downloadStartDto.setFile(Path.of(file).toFile());
        String name = downloadStartDto.getFile().getName();
        String path = downloadStartDto.getFile().getParent();
        String pathFile = downloadStartDto.getFile().getAbsolutePath();
        destFileNameProperty().setValue(name == null ? "" : name);
        destPathProperty().setValue(path == null ? "" : path);
        destPathFileProperty().setValue(pathFile == null ? "" : pathFile);
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
            name = P2DateConst.F_FORMAT_yyyyMMdd.format(new Date()) + '_' + getTheme() + '-' + getTitle() + ".mp4";
        }
        final String[] pathName = {path, name};
        P2FileUtils.checkLengthPath(pathName);
        if (!pathName[0].equals(path) || !pathName[1].equals(name)) {
            Platform.runLater(() ->
                    new PAlert().showInfoAlert("Pfad zu lang!", "Pfad zu lang!",
                            "Dateiname war zu lang und wurde gekürzt!")
            );
            path = pathName[0];
            name = pathName[1];
        }

        //=====================================================
        setFile(P2FileUtils.addsPath(path, name));
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        final String s = "Der Download hatte einen Fehler:\n\n";
        this.errorMessage = s + errorMessage;
    }

    public DownloadData getCopy() {
        final DownloadData downloadData = new DownloadData();
        for (int i = 0; i < properties.length; ++i) {
            downloadData.properties[i].setValue(this.properties[i].getValue());
        }

        downloadData.film = film;
        downloadData.setDownloadStartDto(getDownloadStartDto());
        downloadData.setData = setData;
        downloadData.abo = abo;
        downloadData.getUrlList().setAll(getUrl());
        return downloadData;
    }

    public void copyToMe(DownloadData downloadData) {
        for (int i = 0; i < properties.length; ++i) {
            properties[i].setValue(downloadData.properties[i].getValue());
        }

        film = downloadData.film;
        setDownloadStartDto(downloadData.getDownloadStartDto());
        setData = downloadData.setData;
        abo = downloadData.abo;
        getUrlList().setAll(downloadData.getUrlList());
    }
}
