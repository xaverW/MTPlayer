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
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.starter.StartDownloadFactory;
import de.p2tools.mtplayer.gui.tools.table.TableLiveFilm;
import javafx.scene.control.*;

import java.util.Optional;

public class LiveFilmTableContextMenu {

    private final ProgData progData;
    private final LiveFilmGuiController liveFilmGuiController;
    private final TableLiveFilm tableView;

    public LiveFilmTableContextMenu(ProgData progData, LiveFilmGuiController liveFilmGuiController, TableLiveFilm tableView) {
        this.progData = progData;
        this.liveFilmGuiController = liveFilmGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(FilmDataMTP film) {
        final ContextMenu contextMenu = new ContextMenu();

        // Start/Save
        MenuItem miStart = new MenuItem("Film abspielen");
        miStart.setOnAction(a -> {
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().liveFilmGuiController.getSel(true, true);
            filmSelection.ifPresent(f -> FilmPlayFactory.playFilm(false, f));
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

        final MenuItem miLoadUt = new MenuItem("Untertitel speichern");
        miLoadUt.setDisable(film == null || film.getUrlSubtitle().isEmpty());
        miLoadUt.setOnAction(a -> StartDownloadFactory.downloadSubtitle(false, film, true));

        final MenuItem miLoadTxt = new MenuItem("Info-Datei speichern");
        miLoadTxt.setDisable(film == null);
        miLoadTxt.setOnAction(a -> StartDownloadFactory.downloadSubtitle(false, film, false));

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setDisable(film == null);
        miFilmInfo.setOnAction(a -> liveFilmGuiController.showFilmInfo());

        Menu mCopyUrl = FilmTableContextMenu.copyInfos(film);

        MenuItem miMediaDb = new MenuItem("Film in der Mediensammlung suchen");
        miMediaDb.setDisable(film == null);
        miMediaDb.setOnAction(a -> liveFilmGuiController.searchFilmInMediaCollection());

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miLoadUt, miLoadTxt, miFilmInfo, mCopyUrl, miMediaDb);

        contextMenu.getItems().add(new SeparatorMenuItem());
        CheckMenuItem smallTableRow = new CheckMenuItem("Nur kleine Button anzeigen");
        smallTableRow.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_TABLE_ROW_LIVE);
        CheckMenuItem toolTipTable = new CheckMenuItem("Infos beim Überfahren einer Zeile anzeigen");
        toolTipTable.selectedProperty().bindBidirectional(ProgConfig.LIVE_FILM_GUI_SHOW_TABLE_TOOL_TIP);
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().addAll(smallTableRow, toolTipTable, resetTable);

        return contextMenu;
    }
}
