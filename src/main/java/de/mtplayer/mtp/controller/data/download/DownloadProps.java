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

import de.mtplayer.mLib.tools.Data;
import de.mtplayer.mLib.tools.MDate;
import javafx.application.Platform;
import javafx.beans.property.*;

public class DownloadProps extends DownloadXml {

    private final IntegerProperty nr = new SimpleIntegerProperty(DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED);
    private final IntegerProperty filmNr = new SimpleIntegerProperty(DownloadConstants.FILM_NUMBER_NOT_FOUND);

    private final StringProperty aboName = new SimpleStringProperty("");
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");

    private final IntegerProperty state = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final IntegerProperty guiState = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final DoubleProperty progress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final DoubleProperty guiProgress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final StringProperty remaining = new SimpleStringProperty("");
    private final StringProperty bandwidth = new SimpleStringProperty("");

    private final DownloadSize downloadSize = new DownloadSize();
    private final ObjectProperty<MDate> filmDate = new SimpleObjectProperty<>(new MDate(0));

    private final StringProperty time = new SimpleStringProperty("");
    private final IntegerProperty durationMinute = new SimpleIntegerProperty(0);
    private final BooleanProperty hd = new SimpleBooleanProperty(false);
    private final BooleanProperty ut = new SimpleBooleanProperty(false);
    private final BooleanProperty geoBlocked = new SimpleBooleanProperty(false);

    private final StringProperty filmUrl = new SimpleStringProperty(""); //in normaler Auflösung
    private final StringProperty historyUrl = new SimpleStringProperty("");
    private final StringProperty url = new SimpleStringProperty(""); //in der gewählte Auflösung
    private final StringProperty urlRtmp = new SimpleStringProperty("");
    private final StringProperty urlSubtitle = new SimpleStringProperty("");

    private final StringProperty setDataId = new SimpleStringProperty("");
    private final StringProperty program = new SimpleStringProperty("");
    private final StringProperty programCall = new SimpleStringProperty("");
    private final StringProperty programCallArray = new SimpleStringProperty("");
    private final BooleanProperty programRestart = new SimpleBooleanProperty(false);
    private final BooleanProperty programDownloadmanager = new SimpleBooleanProperty(false);

    private final StringProperty destFileName = new SimpleStringProperty("");
    private final StringProperty destPath = new SimpleStringProperty("");
    private final StringProperty destPathFile = new SimpleStringProperty("");

    private final StringProperty type = new SimpleStringProperty(DownloadConstants.TYPE_DOWNLOAD);
    private final StringProperty source = new SimpleStringProperty(DownloadConstants.ALL);
    private final BooleanProperty placedBack = new SimpleBooleanProperty(false);
    private final BooleanProperty infoFile = new SimpleBooleanProperty(false);
    private final BooleanProperty subtitle = new SimpleBooleanProperty(false);

    public final Property[] properties = {nr, filmNr, aboName, channel, theme, title,
            state, progress, remaining, bandwidth, downloadSize,
            filmDate, time, durationMinute,
            hd, ut, geoBlocked, filmUrl, historyUrl, url, urlRtmp, urlSubtitle,
            setDataId, program, programCall, programCallArray, programRestart, programDownloadmanager,
            destFileName, destPath, destPathFile,
            type, source, placedBack, infoFile, subtitle};

    public MDate getFilmDate() {
        return filmDate.get();
    }

    public ObjectProperty<MDate> filmDateProperty() {
        return filmDate;
    }

    public void setFilmDate(MDate filmDate) {
        this.filmDate.set(filmDate);
    }

    public void setFilmDate(String date, String time) {
        MDate d = new MDate();
        d.setDatum(date, time);
        this.filmDate.setValue(d);
    }

    // GuiProps


    public int getGuiState() {
        return guiState.get();
    }

    public IntegerProperty guiStateProperty() {
        return guiState;
    }

    public double getGuiProgress() {
        return guiProgress.get();
    }

