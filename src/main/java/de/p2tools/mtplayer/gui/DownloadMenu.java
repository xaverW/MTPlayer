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
import de.p2tools.mtplayer.controller.data.MTShortcut;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.p2Lib.tools.shortcut.PShortcutWorker;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DownloadMenu {
    final private VBox vBox;
    final private ProgData progData;

    public DownloadMenu(VBox vBox) {
        this.vBox = vBox;
        progData = ProgData.getInstance();
    }

    public void init() {
        vBox.getChildren().clear();

        initMenu();
        initButton();
    }

    private void initButton() {
        // Button
        VBox vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(0);
        vBoxSpace.setMinHeight(0);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btDownloadRefresh = new ToolBarButton(vBox,
                "Downloads aktualisieren", "Liste der Downloads aktualisieren", ProgIcons.Icons.FX_ICON_TOOLBAR_DOWNLOAD_REFRESH.getImageView());
        final ToolBarButton btDownloadAll = new ToolBarButton(vBox,
                "Alle Downloads starten", "Alle Downloads starten", ProgIcons.Icons.FX_ICON_TOOLBAR_DOWNLOAD_START_ALL.getImageView());
        final ToolBarButton btDownloadAllTime = new ToolBarButton(vBox,
                "Alle Downloads mit Startzeit starten", "Alle Downloads mit Startzeit starten", ProgIcons.Icons.FX_ICON_TOOLBAR_DOWNLOAD_START_ALL_TIME.getImageView());
        final ToolBarButton btDownloadClear = new ToolBarButton(vBox,
                "Downloads aufräumen", "Liste der Downloads aufräumen", ProgIcons.Icons.FX_ICON_TOOLBAR_DOWNLOAD_CLEAN.getImageView());

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btStartDownloads = new ToolBarButton(vBox,
                "Downloads Starten", "Markierte Downloads starten", ProgIcons.Icons.FX_ICON_TOOLBAR_DOWNLOAD_START.getImageView());
        final ToolBarButton btDownloadBack = new ToolBarButton(vBox,
                "Downloads zurückstellen", "Markierte Downloads zurückstellen", ProgIcons.Icons.FX_ICON_TOOLBAR_DOWNLOAD_UNDO.getImageView());
        final ToolBarButton btDownloadDel = new ToolBarButton(vBox,
                "Downloads löschen", "Markierte Downloads löschen", ProgIcons.Icons.FX_ICON_TOOLBAR_DOWNLOAD_DEL.getImageView());

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btDownloadFilm = new ToolBarButton(vBox,
                "Film Starten", "Gespeicherten Film abspielen", ProgIcons.Icons.FX_ICON_TOOLBAR_DOWNLOAD_FILM_START.getImageView());

        btDownloadRefresh.setOnAction(a -> progData.worker.searchForAbosAndMaybeStart());
        btDownloadAll.setOnAction(a -> progData.downloadGuiController.startDownload(true));
        btDownloadAllTime.setOnAction(a -> progData.downloadGuiController.startDownloadTime());
        btDownloadClear.setOnAction(a -> progData.downloadGuiController.cleanUp());
        btStartDownloads.setOnAction(a -> progData.downloadGuiController.startDownload(false));
        btDownloadBack.setOnAction(a -> progData.downloadGuiController.moveDownloadBack());
        btDownloadDel.setOnAction(a -> progData.downloadGuiController.deleteDownloads());
        btDownloadFilm.setOnAction(a -> progData.downloadGuiController.playFilm());
    }

    private void initMenu() {
        // MenuButton
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Downloadmenü anzeigen"));
        mb.setGraphic(ProgIcons.Icons.FX_ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-1");

        final MenuItem miDownloadStart = new MenuItem("Downloads starten");
        miDownloadStart.setOnAction(a -> progData.downloadGuiController.startDownload(false));
        PShortcutWorker.addShortCut(miDownloadStart, MTShortcut.SHORTCUT_DOWNLOAD_START);

        final MenuItem miDownloadStop = new MenuItem("Downloads stoppen");
        miDownloadStop.setOnAction(a -> progData.downloadGuiController.stopDownload(false));
        PShortcutWorker.addShortCut(miDownloadStop, MTShortcut.SHORTCUT_DOWNLOAD_STOP);

        final MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> progData.downloadGuiController.changeDownload());
        PShortcutWorker.addShortCut(miChange, MTShortcut.SHORTCUT_DOWNLOAD_CHANGE);

        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen");
        miUndo.setOnAction(a -> progData.downloadGuiController.undoDeleteDownload());
        PShortcutWorker.addShortCut(miUndo, MTShortcut.SHORTCUT_DOWNLOAD_UNDO_DELETE);
        miUndo.disableProperty().bind(Bindings.isEmpty(progData.downloadList.getUndoList()));

        mb.getItems().addAll(miDownloadStart, miDownloadStop, miChange, miUndo);

        // Submenü "Download"
        final MenuItem miPrefer = new MenuItem("Downloads vorziehen");
        miPrefer.setOnAction(a -> progData.downloadGuiController.preferDownload());
        final MenuItem miPutBack = new MenuItem("Downloads zurückstellen");
        miPutBack.setOnAction(a -> progData.downloadGuiController.moveDownloadBack());
        final MenuItem miRemove = new MenuItem("Downloads aus Liste entfernen");
        miRemove.setOnAction(a -> progData.downloadGuiController.deleteDownloads());

        Menu submenuDownload = new Menu("Downloads");
        submenuDownload.getItems().addAll(miPrefer, miPutBack, miRemove);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(submenuDownload);

        // Submenü "alle Downloads"
        final MenuItem mbStartAll = new MenuItem("Alle Downloads starten");
        mbStartAll.setOnAction(a -> progData.downloadGuiController.startDownload(true /* alle */));
        final MenuItem mbStartTimeAll = new MenuItem("Alle Downloads mit Startzeit starten");
        mbStartTimeAll.setOnAction(a -> progData.downloadGuiController.startDownloadTime());
        final MenuItem mbStopAll = new MenuItem("Alle Downloads stoppen");
        mbStopAll.setOnAction(a -> progData.downloadGuiController.stopDownload(true /* alle */));
        final MenuItem mbStopWait = new MenuItem("Alle wartenden Downloads stoppen");
        mbStopWait.setOnAction(a -> progData.downloadGuiController.stopWaitingDownloads());
        final MenuItem mbUpdateList = new MenuItem("Liste der Downloads aktualisieren");
        mbUpdateList.setOnAction(e -> progData.worker.searchForAbosAndMaybeStart());
        PShortcutWorker.addShortCut(mbUpdateList, MTShortcut.SHORTCUT_DOWNLOADS_UPDATE);

        final MenuItem mbClean = new MenuItem("Liste der Downloads aufräumen");
        mbClean.setOnAction(e -> progData.downloadGuiController.cleanUp());
        PShortcutWorker.addShortCut(mbClean, MTShortcut.SHORTCUT_DOWNLOADS_CLEAN_UP);

        Menu submenuAllDownloads = new Menu("Alle Downloads");
        submenuAllDownloads.getItems().addAll(mbStartAll, mbStartTimeAll, mbStopAll, mbStopWait, mbUpdateList, mbClean);
        mb.getItems().addAll(submenuAllDownloads);

        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> progData.downloadGuiController.guiFilmMediaCollection());
        PShortcutWorker.addShortCut(miMediaDb, MTShortcut.SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIACOLLECTION);

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> progData.downloadGuiController.showFilmInfo());
        PShortcutWorker.addShortCut(miFilmInfo, MTShortcut.SHORTCUT_INFO_FILM);

        MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> progData.downloadGuiController.playUrl());
        MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> progData.downloadGuiController.copyUrl());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miMediaDb, miFilmInfo, miPlayUrl, miCopyUrl);

        final MenuItem miSelectAll = new MenuItem("Alles auswählen");
        miSelectAll.setOnAction(a -> progData.downloadGuiController.selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> progData.downloadGuiController.invertSelection());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miSelectAll, miSelection);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden");
        //ausgeführt wird aber der Button im Tab Filme!!
        miShowFilter.setOnAction(a -> progData.mtPlayerController.setFilter());
        PShortcutWorker.addShortCut(miShowFilter, MTShortcut.SHORTCUT_SHOW_FILTER);

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden");
        miShowInfo.setOnAction(a -> progData.mtPlayerController.setInfos());
        PShortcutWorker.addShortCut(miShowInfo, MTShortcut.SHORTCUT_SHOW_INFOS);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }
}
