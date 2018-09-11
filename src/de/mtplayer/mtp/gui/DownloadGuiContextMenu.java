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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.gui.tools.Table;
import javafx.scene.control.*;

public class DownloadGuiContextMenu {

    private final ProgData progData;
    private final DownloadGuiController downloadGuiController;
    private final TableView tableView;

    public DownloadGuiContextMenu(ProgData progData, DownloadGuiController downloadGuiController, TableView tableView) {

        this.progData = progData;
        this.downloadGuiController = downloadGuiController;
        this.tableView = tableView;

    }

    public ContextMenu getContextMenu(Download download) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, download);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu, Download download) {

        MenuItem miStart = new MenuItem("Download starten");
        miStart.setOnAction(a -> downloadGuiController.startDownload(false));

        MenuItem miStop = new MenuItem("Download stoppen");
        miStop.setOnAction(a -> downloadGuiController.stopDownload(false));

        MenuItem miVorziehen = new MenuItem("Download vorziehen");
        miVorziehen.setOnAction(a -> downloadGuiController.preferDownload());

        MenuItem miDelete = new MenuItem("Download zurückstellen");
        miDelete.setOnAction(a -> downloadGuiController.moveDownloadBack());

        MenuItem miDeletePermanent = new MenuItem("Download aus Liste entfernen");
        miDeletePermanent.setOnAction(a -> downloadGuiController.deleteDownloads());

        MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> downloadGuiController.changeDownload());

        MenuItem miStartAll = new MenuItem("alle Downloads starten");
        miStartAll.setOnAction(a -> downloadGuiController.startDownload(true /* alle */));

        MenuItem miStopAll = new MenuItem("alle Downloads stoppen");
        miStopAll.setOnAction(a -> downloadGuiController.stopDownload(true /* alle */));

        MenuItem miStopWaiting = new MenuItem("wartende Downloads stoppen");
        miStopWaiting.setOnAction(a -> downloadGuiController.stopWaitingDownloads());

        MenuItem miUpdate = new MenuItem("Liste der Downloads aktualisieren");
        miUpdate.setOnAction(e -> progData.worker.searchForAbosAndMaybeStart());

        MenuItem miCleanUp = new MenuItem("Liste der Downloads aufräumen");
        miCleanUp.setOnAction(e -> downloadGuiController.cleanUp());

        MenuItem miPlayerDownload = new MenuItem("gespeicherten Film (Datei) abspielen");
        miPlayerDownload.setOnAction(a -> downloadGuiController.playFilm());

        MenuItem miDeleteDownload = new MenuItem("gespeicherten Film (Datei) löschen");
        miDeleteDownload.setOnAction(a -> downloadGuiController.deleteFilmFile());

        MenuItem miOpenDir = new MenuItem("Zielordner öffnen");
        miOpenDir.setOnAction(e -> downloadGuiController.openDestinationDir());

        // Abo
        Menu submenuAbo = new Menu("Abo");
        MenuItem miChangeAbo = new MenuItem("Abo ändern");
        MenuItem miDelAbo = new MenuItem("Abo löschen");

        if (download.getAbo() == null) {
            miChangeAbo.setDisable(true);
            miDelAbo.setDisable(true);
        } else {
            miChangeAbo.setOnAction(event ->
                    progData.aboList.changeAbo(download.getAbo()));
            miDelAbo.setOnAction(event -> progData.aboList.deleteAbo(download.getAbo()));
        }
        submenuAbo.getItems().addAll(miChangeAbo, miDelAbo);

        // MediaDB
        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> downloadGuiController.guiFilmMediaCollection());


        MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> downloadGuiController.playUrl());

        MenuItem miCopyUrl = new MenuItem("URL kopieren");
        miCopyUrl.setOnAction(a -> downloadGuiController.copyUrl());

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> downloadGuiController.showFilmInfo());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> downloadGuiController.invertSelection());

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> new Table().resetTable(tableView, Table.TABLE.DOWNLOAD));

        contextMenu.getItems().addAll(miStart, miStop,
                new SeparatorMenuItem(),
                miVorziehen, miDelete, miDeletePermanent, miChange,
                new SeparatorMenuItem(),
                miStartAll, miStopAll, miStopWaiting, miUpdate, miCleanUp,
                new SeparatorMenuItem(),
                miPlayerDownload, miDeleteDownload, miOpenDir,
                new SeparatorMenuItem(),
                submenuAbo,
                new SeparatorMenuItem(),
                miMediaDb, miPlayUrl, miCopyUrl, miFilmInfo,
                new SeparatorMenuItem(),
                miSelection, resetTable);

    }

}
