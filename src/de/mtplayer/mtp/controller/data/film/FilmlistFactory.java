/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.controller.data.film;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.history.HistoryList;

public class FilmlistFactory {
    private FilmlistFactory() {
    }

    public static void markBookmarks() {
        Filmlist filmlist = ProgData.getInstance().filmlist;
        HistoryList bookmarks = ProgData.getInstance().bookmarks;

        filmlist.stream().forEach(film -> {
            if (bookmarks.checkIfUrlAlreadyIn(film.getUrlHistory())) {
                film.setBookmark(true);
            }
        });
    }

    public static void clearAllBookmarks() {
        Filmlist filmlist = ProgData.getInstance().filmlist;
        filmlist.stream().forEach(film -> film.setBookmark(false));
    }
}
