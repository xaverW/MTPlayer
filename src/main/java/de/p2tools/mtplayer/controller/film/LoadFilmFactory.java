/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.film;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.mediathek.filmdata.Filmlist;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadConst;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadFilmlist;

public class LoadFilmFactory {
    private static P2LoadFilmlist p2LoadFilmlist; // erledigt das Update der Filmliste

    private LoadFilmFactory() {
    }

    public static void loadFilmlistProgStart() {
        LoadFactory.initLoadFactoryConst();

        Filmlist<FilmDataMTP> filmlistNew = new FilmListMTP();
        Filmlist<FilmDataMTP> filmlistDiff = new FilmListMTP();
        p2LoadFilmlist = new P2LoadFilmlist(ProgData.getInstance().pEventHandler, filmlistNew, filmlistDiff);
        p2LoadFilmlist.loadFilmlistProgStart();
    }

    public static void loadNewListFromWeb(boolean alwaysLoadNew) {
        // es wird immer eine neue Filmliste aus dem Web geladen
        // Button oder automatisch wenns eine neue gibt

        LoadFactory.initLoadFactoryConst();
        if (!alwaysLoadNew && ProgData.getInstance().filmGuiController != null /*mal vorsichtshalber*/) {
            // sonst machts keinen Sinn, sind dann ja alle neu
            ProgData.getInstance().filmGuiController.getSel(true, false); // damit die letzte Pos gesetzt wird
        }

        Filmlist<FilmDataMTP> filmlistNew = new FilmListMTP();
        Filmlist<FilmDataMTP> filmlistDiff = new FilmListMTP();
        p2LoadFilmlist = new P2LoadFilmlist(ProgData.getInstance().pEventHandler, filmlistNew, filmlistDiff);
        p2LoadFilmlist.loadNewFilmlistFromWeb(alwaysLoadNew/*, ProgInfos.getLocalFilmListFile()*/);
    }

    public static void setLoadStop() {
        if (p2LoadFilmlist != null) {
            P2LoadConst.stop.set(true);
        }
    }
}
