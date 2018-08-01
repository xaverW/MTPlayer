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
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.BlackData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.gui.tools.Table;
import de.mtplayer.mtp.tools.filmListFilter.FilmlistBlackFilterCountHits;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.util.Collection;

public class BlackPane {
    private final TableView<BlackData> tableView = new TableView<>();

    private final GridPane gridPane = new GridPane();
    private final ComboBox<String> cboChannel = new ComboBox<>();
    private final PToggleSwitch tgChannel = new PToggleSwitch("exakt:");
    private final TextField theme = new TextField();
    private final PToggleSwitch tgTheme = new PToggleSwitch("exakt:");
    private final TextField title = new TextField();
    private final TextField themeTitle = new TextField();
    private BlackData blackData = null;

    BooleanProperty propWhite = ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getBooleanProperty();

    public void makeBlackTable(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(10);

        makeConfig(vBox);
        initTable(vBox);
        addConfigs(vBox);

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
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);

        final TableColumn<BlackData, String> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        nrColumn.getStyleClass().add("center");

        final TableColumn<BlackData, String> channelColumn = new TableColumn<>("Sender");
        channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));

        final TableColumn<BlackData, Boolean> channelExactColumn = new TableColumn<>("Sender exakt");
        channelExactColumn.setCellValueFactory(new PropertyValueFactory<>("channelExact"));
        channelExactColumn.setCellFactory(CheckBoxTableCell.forTableColumn(channelExactColumn));

        final TableColumn<BlackData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        final TableColumn<BlackData, Boolean> themeExactColumn = new TableColumn<>("Thema exakt");
        themeExactColumn.setCellValueFactory(new PropertyValueFactory<>("themeExact"));
        themeExactColumn.setCellFactory(CheckBoxTableCell.forTableColumn(themeExactColumn));

        final TableColumn<BlackData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        final TableColumn<BlackData, String> themeTitleColumn = new TableColumn<>("Thema-Titel");
        themeTitleColumn.setCellValueFactory(new PropertyValueFactory<>("themeTitle"));

        final TableColumn<BlackData, Integer> hitsColumn = new TableColumn<>("Treffer");
        hitsColumn.setCellValueFactory(new PropertyValueFactory<>("countHits"));

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(nrColumn, channelColumn, channelExactColumn, themeColumn, themeExactColumn,
                titleColumn, themeTitleColumn, hitsColumn);
        tableView.setItems(ProgData.getInstance().blackList);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActBlackData));

        Button btnDel = new Button("");
        btnDel.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        btnDel.setOnAction(event -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();

            if (selected == null || selected.isEmpty()) {
                new MTAlert().showInfoNoSelection();
            } else {
                ProgData.getInstance().blackList.removeAll(selected);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(new Icons().ICON_BUTTON_ADD);
        btnNew.setOnAction(event -> {
            BlackData blackData = new BlackData();
            ProgData.getInstance().blackList.add(blackData);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(blackData);
            tableView.scrollTo(blackData);
        });

        Button btnCountHits = new Button("Treffer zählen");
        btnCountHits.setTooltip(new Tooltip("Damit kann man die Filmliste nach Treffern durchsuchen.\n" +
                "Für jeden Eintrag in der Blacklist wird gezählt,\n" +
                "wieviele Filme damit geblockt werden."));
        btnCountHits.setOnAction(a -> {
            FilmlistBlackFilterCountHits.countHits();
            Table.refresh_table(tableView);
        });

        HBox hBoxCount = new HBox();
        hBoxCount.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(hBoxCount, Priority.ALWAYS);
        hBoxCount.getChildren().add(btnCountHits);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(btnNew, btnDel, hBoxCount);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView, hBox);

    }

    private void addConfigs(VBox vBox) {

        gridPane.setStyle("-fx-background-color: #E0E0E0;");

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);

        cboChannel.setEditable(true);
        cboChannel.setItems(ProgData.getInstance().nameLists.getObsAllChannel());

        gridPane.add(new Label("Sender:"), 0, 0);
        gridPane.add(cboChannel, 1, 0);
        gridPane.add(tgChannel, 2, 0);

        gridPane.add(new Label("Thema:"), 0, 1);
        gridPane.add(theme, 1, 1);
        gridPane.add(tgTheme, 2, 1);

        gridPane.add(new Label("Titel:"), 0, 2);
        gridPane.add(title, 1, 2);
        gridPane.add(new Label("Thema/Titel:"), 0, 3);
        gridPane.add(themeTitle, 1, 3);

        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);
    }

    private void setActBlackData() {
        BlackData blackDataAct = tableView.getSelectionModel().getSelectedItem();
        if (blackDataAct == blackData) {
            return;
        }

        if (blackData != null) {
            cboChannel.valueProperty().unbindBidirectional(blackData.channelProperty());
            tgChannel.selectedProperty().unbindBidirectional(blackData.channelExactProperty());
            theme.textProperty().unbindBidirectional(blackData.themeProperty());
            tgTheme.selectedProperty().unbindBidirectional(blackData.themeExactProperty());
            title.textProperty().unbindBidirectional(blackData.titleProperty());
            themeTitle.textProperty().unbindBidirectional(blackData.themeTitleProperty());
        }

        blackData = blackDataAct;
        gridPane.setDisable(blackData == null);
        if (blackData != null) {
            cboChannel.valueProperty().bindBidirectional(blackData.channelProperty());
            tgChannel.selectedProperty().bindBidirectional(blackData.channelExactProperty());
            theme.textProperty().bindBidirectional(blackData.themeProperty());
            tgTheme.selectedProperty().bindBidirectional(blackData.themeExactProperty());
            title.textProperty().bindBidirectional(blackData.titleProperty());
            themeTitle.textProperty().bindBidirectional(blackData.themeTitleProperty());
        }

    }

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
