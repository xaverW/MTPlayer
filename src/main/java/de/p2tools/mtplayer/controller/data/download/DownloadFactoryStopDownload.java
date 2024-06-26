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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.tools.duration.P2Duration;

import java.util.ArrayList;
import java.util.List;

public class DownloadFactoryStopDownload {

    private DownloadFactoryStopDownload() {
    }

    /**
     * eine Liste Downloads aus der "DownloadListe" stoppen und dann entfernen
     *
     * @param list
     */

    public static synchronized boolean delDownloads(DownloadList downloadList, ArrayList<DownloadData> list) {
        // aus dem Menü oder Tabelle-Download-Button
        P2Duration.counterStart("delDownloads");
        if (list == null || list.isEmpty()) {
            return false;
        }
        // das Starten von neuen Downloads etwas Pausieren
        ProgData.getInstance().startDownload.setPaused();


        if (!DownloadFactoryDelDownloadFiles.stopDownloadAndDeleteFile(list, true)) {
            // dann wurden keine Downloads gestoppt, abgebrochen
            return false;
        }

        ProgData.getInstance().downloadList.addDownloadsToUndoList(list); // erst eintragen, dann löschen - selList ändert sich dann

        final ArrayList<DownloadData> aboHistoryList = new ArrayList<>();
        for (final DownloadData download : list) {
            if (download.isAbo()) {
                // ein Abo wird zusätzlich ins Logfile geschrieben
                aboHistoryList.add(download);
            }
        }
        if (!aboHistoryList.isEmpty()) {
            // kommt nur in die "erledigte Abos" nicht in die History, da Downloads abgebrochen
            ProgData.getInstance().historyListAbos.addDownloadDataListToHistory(aboHistoryList);
        }
        downloadList.removeAll(list);

        P2Duration.counterStop("delDownloads");
        return true;
    }

    /**
     * eine Liste Downloads stoppen
     *
     * @param list
     */
    public static synchronized boolean stopDownloads(List<DownloadData> list) {
        // Aufruf aus den Menüs
        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        ProgData.getInstance().startDownload.setPaused();
        return DownloadFactoryDelDownloadFiles.stopDownloadAndDeleteFile(list, false);
    }

    /**
     * eine Liste Downloads aus der "Dwonloadliste" zurückstellen
     *
     * @param list
     */
    public static synchronized boolean putBackDownloads(ArrayList<DownloadData> list) {
        boolean found = false;

        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        ProgData.getInstance().startDownload.setPaused();

        for (final DownloadData download : list) {
            if (download.isStateInit() || download.isStateStopped()) {
                download.putBack();
                found = true;
            }
        }

        return found;
    }

    public static P2Alert.BUTTON restartDownload(int size, String title, P2Alert.BUTTON answer) {
        if (answer.equals(P2Alert.BUTTON.UNKNOWN)) {
            // nur einmal fragen
            String text;
            if (size > 1) {
                text = "Es sind auch fehlerhafte Filme dabei," + P2LibConst.LINE_SEPARATOR + "diese nochmal starten?";
            } else {
                text = "Film nochmal starten?  ==> " + title;
            }
            answer = P2Alert.showAlert_yes_no_cancel("Download", "Fehlerhafte Downloads", text);
        }
        return answer;
    }
}