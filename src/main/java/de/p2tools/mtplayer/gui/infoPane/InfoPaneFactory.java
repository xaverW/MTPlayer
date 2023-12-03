/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class InfoPaneFactory {
    private InfoPaneFactory() {
    }

    public static boolean paneIsVisible(MTPlayerController.PANE_SHOWN paneShown, VBox vBoxAll, TabPane tabPane, Pane pane,
                                        BooleanProperty dividerOn, BooleanProperty booleanProperty) {
        if (MTPlayerController.paneShown != paneShown) {
            return false;

        } else if (booleanProperty.getValue()) {
            // dann im Extrafenster
            return true;
        } else if (!dividerOn.getValue()) {
            // dann wird gar nix angezeigt
            return false;
        } else if (!vBoxAll.getChildren().isEmpty() &&
                vBoxAll.getChildren().get(0).equals(pane)) {
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

    public static boolean isSelPane(VBox vBoxAll, TabPane tabPane, Pane pane) {
        if (!vBoxAll.getChildren().isEmpty() &&
                vBoxAll.getChildren().get(0).equals(pane)) {
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

    public static void setDialogInfo(Tab tab, Pane pane, String title,
                                     StringProperty size, BooleanProperty paneOn,
                                     BooleanProperty dividerOn, BooleanProperty tabOn) {
        tab.setContent(null);
        new InfoPaneDialog(pane, title, size, paneOn, dividerOn, tabOn);
    }
}
