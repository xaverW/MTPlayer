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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.starter.Start;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class DownloadListStarts {
    private final ProgData progData;
    private final DownloadList downloadList;
    private final LinkedList<Download> aktivDownloads = new LinkedList<>();

    public DownloadListStarts(ProgData progData, DownloadList downloadList) {
        this.progData = progData;
        this.downloadList = downloadList;
    }

    public synchronized int[] getStarts() {
        // liefert die Anzahl Starts die:
        // Anzahl, Anz-Abo, Anz-Down, nicht gestarted, laufen, fertig OK, fertig fehler
        // Downloads und Abos

        final int[] ret = new int[]{0, 0, 0, 0, 0, 0, 0};
        for (final Download download : downloadList) {
            if (!download.isZurueckgestellt()) {
                ++ret[0];
            }
            if (download.isAbo()) {
                ++ret[1];
            } else {
                ++ret[2];
            }
            if (download.isStarted() || download.isFinishedOrError()) {
                // final int quelle = download.getSource();
                if (download.getSource().equals(DownloadInfos.SRC_ABO) || download.getSource().equals(DownloadInfos.SRC_DOWNLOAD)) {
                    if (download.isStateStartedWaiting()) {
                        ++ret[3];
                    } else if (download.isStateStartedRun()) {
                        ++ret[4];
                    } else if (download.isStateFinished()) {
                        ++ret[5];
                    } else if (download.isStateError()) {
                        ++ret[6];
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Return the number of Starts, which are queued in state INIT or RUN.
     *
     * @return number of queued Starts.
     */
    public synchronized int getNumberOfStartsNotFinished() {
        // todo?? wird aber nicht benutzt
        for (final Download datenDownload : downloadList) {
            final Start s = datenDownload.getStart();
            if (datenDownload.isStarted()) {
                return downloadList.size();
            }
        }
        return 0;
    }

    /**
     * Return the maximum time of all running starts until finish.
     *
     * @return The time in SECONDS.
     */
    public synchronized long getMaximumFinishTimeOfRunningStarts() {
        long rem = 0;
        for (final Download d : downloadList) {
            final Start s = d.getStart();
            if (d.isStarted()) {
                rem = Math.max(rem, s.getTimeLeft());
            }
        }

        return rem;
    }

    public Download getRestartDownload() {
        // Versuch einen Fehlgeschlagenen Download zu finden um ihn wieder zu starten
        // die Fehler laufen aber einzeln, vorsichtshalber

        if (!getDown(1)) {
            // dann läuft noch einer
            return null;
        }

        for (final Download download : downloadList) {
            if (download.isStateInit()) {
                continue;
            }

            if (download.isStateError()
                    && download.getStart().getRestartCounter() < ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getInt()
                    && !maxChannelPlay(download, 1)) {

                int restarted = download.getStart().getRestartCounter();
                if (download.getArt().equals(DownloadInfos.ART_DOWNLOAD)) {
                    download.restartDownload();
                    progData.downloadList.startDownloads(download);
                    // UND jetzt den Restartcounter wieder setzen!!
                    download.getStart().setRestartCounter(++restarted);
                    return download;
                }
            }
        }
        return null;
    }

    /**
     * Return a List of all not yet finished downloads.
     *
     * @param quelle Use QUELLE_XXX constants from {@link de.mtplayer.mtp.controller.starter.Start}.
     * @return A list with all download objects.
     */
    public synchronized LinkedList<Download> getListOfStartsNotFinished(String quelle) {

        aktivDownloads.clear();
        aktivDownloads.addAll(downloadList.stream().filter(download -> download.isStateStartedRun())
                .filter(download -> quelle.equals(DownloadInfos.SRC_ALL) || download.getSource().equals(quelle))
                .collect(Collectors.toList()));
        return aktivDownloads;
    }

    public synchronized void buttonStartsPutzen() {
        // Starts durch Button die fertig sind, löschen
        boolean gefunden = false;
        final Iterator<Download> it = downloadList.iterator();
        while (it.hasNext()) {
            final Download d = it.next();
            if (d.isStateFinished()) {
                if (d.getSource().equals(DownloadInfos.SRC_BUTTON)) {
                    // dann ist er fertig oder abgebrochen
                    it.remove();
                    gefunden = true;
                }
            }
        }
        if (gefunden) {
//            Listener.notify(Listener.EREIGNIS_START_EVENT_BUTTON, this.getClass().getSimpleName());
        }
    }

    public synchronized Download getNextStart() {
        // erste passende Element der Liste zurückgeben oder null
        // und versuchen dass bei mehreren laufenden Downloads ein anderer Sender gesucht wird
        Download ret = null;
        if (downloadList.size() > 0 && getDown(ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getInt())) {
            final Download datenDownload = naechsterStart();
            if (datenDownload != null && datenDownload.isStateStartedWaiting()) {
                ret = datenDownload;
            }
        }
        return ret;
    }

    private Download naechsterStart() {
        int nr = -1;
        Download tmpDownload = null;

        // erster Versuch, Start mit einem anderen Sender
        for (Download download : downloadList) {
            if (download.isStateStartedWaiting() &&
                    !maxChannelPlay(download, 1) &&
                    (nr == -1 || download.getNr() < nr)) {
                tmpDownload = download;
                nr = tmpDownload.getNr();
            }
        }
        if (tmpDownload != null) {
            return tmpDownload;
        }


        // zweiter Versuch, Start mit einem passenden Sender
        nr = -1;
        int maxProChannel = ProgConst.MAX_SENDER_FILME_LADEN;
        if (Boolean.parseBoolean(ProgConfig.DOWNLOAD_MAX_ONE_PER_SERVER.get())) {
            // dann darf nur ein Download pro Server gestartet werden
            maxProChannel = 1;
        }
        for (Download download : downloadList) {
            if (download.isStateStartedWaiting() &&
                    !maxChannelPlay(download, maxProChannel) &&
                    (nr == -1 || download.getNr() < nr)) {
                tmpDownload = download;
                nr = tmpDownload.getNr();
            }
        }

        return tmpDownload;
    }

    private boolean maxChannelPlay(Download d, int max) {
        // true wenn bereits die maxAnzahl pro Sender läuft
        try {
            int counter = 0;
            final String host = getHost(d);
            for (final Download download : downloadList) {
                if (download.isStateStartedRun() && getHost(download).equalsIgnoreCase(host)) {
                    counter++;
                    if (counter >= max) {
                        return true;
                    }
                }
            }
            return false;
        } catch (final Exception ex) {
            return false;
        }
    }

    private String getHost(Download datenDownload) {
        String host = "";
        try {
            try {
                String uurl = datenDownload.getUrl();
                // die funktion "getHost()" kann nur das Protokoll "http" ??!??
                if (uurl.startsWith("rtmpt:")) {
                    uurl = uurl.toLowerCase().replace("rtmpt:", "http:");
                }
                if (uurl.startsWith("rtmp:")) {
                    uurl = uurl.toLowerCase().replace("rtmp:", "http:");
                }
                if (uurl.startsWith("mms:")) {
                    uurl = uurl.toLowerCase().replace("mms:", "http:");
                }
                final URL url = new URL(uurl);
                String tmp = url.getHost();
                if (tmp.contains(".")) {
                    host = tmp.substring(tmp.lastIndexOf('.'));
                    tmp = tmp.substring(0, tmp.lastIndexOf('.'));
                    if (tmp.contains(".")) {
                        host = tmp.substring(tmp.lastIndexOf('.') + 1) + host;
                    } else if (tmp.contains("/")) {
                        host = tmp.substring(tmp.lastIndexOf('/') + 1) + host;
                    } else {
                        host = "host";
                    }
                }
            } catch (final Exception ex) {
                // für die Hosts bei denen das nicht klappt
                // Log.systemMeldung("getHost 1: " + s.download.arr[DatenDownload.DOWNLOAD_URL_NR]);
                host = "host";
            } finally {
                if (host.isEmpty()) {
                    // Log.systemMeldung("getHost 3: " + s.download.arr[DatenDownload.DOWNLOAD_URL_NR]);
                    host = "host";
                }
            }
        } catch (final Exception ex) {
            // Log.systemMeldung("getHost 4: " + s.download.arr[DatenDownload.DOWNLOAD_URL_NR]);
            host = "exception";
        }
        return host;
    }

    private boolean getDown(int max) {
        int count = 0;
        for (final Download datenDownload : downloadList) {
            if (datenDownload.isStateStartedRun()) {
                ++count;
                if (count >= max) {
                    return false;
                }
            }
        }
        return true;
    }

}
