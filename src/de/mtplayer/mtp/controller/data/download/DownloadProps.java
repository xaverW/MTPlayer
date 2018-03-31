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
import javafx.beans.property.*;

public class DownloadProps extends DownloadXml {

    private final IntegerProperty nr = new SimpleIntegerProperty(DownloadInfos.DOWNLOAD_NUMBER_NOT_STARTED);
    private final IntegerProperty filmNr = new SimpleIntegerProperty(DownloadInfos.FILM_NUMBER_NOT_FOUND);

    private final StringProperty aboName = new SimpleStringProperty("");
    private final StringProperty sender = new SimpleStringProperty("");
    private final StringProperty thema = new SimpleStringProperty("");
    private final StringProperty titel = new SimpleStringProperty("");

    private final IntegerProperty state = new SimpleIntegerProperty(DownloadInfos.STATE_INIT);
    private final DoubleProperty progress = new SimpleDoubleProperty(DownloadInfos.PROGRESS_NICHT_GESTARTET);
    private final StringProperty restzeit = new SimpleStringProperty("");
    private final StringProperty bandbreite = new SimpleStringProperty("");

    private final DownloadSize downloadSize = new DownloadSize();
    private final ObjectProperty<MDate> filmDate = new SimpleObjectProperty<>(new MDate(0));

    private final StringProperty zeit = new SimpleStringProperty("");
    private final StringProperty dauer = new SimpleStringProperty("");
    private final BooleanProperty hd = new SimpleBooleanProperty(false);
    private final BooleanProperty ut = new SimpleBooleanProperty(false);
    private final BooleanProperty geoBlocked = new SimpleBooleanProperty(false);

    private final StringProperty filmUrl = new SimpleStringProperty(""); //in normaler Auflösung
    private final StringProperty historyUrl = new SimpleStringProperty("");
    private final StringProperty url = new SimpleStringProperty(""); //in der gewählte Auflösung
    private final StringProperty urlRtmp = new SimpleStringProperty("");
    private final StringProperty urlSubtitle = new SimpleStringProperty("");

    private final StringProperty set = new SimpleStringProperty("");
    private final StringProperty programm = new SimpleStringProperty("");
    private final StringProperty programmAufruf = new SimpleStringProperty("");
    private final StringProperty programmAufrufArray = new SimpleStringProperty("");
    private final BooleanProperty programmRestart = new SimpleBooleanProperty(false);
    private final BooleanProperty programmDownloadmanager = new SimpleBooleanProperty(false);

    private final StringProperty zielDateiname = new SimpleStringProperty("");
    private final StringProperty zielPfad = new SimpleStringProperty("");
    private final StringProperty zielPfadDatei = new SimpleStringProperty("");

    private final StringProperty art = new SimpleStringProperty(DownloadInfos.ART_DOWNLOAD);
    private final StringProperty source = new SimpleStringProperty(DownloadInfos.SRC_ALL);
    private final BooleanProperty zurueckgestellt = new SimpleBooleanProperty(false);
    private final BooleanProperty infodatei = new SimpleBooleanProperty(false);
    private final BooleanProperty subtitle = new SimpleBooleanProperty(false);

    public final Property[] properties = {nr, filmNr, aboName, sender, thema, titel,
            progress, restzeit, bandbreite, downloadSize,
            filmDate, zeit, dauer,
            hd, ut, geoBlocked, filmUrl, historyUrl, url, urlRtmp, urlSubtitle,
            set, programm, programmAufruf, programmAufrufArray, programmRestart, programmDownloadmanager,
            zielDateiname, zielPfad, zielPfadDatei,
            art, source, zurueckgestellt, infodatei, subtitle};


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

    public String getSender() {
        return sender.get();
    }

