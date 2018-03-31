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

package de.mtplayer.mtp.gui.mediaDialog;

import de.mtplayer.mLib.tools.SysTools;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.HistoryData;
import de.mtplayer.mtp.controller.data.film.Film;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;

public class MediaConfigHistoryPaneContextMenu {

    private final Daten daten;
    private final ArrayList<HistoryData> historyDataArrayList;
    private final boolean history;

    public MediaConfigHistoryPaneContextMenu(ArrayList<HistoryData> historyDataArrayList, boolean history) {
        this.daten = Daten.getInstance();
        this.historyDataArrayList = historyDataArrayList;
        this.history = history;
    }

    public ContextMenu getContextMenue() {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu) {

        Film film = daten.filmList.getFilmByUrl(historyDataArrayList.get(0).getUrl());

        // Start/Stop
        MenuItem miDelUrl = new MenuItem("Url aus der Liste löschen");
        miDelUrl.setOnAction(a -> {
            if (history) {
                daten.history.removeListFromHistory(historyDataArrayList);
            } else {
                daten.erledigteAbos.removeListFromHistory(historyDataArrayList);
            }
        });

        MenuItem miCopyUrl = new MenuItem("URL kopieren");
        miCopyUrl.setOnAction(a -> {
            String str = "";
            for (HistoryData historyData : historyDataArrayList) {
                str += str.isEmpty() ? historyData.getUrl() : "\n" + historyData.getUrl();
            }
            SysTools.copyToClipboard(str);
        });

        MenuItem miShowFilm = new MenuItem("Infos zum Film anzeigen");
        miShowFilm.setDisable(film == null);
        miShowFilm.setOnAction(a -> {
            daten.filmInfosDialogController.set(film);
            daten.filmInfosDialogController.showFilmInfo();
        });

        MenuItem miDownload = new MenuItem("Download noch einmal anlegen");
        miDownload.setDisable(film == null);
        miDownload.setOnAction(a -> daten.filmList.saveFilm(film, null));

        contextMenu.getItems().addAll(miDelUrl, miCopyUrl, miShowFilm, miDownload);

    }

}
