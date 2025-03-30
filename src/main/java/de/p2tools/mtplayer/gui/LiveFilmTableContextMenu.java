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
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.starter.StartDownloadFactory;
import de.p2tools.mtplayer.gui.tools.table.TableLiveFilm;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.Optional;

public class LiveFilmTableContextMenu {

    private final ProgData progData;
    private final LiveFilmGuiController filmGuiController;
    private final TableLiveFilm tableView;

    public LiveFilmTableContextMenu(ProgData progData, LiveFilmGuiController filmGuiController, TableLiveFilm tableView) {
        this.progData = progData;
        this.filmGuiController = filmGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(FilmDataMTP film) {
        final ContextMenu contextMenu = new ContextMenu();

        // Start/Save
        MenuItem miStart = new MenuItem("Film abspielen");
        miStart.setOnAction(a -> {
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().liveFilmGuiController.getSel(true, true);
            filmSelection.ifPresent(FilmPlayFactory::playFilm);
        });

        MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> FilmSaveFactory.saveFilmList());
        miStart.setDisable(film == null);
        miSave.setDisable(film == null);
        contextMenu.getItems().addAll(miStart, miSave);

        Menu mStartFilm = FilmTableContextMenu.startFilmWithSet(progData, film); // Film mit Set starten
        if (mStartFilm != null) {
            contextMenu.getItems().add(mStartFilm);
        }

        // URL kopieren
        Menu mCopyUrl = FilmTableContextMenu.copyInfos(film);
        contextMenu.getItems().addAll(mCopyUrl);

        final MenuItem miLoadUt = new MenuItem("Untertitel speichern");
        miLoadUt.setDisable(film == null || film.getUrlSubtitle().isEmpty());
        miLoadUt.setOnAction(a -> StartDownloadFactory.downloadSubtitle(film, true));

        final MenuItem miLoadTxt = new MenuItem("Info-Datei speichern");
        miLoadTxt.setDisable(film == null);
        miLoadTxt.setOnAction(a -> StartDownloadFactory.downloadSubtitle(film, false));

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setDisable(film == null);
        miFilmInfo.setOnAction(a -> filmGuiController.showFilmInfo());

        MenuItem miMediaDb = new MenuItem("Film in der Mediensammlung suchen");
        miMediaDb.setDisable(film == null);
        miMediaDb.setOnAction(a -> filmGuiController.searchFilmInMediaCollection());

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miLoadUt, miLoadTxt, miFilmInfo, miMediaDb);

        MenuItem toolTipTable = new MenuItem(ProgConfig.LIVE_FILM_GUI_SHOW_TABLE_TOOL_TIP.getValue() ?
                "Keine Infos beim Überfahren einer Zeile anzeigen" : "Infos beim Überfahren einer Zeile anzeigen");
        toolTipTable.setOnAction(a -> ProgConfig.LIVE_FILM_GUI_SHOW_TABLE_TOOL_TIP.setValue(!ProgConfig.LIVE_FILM_GUI_SHOW_TABLE_TOOL_TIP.getValue()));
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(toolTipTable, resetTable);

        return contextMenu;
    }
}