    public StringProperty senderProperty() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender.set(sender);
    }

    public String getThema() {
        return thema.get();
    }

    public StringProperty themaProperty() {
        return thema;
    }

    public void setThema(String thema) {
        this.thema.set(thema);
    }

    public String getTitel() {
        return titel.get();
    }

    public StringProperty titelProperty() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel.set(titel);
    }

    public int getState() {
        return state.get();
    }

    public IntegerProperty stateProperty() {
        return state;
    }

    public void setState(int state) {
        this.state.set(state);
    }

    public Double getProgress() {
        return progress.getValue();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress.setValue(progress);
    }

    public String getRestzeit() {
        return restzeit.get();
    }

    public StringProperty restzeitProperty() {
        return restzeit;
    }

    public void setRestzeit(String restzeit) {
        this.restzeit.set(restzeit);
    }

    public String getBandbreite() {
        return bandbreite.get();
    }

    public StringProperty bandbreiteProperty() {
        return bandbreite;
    }

    public void setBandbreite(String bandbreite) {
        this.bandbreite.set(bandbreite);
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


    public String getZeit() {
        return zeit.get();
    }

    public StringProperty zeitProperty() {
        return zeit;
    }

    public void setZeit(String zeit) {
        this.zeit.set(zeit);
    }

    public String getDauer() {
        return dauer.get();
    }

    public StringProperty dauerProperty() {
        return dauer;
    }

    public void setDauer(String dauer) {
        this.dauer.set(dauer);
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

    public String getSet() {
        return set.get();
    }

    public StringProperty setProperty() {
        return set;
    }

    public void setSet(String set) {
        this.set.set(set);
    }

    public String getProgramm() {
        return programm.get();
    }

    public StringProperty programmProperty() {
        return programm;
    }

    public void setProgramm(String programm) {
        this.programm.set(programm);
    }

    public String getProgrammAufruf() {
        return programmAufruf.get();
    }

    public StringProperty programmAufrufProperty() {
        return programmAufruf;
    }

    public void setProgrammAufruf(String programmAufruf) {
        this.programmAufruf.set(programmAufruf);
    }

    public String getProgrammAufrufArray() {
        return programmAufrufArray.get();
    }

    public StringProperty programmAufrufArrayProperty() {
        return programmAufrufArray;
    }

    public void setProgrammAufrufArray(String programmAufrufArray) {
        this.programmAufrufArray.set(programmAufrufArray);
    }

    public boolean isProgrammRestart() {
        return programmRestart.get();
    }

    public BooleanProperty programmRestartProperty() {
        return programmRestart;
    }

    public void setProgrammRestart(boolean programmRestart) {
        this.programmRestart.set(programmRestart);
    }

    public boolean isProgrammDownloadmanager() {
        return programmDownloadmanager.get();
    }

    public BooleanProperty programmDownloadmanagerProperty() {
        return programmDownloadmanager;
    }

    public void setProgrammDownloadmanager(boolean programmDownloadmanager) {
        this.programmDownloadmanager.set(programmDownloadmanager);
    }

    public String getZielDateiname() {
        return zielDateiname.get();
    }

    public StringProperty zielDateinameProperty() {
        return zielDateiname;
    }

    public void setZielDateiname(String zielDateiname) {
        this.zielDateiname.set(zielDateiname);
    }

    public String getZielPfad() {
        return zielPfad.get();
    }

    public StringProperty zielPfadProperty() {
        return zielPfad;
    }

    public void setZielPfad(String zielPfad) {
        this.zielPfad.set(zielPfad);
    }

    public String getZielPfadDatei() {
        return zielPfadDatei.get();
    }

    public StringProperty zielPfadDateiProperty() {
        return zielPfadDatei;
    }

    public void setZielPfadDatei(String zielPfadDatei) {
        this.zielPfadDatei.set(zielPfadDatei);
    }

    public String getArt() {
        return art.get();
    }

    public StringProperty artProperty() {
        return art;
    }

    public void setArt(String art) {
        this.art.set(art);
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

    public boolean isZurueckgestellt() {
        return zurueckgestellt.get();
    }

    public BooleanProperty zurueckgestelltProperty() {
        return zurueckgestellt;
    }

    public void setZurueckgestellt(boolean zurueckgestellt) {
        this.zurueckgestellt.set(zurueckgestellt);
    }

    public boolean isInfodatei() {
        return infodatei.get();
    }

    public BooleanProperty infodateiProperty() {
        return infodatei;
    }

    public void setInfodatei(boolean infodatei) {
        this.infodatei.set(infodatei);
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
        setSender(arr[DOWNLOAD_SENDER]);
        setThema(arr[DOWNLOAD_THEMA]);
        setTitel(arr[DOWNLOAD_TITEL]);

        setFilmDate(arr[DOWNLOAD_DATUM], arr[DOWNLOAD_ZEIT]);
        setZeit(arr[DOWNLOAD_ZEIT]);
        setDauer(arr[DOWNLOAD_DAUER]);

        setHd(Boolean.parseBoolean(arr[DOWNLOAD_HD]));
        setUt(Boolean.parseBoolean(arr[DOWNLOAD_UT]));
        setGeoBlocked(Boolean.parseBoolean(arr[DOWNLOAD_GEO]));
        setFilmUrl(arr[DOWNLOAD_FILM_URL]);
        setHistoryUrl(arr[DOWNLOAD_HISTORY_URL]);
        setUrl(arr[DOWNLOAD_URL]);
        setUrlRtmp(arr[DOWNLOAD_URL_RTMP]);
        setSubtitle(Boolean.parseBoolean(arr[DOWNLOAD_URL_SUBTITLE]));
        setSet(arr[DOWNLOAD_PROGRAMMSET]);
        setProgramm(arr[DOWNLOAD_PROGRAMM]);
        setProgrammAufruf(arr[DOWNLOAD_PROGRAMM_AUFRUF]);
        setProgrammAufrufArray(arr[DOWNLOAD_PROGRAMM_AUFRUF_ARRAY]);
        setZielDateiname(arr[DOWNLOAD_ZIEL_DATEINAME]);
        setZielPfad(arr[DOWNLOAD_ZIEL_PFAD]);
        setZielPfadDatei(arr[DOWNLOAD_ZIEL_PFAD_DATEINAME]);

        setArt(arr[Download.DOWNLOAD_ART]);
        if (!arr[Download.DOWNLOAD_QUELLE].equals(DownloadInfos.SRC_ABO)) {
            // bei gelöschten Abos kanns dazu kommen
            arr[Download.DOWNLOAD_QUELLE] = DownloadInfos.SRC_DOWNLOAD;
        }
        setSource(arr[DOWNLOAD_QUELLE]);
        setZurueckgestellt(Boolean.parseBoolean(arr[DOWNLOAD_ZURUECKGESTELLT]));
        setInfodatei(Boolean.parseBoolean(arr[DOWNLOAD_INFODATEI]));
        setSubtitle(Boolean.parseBoolean(arr[DOWNLOAD_SUBTITLE]));
        setProgrammDownloadmanager(Boolean.parseBoolean(arr[DOWNLOAD_PROGRAMM_DOWNLOADMANAGER]));
    }


    public void setXmlFromProps() {
        arr[DOWNLOAD_ABO] = getAboName();
        arr[DOWNLOAD_SENDER] = getSender();
        arr[DOWNLOAD_THEMA] = getThema();
        arr[DOWNLOAD_TITEL] = getTitel();
        arr[DOWNLOAD_DATUM] = getFilmDate().toString();
        arr[DOWNLOAD_ZEIT] = getZeit();
        arr[DOWNLOAD_DAUER] = getDauer();
        arr[DOWNLOAD_HD] = String.valueOf(isHd());
        arr[DOWNLOAD_UT] = String.valueOf(isUt());
        arr[DOWNLOAD_GEO] = String.valueOf(getGeoBlocked());
        arr[DOWNLOAD_FILM_URL] = getFilmUrl();
        arr[DOWNLOAD_HISTORY_URL] = getHistoryUrl();
        arr[DOWNLOAD_URL] = getUrl();
        arr[DOWNLOAD_URL_RTMP] = getUrlRtmp();
        arr[DOWNLOAD_URL_SUBTITLE] = getUrlSubtitle();
        arr[DOWNLOAD_PROGRAMMSET] = getSet();
        arr[DOWNLOAD_PROGRAMM] = getProgramm();
        arr[DOWNLOAD_PROGRAMM_AUFRUF] = getProgrammAufruf();
        arr[DOWNLOAD_PROGRAMM_AUFRUF_ARRAY] = getProgrammAufrufArray();
        arr[DOWNLOAD_ZIEL_DATEINAME] = getZielDateiname();
        arr[DOWNLOAD_ZIEL_PFAD] = getZielPfad();
        arr[DOWNLOAD_ZIEL_PFAD_DATEINAME] = getZielPfadDatei();
        arr[DOWNLOAD_ART] = getArt();
        arr[DOWNLOAD_QUELLE] = getSource();
        arr[DOWNLOAD_ZURUECKGESTELLT] = String.valueOf(isZurueckgestellt());
        arr[DOWNLOAD_INFODATEI] = String.valueOf(isInfodatei());
        arr[DOWNLOAD_SUBTITLE] = String.valueOf(isSubtitle());
        arr[DOWNLOAD_PROGRAMM_DOWNLOADMANAGER] = String.valueOf(isProgrammDownloadmanager());
    }


    public int compareTo(DownloadProps arg0) {
        return Data.sorter.compare(getSender(), arg0.getSender());
    }

}
