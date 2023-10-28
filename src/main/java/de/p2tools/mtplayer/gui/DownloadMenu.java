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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.MTPlayerFactory;
import de.p2tools.mtplayer.ShortKeyFactory;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.config.ProgShortcut;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.tools.shortcut.PShortcutWorker;
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
        vBox.getChildren().add(P2GuiTools.getVDistance(10));
        final ToolBarButton btnRefresh = new ToolBarButton(vBox,
                "Downloads aktualisieren", "Liste der Downloads aktualisieren", ProgIconsMTPlayer.ICON_TOOLBAR_DOWNLOAD_REFRESH.getImageView());

        vBox.getChildren().add(P2GuiTools.getVDistance(10));
        final ToolBarButton btnStart = new ToolBarButton(vBox,
                "Downloads Starten", "Markierte Downloads starten", ProgIconsMTPlayer.ICON_TOOLBAR_DOWNLOAD_START.getImageView());
        final ToolBarButton btnStartAll = new ToolBarButton(vBox,
                "Alle Downloads starten", "Alle Downloads starten", ProgIconsMTPlayer.ICON_TOOLBAR_DOWNLOAD_START_ALL.getImageView());
        final ToolBarButton btStartAllTime = new ToolBarButton(vBox,
                "Alle Downloads mit Startzeit starten", "Alle Downloads mit Startzeit starten", ProgIconsMTPlayer.ICON_TOOLBAR_DOWNLOAD_START_ALL_TIME.getImageView());

        vBox.getChildren().add(P2GuiTools.getVDistance(10));
        final ToolBarButton btnBack = new ToolBarButton(vBox,
                "Downloads zurückstellen", "Markierte Downloads zurückstellen", ProgIconsMTPlayer.ICON_TOOLBAR_DOWNLOAD_UNDO.getImageView());
        final ToolBarButton btnDel = new ToolBarButton(vBox,
                "Downloads löschen", "Markierte Downloads löschen", ProgIconsMTPlayer.ICON_TOOLBAR_DOWNLOAD_DEL.getImageView());
        final ToolBarButton btnChange = new ToolBarButton(vBox,
                "Downloads ändern", "Markierte Downloads ändern", ProgIconsMTPlayer.ICON_TOOLBAR_ABO_CONFIG.getImageView());
        final ToolBarButton btnClear = new ToolBarButton(vBox,
                "Downloads aufräumen", "Liste der Downloads aufräumen", ProgIconsMTPlayer.ICON_TOOLBAR_DOWNLOAD_CLEAN.getImageView());

        vBox.getChildren().add(P2GuiTools.getVDistance(10));
        final ToolBarButton btnDownloadFilm = new ToolBarButton(vBox,
                "Film Starten", "Gespeicherten Film abspielen", ProgIconsMTPlayer.ICON_TOOLBAR_DOWNLOAD_FILM_START.getImageView());

        btnRefresh.setOnAction(a -> DownloadFactory.searchForAbosAndMaybeStart());
        btnClear.setOnAction(a -> DownloadFactory.cleanUpList(progData.downloadList));
        btnChange.setOnAction(a -> progData.downloadGuiController.changeDownload());
        btnStart.setOnAction(a -> progData.downloadGuiController.startDownload(false));
        btnStartAll.setOnAction(a -> progData.downloadGuiController.startDownload(true));
        btStartAllTime.setOnAction(a -> progData.downloadGuiController.startDownloadTime());
        btnBack.setOnAction(a -> progData.downloadGuiController.moveDownloadBack());
        btnDel.setOnAction(a -> progData.downloadGuiController.deleteDownloads());
        btnDownloadFilm.setOnAction(a -> progData.downloadGuiController.playFilm());
    }

    private void initMenu() {
        // MenuButton
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Downloadmenü anzeigen"));
        mb.setGraphic(ProgIconsMTPlayer.ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-2");

        final MenuItem miDownloadStart = new MenuItem("Downloads starten");
        miDownloadStart.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            progData.downloadGuiController.startDownload(false);
        });
        PShortcutWorker.addShortCut(miDownloadStart, ProgShortcut.SHORTCUT_DOWNLOAD_START);

        final MenuItem miDownloadStop = new MenuItem("Downloads stoppen");
        miDownloadStop.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            progData.downloadGuiController.stopDownload(false);
        });
        PShortcutWorker.addShortCut(miDownloadStop, ProgShortcut.SHORTCUT_DOWNLOAD_STOP);

        final MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            progData.downloadGuiController.changeDownload();
        });
        PShortcutWorker.addShortCut(miChange, ProgShortcut.SHORTCUT_DOWNLOAD_CHANGE);

        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen" + ShortKeyFactory.SHORT_CUT_LEER +
                ProgShortcut.SHORTCUT_UNDO_DELETE.getActShortcut());
        miUndo.setOnAction(a -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            progData.downloadList.undoDownloads();
        });
