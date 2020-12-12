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

import de.p2tools.mtplayer.controller.data.download.Download;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;

import java.util.ArrayList;

public class BandwidthData extends ArrayList<Long> {
    private Download download;
    private int downloadState = DownloadConstants.STATE_INIT;
    private String name = "";
    private int startTimeSec; //Startzeit in Sekunden
    private boolean isShowing = false;

    public BandwidthData(Download download, int startTimeSec) {
        this.download = download;
        this.startTimeSec = startTimeSec;
        this.add(0L);
        setDownloadState();
    }

    public Download getDownload() {
        return download;
    }

    public void setDownload(Download download) {
        setDownloadState();
        this.download = download;
        setDownloadState();
    }

    public int getDownloadState() {
        setDownloadState();
        return downloadState;
    }

    private void setDownloadState() {
        if (this.download != null) {
            downloadState = download.getState();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartTimeSec() {
        return startTimeSec;
    }

    public void removeFirst() {
        //ersten Wert entfernen und dann auch!! die Startzeit weiterschieben
        this.remove(0);
        this.startTimeSec += ChartFactory.DATA_ALL_SECONDS;
    }

    public int getTimeSec(int sec) {
        return startTimeSec + sec * ChartFactory.DATA_ALL_SECONDS;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public double getTimeMin(int sec) {
        return getTimeSec(sec) / 60.0;
    }

    public long getLast() {
        if (this.isEmpty()) {
            return 0;
        }

        return this.get(this.size() - 1);
    }

    public long removeLast() {
        if (this.isEmpty()) {
            return 0;
        }

        return this.remove(this.size() - 1);
    }
}
