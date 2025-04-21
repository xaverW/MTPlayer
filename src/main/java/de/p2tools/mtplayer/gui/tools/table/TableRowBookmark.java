/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;


public class TableRowBookmark<T> extends TableRow<T> {

    public TableRowBookmark() {
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setStyle("");
            setTooltip(null);

        } else {
            BookmarkData bookmarkData = (BookmarkData) item;
            FilmDataMTP film = bookmarkData.getFilmData();

            if (ProgConfig.BOOKMARK_DIALOG_SHOW_TABLE_TOOL_TIP.getValue()) {
                setTooltip(new Tooltip(bookmarkData.getTheme() + "\n" + bookmarkData.getTitle()));
            }

            if (bookmarkData.getFilmData() == null) {
                setStyle(ProgColorList.BOOKMARK_NO_FILM.getCssBackground());

            } else if (film != null && film.isShown()) {
                setStyle(ProgColorList.FILM_HISTORY.getCssBackground());

            } else {
                setStyle("");
            }
        }
    }
}
