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

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.abo.AboConstants;
import de.p2tools.mtplayer.tools.storedFilter.FilterCheckRegEx;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AboFilterController extends FilterController {

    private ComboBox<String> cboChannel = new ComboBox<>();
    private ComboBox<String> cboArt = new ComboBox<>(); // Abo ein-/ausgeschaltet
    private TextField txtDescription = new TextField();
    private TextField txtName = new TextField();
    private Button btnClear = new Button("");

    private final VBox vBoxFilter;
    private final ProgData progData;

    public AboFilterController() {
        super(ProgConfig.ABO_GUI_FILTER_DIVIDER_ON);
        vBoxFilter = getVBoxFilter(true);
        progData = ProgData.getInstance();
        progData.aboFilterController = this;

        addCont("Abos für Sender", cboChannel, vBoxFilter);
        addCont("Status", cboArt, vBoxFilter);
        addCont("Name", txtName, vBoxFilter);
        addCont("Beschreibung", txtDescription, vBoxFilter);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.getChildren().add(btnClear);
        vBoxFilter.getChildren().add(hBox);

        initFilter();

        btnClear.setGraphic(new ProgIcons().ICON_BUTTON_CLEAR_FILTER);
        btnClear.getStyleClass().add("btnClearFilter");
        btnClear.setOnAction(a -> clearFilter());
        btnClear.setTooltip(new Tooltip("Filter löschen"));
    }

//    public void setClearText(String txt) {
////        btnClear.setText(txt);
//    }

    private void initFilter() {
        txtName.textProperty().bindBidirectional(ProgConfig.FILTER_ABO_NAME);
        FilterCheckRegEx fN = new FilterCheckRegEx(txtName);
        txtName.textProperty().addListener((observable, oldValue, newValue) -> fN.checkPattern());

        txtDescription.textProperty().bindBidirectional(ProgConfig.FILTER_ABO_DESCRIPTION);
        FilterCheckRegEx fD = new FilterCheckRegEx(txtDescription);
        txtDescription.textProperty().addListener((observable, oldValue, newValue) -> fD.checkPattern());

        cboChannel.setItems(progData.worker.getChannelsForAbosList());
        cboChannel.valueProperty().bindBidirectional(ProgConfig.FILTER_ABO_CHANNEL);

        cboArt.getItems().addAll(AboConstants.ALL,
                AboConstants.ABO_ON,
                AboConstants.ABO_OFF);
        cboArt.valueProperty().bindBidirectional(ProgConfig.FILTER_ABO_TYPE);
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
}
