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
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AboInfoController extends VBox {

    private final TabPane tabPane = new TabPane();
    private PaneAboInfo paneAboInfo;
    private PaneAboInfoList paneAboInfoList;

    public AboInfoController() {
        initInfoPane();
        PListener.addListener(new PListener(PListener.EVENT_TIMER_SECOND, DownloadInfoController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.ABO, tabPane, paneAboInfoList)) {
                    paneAboInfoList.setInfoText();
                }
            }
        });
    }

    public void setAboInfos(AboData abo) {
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.PANE_SHOWN.ABO, tabPane, paneAboInfo)) {
            paneAboInfo.setAbo(abo);
        }
    }

    public boolean arePanesShowing() {
        // dann wird wenigsten eins angezeigt
        return !ProgConfig.ABO_PANE_INFO_IS_RIP.get() ||
                !ProgConfig.ABO_PANE_INFO_LIST_IS_RIP.getValue();
    }

    private void initInfoPane() {
        paneAboInfo = new PaneAboInfo();
        paneAboInfoList = new PaneAboInfoList();

        if (ProgConfig.ABO_PANE_INFO_IS_RIP.get()) {
            dialogInfo();
        }
        ProgConfig.ABO_PANE_INFO_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogInfo();
            } else {
                ProgConfig.ABO_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.ABO_PANE_INFO_LIST_IS_RIP.get()) {
            dialogInfoList();
        }
        ProgConfig.ABO_PANE_INFO_LIST_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogInfoList();
            } else {
                ProgConfig.ABO_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        setTabs();
    }

    private void dialogInfo() {
        new InfoPaneDialog(paneAboInfo, "Abo Infos",
                ProgConfig.ABO_PANE_DIALOG_INFO_SIZE,
                ProgConfig.ABO_PANE_INFO_IS_RIP,
                ProgData.ABO_TAB_ON);
    }

    private void dialogInfoList() {
        new InfoPaneDialog(paneAboInfoList, "AboList Infos",
                ProgConfig.ABO_PANE_DIALOG_INFO_LIST_SIZE,
                ProgConfig.ABO_PANE_INFO_LIST_IS_RIP,
                ProgData.ABO_TAB_ON);
    }

    private void setTabs() {
        tabPane.getTabs().clear();

        if (!ProgConfig.ABO_PANE_INFO_IS_RIP.get()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneAboInfo, "Beschreibung", ProgConfig.ABO_INFO_TAB_IS_SHOWING, ProgConfig.ABO_PANE_INFO_IS_RIP));

        }
        if (!ProgConfig.ABO_PANE_INFO_LIST_IS_RIP.get()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneAboInfoList, "Infos", ProgConfig.ABO_INFO_TAB_IS_SHOWING, ProgConfig.ABO_PANE_INFO_LIST_IS_RIP));
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
