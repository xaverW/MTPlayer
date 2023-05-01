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
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFilterFactory;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.tools.date.PDate;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import org.apache.commons.io.FilenameUtils;

import java.util.*;

public class DownloadFactoryAbo {
    private static boolean found = false;

    private DownloadFactoryAbo() {
    }

    static synchronized void searchDownloadsFromAbos(DownloadList downloadList) {
        refreshDownloads(downloadList);
        searchForNewDownloads(downloadList);
    }

    private static void refreshDownloads(DownloadList downloadList) {
        // fehlerhafte und nicht gestartete löschen, wird nicht gemeldet ob was gefunden wurde
        PDuration.counterStart("refreshDownloads");
        List<DownloadData> syncRemoveList = Collections.synchronizedList(new ArrayList<>());

        downloadList.stream()
                .filter(d -> !d.isStateStopped())
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

        PDuration.counterStop("refreshDownloads");
    }

    private static void searchForNewDownloads(DownloadList downloadList) {
        // in der Filmliste nach passenden Filmen suchen und Downloads anlegen
        PDuration.counterStart("searchForNewDownloads");
        List<DownloadData> syncDownloadArrayList = Collections.synchronizedList(new ArrayList<>());

        // den Abo-Trefferzähler zurücksetzen
        ProgData.getInstance().aboList.stream().forEach(abo -> abo.clearCountHit());

        if (ProgData.getInstance().setDataList.getSetDataForAbo("") == null) {
            // dann fehlt ein Set für die Abos
            Platform.runLater(() -> new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.ABO));
            return;
        }

        // mit den bereits enthaltenen Download-URLs füllen
        Set<String> syncDownloadsAlreadyInTheListHash = Collections.synchronizedSet(new HashSet<>(500)); //todo für 90% übertrieben, für 10% immer noch zu wenig???
        downloadList.forEach((download) -> syncDownloadsAlreadyInTheListHash.add(download.getUrl()));

        // prüfen ob in "alle Filme" oder nur "nach Blacklist" gesucht werden soll
        final boolean checkWithBlackList = ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getValue();

        // und jetzt die Filmliste ablaufen
        ProgData.getInstance().filmlist.parallelStream().forEach(film -> {
            final AboData abo = film.getAbo();
            if (abo == null) {
                //dann gibts dafür kein Abo
                //oder abo ist ausgeschaltet, ...
                return;
            }

            abo.incrementCountHit();

            if (checkWithBlackList && BlacklistFilterFactory.checkFilmIsBlockedCompleteBlackData(film, false)) {
                // Blacklist auch bei Abos anwenden und Film wird blockiert
                return;
            }

            if (ProgData.getInstance().erledigteAbos.checkIfUrlAlreadyIn(film.getUrlHistory())) {
                // ist schon mal geladen worden
                return;
            }

            // mit der tatsächlichen URL prüfen, ob die URL schon in der Downloadliste ist
            final String urlDownload = film.getUrlForResolution(abo.getResolution());
            if (!syncDownloadsAlreadyInTheListHash.add(urlDownload)) {
                return;
            }

            //dann haben wird einen Treffer :)
            //und dann auch in die Liste schreiben
            abo.setDate(new PDate());
            final SetData setData = abo.getSetData(ProgData.getInstance());
            syncDownloadArrayList.add(new DownloadData(setData, film, DownloadConstants.SRC_ABO, abo, "", "", ""));
            found = true;
        });

        if (found) {
            checkDoubleNames(syncDownloadArrayList, downloadList);
            downloadList.addAll(syncDownloadArrayList);
            downloadList.setNumbersInList();
        }
        syncDownloadArrayList.clear();
        syncDownloadsAlreadyInTheListHash.clear();

        // und jetzt die hits eintragen (hier, damit nicht bei jedem die Tabelle geändert werden muss)
        ProgData.getInstance().aboList.forEach(abo -> abo.setCountedHits());

        PDuration.counterStop("searchForNewDownloads");
    }

    public static void checkDoubleNames(List<DownloadData> foundDownloads, List<DownloadData> downloadList) {
        // prüfen ob schon ein Download mit dem Zieldateinamen in der Downloadliste existiert
        try {
            final List<DownloadData> alreadyDone = new ArrayList<>();

            foundDownloads.stream().forEach(download -> {
                final String oldName = download.getDestFileName();
                String newName = oldName;
                int i = 1;
                while (searchName(downloadList, newName) || searchName(alreadyDone, newName)) {
                    newName = getNewName(oldName, ++i);
                }

                if (!oldName.equals(newName)) {
                    download.setDestFileName(newName);
                }

                alreadyDone.add(download);
            });
        } catch (final Exception ex) {
            PLog.errorLog(303021458, ex);
        }
    }

    private static String getNewName(String oldName, int i) {
        String base = FilenameUtils.getBaseName(oldName);
        String suff = FilenameUtils.getExtension(oldName);
        return base + "_" + i + "." + suff;
    }

    private static boolean searchName(List<DownloadData> searchDownloadList, String name) {
        return searchDownloadList.stream().anyMatch(download -> download.getDestFileName().equals(name));
    }
}
