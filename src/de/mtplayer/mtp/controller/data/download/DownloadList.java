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

import de.mtplayer.mLib.tools.Duration;
import de.mtplayer.mLib.tools.SysMsg;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.SetData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class DownloadList extends SimpleListProperty<Download> {

    private final Daten daten;
    private final DownloadListAbo downloadListAbo;
    private final DownloadListStarts downloadListStarts;
    private final DownloadStartStop download_startStop;

    private final DownloadInfosAll download_infosAll;
    private BooleanProperty downloadsChanged = new SimpleBooleanProperty(true);


    public DownloadList(Daten daten) {
        super(FXCollections.observableArrayList());
        this.daten = daten;
        this.downloadListAbo = new DownloadListAbo(daten, this);
        this.downloadListStarts = new DownloadListStarts(daten, this);
        this.download_startStop = new DownloadStartStop(daten, this);
        this.download_infosAll = new DownloadInfosAll(daten, this);
    }

    public boolean getDownloadsChanged() {
        return downloadsChanged.get();
    }

    public void setDownloadsChanged() {
        downloadsChanged.set(!downloadsChanged.get());
    }

    public BooleanProperty downloadsChangedProperty() {
        return downloadsChanged;
    }

    public void sort() {
        Collections.sort(this);
    }

    public synchronized boolean add(Download d) {
        return super.add(d);
    }

    public synchronized boolean addAll(ArrayList<Download> d) {
        return super.addAll(d);
    }

    public synchronized boolean addWithNr(Download e) {
        final boolean ret = super.add(e);
        listeNummerieren();
        return ret;
    }


    public DownloadInfosAll getDownload_infosAll() {
        return download_infosAll;
    }

    public synchronized int countRunningDownloads() {
        // es wird nach noch nicht fertigen gestarteten Downloads gesucht
        int ret = 0;
        for (final Download download : this) {
            if (download.isStateStartedRun()) {
                ++ret;
            }
        }
        return ret;
    }

    private int counter = 50; //todo das dauert sonst viel zu lang

    public synchronized void filmEintragen() {
        // bei einmal Downloads nach einem Programmstart/Neuladen der Filmliste
        // den Film wieder eintragen
        Duration.counterStart("Filme eintragen");
        SysMsg.sysMsg("Filme in Downloads eintragen");
        for (Download d : this) {
            --counter;
            if (counter < 0) {
                break;
            }
            d.setFilm(daten.filmList.getFilmByUrl_klein_hoch_hd(d.getUrl())); //todo sollen da wirklich alle Filmfelder gesetzt werden??
            d.setSizeDownloadFromFilm();
        }
//        parallelStream().filter(d -> {
//            counter -= 1;
//            System.out.println(counter);
//            if ((counter > 0) && d.getFilm() == null) {
//                return true;
//            } else {
//                return false;
//            }
//        }).forEach(d -> {
//            d.setFilm(daten.filmList.getFilmByUrl_klein_hoch_hd(d.getUrl())); //todo sollen da wirklich alle Filmfelder gesetzt werden??
//            d.setSizeDownloadFromFilm();
//
//        });
        SysMsg.sysMsg("  -> Filme in Downloads eingetragen");
        Duration.counterStop("Filme eintragen");
    }


    public synchronized void downloadsVorziehen(ArrayList<Download> download) {
        renumberList(1 + download.size());
        int i = 1;
        for (final Download datenDownload : download) {
            datenDownload.setNr(i++);
        }
    }

    public synchronized Download getDownloadByUrl(String url) {
        Download ret = null;
        for (final Download download : this) {
            if (download.getUrl().equals(url)) {
                ret = download;
                break;
            }
        }
        return ret;
    }


    public synchronized Download getDownloadUrlFilm(String urlFilm) {
        for (final Download datenDownload : this) {
            if (datenDownload.getFilmUrl().equals(urlFilm)) {
                return datenDownload;
            }
        }
        return null;
    }

    public synchronized void listePutzen() {
        // fertige Downloads löschen, fehlerhafte zurücksetzen

        boolean gefunden = false;
        Iterator<Download> it = this.iterator();
        while (it.hasNext()) {
            Download download = it.next();
            if (download.isStateInit() ||
                    download.isStateStoped()) {
                continue;
            }
            if (download.isStateFinished()) {
                // alles was fertig/fehlerhaft ist, kommt beim putzen weg
                it.remove();
                gefunden = true;
            } else if (download.isStateError()) {
                // fehlerhafte werden zurückgesetzt
                download.resetDownload();
                gefunden = true;
            }
        }

        if (gefunden) {
            setDownloadsChanged();
        }
    }


    // =========================
    // Abos
    public synchronized void abosSuchen() {
        daten.mtFxController.setMasker();

        final int count = getSize();
        new Thread(() -> {
            downloadListAbo.abosAuffrischen();
            downloadListAbo.abosSuchen();
            if (daten.downloadList.getSize() == count) {
                // dann wurden evtl. nur zurückgestellte Downloads wieder aktiviert
                setDownloadsChanged();
            }
            daten.mtFxController.resetMasker();
        }).start();
    }

    public synchronized ArrayList<String> generateAboNameList(ArrayList<String> nameList) {
        return downloadListAbo.generateAboNameList(nameList);
    }

    // =========================
    // Starts
    public synchronized int[] getStarts() {
        return downloadListStarts.getStarts();
    }

    public synchronized int getNumberOfStartsNotFinished() {
        return downloadListStarts.getNumberOfStartsNotFinished();
    }

    public synchronized long getMaximumFinishTimeOfRunningStarts() {
        return downloadListStarts.getMaximumFinishTimeOfRunningStarts();
    }

    public synchronized LinkedList<Download> getListOfStartsNotFinished(String quelle) {
        return downloadListStarts.getListOfStartsNotFinished(quelle);
    }

    public synchronized Download getRestartDownload() {
        return downloadListStarts.getRestartDownload();
    }


    public synchronized void buttonStartsPutzen() {
        downloadListStarts.buttonStartsPutzen();
    }

    public synchronized Download getNextStart() {
        return downloadListStarts.getNextStart();
    }

    // ==============================
    // DownloadStartStop

    public synchronized void stopDownloads(ArrayList<Download> list) {
        if (download_startStop.stopDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void delDownloads(Download download) {
        download_startStop.delDownloads(download);
    }

    public synchronized void putbackDownloads(ArrayList<Download> list) {
        if (download_startStop.putbackDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void resetDownloads(ArrayList<Download> list) {
        if (download_startStop.delDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void delDownloads(ArrayList<Download> list) {
        if (download_startStop.delDownloads(list)) {
            setDownloadsChanged();
        }
    }


    public void startDownloads(Download download) {
        download_startStop.startDownloads(download);
        setDownloadsChanged();
    }


    public void startDownloads(ArrayList<Download> liste, boolean auchFertige) {
        if (download_startStop.startDownloads(liste, auchFertige)) {
            setDownloadsChanged();
        }
    }

    // ======================================
    // DownloadInfosAll
    public synchronized void makeDownloadInfos() {
        download_infosAll.makeDownloadInfos();
    }


    //==========================================================
    public synchronized void initDownloads() {
        this.stream().forEach(download -> {
            //ist bei gespeiherten Downloads der Fall
            SetData pSet = daten.setList.getPsetName(download.getSet());
            if (pSet != null) { //todo und dann??
                download.setPset(pSet);
            }
        });
    }

    public synchronized void listeNummerieren() {
        int i = 1;
        for (final Download download : this) {
            if (download.isStarted()) {
                download.setNr(i++);
            } else {
                download.setNr(DownloadInfos.DOWNLOAD_NUMBER_NOT_STARTED);
            }
        }
    }

    public synchronized void renumberList(int addNr) {
        for (final Download download : this) {
            final int i = download.getNr();
            if (i < DownloadInfos.DOWNLOAD_NUMBER_NOT_STARTED) {
                download.setNr(i + addNr);
            }
        }
    }

    public synchronized void numberStoppedDownloads() {
        this.stream().filter(download -> download.isStateInit()).forEach(download -> download.setNr(DownloadInfos.DOWNLOAD_NUMBER_NOT_STARTED));
    }

    private int getNextNumber() {
        int i = 1;
        for (final Download download : this) {
            if (download.getNr() < DownloadInfos.DOWNLOAD_NUMBER_NOT_STARTED && download.getNr() >= i) {
                i = download.getNr() + 1;
            }
        }
        return i++;
    }

    public synchronized void addNumber(Download addDownload) {
        addDownload.setNr(getNextNumber());
    }

    public synchronized void addNumber(ArrayList<Download> downloads) {
        int i = getNextNumber();
        for (Download download : downloads) {
            download.setNr(i++);
        }
    }
}
