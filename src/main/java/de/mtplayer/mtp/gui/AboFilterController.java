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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.abo.AboConstants;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AboFilterController extends FilterController {

    ComboBox<String> cboChannel = new ComboBox<>();
    ComboBox<String> cboArt = new ComboBox<>(); // Abo ein-/ausgeschaltet
    TextField txtDescription = new TextField();
    TextField txtName = new TextField();
    Button btnClear = new Button("Filter löschen");

    private final VBox vBoxFilter;
    private final ProgData progData;

    public AboFilterController() {
        super();
        vBoxFilter = getVBoxFilter(true);
        progData = ProgData.getInstance();

        addCont("Abos für Sender", cboChannel, vBoxFilter);
        addCont("Status", cboArt, vBoxFilter);
        addCont("Name", txtName, vBoxFilter);
        addCont("Beschreibung", txtDescription, vBoxFilter);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnClear);
        vBoxFilter.getChildren().add(hBox);

        initFilter();
        btnClear.setOnAction(a -> clearFilter());
    }

    private void initFilter() {
        txtName.textProperty().bindBidirectional(ProgConfig.FILTER_ABO_NAME.getStringProperty());
        txtDescription.textProperty().bindBidirectional(ProgConfig.FILTER_ABO_DESCRIPTION.getStringProperty());

        cboChannel.setItems(progData.worker.getChannelsForAbosList());
        cboChannel.valueProperty().bindBidirectional(ProgConfig.FILTER_ABO_CHANNEL.getStringProperty());

        cboArt.getItems().addAll(AboConstants.ALL,
                AboConstants.ABO_ON,
                AboConstants.ABO_OFF);
        cboArt.valueProperty().bindBidirectional(ProgConfig.FILTER_ABO_KIND.getStringProperty());
    }

    private void clearFilter() {
        txtName.setText("");
        txtDescription.setText("");
        if (cboChannel.getSelectionModel() != null) {
            cboChannel.getSelectionModel().selectFirst();
        }
        if (cboArt.getSelectionModel() != null) {
            cboArt.getSelectionModel().selectFirst();
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
