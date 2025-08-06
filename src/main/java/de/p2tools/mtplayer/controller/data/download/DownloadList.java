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
import de.p2tools.p2lib.configfile.pdata.P2DataList;
import de.p2tools.p2lib.tools.P2GetList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DownloadList extends SimpleListProperty<DownloadData> implements P2DataList<DownloadData> {

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
            DownloadData d = (DownloadData) obj;
            // cleanUp
            d.setPlacedBack(false);
            add(d);
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
        super.addAll(list);
        setNumbersInList();
    }

    public boolean getDownloadsChanged() {
        return downloadsChanged.get();
    }

    public synchronized void setDownloadsChanged() {
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

    public synchronized int countRunningDownloads() {
        int count = 0;
        for (final DownloadData download : this) {
            if (download.isStateStartedRun()) {
                ++count;
            }
        }
        return count;
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

    public synchronized void resetPlacedBack() {
        // zurückgestellte wieder aktivieren
        forEach(d -> d.setPlacedBack(false));
    }

    public DownloadList getCopyForSaving() {
        // unterbrochene werden gespeichert, dass die Info "Interrupt" erhalten bleibt
        // Download, (Abo müssen neu angelegt werden)

        DownloadList dl = new DownloadList(progData);
        dl.addAll(this);
        dl.removeIf(download -> (!download.isStateStopped() && (download.isAbo() ||
                download.isStateFinished())));

        return dl;
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
        // Button in der Tabelle-Downloads, Download noch nicht abgeschlossen
        DownloadFactoryStopDownload.delDownloads(this, new P2GetList<DownloadData>().getArrayList(download));
    }

    public synchronized void delDownloads(ArrayList<DownloadData> list) {
        // aus dem Menü: Tabelle-Kontext-Menü, Menü-Button
        if (DownloadFactoryStopDownload.delDownloads(this, list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void putBackDownloads(ArrayList<DownloadData> list) {
        if (DownloadFactoryStopDownload.putBackDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void startAllDownloads() {
        startDownloads(this, false);
    }

    public synchronized void startDownloads(DownloadData download) {
        // Menü/Button
        DownloadFactoryStartDownload.startDownloads(this,
                new P2GetList<DownloadData>().getArrayList(download));
        setDownloadsChanged();
    }

    public synchronized void startDownloads(Collection<DownloadData> list, boolean alsoFinished) {
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
