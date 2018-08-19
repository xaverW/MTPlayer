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


package de.mtplayer.mtp.controller.filmlist.checkFilmlistUpdate;

import de.mtplayer.mtp.controller.config.ProgConst;

public class SearchForFilmlistUpdate {

    private int counter = ProgConst.CHECK_FILMLIST_UPDATE_PROGRAMSTART;
    private String dateOfFilmlistFromLastCheck = "";
    private boolean checkedListWasNewer = false;

    public boolean doCheck(String source, String date) {
        boolean ret = false;
        ++counter;
        if (counter > ProgConst.CHECK_FILMLIST_UPDATE) {
//        if (counter > 10) {
            counter = 0;

            if (date.equals(dateOfFilmlistFromLastCheck) && checkedListWasNewer) {
                // dann wurde die Filmliste schon mal nicht geändert und
                // es ist schon bekannt, dass es eine neue Liste gibt
                return false;
            }

            dateOfFilmlistFromLastCheck = date;
            checkedListWasNewer = false;

            // dann Filmliste prüfen
//            if (new SearchUpdateWithId().hasNewRemoteFilmlist()) { //todo wenn drin und source ist URL!! dann das
            if (new SearchUpdateWithDate().hasNewRemoteFilmlist(source, date)) {
                checkedListWasNewer = true;
                ret = true;
            }
        }

        return ret;
    }

}
