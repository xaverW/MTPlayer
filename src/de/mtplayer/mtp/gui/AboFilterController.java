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

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AboFilterController extends FilterController {

    ComboBox<String> cbSender = new ComboBox<>();
    Button btnClear = new Button("Filter löschen");

    private final Daten daten;

    public AboFilterController() {
        daten = Daten.getInstance();

        addCont("Abos für Sender", cbSender, vbFilter);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnClear);
        vbFilter.getChildren().add(hBox);

        cbSender.valueProperty().bindBidirectional(Config.FILTER_ABO_SENDER.getStringProperty());
        cbSender.setItems(daten.nameLists.getObsSenderForAbos());
        cbSender.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                Config.FILTER_ABO_SENDER.setValue(newValue);
            }
        });

        btnClear.setOnAction(a -> clearFilter());
    }

    private void clearFilter() {
        if (cbSender.getSelectionModel() != null) {
            cbSender.getSelectionModel().selectFirst();
        }
    }

    private void addCont(String txt, Control control, VBox vBox) {
        control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox v = new VBox();
        Label label = new Label(txt);
        v.getChildren().addAll(label, control);
        vBox.getChildren().add(v);
    }

}
