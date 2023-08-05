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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.starter.Start;
import de.p2tools.p2lib.tools.date.PLTimeFactory;
import de.p2tools.p2lib.tools.log.PLog;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadFactoryStarts {

    private DownloadFactoryStarts() {
    }

    public static DownloadData getRestartDownload(DownloadList downloadList) {
        //Versuch einen fehlgeschlagenen Download zu finden, um ihn wieder zu starten
        //die Fehler laufen aber einzeln, vorsichtshalber

        if (!getDown(downloadList, 1)) {
            // dann läuft noch einer
            return null;
        }

        for (final DownloadData download : downloadList) {
            if (download.isStateInit()) {
                continue;
            }

            if (download.isStateError()
                    && download.getStart().getRestartCounter() < ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getValue()
                    && !maxChannelPlay(downloadList, download, 1)) {

                int restarted = download.getStart().getRestartCounter();
                if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
                    download.resetDownload();
                    ProgData.getInstance().downloadList.startDownloads(download);
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
     * @param source Use QUELLE_XXX constants from {@link Start}.
     * @return A list with all download objects.
     */
    static synchronized List<DownloadData> getListOfStartsNotFinished(DownloadList downloadList, String source) {
        final List<DownloadData> activeDownloadData = new ArrayList<>();

        activeDownloadData.addAll(downloadList.stream().filter(DownloadData::isStateStartedRun)
                .filter(download -> source.equals(DownloadConstants.ALL) || download.getSource().equals(source))
                .collect(Collectors.toList()));

        return activeDownloadData;
    }

    /**
     * Return a List of all started but not loading downloads.
     *
     * @param source Use QUELLE_XXX constants from {@link Start}.
     * @return A list with all download objects.
     */
    static synchronized List<DownloadData> getListOfStartsNotLoading(DownloadList downloadList, String source) {
        final List<DownloadData> activeDownloadData = new ArrayList<>();

        activeDownloadData.addAll(downloadList.stream().filter(DownloadData::isStateStartedWaiting)
                .filter(download -> source.equals(DownloadConstants.ALL) || download.getSource().equals(source))
                .collect(Collectors.toList()));

        return activeDownloadData;
    }

    public static synchronized void cleanUpButtonStarts(DownloadList downloadList) {
        // Starts durch Button (zB. Film abspielen) die fertig sind, löschen
        final Iterator<DownloadData> it = downloadList.iterator();
        while (it.hasNext()) {
            final DownloadData d = it.next();
            if (d.isStateFinished()) {
                if (d.getSource().equals(DownloadConstants.SRC_BUTTON)) {
                    // dann ist er fertig oder abgebrochen
                    it.remove();
                }
            }
        }
    }

    public static synchronized DownloadData getNextStart(DownloadList downloadList) {
        //ersten passenden Download der Liste zurückgeben oder null
        //und versuchen, dass bei mehreren laufenden Downloads ein anderer Sender gesucht wird
        DownloadData ret = null;
        if (!downloadList.isEmpty() && getDown(downloadList, ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getValue())) {
            final DownloadData download = nextStart(downloadList);
            if (download != null && download.isStateStartedWaiting()) {
                ret = download;
            }
        }
        return ret;
    }

    private static DownloadData nextStart(DownloadList downloadList) {
        // Download mit der kleinsten Nr finden, der zu Starten ist
        // erster Versuch, Start mit einem anderen Sender

        DownloadData tmpDownload = searchNextDownload(downloadList, 1);
        if (tmpDownload != null) {
            // einer wurde gefunden
            return tmpDownload;
        }

        if (ProgConfig.DOWNLOAD_MAX_ONE_PER_SERVER.getValue()) {
            // dann darf nur ein Download pro Server gestartet werden
            return null;
        }

        // zweiter Versuch, Start mit einem passenden Sender
        tmpDownload = searchNextDownload(downloadList, -1);
        return tmpDownload;
    }

    private static DownloadData searchNextDownload(DownloadList downloadList, int maxProChannel) {
        // max: max. Anzahl pro Domain ODER "-1" dann egal
        DownloadData tmpDownload = null;
        int nr = -1;
        for (DownloadData download : downloadList) {
            if (download.isStateStartedWaiting() &&
                    checkStartTime(download) &&
                    (maxProChannel < 0 || !maxChannelPlay(downloadList, download, maxProChannel)) &&
                    (nr == -1 || download.getNo() < nr)) {

                tmpDownload = download;
                nr = tmpDownload.getNo();
            }
        }

        return tmpDownload;
    }

    private static boolean checkStartTime(DownloadData download) {
        if (download.getStartTime().isEmpty()) {
            return true;
        }

        try {
            LocalTime lNow = LocalTime.now();
            LocalTime lDownload = PLTimeFactory.fromString(download.getStartTime());
            if (lNow.compareTo(lDownload) >= 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            PLog.errorLog(945123690, "Download-Starttime: " + download.getStartTime());
        }

        return true;
    }

    private static boolean maxChannelPlay(DownloadList downloadList, DownloadData d, int max) {
        // true, wenn bereits die maxAnzahl pro Sender läuft
        try {
            int counter = 0;
            final String host = getHost(d);
            if (host.equals("akamaihd.net")) {
                // content delivery network
                return false;
            }

            for (final DownloadData download : downloadList) {
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

    private static String getHost(DownloadData download) {
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

    private static boolean getDown(DownloadList downloadList, int max) {
        int count = 0;
        try {
            for (final DownloadData download : downloadList) {
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
