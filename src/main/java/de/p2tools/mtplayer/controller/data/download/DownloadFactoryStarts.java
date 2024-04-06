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
import de.p2tools.mtplayer.controller.starter.StartDownloadDto;
import de.p2tools.p2lib.tools.log.P2Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadFactoryStarts {

    private DownloadFactoryStarts() {
    }

    /**
     * Return a List of all loading but not yet finished downloads.
     *
     * @param source Use QUELLE_XXX constants from {@link StartDownloadDto}.
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
     * @param source Use QUELLE_XXX constants from {@link StartDownloadDto}.
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
        DownloadData tmpDownload = null;
        int nr = -1;
        for (DownloadData download : downloadList) {
            if (download.isStateStartedWaiting() &&
                    checkStartTime(download) &&
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
            final LocalDateTime lNow = LocalDateTime.now();
            final LocalDateTime lDownload = download.getStartTimeLdt();
            if (lNow.isAfter(lDownload)) {
                // dann starten
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            P2Log.errorLog(945123690, "Download-StartTime: " + download.getStartTime());
        }

        return true;
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
            P2Log.errorLog(794519083, ex);
        }
        return false;
    }
}
