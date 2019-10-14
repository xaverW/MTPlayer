/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.controller.data.download;

import de.mtplayer.mLib.tools.SizeTools;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.tools.log.PLog;

import java.text.DecimalFormat;

public class DownloadInfos {
    private int placedBack = 0; //Zurüchgestellt
    private int amount = 0; //Gesamtanzahl

    private int amountAbo = 0; //davon Abos
    private int amountDownload = 0; //und manuelle Downloads

    private int notStarted = 0; //davon gestartet, alle, egal ob warten, laden oder fertig
    private int started = 0; //davon gestartet, alle, egal ob warten, laden oder fertig

    private int startedNotLoading = 0; //davon gestartet, warten aber noch
    private int loading = 0; //laden schon
    private int finishedOk = 0; //fertig und Ok
    private int finishedError = 0; //fertig mit Fehler


    private int numberNotStartedDownloads = 0; //Anzahl aller noch nicht gestarteten Downloads
    private int numberWaitingDownloads = 0; //Anzahl aller gestarteten und wartenden Downloads
    private int numberLoadingDownloads = 0; //Anzahl aller ladenden Downloads

    private long byteNotStartedDownloads = 0; //anz. Bytes für alle noch nicht gestarteten Downloads
    private long byteWaitingDownloads = 0; //anz. Bytes für alle gestarteten und wartenden Downloads
    private long byteLoadingDownloads = 0; //anz. Bytes für alle ladenden Downloads
    private long byteLoadingDownloadsAlreadyLoaded = 0; //anz. Bytes bereits geladen für die gerade ladenden Downloads

    private long timeLeftNotStartedDownloads = 0; //Restzeit für alle noch nicht gestarteten Downloads
    private long timeLeftWaitingDownloads = 0; //Restzeit für alle gestarteten und wartenden Downloads
    private long timeLeftLoadingDownloads = 0; //Restzeit für alle ladenden Downloads
    private long bandwidth = 0; //Bandbreite: bytes per second
    private String bandwidthStr = "";
    private int percent = -1; // Prozent fertig (alle)


    private final ProgData progData;

