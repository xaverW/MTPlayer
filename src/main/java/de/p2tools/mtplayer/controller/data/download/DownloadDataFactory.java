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
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.controller.tools.SizeTools;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.mtdownload.DownloadFactory;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.file.P2FileUtils;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.apache.commons.lang3.SystemUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class DownloadDataFactory {

    private DownloadDataFactory() {
    }

    public static void searchForAbosAndMaybeStart() {
        if (LoadFilmFactory.getInstance().loadFilmlist.getPropLoadFilmlist()) {
            // wird danach eh gemacht
            return;
        }

        if (ProgData.getInstance().setDataList.getSetDataForAbo() == null) {
            // SetData sind nicht eingerichtet
            Platform.runLater(() -> new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.ABO));
            return;
        }

        PDuration.counterStart("searchForAbosAndMaybeStart");
        P2Log.sysLog("Downloads aus Abos suchen");
        //erledigte entfernen, nicht gestartete Abos entfernen und nach neuen Abos suchen
        ProgData.getInstance().downloadList.searchForDownloadsFromAbos();

        if (ProgConfig.DOWNLOAD_START_NOW.getValue() || ProgData.autoMode) {
            // und wenn gewollt auch gleich starten, kann kein Dialog aufgehen: false!
            P2Log.sysLog("Downloads aus Abos starten");
            ProgData.getInstance().downloadList.startAllDownloads();
        }
        PDuration.counterStop("searchForAbosAndMaybeStart");
    }

    public static void preferDownloads(DownloadList downloadList, List<DownloadData> prefDownList) {
        // macht nur Sinn, wenn der Download auf Laden wartet: Init
        // todo auch bei noch nicht gestarteten ermöglichen
        prefDownList.removeIf(d -> d.getState() != DownloadConstants.STATE_STARTED_WAITING);
        if (prefDownList.isEmpty()) {
            return;
        }

        // zum neu nummerieren der alten Downloads
        List<DownloadData> list = new ArrayList<>();
        for (final DownloadData download : downloadList) {
            final int i = download.getNo();
            if (i < P2LibConst.NUMBER_NOT_STARTED) {
                list.add(download);
            }
        }
        prefDownList.forEach(list::remove);
        list.sort(new Comparator<DownloadData>() {
            @Override
            public int compare(DownloadData d1, DownloadData d2) {
                return (d1.getNo() < d2.getNo()) ? -1 : 1;
            }
        });
        int addNr = prefDownList.size();
        for (final DownloadData download : list) {
            ++addNr;
            download.setNo(addNr);
        }

        // und jetzt die vorgezogenen Downloads nummerieren
        int i = 1;
        for (final DownloadData dataDownload : prefDownList) {
            dataDownload.setNo(i++);
        }
    }

    public static synchronized void cleanUpList(DownloadList downloadList) {
        // fertige Downloads löschen, fehlerhafte zurücksetzen
        boolean found = false;
        Iterator<DownloadData> it = downloadList.iterator();
        while (it.hasNext()) {
            DownloadData download = it.next();
            if (download.isStateInit() ||
                    download.isStateStopped()) {
                continue;
            }
            if (download.isStateFinished()) {
                // alles was fertig/fehlerhaft ist, kommt beim putzen weg
                it.remove();
                found = true;
            } else if (download.isStateError()) {
                // fehlerhafte werden zurückgesetzt
                download.resetDownload();
                found = true;
            }
        }

        if (found) {
            downloadList.setDownloadsChanged();
        }
    }

    /**
     * Calculate free disk space on volume and check if the movies can be safely downloaded.
     */
    public static void calculateAndCheckDiskSpace(DownloadData download, String path, Label lblSizeFree) {
        if (path == null || path.isEmpty()) {
            return;
        }
        try {
            String noSize = "";
            long usableSpace = P2FileUtils.getFreeDiskSpace(path);
            String sizeFree = "";
            if (usableSpace == 0) {
                lblSizeFree.setText("");
            } else {
                sizeFree = SizeTools.humanReadableByteCount(usableSpace, true);
            }

            // jetzt noch prüfen, obs auf die Platte passt
            usableSpace /= 1_000_000;
            if (usableSpace > 0) {
                long size = download.getDownloadSize().getTargetSize();
                size /= 1_000_000;
                if (size > usableSpace) {
                    noSize = " [ nicht genug Speicher: ";

                }
            }

            if (noSize.isEmpty()) {
                lblSizeFree.setText(" [ noch frei: " + sizeFree + " ]");
            } else {
                lblSizeFree.setText(noSize + sizeFree + " ]");
            }


        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getDownloadPath() {
        return ProgConfig.START_DIALOG_DOWNLOAD_PATH.get().isEmpty() ?
                PSystemUtils.getStandardDownloadPath() : ProgConfig.START_DIALOG_DOWNLOAD_PATH.get();
    }

    /**
     * Entferne verbotene Zeichen aus Dateiname.
     *
     * @param name        Dateiname
     * @param isPath
     * @param userReplace
     * @param onlyAscii
     * @return Bereinigte Fassung
     */
    public static String replaceEmptyFileName(String name, boolean isPath, boolean userReplace, boolean onlyAscii) {
        String ret = name;
        boolean isWindowsPath = false;
        if (SystemUtils.IS_OS_WINDOWS && isPath && ret.length() > 1 && ret.charAt(1) == ':') {
            // damit auch "d:" und nicht nur "d:\" als Pfad geht
            isWindowsPath = true;
            ret = ret.replaceFirst(":", ""); // muss zum Schluss wieder rein, kann aber so nicht ersetzt werden
        }

        // zuerst die Ersetzungstabelle mit den Wünschen des Users
        if (userReplace) {
            ret = ProgData.getInstance().replaceList.replace(ret, isPath);
        }

        // und wenn gewünscht: "NUR Ascii-Zeichen"
        if (onlyAscii) {
            ret = FileNameUtils.convertToASCIIEncoding(ret, isPath);
        } else {
            ret = FileNameUtils.convertToNativeEncoding(ret, isPath);
        }

        if (isWindowsPath) {
            // c: wieder herstellen
            if (ret.length() == 1) {
                ret = ret + ":";
            } else if (ret.length() > 1) {
                ret = ret.charAt(0) + ":" + ret.substring(1);
            }
        }
        return ret;
    }

    public static void setDownloadSize(DownloadData download) {
        // https://srf-vod-amd.....x-f1-v1-a1.m3u8
        final String M3U8 = ".m3u8";
        if (download.getFilmSizeHd().isEmpty() &&
                !download.getFilmUrlHd().endsWith(M3U8)) {

            download.setFilmSizeHd(download.getFilmUrlHd().isEmpty() ?
                    "" : DownloadFactory.getContentLengthMB(download.getFilmUrlHd()));
        }

        if (download.getFilmSizeSmall().isEmpty() &&
                !download.getFilmUrlSmall().endsWith(M3U8)) {

            download.setFilmSizeSmall(download.getFilmUrlSmall().isEmpty() ?
                    "" : DownloadFactory.getContentLengthMB(download.getFilmUrlSmall()));
        }
    }
}