//        PShortcutWorker.addShortCut(miUndo, ProgShortcut.SHORTCUT_DOWNLOAD_UNDO_DELETE);
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
        mbUpdateList.setOnAction(e -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            DownloadFactory.searchForAbosAndMaybeStart();
        });
        PShortcutWorker.addShortCut(mbUpdateList, ProgShortcut.SHORTCUT_DOWNLOADS_UPDATE);

        final MenuItem mbClean = new MenuItem("Liste der Downloads aufräumen");
        mbClean.setOnAction(e -> {
            if (MTPlayerController.paneShown != MTPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            DownloadFactory.cleanUpList(progData.downloadList);
        });
        PShortcutWorker.addShortCut(mbClean, ProgShortcut.SHORTCUT_DOWNLOADS_CLEAN_UP);

        Menu submenuAllDownloads = new Menu("Alle Downloads");
        submenuAllDownloads.getItems().addAll(mbStartAll, mbStartTimeAll, mbStopAll, mbStopWait, mbUpdateList, mbClean);
        mb.getItems().addAll(submenuAllDownloads);

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen" + ShortKeyFactory.SHORT_CUT_LEER +
                ProgShortcut.SHORTCUT_INFO_FILM.getActShortcut());
        miFilmInfo.setOnAction(a -> {
            progData.downloadGuiController.showFilmInfo();
        });

        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_COPY_FILM_THEME_TO_CLIPBOARD.getActShortcut());
        miCopyTheme.setOnAction(a -> MTPlayerFactory.copyTheme());

        final MenuItem miCopyTitle = new MenuItem("Titel in die Zwischenablage kopieren" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_COPY_FILM_TITLE_TO_CLIPBOARD.getActShortcut());
        miCopyTitle.setOnAction(a -> MTPlayerFactory.copyTitle());

        //Blacklist
        Menu submenuBlacklist = new Menu("Blacklist");
        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für den Film erstellen" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_ADD_BLACKLIST.getActShortcut());
        miBlack.setOnAction(event -> BlacklistFactory.addBlackFilm(false));

        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen" +
                ShortKeyFactory.SHORT_CUT_LEER + ProgShortcut.SHORTCUT_ADD_BLACKLIST_THEME.getActShortcut());
        miBlackTheme.setOnAction(event -> {
            BlacklistFactory.addBlackThemeDownload();
        });
        submenuBlacklist.getItems().addAll(miBlack, miBlackTheme);


        MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> progData.downloadGuiController.playUrl());
        MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> progData.downloadGuiController.copyUrl());

        MenuItem miMediaDb = new MenuItem("Download in der Mediensammlung suchen" + ShortKeyFactory.SHORT_CUT_LEER +
                ProgShortcut.SHORTCUT_SEARCH_FILM_IN_MEDIACOLLECTION.getActShortcut());
        miMediaDb.setOnAction(a -> {
            progData.downloadGuiController.searchFilmInMediaCollection();
        });

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miFilmInfo, miCopyTheme, miCopyTitle, submenuBlacklist, miPlayUrl, miCopyUrl, miMediaDb);

        final MenuItem miSelectAll = new MenuItem("Alles auswählen");
        miSelectAll.setOnAction(a -> progData.downloadGuiController.selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> progData.downloadGuiController.invertSelection());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miSelectAll, miSelection);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden" + ShortKeyFactory.SHORT_CUT_LEER +
                ProgShortcut.SHORTCUT_SHOW_FILTER.getActShortcut());
        //ausgeführt wird aber der Button im Tab Filme!!
        miShowFilter.setOnAction(a -> MTPlayerFactory.setFilter());

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden" + ShortKeyFactory.SHORT_CUT_LEER +
                ProgShortcut.SHORTCUT_SHOW_INFOS.getActShortcut());
        miShowInfo.setOnAction(a -> MTPlayerFactory.setInfos());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }
}
