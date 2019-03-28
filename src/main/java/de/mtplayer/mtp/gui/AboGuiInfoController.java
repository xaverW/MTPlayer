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
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class AboGuiInfoController {
    private final TextArea txtInfo = new TextArea();
    private final Text txtName = new Text("");

    private Abo abo = null;

    public AboGuiInfoController(AnchorPane anchorPane) {

        txtName.setFont(Font.font(null, FontWeight.BOLD, -1));
        txtInfo.setWrapText(true);
        txtInfo.setPrefRowCount(4);

//        final GridPane gridPane = new GridPane();
//        gridPane.setHgap(15);
//        gridPane.setVgap(15);
//        gridPane.setPadding(new Insets(10));
//        GridPane.setVgrow(txtInfo, Priority.ALWAYS);
//
//        int row = 0;
//        gridPane.add(new Label(Abo.COLUMN_NAMES[Abo.ABO_NAME] + ":"), 0, row);
//        gridPane.add(txtName, 1, row);
//        gridPane.add(new Label(Abo.COLUMN_NAMES[Abo.ABO_INFOS] + ":"), 0, ++row);
//        gridPane.add(txtInfo, 1, row);
//        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
//                PColumnConstraints.getCcComputedSizeAndHgrow());
//
//        AnchorPane.setLeftAnchor(gridPane, 10.0);
//        AnchorPane.setBottomAnchor(gridPane, 10.0);
//        AnchorPane.setRightAnchor(gridPane, 10.0);
//        AnchorPane.setTopAnchor(gridPane, 10.0);
//        anchorPane.getChildren().add(gridPane);


        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(txtName, txtInfo);
        VBox.setVgrow(txtInfo, Priority.ALWAYS);

        AnchorPane.setLeftAnchor(vBox, 10.0);
        AnchorPane.setBottomAnchor(vBox, 10.0);
        AnchorPane.setRightAnchor(vBox, 10.0);
        AnchorPane.setTopAnchor(vBox, 10.0);
        anchorPane.getChildren().add(vBox);

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

