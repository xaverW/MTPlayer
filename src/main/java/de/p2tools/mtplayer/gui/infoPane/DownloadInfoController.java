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
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.tools.PListener;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneH;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DownloadInfoController extends P2ClosePaneH {

    private PaneFilmInfo paneFilmInfo;
    private PaneMedia paneMedia;
    private PaneBandwidthChart paneBandwidthChart;
    private PaneDownloadInfo paneDownloadInfo;
    private Tab tabFilmInfo;
    private Tab tabMedia;
    private Tab tabDownloadChart;
    private Tab tabDownloadInfo;

    private final ProgData progData;
    private final TabPane tabPane = new TabPane();

    public DownloadInfoController() {
        super(ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, false, true);
        progData = ProgData.getInstance();
        initInfoPane();
        PListener.addListener(new PListener(PListener.EVENT_TIMER_SECOND, DownloadInfoController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                paneBandwidthChart.searchInfos(MTPlayerController.paneShown == MTPlayerController.PANE_SHOWN.DOWNLOAD &&
                        paneIsVisible(paneBandwidthChart, ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON));
            }
        });
    }

    private void initInfoPane() {
        paneFilmInfo = new PaneFilmInfo(ProgConfig.DOWNLOAD_GUI_INFO_DIVIDER);
        MediaDataDto mDtoMedia = new MediaDataDto();
        MediaDataDto mDtoAbo = new MediaDataDto();
        initDto(mDtoMedia, mDtoAbo);
        paneMedia = new PaneMedia(mDtoMedia, mDtoAbo);
        paneBandwidthChart = new PaneBandwidthChart(progData);
        paneDownloadInfo = new PaneDownloadInfo();

        tabFilmInfo = new Tab("Beschreibung");
        tabFilmInfo.setClosable(false);
        tabMedia = new Tab("Mediensammlung");
        tabMedia.setClosable(false);
        tabDownloadChart = new Tab("Downloadchart");
        tabDownloadChart.setClosable(false);
        tabDownloadInfo = new Tab("Downloadinfos");
        tabDownloadInfo.setClosable(false);

        super.getRipProperty().addListener((u, o, n) -> {
            if (tabFilmInfo.isSelected()) {
                dialogInfo();
            } else if (tabMedia.isSelected()) {
                dialogMedia();
            } else if (tabDownloadChart.isSelected()) {
                dialogChart();
            } else {
                dialogDownloadInfo();
            }
        });

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.getValue()) {
            dialogInfo();
        }
        if (ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON.getValue()) {
            dialogMedia();
        }
        if (ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.getValue()) {
            dialogChart();
        }
        if (ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.getValue()) {
            dialogDownloadInfo();
        }

        ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.addListener((u, o, n) -> setTabs());
        ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON.addListener((u, o, n) -> setTabs());
        ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.addListener((u, o, n) -> setTabs());
        ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.addListener((u, o, n) -> setTabs());
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

    public void setDownloadInfos(DownloadData download) {
        if (paneIsVisible(paneFilmInfo, ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON)) {
            paneFilmInfo.setFilm(download);
        }
        if (paneIsVisible(paneMedia, ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON)) {
            paneMedia.setSearchPredicate(download);
        }
    }

    private boolean paneIsVisible(Pane pane, BooleanProperty booleanProperty) {
        if (booleanProperty.getValue()) {
            // dann im Extrafenster
            return true;
        } else if (!ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue()) {
            // dann wird gar nix angezeigt
            return false;
        } else if (!getVBoxAll().getChildren().isEmpty() &&
                getVBoxAll().getChildren().get(0).equals(pane)) {
            // dann wird nur das angezeigt
            return true;
        } else if (tabPane.getSelectionModel().getSelectedItem() != null &&
                tabPane.getSelectionModel().getSelectedItem().getContent().equals(pane)) {
            // dann ist der Tab ausgewählt
            return true;
        } else {
            return false;
        }
    }

    private void dialogInfo() {
        tabFilmInfo.setContent(null);
        new InfoPaneDialog(paneFilmInfo, "Filminfos",
                ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON,
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, ProgData.DOWNLOAD_TAB_ON);
    }


    private void dialogMedia() {
        tabMedia.setContent(null);
        new InfoPaneDialog(paneMedia, "Mediensammlung",
                ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON,
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogChart() {
        tabDownloadChart.setContent(null);
        new InfoPaneDialog(paneBandwidthChart, "Downloadchart",
                ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON,
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogDownloadInfo() {
        tabDownloadInfo.setContent(null);
        new InfoPaneDialog(paneDownloadInfo, "Downloadinfos",
                ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON,
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void setTabs() {
        int i = 0;

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.getValue()) {
            tabPane.getTabs().remove(tabFilmInfo);
        } else {
            tabFilmInfo.setContent(paneFilmInfo);
            if (!tabPane.getTabs().contains(tabFilmInfo)) {
                tabPane.getTabs().add(i, tabFilmInfo);
            }
            ++i;
        }

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON.getValue()) {
            tabPane.getTabs().remove(tabMedia);
        } else {
            tabMedia.setContent(paneMedia);
            if (!tabPane.getTabs().contains(tabMedia)) {
                tabPane.getTabs().add(i, tabMedia);
            }
            ++i;
        }

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.getValue()) {
            tabPane.getTabs().remove(tabDownloadChart);
        } else {
            tabDownloadChart.setContent(paneBandwidthChart);
            if (!tabPane.getTabs().contains(tabDownloadChart)) {
                tabPane.getTabs().add(i, tabDownloadChart);
            }
            ++i;
        }

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.getValue()) {
            tabPane.getTabs().remove(tabDownloadInfo);
        } else {
            tabDownloadInfo.setContent(paneDownloadInfo);
            if (!tabPane.getTabs().contains(tabDownloadInfo)) {
                tabPane.getTabs().add(i, tabDownloadInfo);
            }
            ++i;
        }

        if (i == 0) {
            getVBoxAll().getChildren().clear();
            ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.set(false);
        } else if (i == 1) {
            // dann gibts einen Tab
            final Node node = tabPane.getTabs().get(0).getContent();
            tabPane.getTabs().remove(0);
            getVBoxAll().getChildren().setAll(node);
            VBox.setVgrow(node, Priority.ALWAYS);
        } else {
            // dann gibts mehre Tabs
            getVBoxAll().getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
        }
    }
}
