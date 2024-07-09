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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.dialog.NoSetDialogController;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class DownloadsFromAbosFactory {

    private DownloadsFromAbosFactory() {
    }

    public static void searchForDownloadsFromAbosAndMaybeStart() {
        // über Menü oder Button: "Download auffrischen"
        // EVENT_BLACKLIST_CHANGED geändert und "Abo suchen" ist ein
        // SYSTEM_BLACKLIST_SHOW_ABO geändert und "Abo suchen" ist ein
        // AboList geändert und "Abo suchen" ist ein
        // workOnFilmListLoadFinished und "Abo suchen" ist ein oder AUTOMODE
        if (LoadFilmFactory.getInstance().loadFilmlist.getPropLoadFilmlist()) {
            // wird danach eh gemacht
            return;
        }

        if (ProgData.getInstance().setDataList.getSetDataForAbo() == null) {
            // SetData sind nicht eingerichtet
            Platform.runLater(() -> new NoSetDialogController(ProgData.getInstance(), NoSetDialogController.TEXT.ABO));
            return;
        }

        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                searchDownloadsFromAbos();
                return null;
            }
        }).start();
    }

    private static void searchDownloadsFromAbos() {
        ProgData.getInstance().mtPlayerController.workerAdd("Downloads suchen", 0.0, true);
        P2Duration.counterStart("searchForAbosAndMaybeStart");
        P2Log.sysLog("Downloads aus Abos suchen");

        //erledigte entfernen, nicht gestartete Abos entfernen und nach neuen Abos suchen
        final int count = ProgData.getInstance().downloadList.getSize();
        DownloadFactory.refreshDownloads(ProgData.getInstance().downloadList);
        DownloadFactory.searchForNewDownloadsForAbos(ProgData.getInstance().downloadList);

        if (ProgData.getInstance().downloadList.getSize() == count) {
            // dann wurden evtl. nur zurückgestellte Downloads wieder aktiviert
            ProgData.getInstance().downloadList.setDownloadsChanged();
        }

        if (ProgConfig.DOWNLOAD_START_NOW.getValue() || ProgData.autoMode) {
            // und wenn gewollt auch gleich starten, kann kein Dialog aufgehen: false!
            P2Log.sysLog("Downloads aus Abos starten");
            ProgData.getInstance().downloadList.startAllDownloads();
        }

        ProgData.downloadSearchDone = true; // braucht der AutoMode, damit er weiß, wann er anfangen kann
        P2Duration.counterStop("searchForAbosAndMaybeStart");
        ProgData.getInstance().mtPlayerController.workerRemove();
    }
}
