/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackList;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmlistMTP;
import de.p2tools.mtplayer.gui.dialog.AddBlackListDialogController;
import de.p2tools.p2lib.tools.duration.PDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlacklistFactory {
    private BlacklistFactory() {
    }

    public static void addBlackFilm() {
        // aus dem Menü: mit markiertem Film ein Black erstellen
        // Dialog anzeigen
        final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel(true, true);
        if (filmSelection.isEmpty()) {
            return;
        }

        BlackData blackData = new BlackData(filmSelection.get().getChannel(), filmSelection.get().getTheme(),
                filmSelection.get().getTitle(), "");
        AddBlackListDialogController addBlacklistDialogController =
                new AddBlackListDialogController(ProgData.getInstance(), filmSelection.get().FILM_CHANNEL_STR,
                        filmSelection.get().getTheme(), filmSelection.get().getTitle(), blackData);

        if (!addBlacklistDialogController.isOk()) {
            //dann doch nicht
            return;
        }
        ProgData.getInstance().blackList.addAndNotify(blackData);
    }

    public static void addBlackDownload() {
        // aus dem Menü: mit markiertem Download ein Black erstellen
        // Dialog anzeigen
        final Optional<DownloadData> downloadData = ProgData.getInstance().downloadGuiController.getSel(true);
        if (downloadData.isEmpty()) {
            return;
        }

        BlackData blackData = new BlackData(downloadData.get().getChannel(), downloadData.get().getTheme(),
                downloadData.get().getTitle(), "");
        AddBlackListDialogController addBlacklistDialogController =
                new AddBlackListDialogController(ProgData.getInstance(), downloadData.get().getChannel(),
                        downloadData.get().getTheme(), downloadData.get().getTitle(), blackData);

        if (!addBlacklistDialogController.isOk()) {
            //dann doch nicht
            return;
        }
        ProgData.getInstance().blackList.addAndNotify(blackData);
    }

    public static void addBlackThemeFilm() {
        // aus dem Menü: mit markiertem Film ein Black erstellen
        // Dialog anzeigen
        final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel(true, true);
        if (filmSelection.isEmpty()) {
            return;
        }
        addBlack("", filmSelection.get().getTheme(), "");
    }

    public static void addBlackThemeDownload() {
        // aus dem Menü: mit markiertem Film ein Black erstellen
        // Dialog anzeigen
        final Optional<DownloadData> downloadData = ProgData.getInstance().downloadGuiController.getSel(true);
        if (downloadData.isEmpty()) {
            return;
        }
        addBlack("", downloadData.get().getTheme(), "");
    }

    public static void addBlack(String sender, String theme, String titel) {
        BlackData blackData = new BlackData(sender, theme, titel, "");
        ProgData.getInstance().blackList.addAndNotify(blackData);
    }

    public static synchronized void countHits(BlackData blackData) {
        //Aufruf mit Button zum Zählen
        //hier wird ein BlackDate gegen die Filmliste gefiltert und die Treffer ermittelt
        List<BlackData> bl = new ArrayList<>();
        bl.add(blackData);
        countHits(bl);
    }

    public static synchronized void countHits(List<BlackData> list) {
        //Aufruf mit Button zum Zählen
        //hier wird die Blacklist gegen die Filmliste gefiltert und die Treffer
        //für *jeden* Blacklist-Eintrag ermittelt, wird nicht nach einem Treffer abgebrochen
        PDuration.counterStart("countHitsList");
        for (BlackData bl : list) {
            bl.clearCounter();
        }
        final FilmlistMTP filmDataMTPS = ProgData.getInstance().filmlist;
        if (filmDataMTPS != null) {
            filmDataMTPS.parallelStream().forEach(film ->
                    BlacklistFilterFactory.checkFilmIsBlockedAndCountHits(film, list));
        }
        PDuration.counterStop("countHitsList");
    }

    public static boolean blackIsEmpty(BlackData blackData) {
        // true, wenn es das Black schon gibt
        if (blackData.getChannel().isEmpty() &&
                blackData.getTheme().isEmpty() &&
                blackData.getTitle().isEmpty() &&
                blackData.getThemeTitle().isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean blackExistsAlready(BlackData blackData, List<BlackData> list) {
        // true, wenn es das Black schon gibt
        for (final BlackData data : list) {
            if (data.getChannel().equalsIgnoreCase(blackData.getChannel()) &&
                    data.getTheme().equalsIgnoreCase(blackData.getTheme()) &&

                    ((data.getTheme().isEmpty() && blackData.getTheme().isEmpty()) ||
                            data.isThemeExact() == blackData.isThemeExact()) &&

                    data.getTitle().equalsIgnoreCase(blackData.getTitle()) &&
                    data.getThemeTitle().equalsIgnoreCase(blackData.getThemeTitle())) {
                return true;
            }
        }
        return false;
    }

    public static void addStandardsList(BlackList list) {
        //nach Auftreten sortiert!
        BlackData bl = new BlackData("", "", "- Audiodeskription", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "(Audiodeskription)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "(Gebärdensprache)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "mit Gebärdensprache", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "(mit Untertitel)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "(Originalversion mit Untertitel)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "in Gebärdensprache", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "in Gebärdensprache", "", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData("", "", "\"Trailer:\"", "");
        bl.setThemeExact(false);
        list.add(bl);
    }
}
