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
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.tools.duration.PDuration;

import java.util.ArrayList;
import java.util.Collection;

public class DownloadFactoryStartDownload {

    private DownloadFactoryStartDownload() {
    }

    public static void startDownloads(DownloadList downloadList, ArrayList<DownloadData> downloads) {
        // Menü/Automatisch
        if (downloads.isEmpty()) {
            return;
        }
        downloads.forEach(DownloadData::initStartDownload);
        downloadList.addNumber(downloads);
        ProgData.getInstance().historyList.addDownloadDataListToHistory(downloads);
    }

    /**
     * eine Liste Downloads starten
     *
     * @param list
     * @param alsoFinished
     */

    public static boolean startDownloads(DownloadList downloadList, Collection<DownloadData> list,
                                         boolean alsoFinished) {
        // Button/Menü oder automatisch
        if (list == null || list.isEmpty()) {
            return false;
        }

        PDuration.counterStart("startDownloads");
        final ArrayList<DownloadData> listStartDownloads = new ArrayList<>();

        // das Starten von neuen Downloads etwas Pausieren
        ProgData.getInstance().startDownload.setPaused();

        // nicht gestartete einfach starten
        list.stream().filter(DownloadData::isStateInit).forEach(listStartDownloads::add);

        if (alsoFinished) {
            if (!startAlsoFinishedDownloads(list, listStartDownloads)) {
                return false;
            }
        }

        // alle Downloads starten/wiederstarten
        startDownloads(downloadList, listStartDownloads);

        PDuration.counterStop("startDownloads");
        return true;
    }

    private static boolean startAlsoFinishedDownloads(Collection<DownloadData> list,
                                                      Collection<DownloadData> listStartDownloads) {
        PAlert.BUTTON answer = PAlert.BUTTON.UNKNOWN;
        final ArrayList<DownloadData> listDelDownloads = new ArrayList<>();
        final ArrayList<DownloadData> listDownloadsRemoveAboHistory = new ArrayList<>();

        // bereits gestartete erst vorbehandeln: wenn er noch läuft/fertig ist gibts nix
        // fehlerhafte nur, wenn gewollt
        for (DownloadData download : list) {

            // abgebrochene starten
            if (download.isStateStopped()) {
                listDelDownloads.add(download);
                if (download.isAbo()) {
                    // wenn er schon fertig ist und ein Abos ist, Url auch aus dem Logfile löschen,
                    // der Film ist damit wieder auf "Anfang"
                    listDownloadsRemoveAboHistory.add(download);
                }
                listStartDownloads.add(download);
            }

            //fehlerhafte nur, wenn gewollt wieder starten
            if (download.isStateError()) {
                if (answer.equals(PAlert.BUTTON.UNKNOWN)) {
                    answer = DownloadFactoryStopDownload.restartDownload(list.size(), download.getTitle(), answer);
                }

                switch (answer) {
                    case CANCEL:
                        break;
                    case NO:
                        // weiter mit der nächsten URL
                        continue;
                    case YES:
                    default:
                        listDelDownloads.add(download);
                        if (download.isAbo()) {
                            // wenn er schon fertig ist und ein Abos ist, Url auch aus dem Logfile löschen,
                            // der Film ist damit wieder auf "Anfang"
                            listDownloadsRemoveAboHistory.add(download);
                        }
                        listStartDownloads.add(download);
                }
            }
        }
        if (answer.equals(PAlert.BUTTON.CANCEL)) {
            // dann machmer nix
            return false;
        }

        //aus der AboHistory löschen
        ProgData.getInstance().historyListAbos.removeDownloadDataFromHistory(listDownloadsRemoveAboHistory);

        // jetzt noch die Starts stoppen
        listDelDownloads.forEach(download -> download.stopDownload(false));
        return true;
    }
}
