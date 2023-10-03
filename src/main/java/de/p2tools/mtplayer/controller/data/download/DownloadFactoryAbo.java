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
import de.p2tools.mtplayer.controller.data.abo.AboDataProps;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.tools.date.PDate;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.file.PFileUtils;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;

import java.nio.file.Paths;
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
        ProgData.getInstance().aboList.forEach(AboDataProps::clearCountHit);

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
        ProgData.getInstance().filmList.parallelStream().forEach(film -> {
            final AboData aboData = film.getAbo();
            if (aboData == null) {
                //dann gibts dafür kein Abo
                //oder abo ist ausgeschaltet, ...
                return;
            }

            aboData.incrementCountHit();

            if (checkWithBlackList && BlacklistFilterFactory.checkFilmIsBlockedCompleteBlackData(film, false)) {
                // Blacklist auch bei Abos anwenden und Film wird blockiert
                return;
            }

            if (ProgData.getInstance().historyListAbos.checkIfUrlAlreadyIn(film.getUrlHistory())) {
                // ist schon mal geladen worden
                return;
            }

            // mit der tatsächlichen URL prüfen, ob die URL schon in der Downloadliste ist
            final String urlDownload = film.getUrlForResolution(aboData.getResolution());
            if (!syncDownloadsAlreadyInTheListHash.add(urlDownload)) {
                return;
            }

            //dann haben wir einen Treffer :)
            //und dann auch in die Liste schreiben
            aboData.setDate(new PDate());
            final SetData setData = aboData.getSetData(ProgData.getInstance());
            syncDownloadArrayList.add(new DownloadData(DownloadConstants.SRC_ABO, setData, film, aboData, "", "", ""));
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
        ProgData.getInstance().aboList.forEach(AboDataProps::setCountedHits);

        PDuration.counterStop("searchForNewDownloads");
    }

    private static void checkDoubleNames(List<DownloadData> foundNewDownloads, List<DownloadData> downloadList) {
        // prüfen ob schon ein Download mit dem Zieldateinamen in der Downloadliste existiert
        try {
            final List<DownloadData> alreadyDone = new ArrayList<>();

            foundNewDownloads.forEach(download -> {
                final String oldName = download.getDestPathFile();
                String newName = oldName;
                int i = 0;
                while (searchName(downloadList, newName) || searchName(alreadyDone, newName)) {
                    ++i;
                    newName = getNextFileName(oldName, i);
                }

                if (!oldName.equals(newName)) {
                    download.setFile(newName);
                }

                alreadyDone.add(download);
            });
        } catch (final Exception ex) {
            PLog.errorLog(303021458, ex);
        }
    }

    private static String getNextFileName(String file, int i) {
        String suffix = PFileUtils.getFileNameSuffix(file);
        String name = PFileUtils.getFileNameWithOutExtension(file);
        String path = PFileUtils.getPath(file);
        return Paths.get(path, name + "_" + i + "." + suffix).toString();
    }


    private static boolean searchName(List<DownloadData> searchDownloadList, String name) {
        return searchDownloadList.stream().anyMatch(download -> download.getDestPathFile().equals(name));
    }
}
