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

package de.mtplayer.mtp.gui.configDialog;

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.ReplaceData;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.table.TableRowExpanderColumn;

import java.util.Collection;

public class ReplacePane {

    TableView<ReplaceData> tableView = new TableView<>();

    BooleanProperty propAscii = Config.SYSTEM_ONLY_ASCII.getBooleanProperty();
    BooleanProperty propReplace = Config.SYSTEM_USE_REPLACETABLE.getBooleanProperty();

    public void makeReplaceListTable(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(10);

        makeAscii(vBox);
        initTable(vBox);

        TitledPane tpReplace = new TitledPane("Ersetzungstabelle", vBox);
        result.add(tpReplace);
        tpReplace.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpReplace, Priority.ALWAYS);
    }


    private void makeAscii(VBox vBox) {

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        vBox.getChildren().add(gridPane);

        final ToggleSwitch tglAscii = new ToggleSwitch("nur ASCII-Zeichen erlauben");
        tglAscii.setMaxWidth(Double.MAX_VALUE);
        tglAscii.selectedProperty().bindBidirectional(propAscii);

        final Button btnHelpAscii = new Button("");
        btnHelpAscii.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpAscii.setOnAction(a -> new MTAlert().showHelpAlert("Nur ASCII-Zeichen",
                HelpText.DOWNLOAD_ONLY_ASCII));


        final ToggleSwitch tglReplace = new ToggleSwitch("Ersetzungstabelle");
        tglReplace.setMaxWidth(Double.MAX_VALUE);
        tglReplace.selectedProperty().bindBidirectional(propReplace);

        final Button btnHelpReplace = new Button("");
        btnHelpReplace.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpReplace.setOnAction(a -> new MTAlert().showHelpAlert("Ersetzungstabelle",
                HelpText.DOWNLOAD_REPLACELIST));


        gridPane.add(tglAscii, 0, 0);
        GridPane.setHalignment(btnHelpAscii, HPos.RIGHT);
        gridPane.add(btnHelpAscii, 2, 0);

        gridPane.add(tglReplace, 0, 1);
        GridPane.setHalignment(btnHelpReplace, HPos.RIGHT);
        gridPane.add(btnHelpReplace, 2, 1);


        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
    }


    private void initTable(VBox vBox) {

        final TableColumn<ReplaceData, String> fromColumn = new TableColumn<>("Von");
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));

        final TableColumn<ReplaceData, String> toColumn = new TableColumn<>("Nach");
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.setMinHeight(Region.USE_PREF_SIZE);

        tableView.getColumns().addAll(expander, fromColumn, toColumn);
        tableView.setItems(Daten.getInstance().replaceList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);

        Button del = new Button("");
        del.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        del.setOnAction(event -> {
            final ObservableList<ReplaceData> sels = tableView.getSelectionModel().getSelectedItems();

            if (sels == null || sels.isEmpty()) {
                new MTAlert().showInfoNoSelection();
            } else {
                Daten.getInstance().replaceList.removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button neu = new Button("");
        neu.setGraphic(new Icons().ICON_BUTTON_ADD);
        neu.setOnAction(event -> {
            ReplaceData replaceData = new ReplaceData();
            Daten.getInstance().replaceList.add(replaceData);
            tableView.getSelectionModel().select(replaceData);
            tableView.scrollTo(replaceData);
        });

        Button up = new Button("");
        up.setGraphic(new Icons().ICON_BUTTON_MOVE_UP);
        up.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                new MTAlert().showInfoNoSelection();
            } else {
                int res = Daten.getInstance().replaceList.up(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        Button down = new Button("");
        down.setGraphic(new Icons().ICON_BUTTON_MOVE_DOWN);
        down.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();

            if (sel < 0) {
                new MTAlert().showInfoNoSelection();
            } else {
                int res = Daten.getInstance().replaceList.up(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });

        Button reset = new Button("Tabelle zurücksetzen");
        reset.setOnAction(event -> {
            Daten.getInstance().replaceList.init();
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(neu, del, up, down, reset);
        vBox.getChildren().addAll(hBox);

    }

    TableRowExpanderColumn<ReplaceData> expander = new TableRowExpanderColumn<>(param -> {
        final GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: #E0E0E0;");
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);

        TextField txtFrom = new TextField();
        txtFrom.textProperty().bindBidirectional(param.getValue().fromProperty());

        TextField txtTo = new TextField();
        txtTo.textProperty().bindBidirectional(param.getValue().toProperty());


        gridPane.add(new Label("Von: "), 0, 0);
        gridPane.add(txtFrom, 1, 0);
        gridPane.add(new Label("Nach: "), 0, 1);
        gridPane.add(txtTo, 1, 1);

        return gridPane;
    });

    private Callback<TableColumn<ReplaceData, String>, TableCell<ReplaceData, String>> cellFactoryDel
            = (final TableColumn<ReplaceData, String> param) -> {


        final TableCell<ReplaceData, String> cell = new TableCell<ReplaceData, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                ReplaceData blackData = tableView.getItems().get(getIndex());

                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnDel;

                btnDel = new Button("x");

                btnDel.setOnAction(event -> {
                    Daten.getInstance().replaceList.remove(blackData);
                });
                hbox.getChildren().add(btnDel);
                setGraphic(hbox);
            }
        };
        return cell;
    };


}
