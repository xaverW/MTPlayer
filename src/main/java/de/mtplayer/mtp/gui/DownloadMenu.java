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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.MTShortcut;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.p2tools.p2Lib.tools.shortcut.PShortcutWorker;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DownloadMenu {
    final private VBox vBox;
    final private ProgData progData;
    BooleanProperty boolDivOn = ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.getBooleanProperty();
    BooleanProperty boolInfoOn = ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getBooleanProperty();

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
                "Downloads aktualisieren", "Liste der Downloads aktualisieren", new ProgIcons().FX_ICON_TOOLBAR_DOWNLOAD_REFRESH);
        final ToolBarButton btDownloadAll = new ToolBarButton(vBox,
                "alle Downloads starten", "alle Downloads starten", new ProgIcons().FX_ICON_TOOLBAR_DOWNLOAD_START_ALL);
        final ToolBarButton btDownloadClear = new ToolBarButton(vBox,
                "Downloads aufräumen", "Liste der Downloads aufräumen", new ProgIcons().FX_ICON_TOOLBAR_DOWNLOAD_CLEAN);

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btStartDownloads = new ToolBarButton(vBox,
                "Downloads Starten", "markierte Downloads starten", new ProgIcons().FX_ICON_TOOLBAR_DOWNLOAD_START);
        final ToolBarButton btDownloadBack = new ToolBarButton(vBox,
                "Downloads zurückstellen", "markierte Downloads zurückstellen", new ProgIcons().FX_ICON_TOOLBAR_DOWNLOAD_UNDO);
        final ToolBarButton btDownloadDel = new ToolBarButton(vBox,
                "Downloads löschen", "markierte Downloads löschen", new ProgIcons().FX_ICON_TOOLBAR_DOWNLOAD_DEL);

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btDownloadFilm = new ToolBarButton(vBox,
                "Film Starten", "gespeicherten Film abspielen", new ProgIcons().FX_ICON_TOOLBAR_DOWNLOAD_FILM_START);

        btDownloadRefresh.setOnAction(a -> progData.worker.searchForAbosAndMaybeStart());
        btDownloadAll.setOnAction(a -> progData.downloadGuiController.startDownload(true));
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
        mb.setGraphic(new ProgIcons().FX_ICON_TOOLBAR_MENU);
        mb.getStyleClass().add("btnFunctionWide");


        final MenuItem miDownloadStart = new MenuItem("Downloads starten");
        miDownloadStart.setOnAction(a -> progData.downloadGuiController.startDownload(false));
        PShortcutWorker.addShortCut(miDownloadStart, MTShortcut.SHORTCUT_DOWNLOAD_START);

        final MenuItem miDownloadStop = new MenuItem("Downloads stoppen");
        miDownloadStop.setOnAction(a -> progData.downloadGuiController.stopDownload(false));
        PShortcutWorker.addShortCut(miDownloadStop, MTShortcut.SHORTCUT_DOWNLOAD_STOP);

        final MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> progData.downloadGuiController.changeDownload());
        PShortcutWorker.addShortCut(miChange, MTShortcut.SHORTCUT_DOWNLOAD_CHANGE);

        mb.getItems().addAll(miDownloadStart, miDownloadStop, miChange);


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
        final MenuItem mbStartAll = new MenuItem("alle Downloads starten");
        mbStartAll.setOnAction(a -> progData.downloadGuiController.startDownload(true /* alle */));
        final MenuItem mbStopAll = new MenuItem("alle Downloads stoppen");
        mbStopAll.setOnAction(a -> progData.downloadGuiController.stopDownload(true /* alle */));
        final MenuItem mbStopWait = new MenuItem("alle wartenden Downloads stoppen");
        mbStopWait.setOnAction(a -> progData.downloadGuiController.stopWaitingDownloads());
        final MenuItem mbUpdateList = new MenuItem("Liste der Downloads aktualisieren");
        mbUpdateList.setOnAction(e -> progData.worker.searchForAbosAndMaybeStart());
        PShortcutWorker.addShortCut(mbUpdateList, MTShortcut.SHORTCUT_DOWNLOADS_UPDATE);

        final MenuItem mbClean = new MenuItem("Liste der Downloads aufräumen");
        mbClean.setOnAction(e -> progData.downloadGuiController.cleanUp());
        PShortcutWorker.addShortCut(mbClean, MTShortcut.SHORTCUT_DOWNLOADS_CLEAN_UP);

        Menu submenuAllDownloads = new Menu("alle Downloads");
        submenuAllDownloads.getItems().addAll(mbStartAll, mbStopAll, mbStopWait, mbUpdateList, mbClean);
        mb.getItems().addAll(submenuAllDownloads);


//        // Submenü "gespeicherte Filme"
//        final MenuItem miDownloadShown = new MenuItem("Filme als gesehen markieren");
//        miDownloadShown.setOnAction(a -> progData.downloadGuiController.setFilmShown());
//        final MenuItem miDownloadNotShown = new MenuItem("Filme als ungesehen markieren");
//        miDownloadNotShown.setOnAction(a -> progData.downloadGuiController.setFilmNotShown());
//        final MenuItem miPlayerDownload = new MenuItem("gespeicherten Film (Datei) abspielen");
//        miPlayerDownload.setOnAction(a -> progData.downloadGuiController.playFilm());
//        MenuItem miDeleteDownload = new MenuItem("gespeicherten Film (Datei) löschen");
//        miDeleteDownload.setOnAction(a -> progData.downloadGuiController.deleteFilmFile());
//        MenuItem miOpenDir = new MenuItem("Zielordner öffnen");
//        miOpenDir.setOnAction(e -> progData.downloadGuiController.openDestinationDir());
//        final MenuItem miFilmMediaCollection = new MenuItem("Titel in der Mediensammlung suchen");
//        miFilmMediaCollection.setOnAction(a -> progData.downloadGuiController.guiFilmMediaCollection());
//
//        Menu submenuFilm = new Menu("gespeicherten Film");
//        submenuFilm.getItems().addAll(miDownloadShown, miDownloadNotShown, miPlayerDownload,
//                miDeleteDownload, miOpenDir, miFilmMediaCollection);
//        mb.getItems().addAll(submenuFilm);

        MenuItem miMediaDb = new MenuItem("Titel in der Mediensammlung suchen");
        miMediaDb.setOnAction(a -> progData.downloadGuiController.guiFilmMediaCollection());
        PShortcutWorker.addShortCut(miMediaDb, MTShortcut.SHORTCUT_SEARCH_DOWNLOAD_IN_MEDIACOLLECTION);

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> progData.downloadGuiController.showFilmInfo());
        MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> progData.downloadGuiController.playUrl());
        MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> progData.downloadGuiController.copyUrl());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miMediaDb, miFilmInfo, miPlayUrl, miCopyUrl);


        final MenuItem miSelectAll = new MenuItem("alles auswählen");
        miSelectAll.setOnAction(a -> progData.downloadGuiController.selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> progData.downloadGuiController.invertSelection());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miSelectAll, miSelection);


        final CheckMenuItem miShowFilter = new CheckMenuItem("Filter anzeigen");
        miShowFilter.selectedProperty().bindBidirectional(boolDivOn);
        final CheckMenuItem miShowInfo = new CheckMenuItem("Infos anzeigen");
        miShowInfo.selectedProperty().bindBidirectional(boolInfoOn);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);

        vBox.getChildren().add(mb);

    }
}
