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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.gui.tools.table.TableDownload;
import de.p2tools.p2Lib.tools.PSystemUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class DownloadGuiTableContextMenu {

    private final ProgData progData;
    private final DownloadGuiController downloadGuiController;
    private final TableDownload tableView;

    public DownloadGuiTableContextMenu(final ProgData progData, final DownloadGuiController downloadGuiController, final TableDownload tableView) {
        this.progData = progData;
        this.downloadGuiController = downloadGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(final DownloadData download) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, download);
        return contextMenu;
    }

    private void getMenu(final ContextMenu contextMenu, final DownloadData download) {

        final MenuItem miStart = new MenuItem("Download starten");
        miStart.setOnAction(a -> downloadGuiController.startDownload(false));
        final MenuItem miStop = new MenuItem("Download stoppen");
        miStop.setOnAction(a -> downloadGuiController.stopDownload(false));
        final MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> downloadGuiController.changeDownload());

        miStart.setDisable(download == null);
        miStop.setDisable(download == null);
        miChange.setDisable(download == null);
        contextMenu.getItems().addAll(miStart, miStop, miChange);


        // Submenü "Download"
        final MenuItem miPrefer = new MenuItem("Downloads vorziehen");
        miPrefer.setOnAction(a -> downloadGuiController.preferDownload());
        final MenuItem miPutBack = new MenuItem("Downloads zurückstellen");
        miPutBack.setOnAction(a -> downloadGuiController.moveDownloadBack());
        final MenuItem miRemove = new MenuItem("Downloads aus Liste entfernen");
        miRemove.setOnAction(a -> downloadGuiController.deleteDownloads());

        final Menu submenuDownload = new Menu("Downloads");
        submenuDownload.setDisable(download == null);
        submenuDownload.getItems().addAll(miPrefer, miPutBack, miRemove);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(submenuDownload);


        // Submenü "alle Downloads"
        final MenuItem miStartAll = new MenuItem("Alle Downloads starten");
        miStartAll.setOnAction(a -> downloadGuiController.startDownload(true /* alle */));

        final MenuItem miStartTimeAll = new MenuItem("Alle Downloads mit Startzeit starten");
        miStartTimeAll.setOnAction(a -> progData.downloadGuiController.startDownloadTime());

        final MenuItem miStopAll = new MenuItem("Alle Downloads stoppen");
        miStopAll.setOnAction(a -> downloadGuiController.stopDownload(true /* alle */));
        final MenuItem miStopWaiting = new MenuItem("Alle wartenden Downloads stoppen");
        miStopWaiting.setOnAction(a -> downloadGuiController.stopWaitingDownloads());
        final MenuItem miUpdate = new MenuItem("Liste der Downloads aktualisieren");
        miUpdate.setOnAction(e -> progData.worker.searchForAbosAndMaybeStart());
        final MenuItem miCleanUp = new MenuItem("Liste der Downloads aufräumen");
        miCleanUp.setOnAction(e -> downloadGuiController.cleanUp());

        miStartAll.setDisable(download == null);
        miStartTimeAll.setDisable(download == null);
        miStopAll.setDisable(download == null);
        miStopWaiting.setDisable(download == null);

        final Menu submenuAllDownloads = new Menu("Alle Downloads");
        submenuAllDownloads.getItems().addAll(miStartAll, miStartTimeAll, miStopAll, miStopWaiting, miUpdate, miCleanUp);
        contextMenu.getItems().addAll(submenuAllDownloads);


        // Submenü "gespeicherte Filme"
        final MenuItem miDownloadShown = new MenuItem("Filme als gesehen markieren");
        miDownloadShown.setOnAction(a -> downloadGuiController.setFilmShown());
        final MenuItem miDownloadNotShown = new MenuItem("Filme als ungesehen markieren");
        miDownloadNotShown.setOnAction(a -> downloadGuiController.setFilmNotShown());
        final MenuItem miPlayerDownload = new MenuItem("Gespeicherten Film (Datei) abspielen");
        miPlayerDownload.setOnAction(a -> downloadGuiController.playFilm());
        final MenuItem miDeleteDownload = new MenuItem("Gespeicherten Film (Datei) löschen");
        miDeleteDownload.setOnAction(a -> downloadGuiController.deleteFilmFile());
        final MenuItem miOpenDir = new MenuItem("Zielordner öffnen");
        miOpenDir.setOnAction(e -> downloadGuiController.openDestinationDir());

        final Menu submenuFilm = new Menu("Gespeicherten Film");
        submenuFilm.setDisable(download == null);
        submenuFilm.getItems().addAll(miDownloadShown, miDownloadNotShown, miPlayerDownload, miDeleteDownload, miOpenDir);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(submenuFilm);


        // Submenü "Abo"
        final MenuItem miChangeAbo = new MenuItem("Abo ändern");
        final MenuItem miDelAbo = new MenuItem("Abo löschen");
        if (download != null && download.getAbo() != null) {
            miChangeAbo.setOnAction(event -> progData.aboList.changeAbo(download.getAbo()));
            miDelAbo.setOnAction(event -> progData.aboList.deleteAbo(download.getAbo()));
        } else {
            miChangeAbo.setDisable(true);
            miDelAbo.setDisable(true);
        }

        final Menu submenuAbo = new Menu("Abo");
        submenuAbo.setDisable(download == null);
        submenuAbo.getItems().addAll(miChangeAbo, miDelAbo);
        contextMenu.getItems().addAll(submenuAbo);


        final MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> downloadGuiController.guiFilmMediaCollection());
        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> downloadGuiController.showFilmInfo());
        final MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> downloadGuiController.playUrl());
        final MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> downloadGuiController.copyUrl());


        final MenuItem miCopyName = new MenuItem("Titel in die Zwischenablage kopieren");
        miCopyName.setOnAction(a -> {
            PSystemUtils.copyToClipboard(download.getTitle());
        });
        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren");
        miCopyTheme.setOnAction(a -> {
            PSystemUtils.copyToClipboard(download.getTheme());
        });

        miMediaDb.setDisable(download == null);
        miFilmInfo.setDisable(download == null);
        miPlayUrl.setDisable(download == null);
        miCopyUrl.setDisable(download == null);
        miCopyName.setDisable(download == null);
        miCopyTheme.setDisable(download == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miMediaDb, miFilmInfo, miPlayUrl, miCopyUrl, miCopyName, miCopyTheme);


        final MenuItem miSelectAll = new MenuItem("Alles auswählen");
        miSelectAll.setOnAction(a -> tableView.getSelectionModel().selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> downloadGuiController.invertSelection());
        final MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());

        miSelectAll.setDisable(download == null);
        miSelection.setDisable(download == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miSelectAll, miSelection, resetTable);
    }
}
