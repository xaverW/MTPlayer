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

package de.p2tools.mtplayer.controller.data.abo;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadList;
import de.p2tools.mtplayer.controller.data.film.FilmListMTP;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.tools.date.P2Date;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.file.P2FileUtils;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

import java.nio.file.Paths;
import java.util.*;

public class AboSearchDownloadsFactory {
    private static boolean found = false;
    public static BooleanProperty alreadyRunning = new SimpleBooleanProperty(false);
    private static int act = 0;
    private static int now = 0;
    private static int count = 0;

    private AboSearchDownloadsFactory() {
    }

    public static void searchFromDialog(boolean fromOk) {
        if (ProgConfig.ABO_SEARCH_NOW.getValue()) {
            // nur dann werden Downloads gesucht
            ProgData.busy.busyOn(fromOk ? Busy.BUSY_SRC.GUI : Busy.BUSY_SRC.ABO_DIALOG,
                    "Downloads suchen:", -1.0, false);
            alreadyRunning.set(true);
        }

        ProgData.getInstance().aboList.notifyChanges();
    }

    public static void searchForDownloadsFromAbosAndMaybeStart() {
        // über Menü oder Button: "Download auffrischen"
        // EVENT_BLACKLIST_CHANGED geändert und "Abo suchen" ist ein
        // SYSTEM_BLACKLIST_SHOW_ABO geändert und "Abo suchen" ist ein
        // AboList geändert und "Abo suchen" ist ein
        // workOnFilmListLoadFinished und "Abo suchen" ist ein oder AUTOMODE
        ProgData.busy.busyOnFx(Busy.BUSY_SRC.GUI, "Downloads suchen:", -1.0, false);

        if (ProgData.FILMLIST_IS_DOWNLOADING.get() ||
                ProgData.AUDIOLIST_IS_DOWNLOADING.get()) {
            // wird danach eh gemacht
            alreadyRunning.set(false);
            ProgData.busy.busyOffFx();
            return;
        }

        if (ProgData.getInstance().setDataList.getSetDataForAbo() == null) {
            // SetData sind nicht eingerichtet
            Platform.runLater(() -> new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.ABO));
            alreadyRunning.set(false);
            ProgData.busy.busyOffFx();
            return;
        }

