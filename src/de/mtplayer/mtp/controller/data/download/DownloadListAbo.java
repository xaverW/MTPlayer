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
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.gui.dialog.NoSetDialogController;
import de.mtplayer.mtp.tools.filmListFilter.FilmlistBlackFilter;
import de.p2tools.p2Lib.tools.log.Duration;

import java.util.*;

public class DownloadListAbo {

    private final ProgData progData;
    private final DownloadList downloadList;

    public DownloadListAbo(ProgData progData, DownloadList downloadList) {
        this.progData = progData;
        this.downloadList = downloadList;
    }

    public synchronized void refreshAbos() {
        // fehlerhafte und nicht gestartete löschen, wird nicht gemeldet ob was gefunden wurde
        Duration.counterStart("DownloadListAbo.abosAuffrischen");
        List<Download> remove = new ArrayList<>();
        final Iterator<Download> it = downloadList.iterator();


//        downloadList.parallelStream()
//                .filter(d -> !d.isStateStoped())
//                .filter(d -> d.isAbo())
//                .filter(d -> d.isStateInit())
//                .collect(Collectors.toList()).removeAll(downloadList);
//
//        Duration.counterPing("DownloadListAbo.abosAuffrischen");
//        downloadList.parallelStream()
//                .filter(d -> !d.isStateStoped())
//                .filter(d -> d.isAbo())
//                .filter(d -> !d.isStateInit())
//                .filter(d -> d.isStateError())
//                .forEach(d -> d.resetDownload());
//
//        Duration.counterPing("DownloadListAbo.abosAuffrischen");
//        downloadList.parallelStream().forEach(d -> d.setZurueckgestellt(false));


        while (it.hasNext()) {
            final Download d = it.next();
            if (d.isStateStoped()) {
                // guter Rat teuer was da besser wäre??
                // wird auch nach dem Neuladen der Filmliste aufgerufen: also Finger weg
                continue;
            }
            if (!d.isAbo()) {
                continue;
            }
            if (d.isStateInit()) {
                // noch nicht gestartet
                remove.add(d);
            } else if (d.isStateError()) {
                // fehlerhafte
                d.resetDownload();
            }
        }
        downloadList.removeAll(remove);
        downloadList.forEach(d -> d.setPlacedBack(false));
        Duration.counterStop("DownloadListAbo.abosAuffrischen");
    }

    synchronized void searchForAbos() {
        // in der Filmliste nach passenden Filmen suchen und
        // in die Liste der Downloads eintragen
        Duration.counterStart("DownloadListAbo.abosSuchen");

        boolean found = false;
        Abo abo;
        ArrayList<Download> downloadArrayList = new ArrayList<>();

        // mit den bereits enthaltenen Download-URLs füllen
        final HashSet<String> listUrls = new HashSet<>(downloadList.size());
        downloadList.forEach((download) -> listUrls.add(download.getUrl()));

        // prüfen ob in "alle Filme" oder nur "nach Blacklist" gesucht werden soll
        final boolean checkWithBlackList = ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getBool();

        if (progData.setList.getPsetAbo("") == null) {
            // dann fehlt ein Set für die Abos
            new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO);
            return;
        }

        for (final Film film : progData.filmlist) {
            abo = progData.aboList.getAboForFilm_quick(film, true /* auch die Länge überprüfen */);

            if (abo == null) {
                // dann gibts dafür kein Abo
                continue;
            }

            if (!abo.getActive()) {
                // oder es ist ausgeschaltet
                continue;
            }

            if (checkWithBlackList) {
                // Blacklist auch bei Abos anwenden
                if (!FilmlistBlackFilter.checkBlacklistForDownloads(film)) {
                    continue;
                }
            }

            if (progData.erledigteAbos.checkIfExists(film.getUrlHistory())) {
                // ist schon mal geladen worden
                continue;
            }

            final SetData setData = progData.setList.getPsetAbo(abo.getPset());
            if (setData != null) {

                // mit der tatsächlichen URL prüfen, ob die URL schon in der Downloadliste ist
                final String urlDownload = film.getUrlForResolution(abo.getResolution());
                if (listUrls.contains(urlDownload)) {
                    continue;
                }

                // diesen Film in die Downloadliste eintragen
                listUrls.add(urlDownload);

                abo.setDate(new MDate());

                // nur den Namen anpassen, falls geändert oder altes Set nicht mehr existiert
                abo.setPset(setData.getName());

                // dann in die Liste schreiben
                downloadArrayList.add(new Download(setData, film, DownloadInfos.SRC_ABO, abo, "", "", "" /* Aufloesung */));
                found = true;
            }
        }
        if (found) {
            downloadList.addAll(downloadArrayList);
            downloadList.setNumbersInList();
        }
        listUrls.clear();
        Duration.counterStop("DownloadListAbo.abosSuchen");
    }

    public synchronized ArrayList<String> generateAboNameList(ArrayList<String> nameList) {
        final ArrayList<String> ret = new ArrayList<>();
        ret.add("");
        for (final String name : nameList) {
            if (name.isEmpty()) {
                continue;
            }
            final Optional<Download> opt =
                    downloadList.parallelStream().filter(d -> d.getAboName().equals(name)).findFirst();
            if (!opt.isPresent()) {
                ret.add(name);
            }
        }
        return ret;
    }
}
