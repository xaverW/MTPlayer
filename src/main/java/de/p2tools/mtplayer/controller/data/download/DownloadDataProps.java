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

import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.configfile.config.*;
import de.p2tools.p2lib.configfile.configlist.ConfigStringList;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import de.p2tools.p2lib.mediathek.download.DownloadSize;
import de.p2tools.p2lib.tools.date.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class DownloadDataProps extends P2DataSample<DownloadData> {

    private final ObservableList<String> urlList = FXCollections.observableArrayList(); // wenn mehrere Filme gestartet werden sollen
    private final IntegerProperty no = new SimpleIntegerProperty(P2LibConst.NUMBER_NOT_STARTED);
    private final IntegerProperty filmNo = new SimpleIntegerProperty(P2LibConst.NUMBER_NOT_STARTED);
    private final BooleanProperty audio = new SimpleBooleanProperty(false); // dann ist es ein Download aus AUDIO

    private final StringProperty aboName = new SimpleStringProperty("");
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");

    private final IntegerProperty state = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final IntegerProperty guiState = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final DoubleProperty progress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final DoubleProperty guiProgress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final IntegerProperty remaining = new SimpleIntegerProperty(DownloadConstants.REMAINING_NOT_STARTET);
    private final LongProperty bandwidth = new SimpleLongProperty(); // bytes per second

    private final DownloadSize downloadSize = new DownloadSize();
    private final P2DateProperty filmDate = new P2DateProperty(new P2Date(0));
    private final StringProperty filmDateStr = new SimpleStringProperty("");
    private final StringProperty filmTime = new SimpleStringProperty("");

    private final IntegerProperty durationMinute = new SimpleIntegerProperty(0);
    private final BooleanProperty hd = new SimpleBooleanProperty(false);
    private final BooleanProperty small = new SimpleBooleanProperty(false);
    private final BooleanProperty ut = new SimpleBooleanProperty(false);
    private final BooleanProperty geoBlocked = new SimpleBooleanProperty(false);

    private final StringProperty resolution = new SimpleStringProperty(FilmDataMTP.RESOLUTION_NORMAL); // Dateigröße in normaler Auflösung
    private final StringProperty filmSizeNormal = new SimpleStringProperty(""); // Dateigröße in normaler Auflösung
    private final StringProperty filmSizeHd = new SimpleStringProperty(""); // Dateigröße in normaler Auflösung
    private final StringProperty filmSizeSmall = new SimpleStringProperty(""); // Dateigröße in normaler Auflösung
    private final StringProperty filmUrlNormal = new SimpleStringProperty(""); // URL in normaler Auflösung
    private final StringProperty filmUrlHd = new SimpleStringProperty("");
    private final StringProperty filmUrlSmall = new SimpleStringProperty("");
    private final StringProperty urlWebsite = new SimpleStringProperty("");
    private final StringProperty historyUrl = new SimpleStringProperty("");
    private final StringProperty urlSubtitle = new SimpleStringProperty("");

    private final StringProperty setDataId = new SimpleStringProperty("");
    private final StringProperty programName = new SimpleStringProperty("");
    private final StringProperty programCall = new SimpleStringProperty("");
    private final StringProperty programCallArray = new SimpleStringProperty("");
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

    public final Property[] properties = {no, filmNo, audio, aboName, channel, theme, title, description,
            state, progress, remaining, bandwidth, downloadSize,
            filmDate, filmDateStr, filmTime, durationMinute,
            hd, small, ut, geoBlocked, resolution, filmSizeNormal, filmSizeHd, filmSizeSmall, filmUrlNormal, filmUrlHd, filmUrlSmall,
            urlWebsite, historyUrl, urlSubtitle,
            setDataId, programName, programCall, programCallArray, programDownloadmanager, startTime,
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
        list.add(new Config_intProp("filmNr", filmNo));
        list.add(new Config_boolProp("audio", audio));
        list.add(new Config_stringProp("aboName", aboName));
        list.add(new Config_stringProp("channel", channel));
        list.add(new Config_stringProp("theme", theme));
        list.add(new Config_stringProp("title", title));
        list.add(new Config_stringProp("description", description));
        list.add(new Config_doubleProp("progress", progress));
        list.add(new Config_intProp("remaining", remaining));
        list.add(new Config_longProp("bandwidth", bandwidth));
        list.add(new Config_stringProp("time", filmTime));
        list.add(new Config_intProp("durationMinute", durationMinute));
        list.add(new Config_boolProp("hd", hd));
        list.add(new Config_boolProp("small", small));
        list.add(new Config_boolProp("ut", ut));
        list.add(new Config_boolProp("geoBlocked", geoBlocked));
        list.add(new Config_stringProp("resolution", resolution));
        list.add(new Config_stringProp("filmSizeNormal", filmSizeNormal));
        list.add(new Config_stringProp("filmSizeHd", filmSizeHd));
        list.add(new Config_stringProp("filmSizeSmall", filmSizeSmall));
        list.add(new Config_stringProp("filmUrl", filmUrlNormal));
        list.add(new Config_stringProp("filmUrlHd", filmUrlHd));
        list.add(new Config_stringProp("filmUrlSmall", filmUrlSmall));
        list.add(new Config_stringProp("urlWebsite", urlWebsite));
        list.add(new Config_stringProp("historyUrl", historyUrl));
        list.add(new ConfigStringList("url", urlList));

        list.add(new Config_stringProp("urlSubtitle", urlSubtitle));
        list.add(new Config_stringProp("setDataId", setDataId));
        list.add(new Config_stringProp("program", programName));
        list.add(new Config_stringProp("programCall", programCall));
        list.add(new Config_stringProp("programCallArray", programCallArray));
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

    public ObservableList<String> getUrlList() {
        return urlList;
    }

    public String getUrl() {
        if (urlList.isEmpty()) {
            return "";
        } else {
            return urlList.get(0);
        }
    }

    public void setUrl(String url) {
        urlList.setAll(url);
    }


    public P2Date getFilmDate() {
        return filmDate.get();
    }

    public ObjectProperty<P2Date> filmDateProperty() {
        return filmDate;
    }

    public void setFilmDate(P2Date filmDate) {
        this.filmDate.set(filmDate);
    }

    public void setFilmDate(String date, String time) {
        P2Date d = new P2Date();
        d.setPDate(date, time, P2DateConst.F_FORMAT_dd_MM_yyyy, P2DateConst.F_FORMAT_dd_MM_yyyyHH_mm_ss);
        this.filmDate.setValue(d);
    }

    public String getFilmDateStr() {
        return filmDateStr.get();
    }

    public StringProperty filmDateStrProperty() {
        return filmDateStr;
    }

    public void setFilmDateStr(String filmDateStr) {
        this.filmDateStr.set(filmDateStr);
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

    public int getFilmNo() {
        return filmNo.get();
    }

    public IntegerProperty filmNoProperty() {
        return filmNo;
    }

    public void setFilmNo(int filmNo) {
        this.filmNo.set(filmNo);
    }

    public boolean isAudio() {
        return audio.get();
    }

    public BooleanProperty audioProperty() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio.set(audio);
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

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
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

    public String getFilmTime() {
        return filmTime.get();
    }

    public StringProperty filmTimeProperty() {
        return filmTime;
    }

    public void setFilmTime(String filmTime) {
        this.filmTime.set(filmTime);
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

    public boolean isSmall() {
        return small.get();
    }

    public BooleanProperty smallProperty() {
        return small;
    }

    public void setSmall(boolean small) {
        this.small.set(small);
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

    public String getResolution() {
        return resolution.get();
    }

    public StringProperty resolutionProperty() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution.set(resolution);
    }

    public String getFilmSizeNormal() {
        return filmSizeNormal.get();
    }

    public StringProperty filmSizeNormalProperty() {
        return filmSizeNormal;
    }

    public void setFilmSizeNormal(String filmSizeNormal) {
        this.filmSizeNormal.set(filmSizeNormal);
    }

    public String getFilmSizeHd() {
        return filmSizeHd.get();
    }

    public StringProperty filmSizeHdProperty() {
        return filmSizeHd;
    }

    public void setFilmSizeHd(String filmSizeHd) {
        this.filmSizeHd.set(filmSizeHd);
    }

    public String getFilmSizeSmall() {
        return filmSizeSmall.get();
    }

    public StringProperty filmSizeSmallProperty() {
        return filmSizeSmall;
    }

    public void setFilmSizeSmall(String filmSizeSmall) {
        this.filmSizeSmall.set(filmSizeSmall);
    }

    public String getFilmUrlNormal() {
        return filmUrlNormal.get();
    }

    public StringProperty filmUrlNormalProperty() {
        return filmUrlNormal;
    }

    public void setFilmUrlNormal(String filmUrlNormal) {
        this.filmUrlNormal.set(filmUrlNormal);
    }

    public String getFilmUrlHd() {
        return filmUrlHd.get();
    }

    public StringProperty filmUrlHdProperty() {
        return filmUrlHd;
    }

    public void setFilmUrlHd(String filmUrlHd) {
        this.filmUrlHd.set(filmUrlHd);
    }

    public String getFilmUrlSmall() {
        return filmUrlSmall.get();
    }

    public StringProperty filmUrlSmallProperty() {
        return filmUrlSmall;
    }

    public void setFilmUrlSmall(String filmUrlSmall) {
        this.filmUrlSmall.set(filmUrlSmall);
    }

    public String getUrlWebsite() {
        return urlWebsite.get();
    }

    public StringProperty urlWebsiteProperty() {
        return urlWebsite;
    }

    public void setUrlWebsite(String urlWebsite) {
        this.urlWebsite.set(urlWebsite);
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

    public String getProgramName() {
        return programName.get();
    }

    public StringProperty programNameProperty() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName.set(programName);
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

    public boolean isProgramDownloadmanager() {
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

    public LocalDateTime getStartTimeLdt() {
        LocalDateTime ldt = P2LDateTimeFactory.fromString(startTime.getValueSafe());
        if (ldt.equals(LocalDateTime.MIN)) {
            startTime.set("");
        }
        return ldt;
    }

    public String getStartTimeOnly() {
        LocalDateTime ldt = getStartTimeLdt();
        if (ldt.equals(LocalDateTime.MIN)) {
            return "";
        }
        LocalTime lt = ldt.toLocalTime();
        return P2LTimeFactory.toString_HM(lt);
    }


    public StringProperty startTimeProperty() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
    }

    public void setStartTimeToday(String startTime) {
        if (startTime.isEmpty()) {
            this.startTime.set("");
            return;
        }

        final LocalTime localTime = P2LTimeFactory.fromString_HM(startTime);
        final LocalDateTime ldt = LocalDateTime.of(LocalDate.now(), localTime);
        final String localDateTime = P2LDateTimeFactory.toString(ldt);
        this.startTime.set(localDateTime);
    }

    public void setStartTimeAlsoTomorrow(String startTime) {
        if (startTime.isEmpty()) {
            this.startTime.set("");
            return;
        }

        final LocalTime localTime = P2LTimeFactory.fromString_HM(startTime);
        final LocalDateTime ldt;
        if (LocalTime.now().isBefore(localTime)) {
            // dann passts, kommt noch heute
            ldt = LocalDateTime.of(LocalDate.now(), localTime);
        } else {
            // dann kommt es erst morgen
            ldt = LocalDateTime.of(LocalDate.now().plusDays(1), localTime);
        }

        final String localDateTime = P2LDateTimeFactory.toString(ldt);
        this.startTime.set(localDateTime);
    }


    // ===== File
    // setzen immer!!! über File im dto
    public String getDestFileName() {
        return destFileName.get();
    }

    public StringProperty destFileNameProperty() {
        return destFileName;
    }

    public String getDestPath() {
        return destPath.get();
    }

    public StringProperty destPathProperty() {
        return destPath;
    }

    public String getDestPathFile() {
        return destPathFile.get();
    }

    public StringProperty destPathFileProperty() {
        return destPathFile;
    }
    //======

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

    public boolean isInfoFile() {
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
