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
import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DownloadInfoController extends VBox {

    private PaneFilmInfo paneFilmInfo;
    private PaneMedia paneMedia;
    private PaneBandwidthChart paneBandwidthChart;
    private PaneDownloadError paneDownloadError;
    private PaneDownloadInfoList paneDownloadInfoList;

    private final ProgData progData;
    private final TabPane tabPane = new TabPane();

    public DownloadInfoController() {
        progData = ProgData.getInstance();
        initInfoPane();
        PListener.addListener(new PListener(PListener.EVENT_TIMER_SECOND, DownloadInfoController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                paneBandwidthChart.searchInfos(InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.DOWNLOAD,
                        tabPane, paneBandwidthChart)
                );

                if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.DOWNLOAD,
                        tabPane, paneDownloadInfoList)) {
                    paneDownloadInfoList.setInfoText();
                }

            }
        });
    }

    public void setDownloadInfos(DownloadData download) {
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.DOWNLOAD,
                tabPane, paneFilmInfo)) {
            paneFilmInfo.setFilm(download);
        }
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.DOWNLOAD,
                tabPane, paneMedia)) {
            paneMedia.setSearchPredicate(download);
        }
    }

    public boolean arePanesShowing() {
        return !ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP.get() ||
                !ProgConfig.DOWNLOAD_PANE_MEDIA_IS_RIP.get() ||
                !ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP.get() ||
                !ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP.get() ||
                !ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP.get();
    }

    private void initInfoPane() {
        paneFilmInfo = new PaneFilmInfo(ProgConfig.DOWNLOAD_PANE_INFO_DIVIDER);

        MediaDataDto mDtoMedia = new MediaDataDto();
        MediaDataDto mDtoAbo = new MediaDataDto();
        initDto(mDtoMedia, mDtoAbo);
        paneMedia = new PaneMedia(mDtoMedia, mDtoAbo);

        paneBandwidthChart = new PaneBandwidthChart(progData);
        paneDownloadError = new PaneDownloadError();
        paneDownloadInfoList = new PaneDownloadInfoList();

        if (ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP.get()) {
            dialogInfo();
        }
        ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogInfo();
            } else {
                ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.DOWNLOAD_PANE_MEDIA_IS_RIP.get()) {
            dialogMedia();
        }
        ProgConfig.DOWNLOAD_PANE_MEDIA_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogMedia();
            } else {
                ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP.get()) {
            dialogChart();
        }
        ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogChart();
            } else {
                ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP.get()) {
            dialogDownloadError();
        }
        ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogDownloadError();
            } else {
                ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP.get()) {
            dialogInfoList();
        }
        ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogInfoList();
            } else {
                ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> setTabs());
        setTabs();
    }

    private void initDto(MediaDataDto mediaDataDtoMedia, MediaDataDto mediaDataDtoAbo) {
        mediaDataDtoMedia.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_MEDIA;
        mediaDataDtoMedia.buildSearchFrom = ProgConfig.INFO_DOWNLOAD_BUILD_SEARCH_FROM_FOR_MEDIA;
        mediaDataDtoMedia.searchInWhat = ProgConfig.INFO_DOWNLOAD_SEARCH_IN_WHAT_FOR_MEDIA;
        mediaDataDtoMedia.cleaning = ProgConfig.INFO_DOWNLOAD_CLEAN_MEDIA;
        mediaDataDtoMedia.cleaningExact = ProgConfig.INFO_DOWNLOAD_CLEAN_EXACT_MEDIA;
        mediaDataDtoMedia.cleaningAndOr = ProgConfig.INFO_DOWNLOAD_CLEAN_AND_OR_MEDIA;
        mediaDataDtoMedia.cleaningList = ProgConfig.INFO_DOWNLOAD_CLEAN_LIST_MEDIA;

        mediaDataDtoAbo.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_ABO;
        mediaDataDtoAbo.buildSearchFrom = ProgConfig.INFO_DOWNLOAD_BUILD_SEARCH_FROM_FOR_ABO;
        mediaDataDtoAbo.searchInWhat = ProgConfig.INFO_DOWNLOAD_SEARCH_IN_WHAT_FOR_ABO;
        mediaDataDtoAbo.cleaning = ProgConfig.INFO_DOWNLOAD_CLEAN_ABO;
        mediaDataDtoAbo.cleaningExact = ProgConfig.INFO_DOWNLOAD_CLEAN_EXACT_ABO;
        mediaDataDtoAbo.cleaningAndOr = ProgConfig.INFO_DOWNLOAD_CLEAN_AND_OR_ABO;
        mediaDataDtoAbo.cleaningList = ProgConfig.INFO_DOWNLOAD_CLEAN_LIST_ABO;
    }

    private void dialogInfo() {
        new InfoPaneDialog(paneFilmInfo, "Filminfos",
                ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_SIZE,
                ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP,
                ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogMedia() {
        new InfoPaneDialog(paneMedia, "Mediensammlung",
                ProgConfig.DOWNLOAD_DIALOG_MEDIA_SIZE,
                ProgConfig.DOWNLOAD_PANE_MEDIA_IS_RIP,
                ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogChart() {
        new InfoPaneDialog(paneBandwidthChart, "Downloadchart",
                ProgConfig.DOWNLOAD_DIALOG_CHART_SIZE,
                ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP,
                ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogDownloadError() {
        new InfoPaneDialog(paneDownloadError, "Downloadfehler",
                ProgConfig.DOWNLOAD_DIALOG_ERROR_SIZE,
                ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP,
                ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogInfoList() {
        new InfoPaneDialog(paneDownloadInfoList, "Downloadinfos",
                ProgConfig.DOWNLOAD_DIALOG_INFO_LIST_SIZE,
                ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP,
                ProgData.DOWNLOAD_TAB_ON);
    }

    private void setTabs() {
        tabPane.getTabs().clear();

        if (!ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneFilmInfo, "Beschreibung", ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING, ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP));
        }

        if (!ProgConfig.DOWNLOAD_PANE_MEDIA_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneMedia, "Mediensammlung", ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING, ProgConfig.DOWNLOAD_PANE_MEDIA_IS_RIP));
        }

        if (!ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneBandwidthChart, "Downloadchart", ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING, ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP));
        }

        if (!ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneDownloadError, "Downloadfehler", ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING, ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP));
        }

        if (!ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneDownloadInfoList, "Infos", ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING, ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP));
        }

        if (tabPane.getTabs().isEmpty()) {

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
