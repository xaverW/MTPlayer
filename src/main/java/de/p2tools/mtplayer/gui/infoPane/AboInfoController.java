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
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneH;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AboInfoController extends P2ClosePaneH {

    private Tab tabAboInfo;
    private Tab tabAboListInfo;

    private PaneAboInfo paneAboInfo;
    private PaneAboListInfo paneAboListInfo;
    private final TabPane tabPane = new TabPane();

    public AboInfoController() {
        super(ProgConfig.ABO_GUI_DIVIDER_ON, false, true);
        initInfoPane();
        PListener.addListener(new PListener(PListener.EVENT_TIMER_SECOND, DownloadInfoController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.ABO,
                        getVBoxAll(), tabPane, paneAboListInfo,
                        ProgConfig.ABO_GUI_DIVIDER_ON, ProgConfig.ABO_PANE_DIALOG_LIST_INFO_ON)) {
                    paneAboListInfo.setInfoText();
                }
            }
        });
    }

    public void setAboInfos(AboData abo) {
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.ABO,
                getVBoxAll(), tabPane, paneAboInfo,
                ProgConfig.ABO_GUI_DIVIDER_ON, ProgConfig.ABO_PANE_DIALOG_INFO_ON)) {
            paneAboInfo.setAbo(abo);
        }
    }

    private void initInfoPane() {
        paneAboInfo = new PaneAboInfo();
        paneAboListInfo = new PaneAboListInfo();

        tabAboInfo = new Tab("Beschreibung");
        tabAboInfo.setClosable(false);
        tabAboListInfo = new Tab("Infos");
        tabAboListInfo.setClosable(false);

        super.getRipProperty().addListener((u, o, n) -> {
            if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneAboInfo)) {
                dialogInfo();
            } else if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneAboListInfo)) {
                dialogListInfo();
            }
        });

        if (ProgConfig.ABO_PANE_DIALOG_INFO_ON.getValue()) {
            dialogInfo();
        }
        if (ProgConfig.ABO_PANE_DIALOG_LIST_INFO_ON.getValue()) {
            dialogListInfo();
        }

        ProgConfig.ABO_PANE_DIALOG_INFO_ON.addListener((u, o, n) -> setTabs());
        ProgConfig.ABO_PANE_DIALOG_LIST_INFO_ON.addListener((u, o, n) -> setTabs());
        setTabs();
    }

    private void dialogInfo() {
        InfoPaneFactory.setDialogInfo(tabAboInfo, paneAboInfo, "Abo Infos",
                ProgConfig.ABO_PANE_DIALOG_INFO_SIZE, ProgConfig.ABO_PANE_DIALOG_INFO_ON,
                ProgConfig.ABO_GUI_DIVIDER_ON, ProgData.ABO_TAB_ON);
    }

    private void dialogListInfo() {
        InfoPaneFactory.setDialogInfo(tabAboListInfo, paneAboListInfo, "AboList Infos",
                ProgConfig.ABO_PANE_DIALOG_List_INFO_SIZE, ProgConfig.ABO_PANE_DIALOG_LIST_INFO_ON,
                ProgConfig.ABO_GUI_DIVIDER_ON, ProgData.ABO_TAB_ON);
    }

    private void setTabs() {
        int i = 0;

        if (ProgConfig.ABO_PANE_DIALOG_INFO_ON.getValue()) {
            tabPane.getTabs().remove(tabAboInfo);
        } else {
            tabAboInfo.setContent(paneAboInfo);
            if (!tabPane.getTabs().contains(tabAboInfo)) {
                tabPane.getTabs().add(i, tabAboInfo);
            }
            ++i;
        }

        if (ProgConfig.ABO_PANE_DIALOG_LIST_INFO_ON.getValue()) {
            tabPane.getTabs().remove(tabAboListInfo);
        } else {
            tabAboListInfo.setContent(paneAboListInfo);
            if (!tabPane.getTabs().contains(tabAboListInfo)) {
                tabPane.getTabs().add(i, tabAboListInfo);
            }
            ++i;
        }

        if (i == 0) {
            getVBoxAll().getChildren().clear();
            ProgConfig.ABO_GUI_DIVIDER_ON.set(false);

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
