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

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.configfile.config.*;
import de.p2tools.p2lib.configfile.configlist.ConfigStringList;
import de.p2tools.p2lib.configfile.pdata.PDataSample;
import de.p2tools.p2lib.mtdownload.DownloadSize;
import de.p2tools.p2lib.tools.date.DateFactory;
import de.p2tools.p2lib.tools.date.PDate;
import de.p2tools.p2lib.tools.date.PDateProperty;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class DownloadDataProps extends PDataSample<DownloadData> {

    private final ArrayList<FilmDataMTP> filmList = new ArrayList<>(); // wenn mehrere Filme gestartet werden sollen
    private final ObservableList<String> urlList = FXCollections.observableArrayList();
    ; // wenn mehrere Filme gestartet werden sollen

    private final IntegerProperty no = new SimpleIntegerProperty(P2LibConst.NUMBER_NOT_STARTED);
    private final IntegerProperty filmNr = new SimpleIntegerProperty(P2LibConst.NUMBER_NOT_STARTED);

    private final StringProperty aboName = new SimpleStringProperty("");
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");

    private final IntegerProperty state = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final IntegerProperty guiState = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final DoubleProperty progress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final DoubleProperty guiProgress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final IntegerProperty remaining = new SimpleIntegerProperty(DownloadConstants.REMAINING_NOT_STARTET);
    private final LongProperty bandwidth = new SimpleLongProperty();

    private final DownloadSize downloadSize = new DownloadSize();
    private final PDateProperty filmDate = new PDateProperty(new PDate(0));

    private final StringProperty time = new SimpleStringProperty("");
    private final IntegerProperty durationMinute = new SimpleIntegerProperty(0);
    private final BooleanProperty hd = new SimpleBooleanProperty(false);
    private final BooleanProperty ut = new SimpleBooleanProperty(false);
    private final BooleanProperty geoBlocked = new SimpleBooleanProperty(false);

    private final StringProperty filmUrl = new SimpleStringProperty(""); //in normaler Auflösung
    private final StringProperty historyUrl = new SimpleStringProperty("");
    //    private final StringProperty url = new SimpleStringProperty(""); //in der gewählte Auflösung
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
            hd, ut, geoBlocked, filmUrl, historyUrl, /*url,*/ urlSubtitle,
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
        list.add(new Config_intProp("no", no));
        list.add(new Config_intProp("filmNr", filmNr));
        list.add(new Config_stringProp("aboName", aboName));
        list.add(new Config_stringProp("channel", channel));
        list.add(new Config_stringProp("theme", theme));
        list.add(new Config_stringProp("title", title));
//        list.add(new Config_intProp("state", state)); Downloads starten immer in "init" damit sie nicht automatisch starten
        list.add(new Config_doubleProp("progress", progress));
        list.add(new Config_intProp("remaining", remaining));
        list.add(new Config_longProp("bandwidth", bandwidth));
        list.add(new Config_stringProp("time", time));
        list.add(new Config_intProp("durationMinute", durationMinute));
        list.add(new Config_boolProp("hd", hd));
        list.add(new Config_boolProp("ut", ut));
        list.add(new Config_boolProp("geoBlocked", geoBlocked));
        list.add(new Config_stringProp("filmUrl", filmUrl));
        list.add(new Config_stringProp("historyUrl", historyUrl));

//        list.add(new Config_stringProp("url", url));
        list.add(new ConfigStringList("url", urlList));


        list.add(new Config_stringProp("urlSubtitle", urlSubtitle));
        list.add(new Config_stringProp("setDataId", setDataId));
        list.add(new Config_stringProp("program", program));
        list.add(new Config_stringProp("programCall", programCall));
        list.add(new Config_stringProp("programCallArray", programCallArray));
        list.add(new Config_boolProp("programRestart", programRestart));
        list.add(new Config_boolProp("programDownloadmanager", programDownloadmanager));
        list.add(new Config_stringProp("startTime", startTime));
        list.add(new Config_stringProp("destFileName", destFileName));
        list.add(new Config_stringProp("destPath", destPath));
        list.add(new Config_stringProp("destPathFile", destPathFile));
        list.add(new Config_stringProp("type", type));
        list.add(new Config_stringProp("source", source));
        list.add(new Config_boolProp("placedBack", placedBack));
        list.add(new Config_boolProp("infoFile", infoFile));
        list.add(new Config_boolProp("subtitle", subtitle));

        return list.toArray(new Config[]{});
    }

    public ArrayList<FilmDataMTP> getFilmList() {
        return filmList;
    }

    public ObservableList<String> getUrlList() {
        return urlList;
    }

    public FilmDataMTP getFilm() {
        if (filmList.isEmpty()) {
            return null;
        } else {
            return filmList.get(0);
        }
    }

    public void setFilm(FilmDataMTP filmDataMTP) {
        filmList.add(0, filmDataMTP);
    }

    public String getUrl() {
        if (urlList.isEmpty()) {
            return "";
        } else {
            return urlList.get(0);
        }
    }

    public void setUrl(String url) {
        urlList.add(0, url);
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
        d.setPDate(date, time, DateFactory.F_FORMAT_dd_MM_yyyy, DateFactory.F_FORMAT_dd_MM_yyyyHH_mm_ss);
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

    public int getRemaining() {
        return remaining.get();
    }

    public IntegerProperty remainingProperty() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining.set(remaining);
    }

    public long getBandwidth() {
        return bandwidth.get();
    }

    public LongProperty bandwidthProperty() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth.set(bandwidth);
    }

    public void setBandwidthEnd(long bandwidth) {
        this.bandwidth.setValue(-1 * bandwidth);
    }

    public DownloadSize getDownloadSize() {
        return downloadSize;
    }

    public DownloadSize downloadSizeProperty() {
        return downloadSize;
    }

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

    public boolean isPlacedBack() {
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

    @Override
    public int compareTo(DownloadData arg0) {
        int ret;
        if ((ret = sorter.compare(getChannel(), arg0.getChannel())) == 0) {
            return sorter.compare(getTheme(), arg0.getTheme());
        }
        return ret;
    }
}
