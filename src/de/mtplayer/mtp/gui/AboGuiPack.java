/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class AboGuiPack {

    Daten daten;
    private final SplitPane splitPane = new SplitPane();
    private final HBox hBox = new HBox();
    DoubleProperty doubleProperty;//sonst geht die Ref verloren
    BooleanProperty boolDivOn;
    private final AboFilterController aboFilterController;
    private AboGuiController aboGuiController;
    private boolean bound = false;


    public AboGuiPack() {
        daten = Daten.getInstance();
        this.doubleProperty = Config.ABO_GUI_FILTER_DIVIDER.getDoubleProperty();
        this.boolDivOn = Config.ABO_GUI_FILTER_DIVIDER_ON.getBooleanProperty();
        aboFilterController = new AboFilterController();
        aboGuiController = new AboGuiController();
    }

    public void closeSplit() {
        boolDivOn.setValue(!boolDivOn.get());
    }

    private void setSplit() {
        if (boolDivOn.getValue()) {
            splitPane.getItems().clear();
            splitPane.getItems().addAll(aboFilterController, aboGuiController);
            bound = true;
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(doubleProperty);
        } else {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(doubleProperty);
            }
            splitPane.getItems().clear();
            splitPane.getItems().addAll(aboGuiController);
        }
    }

    public SplitPane pack() {

        // Menü
        final MenuController menuController = new MenuController(MenuController.StartupMode.ABO);
        menuController.setId("abo-menu-pane");

        // Abo Gui
        daten.aboGuiController = aboGuiController;

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        SplitPane.setResizableWithParent(aboFilterController, Boolean.FALSE);

        hBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        hBox.getChildren().addAll(splitPane, menuController);

        boolDivOn.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        return new SplitPane(hBox);
    }

}
