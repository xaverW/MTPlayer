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

import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AboGuiInfoController extends VBox {
    private final TextArea txtInfo = new TextArea();
    private final TextField txtName = new TextField("");

    private AboData abo = null;

    public AboGuiInfoController() {
        txtName.setFont(Font.font(null, FontWeight.BOLD, -1));
        txtName.setTooltip(new Tooltip("Name des Abos"));
        txtInfo.setWrapText(true);
        txtInfo.setPrefRowCount(4);
        txtInfo.setTooltip(new Tooltip("Beschreibung des Abos"));

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10));
        GridPane.setVgrow(txtInfo, Priority.ALWAYS);

        gridPane.add(new Label(AboFieldNames.ABO_NAME), 0, 0);
        gridPane.add(txtName, 1, 0);
        gridPane.add(new Label(AboFieldNames.ABO_DESCRIPTION), 0, 1);
        gridPane.add(txtInfo, 1, 1);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), PColumnConstraints.getCcComputedSizeAndHgrow());
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        getChildren().add(gridPane);
    }

    public void setAbo(AboData newAbo) {
        if (this.abo != null) {
            txtName.textProperty().unbindBidirectional(this.abo.nameProperty());
            txtInfo.textProperty().unbindBidirectional(this.abo.descriptionProperty());
        }

        this.abo = newAbo;
        if (newAbo == null) {
            txtName.setText("");
            txtInfo.setText("");
            return;
        }

        txtName.textProperty().bindBidirectional(this.abo.nameProperty());
        txtInfo.textProperty().bindBidirectional(this.abo.descriptionProperty());
    }
}