    public DoubleProperty guiProgressProperty() {
        return guiProgress;
    }

    public int getNr() {
        return nr.get();
    }

    public IntegerProperty nrProperty() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr.set(nr);
    }

    public int getFilmNr() {
        return filmNr.get();
    }

    public IntegerProperty filmNrProperty() {
        return filmNr;
    }

    public void setFilmNr(int filmNr) {
        this.filmNr.set(filmNr);
    }

    public String getAboName() {
        return aboName.get();
    }

    public StringProperty aboNameProperty() {
        return aboName;
    }

    public void setAboName(String aboName) {
        this.aboName.set(aboName);
    }

    public String getChannel() {
        return channel.get();
    }

    public StringProperty channelProperty() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel.set(channel);
    }

    public String getTheme() {
        return theme.get();
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public int getState() {
        return state.get();
    }

    public IntegerProperty stateProperty() {
        return state;
    }

    public void setState(int state) {
        this.state.set(state);
        Platform.runLater(() -> guiState.setValue(state));
    }

    public Double getProgress() {
        return progress.getValue();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress.setValue(progress);
        Platform.runLater(() -> guiProgress.setValue(progress));
    }

    public String getRemaining() {
        return remaining.get();
    }

    public StringProperty remainingProperty() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining.set(remaining);
    }

    public String getBandwidth() {
        return bandwidth.get();
    }

    public StringProperty bandwidthProperty() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth.set(bandwidth);
    }

    public DownloadSize getDownloadSize() {
        return downloadSize;
    }

    public DownloadSize downloadSizeProperty() {
        return downloadSize;
    }

