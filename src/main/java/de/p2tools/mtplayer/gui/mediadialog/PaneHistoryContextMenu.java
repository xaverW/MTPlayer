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

package de.p2tools.mtplayer.gui.mediadialog;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.data.history.HistoryData;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.util.ArrayList;

public class PaneHistoryContextMenu {

    private final ProgData progData;
    private final ArrayList<HistoryData> historyDataArrayList;
    private final boolean history;
    private final Stage stage;

    public PaneHistoryContextMenu(Stage stage, ArrayList<HistoryData> historyDataArrayList, boolean history) {
        this.stage = stage;
        this.historyDataArrayList = historyDataArrayList;
        this.history = history;
        this.progData = ProgData.getInstance();
    }

    public ContextMenu getContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu) {
        FilmDataMTP film = progData.filmList.getFilmByUrl(historyDataArrayList.get(0).getUrl());

        MenuItem miDelUrl = new MenuItem("Url aus der Liste löschen");
        miDelUrl.setOnAction(a -> {
            if (history) {
                progData.historyList.removeHistoryDataFromHistory(historyDataArrayList);
            } else {
                progData.historyListAbos.removeHistoryDataFromHistory(historyDataArrayList);
            }
        });

        MenuItem miCopyUrl = new MenuItem("URL kopieren");
        miCopyUrl.setOnAction(a -> {
            String str = "";
            for (HistoryData historyData : historyDataArrayList) {
                str += str.isEmpty() ? historyData.getUrl() : P2LibConst.LINE_SEPARATOR + historyData.getUrl();
            }
            P2ToolsFactory.copyToClipboard(str);
        });

        MenuItem miShowFilm = new MenuItem("Infos zum Film anzeigen");
        miShowFilm.setDisable(film == null);
        miShowFilm.setOnAction(a -> {
            FilmInfoDialogController.getInstance().setFilm(film);
            FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
        });

        MenuItem miDownload = new MenuItem("Download noch einmal anlegen");
        miDownload.setDisable(film == null);
        // todo audio
        miDownload.setOnAction(a -> FilmSaveFactory.saveFilm(false, film));

        contextMenu.getItems().addAll(miDelUrl, miCopyUrl, miShowFilm, miDownload);
    }
}
