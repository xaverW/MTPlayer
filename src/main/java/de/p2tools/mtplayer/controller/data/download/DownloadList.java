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
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.configfile.pdata.PDataList;
import de.p2tools.p2lib.tools.PGetList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DownloadList extends SimpleListProperty<DownloadData> implements PDataList<DownloadData> {

    public static final String TAG = "DownloadList";
    private final ProgData progData;
    private final ObservableList<DownloadData> undoList = FXCollections.observableArrayList();
    private final BooleanProperty downloadsChanged = new SimpleBooleanProperty(true);

    public DownloadList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Downloads";
    }

    @Override
    public DownloadData getNewItem() {
        return new DownloadData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(DownloadData.class)) {
            add((DownloadData) obj);
        }
    }

    public synchronized void initDownloads() {
        this.forEach(download -> {
            download.setFile(download.getDestPathFile());

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

    public synchronized void addWithNo(List<DownloadData> list) {
        list.stream().forEach(download -> super.add(download));
        setNumbersInList();
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

    public ObservableList<DownloadData> getUndoList() {
        return undoList;
    }

    public synchronized void addDownloadsToUndoList(List<DownloadData> list) {
        undoList.clear();
        undoList.addAll(list);
    }

    public synchronized void undoDownloads() {
        if (undoList.isEmpty()) {
            return;
        }
        //aus der Abo-History löschen
        progData.historyListAbos.removeDownloadDataFromHistory(undoList);
        addAll(undoList);
        undoList.clear();
    }

    public synchronized int countStartedAndRunningDownloads() {
        // es wird nach noch nicht fertigen, gestarteten Downloads gesucht
        int ret = 0;
        for (final DownloadData download : this) {
            if (download.isStateStartedWaiting() || download.isStateStartedRun()) {
                ++ret;
            }
        }
        return ret;
    }

    public synchronized void preferDownloads(ArrayList<DownloadData> prefDownList) {
        DownloadFactory.preferDownloads(this, prefDownList);
    }

    public synchronized DownloadData getDownloadWithFilmUrl(String urlFilm) {
        for (final DownloadData dataDownload : this) {
            if (dataDownload.getFilmUrlNormal().equals(urlFilm)) {
                return dataDownload;
            }
        }
        return null;
    }

    /**
     * search downloads for abos after loading a filmlist
     */
    public synchronized void searchForDownloadsFromAbos() {
        final int count = getSize();
        DownloadFactoryAbo.searchDownloadsFromAbos(this);
        if (getSize() == count) {
            // dann wurden evtl. nur zurückgestellte Downloads wieder aktiviert
            setDownloadsChanged();
        }
    }

    public synchronized List<DownloadData> getListOfStartsNotFinished(String source) {
        return DownloadFactoryStarts.getListOfStartsNotFinished(this, source);
    }

    public synchronized List<DownloadData> getListOfStartsNotLoading(String source) {
        return DownloadFactoryStarts.getListOfStartsNotLoading(this, source);
    }

    public synchronized void cleanUpButtonStarts() {
        DownloadFactoryStarts.cleanUpButtonStarts(this);
    }

    public synchronized DownloadData getNextStart() {
        return DownloadFactoryStarts.getNextStart(this);
    }

    public synchronized void resetPlacedBack() {
        // zurückgestellte wieder aktivieren
        forEach(d -> d.setPlacedBack(false));
    }

    // ==============================
    // DownloadListStartStop
    public synchronized void stopDownloads(List<DownloadData> list) {
        // Aufruf aus den Menüs
        if (DownloadFactoryStopDownload.stopDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void delDownloads(DownloadData download) {
        // aus dem Menü
        DownloadFactoryStopDownload.delDownloads(this, new PGetList<DownloadData>().getArrayList(download));
    }

    public synchronized void delDownloads(ArrayList<DownloadData> list) {
        // aus dem Menü
        if (DownloadFactoryStopDownload.delDownloads(this, list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void putBackDownloads(ArrayList<DownloadData> list) {
        if (DownloadFactoryStopDownload.putBackDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public void startAllDownloads() {
        startDownloads(this, false);
    }

    public void startDownloads(DownloadData download) {
        DownloadFactoryStartDownload.startDownloads(this,
                new PGetList<DownloadData>().getArrayList(download));
        setDownloadsChanged();
    }

    public void startDownloads(Collection<DownloadData> list, boolean alsoFinished) {
        if (DownloadFactoryStartDownload.startDownloads(this, list, alsoFinished)) {
            setDownloadsChanged();
        }
    }

    public synchronized void setNumbersInList() {
        int i = getNextNumber();
        for (final DownloadData download : this) {
            if (download.isStarted()) {
                // gestartete Downloads ohne!! Nummer nummerieren
                if (download.getNo() == P2LibConst.NUMBER_NOT_STARTED) {
                    download.setNo(i++);
                }

            } else {
                // nicht gestartete Downloads
                download.setNo(P2LibConst.NUMBER_NOT_STARTED);
            }
        }
    }

    public synchronized void renumberList(int addNr) {
        for (final DownloadData download : this) {
            final int i = download.getNo();
            if (i < P2LibConst.NUMBER_NOT_STARTED) {
                download.setNo(i + addNr);
            }
        }
    }

    private int getNextNumber() {
        int i = 1;
        for (final DownloadData download : this) {
            if (download.getNo() < P2LibConst.NUMBER_NOT_STARTED && download.getNo() >= i) {
                i = download.getNo() + 1;
            }
        }
        return i;
    }

    public synchronized void addNumber(ArrayList<DownloadData> downloads) {
        int i = getNextNumber();
        for (DownloadData download : downloads) {
            download.setNo(i++);
        }
    }
}
