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

package de.mtplayer.mtp.gui.mediaConfig;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.history.HistoryData;
import de.mtplayer.mtp.tools.storedFilter.Filter;
import de.mtplayer.mtp.tools.storedFilter.FilterCheckRegEx;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.dialogs.accordion.PAccordionPane;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class PaneHistoryController extends PAccordionPane {

    private final TextField txtSearch = new TextField();
    private final Label lblTreffer = new Label();
    private final RadioButton rbTheme = new RadioButton("Thema");
    private final RadioButton rbTitle = new RadioButton("Titel");
    private final RadioButton rbTt = new RadioButton("Thema oder Titel");

    private final IntegerProperty search;
    private ListChangeListener<HistoryData> listener;

    private final boolean history;
    private final ProgData progData;
    private final Stage stage;

    public PaneHistoryController(Stage stage, boolean history) {
        super(stage, ProgConfig.MEDIA_CONFIG_DIALOG_ACCORDION.getBooleanProperty(), ProgConfig.SYSTEM_MEDIA_DIALOG_HISTORY);
        this.stage = stage;
        this.history = history;
        progData = ProgData.getInstance();
        init();

        if (history) {
            search = ProgConfig.MEDIA_CONFIG_DIALOG_SEARCH_HISTORY.getIntegerProperty();
        } else {
            search = ProgConfig.MEDIA_CONFIG_DIALOG_SEARCH_ABO.getIntegerProperty();
        }
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(rbTheme, rbTitle, rbTt);
        switch (search.get()) {
            case 0:
                rbTheme.setSelected(true);
                break;
            case 1:
                rbTitle.setSelected(true);
                break;
            case 2:
            default:
                rbTt.setSelected(true);
                break;
        }
    }

    public void close() {
        super.close();
        if (history) {
            progData.history.removeListener(listener);
        } else {
            progData.erledigteAbos.removeListener(listener);
        }
    }

    public Collection<TitledPane> createPanes() {
        VBox vBox = new VBox(10);
        Collection<TitledPane> result = new ArrayList();
        TitledPane tpConfig = new TitledPane(history ? "History" : "Downloads", vBox);
        result.add(tpConfig);
        tpConfig.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpConfig, Priority.ALWAYS);

        initTable(vBox);
        initFilter(vBox);
        writeQuantity();

        listener = new ListChangeListener<HistoryData>() {
            @Override
            public void onChanged(Change<? extends HistoryData> c) {
                Platform.runLater(() -> {
                    PaneHistoryController.this.writeQuantity();
                });
            }
        };
        if (history) {
            progData.history.addListener(listener);
        } else {
            progData.erledigteAbos.addListener(listener);
        }

        return result;
    }

    private void initTable(VBox vBox) {
        TableView<HistoryData> tableView = new TableView<>();
        tableView.setMinHeight(Region.USE_PREF_SIZE);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TableColumn<HistoryData, String> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

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
        btnClear.setGraphic(new ProgIcons().ICON_BUTTON_STOP);
        btnClear.setOnAction(a -> txtSearch.clear());

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(new Label("Suchen: "), txtSearch, btnClear, lblTreffer);
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        hBox.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().addAll(hBox);

        hBox = new HBox(10);
        Label lbl = new Label();
        lbl.setMaxWidth(Double.MAX_VALUE);
        hBox.getChildren().addAll(rbTheme, rbTitle, rbTt, lbl, btnDel);
        HBox.setHgrow(lbl, Priority.ALWAYS);
        vBox.getChildren().addAll(hBox);

        FilterCheckRegEx fTT = new FilterCheckRegEx(txtSearch);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            fTT.checkPattern();
            filter();
        });

        rbTheme.selectedProperty().addListener((o, ol, ne) -> filter());
        rbTitle.selectedProperty().addListener((o, ol, ne) -> filter());
        rbTt.selectedProperty().addListener((o, ol, ne) -> filter());
    }

    private void filter() {
        if (rbTheme.isSelected()) {
            search.setValue(0);
        } else if (rbTitle.isSelected()) {
            search.setValue(1);
        } else {
            search.setValue(2);
        }

        final String search = txtSearch.getText().toLowerCase().trim();

        if (history) {
            progData.history.filteredListSetPred(historyData ->
                    compare(search, historyData));

        } else {
            progData.erledigteAbos.filteredListSetPred(media ->
                    compare(search, media));
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

    private boolean compare(String search, HistoryData media) {
        if (search.isEmpty()) {
            return true;
        }
        final Pattern p = Filter.makePattern(search);
        if (p != null) {
            return filterData(media, p);
        } else {
            return filterData(media, search);
        }
    }

    private boolean filterData(HistoryData historyData, Pattern p) {
        if (rbTheme.isSelected()) {
            return (p.matcher(historyData.getTheme()).matches());
        } else if (rbTitle.isSelected()) {
            return (p.matcher(historyData.getTitle()).matches());
        } else {
            return (p.matcher(historyData.getTheme()).matches()) ||
                    (p.matcher(historyData.getTitle()).matches());
        }
    }

    private boolean filterData(HistoryData historyData, String search) {
        if (search.isEmpty()) {
            return true;
        }

        if (rbTheme.isSelected()) {
            return (historyData.getTheme().toLowerCase().contains(search));
        } else if (rbTitle.isSelected()) {
            return (historyData.getTitle().toLowerCase().contains(search));
        } else {
            return (historyData.getTheme().toLowerCase().contains(search) ||
                    historyData.getTitle().toLowerCase().contains(search));
        }
    }

}
