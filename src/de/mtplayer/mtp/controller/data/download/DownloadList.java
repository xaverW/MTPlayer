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

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.p2tools.p2Lib.tools.log.PDuration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.*;


//de.mtplayer.mtp.controller.data.download.DownloadList.getNextStart(DownloadList.java:231)
//de.mtplayer.mtp.controller.data.download.DownloadList.searchForDownloadsFromAbos(DownloadList.java:195)
//de.mtplayer.mtp.controller.data.download.DownloadList.getListOfStartsNotFinished(DownloadList.java:218)


public class DownloadList extends SimpleListProperty<Download> {

    private final ProgData progData;
    private final DownloadListAbo downloadListAbo;
    private final DownloadListStarts downloadListStarts;
    private final DownloadListStartStop downloadListStartStop;
    private final DownloadListInfoAll downloadListInfoAll;

//    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
//    private final Lock readLock = lock.readLock();
//    private final Lock writeLock = lock.writeLock();

    private BooleanProperty downloadsChanged = new SimpleBooleanProperty(true);


    public DownloadList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
        this.downloadListAbo = new DownloadListAbo(progData, this);
        this.downloadListStarts = new DownloadListStarts(progData, this);
        this.downloadListStartStop = new DownloadListStartStop(progData, this);
        this.downloadListInfoAll = new DownloadListInfoAll(progData, this);

//        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, MsgMemController.class.getSimpleName()) {
//            @Override
//            public void ping() {
//                setGuiProps();
//            }
//        });

    }

//    private void setGuiProps() {
//        this.stream().forEach(download -> download.setGuiPropertys());
//    }

    public boolean getDownloadsChanged() {
        return downloadsChanged.get();
    }

    synchronized void setDownloadsChanged() {
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

    @Override
    public synchronized boolean addAll(Collection<? extends Download> elements) {
        return super.addAll(elements);
    }

    public synchronized boolean addWithNr(Download e) {
        final boolean ret = super.add(e);
        setNumbersInList();
        return ret;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> objects) {
        return super.removeAll(objects);
    }

    public DownloadListInfoAll getDownloadListInfoAll() {
        return downloadListInfoAll;
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

    public synchronized void addFilmInList() {
        // bei einmal Downloads nach einem Programmstart/Neuladen der Filmliste
        // den Film wieder eintragen
        PDuration.counterStart("Filme eintragen");

        for (Download d : this) {
            --counter;
            if (counter < 0) {
                break;
            }
            d.setFilm(progData.filmlist.getFilmByUrl_small_high_hd(d.getUrl())); //todo sollen da wirklich alle Filmfelder gesetzt werden??
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
//            d.setFilm(daten.filmlist.getFilmByUrl_klein_hoch_hd(d.getUrl())); //todo sollen da wirklich alle Filmfelder gesetzt werden??
//            d.setSizeDownloadFromFilm();
//
//        });
        PDuration.counterStop("Filme eintragen");
    }


    public synchronized void preferDownloads(ArrayList<Download> download) {
        renumberList(1 + download.size());
        int i = 1;
        for (final Download dataDownload : download) {
            dataDownload.setNr(i++);
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
        for (final Download dataDownload : this) {
            if (dataDownload.getFilmUrl().equals(urlFilm)) {
                return dataDownload;
            }
        }
        return null;
    }

    public synchronized void cleanUpList() {
        // fertige Downloads löschen, fehlerhafte zurücksetzen

        boolean found = false;
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
                found = true;
            } else if (download.isStateError()) {
                // fehlerhafte werden zurückgesetzt
                download.resetDownload();
                found = true;
            }
        }

        if (found) {
            setDownloadsChanged();
        }
    }


    // =========================
    // Downloads für Abos suchen
    public synchronized void searchForDownloadsFromAbos() {
//        System.out.println("writeLock 0");
//        writeLock.lock();
//        try {
        final int count = getSize();
        downloadListAbo.searchDownloadsFromAbos();
        if (getSize() == count) {
            // dann wurden evtl. nur zurückgestellte Downloads wieder aktiviert
            setDownloadsChanged();
        }
//        } finally {
//            System.out.println("writeLock 1");
//            writeLock.unlock();
//        }
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

    public synchronized LinkedList<Download> getListOfStartsNotFinished(String source) {
        LinkedList<Download> ret;

//        System.out.println("readLock 0");
//        readLock.lock();
//        try {
        ret = downloadListStarts.getListOfStartsNotFinished(source);
//        } finally {
//            readLock.unlock();
//            System.out.println("readLock 1");
//        }

        return ret;
    }

    public synchronized Download getRestartDownload() {
        return downloadListStarts.getRestartDownload();
    }


    public synchronized void cleanUpButtonStarts() {
        downloadListStarts.cleanUpButtonStarts();
    }

    public synchronized Download getNextStart() {
        Download download = null;

//        System.out.println("readLock 0");
//        readLock.lock();
//        try {
        download = downloadListStarts.getNextStart();
//        } finally {
//            readLock.unlock();
//            System.out.println("readLock 1");
//        }
        return download;
    }

    public synchronized void resetPlacedBack() {
        // zurückgestellte wieder aktivieren
        forEach(d -> d.setPlacedBack(false));
    }

    // ==============================
    // DownloadListStartStop

    public synchronized void stopDownloads(ArrayList<Download> list) {
        if (downloadListStartStop.stopDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void delDownloads(Download download) {
        downloadListStartStop.delDownloads(download);
    }

    public synchronized void putBackDownloads(ArrayList<Download> list) {
        if (downloadListStartStop.putBackDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void resetDownloads(ArrayList<Download> list) {
        if (downloadListStartStop.delDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void delDownloads(ArrayList<Download> list) {
        if (downloadListStartStop.delDownloads(list)) {
            setDownloadsChanged();
        }
    }


    public void startDownloads(Download download) {
        downloadListStartStop.startDownloads(download);
        setDownloadsChanged();
    }


    public void startDownloads() {
        startDownloads(this, false);
    }

    public void startDownloads(Collection<Download> list, boolean alsoFinished) {
        if (downloadListStartStop.startDownloads(list, alsoFinished)) {
            setDownloadsChanged();
        }
    }

    // ======================================
    // DownloadInfosAll
    public synchronized void makeDownloadInfo() {
        downloadListInfoAll.makeDownloadInfo();
    }


    //==========================================================
    public synchronized void initDownloads() {
        this.stream().forEach(download -> {
            //ist bei gespeiherten Downloads der Fall
            SetData pSet = progData.setList.getPsetName(download.getSet());
            if (pSet != null) { //todo und dann??
                download.setPset(pSet);
            }
        });
    }

    public synchronized void setNumbersInList() {
        int i = 1;
        for (final Download download : this) {
            if (download.isStarted()) {
                download.setNr(i++);
            } else {
                download.setNr(DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED);
            }
        }
    }

    public synchronized void renumberList(int addNr) {
        for (final Download download : this) {
            final int i = download.getNr();
            if (i < DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED) {
                download.setNr(i + addNr);
            }
        }
    }

    public synchronized void numberStoppedDownloads() {
        this.stream().filter(download -> download.isStateInit()).forEach(download -> download.setNr(DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED));
    }

    private int getNextNumber() {
        int i = 1;
        for (final Download download : this) {
            if (download.getNr() < DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED && download.getNr() >= i) {
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
