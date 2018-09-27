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

import de.mtplayer.mtp.controller.config.ProgData;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.tools.log.PDuration;

import java.util.ArrayList;
import java.util.Collection;

public class DownloadListStartStop {

    private final ProgData progData;
    private final DownloadList downloadList;

    public DownloadListStartStop(ProgData progData, DownloadList downloadList) {
        this.progData = progData;
        this.downloadList = downloadList;
    }


    /**
     * einen Download aus der "Dwonloadliste"  stoppen und dann entfernen
     *
     * @param download
     */
    public synchronized void delDownloads(Download download) {
        ArrayList<Download> list = new ArrayList<>();
        list.add(download);
        delDownloads(list);
    }


    /**
     * eine Liste Downloads aus der "Dwonloadliste"  zurückstellen
     *
     * @param list
     */
    public synchronized boolean putBackDownloads(ArrayList<Download> list) {
        boolean found = false;

        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        progData.starterClass.setPaused();

        for (final Download download : list) {
            if (download.isStateInit() || download.isStateStoped()) {
                download.putBack();
                found = true;
            }
        }

        return found;
    }

    /**
     * eine Liste Downloads aus der "Dwonloadliste"  stoppen und dann entfernen
     *
     * @param list
     */

    public synchronized boolean delDownloads(ArrayList<Download> list) {

        PDuration.counterStart("DownloadListStartStop.delDownloads");
        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        progData.starterClass.setPaused();

        final ArrayList<Download> aboHistoryList = new ArrayList<>();

        for (final Download download : list) {
            if (download.isAbo()) {
                // ein Abo wird zusätzlich ins Logfile geschrieben
                aboHistoryList.add(download);
            }
        }

        if (!aboHistoryList.isEmpty()) {
            progData.erledigteAbos.addDownloadDataListToHistory(aboHistoryList);
        }

        list.stream().filter(download -> download.isStateStartedRun()).forEach(download -> download.stopDownload());
        boolean found = downloadList.removeAll(list);

        PDuration.counterStop("DownloadListStartStop.delDownloads");
        return found;
    }

    /**
     * eine Liste Downloads stoppen
     *
     * @param list
     */
    public synchronized boolean stopDownloads(ArrayList<Download> list) {
        boolean found = false;

        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        progData.starterClass.setPaused();

        for (Download download : list) {
            if (download.isStateStartedWaiting() || download.isStateStartedRun() || download.isStateError()) {
                // nur dann läuft er
                download.stopDownload();
                found = true;
            }
        }

        return found;
    }


    private PAlert.BUTTON restartDownload(int size, String title, PAlert.BUTTON answer) {
        if (answer.equals(PAlert.BUTTON.UNKNOWN)) {
            // nur einmal fragen
            String text;
            if (size > 1) {
                text = "Es sind auch fehlerhafte Filme dabei," + PConst.LINE_SEPARATOR + "diese nochmal starten?";
            } else {
                text = "Film nochmal starten?  ==> " + title;
            }
            answer = new PAlert().showAlert_yes_no_cancel("Download", "Fehlerhafte Downloads", text);
        }
        return answer;
    }

    private void start(ArrayList<Download> downloads) {
        if (downloads.isEmpty()) {
            return;
        }
        downloads.stream().forEach(download -> download.initStartDownload());
        downloadList.addNumber(downloads);
        progData.history.addDownloadDataListToHistory(downloads);
    }

    public void startDownloads(Download download) {
        // Download starten
        ArrayList<Download> list = new ArrayList<>();
        list.add(download);

        start(list);
    }


    /**
     * eine Liste Downloads starten
     *
     * @param list
     * @param alsoFinished
     */

    public boolean startDownloads(Collection<Download> list, boolean alsoFinished) {
        PDuration.counterStart("DownloadListStartStop.startDownloads");

        PAlert.BUTTON answer = PAlert.BUTTON.UNKNOWN;
        final ArrayList<Download> listDelDownloads = new ArrayList<>();
        final ArrayList<Download> listStartDownloads = new ArrayList<>();
        final ArrayList<Download> listDownloadsRemoveAboHistory = new ArrayList<>();

        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        progData.starterClass.setPaused();


        // nicht gestartete einfach starten
        list.stream().filter(download -> download.isStateInit()).forEach(download -> {
            listStartDownloads.add(download);
        });

        // bereits gestartete erst vorbehandeln: wenn er noch läuft/fertig ist gibts nix
        // fehlerhafte nur wenn gewollt
        for (Download download : list) {

            // abgebrochene starten
            if (alsoFinished && download.isStateStoped()) {
                listDelDownloads.add(download);
                if (download.isAbo()) {
                    // wenn er schon feritg ist und ein Abos ist, Url auch aus dem Logfile löschen, der Film ist damit wieder auf "Anfang"
                    listDownloadsRemoveAboHistory.add(download);
                }
                listStartDownloads.add(download);
            }

            //fehlerhafte nur wenn gewollt wieder starten
            if (alsoFinished && download.isStateError()) {
                if (answer.equals(PAlert.BUTTON.UNKNOWN)) {
                    answer = restartDownload(list.size(), download.arr[Download.DOWNLOAD_TITLE], answer);
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
                            // wenn er schon feritg ist und ein Abos ist, Url auch aus dem Logfile löschen, der Film ist damit wieder auf "Anfang"
                            listDownloadsRemoveAboHistory.add(download);
                        }
                        listStartDownloads.add(download);
                }
            }
        }

        PDuration.counterStop("DownloadListStartStop.startDownloads");


        if (answer.equals(PAlert.BUTTON.CANCEL)) {
            // dann machmer nix
            return false;
        }

        //aus der AboHistory löschen
        progData.erledigteAbos.removeDownloadDataFromHistory(listDownloadsRemoveAboHistory);

        // jetzt noch die Starts stoppen
        listDelDownloads.stream().forEach(download -> download.stopDownload());

        // alle Downloads starten/wiederstarten
        start(listStartDownloads);
        return true;
    }
}
