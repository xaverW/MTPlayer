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


        // Submenü "Download"
        MenuItem miVorziehen = new MenuItem("Download vorziehen");
        miVorziehen.setOnAction(a -> downloadGuiController.preferDownload());
        MenuItem miDelete = new MenuItem("Download zurückstellen");
        miDelete.setOnAction(a -> downloadGuiController.moveDownloadBack());
        MenuItem miDeletePermanent = new MenuItem("Download aus Liste entfernen");
        miDeletePermanent.setOnAction(a -> downloadGuiController.deleteDownloads());
        MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> downloadGuiController.changeDownload());

        Menu submenuDownload = new Menu("Download");
        submenuDownload.getItems().addAll(miVorziehen, miDelete, miDeletePermanent, miChange);


        // Submenü "alle Downloads"
        MenuItem miStartAll = new MenuItem("alle Downloads starten");
        miStartAll.setOnAction(a -> downloadGuiController.startDownload(true /* alle */));
        MenuItem miStopAll = new MenuItem("alle Downloads stoppen");
        miStopAll.setOnAction(a -> downloadGuiController.stopDownload(true /* alle */));

        Menu submenuAllDownloads = new Menu("alle Downloads");
        submenuAllDownloads.getItems().addAll(miStartAll, miStopAll);


        MenuItem miStopWaiting = new MenuItem("wartende Downloads stoppen");
        miStopWaiting.setOnAction(a -> downloadGuiController.stopWaitingDownloads());

        MenuItem miUpdate = new MenuItem("Liste der Downloads aktualisieren");
        miUpdate.setOnAction(e -> progData.worker.searchForAbosAndMaybeStart());

        MenuItem miCleanUp = new MenuItem("Liste der Downloads aufräumen");
        miCleanUp.setOnAction(e -> downloadGuiController.cleanUp());

        // Submenü "gespeicherte Filme"
        MenuItem miPlayerDownload = new MenuItem("gespeicherten Film (Datei) abspielen");
        miPlayerDownload.setOnAction(a -> downloadGuiController.playFilm());
        MenuItem miDeleteDownload = new MenuItem("gespeicherten Film (Datei) löschen");
        miDeleteDownload.setOnAction(a -> downloadGuiController.deleteFilmFile());
        MenuItem miOpenDir = new MenuItem("Zielordner öffnen");
        miOpenDir.setOnAction(e -> downloadGuiController.openDestinationDir());

        Menu submenuFilm = new Menu("gespeicherten Film");
        submenuFilm.getItems().addAll(miPlayerDownload, miDeleteDownload, miOpenDir);

        // Submenü "Abo"
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

        Menu submenuAbo = new Menu("Abo");
        submenuAbo.getItems().addAll(miChangeAbo, miDelAbo);


        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> downloadGuiController.guiFilmMediaCollection());

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> downloadGuiController.showFilmInfo());

        MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> downloadGuiController.playUrl());

        MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> downloadGuiController.copyUrl());

        final MenuItem miSelectAll = new MenuItem("alles auswählen");
        miSelectAll.setOnAction(a -> tableView.getSelectionModel().selectAll());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> downloadGuiController.invertSelection());

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> new Table().resetTable(tableView, Table.TABLE.DOWNLOAD));

        contextMenu.getItems().addAll(
                miStart, miStop,

                new SeparatorMenuItem(),
                submenuDownload,
                submenuAllDownloads,

                new SeparatorMenuItem(),
                miStopWaiting, miUpdate, miCleanUp,

                new SeparatorMenuItem(),
                submenuFilm,
                submenuAbo,

                new SeparatorMenuItem(),
                miMediaDb, miFilmInfo, miPlayUrl, miCopyUrl,

                new SeparatorMenuItem(),
                miSelectAll, miSelection, resetTable);
    }

}