    public DownloadInfos(ProgData progData) {
        this.progData = progData;
        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, DownloadInfos.class.getSimpleName()) {
            @Override
            public void ping() {
                clean();
                generateDownloadInfos();
                generateBandwidthInfo();
            }
        });
    }

    public int getPlacedBack() {
        return placedBack;
    }

    public synchronized int getAmount() {
        return amount;
    }

    public synchronized int getAmountAbo() {
        return amountAbo;
    }

    public int getNotStarted() {
        return notStarted;
    }

    public synchronized int getStarted() {
        return started;
    }

    public synchronized int getAmountDownload() {
        return amountDownload;
    }

    public synchronized int getStartedNotLoading() {
        return startedNotLoading;
    }

    public synchronized int getLoading() {
        return loading;
    }

    public synchronized int getFinishedOk() {
        return finishedOk;
    }

    public synchronized int getFinishedError() {
        return finishedError;
    }

    public int getNumberNotStartedDownloads() {
        return numberNotStartedDownloads;
    }

    public int getNumberWaitingDownloads() {
        return numberWaitingDownloads;
    }

    public int getNumberLoadingDownloads() {
        return numberLoadingDownloads;
    }

    public long getByteNotStartedDownloads() {
        return byteNotStartedDownloads;
    }

    public long getByteWaitingDownloads() {
        return byteWaitingDownloads;
    }

    public long getByteLoadingDownloads() {
        return byteLoadingDownloads;
    }

    public long getByteLoadingDownloadsAlreadyLoaded() {
        return byteLoadingDownloadsAlreadyLoaded;
    }

    public long getTimeLeftNotStartedDownloads() {
        return timeLeftNotStartedDownloads;
    }

    public long getTimeLeftWaitingDownloads() {
        return timeLeftWaitingDownloads;
    }

    public long getTimeLeftLoadingDownloads() {
        return timeLeftLoadingDownloads;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public String getBandwidthStr() {
        return bandwidthStr;
    }

    public int getPercent() {
        return percent;
    }

    public String getTimeLeftNotStarted() {
        return getTimeInfo(timeLeftNotStartedDownloads);
    }

    public String getTimeLeftWaiting() {
        return getTimeInfo(timeLeftWaitingDownloads);
    }

    public String getTimeLeftLoading() {
        return getTimeInfo(timeLeftLoadingDownloads);
    }

    private String getTimeInfo(long time) {
        if (time <= 0) {
            return "";
        }

        time = time / 60; // Minuten
        if (time < 0) {
            return "< 1 min";
        }

        if (time < 90) {
            return time + " min";
        }

        String m = time % 60 + "";
        while (m.length() < 2) {
            m = m + "0";
        }
        return time / 60 + ":" + m + " Stunden";
    }

    private synchronized void generateDownloadInfos() {
        // generiert die Anzahl Downloads (aus Downloads und Abos):
        // Anzahl alle Downloads,
        // davon Anz-Abo, Anz-Down,
        // davon gestartet und warten, laufen, fertig OK, fertig Fehler

        for (final Download download : progData.downloadList) {
            if (download.getPlacedBack()) {
                ++placedBack;
            } else {
                ++amount;
            }

            if (download.isAbo()) {
                ++amountAbo;
            } else {
                ++amountDownload;
            }

            if (download.isStarted() || download.isFinishedOrError()) {
                // final int quelle = download.getSource();
                if (download.getSource().equals(DownloadConstants.SRC_ABO) ||
                        download.getSource().equals(DownloadConstants.SRC_DOWNLOAD)) {

                    ++started;
                    if (download.isStateStartedWaiting()) {
                        ++startedNotLoading;
                    } else if (download.isStateStartedRun()) {
                        ++loading;
                    } else if (download.isStateFinished()) {
                        ++finishedOk;
                    } else if (download.isStateError()) {
                        ++finishedError;
                    }
                } else {
                    System.out.println("download.getSource().equals(DownloadConstants.SRC_ABO) ||\n" +
                            "                        download.getSource().equals(DownloadConstants.SRC_DOWNLOAD)");
                }

            } else {
                ++notStarted;
            }
        }
    }

    private synchronized void generateBandwidthInfo() {
        // Liste aller nicht gestarteten oder wartenden Downloads
        for (final Download download : progData.downloadList) {
            if (download.isStateInit()) {
                // noch nicht gestartet
                ++numberNotStartedDownloads;
                byteNotStartedDownloads += (download.getDownloadSize().getFilmSize() > 0 ? download.getDownloadSize().getFilmSize() : 0);

            } else if (download.isStateStartedWaiting()) {
                // gestartet und warten auf den Download
                ++numberWaitingDownloads;
                byteWaitingDownloads += (download.getDownloadSize().getFilmSize() > 0 ? download.getDownloadSize().getFilmSize() : 0);

            } else if (download.isStateStartedRun()) {
                // die Downlaods laufen gerade
                ++numberLoadingDownloads;
                byteLoadingDownloads += (download.getDownloadSize().getFilmSize() > 0 ? download.getDownloadSize().getFilmSize() : 0);

                bandwidth += download.getStart().getBandwidth(); // bytes per second
                if (bandwidth < 0) {
                    bandwidth = 0;
                }
                byteLoadingDownloadsAlreadyLoaded += (download.getDownloadSize().getAktFileSize() > 0 ? download.getDownloadSize().getAktFileSize() : 0);
                if (download.getStart().getTimeLeftSeconds() > timeLeftLoadingDownloads) {
                    // der längeste gibt die aktuelle Restzeit vor
                    timeLeftLoadingDownloads = download.getStart().getTimeLeftSeconds();
                }
            }
        }


        if (bandwidth > 0) {
            ProgConfig.DOWNLOAD_BANDWITH_KBYTE.setValue(bandwidth);

            // Restzeit laufende Downloads
            final long b = byteLoadingDownloads - byteLoadingDownloadsAlreadyLoaded;
            long timeLeft;

            if (b <= 0) {
                timeLeft = 0;
            } else {
                timeLeft = b / bandwidth;
            }
            if (timeLeft < timeLeftLoadingDownloads) {
                timeLeft = timeLeftLoadingDownloads; // falsch geraten oder es gibt nur einen
            }
            timeLeftLoadingDownloads = timeLeft;
        }

        final long resBandwidth = bandwidth > 0 ? bandwidth : ProgConfig.DOWNLOAD_BANDWITH_KBYTE.getLong();
        if (resBandwidth > 0) {
            // wartende Downloads
            if (byteWaitingDownloads <= 0) {
                timeLeftWaitingDownloads = 0;
            } else {
                timeLeftWaitingDownloads = byteWaitingDownloads / resBandwidth;
            }

            // nicht gestartete Downloads
            if (byteNotStartedDownloads <= 0) {
                timeLeftNotStartedDownloads = 0;
            } else {
                timeLeftNotStartedDownloads = byteNotStartedDownloads / resBandwidth;
            }
        }

        if (byteLoadingDownloads > 0) {
            percent = (int) (byteLoadingDownloadsAlreadyLoaded * 100 / byteLoadingDownloads);
            progressMsg();
        }

        roundBandwidth();
    }

    private synchronized void clean() {
        //DonwloadInfos
        placedBack = 0;
        amount = 0;
        amountAbo = 0;
        amountDownload = 0;
        notStarted = 0;
        started = 0;
        startedNotLoading = 0;
        loading = 0;
        finishedOk = 0;
        finishedError = 0;

        //BandwidthInfos
        numberNotStartedDownloads = 0;
        numberWaitingDownloads = 0;
        numberLoadingDownloads = 0;
        byteNotStartedDownloads = 0;
        byteWaitingDownloads = 0;
        byteLoadingDownloads = 0;
        byteLoadingDownloadsAlreadyLoaded = 0;
        timeLeftNotStartedDownloads = 0;
        timeLeftWaitingDownloads = 0;
        timeLeftLoadingDownloads = 0;
        bandwidth = 0;
        percent = -1;
    }

    private void progressMsg() {
        if (!ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_PROGRESS.getBool()) {
            return;
        }
        final int progress = percent;
        if (progress >= 0) {
            String text = "  [ ";
            final int a = progress / 10;
            for (int i = 0; i < a; ++i) {
                text += "#";
            }
            for (int i = 0; i < (10 - a); ++i) {
                text += "-";
            }
            text += " ]  " + SizeTools.getSize(byteLoadingDownloadsAlreadyLoaded) + " von " + SizeTools.getSize(byteLoadingDownloads) + " MByte /";
            text += " Downloads: " + numberLoadingDownloads + " /";
            text += " Bandbreite: " + roundBandwidth();
            PLog.progress(text);
        }
    }

    private String roundBandwidth() {
        if (bandwidth > 1_000_000.0) {
            bandwidthStr = new DecimalFormat("####0.00").format(bandwidth / 1_000_000.0) + " MByte/s";
        } else if (bandwidth > 1_000.0) {
            bandwidthStr = Math.round(bandwidth / 1_000.0) + " kByte/s";
        } else {
            bandwidthStr = Math.round(bandwidth) + " Byte/s";
        }
        return bandwidthStr;
    }
}
