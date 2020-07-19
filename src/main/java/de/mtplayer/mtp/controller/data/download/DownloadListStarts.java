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
import de.p2tools.p2Lib.tools.log.PLog;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadListStarts {
    private final ProgData progData;
    private final DownloadList downloadList;

    public DownloadListStarts(ProgData progData, DownloadList downloadList) {
        this.progData = progData;
        this.downloadList = downloadList;
    }

//    /**
//     * Return the number of Starts, which are queued in state INIT or RUN.
//     *
//     * @return number of queued Starts.
//     */
//    public synchronized int getNumberOfStartsNotFinished() {
//        // todo?? wird aber nicht benutzt
//        for (final Download dataDownload : downloadList) {
//            final Start s = dataDownload.getStart();
//            if (dataDownload.isStarted()) {
//                return downloadList.size();
//            }
//        }
//        return 0;
//    }

//    /**
//     * Return the maximum time of all running starts until finish.
//     *
//     * @return The time in SECONDS.
//     */
//    public synchronized long getMaximumFinishTimeOfRunningStarts() {
//        long rem = 0;
//        for (final Download d : downloadList) {
//            final Start s = d.getStart();
//            if (d.isStarted()) {
//                rem = Math.max(rem, s.getTimeLeftSeconds());
//            }
//        }
//
//        return rem;
//    }

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
                if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
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
     * Return a List of all loading but not yet finished downloads.
     *
     * @param source Use QUELLE_XXX constants from {@link de.mtplayer.mtp.controller.starter.Start}.
     * @return A list with all download objects.
     */
    synchronized List<Download> getListOfStartsNotFinished(String source) {
        final List<Download> activeDownloads = new ArrayList<>();

        activeDownloads.addAll(downloadList.stream().filter(download -> download.isStateStartedRun())
                .filter(download -> source.equals(DownloadConstants.ALL) || download.getSource().equals(source))
                .collect(Collectors.toList()));

        return activeDownloads;
    }

    /**
     * Return a List of all started but not loading downloads.
     *
     * @param source Use QUELLE_XXX constants from {@link de.mtplayer.mtp.controller.starter.Start}.
     * @return A list with all download objects.
     */
    synchronized List<Download> getListOfStartsNotLoading(String source) {
        final List<Download> activeDownloads = new ArrayList<>();

        activeDownloads.addAll(downloadList.stream().filter(download -> download.isStateStartedWaiting())
                .filter(download -> source.equals(DownloadConstants.ALL) || download.getSource().equals(source))
                .collect(Collectors.toList()));

        return activeDownloads;
    }

    public synchronized void cleanUpButtonStarts() {
        // Starts durch Button (zB. Film abspielen) die fertig sind, löschen
        boolean found = false;
        final Iterator<Download> it = downloadList.iterator();
        while (it.hasNext()) {
            final Download d = it.next();
            if (d.isStateFinished()) {
                if (d.getSource().equals(DownloadConstants.SRC_BUTTON)) {
                    // dann ist er fertig oder abgebrochen
                    it.remove();
                    found = true;
                }
            }
        }
        if (found) {
//            Listener.notify(Listener.EREIGNIS_START_EVENT_BUTTON, this.getClass().getSimpleName());
        }
    }

    public synchronized Download getNextStart() {
        // erste passende Element der Liste zurückgeben oder null
        // und versuchen dass bei mehreren laufenden Downloads ein anderer Sender gesucht wird
        Download ret = null;
        if (downloadList.size() > 0 && getDown(ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getInt())) {
            final Download dataDownload = nextStart();
            if (dataDownload != null && dataDownload.isStateStartedWaiting()) {
                ret = dataDownload;
            }
        }
        return ret;
    }

    private Download nextStart() {
        // Download mit der kleinsten Nr finden der zu Starten ist
        // erster Versuch, Start mit einem anderen Sender

        Download tmpDownload = searchNextDownload(1);
        if (tmpDownload != null) {
            // einer wurde gefunden
            return tmpDownload;
        }

        if (Boolean.parseBoolean(ProgConfig.DOWNLOAD_MAX_ONE_PER_SERVER.get())) {
            // dann darf nur ein Download pro Server gestartet werden
            return null;
        }

        // zweiter Versuch, Start mit einem passenden Sender
        tmpDownload = searchNextDownload(ProgConst.MAX_SENDER_FILME_LADEN);
        return tmpDownload;
    }

    private Download searchNextDownload(int maxProChannel) {
        Download tmpDownload = null;
        int nr = -1;

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
            if (host.equals("akamaihd.net")) {
                // content delivery network
                return false;
            }

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

    private String getHost(Download download) {
        String host = "";
        try {
            try {
                String uurl = download.getUrl();
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
        try {
            for (final Download download : downloadList) {
                if (download.isStateStartedRun()) {
                    ++count;
                    if (count >= max) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            PLog.errorLog(794519083, ex);
        }
        return false;
    }

}
