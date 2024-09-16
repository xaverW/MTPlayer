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
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LiveFilmInfoController extends VBox {

    private PaneFilmInfo paneFilmInfo;
    private PaneFilmButton paneButton;
    private PaneMedia paneMedia;
    private final TabPane tabPane = new TabPane();

    private final ProgData progData;

    public LiveFilmInfoController() {
        progData = ProgData.getInstance();
        initInfoPane();
    }

    public void setFilmInfos(FilmDataMTP film) {
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.LIVE_FILM, paneFilmInfo)) {
            paneFilmInfo.setFilm(film);
        }
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.LIVE_FILM, paneMedia)) {
            paneMedia.setSearchPredicate(film);
        }
    }

    public boolean arePanesShowing() {
        return !ProgConfig.LIVE_FILM_PANE_INFO_IS_RIP.getValue() ||
                !ProgConfig.LIVE_FILM_PANE_BUTTON_IS_RIP.getValue() ||
                !ProgConfig.LIVE_FILM_PANE_MEDIA_IS_RIP.getValue();
    }

    private void initInfoPane() {
        paneFilmInfo = new PaneFilmInfo(ProgConfig.LIVE_FILM_PANE_INFO_DIVIDER);
        paneButton = new PaneFilmButton(true);

        MediaDataDto mDtoMedia = new MediaDataDto();
        MediaDataDto mDtoAbo = new MediaDataDto();
        initDto(mDtoMedia, mDtoAbo);
        paneMedia = new PaneMedia(mDtoMedia, mDtoAbo);

        if (ProgConfig.LIVE_FILM_PANE_INFO_IS_RIP.get()) {
            dialogInfo();
        }
        ProgConfig.LIVE_FILM_PANE_INFO_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogInfo();
            } else {
                ProgConfig.LIVE_FILM_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.LIVE_FILM_PANE_BUTTON_IS_RIP.getValue()) {
            dialogButton();
        }
        ProgConfig.LIVE_FILM_PANE_BUTTON_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogButton();
            } else {
                ProgConfig.LIVE_FILM_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.LIVE_FILM_PANE_MEDIA_IS_RIP.getValue()) {
            dialogMedia();
        }
        ProgConfig.LIVE_FILM_PANE_MEDIA_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogMedia();
            } else {
                ProgConfig.LIVE_FILM_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

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

    private void dialogInfo() {
        new InfoPaneDialog(paneFilmInfo, "Filminfos",
                ProgConfig.LIVE_FILM_PANE_DIALOG_INFO_SIZE,
                ProgConfig.LIVE_FILM_PANE_INFO_IS_RIP,
                ProgData.LIVE_FILM_TAB_ON);
    }

    private void dialogButton() {
        new InfoPaneDialog(paneButton, "Startbutton",
                ProgConfig.LIVE_FILM_PANE_DIALOG_BUTTON_SIZE,
                ProgConfig.LIVE_FILM_PANE_BUTTON_IS_RIP,
                ProgData.LIVE_FILM_TAB_ON);
    }

    private void dialogMedia() {
        new InfoPaneDialog(paneMedia, "Mediensammlung",
                ProgConfig.LIVE_FILM_PANE_DIALOG_MEDIA_SIZE,
                ProgConfig.LIVE_FILM_PANE_MEDIA_IS_RIP,
                ProgData.LIVE_FILM_TAB_ON);
    }

    private void setTabs() {
        tabPane.getTabs().clear();

        if (!ProgConfig.LIVE_FILM_PANE_INFO_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneFilmInfo, "Beschreibung", ProgConfig.LIVE_FILM_INFO_TAB_IS_SHOWING, ProgConfig.LIVE_FILM_PANE_INFO_IS_RIP));
        }

        if (!ProgConfig.LIVE_FILM_PANE_BUTTON_IS_RIP.getValue()) {
            if (!progData.setDataList.getSetDataListButton().isEmpty()) {
                // dann gibts Button
                tabPane.getTabs().add(
                        InfoPaneFactory.makeTab(paneButton, "Startbutton", ProgConfig.LIVE_FILM_INFO_TAB_IS_SHOWING, ProgConfig.LIVE_FILM_PANE_BUTTON_IS_RIP));
            }
        }

        if (!ProgConfig.LIVE_FILM_PANE_MEDIA_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneMedia, "Mediensammlung", ProgConfig.LIVE_FILM_INFO_TAB_IS_SHOWING, ProgConfig.LIVE_FILM_PANE_MEDIA_IS_RIP));
        }

        if (tabPane.getTabs().isEmpty()) {
            // keine Tabs
        } else if (tabPane.getTabs().size() == 1) {
            // dann gibts einen Tab
            final Node node = tabPane.getTabs().get(0).getContent();
            tabPane.getTabs().remove(0);
            getChildren().setAll(node);
            VBox.setVgrow(node, Priority.ALWAYS);

        } else {
            // dann gibts mehre Tabs
            getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
        }
    }
}
