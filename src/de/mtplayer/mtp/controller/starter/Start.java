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

package de.mtplayer.mtp.controller.starter;

import de.mtplayer.mLib.tools.MDate;
import de.mtplayer.mLib.tools.MLInputStream;
import de.mtplayer.mLib.tools.SizeTools;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;

public class Start {

    private int startCounter = 0;
    private int restartCounter = 0;

    private boolean startViewing = false;

    private long bandwidth = -1; // Downloadbandbreite: bytes per second
    private long timeLeft = -1; // restliche Laufzeit des Downloads

    private Process process = null; //Prozess des Download
    private MDate startTime = null;
    private MLInputStream inputStream = null;

    private Download download; //Referenz auf den Download dazu

    public Start(Download download) {
        this.download = download;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
        download.setBandwidth(SizeTools.humanReadableBandwidth(bandwidth));
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
        if (download.isStateStartedRun() && getTimeLeft() > 0) {
            download.setRemaining(DownloadConstants.getTimeLeft(timeLeft));
        } else {
            download.setRemaining("");
        }
    }

    public void startDownload() {
        setStartTime(new MDate());
    }

    public boolean isStartViewing() {
        return startViewing;
    }

    public void setStartViewing(boolean startViewing) {
        this.startViewing = startViewing;
    }

    public MDate getStartTime() {
        return startTime;
    }

    public void setStartTime(MDate startTime) {
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
