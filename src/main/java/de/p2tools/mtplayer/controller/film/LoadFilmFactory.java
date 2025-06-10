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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.p2lib.mediathek.filmdata.Filmlist;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadConst;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadFilmlist;

public class LoadFilmFactory {
    private static P2LoadFilmlist p2LoadFilmlist; // erledigt das Update der Filmliste

    private LoadFilmFactory() {
    }

    public static void loadFilmlistProgStart() {
        Filmlist<FilmDataMTP> filmlistNew = new FilmListMTP();
        Filmlist<FilmDataMTP> filmlistDiff = new FilmListMTP();
        p2LoadFilmlist = new P2LoadFilmlist(ProgData.getInstance().pEventHandler, filmlistNew, filmlistDiff);
        initLoadFactoryConst();
        p2LoadFilmlist.loadFilmlistProgStart();
    }

    public static void loadNewListFromWeb(boolean alwaysLoadNew) {
        //es wird immer eine neue Filmliste aus dem Web geladen
        initLoadFactoryConst();

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

    public static void initLoadFactoryConst() {
        P2LoadConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
        P2LoadConst.SYSTEM_LOAD_NOT_SENDER = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue();
        P2LoadConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        P2LoadConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
        P2LoadConst.removeDiacritic = ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue();
        P2LoadConst.userAgent = ProgConfig.SYSTEM_USERAGENT.getValue();
        P2LoadConst.firstProgramStart = ProgData.firstProgramStart;
        P2LoadConst.debug = ProgData.debug;
        P2LoadConst.primaryStage = ProgData.getInstance().primaryStage;
        P2LoadConst.p2EventHandler = ProgData.getInstance().pEventHandler;

        P2LoadConst.loadNewFilmlistOnProgramStart = ProgConfig.SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART.getValue()
                || ProgData.autoMode; // wenn gewollt oder im AutoMode immer laden
        P2LoadConst.dateStoredFilmlist = ProgConfig.SYSTEM_FILMLIST_DATE;


        P2LoadConst.localFilmListFile = ProgInfos.getLocalFilmListFile();
        P2LoadConst.filmlistLocal = ProgData.getInstance().filmList;
        P2LoadConst.filmListUrl = ProgData.filmListUrl;


        // ProgData.getInstance().filmListFilter.clearCounter(); //todo evtl. nur beim Neuladen einer kompletten Liste??
        if (ProgConfig.SYSTEM_FILMLIST_FILTER.getValue() == BlacklistFilterFactory.BLACKLILST_FILTER_OFF) {
            //ist sonst evtl. noch von "vorher" gesetzt
            P2LoadConst.checker = null;

        } else if (ProgConfig.SYSTEM_FILMLIST_FILTER.getValue() == BlacklistFilterFactory.BLACKLILST_FILTER_ON) {
            //dann sollen Filme geprüft werden
            ProgData.getInstance().filmListFilter.clearCounter();
            P2LoadConst.checker = filmData -> BlacklistFilterFactory.checkFilmAndCountHits(filmData,
                    ProgData.getInstance().filmListFilter, true);

        } else {
            //dann ist er inverse
            ProgData.getInstance().filmListFilter.clearCounter();
            P2LoadConst.checker = filmData -> !BlacklistFilterFactory.checkFilmAndCountHits(filmData,
                    ProgData.getInstance().filmListFilter, true);
        }
    }
}
