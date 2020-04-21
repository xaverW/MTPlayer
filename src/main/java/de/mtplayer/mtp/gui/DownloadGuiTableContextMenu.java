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
import de.mtplayer.mtp.gui.tools.table.Table;
import javafx.scene.control.*;

public class DownloadGuiTableContextMenu {

    private final ProgData progData;
    private final DownloadGuiController downloadGuiController;
    private final TableView tableView;

    public DownloadGuiTableContextMenu(ProgData progData, DownloadGuiController downloadGuiController, TableView tableView) {

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
        MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> downloadGuiController.changeDownload());

        miStart.setDisable(download == null);
        miStop.setDisable(download == null);
        miChange.setDisable(download == null);
        contextMenu.getItems().addAll(miStart, miStop, miChange);


        // Submenü "Download"
        MenuItem miPrefer = new MenuItem("Downloads vorziehen");
        miPrefer.setOnAction(a -> downloadGuiController.preferDownload());
        MenuItem miPutBack = new MenuItem("Downloads zurückstellen");
        miPutBack.setOnAction(a -> downloadGuiController.moveDownloadBack());
        MenuItem miRemove = new MenuItem("Downloads aus Liste entfernen");
        miRemove.setOnAction(a -> downloadGuiController.deleteDownloads());

        Menu submenuDownload = new Menu("Downloads");
        submenuDownload.setDisable(download == null);
        submenuDownload.getItems().addAll(miPrefer, miPutBack, miRemove);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(submenuDownload);


        // Submenü "alle Downloads"
        MenuItem miStartAll = new MenuItem("alle Downloads starten");
        miStartAll.setOnAction(a -> downloadGuiController.startDownload(true /* alle */));
        MenuItem miStopAll = new MenuItem("alle Downloads stoppen");
        miStopAll.setOnAction(a -> downloadGuiController.stopDownload(true /* alle */));
        MenuItem miStopWaiting = new MenuItem("alle wartenden Downloads stoppen");
        miStopWaiting.setOnAction(a -> downloadGuiController.stopWaitingDownloads());
        MenuItem miUpdate = new MenuItem("Liste der Downloads aktualisieren");
        miUpdate.setOnAction(e -> progData.worker.searchForAbosAndMaybeStart());
        MenuItem miCleanUp = new MenuItem("Liste der Downloads aufräumen");
        miCleanUp.setOnAction(e -> downloadGuiController.cleanUp());

        miStartAll.setDisable(download == null);
        miStopAll.setDisable(download == null);
        miStopWaiting.setDisable(download == null);

        Menu submenuAllDownloads = new Menu("alle Downloads");
        submenuAllDownloads.getItems().addAll(miStartAll, miStopAll, miStopWaiting, miUpdate, miCleanUp);
        contextMenu.getItems().addAll(submenuAllDownloads);

        
        // Submenü "gespeicherte Filme"
        final MenuItem miDownloadShown = new MenuItem("Filme als gesehen markieren");
        miDownloadShown.setOnAction(a -> downloadGuiController.setFilmShown());
        final MenuItem miDownloadNotShown = new MenuItem("Filme als ungesehen markieren");
        miDownloadNotShown.setOnAction(a -> downloadGuiController.setFilmNotShown());
        MenuItem miPlayerDownload = new MenuItem("gespeicherten Film (Datei) abspielen");
        miPlayerDownload.setOnAction(a -> downloadGuiController.playFilm());
        MenuItem miDeleteDownload = new MenuItem("gespeicherten Film (Datei) löschen");
        miDeleteDownload.setOnAction(a -> downloadGuiController.deleteFilmFile());
        MenuItem miOpenDir = new MenuItem("Zielordner öffnen");
        miOpenDir.setOnAction(e -> downloadGuiController.openDestinationDir());

        Menu submenuFilm = new Menu("gespeicherten Film");
        submenuFilm.setDisable(download == null);
        submenuFilm.getItems().addAll(miDownloadShown, miDownloadNotShown, miPlayerDownload, miDeleteDownload, miOpenDir);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(submenuFilm);


        // Submenü "Abo"
        MenuItem miChangeAbo = new MenuItem("Abo ändern");
        MenuItem miDelAbo = new MenuItem("Abo löschen");
        if (download != null && download.getAbo() != null) {
            miChangeAbo.setOnAction(event -> progData.aboList.changeAbo(download.getAbo()));
            miDelAbo.setOnAction(event -> progData.aboList.deleteAbo(download.getAbo()));
        } else {
            miChangeAbo.setDisable(true);
            miDelAbo.setDisable(true);
        }

        Menu submenuAbo = new Menu("Abo");
        submenuAbo.setDisable(download == null);
        submenuAbo.getItems().addAll(miChangeAbo, miDelAbo);
        contextMenu.getItems().addAll(submenuAbo);


        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> downloadGuiController.guiFilmMediaCollection());
        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> downloadGuiController.showFilmInfo());
        MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> downloadGuiController.playUrl());
        MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> downloadGuiController.copyUrl());

        miMediaDb.setDisable(download == null);
        miFilmInfo.setDisable(download == null);
        miPlayUrl.setDisable(download == null);
        miCopyUrl.setDisable(download == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miMediaDb, miFilmInfo, miPlayUrl, miCopyUrl);


        final MenuItem miSelectAll = new MenuItem("alles auswählen");
        miSelectAll.setOnAction(a -> tableView.getSelectionModel().selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> downloadGuiController.invertSelection());
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> new Table().resetTable(tableView, Table.TABLE.DOWNLOAD));

        miSelectAll.setDisable(download == null);
        miSelection.setDisable(download == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miSelectAll, miSelection, resetTable);
    }

}
