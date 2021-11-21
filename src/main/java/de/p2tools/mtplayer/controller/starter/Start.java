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

package de.p2tools.mtplayer.controller.starter;

import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.tools.MLInputStream;
import de.p2tools.mtplayer.tools.SizeTools;
import de.p2tools.p2Lib.tools.date.PDate;

public class Start {

    private int startCounter = 0;
    private int restartCounter = 0; // zählt die Anzahl der Neustarts bei einem Downloadfeheler->Summe Starts = erster Start + Restarts

    private boolean startViewing = false;

    private long bandwidth = -1; // Downloadbandbreite: bytes per second
    private long timeLeftSeconds = -1; // restliche Laufzeit [s] des Downloads

    private Process process = null; //Prozess des Download
    private PDate startTime = null;
    private MLInputStream inputStream = null;

    private DownloadData download; //Referenz auf den Download dazu

    public Start(DownloadData download) {
        this.download = download;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
        download.setBandwidth(SizeTools.humanReadableByteCount(bandwidth, true));
    }

    public long getTimeLeftSeconds() {
        return timeLeftSeconds;
    }

    public void setTimeLeftSeconds(long timeLeftSeconds) {
        this.timeLeftSeconds = timeLeftSeconds;
        if (download.isStateStartedRun() && getTimeLeftSeconds() > 0) {
            download.setRemaining(DownloadConstants.getTimeLeft(timeLeftSeconds));
        } else {
            download.setRemaining("");
        }
    }

    public void startDownload() {
        setStartTime(new PDate());
    }

    public boolean isStartViewing() {
        return startViewing;
    }

    public void setStartViewing(boolean startViewing) {
        this.startViewing = startViewing;
    }

    public PDate getStartTime() {
        return startTime;
    }

    public void setStartTime(PDate startTime) {
        this.startTime = startTime;
    }

    public MLInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(MLInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int getStartCounter() {
        return startCounter;
    }

    public void setStartCounter(int startCounter) {
        this.startCounter = startCounter;
    }

    public int getRestartCounter() {
        return restartCounter;
    }

    public void setRestartCounter(int restartCounter) {
        this.restartCounter = restartCounter;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
