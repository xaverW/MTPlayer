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
import de.p2tools.mtplayer.controller.data.film.FilmData;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TableRow;


public class TableRowFilm<T> extends TableRow {

    private final BooleanProperty geoMelden;

    public TableRowFilm() {
        geoMelden = ProgConfig.SYSTEM_MARK_GEO;
    }

    @Override
    public void updateItem(Object f, boolean empty) {
        super.updateItem(f, empty);

        FilmData film = (FilmData) f;
        if (film == null || empty) {
            setStyle("");
        } else {
            if (film.isLive()) {
                // livestream
                for (int i = 0; i < getChildren().size(); i++) {
                    getChildren().get(i).setStyle(ProgColorList.FILM_LIVESTREAM.getCssFontBold());
                }

            } else if (geoMelden.get() && film.isGeoBlocked()) {
                // geogeblockt
                for (int i = 0; i < getChildren().size(); i++) {
                    getChildren().get(i).setStyle(ProgColorList.FILM_GEOBLOCK.getCssFontBold());
                }

            } else if (film.isNewFilm()) {
                // neue Filme
                for (int i = 0; i < getChildren().size(); i++) {
                    getChildren().get(i).setStyle(ProgColorList.FILM_NEW.getCssFont());
                }

            } else {
                for (int i = 0; i < getChildren().size(); i++) {
                    getChildren().get(i).setStyle("");
                }
            }

            if (film.isBookmark()) {
                setStyle(ProgColorList.FILM_BOOKMARK.getCssBackgroundAndSel());

            } else if (film.isShown()) {
                setStyle(ProgColorList.FILM_HISTORY.getCssBackgroundAndSel());

            } else {
                setStyle("");
            }

        }
    }
}
