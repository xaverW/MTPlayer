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
import de.mtplayer.mtp.controller.data.Icons;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;

public class DownloadMenu {
    final private VBox vbox;
    final private ProgData progData;
    private static final String UPDATE_DOWNLOADS_TEXT = "Liste der Downloads aktualisieren";
    private static final String START_ALL_DOWNLOADS_TEXT = "alle Downloads starten";
    private static final String REMOVE_DOWNLOADS_TEXT = "Downloads aus Liste entfernen";
    private static final String CLEANUP_DL_LIST_TEXT = "Liste der Downloads aufräumen";
    private static final String PUTBACK_DL_TEXT = "Downloads zurückstellen";
    BooleanProperty boolDivOn = ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.getBooleanProperty();
    BooleanProperty boolInfoOn = ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getBooleanProperty();

    public DownloadMenu(VBox vbox) {
        this.vbox = vbox;
        progData = ProgData.getInstance();
    }


    public void init() {
        vbox.getChildren().clear();

        initMenu();
        initButton();
    }

    private void initButton() {
        // Button
        final ToolBarButton btDownloadRefresh =
                new ToolBarButton(vbox, UPDATE_DOWNLOADS_TEXT, UPDATE_DOWNLOADS_TEXT, new Icons().FX_ICON_TOOLBAR_DOWNLOAD_REFRESH);

        final ToolBarButton btDownloadAll = new ToolBarButton(vbox,
                START_ALL_DOWNLOADS_TEXT,
                START_ALL_DOWNLOADS_TEXT,
                new Icons().FX_ICON_TOOLBAR_DOWNLOAD_START_ALL);

        final ToolBarButton btStartDownloads = new ToolBarButton(vbox,
                "Downloads Starten",
                "markierte Downloads starten",
                new Icons().FX_ICON_TOOLBAR_DOWNLOAD_START);

        final ToolBarButton btDownloadFilm = new ToolBarButton(vbox,
                "Film Starten",
                "gespeicherten Film abspielen",
                new Icons().FX_ICON_TOOLBAR_DOWNLOAD_FILM_START);

        final ToolBarButton btDownloadBack =
                new ToolBarButton(vbox, PUTBACK_DL_TEXT, PUTBACK_DL_TEXT, new Icons().FX_ICON_TOOLBAR_DOWNLOAD_UNDO);
        final ToolBarButton btDownloadDel =
                new ToolBarButton(vbox, REMOVE_DOWNLOADS_TEXT, REMOVE_DOWNLOADS_TEXT, new Icons().FX_ICON_TOOLBAR_DOWNLOAD_DEL);
        final ToolBarButton btDownloadClear =
                new ToolBarButton(vbox, CLEANUP_DL_LIST_TEXT, CLEANUP_DL_LIST_TEXT, new Icons().FX_ICON_TOOLBAR_DOWNLOAD_CLEAR);
        btDownloadRefresh.setOnAction(a -> progData.downloadGuiController.update());
        btDownloadAll.setOnAction(a -> progData.downloadGuiController.startDownload(true));
        btStartDownloads.setOnAction(a -> progData.downloadGuiController.startDownload(false));
        btDownloadFilm.setOnAction(a -> progData.downloadGuiController.playFilm());
        btDownloadBack.setOnAction(a -> progData.downloadGuiController.moveDownloadBack());
        btDownloadDel.setOnAction(a -> progData.downloadGuiController.deleteDownloads());
        btDownloadClear.setOnAction(a -> progData.downloadGuiController.cleanUp());

    }

    private void initMenu() {

        // MenuButton
        final MenuButton mb = new MenuButton("");
        mb.setGraphic(new Icons().FX_ICON_TOOLBAR_MENU);
        mb.getStyleClass().add("btnFunction");

        final MenuItem mbStartAll = new MenuItem("alle Downloads starten");
        mbStartAll.setOnAction(a -> progData.downloadGuiController.startDownload(true /* alle */));

        final MenuItem mbStopAll = new MenuItem("alle Downloads stoppen");
        mbStopAll.setOnAction(a -> progData.downloadGuiController.stopDownload(true /* alle */));

        final MenuItem mbStopWait = new MenuItem("wartende stoppen");
        mbStopWait.setOnAction(a -> progData.downloadGuiController.stopWaitingDownloads());

        final MenuItem mbAct = new MenuItem("Liste der Downloads aktualisieren");
        mbAct.setOnAction(e -> progData.downloadGuiController.update());

        final MenuItem mbClean = new MenuItem("Liste der Downloads aufräumen");
        mbClean.setOnAction(e -> progData.downloadGuiController.cleanUp());

        final MenuItem miDownloadStart = new MenuItem("Download starten");
        miDownloadStart.setOnAction(a -> progData.downloadGuiController.startDownload(false));

        final MenuItem miDownloadStop = new MenuItem("Download stoppen");
        miDownloadStop.setOnAction(a -> progData.downloadGuiController.stopDownload(false));

        final MenuItem miDownloadTop = new MenuItem("Download vorziehen");
        miDownloadTop.setOnAction(a -> progData.downloadGuiController.preferDownload());

        final MenuItem miDownloadEnd = new MenuItem("Download zurückstellen");
        miDownloadEnd.setOnAction(a -> progData.downloadGuiController.moveDownloadBack());

        final MenuItem miDownloadRemove = new MenuItem("Downloads aus Liste entfernen");
        miDownloadRemove.setOnAction(a -> progData.downloadGuiController.deleteDownloads());

        final MenuItem miDownloadChange = new MenuItem("Download ändern");
        miDownloadChange.setOnAction(a -> progData.downloadGuiController.changeDownload());

        final MenuItem miDownloadShown = new MenuItem("Filme als gesehen markieren");
        miDownloadShown.setOnAction(a -> progData.downloadGuiController.setFilmShown());

        final MenuItem miDownloadNotShown = new MenuItem("Filme als ungesehen markieren");
        miDownloadNotShown.setOnAction(a -> progData.downloadGuiController.setFilmNotShown());

        final MenuItem miDownloadStored = new MenuItem("gespeicherten Film abspielen");
        miDownloadStored.setOnAction(a -> progData.downloadGuiController.playFilm());

        final MenuItem miFilmMediaCollection = new MenuItem("Titel in der Mediensammlung suchen");
        miFilmMediaCollection.setOnAction(a -> progData.downloadGuiController.guiFilmMediaCollection());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> progData.downloadGuiController.invertSelection());

        final CheckMenuItem miShowFilter = new CheckMenuItem("Filter anzeigen");
        miShowFilter.selectedProperty().bindBidirectional(boolDivOn);

        final CheckMenuItem miShowInfo = new CheckMenuItem("Downloadinfos anzeigen");
        miShowInfo.selectedProperty().bindBidirectional(boolInfoOn);


        mb.getItems().addAll(mbStartAll, mbStopAll, mbStopWait, mbAct, mbClean);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miDownloadStart,
                miDownloadStop,
                miDownloadTop,
                miDownloadEnd,
                miDownloadRemove,
                miDownloadChange);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miDownloadShown, miDownloadNotShown, miDownloadStored, miFilmMediaCollection);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().add(miSelection);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);

        vbox.getChildren().add(mb);

    }
}