        P2Log.sysLog("Downloads aus Abos suchen");
        //erledigte entfernen, nicht gestartete Abos entfernen und nach neuen Abos suchen
        count = ProgData.getInstance().downloadList.getSize();
        DownloadFactory.refreshDownloads(ProgData.getInstance().downloadList);

        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                searchForNewDownloadsForAbos(ProgData.getInstance().downloadList);
                alreadyRunning.set(false);
                ProgData.busy.busyOffFx();
                ProgData.getInstance().downloadList.setDownloadsChanged();
                return null;
            }
        }).start();
    }

    private static void searchForNewDownloadsForAbos(DownloadList downloadList) {
        // in der Filmliste nach passenden Filmen (für Abos) suchen und Downloads anlegen
        P2Duration.counterStart("searchForNewDownloadsForAbos");
        List<DownloadData> syncDownloadArrayList = Collections.synchronizedList(new ArrayList<>());

        // den Abo-TrefferZähler zurücksetzen
        ProgData.getInstance().aboList.forEach(AboDataProps::clearCountHit);


        if (ProgData.getInstance().setDataList.getSetDataForAbo("") == null) {
            // dann fehlt ein Set für die Abos
            Platform.runLater(() -> new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.ABO));
            return;
        }


        // mit den bereits enthaltenen Download-URLs füllen
        Set<String> syncDownloadsAlreadyInTheListHash = Collections.synchronizedSet(new HashSet<>(500)); // für 90% übertrieben, für 10% immer noch zu wenig???
        downloadList.forEach((download) -> syncDownloadsAlreadyInTheListHash.add(download.getUrl()));

        final int sum = ProgData.getInstance().audioList.size() + ProgData.getInstance().filmList.size();
        act = 0;
        now = 0;
        search(false, sum, syncDownloadArrayList, syncDownloadsAlreadyInTheListHash);
        search(true, sum, syncDownloadArrayList, syncDownloadsAlreadyInTheListHash);

        if (found) {
            Platform.runLater(() -> {
                searchDownloadsAfter(syncDownloadArrayList, downloadList, syncDownloadsAlreadyInTheListHash);
                ProgData.downloadSearchDone = true; // braucht der AutoMode, damit er weiß, wann er anfangen kann
                P2Duration.counterStop("searchForNewDownloadsForAbos");
            });
        } else {
            ProgData.downloadSearchDone = true; // braucht der AutoMode, damit er weiß, wann er anfangen kann
            P2Duration.counterStop("searchForNewDownloadsForAbos");
        }
    }

    private static void search(boolean audio, int sum, List<DownloadData> syncDownloadArrayList, Set<String> syncDownloadsAlreadyInTheListHash) {
        // prüfen ob in "alle Filme" oder nur "nach Blacklist" gesucht werden soll
        final boolean checkWithBlackList = ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getValue();

        // und jetzt die Filmliste ablaufen
        FilmListMTP filmList = audio ? ProgData.getInstance().audioList : ProgData.getInstance().filmList;

        filmList.parallelStream().forEach(film -> {
            ++act;
            ++now;
            if (now > 5_000) {
                now = 0;
                final double percent = (double) act / sum;
                ProgData.busy.setProgress(percent);
            }

            final AboData aboData = film.getAbo();
            if (aboData == null) {
                //dann gibts dafür kein Abo
                //oder abo ist ausgeschaltet, ...
                return;
            }

            aboData.incrementCountHit();

            if (checkWithBlackList && BlacklistFilterFactory.
                    checkFilmIsBlackComplete(audio, film, ProgData.getInstance().blackList, false)) {
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
            aboData.setDate(new P2Date());
            final SetData setData = aboData.getSetData(ProgData.getInstance());
            DownloadData downloadData;

            if (syncDownloadArrayList.size() < ProgConst.DOWNLOAD_ADD_DIALOG_MAX_LOOK_FILE_SIZE) {
                downloadData = new DownloadData(audio, DownloadConstants.SRC_ABO, setData, film, aboData, "", "", true);
            } else {
                downloadData = new DownloadData(audio, DownloadConstants.SRC_ABO, setData, film, aboData, "", "", false);
            }

            syncDownloadArrayList.add(downloadData);
            found = true;
        });
    }

    private static void searchDownloadsAfter(List<DownloadData> syncDownloadArrayList,
                                             DownloadList downloadList, Set<String> syncDownloadsAlreadyInTheListHash) {
        checkDoubleNames(syncDownloadArrayList, downloadList);
        downloadList.addAll(syncDownloadArrayList);
        downloadList.setNumbersInList();
        syncDownloadArrayList.clear();
        syncDownloadsAlreadyInTheListHash.clear();

        // und jetzt die hits eintragen (hier, damit nicht bei jedem die Tabelle geändert werden muss)
        ProgData.getInstance().aboList.forEach(AboDataProps::setCountedHits);

        if (ProgData.getInstance().downloadList.getSize() == count) {
            // dann wurden evtl. nur zurückgestellte Downloads wieder aktiviert
            ProgData.getInstance().downloadList.setDownloadsChanged();
        }

        if (ProgConfig.DOWNLOAD_START_NOW.getValue() || ProgData.autoMode) {
            // und wenn gewollt auch gleich starten, kann kein Dialog aufgehen: false!
            P2Log.sysLog("Downloads aus Abos starten");
            ProgData.getInstance().downloadList.startAllDownloads();
        }
    }

    private static void checkDoubleNames(List<DownloadData> foundNewDownloads, List<DownloadData> downloadList) {
        // prüfen ob schon ein Download mit dem Zieldateinamen in der DownloadListe existiert
        try {
            Set<String> fileNames = Collections.synchronizedSet(new HashSet<>(foundNewDownloads.size() + downloadList.size()));
            downloadList.forEach((download) -> fileNames.add(download.getDestPathFile()));

            for (DownloadData foundDownload : foundNewDownloads) {
                if (fileNames.contains(foundDownload.getDestPathFile())) {
                    // dann einen neuen Namen suchen
                    int i = 1;
                    String newName = getNextFileName(foundDownload.getDestPathFile(), i);
                    while (fileNames.contains(newName)) {
                        // dann ist er schon drin
                        i += 1;
                        newName = getNextFileName(foundDownload.getDestPathFile(), i);
                    }
                    foundDownload.setFile(newName);
                }
                fileNames.add(foundDownload.getDestPathFile());
            }
        } catch (final Exception ex) {
            P2Log.errorLog(303021458, ex);
        }
    }

    private static void checkDoubleNames_old(List<DownloadData> foundNewDownloads, List<DownloadData> downloadList) {
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
            P2Log.errorLog(303021458, ex);
        }
    }

    private static String getNextFileName(String file, int i) {
        String suffix = P2FileUtils.getFileNameSuffix(file);
        String name = P2FileUtils.getFileNameWithOutExtension(file);
        String path = P2FileUtils.getPath(file);
        return Paths.get(path, name + "_" + i + "." + suffix).toString();
    }


    private static boolean searchName(List<DownloadData> searchDownloadList, String name) {
        return searchDownloadList.stream().anyMatch(download -> download.getDestPathFile().equals(name));
    }
}
