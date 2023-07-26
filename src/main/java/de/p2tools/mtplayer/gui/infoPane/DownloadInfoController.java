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
import de.p2tools.mtplayer.gui.tools.MTListener;
import de.p2tools.p2lib.guitools.pclosepane.PClosePaneH;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DownloadInfoController extends PClosePaneH {

    private PaneFilmInfo paneFilmInfo;
    private PaneDownloadMedia paneDownloadMedia;
    private PaneDownloadChart paneDownloadChart;
    private PaneDownloadInfo paneDownloadInfo;
    private final ProgData progData;
    private final TabPane tabPane = new TabPane();

    public DownloadInfoController() {
        super(ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, false, true);
        progData = ProgData.getInstance();

        initInfoPane();
        MTListener.addListener(new MTListener(MTListener.EVENT_TIMER_SECOND, DownloadInfoController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                paneDownloadChart.searchInfos(MTPlayerController.paneShown == MTPlayerController.PANE_SHOWN.DOWNLOAD &&
                        paneIsVisible(paneDownloadChart, ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON));
            }
        });
    }

    private void initInfoPane() {
        paneFilmInfo = new PaneFilmInfo(ProgConfig.DOWNLOAD_GUI_INFO_DIVIDER);
        paneDownloadMedia = new PaneDownloadMedia();
        paneDownloadChart = new PaneDownloadChart(progData);
        paneDownloadInfo = new PaneDownloadInfo();

        super.getRipProperty().addListener((u, o, n) -> {
            if (tabPane.getTabs().isEmpty() && !getVBoxAll().getChildren().isEmpty()) {
                // dann gibts keine Tabs
                if (getVBoxAll().getChildren().get(0).equals(paneFilmInfo)) {
                    dialogInfo();
                } else if (getVBoxAll().getChildren().get(0).equals(paneDownloadMedia)) {
                    dialogMedia();
                } else if (getVBoxAll().getChildren().get(0).equals(paneDownloadChart)) {
                    dialogChart();
                } else if (getVBoxAll().getChildren().get(0).equals(paneDownloadInfo)) {
                    dialogDownloadInfo();
                }

            } else {
                Tab sel = tabPane.getSelectionModel().getSelectedItem();
                if (sel.getContent().equals(paneFilmInfo)) {
                    dialogInfo();
                } else if (sel.getContent().equals(paneDownloadMedia)) {
                    dialogMedia();
                } else if (sel.getContent().equals(paneDownloadChart)) {
                    dialogChart();
                } else if (sel.getContent().equals(paneDownloadInfo)) {
                    dialogDownloadInfo();
                }
            }
            setInfoTabPane();
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

        ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.addListener((u, o, n) -> setInfoTabPane());
        ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON.addListener((u, o, n) -> setInfoTabPane());
        ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.addListener((u, o, n) -> setInfoTabPane());
        ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.addListener((u, o, n) -> setInfoTabPane());
        progData.setDataList.listChangedProperty().addListener((observable, oldValue, newValue) -> setInfoTabPane());
        setInfoTabPane();
    }

    public void setDownloadInfos(DownloadData download) {
        paneFilmInfo.setFilm(download != null ? download.getFilm() : null);
        if (paneIsVisible(paneDownloadMedia, ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON)) {
            paneDownloadMedia.setSearchPredicate(download);
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
        new InfoPaneDialog(paneFilmInfo, "Filminfos",
                ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON,
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, ProgData.DOWNLOAD_TAB_ON);
    }


    private void dialogMedia() {
        new InfoPaneDialog(paneDownloadMedia, "Mediensammlung",
                ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON,
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogChart() {
        new InfoPaneDialog(paneDownloadChart, "Downloadchart",
                ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON,
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogDownloadInfo() {
        new InfoPaneDialog(paneDownloadInfo, "Downloadinfos",
                ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON,
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void setInfoTabPane() {
        tabPane.getTabs().clear();
        getVBoxAll().getChildren().clear();

        int count = 0;
        if (!ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.getValue()) {
            ++count;
        }
        if (!ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON.getValue()) {
            ++count;
        }
        if (!ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.getValue()) {
            ++count;
        }
        if (!ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.getValue()) {
            ++count;
        }

        if (count == 0) {
            // dann gibts nix zu sehen und dann das InfoPane ausblenden
            ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.set(false);

        } else if (count == 1) {
            // dann kein Tab
            if (!ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.getValue()) {
                getVBoxAll().getChildren().setAll(paneFilmInfo);
                VBox.setVgrow(paneFilmInfo, Priority.ALWAYS);
            } else if (!ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON.getValue()) {
                getVBoxAll().getChildren().setAll(paneDownloadMedia);
                VBox.setVgrow(paneDownloadMedia, Priority.ALWAYS);
            } else if (!ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.getValue()) {
                getVBoxAll().getChildren().setAll(paneDownloadChart);
                VBox.setVgrow(paneDownloadChart, Priority.ALWAYS);
            } else if (!ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.getValue()) {
                getVBoxAll().getChildren().setAll(paneDownloadInfo);
                VBox.setVgrow(paneDownloadInfo, Priority.ALWAYS);
            }

        } else {
            // dann werden mehrere angezeigt
            if (!ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.getValue()) {
                Tab tab = new Tab("Beschreibung");
                tab.setClosable(false);
                tab.setContent(paneFilmInfo);
                tabPane.getTabs().addAll(tab);
            }

            if (!ProgConfig.DOWNLOAD_PANE_DIALOG_MEDIA_ON.getValue()) {
                Tab tab = new Tab("Mediensammlung");
                tab.setClosable(false);
                tab.setContent(paneDownloadMedia);
                tabPane.getTabs().addAll(tab);
            }

            if (!ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.getValue()) {
                Tab tab = new Tab("Downloadchart");
                tab.setClosable(false);
                tab.setContent(paneDownloadChart);
                tabPane.getTabs().addAll(tab);
            }

            if (!ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.getValue()) {
                Tab tab = new Tab("Downloadinfos");
                tab.setClosable(false);
                tab.setContent(paneDownloadInfo);
                tabPane.getTabs().addAll(tab);
            }

            getVBoxAll().getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
        }
    }
}
