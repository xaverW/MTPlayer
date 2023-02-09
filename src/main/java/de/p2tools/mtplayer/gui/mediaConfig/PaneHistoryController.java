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

package de.p2tools.mtplayer.gui.mediaConfig;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.dialogs.accordion.PAccordionPane;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import de.p2tools.p2Lib.mtFilter.FilterCheckRegEx;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class PaneHistoryController extends PAccordionPane {

    private final TextField txtSearch = new TextField();
    private final Label lblTreffer = new Label();
    private final RadioButton rbTheme = new RadioButton("Thema");
    private final RadioButton rbTitle = new RadioButton("Titel");
    private final RadioButton rbTt = new RadioButton("Thema oder Titel");

    private final IntegerProperty search;
    private final StringProperty searchText;
    private ListChangeListener<HistoryData> listener;

    private final boolean history;
    private final ProgData progData;
    private final Stage stage;

    public PaneHistoryController(Stage stage, boolean history, StringProperty searchText) {
        super(stage, ProgConfig.MEDIA_CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_MEDIA_DIALOG_HISTORY);
        this.stage = stage;
        this.history = history;
        this.searchText = searchText;
        progData = ProgData.getInstance();
        search = ProgConfig.MEDIA_CONFIG_DIALOG_SEARCH;

        init();
    }

    @Override
    public void close() {
        super.close();
        if (history) {
            progData.history.removeListener(listener);
        } else {
            progData.erledigteAbos.removeListener(listener);
        }
    }

    public void tabChange() {
        selectSearch();
        txtSearch.setText(searchText.getValueSafe());
    }

    @Override
    public Collection<TitledPane> createPanes() {
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(rbTheme, rbTitle, rbTt);
        selectSearch();

        VBox vBox = new VBox(10);
        Collection<TitledPane> result = new ArrayList();
        TitledPane tpConfig = new TitledPane(history ? "History" : "Downloads", vBox);
        result.add(tpConfig);
        tpConfig.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpConfig, Priority.ALWAYS);

        initTable(vBox);
        initFilter(vBox);
        writeQuantity();

        listener = c -> Platform.runLater(() -> {
            PaneHistoryController.this.writeQuantity();
        });
        if (history) {
            progData.history.addListener(listener);
        } else {
            progData.erledigteAbos.addListener(listener);
        }

        return result;
    }

    private void initTable(VBox vBox) {
        TableView<HistoryData> tableView = new TableView<>();
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TableColumn<HistoryData, String> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.getStyleClass().add("alignCenter");

        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        final TableColumn<HistoryData, String> urlColumn = new TableColumn<>("Url");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        dateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(20.0 / 100));
        themeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(30.0 / 100));
        titleColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(30.0 / 100));
        urlColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(19.0 / 100));

        tableView.getColumns().addAll(dateColumn, themeColumn, titleColumn, urlColumn);
        if (history) {
            SortedList<HistoryData> sortedList = progData.history.getSortedList();
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            progData.history.filterdListSetPredTrue();
            tableView.setItems(sortedList);
        } else {
            SortedList<HistoryData> sortedList = progData.erledigteAbos.getSortedList();
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            progData.erledigteAbos.filterdListSetPredTrue();
            tableView.setItems(sortedList);
        }

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                ArrayList<HistoryData> historyDataArrayList = new ArrayList<>();
                historyDataArrayList.addAll(tableView.getSelectionModel().getSelectedItems());
                if (historyDataArrayList.isEmpty()) {
                    PAlert.showInfoNoSelection();

                } else {
                    ContextMenu contextMenu =
                            new PaneHistoryContextMenu(stage, historyDataArrayList, history).getContextMenu();
                    tableView.setContextMenu(contextMenu);
                }
            }
        });

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void selectSearch() {
        switch (search.get()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEMA:
                rbTheme.setSelected(true);
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_TITEL:
                rbTitle.setSelected(true);
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEMA_TITEL:
                rbTt.setSelected(true);
                break;
            default:
                rbTt.setSelected(true);
                search.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEMA_TITEL);
                break;
        }
    }

    private void initFilter(VBox vBox) {
        Button btnDel = new Button("_Liste löschen");
        btnDel.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
        btnDel.setOnAction(event -> {
            if (history) {
                progData.history.clearAll(stage);
            } else {
                progData.erledigteAbos.clearAll(stage);
            }
        });

        Button btnClear = new Button();
        btnClear.setGraphic(ProgIcons.Icons.ICON_BUTTON_STOP.getImageView());
        btnClear.setOnAction(a -> txtSearch.clear());

        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.getChildren().addAll(rbTheme, rbTitle, rbTt);

        HBox hBoxSum = new HBox(P2LibConst.DIST_BUTTON);
        hBoxSum.getChildren().addAll(lblTreffer, PGuiTools.getHBoxGrower(), btnDel);
        vBox.getChildren().addAll(hBoxSum);

        GridPane.setHgrow(txtSearch, Priority.ALWAYS);
        GridPane.setHalignment(btnClear, HPos.RIGHT);
        GridPane.setHalignment(btnDel, HPos.RIGHT);
        GridPane.setValignment(txtSearch, VPos.CENTER);

        GridPane gridPane = new GridPane();
//        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        gridPane.add(hBoxSum, 0, 0, 3, 1);

        gridPane.add(new Label("Suchen: "), 0, 1);
        gridPane.add(txtSearch, 1, 1);
        gridPane.add(btnClear, 2, 1);

        gridPane.add(hBox, 1, 2, 2, 1);

        vBox.getChildren().add(gridPane);

        FilterCheckRegEx fTT = new FilterCheckRegEx(txtSearch);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText.setValue(txtSearch.getText());
            fTT.checkPattern();
            filter();
        });

        rbTheme.selectedProperty().addListener((o, ol, ne) -> {
            if (ne) filter();
        });
        rbTitle.selectedProperty().addListener((o, ol, ne) -> {
            if (ne) filter();
        });
        rbTt.selectedProperty().addListener((o, ol, ne) -> {
            if (ne) filter();
        });
    }

    public void filter() {
        if (rbTheme.isSelected()) {
            search.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEMA);
        } else if (rbTitle.isSelected()) {
            search.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_TITEL);
        } else {
            search.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEMA_TITEL);
        }

        if (history) {
            progData.history.filteredListSetPredicate(SearchPredicateWorker.getPredicateHistoryData(rbTheme.isSelected(), rbTitle.isSelected(),
                    txtSearch.getText(), true));
        } else {
            progData.erledigteAbos.filteredListSetPredicate(SearchPredicateWorker.getPredicateHistoryData(rbTheme.isSelected(), rbTitle.isSelected(),
                    txtSearch.getText(), true));
        }

        writeQuantity();
    }

    private void writeQuantity() {
        final int filtered;
        final int sum;
        if (history) {
            filtered = progData.history.getFilteredList().size();
            sum = progData.history.size();
        } else {
            filtered = progData.erledigteAbos.getFilteredList().size();
            sum = progData.erledigteAbos.size();
        }

        if (sum != filtered) {
            lblTreffer.setText("Anzahl: " + filtered + " von " + sum);
        } else {
            lblTreffer.setText("Anzahl: " + sum + "");
        }
    }
}
