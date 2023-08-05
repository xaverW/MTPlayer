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
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.gui.tools.table.TableDownload;
import de.p2tools.p2lib.tools.PSystemUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class DownloadTableContextMenu {

    private final ProgData progData;
    private final DownloadGuiController downloadGuiController;
    private final TableDownload tableView;

    public DownloadTableContextMenu(final ProgData progData, final DownloadGuiController downloadGuiController, final TableDownload tableView) {
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
        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen");
        miUndo.setOnAction(a -> progData.downloadList.undoDownloads());

        miUndo.setDisable(progData.downloadList.getUndoList().isEmpty());
        miStart.setDisable(download == null);
        miStop.setDisable(download == null);
        miChange.setDisable(download == null);

        contextMenu.getItems().addAll(miStart, miStop, miChange, miUndo);

        // Submenü "Download"
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(addSubMenuDownload(download));

        // Submenü "alle Downloads"
        contextMenu.getItems().addAll(addSubMenuAllDownload(download));

        // Submenü "gespeicherte Filme"
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(addSubMenuFilm(download));

        // Submenü "Abo"
        contextMenu.getItems().addAll(addSubMenuAbo(download));

        // Blacklist
        Menu mBlacklist = addBlacklist(download);
        contextMenu.getItems().addAll(mBlacklist);


        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> downloadGuiController.showFilmInfo());
        final MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> downloadGuiController.playUrl());
        final MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> downloadGuiController.copyUrl());

        final MenuItem miMediaDb = new MenuItem("Download in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> downloadGuiController.searchFilmInMediaCollection());

        final MenuItem miCopyName = new MenuItem("Titel in die Zwischenablage kopieren");
        miCopyName.setOnAction(a -> {
            PSystemUtils.copyToClipboard(download.getTitle());
        });
        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren");
        miCopyTheme.setOnAction(a -> {
            PSystemUtils.copyToClipboard(download.getTheme());
        });

        miFilmInfo.setDisable(download == null);
        miPlayUrl.setDisable(download == null);
        miCopyUrl.setDisable(download == null);
        miMediaDb.setDisable(download == null);
        miCopyName.setDisable(download == null);
        miCopyTheme.setDisable(download == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miFilmInfo, miPlayUrl, miCopyUrl, miMediaDb, miCopyName, miCopyTheme);


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

    private Menu addBlacklist(DownloadData downloadData) {
        Menu submenuBlacklist = new Menu("Blacklist");

        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für den Film erstellen");
        miBlack.setOnAction(event -> BlacklistFactory.addBlackFilm(false));

        final MenuItem miBlackSenderTheme = new MenuItem("Sender und Thema direkt in die Blacklist einfügen");
        miBlackSenderTheme.setOnAction(event -> BlacklistFactory.addBlack(downloadData.getChannel(), downloadData.getTheme(), ""));
        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen");
        miBlackTheme.setOnAction(event -> BlacklistFactory.addBlack("", downloadData.getTheme(), ""));
        final MenuItem miBlackTitle = new MenuItem("Titel direkt in die Blacklist einfügen");
        miBlackTitle.setOnAction(event -> BlacklistFactory.addBlack("", "", downloadData.getTitle()));

        miBlack.setDisable(downloadData == null);
        miBlackSenderTheme.setDisable(downloadData == null);
        miBlackTheme.setDisable(downloadData == null);
        miBlackTitle.setDisable(downloadData == null);

        submenuBlacklist.getItems().addAll(miBlack, miBlackSenderTheme, miBlackTheme, miBlackTitle);
        return submenuBlacklist;
    }

    private Menu addSubMenuFilm(DownloadData download) {
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

        miDownloadShown.setDisable(download == null);
        miDownloadNotShown.setDisable(download == null);
        miPlayerDownload.setDisable(download == null);
        miDeleteDownload.setDisable(download == null);
        miOpenDir.setDisable(download == null);
        submenuFilm.getItems().addAll(miDownloadShown, miDownloadNotShown, miPlayerDownload, miDeleteDownload, miOpenDir);
        return submenuFilm;
    }

    private Menu addSubMenuAbo(DownloadData download) {
        final MenuItem miChangeAbo = new MenuItem("Abo ändern");
        final MenuItem miDelAbo = new MenuItem("Abo löschen");
        // shon hier, wird evtl. nochmal passive
        miChangeAbo.setDisable(download == null);
        miDelAbo.setDisable(download == null);

        if (download != null && download.getAbo() != null) {
            miChangeAbo.setOnAction(event -> AboListFactory.editAbo(download.getAbo()));
            miDelAbo.setOnAction(event -> AboListFactory.deleteAbo(download.getAbo()));
        } else {
            miChangeAbo.setDisable(true);
            miDelAbo.setDisable(true);
        }

        final Menu submenuAbo = new Menu("Abo");
        submenuAbo.getItems().addAll(miChangeAbo, miDelAbo);
        return submenuAbo;
    }

    private Menu addSubMenuDownload(DownloadData download) {
        final MenuItem miPrefer = new MenuItem("Downloads vorziehen");
        miPrefer.setOnAction(a -> downloadGuiController.preferDownload());
        final MenuItem miPutBack = new MenuItem("Downloads zurückstellen");
        miPutBack.setOnAction(a -> downloadGuiController.moveDownloadBack());
        final MenuItem miRemove = new MenuItem("Downloads aus Liste entfernen");
        miRemove.setOnAction(a -> downloadGuiController.deleteDownloads());

        final Menu submenuDownload = new Menu("Downloads");
        miPrefer.setDisable(download == null || !download.isStateStartedWaiting()); // macht nur dann Sinn
        miPutBack.setDisable(download == null);
        miRemove.setDisable(download == null);
        submenuDownload.getItems().addAll(miPrefer, miPutBack, miRemove);
        return submenuDownload;
    }

    private Menu addSubMenuAllDownload(DownloadData download) {
        final MenuItem miStartAll = new MenuItem("Alle Downloads starten");
        miStartAll.setOnAction(a -> downloadGuiController.startDownload(true /* alle */));

        final MenuItem miStartTimeAll = new MenuItem("Alle Downloads mit Startzeit starten");
        miStartTimeAll.setOnAction(a -> progData.downloadGuiController.startDownloadTime());

        final MenuItem miStopAll = new MenuItem("Alle Downloads stoppen");
        miStopAll.setOnAction(a -> downloadGuiController.stopDownload(true /* alle */));
        final MenuItem miStopWaiting = new MenuItem("Alle wartenden Downloads stoppen");
        miStopWaiting.setOnAction(a -> downloadGuiController.stopWaitingDownloads());
        final MenuItem miUpdate = new MenuItem("Liste der Downloads aktualisieren");
        miUpdate.setOnAction(e -> DownloadFactory.searchForAbosAndMaybeStart());
        final MenuItem miCleanUp = new MenuItem("Liste der Downloads aufräumen");
        miCleanUp.setOnAction(e -> DownloadFactory.cleanUpList(progData.downloadList));

        miStartAll.setDisable(download == null);
        miStartTimeAll.setDisable(download == null);
        miStopAll.setDisable(download == null);
        miStopWaiting.setDisable(download == null);

        final Menu submenuAllDownloads = new Menu("Alle Downloads");
        submenuAllDownloads.getItems().addAll(miStartAll, miStartTimeAll, miStopAll, miStopWaiting, miUpdate, miCleanUp);
        return submenuAllDownloads;
    }
}
