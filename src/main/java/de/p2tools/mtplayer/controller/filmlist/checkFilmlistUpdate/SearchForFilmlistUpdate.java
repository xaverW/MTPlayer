/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.filmlist.checkFilmlistUpdate;

import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;

public class SearchForFilmlistUpdate {

    private int counter = ProgConst.CHECK_FILMLIST_UPDATE_PROGRAMSTART;
    private String dateOfFilmlistFromLastCheck = "";
    private boolean checkedListWasNewer = false;

    public static SearchForFilmlistUpdate StartSearchForFilmlistUpdate() {
        SearchForFilmlistUpdate s = new SearchForFilmlistUpdate();
        s.startCheckForFilmlistUpdate();
        return s;
    }

    private void startCheckForFilmlistUpdate() {
        Listener.addListener(new Listener(Listener.EVENT_TIMER, SearchForFilmlistUpdate.class.getSimpleName()) {
            @Override
            public void ping() {
                try {
                    if (ProgData.getInstance().loadFilmlist.getPropLoadFilmlist()) {
                        // dann laden wir gerade
                        counter = 0;
                        return;
                    }

                    if (doCheck()) {
                        Platform.runLater(() -> ProgData.getInstance().mtPlayerController.setButtonFilmlistUpdate());
                    }

                } catch (final Exception ex) {
                    PLog.errorLog(963014785, ex);
                }
            }
        });
    }

    private boolean doCheck() {
        boolean ret = false;
        String date = ProgData.getInstance().filmlist.genDate();
        ++counter;

        if (counter < ProgConst.CHECK_FILMLIST_UPDATE) {
            // dann ists noch nicht soweit
            return false;
        }
        counter = 0;

        if (date.equals(dateOfFilmlistFromLastCheck) && checkedListWasNewer) {
            // dann wurde die Filmliste schon mal nicht geändert und
            // es ist schon bekannt, dass es eine neue Liste gibt
            return false;
        }
        dateOfFilmlistFromLastCheck = date;
        checkedListWasNewer = false;

        // URL direkt aus der Liste holen, sonst wird die URL-Liste aktualisiert!!
        final String url = ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get().isEmpty() ?
                ProgData.getInstance().searchFilmListUrls.getFilmlistUrlList_akt().getRand(null) :
                ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get();

        // dann Filmliste prüfen
//            if (new SearchUpdateWithId().hasNewRemoteFilmlist()) { //todo wenn drin und source ist URL!! dann das
        if (new SearchUpdateWithDate().hasNewRemoteFilmlist(url, date)) {
            checkedListWasNewer = true;
            ret = true;
        }

        return ret;
    }
}
