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

package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneH;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LiveFilmInfoController extends P2ClosePaneH {

    private PaneLiveFilmInfo paneLiveFilmInfo;
    private PaneFilmButton paneButton;
    private PaneMedia paneMedia;
    private Tab tabLiveFilmInfo;
    private Tab tabButton;
    private Tab tabMedia;
    private final TabPane tabPane = new TabPane();

    private final ProgData progData;

    public LiveFilmInfoController() {
        super(ProgConfig.LIVE_FILM_GUI_INFO_ON, true, true);
        progData = ProgData.getInstance();
        initInfoPane();
    }

    public void setFilmInfos(FilmDataMTP film) {
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.LIVE_FILM,
                getVBoxAll(), tabPane, paneLiveFilmInfo,
                ProgConfig.LIVE_FILM_GUI_INFO_ON, ProgConfig.LIVE_FILM_PANE_DIALOG_INFO_ON)) {
            paneLiveFilmInfo.setFilm(film);
        }
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.LIVE_FILM,
                getVBoxAll(), tabPane, paneMedia,
                ProgConfig.LIVE_FILM_GUI_INFO_ON, ProgConfig.LIVE_FILM_PANE_DIALOG_MEDIA_ON)) {
            paneMedia.setSearchPredicate(film);
        }
    }

    public boolean isPaneShowing() {
        return !ProgConfig.LIVE_FILM_PANE_DIALOG_INFO_ON.getValue() ||
                !ProgConfig.LIVE_FILM_PANE_DIALOG_BUTTON_ON.getValue() ||
                !ProgConfig.LIVE_FILM_PANE_DIALOG_MEDIA_ON.getValue();
    }


    private void initInfoPane() {
        paneLiveFilmInfo = new PaneLiveFilmInfo(ProgConfig.LIVE_FILM_PANE_INFO_DIVIDER);
        paneButton = new PaneFilmButton(true);
        MediaDataDto mDtoMedia = new MediaDataDto();
        MediaDataDto mDtoAbo = new MediaDataDto();
        initDto(mDtoMedia, mDtoAbo);
        paneMedia = new PaneMedia(mDtoMedia, mDtoAbo);
        tabLiveFilmInfo = new Tab("Beschreibung");
        tabLiveFilmInfo.setClosable(false);
        tabButton = new Tab("Startbutton");
        tabButton.setClosable(false);
        tabMedia = new Tab("Mediensammlung");
        tabMedia.setClosable(false);

        super.getRipProperty().addListener((u, o, n) -> {
            if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneLiveFilmInfo)) {
                setDialogInfo();
            } else if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneButton)) {
                setDialogButton();
            } else if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneMedia)) {
                setDialogMedia();
            }
        });

        if (ProgConfig.LIVE_FILM_PANE_DIALOG_INFO_ON.getValue()) {
            setDialogInfo();
        }
        if (ProgConfig.LIVE_FILM_PANE_DIALOG_BUTTON_ON.getValue()) {
            setDialogButton();
        }
        if (ProgConfig.LIVE_FILM_PANE_DIALOG_MEDIA_ON.getValue()) {
            setDialogMedia();
        }
        ProgConfig.LIVE_FILM_PANE_DIALOG_INFO_ON.addListener((u, o, n) -> setTabs()); // kommt beim Ein- und Ausschalten der Fenster
        ProgConfig.LIVE_FILM_PANE_DIALOG_BUTTON_ON.addListener((u, o, n) -> setTabs());
        ProgConfig.LIVE_FILM_PANE_DIALOG_MEDIA_ON.addListener((u, o, n) -> setTabs());
        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> setTabs());

        setTabs();
    }

    private void initDto(MediaDataDto mediaDataDtoMedia, MediaDataDto mediaDataDtoAbo) {
        mediaDataDtoMedia.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_MEDIA;
        mediaDataDtoMedia.buildSearchFrom = ProgConfig.INFO_LIVE_FILM_BUILD_SEARCH_FROM_FOR_MEDIA;
        mediaDataDtoMedia.searchInWhat = ProgConfig.INFO_LIVE_FILM_SEARCH_IN_WHAT_FOR_MEDIA;
        mediaDataDtoMedia.cleaning = ProgConfig.INFO_LIVE_FILM_CLEAN_MEDIA;
        mediaDataDtoMedia.cleaningExact = ProgConfig.INFO_LIVE_FILM_CLEAN_EXACT_MEDIA;
        mediaDataDtoMedia.cleaningAndOr = ProgConfig.INFO_LIVE_FILM_CLEAN_AND_OR_MEDIA;
        mediaDataDtoMedia.cleaningList = ProgConfig.INFO_LIVE_FILM_CLEAN_LIST_MEDIA;

        mediaDataDtoAbo.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_ABO;
        mediaDataDtoAbo.buildSearchFrom = ProgConfig.INFO_LIVE_FILM_BUILD_SEARCH_FROM_FOR_ABO;
        mediaDataDtoAbo.searchInWhat = ProgConfig.INFO_LIVE_FILM_SEARCH_IN_WHAT_FOR_ABO;
        mediaDataDtoAbo.cleaning = ProgConfig.INFO_LIVE_FILM_CLEAN_ABO;
        mediaDataDtoAbo.cleaningExact = ProgConfig.INFO_LIVE_FILM_CLEAN_EXACT_ABO;
        mediaDataDtoAbo.cleaningAndOr = ProgConfig.INFO_LIVE_FILM_CLEAN_AND_OR_ABO;
        mediaDataDtoAbo.cleaningList = ProgConfig.INFO_LIVE_FILM_CLEAN_LIST_ABO;
    }

    private void setDialogInfo() {
        InfoPaneFactory.setDialogInfo(tabLiveFilmInfo, paneLiveFilmInfo, "Filminfos",
                ProgConfig.LIVE_FILM_PANE_DIALOG_INFO_SIZE, ProgConfig.LIVE_FILM_PANE_DIALOG_INFO_ON,
                ProgConfig.LIVE_FILM_GUI_INFO_ON, ProgData.LIVE_FILM_TAB_ON);
    }

    private void setDialogButton() {
        InfoPaneFactory.setDialogInfo(tabButton, paneButton, "Startbutton",
                ProgConfig.LIVE_FILM_PANE_DIALOG_BUTTON_SIZE, ProgConfig.LIVE_FILM_PANE_DIALOG_BUTTON_ON,
                ProgConfig.LIVE_FILM_GUI_INFO_ON, ProgData.LIVE_FILM_TAB_ON);
    }

    private void setDialogMedia() {
        InfoPaneFactory.setDialogInfo(tabMedia, paneMedia, "Mediensammlung",
                ProgConfig.LIVE_FILM_PANE_DIALOG_MEDIA_SIZE, ProgConfig.LIVE_FILM_PANE_DIALOG_MEDIA_ON,
                ProgConfig.LIVE_FILM_GUI_INFO_ON, ProgData.LIVE_FILM_TAB_ON);
    }

    private void setTabs() {
        int i = 0;

        if (ProgConfig.LIVE_FILM_PANE_DIALOG_INFO_ON.getValue()) {
            tabPane.getTabs().remove(tabLiveFilmInfo);
        } else {
            tabLiveFilmInfo.setContent(paneLiveFilmInfo);
            if (!tabPane.getTabs().contains(tabLiveFilmInfo)) {
                tabPane.getTabs().add(i, tabLiveFilmInfo);
            }
            ++i;
        }

        if (ProgConfig.LIVE_FILM_PANE_DIALOG_BUTTON_ON.getValue()) {
            tabPane.getTabs().remove(tabButton);
        } else {
            if (progData.setDataList.getSetDataListButton().size() <= 0) {
                // dann gibts keine Button
                tabPane.getTabs().remove(tabButton);
            } else {
                tabButton.setContent(paneButton);
                if (!tabPane.getTabs().contains(tabButton)) {
                    tabPane.getTabs().add(i, tabButton);
                }
                ++i;
            }
        }

        if (ProgConfig.LIVE_FILM_PANE_DIALOG_MEDIA_ON.getValue()) {
            tabPane.getTabs().remove(tabMedia);
        } else {
            tabMedia.setContent(paneMedia);
            if (!tabPane.getTabs().contains(tabMedia)) {
                tabPane.getTabs().add(i, tabMedia);
            }
            ++i;
        }

        if (i == 0) {
            getVBoxAll().getChildren().clear();
            ProgConfig.LIVE_FILM_GUI_INFO_ON.set(false);

        } else if (i == 1) {
            // dann gibts einen Tab
            final Node node = tabPane.getTabs().get(0).getContent();
            tabPane.getTabs().remove(0);
            getVBoxAll().getChildren().setAll(node);
            VBox.setVgrow(node, Priority.ALWAYS);
            ProgConfig.LIVE_FILM_GUI_INFO_ON.set(true);

        } else {
            // dann gibts mehre Tabs
            getVBoxAll().getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
            ProgConfig.LIVE_FILM_GUI_INFO_ON.set(true);
        }
    }
}
