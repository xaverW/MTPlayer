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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.table.TableRowExpanderColumn;

import java.util.Collection;

public class BlackPane {
    TableView<BlackData> tableView = new TableView<>();

    BooleanProperty propWhite = ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getBooleanProperty();

    public void makeBlackTable(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(10);

        makeConfig(vBox);
        initTable(vBox);

        TitledPane tpBlack = new TitledPane("Blacklist", vBox);
        result.add(tpBlack);
        tpBlack.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpBlack, Priority.ALWAYS);
    }

    private void makeConfig(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);

        vBox.getChildren().add(gridPane);

        final RadioButton rbBlack = new RadioButton();
        final RadioButton rbWhite = new RadioButton();

        final ToggleGroup group = new ToggleGroup();
        rbBlack.setToggleGroup(group);
        rbWhite.setToggleGroup(group);

        rbBlack.setSelected(!ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getBool());

        gridPane.add(rbBlack, 0, 1);
        gridPane.add(new Label("\"Sender / Thema / Titel\" werden nicht angezeigt (Blacklist)"), 1, 1);
        final Button btnHelp = new PButton().helpButton("Blacklist / Whitelist",
                HelpText.BLACKLIST_WHITELIST);
        gridPane.add(btnHelp, 2, 1);

        rbWhite.selectedProperty().bindBidirectional(propWhite);
        gridPane.add(rbWhite, 0, 2);
        gridPane.add(new Label("nur diese \"Sender / Thema / Titel\" anzeigen (Whitelist)"), 1, 2);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
    }


    private void initTable(VBox vBox) {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setMinHeight(Region.USE_PREF_SIZE);

        final TableColumn<BlackData, String> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        nrColumn.getStyleClass().add("center");

        final TableColumn<BlackData, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));

        final TableColumn<BlackData, Boolean> senderExaktColumn = new TableColumn<>("Sender exakt");
        senderExaktColumn.setCellValueFactory(new PropertyValueFactory<>("senderExact"));
        senderExaktColumn.setCellFactory(CheckBoxTableCell.forTableColumn(senderExaktColumn));

        final TableColumn<BlackData, String> themaColumn = new TableColumn<>("Thema");
        themaColumn.setCellValueFactory(new PropertyValueFactory<>("thema"));

        final TableColumn<BlackData, Boolean> themaExaktColumn = new TableColumn<>("Thema exakt");
        themaExaktColumn.setCellValueFactory(new PropertyValueFactory<>("themaExact"));
        themaExaktColumn.setCellFactory(CheckBoxTableCell.forTableColumn(themaExaktColumn));

        final TableColumn<BlackData, Color> titelColumn = new TableColumn<>("Titel");
        titelColumn.setCellValueFactory(new PropertyValueFactory<>("titel"));

        final TableColumn<BlackData, Color> themaTitelColumn = new TableColumn<>("Thema-Titel");
        themaTitelColumn.setCellValueFactory(new PropertyValueFactory<>("themaTitel"));

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(expander, nrColumn, senderColumn, senderExaktColumn, themaColumn, themaExaktColumn,
                titelColumn, themaTitelColumn);
        tableView.setItems(ProgData.getInstance().blackList);


        Button del = new Button("");
        del.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        del.setOnAction(event -> {
            final ObservableList<BlackData> sels = tableView.getSelectionModel().getSelectedItems();

            if (sels == null || sels.isEmpty()) {
                new MTAlert().showInfoNoSelection();
            } else {
                ProgData.getInstance().blackList.removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button neu = new Button("");
        neu.setGraphic(new Icons().ICON_BUTTON_ADD);
        neu.setOnAction(event -> {
            BlackData blackData = new BlackData();
            ProgData.getInstance().blackList.add(blackData);
            tableView.getSelectionModel().select(blackData);
            tableView.scrollTo(blackData);
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(neu, del);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView, hBox);

    }

    TableRowExpanderColumn<BlackData> expander = new TableRowExpanderColumn<>(param -> {
        final GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: #E0E0E0;");

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cSender = new ComboBox<>();
        cSender.setEditable(true);
        cSender.valueProperty().bindBidirectional(param.getValue().senderProperty());
        cSender.setItems(ProgData.getInstance().nameLists.getObsAllSender());

        ToggleSwitch tgSender = new ToggleSwitch("exakt:");
        tgSender.selectedProperty().bindBidirectional(param.getValue().senderExactProperty());

        TextField thema = new TextField();
        thema.textProperty().bindBidirectional(param.getValue().themaProperty());

        ToggleSwitch tgThema = new ToggleSwitch("exakt:");
        tgThema.selectedProperty().bindBidirectional(param.getValue().themaExactProperty());

        TextField titel = new TextField();
        titel.textProperty().bindBidirectional(param.getValue().titelProperty());

        TextField themaTitel = new TextField();
        themaTitel.textProperty().bindBidirectional(param.getValue().themaTitelProperty());

        gridPane.add(new Label("Sender:"), 0, 0);
        gridPane.add(cSender, 1, 0);
        gridPane.add(tgSender, 2, 0);

        gridPane.add(new Label("Thema:"), 0, 1);
        gridPane.add(thema, 1, 1);
        gridPane.add(tgThema, 2, 1);

        gridPane.add(new Label("Titel:"), 0, 2);
        gridPane.add(titel, 1, 2);
        gridPane.add(new Label("Thema/Titel:"), 0, 3);
        gridPane.add(themaTitel, 1, 3);

        return gridPane;
    });

    private Callback<TableColumn<BlackData, String>, TableCell<BlackData, String>> cellFactoryDel
            = (final TableColumn<BlackData, String> param) -> {

        final TableCell<BlackData, String> cell = new TableCell<BlackData, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                BlackData blackData = tableView.getItems().get(getIndex());

                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnDel;

                btnDel = new Button("x");

                btnDel.setOnAction(event -> {
                    ProgData.getInstance().blackList.remove(blackData);
                });
                hbox.getChildren().add(btnDel);
                setGraphic(hbox);
            }
        };
        return cell;
    };


}
