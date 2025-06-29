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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.tools.table.TableBookmark;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import javafx.scene.control.*;

public class BookmarkTableContextMenu {

    private final ProgData progData;
    private final TableBookmark tableView;

    public BookmarkTableContextMenu(ProgData progData, TableBookmark tableView) {
        this.progData = progData;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(BookmarkData bookmarkData) {
        final ContextMenu contextMenu = new ContextMenu();

        if (bookmarkData != null) {
            if (bookmarkData.getFilmData() != null) {
                contextMenu.getItems().add(copyInfos(bookmarkData.getFilmData()));

            } else {
                MenuItem item = new MenuItem("Film-URL kopieren");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(bookmarkData.getUrl()));
                contextMenu.getItems().add(item);
            }
            contextMenu.getItems().add(new SeparatorMenuItem());
        }

        CheckMenuItem smallTableRow = new CheckMenuItem("Nur kleine Button anzeigen");
        smallTableRow.selectedProperty().bindBidirectional(ProgConfig.BOOKMARK_DIALOG_SMALL_TABLE_ROW);
        contextMenu.getItems().addAll(smallTableRow);
        CheckMenuItem toolTipTable = new CheckMenuItem("Infos beim Überfahren einer Zeile anzeigen");
        toolTipTable.selectedProperty().bindBidirectional(ProgConfig.BOOKMARK_DIALOG_SHOW_TABLE_TOOL_TIP);
        contextMenu.getItems().addAll(toolTipTable);
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().addAll(resetTable);
        return contextMenu;
    }

    public static Menu copyInfos(FilmDataMTP film) {
        final Menu subMenuURL = new Menu("Film-Infos kopieren");
        subMenuURL.setDisable(film == null);

        final MenuItem miCopyTheme = new MenuItem("Thema");
        miCopyTheme.setDisable(film == null);
        miCopyTheme.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getTheme()));

        final MenuItem miCopyName = new MenuItem("Titel");
        miCopyName.setDisable(film == null);
        miCopyName.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getTitle()));

        final MenuItem miCopyWeb = new MenuItem("Website-URL");
        miCopyWeb.setDisable(film == null);
        miCopyWeb.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getWebsite()));

        subMenuURL.getItems().addAll(miCopyTheme, miCopyName, miCopyWeb);


        final String uNormal = film == null ? "" : film.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL);
        String uHd = film == null ? "" : film.getUrlForResolution(FilmDataMTP.RESOLUTION_HD);
        String uLow = film == null ? "" : film.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL);
        String uSub = film == null ? "" : film.getUrlSubtitle();

        if (uHd.equals(uNormal)) {
            uHd = ""; // dann gibts keine
        }
        if (uLow.equals(uNormal)) {
            uLow = ""; // dann gibts keine
        }

        MenuItem item;
        if (!uHd.isEmpty() || !uLow.isEmpty() || !uSub.isEmpty()) {
            subMenuURL.getItems().add(new SeparatorMenuItem());
            // HD
            if (!uHd.isEmpty()) {
                item = new MenuItem("URL in HD-Auflösung");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmDataMTP.RESOLUTION_HD)));
                subMenuURL.getItems().add(item);
            }

            // normale Auflösung, gibts immer
            item = new MenuItem("URL in hoher Auflösung");
            item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);

            // kleine Auflösung
            if (!uLow.isEmpty()) {
                item = new MenuItem("URL in kleiner Auflösung");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL)));
                subMenuURL.getItems().add(item);
            }

            // Untertitel
            if (!film.getUrlSubtitle().isEmpty()) {
                subMenuURL.getItems().add(new SeparatorMenuItem());
                item = new MenuItem("Untertitel-URL");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlSubtitle()));
                subMenuURL.getItems().add(item);
            }

        } else {
            item = new MenuItem("Film-URL");
            item.setDisable(film == null);
            item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);
        }

        return subMenuURL;
    }
}
