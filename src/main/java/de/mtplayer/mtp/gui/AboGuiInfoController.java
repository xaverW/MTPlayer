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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.data.abo.Abo;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AboGuiInfoController {
    private final TextArea txtInfo = new TextArea();
    private final TextField txtName = new TextField("");

    private Abo abo = null;

    public AboGuiInfoController(AnchorPane anchorPane) {

        txtName.setFont(Font.font(null, FontWeight.BOLD, -1));
        txtName.setTooltip(new Tooltip("Name des Abos"));
        txtInfo.setWrapText(true);
        txtInfo.setPrefRowCount(4);
        txtInfo.setTooltip(new Tooltip("Beschreibung des Abos"));

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(10));
        GridPane.setVgrow(txtInfo, Priority.ALWAYS);

        VBox vb = new VBox(1);
        vb.getChildren().addAll(new Label(Abo.COLUMN_NAMES[Abo.ABO_NAME]), txtName);
        gridPane.add(vb, 0, 0);

        vb = new VBox(1);
        VBox.setVgrow(txtInfo, Priority.ALWAYS);
        vb.getChildren().addAll(new Label(Abo.COLUMN_NAMES[Abo.ABO_DESCRIPTION]), txtInfo);
        gridPane.add(vb, 0, 1);

        RowConstraints rc = new RowConstraints();
        rc.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().addAll(new RowConstraints(), rc);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);
        AnchorPane.setTopAnchor(gridPane, 0.0);
        anchorPane.getChildren().add(gridPane);
        anchorPane.setMinHeight(0);
    }

    public void setAbo(Abo newAbo) {
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

