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
import de.p2tools.p2lib.mtdownload.MLInputStream;
import de.p2tools.p2lib.tools.date.P2Date;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartDownloadDto {

    private int startCounter = 0;
    private boolean startViewing = false;

    private long timeLeftSeconds = -1; // restliche Laufzeit [s] des Downloads
    private long downloaded = 0;
    private boolean deleteAfterStop = false;

    private Process process = null; // Prozess des Downloads
    private P2Date startTime = null; // Zeit, zu der der Download tatsächlich gestartet wurde
    private MLInputStream inputStream = null;
    private File file = null;
    private final List<String> errMsgList = new ArrayList<>();

    private final DownloadData download; //Referenz auf den Download dazu

    public StartDownloadDto(DownloadData download) {
        this.download = download;
    }

    public long getTimeLeftSeconds() {
        return timeLeftSeconds;
    }

    public void setTimeLeftSeconds(int timeLeftSeconds) {
        this.timeLeftSeconds = timeLeftSeconds;
        if (download.isStateStartedRun() && getTimeLeftSeconds() > 0) {
            download.setRemaining(timeLeftSeconds);
        } else {
            download.setRemaining(DownloadConstants.REMAINING_NOT_STARTET);
        }
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isDeleteAfterStop() {
        return deleteAfterStop;
    }

    public void setDeleteAfterStop(boolean deleteAfterStop) {
        this.deleteAfterStop = deleteAfterStop;
    }

    public void startDownload() {
        setStartTime(new P2Date());
    }

    public boolean isStartViewing() {
        return startViewing;
    }

    public void setStartViewing(boolean startViewing) {
        this.startViewing = startViewing;
    }

    public P2Date getStartTime() {
        return startTime;
    }

    public void setStartTime(P2Date startTime) {
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

    public void addStartCounter() {
        ++startCounter;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<String> getErrMsgList() {
        return errMsgList;
    }
}
