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

package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.offer.OfferData;
import de.p2tools.mtplayer.controller.data.offer.OfferFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class OfferFilterDialog extends P2DialogExtra {

    private final Button btnOk = new Button("_Ok");
    private final Button btnCancel = new Button("_Abbrechen");
    private final CheckBox chkShow = new CheckBox("Nicht mehr anzeigen");

    private final TableView<OfferData> tableView = new TableView<>();
    private final ProgData progData;
    private final ObjectProperty<OfferData> offer;

    public OfferFilterDialog(ProgData progData, ObjectProperty<OfferData> offer) {
        super(progData.primaryStage, null, "Filtervorschlag",
                true, true, true, DECO.NO_BORDER, true);
        this.progData = progData;
        this.offer = offer;
        init(true);
    }

    @Override
    public void close() {
        if (chkShow.isSelected()) {
            ProgConfig.SYSTEM_USE_OFFERTABLE.set(false);
        }
        super.close();
    }

    @Override
    public void make() {
        // Button
        getHboxLeft().getChildren().add(chkShow);
        getHboxLeft().setAlignment(Pos.CENTER_LEFT);

        final Button btnHelp = P2Button.helpButton(getStage(), "Filtervorschläge",
                HelpText.FILTER_OFFER_TABLE);
        addHlpButton(btnHelp);

        addCancelButton(btnCancel);
        btnOk.setOnAction(a -> close());
        btnCancel.setOnAction(a -> {
            offer.set(null);
            close();
        });

        // Tabelle
        getVBoxCont().getChildren().add(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setMaxHeight(Double.MAX_VALUE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            if (n != null) {
                offer.set(n);
            }
        });

        tableView.setRowFactory(param -> new TableRow<>() {
            @Override
            protected void updateItem(OfferData item, boolean empty) {
                super.updateItem(item, empty);

                setOnMouseClicked(event -> {
                    if (event.getButton().equals(MouseButton.PRIMARY) && !isEmpty()) {
                        offer.set(item);
                        close();
                    }
                });
            }
        });

        final TableColumn<OfferData, String> offerColumn = new TableColumn<>("Vorschlag");
        offerColumn.setCellValueFactory(new PropertyValueFactory<>("offer"));

        tableView.getColumns().add(offerColumn);
        tableView.setItems(OfferFactory.getActiveList());
    }
}