//    public void setDownloadSize(DownloadSize downloadSize) {
//        this.downloadSize=downloadSize;
//    }


    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public int getDurationMinute() {
        return durationMinute.get();
    }

    public IntegerProperty durationMinuteProperty() {
        return durationMinute;
    }

    public void setDurationMinute(int durationMinute) {
        this.durationMinute.set(durationMinute);
    }

    public boolean isHd() {
        return hd.get();
    }

    public BooleanProperty hdProperty() {
        return hd;
    }

    public void setHd(boolean hd) {
        this.hd.set(hd);
    }

    public boolean isUt() {
        return ut.get();
    }

    public BooleanProperty utProperty() {
        return ut;
    }

    public void setUt(boolean ut) {
        this.ut.set(ut);
    }

    public boolean getGeoBlocked() {
        return geoBlocked.get();
    }

    public BooleanProperty geoBlockedProperty() {
        return geoBlocked;
    }

    public void setGeoBlocked(boolean geoBlocked) {
        this.geoBlocked.set(geoBlocked);
    }

    public String getFilmUrl() {
        return filmUrl.get();
    }

    public StringProperty filmUrlProperty() {
        return filmUrl;
    }

    public void setFilmUrl(String filmUrl) {
        this.filmUrl.set(filmUrl);
    }

    public String getHistoryUrl() {
        return historyUrl.get();
    }

    public StringProperty historyUrlProperty() {
        return historyUrl;
    }

    public void setHistoryUrl(String historyUrl) {
        this.historyUrl.set(historyUrl);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getUrlRtmp() {
        return urlRtmp.get();
    }

    public StringProperty urlRtmpProperty() {
        return urlRtmp;
    }

    public void setUrlRtmp(String urlRtmp) {
        this.urlRtmp.set(urlRtmp);
    }

    public String getUrlSubtitle() {
        return urlSubtitle.get();
    }

    public StringProperty urlSubtitleProperty() {
        return urlSubtitle;
    }

    public void setUrlSubtitle(String urlSubtitle) {
        this.urlSubtitle.set(urlSubtitle);
    }

    public String getSetDataId() {
        return setDataId.get();
    }

    public StringProperty setDataIdProperty() {
        return setDataId;
    }

    public void setSetDataId(String setDataId) {
        this.setDataId.set(setDataId);
    }

    public String getProgram() {
        return program.get();
    }

    public StringProperty programProperty() {
        return program;
    }

    public void setProgram(String program) {
        this.program.set(program);
    }

    public String getProgramCall() {
        return programCall.get();
    }

    public StringProperty programCallProperty() {
        return programCall;
    }

    public void setProgramCall(String programCall) {
        this.programCall.set(programCall);
    }

    public String getProgramCallArray() {
        return programCallArray.get();
    }

    public StringProperty programCallArrayProperty() {
        return programCallArray;
    }

    public void setProgramCallArray(String programCallArray) {
        this.programCallArray.set(programCallArray);
    }

    public boolean getProgramRestart() {
        return programRestart.get();
    }

    public BooleanProperty programRestartProperty() {
        return programRestart;
    }

    public void setProgramRestart(boolean programRestart) {
        this.programRestart.set(programRestart);
    }

    public boolean getProgramDownloadmanager() {
        return programDownloadmanager.get();
    }

    public BooleanProperty programDownloadmanagerProperty() {
        return programDownloadmanager;
    }

    public void setProgramDownloadmanager(boolean programDownloadmanager) {
        this.programDownloadmanager.set(programDownloadmanager);
    }

    public String getDestFileName() {
        return destFileName.get();
    }

    public StringProperty destFileNameProperty() {
        return destFileName;
    }

    public void setDestFileName(String destFileName) {
        this.destFileName.set(destFileName);
    }

    public String getDestPath() {
        return destPath.get();
    }

    public StringProperty destPathProperty() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath.set(destPath);
    }

    public String getDestPathFile() {
        return destPathFile.get();
    }

    public StringProperty destPathFileProperty() {
        return destPathFile;
    }

    public void setDestPathFile(String destPathFile) {
        this.destPathFile.set(destPathFile);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getSource() {
        return source.get();
    }

    public StringProperty sourceProperty() {
        return source;
    }

    public void setSource(String source) {
        this.source.set(source);
    }

    public boolean getPlacedBack() {
        return placedBack.get();
    }

    public BooleanProperty placedBackProperty() {
        return placedBack;
    }

    public void setPlacedBack(boolean placedBack) {
        this.placedBack.set(placedBack);
    }

    public boolean getInfoFile() {
        return infoFile.get();
    }

    public BooleanProperty infoFileProperty() {
        return infoFile;
    }

    public void setInfoFile(boolean infoFile) {
        this.infoFile.set(infoFile);
    }

    public boolean isSubtitle() {
        return subtitle.get();
    }

    public BooleanProperty subtitleProperty() {
        return subtitle;
    }

    public void setSubtitle(boolean subtitle) {
        this.subtitle.set(subtitle);
    }

    public void setPropsFromXml() {

        setAboName(arr[DOWNLOAD_ABO]);
        setChannel(arr[DOWNLOAD_SENDER]);
        setTheme(arr[DOWNLOAD_THEME]);
        setTitle(arr[DOWNLOAD_TITLE]);

        setFilmDate(arr[DOWNLOAD_DATE], arr[DOWNLOAD_TIME]);
        setTime(arr[DOWNLOAD_TIME]);

        int dur;
        try {
            dur = Integer.parseInt(arr[DOWNLOAD_DURATION]);
        } catch (final Exception ex) {
            dur = 0;
        }
        setDurationMinute(dur);

        setHd(Boolean.parseBoolean(arr[DOWNLOAD_HD]));
        setUt(Boolean.parseBoolean(arr[DOWNLOAD_UT]));
        setGeoBlocked(Boolean.parseBoolean(arr[DOWNLOAD_GEO]));
        setFilmUrl(arr[DOWNLOAD_FILM_URL]);
        setHistoryUrl(arr[DOWNLOAD_HISTORY_URL]);
        setUrl(arr[DOWNLOAD_URL]);
        setUrlRtmp(arr[DOWNLOAD_URL_RTMP]);
        setSubtitle(Boolean.parseBoolean(arr[DOWNLOAD_URL_SUBTITLE]));
        setSetDataId(arr[DOWNLOAD_SET_DATA]);
        setProgram(arr[DOWNLOAD_PROGRAM]);
        setProgramCall(arr[DOWNLOAD_PROGRAM_CALL]);
        setProgramCallArray(arr[DOWNLOAD_PROGRAM_CALL_ARRAY]);
        setDestFileName(arr[DOWNLOAD_DEST_FILE_NAME]);
        setDestPath(arr[DOWNLOAD_DEST_PATH]);
        setDestPathFile(arr[DOWNLOAD_DEST_PATH_FILE_NAME]);

        setType(arr[Download.DOWNLOAD_TYPE]);
        if (!arr[Download.DOWNLOAD_SOURCE].equals(DownloadConstants.SRC_ABO)) {
            // bei gelöschten Abos kanns dazu kommen
            arr[Download.DOWNLOAD_SOURCE] = DownloadConstants.SRC_DOWNLOAD;
        }
        setSource(arr[DOWNLOAD_SOURCE]);
        setPlacedBack(Boolean.parseBoolean(arr[DOWNLOAD_PLACED_BACK]));
        setInfoFile(Boolean.parseBoolean(arr[DOWNLOAD_INFO_FILE]));
        setSubtitle(Boolean.parseBoolean(arr[DOWNLOAD_SUBTITLE]));
        setProgramDownloadmanager(Boolean.parseBoolean(arr[DOWNLOAD_PROGRAM_DOWNLOADMANAGER]));
    }


    public void setXmlFromProps() {
        arr[DOWNLOAD_ABO] = getAboName();
        arr[DOWNLOAD_SENDER] = getChannel();
        arr[DOWNLOAD_THEME] = getTheme();
        arr[DOWNLOAD_TITLE] = getTitle();
        arr[DOWNLOAD_DATE] = getFilmDate().toString();
        arr[DOWNLOAD_TIME] = getTime();
        arr[DOWNLOAD_DURATION] = String.valueOf(getDurationMinute());
        arr[DOWNLOAD_HD] = String.valueOf(isHd());
        arr[DOWNLOAD_UT] = String.valueOf(isUt());
        arr[DOWNLOAD_GEO] = String.valueOf(getGeoBlocked());
        arr[DOWNLOAD_FILM_URL] = getFilmUrl();
        arr[DOWNLOAD_HISTORY_URL] = getHistoryUrl();
        arr[DOWNLOAD_URL] = getUrl();
        arr[DOWNLOAD_URL_RTMP] = getUrlRtmp();
        arr[DOWNLOAD_URL_SUBTITLE] = getUrlSubtitle();
        arr[DOWNLOAD_SET_DATA] = getSetDataId();
        arr[DOWNLOAD_PROGRAM] = getProgram();
        arr[DOWNLOAD_PROGRAM_CALL] = getProgramCall();
        arr[DOWNLOAD_PROGRAM_CALL_ARRAY] = getProgramCallArray();
        arr[DOWNLOAD_DEST_FILE_NAME] = getDestFileName();
        arr[DOWNLOAD_DEST_PATH] = getDestPath();
        arr[DOWNLOAD_DEST_PATH_FILE_NAME] = getDestPathFile();
        arr[DOWNLOAD_TYPE] = getType();
        arr[DOWNLOAD_SOURCE] = getSource();
        arr[DOWNLOAD_PLACED_BACK] = String.valueOf(getPlacedBack());
        arr[DOWNLOAD_INFO_FILE] = String.valueOf(getInfoFile());
        arr[DOWNLOAD_SUBTITLE] = String.valueOf(isSubtitle());
        arr[DOWNLOAD_PROGRAM_DOWNLOADMANAGER] = String.valueOf(getProgramDownloadmanager());
    }


    public int compareTo(DownloadProps arg0) {
        return Data.sorter.compare(getChannel(), arg0.getChannel());
    }

}
