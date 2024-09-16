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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.filter.DownloadFilterController;
import de.p2tools.mtplayer.gui.filter.FilterPaneDialog;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneV;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class DownloadGui {

    ProgData progData;
    private final SplitPane splitPane = new SplitPane();
    private final HBox hBox = new HBox();
    private final DownloadFilterController downloadFilterController;
    private final DownloadGuiController downloadGuiController;
    private boolean bound = false;
    private FilterPaneDialog filterPaneDialog = null;

    public DownloadGui() {
        progData = ProgData.getInstance();
        downloadFilterController = new DownloadFilterController();
        downloadGuiController = new DownloadGuiController();
    }

    private void setSplit() {
        if (bound) {
            splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER);
            bound = false;
        }
        if (filterPaneDialog != null) {
            filterPaneDialog.closeSetNoRip();
            filterPaneDialog = null;
        }
        splitPane.getItems().clear();

        if (ProgConfig.DOWNLOAD_GUI_FILTER_IS_VISIBLE.get()) {

            if (ProgConfig.DOWNLOAD_GUI_FILTER_IS_RIP.get()) {

                filterPaneDialog = new FilterPaneDialog(downloadFilterController, "Downloadfilter",
                        ProgConfig.DOWNLOAD_GUI_FILTER_DIALOG_SIZE,
                        ProgConfig.DOWNLOAD_GUI_FILTER_IS_RIP, ProgData.DOWNLOAD_TAB_ON);
                splitPane.getItems().addAll(downloadGuiController);

            } else {
                P2ClosePaneV closePaneV = new P2ClosePaneV();
                closePaneV.addPane(downloadFilterController);
                closePaneV.getButtonClose().setOnAction(a -> ProgConfig.DOWNLOAD_GUI_FILTER_IS_VISIBLE.set(false));
                closePaneV.getButtonRip().setOnAction(a -> ProgConfig.DOWNLOAD_GUI_FILTER_IS_RIP.set(!ProgConfig.DOWNLOAD_GUI_FILTER_IS_RIP.get()));

                splitPane.getItems().addAll(closePaneV, downloadGuiController);
                splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER);
                bound = true;
            }

        } else {
            splitPane.getItems().addAll(downloadGuiController);
        }
    }

    public SplitPane pack() {
        // Menü
        final MenuController menuController = new MenuController(MenuController.StartupMode.DOWNLOAD);

        // Gui
        progData.downloadGuiController = downloadGuiController;

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        SplitPane.setResizableWithParent(downloadFilterController, Boolean.FALSE);

        hBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        hBox.getChildren().addAll(splitPane, menuController);

        ProgConfig.DOWNLOAD_GUI_FILTER_IS_VISIBLE.addListener((observable, oldValue, newValue) -> setSplit());
        ProgConfig.DOWNLOAD_GUI_FILTER_IS_RIP.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        return new SplitPane(hBox);
    }
}
