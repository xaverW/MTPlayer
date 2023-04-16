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

package de.p2tools.mtplayer.gui.mediadialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.controller.history.HistoryList;
import de.p2tools.mtplayer.controller.mediadb.MediaSearchPredicateFactory;
import de.p2tools.p2lib.alert.PAlert;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Date;

public class PaneAbo extends PaneDialog {

    private final boolean abo;
    private final HistoryList list;
    private final Stage stage;
    private final FilteredList<HistoryData> filteredList;
    private final SortedList<HistoryData> sortedList;

    private ProgData progData = ProgData.getInstance();

    public PaneAbo(Stage stage, String searchThemeOrg, String searchTitelOrg, StringProperty searchStringProp, boolean abo) {
        super(searchThemeOrg, searchTitelOrg, searchStringProp, false);
        this.stage = stage;
        this.abo = abo;
        if (abo) {
            list = progData.erledigteAbos;
            textSearch = new Text("Abos, suchen im: " +
                    (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL ?
                            "Titel des Abos" : "Thema oder Titel des Abos"));
            textSearch.setFont(Font.font(null, FontWeight.BOLD, -1));
            textSearch.getStyleClass().add("downloadGuiMediaText");
            ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.addListener((u, o, n) -> textSearch.setText("Abos, suchen im: " +
                    (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL ?
                            "Titel des Abos" : "Thema oder Titel des Abos")));

        } else {
            list = progData.history;
            textSearch = new Text("History, suchen im: " +
                    (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL ?
                            "Titel des History-Films" : "Thema oder Titel des History-Films"));
            textSearch.setFont(Font.font(null, FontWeight.BOLD, -1));
            textSearch.getStyleClass().add("downloadGuiMediaText");
            ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.addListener((u, o, n) -> textSearch.setText("Abos, suchen im: " +
                    (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL ?
                            "Titel des History-Films" : "Thema oder Titel des History-Films")));
        }
        this.filteredList = new FilteredList<>(list, p -> true);
        this.sortedList = new SortedList<>(filteredList);
    }

    @Override
    public void close() {
        list.removeListener(listener);
    }

    @Override
    void initTable() {
        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(20.0 / 100));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(50.0 / 100));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        final TableColumn<HistoryData, Date> dateColumn = new TableColumn<>("Datum");
        dateColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(15.0 / 100));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        dateColumn.getStyleClass().add("alignCenter");

        final TableColumn<HistoryData, String> pathColumn = new TableColumn<>("Url");
        pathColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(14.0 / 100));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        tableAbo.getColumns().addAll(themeColumn, titleColumn, dateColumn, pathColumn);

        tableAbo.getSelectionModel().selectedItemProperty().addListener((observableValue, dataOld, dataNew) -> {
            setTableSel(dataNew);
        });
        tableAbo.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                ArrayList<HistoryData> historyDataArrayList = new ArrayList<>();
                historyDataArrayList.addAll(tableAbo.getSelectionModel().getSelectedItems());
                if (historyDataArrayList.isEmpty()) {
                    PAlert.showInfoNoSelection();

                } else {
                    ContextMenu contextMenu =
                            new PaneHistoryContextMenu(stage, historyDataArrayList, !abo).getContextMenu();
                    tableAbo.setContextMenu(contextMenu);
                }
            }
        });
        tableAbo.setRowFactory(tv -> {
            TableRow<HistoryData> row = new TableRow<>();
            row.hoverProperty().addListener((observable) -> {
                final HistoryData historyData = row.getItem();
                if (row.isHover() && historyData != null) {
                    setTableSel(historyData);
                } else {
                    setTableSel(tableAbo.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });

        sortedList.comparatorProperty().bind(tableAbo.comparatorProperty());
        tableAbo.setItems(sortedList);
    }

    private void setTableSel(HistoryData historyData) {
        if (historyData == null) {
            txtTitleMedia.setText("");
            txtPathMedia.setText("");
        } else {
            txtTitleMedia.setText(historyData.getTitle());
            txtPathMedia.setText(historyData.getUrl());
        }
    }

    @Override
    void initAction() {
        super.initAction();

        lblGesamtMedia.setText(list.size() + "");
        listener = c -> Platform.runLater(() -> {
            lblGesamtMedia.setText(list.size() + "");
            filter();
        });
        list.addListener(listener);

        btnClearList.setOnAction(a -> {
            list.clearAll(stage);
        });
    }

    @Override
    void filter() {
        filteredList.setPredicate(
                MediaSearchPredicateFactory.getPredicateHistoryData(
                        txtSearch.getText()));
        lblHits.setText(filteredList.size() + "");
    }
}
