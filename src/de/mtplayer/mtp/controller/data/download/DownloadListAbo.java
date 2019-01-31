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

import de.mtplayer.mLib.tools.MDate;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.gui.dialog.NoSetDialogController;
import de.mtplayer.mtp.tools.filmListFilter.FilmlistBlackFilter;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.application.Platform;

import java.util.*;

public class DownloadListAbo {

    private final ProgData progData;
    private final DownloadList downloadList;
    private boolean found = false;


    public DownloadListAbo(ProgData progData, DownloadList downloadList) {
        this.progData = progData;
        this.downloadList = downloadList;
    }

    synchronized void searchDownloadsFromAbos() {
        refreshDownloads();
        searchForNewDownloads();
    }

    private void refreshDownloads() {
        // fehlerhafte und nicht gestartete löschen, wird nicht gemeldet ob was gefunden wurde
        PDuration.counterStart("DownloadListAbo.refreshDownloads");

        List<Download> removeList = new ArrayList<>();
        List<Download> syncRemoveLisst = Collections.synchronizedList(removeList);

        downloadList.stream()
                .filter(d -> !d.isStateStoped())
                .filter(d -> d.isAbo())
                .forEach(download -> {
                    if (download.isStateInit()) {
                        // noch nicht gestartet
                        syncRemoveLisst.add(download);
                    } else if (download.isStateError()) {
                        // fehlerhafte
                        download.resetDownload();
                    }
                });

        // Downloads löschen
        if (syncRemoveLisst.size() == downloadList.size()) {
            downloadList.clear();
        } else {
            // das kostet Zeit
            downloadList.removeAll(syncRemoveLisst);
        }
        // und zurückgestellte wieder aktivieren
        downloadList.resetPlacedBack();

        PDuration.counterStop("DownloadListAbo.refreshDownloads");
    }

    private void searchForNewDownloads() {
        // in der Filmliste nach passenden Filmen suchen und Downloads anlegen
        PDuration.counterStart("DownloadListAbo.searchForNewDownloads");

        // den Abo-Trefferzähler zurücksetzen
        progData.aboList.stream().forEach(abo -> abo.clearCountHit());

        ArrayList<Download> downloadArrayList = new ArrayList<>();
        List<Download> syncDownloadArrayList = Collections.synchronizedList(downloadArrayList);

        final HashSet<String> downloadsAlreadyInTheListHash = new HashSet<>(500); //todo für 90% übertrieben, für 10% immer noch zu wenig???
        Set syncDownloadsAlreadyInTheListHash = Collections.synchronizedSet(downloadsAlreadyInTheListHash);

        // mit den bereits enthaltenen Download-URLs füllen
        downloadList.forEach((download) -> syncDownloadsAlreadyInTheListHash.add(download.getUrl()));

        // prüfen ob in "alle Filme" oder nur "nach Blacklist" gesucht werden soll
        final boolean checkWithBlackList = ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getBool();

        if (progData.setList.getPsetAbo("") == null) {
            // dann fehlt ein Set für die Abos
            Platform.runLater(() -> new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO));
            return;
        }

        // und jetzt die Filmliste ablaufen
        progData.filmlist.parallelStream().forEach(film -> {
            final Abo aboForFilm = progData.aboList.getAboForFilm_quick(film, true);

            if (aboForFilm == null) {
                // dann gibts dafür kein Abo
                return;
            }

            aboForFilm.incrementCountHit();

            if (!aboForFilm.isActive()) {
                // oder es ist ausgeschaltet
                return;
            }


            if (checkWithBlackList && !FilmlistBlackFilter.checkBlacklistForDownloads(film)) {
                // Blacklist auch bei Abos anwenden und Film wird blockiert
                return;
            }

            if (progData.erledigteAbos.checkIfUrlAlreadyIn(film.getUrlHistory())) {
                // ist schon mal geladen worden
                return;
            }

            // mit der tatsächlichen URL prüfen, ob die URL schon in der Downloadliste ist
            final String urlDownload = film.getUrlForResolution(aboForFilm.getResolution());
            if (!syncDownloadsAlreadyInTheListHash.add(urlDownload)) {
                return;
            }

            aboForFilm.setDate(new MDate());
            final SetData setData = progData.setList.getPsetAbo(aboForFilm.getPsetName()); // todo eine ID dafür verwenden

            // nur den Namen anpassen, falls geändert oder altes Set nicht mehr existiert
            aboForFilm.setPsetName(setData.getName()); // todo das machmer beim ProgStart 1x

            // dann in die Liste schreiben
            syncDownloadArrayList.add(new Download(setData, film, DownloadConstants.SRC_ABO, aboForFilm, "", "", ""));
            found = true;
        });


        if (found) {
            downloadList.addAll(syncDownloadArrayList);
            downloadList.setNumbersInList();
        }
        syncDownloadArrayList.clear();
        syncDownloadsAlreadyInTheListHash.clear();

        // und jetzt die hits eintragen (hier, damit nicht bei jedem die Tabelle geändert werden muss)
        progData.aboList.stream().forEach(abo -> abo.setCountedHits());

        PDuration.counterStop("DownloadListAbo.searchForNewDownloads");
    }

}
