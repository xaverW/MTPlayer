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
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.mtplayer.tools.filmListFilter.FilmlistBlackFilter;
import de.p2tools.p2Lib.tools.date.PDate;
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
        List<DownloadData> syncRemoveList = Collections.synchronizedList(new ArrayList<>());

        downloadList.stream()
                .filter(d -> !d.isStateStoped())
                .filter(d -> d.isAbo())
                .forEach(download -> {
                    if (download.isStateInit()) {
                        // noch nicht gestartet
                        syncRemoveList.add(download);
                    } else if (download.isStateError()) {
                        // fehlerhafte
                        download.resetDownload();
                    }
                });

        // Downloads löschen
        if (syncRemoveList.size() == downloadList.size()) {
            downloadList.clear();
        } else {
            // das kostet Zeit
            downloadList.removeAll(syncRemoveList);
        }

        // und zurückgestellte wieder aktivieren
        downloadList.resetPlacedBack();

        PDuration.counterStop("DownloadListAbo.refreshDownloads");
    }

    private void searchForNewDownloads() {
        // in der Filmliste nach passenden Filmen suchen und Downloads anlegen
        PDuration.counterStart("DownloadListAbo.searchForNewDownloads");
        List<DownloadData> syncDownloadArrayList = Collections.synchronizedList(new ArrayList<>());

        // den Abo-Trefferzähler zurücksetzen
        progData.aboList.stream().forEach(abo -> abo.clearCountHit());

        if (progData.setDataList.getSetDataForAbo("") == null) {
            // dann fehlt ein Set für die Abos
            Platform.runLater(() -> new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO));
            return;
        }

        // mit den bereits enthaltenen Download-URLs füllen
        Set<String> syncDownloadsAlreadyInTheListHash = Collections.synchronizedSet(new HashSet<>(500)); //todo für 90% übertrieben, für 10% immer noch zu wenig???
        downloadList.forEach((download) -> syncDownloadsAlreadyInTheListHash.add(download.getUrl()));

        // prüfen ob in "alle Filme" oder nur "nach Blacklist" gesucht werden soll
        final boolean checkWithBlackList = ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getValue();

        // und jetzt die Filmliste ablaufen
        progData.filmlist.parallelStream().forEach(film -> {
            final AboData abo = progData.aboList.getAboForFilm_quick(film, true);
            if (abo == null) {
                // dann gibts dafür kein Abo
                return;
            }

            abo.incrementCountHit();
            if (!abo.isActive()) {
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
            final String urlDownload = film.getUrlForResolution(abo.getResolution());
            if (!syncDownloadsAlreadyInTheListHash.add(urlDownload)) {
                return;
            }

            abo.setDate(new PDate());
            final SetData setData = abo.getSetData(progData);

            // dann in die Liste schreiben
            syncDownloadArrayList.add(new DownloadData(setData, film, DownloadConstants.SRC_ABO, abo, "", "", ""));
            found = true;
        });


        if (found) {
            DownloadTools.checkDoubleNames(syncDownloadArrayList, downloadList);
            downloadList.addAll(syncDownloadArrayList);
            downloadList.setNumbersInList();
        }
        syncDownloadArrayList.clear();
        syncDownloadsAlreadyInTheListHash.clear();

        // und jetzt die hits eintragen (hier, damit nicht bei jedem die Tabelle geändert werden muss)
        progData.aboList.forEach(abo -> abo.setCountedHits());

        PDuration.counterStop("DownloadListAbo.searchForNewDownloads");
    }

}
