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
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.*;

public class DownloadList extends SimpleListProperty<Download> {

    private final ProgData progData;
    private final DownloadListAbo downloadListAbo;
    private final DownloadListStarts downloadListStarts;
    private final DownloadListStartStop downloadListStartStop;

    private BooleanProperty downloadsChanged = new SimpleBooleanProperty(true);

    public DownloadList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
        this.downloadListAbo = new DownloadListAbo(progData, this);
        this.downloadListStarts = new DownloadListStarts(progData, this);
        this.downloadListStartStop = new DownloadListStartStop(progData, this);
    }

    public boolean getDownloadsChanged() {
        return downloadsChanged.get();
    }

    synchronized void setDownloadsChanged() {
        downloadsChanged.set(!downloadsChanged.get());
    }

    public BooleanProperty downloadsChangedProperty() {
        return downloadsChanged;
    }

    public synchronized void initDownloads() {
        this.stream().forEach(download -> {
            //ist bei gespeicherten Downloads der Fall
            SetData setData = progData.setDataList.getSetDataForDownloads(download.getSetDataId());
            if (setData != null) { //todo und dann??
                download.setSetData(setData, false);
            }
        });
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

    public synchronized void addWithNr(List<Download> list) {
        list.stream().forEach(download -> super.add(download));
        setNumbersInList();
    }

    @Override
    public synchronized boolean removeAll(Collection<?> objects) {
        return super.removeAll(objects);
    }

    public synchronized int countStartedAndRunningDownloads() {
        // es wird nach noch nicht fertigen, gestarteten Downloads gesucht
        int ret = 0;
        for (final Download download : this) {
            if (download.isStateStartedWaiting() || download.isStateStartedRun()) {
                ++ret;
            }
        }
        return ret;
    }


    public synchronized void addFilmInList() {

        // bei einmal Downloads nach einem Programmstart/Neuladen der Filmliste
        // den Film wieder eintragen
        PDuration.counterStart("DownloadList.addFilmInList");

        int counter = 50; //todo das dauert sonst viel zu lang
        for (Download d : this) {
            --counter;
            if (counter < 0) {
                break;
            }
            d.setFilm(progData.filmlist.getFilmByUrl_small_high_hd(d.getUrl())); //todo sollen da wirklich alle Filmfelder gesetzt werden??
            d.setSizeDownloadFromFilm();
        }

//        counter = 50;
//        parallelStream().filter(d -> {
//            counter -= 1;
//            System.out.println(counter);
//            if ((counter > 0)/* && d.getFilm() == null*/) {
//                return true;
//            } else {
//                return false;
//            }
//        }).forEach(d -> {
//            d.setFilm(progData.filmlist.getFilmByUrl_small_high_hd(d.getUrl())); //todo sollen da wirklich alle Filmfelder gesetzt werden??
//            d.setSizeDownloadFromFilm();
//
//        });
        PDuration.counterStop("DownloadList.addFilmInList");
    }


    public synchronized void preferDownloads(ArrayList<Download> prefDownList) {
        // macht nur Sinn, wenn der Download auf Laden wartet: Init
        // todo auch bei noch nicht gestarteten ermöglichen
        prefDownList.removeIf(d -> d.getState() != DownloadConstants.STATE_STARTED_WAITING);
        if (prefDownList.isEmpty()) {
            return;
        }

        // zum neu nummerieren der alten Downloads
        List<Download> list = new ArrayList<>();
        for (final Download download : this) {
            final int i = download.getNr();
            if (i < DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED) {
                list.add(download);
            }
        }
        prefDownList.stream().forEach(d -> list.remove(d));
        Collections.sort(list, new Comparator<Download>() {
            @Override
            public int compare(Download d1, Download d2) {
                return (d1.getNr() < d2.getNr()) ? -1 : 1;
            }
        });
        int addNr = prefDownList.size();
        for (final Download download : list) {
            ++addNr;
            download.setNr(addNr);
        }

        // und jetzt die vorgezogenen Downloads nummerieren
        int i = 1;
        for (final Download dataDownload : prefDownList) {
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


    /**
     * search downloads for abos after loading a filmlist
     */
    public synchronized void searchForDownloadsFromAbos() {
        final int count = getSize();
        downloadListAbo.searchDownloadsFromAbos();
        if (getSize() == count) {
            // dann wurden evtl. nur zurückgestellte Downloads wieder aktiviert
            setDownloadsChanged();
        }
    }

//    public synchronized int getNumberOfStartsNotFinished() {
//        return downloadListStarts.getNumberOfStartsNotFinished();
//    }
//
//    public synchronized long getMaximumFinishTimeOfRunningStarts() {
//        return downloadListStarts.getMaximumFinishTimeOfRunningStarts();
//    }

    public synchronized List<Download> getListOfStartsNotFinished(String source) {
        return downloadListStarts.getListOfStartsNotFinished(source);
    }

    public synchronized List<Download> getListOfStartsNotLoading(String source) {
        return downloadListStarts.getListOfStartsNotLoading(source);
    }

    public synchronized Download getRestartDownload() {
        return downloadListStarts.getRestartDownload();
    }


    public synchronized void cleanUpButtonStarts() {
        downloadListStarts.cleanUpButtonStarts();
    }

    public synchronized Download getNextStart() {
        return downloadListStarts.getNextStart();
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

    public void startDownloads(Collection<Download> list) {
        if (downloadListStartStop.startDownloads(list, false)) {
            setDownloadsChanged();
        }
    }

    public void startDownloads(Collection<Download> list, boolean alsoFinished) {
        if (downloadListStartStop.startDownloads(list, alsoFinished)) {
            setDownloadsChanged();
        }
    }

//    // ======================================
//    // DownloadInfosAll
//    public synchronized void makeDownloadInfo() {
//        downloadListInfoAll.makeDownloadInfo();
//    }


    public synchronized void setNumbersInList() {
        int i = getNextNumber();
        for (final Download download : this) {
            if (download.isStarted()) {
                // gestartete Downloads ohne!! Nummer nummerieren
                if (download.getNr() == DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED) {
                    download.setNr(i++);
                }

            } else {
                // nicht gestartete Downloads
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

    private int getNextNumber() {
        int i = 1;
        for (final Download download : this) {
            if (download.getNr() < DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED && download.getNr() >= i) {
                i = download.getNr() + 1;
            }
        }
        return i;
    }

    public synchronized void addNumber(ArrayList<Download> downloads) {
        int i = getNextNumber();
        for (Download download : downloads) {
            download.setNr(i++);
        }
    }
}
