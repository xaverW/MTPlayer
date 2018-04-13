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

import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.p2tools.p2Lib.tools.log.Duration;

import java.util.ArrayList;

public class DownloadStartStop {

    private final Daten daten;
    private final DownloadList downloadList;

    public DownloadStartStop(Daten daten, DownloadList downloadList) {
        this.daten = daten;
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
    public synchronized boolean putbackDownloads(ArrayList<Download> list) {
        boolean gefunden = false;

        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        daten.starterClass.pause();

        for (final Download download : list) {
            if (download.isStateInit() || download.isStateStoped()) {
                download.putBack();
                gefunden = true;
            }
        }

        return gefunden;
    }

    /**
     * eine Liste Downloads aus der "Dwonloadliste"  stoppen und dann entfernen
     *
     * @param list
     */

    public synchronized boolean delDownloads(ArrayList<Download> list) {

        Duration.counterStart("DownloadStartStop.delDownloads");
        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        daten.starterClass.pause();

        final ArrayList<Download> aboHistoryList = new ArrayList<>();

        for (final Download download : list) {
            if (download.isAbo()) {
                // ein Abo wird zusätzlich ins Logfile geschrieben
                aboHistoryList.add(download);
            }
        }

        if (!aboHistoryList.isEmpty()) {
            daten.erledigteAbos.writeDownloadArray(aboHistoryList);
        }

        list.stream().filter(download -> download.isStateStartedRun()).forEach(download -> download.stopDownload());
        boolean gefunden = downloadList.removeAll(list);

        Duration.counterStop("DownloadStartStop.delDownloads");
        return gefunden;
    }

    /**
     * eine Liste Downloads stoppen
     *
     * @param list
     */
    public synchronized boolean stopDownloads(ArrayList<Download> list) {
        boolean gefunden = false;

        if (list == null || list.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        daten.starterClass.pause();

        for (Download download : list) {
            if (download.isStateStartedWaiting() || download.isStateStartedRun() || download.isStateError()) {
                // nur dann läuft er
                download.stopDownload();
                gefunden = true;
            }
        }

        return gefunden;
    }


    private MTAlert.BUTTON restartDownload(int size, String title, MTAlert.BUTTON antwort) {
        if (antwort.equals(MTAlert.BUTTON.UNKNOWN)) {
            // nur einmal fragen
            String text;
            if (size > 1) {
                text = "Es sind auch fehlerhafte Filme dabei,\n" + "diese nochmal starten?";
            } else {
                text = "Film nochmal starten?  ==> " + title;
            }
            antwort = new MTAlert().showAlert_yes_no_cancel("Download", "Fehlerhafte Downloads", text);
        }
        return antwort;
    }

    private void start(ArrayList<Download> downloads) {
        if (downloads.isEmpty()) {
            return;
        }
        downloads.stream().forEach(download -> download.initStartDownload());
        downloadList.addNumber(downloads);
        daten.history.writeDownloadArray(downloads);
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
     * @param liste
     * @param auchFertige
     */

    public boolean startDownloads(ArrayList<Download> liste, boolean auchFertige) {
        Duration.counterStart("DownloadStartStop.startDownloads");

        MTAlert.BUTTON antwort = MTAlert.BUTTON.UNKNOWN;
        final ArrayList<Download> listeDownloadsLoeschen = new ArrayList<>();
        final ArrayList<Download> listeDownloadsStarten = new ArrayList<>();
        final ArrayList<Download> listeDownloadsRemoveAboHistory = new ArrayList<>();

        if (liste == null || liste.isEmpty()) {
            return false;
        }

        // das Starten von neuen Downloads etwas Pausieren
        daten.starterClass.pause();


        // nicht gestartete einfach starten
        liste.stream().filter(download -> download.isStateInit()).forEach(download -> {
            listeDownloadsStarten.add(download);
        });

        // bereits gestartete erst vorbehandeln: wenn er noch läuft/fertig ist gibts nix
        // fehlerhafte nur wenn gewollt
        for (Download download : liste) {

            // abgebrochene starten
            if (auchFertige && download.isStateStoped()) {
                listeDownloadsLoeschen.add(download);
                if (download.isAbo()) {
                    // wenn er schon feritg ist und ein Abos ist, Url auch aus dem Logfile löschen, der Film ist damit wieder auf "Anfang"
                    listeDownloadsRemoveAboHistory.add(download);
                }
                listeDownloadsStarten.add(download);
            }

            //fehlerhafte nur wenn gewollt wieder starten
            if (auchFertige && download.isStateError()) {
                if (antwort.equals(MTAlert.BUTTON.UNKNOWN)) {
                    antwort = restartDownload(liste.size(), download.arr[Download.DOWNLOAD_TITEL], antwort);
                }

                switch (antwort) {
                    case CANCEL:
                        break;
                    case NO:
                        // weiter mit der nächsten URL
                        continue;
                    case YES:
                    default:
                        listeDownloadsLoeschen.add(download);
                        if (download.isAbo()) {
                            // wenn er schon feritg ist und ein Abos ist, Url auch aus dem Logfile löschen, der Film ist damit wieder auf "Anfang"
                            listeDownloadsRemoveAboHistory.add(download);
                        }
                        listeDownloadsStarten.add(download);
                }
            }
        }

        Duration.counterStop("DownloadStartStop.startDownloads");


        if (antwort.equals(MTAlert.BUTTON.CANCEL)) {
            // dann machmer nix
            return false;
        }

        //aus der AboHitory löschen
        daten.erledigteAbos.removeDownloadListFromHistory(listeDownloadsRemoveAboHistory);

        // jetzt noch die Starts stoppen
        listeDownloadsLoeschen.stream().forEach(download -> download.stopDownload());

        // alle Downloads starten/wiederstarten
        start(listeDownloadsStarten);
        return true;
    }
}
