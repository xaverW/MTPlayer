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
import de.p2tools.mtplayer.gui.filter.AboFilterController;
import de.p2tools.mtplayer.gui.filter.FilterPaneDialog;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class AboGui {

    ProgData progData;
    private final SplitPane splitPane = new SplitPane();
    private final HBox hBox = new HBox();
    private final AboFilterController aboFilterController;
    private AboGuiController aboGuiController;
    private boolean bound = false;
    private FilterPaneDialog filterPaneDialog = null;

    public AboGui() {
        progData = ProgData.getInstance();
        aboFilterController = new AboFilterController();
        aboGuiController = new AboGuiController();
    }

    private void setSplit() {
        if (bound) {
            splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.ABO_GUI_FILTER_DIVIDER);
            bound = false;
        }
        if (filterPaneDialog != null) {
            filterPaneDialog.closeSetNoRip();
            filterPaneDialog = null;
        }
        splitPane.getItems().clear();

        if (ProgConfig.ABO_GUI_FILTER_IS_VISIBLE.get()) {
            if (ProgConfig.ABO_GUI_FILTER_IS_RIP.get()) {
                filterPaneDialog = new FilterPaneDialog(aboFilterController, "Abofilter",
                        ProgConfig.ABO_GUI_FILTER_DIALOG_SIZE,
                        ProgConfig.ABO_GUI_FILTER_IS_RIP, ProgData.ABO_TAB_ON);

                splitPane.getItems().addAll(aboGuiController);

            } else {
                splitPane.getItems().addAll(aboFilterController, aboGuiController);
                splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.ABO_GUI_FILTER_DIVIDER);
                bound = true;
            }

        } else {
            splitPane.getItems().addAll(aboGuiController);
        }
    }

    public SplitPane pack() {
        // Menü
        final MenuController menuController = new MenuController(MenuController.StartupMode.ABO);

        // Abo Gui
        progData.aboGuiController = aboGuiController;

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        SplitPane.setResizableWithParent(aboFilterController, Boolean.FALSE);

        hBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        hBox.getChildren().addAll(splitPane, menuController);

        ProgConfig.ABO_GUI_FILTER_IS_VISIBLE.addListener((observable, oldValue, newValue) -> setSplit());
        ProgConfig.ABO_GUI_FILTER_IS_RIP.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        return new SplitPane(hBox);
    }
}
