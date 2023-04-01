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

    public static void addBlack() {
        //aus markiertem Film, ein Black erstellen
        //Dialog anzeigen
        final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel();
        if (!filmSelection.isPresent()) {
            return;
        }

        BlackData blackData = new BlackData(filmSelection.get().getChannel(), filmSelection.get().getTheme(),
                filmSelection.get().getTitle(), "");
        AddBlackListDialogController addBlacklistDialogController =
                new AddBlackListDialogController(ProgData.getInstance(), filmSelection.get(), blackData);

        if (!addBlacklistDialogController.isOk()) {
            //dann doch nicht
            return;
        }
        ProgData.getInstance().blackList.addAndNotify(blackData);
    }

    public static void addBlackTheme() {
        //aus markiertem Film ein Black erstellen
        //Dialog anzeigen
        final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel();
        if (!filmSelection.isPresent()) {
            return;
        }
        addBlack("", filmSelection.get().getTheme(), "");
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
}
