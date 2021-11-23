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

import de.p2tools.mtplayer.tools.Data;
import de.p2tools.p2Lib.configFile.config.*;
import de.p2tools.p2Lib.configFile.pData.PDataSample;
import de.p2tools.p2Lib.tools.date.PDate;
import de.p2tools.p2Lib.tools.date.PDateFactory;
import de.p2tools.p2Lib.tools.date.PDateProperty;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.ArrayList;

public class DownloadDataProps extends PDataSample<DownloadData> {

    private final IntegerProperty no = new SimpleIntegerProperty(DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED);
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
    private final PDateProperty filmDate = new PDateProperty(new PDate(0));

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
    private final StringProperty startTime = new SimpleStringProperty("");

    private final StringProperty destFileName = new SimpleStringProperty("");
    private final StringProperty destPath = new SimpleStringProperty("");
    private final StringProperty destPathFile = new SimpleStringProperty("");

    private final StringProperty type = new SimpleStringProperty(DownloadConstants.TYPE_DOWNLOAD);
    private final StringProperty source = new SimpleStringProperty(DownloadConstants.ALL);
    private final BooleanProperty placedBack = new SimpleBooleanProperty(false);
    private final BooleanProperty infoFile = new SimpleBooleanProperty(false);
    private final BooleanProperty subtitle = new SimpleBooleanProperty(false);

    public final Property[] properties = {no, filmNr, aboName, channel, theme, title,
            state, progress, remaining, bandwidth, downloadSize,
            filmDate, time, durationMinute,
            hd, ut, geoBlocked, filmUrl, historyUrl, url, urlRtmp, urlSubtitle,
            setDataId, program, programCall, programCallArray, programRestart, programDownloadmanager, startTime,
            destFileName, destPath, destPathFile,
            type, source, placedBack, infoFile, subtitle};

    public static final String TAG = "DownloadData";

    DownloadDataProps() {
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "DownloadData";
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new ConfigIntPropExtra("no", DownloadFieldNames.DOWNLOAD_NR, no));
        list.add(new ConfigIntPropExtra("filmNr", DownloadFieldNames.DOWNLOAD_NR, filmNr));
        list.add(new ConfigStringPropExtra("aboName", DownloadFieldNames.DOWNLOAD_NR, aboName));
        list.add(new ConfigStringPropExtra("channel", DownloadFieldNames.DOWNLOAD_NR, channel));
        list.add(new ConfigStringPropExtra("theme", DownloadFieldNames.DOWNLOAD_NR, theme));
        list.add(new ConfigStringPropExtra("title", DownloadFieldNames.DOWNLOAD_NR, title));
        list.add(new ConfigIntPropExtra("state", DownloadFieldNames.DOWNLOAD_NR, state));
        list.add(new ConfigIntPropExtra("guiState", DownloadFieldNames.DOWNLOAD_NR, guiState));
        list.add(new ConfigDoublePropExtra("progress", DownloadFieldNames.DOWNLOAD_NR, progress));
        list.add(new ConfigDoublePropExtra("guiProgress", DownloadFieldNames.DOWNLOAD_NR, guiProgress));
        list.add(new ConfigStringPropExtra("remaining", DownloadFieldNames.DOWNLOAD_NR, remaining));
        list.add(new ConfigStringPropExtra("bandwidth", DownloadFieldNames.DOWNLOAD_NR, bandwidth));
        list.add(new ConfigPDateProp("filmDate", DownloadFieldNames.DOWNLOAD_NR, filmDate));
        list.add(new ConfigStringPropExtra("time", DownloadFieldNames.DOWNLOAD_NR, time));
        list.add(new ConfigIntPropExtra("durationMinute", DownloadFieldNames.DOWNLOAD_NR, durationMinute));
        list.add(new ConfigBoolPropExtra("hd", DownloadFieldNames.DOWNLOAD_NR, hd));
        list.add(new ConfigBoolPropExtra("ut", DownloadFieldNames.DOWNLOAD_NR, ut));
        list.add(new ConfigBoolPropExtra("geoBlocked", DownloadFieldNames.DOWNLOAD_NR, geoBlocked));
        list.add(new ConfigStringPropExtra("filmUrl", DownloadFieldNames.DOWNLOAD_NR, filmUrl));
        list.add(new ConfigStringPropExtra("historyUrl", DownloadFieldNames.DOWNLOAD_NR, historyUrl));
        list.add(new ConfigStringPropExtra("url", DownloadFieldNames.DOWNLOAD_NR, url));
        list.add(new ConfigStringPropExtra("urlRtmp", DownloadFieldNames.DOWNLOAD_NR, urlRtmp));
        list.add(new ConfigStringPropExtra("urlSubtitle", DownloadFieldNames.DOWNLOAD_NR, urlSubtitle));
        list.add(new ConfigStringPropExtra("setDataId", DownloadFieldNames.DOWNLOAD_NR, setDataId));
        list.add(new ConfigStringPropExtra("program", DownloadFieldNames.DOWNLOAD_NR, program));
        list.add(new ConfigStringPropExtra("programCall", DownloadFieldNames.DOWNLOAD_NR, programCall));
        list.add(new ConfigStringPropExtra("programCallArray", DownloadFieldNames.DOWNLOAD_NR, programCallArray));
        list.add(new ConfigBoolPropExtra("programRestart", DownloadFieldNames.DOWNLOAD_NR, programRestart));
        list.add(new ConfigBoolPropExtra("programDownloadmanager", DownloadFieldNames.DOWNLOAD_NR, programDownloadmanager));
        list.add(new ConfigStringPropExtra("startTime", DownloadFieldNames.DOWNLOAD_NR, startTime));
        list.add(new ConfigStringPropExtra("destFileName", DownloadFieldNames.DOWNLOAD_NR, destFileName));
        list.add(new ConfigStringPropExtra("destPath", DownloadFieldNames.DOWNLOAD_NR, destPath));
        list.add(new ConfigStringPropExtra("destPathFile", DownloadFieldNames.DOWNLOAD_NR, destPathFile));
        list.add(new ConfigStringPropExtra("type", DownloadFieldNames.DOWNLOAD_NR, type));
        list.add(new ConfigStringPropExtra("source", DownloadFieldNames.DOWNLOAD_NR, source));
        list.add(new ConfigBoolPropExtra("placedBack", DownloadFieldNames.DOWNLOAD_NR, placedBack));
        list.add(new ConfigBoolPropExtra("infoFile", DownloadFieldNames.DOWNLOAD_NR, infoFile));
        list.add(new ConfigBoolPropExtra("subtitle", DownloadFieldNames.DOWNLOAD_NR, subtitle));

        return list.toArray(new Config[]{});
    }


    public PDate getFilmDate() {
        return filmDate.get();
    }

    public ObjectProperty<PDate> filmDateProperty() {
        return filmDate;
    }

    public void setFilmDate(PDate filmDate) {
        this.filmDate.set(filmDate);
    }

    public void setFilmDate(String date, String time) {
        PDate d = new PDate();
        d.setPDate(date, time, PDateFactory.F_FORMAT_dd_MM_yyyy, PDateFactory.F_FORMAT_dd_MM_yyyyHH_mm_ss);
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

    public int getNo() {
        return no.get();
    }

    public IntegerProperty noProperty() {
        return no;
    }

    public void setNo(int no) {
        this.no.set(no);
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

    public void setProgress(double progress) {
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

    public String getStartTime() {
        return startTime.get();
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
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


    public int compareTo(DownloadDataProps arg0) {
        return Data.sorter.compare(getChannel(), arg0.getChannel());
    }

}
