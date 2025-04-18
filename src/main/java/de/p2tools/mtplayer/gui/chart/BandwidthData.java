/*
 * P2tools Copyright (C) 2020 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.chart;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.p2event.P2EventHandler;

import java.util.Arrays;

public class BandwidthData {
    private DownloadData download;
    private int downloadState = DownloadConstants.STATE_INIT;
    private String name = "";
    private long startTimeSec = -1; // Startzeit in Sekunden
    private boolean showing = false;
    private boolean downloading = true;
    private int[] data = new int[BandwidthDataFactory.MAX_DATA]; // sind kByte!!!!

    public BandwidthData(DownloadData download) {
        this.download = download;
        setDownloadState();
    }

    public void setDownload(DownloadData download) {
        this.download = download;
        setDownloadState();
    }

    public DownloadData getDownload() {
        return download;
    }

    private void setDownloadState() {
        if (this.download != null) {
            downloadState = download.getState();
        }
    }

    public int getDownloadState() {
        setDownloadState();
        return downloadState;
    }

    public void setStartTimeNow() {
        this.startTimeSec = P2EventHandler.countRunningTimeSeconds;
        cleanUpData();
    }

    public long getStartTimeSec() {
        return startTimeSec;
    }

    public void cleanUpData() {
        Arrays.fill(data, 0);
    }

    private int tmpValue = 0;

    public void addData(long a) {
        // wenn der erste Wert, dann Startzeit setzen
        if (startTimeSec < 0) {
            setStartTimeNow();
        }

        tmpValue += (int) (a / 1000); // data sind kByte!!!!!!!!
        if (BandwidthDataFactory.GET_DATA_COUNT >= BandwidthDataFactory.DATA_ALL_SECONDS - 1) {
            // neuer Wert wird der LETZTE in der Liste
            for (int i = 1; i < data.length; ++i) {
                data[i - 1] = data[i];
            }
            data[data.length - 1] = tmpValue / BandwidthDataFactory.DATA_ALL_SECONDS;
            tmpValue = 0;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowing() {
        return showing;
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public int[] getData() {
        return data;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    public boolean isEmpty() {
        for (int datum : data) {
            if (datum > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean allValuesEmpty() {
        for (int datum : data) {
            if (datum > 0) {
                return false;
            }
        }
        return true;
    }

    public int getMaxValue() {
        // liefert den MAX im angezeigten Zeitbereich
        int max = 0;
        for (int i = getMaxFirstIdx(); i < data.length; ++i) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    private int getMaxFirstIdx() {
        final int first = data.length -
                (ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN.get() * 60) / BandwidthDataFactory.DATA_ALL_SECONDS;
        return Math.max(first, 0);
    }
}