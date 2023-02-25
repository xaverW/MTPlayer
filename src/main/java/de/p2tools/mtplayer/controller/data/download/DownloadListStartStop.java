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
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.tools.duration.PDuration;

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
    public synchronized void delDownloads(DownloadData download) {
        ArrayList<DownloadData> list = new ArrayList<>();
        list.add(download);
        delDownloads(list);
    }


    /**
     * eine Liste Downloads aus der "Dwonloadliste"  stoppen und dann entfernen
     *
     * @param list
     */

    public synchronized boolean delDownloads(ArrayList<DownloadData> list) {
        PDuration.counterStart("DownloadListStartStop.delDownloads");
        if (list == null || list.isEmpty()) {
            return false;
        }
        // das Starten von neuen Downloads etwas Pausieren
        progData.starterClass.setPaused();
        progData.downloadList.addDownloadUndoList(list);

        final ArrayList<DownloadData> aboHistoryList = new ArrayList<>();
        for (final DownloadData download : list) {
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
     * eine Liste Downloads aus der "Dwonloadliste"  zurückstellen
     *
     * @param list
     */
    public synchronized boolean putBackDownloads(ArrayList<DownloadData> list) {
        boolean found = false;

        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        progData.starterClass.setPaused();

        for (final DownloadData download : list) {
            if (download.isStateInit() || download.isStateStoped()) {
                download.putBack();
                found = true;
            }
        }

        return found;
    }

    /**
     * eine Liste Downloads stoppen
     *
     * @param list
     */
    public synchronized boolean stopDownloads(ArrayList<DownloadData> list) {
        boolean found = false;

        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        progData.starterClass.setPaused();

        for (DownloadData download : list) {
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
                text = "Es sind auch fehlerhafte Filme dabei," + P2LibConst.LINE_SEPARATOR + "diese nochmal starten?";
            } else {
                text = "Film nochmal starten?  ==> " + title;
            }
            answer = new PAlert().showAlert_yes_no_cancel("Download", "Fehlerhafte Downloads", text);
        }
        return answer;
    }

    private void start(ArrayList<DownloadData> downloads) {
        if (downloads.isEmpty()) {
            return;
        }
        downloads.stream().forEach(download -> download.initStartDownload());
        downloadList.addNumber(downloads);
        progData.history.addDownloadDataListToHistory(downloads);
    }

    public void startDownloads(DownloadData download) {
        // Download starten
        ArrayList<DownloadData> list = new ArrayList<>();
        list.add(download);

        start(list);
    }


    /**
     * eine Liste Downloads starten
     *
     * @param list
     * @param alsoFinished
     */

    public boolean startDownloads(Collection<DownloadData> list, boolean alsoFinished) {

        if (list == null || list.isEmpty()) {
            return false;
        }

        PDuration.counterStart("DownloadListStartStop.startDownloads");
        final ArrayList<DownloadData> listStartDownloads = new ArrayList<>();

        // das Starten von neuen Downloads etwas Pausieren
        progData.starterClass.setPaused();

        // nicht gestartete einfach starten
        list.stream().filter(download -> download.isStateInit()).forEach(download ->
                listStartDownloads.add(download));

        if (alsoFinished) {
            if (!startAlsoFinishedDownloads(list, listStartDownloads)) {
                return false;
            }
        }

        // alle Downloads starten/wiederstarten
        start(listStartDownloads);

        PDuration.counterStop("DownloadListStartStop.startDownloads");
        return true;
    }

    private boolean startAlsoFinishedDownloads(Collection<DownloadData> list, ArrayList<DownloadData> listStartDownloads) {

        PAlert.BUTTON answer = PAlert.BUTTON.UNKNOWN;
        final ArrayList<DownloadData> listDelDownloads = new ArrayList<>();
        final ArrayList<DownloadData> listDownloadsRemoveAboHistory = new ArrayList<>();

        // bereits gestartete erst vorbehandeln: wenn er noch läuft/fertig ist gibts nix
        // fehlerhafte nur wenn gewollt
        for (DownloadData download : list) {

            // abgebrochene starten
            if (download.isStateStoped()) {
                listDelDownloads.add(download);
                if (download.isAbo()) {
                    // wenn er schon feritg ist und ein Abos ist, Url auch aus dem Logfile löschen, der Film ist damit wieder auf "Anfang"
                    listDownloadsRemoveAboHistory.add(download);
                }
                listStartDownloads.add(download);
            }

            //fehlerhafte nur, wenn gewollt wieder starten
            if (download.isStateError()) {
                if (answer.equals(PAlert.BUTTON.UNKNOWN)) {
                    answer = restartDownload(list.size(), download.getTitle(), answer);
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

        if (answer.equals(PAlert.BUTTON.CANCEL)) {
            // dann machmer nix
            return false;
        }

        //aus der AboHistory löschen
        progData.erledigteAbos.removeDownloadDataFromHistory(listDownloadsRemoveAboHistory);

        // jetzt noch die Starts stoppen
        listDelDownloads.stream().forEach(download -> download.stopDownload());

        return true;
    }

}
