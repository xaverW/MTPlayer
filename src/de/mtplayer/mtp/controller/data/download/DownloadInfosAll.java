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

import de.mtplayer.mLib.tools.SizeTools;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.p2tools.p2Lib.tools.log.PLog;

import java.text.DecimalFormat;
import java.util.LinkedList;

public class DownloadInfosAll {

    private final ProgData progData;
    private final DownloadList downloadList;

    // Anzahl
    public int anzDownloadsRun = 0; //Anzahl gestarteter Downloads
    // Größe
    public long byteAlleDownloads = 0; //anz. Bytes für alle gestarteten Downloads
    public long byteAktDownloads = 0; //anz. Bytes bereits geladen für die gerade ladenden/laufenden Downloads
    // Zeit
    public long timeRestAktDownloads = 0; //Restzeit für die gerade ladenden/laufenden Downloads
    public long timeRestAllDownloads = 0; // Restzeit aller gestarteten Downloads
    // Bandbreite
    public long bandwidth = 0; //Bandbreite: bytes per second
    public String bandwidthStr = "";
    // Prozent fertig (alle)
    public int percent = -1;

    // Anzahl, Anz-Abo, Anz-Down, nicht gestarted, laufen, fertig OK, fertig fehler
    public int[] downloadStarts = new int[]{0, 0, 0, 0, 0, 0, 0};

    private LinkedList<Download> aktivDownloads; // Liste gestarteter Downloads

    public DownloadInfosAll(ProgData progData, DownloadList downloadList) {
        this.progData = progData;
        this.downloadList = downloadList;
    }

    public String roundBandwidth(long time) {
        roundBandwidth();
        if (bandwidth > 1_000_000.0) {
            return time / 60 + ":" + (time % 60 < 10 ? "0" + time % 60 : time % 60) + " Minuten / " + bandwidthStr;
        } else if (bandwidth > 1_000.0) {
            return time / 60 + ":" + (time % 60 < 10 ? "0" + time % 60 : time % 60) + " Minuten / " + bandwidthStr;
        } else {
            return time / 60 + ":" + (time % 60 < 10 ? "0" + time % 60 : time % 60) + " Minuten / " + bandwidthStr;
        }
    }

    public String roundBandwidth() {
        if (bandwidth > 1_000_000.0) {
            bandwidthStr = new DecimalFormat("####0.00").format(bandwidth / 1_000_000.0) + " MByte/s";
        } else if (bandwidth > 1_000.0) {
            bandwidthStr = Math.round(bandwidth / 1_000.0) + " kByte/s";
        } else {
            bandwidthStr = Math.round(bandwidth) + " Byte/s";
        }
        return bandwidthStr;
    }

    public String getGesamtRestzeit() {
        if (timeRestAllDownloads > 0) {
            if (timeRestAllDownloads < 60) {
                return "< 1 Min";
            } else {
                return Long.toString(timeRestAllDownloads / 60) + " Min";
            }
        }
        return "";
    }

    public String getRestzeit() {
        if (timeRestAktDownloads > 0) {
            if (timeRestAktDownloads < 60) {
                return "< 1 Min";
            } else {
                return Long.toString(timeRestAktDownloads / 60) + " Min";
            }
        }
        return "";
    }

    synchronized void makeDownloadInfos() {
        clean();

        downloadStarts = downloadList.getStarts();

        aktivDownloads = downloadList.getListOfStartsNotFinished(DownloadInfos.SRC_ALL);
        for (final Download download : aktivDownloads) {
            ++anzDownloadsRun;
            byteAlleDownloads += (download.getDownloadSize().getFilmSize() > 0 ? download.getDownloadSize().getFilmSize() : 0);
            if (download.isStateStartedRun()) {
                // die Downlaods laufen gerade
                bandwidth += download.getStart().getBandwidth(); // bytes per second
                byteAktDownloads += (download.getDownloadSize().getAktFileSize() > 0 ? download.getDownloadSize().getAktFileSize() : 0);
                if (download.getStart().getTimeLeft() > timeRestAktDownloads) {
                    // der längeste gibt die aktuelle Restzeit vor
                    timeRestAktDownloads = download.getStart().getTimeLeft();
                }
            }
        }

        if (bandwidth < 0) {
            bandwidth = 0;
        }

        if (bandwidth > 0) {
            // sonst macht die Restzeit keinen Sinn
            final long b = byteAlleDownloads - byteAktDownloads;
            if (b <= 0) {
                timeRestAllDownloads = 0;
            } else {
                timeRestAllDownloads = b / bandwidth;
            }
            if (timeRestAllDownloads < timeRestAktDownloads) {
                timeRestAllDownloads = timeRestAktDownloads; // falsch geraten oder es gibt nur einen
            }
            if (anzDownloadsRun == 1) {
                timeRestAllDownloads = 0; // gibt ja nur noch einen
            }
        }
        if (byteAlleDownloads > 0) {
            percent = (int) (byteAktDownloads * 100 / byteAlleDownloads);
            progressMsg();
        }
        roundBandwidth();
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
            text += " ]  " + SizeTools.getGroesse(byteAktDownloads) + " von " + SizeTools.getGroesse(byteAlleDownloads) + " MByte /";
            text += " Downloads: " + anzDownloadsRun + " /";
            text += " Bandbreite: " + roundBandwidth();
            PLog.progress(text);
        }
    }

    private void clean() {
        anzDownloadsRun = 0;
        byteAlleDownloads = 0;
        byteAktDownloads = 0;
        timeRestAktDownloads = 0;
        timeRestAllDownloads = 0;
        bandwidth = 0;
        percent = -1;
    }

}
