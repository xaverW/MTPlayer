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

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.controller.history.HistoryList;
import de.p2tools.mtplayer.controller.mediadb.MediaSearchPredicateFactory;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.p2lib.alert.PAlert;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Date;

public class PaneDialogAbo extends PaneDialogScrollPane {

    //    private final boolean aboNotHistory;
    private final HistoryList list;
    private final Stage stage;
    private final FilteredList<HistoryData> filteredList;
    private final SortedList<HistoryData> sortedList;

    private ProgData progData = ProgData.getInstance();

    public PaneDialogAbo(Stage stage, MediaDataDto mediaDataDto) {
        // nur im MediaDialog
        super(mediaDataDto);
        this.stage = stage;
        if (mediaDataDto.whatToShow == MediaDataDto.SHOW_WHAT.SHOW_ABO) {
            list = progData.historyListAbos;
            textSearch.setFont(Font.font(null, FontWeight.BOLD, -1));
            textSearch.getStyleClass().add("downloadGuiMediaText");

            textSearch = new Text("Abos, suchen im: " +
                    (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME ?
                            "Titel des Abos" : "Thema oder Titel des Abos"));
            mediaDataDto.searchInWhat.addListener((u, o, n) -> textSearch.setText("Abos, suchen im: " +
                    (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME ?
                            "Titel des Abos" : "Thema oder Titel des Abos")));

        } else {
            list = progData.historyList;
            textSearch.setFont(Font.font(null, FontWeight.BOLD, -1));
            textSearch.getStyleClass().add("downloadGuiMediaText");

            textSearch = new Text("History, suchen im: " +
                    (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME ?
                            "Titel des History-Films" : "Thema oder Titel des History-Films"));
            mediaDataDto.searchInWhat.addListener((u, o, n) -> textSearch.setText("History, suchen im: " +
                    (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME ?
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
        tableAboOrHistory.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableAboOrHistory.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableAboOrHistory.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableAboOrHistory.setEditable(true);

        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        final TableColumn<HistoryData, Date> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        dateColumn.getStyleClass().add("alignCenter");

        final TableColumn<HistoryData, String> pathColumn = new TableColumn<>("Url");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        themeColumn.prefWidthProperty().bind(tableAboOrHistory.widthProperty().multiply(20.0 / 100));
        titleColumn.prefWidthProperty().bind(tableAboOrHistory.widthProperty().multiply(50.0 / 100));
        dateColumn.prefWidthProperty().bind(tableAboOrHistory.widthProperty().multiply(15.0 / 100));
        pathColumn.prefWidthProperty().bind(tableAboOrHistory.widthProperty().multiply(14.0 / 100));

        tableAboOrHistory.getColumns().addAll(themeColumn, titleColumn, dateColumn, pathColumn);

        tableAboOrHistory.getSelectionModel().selectedItemProperty().addListener((observableValue, dataOld, dataNew) -> {
            setTableSel(dataNew);
        });
        tableAboOrHistory.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                ArrayList<HistoryData> historyDataArrayList = new ArrayList<>();
                historyDataArrayList.addAll(tableAboOrHistory.getSelectionModel().getSelectedItems());
                if (historyDataArrayList.isEmpty()) {
                    PAlert.showInfoNoSelection();

                } else {
                    ContextMenu contextMenu =
                            new PaneHistoryContextMenu(stage, historyDataArrayList,
                                    mediaDataDto.whatToShow == MediaDataDto.SHOW_WHAT.SHOW_HISTORY).getContextMenu();
                    tableAboOrHistory.setContextMenu(contextMenu);
                }
            }
        });
        tableAboOrHistory.setRowFactory(tv -> {
            TableRow<HistoryData> row = new TableRow<>();
            row.hoverProperty().addListener((observable) -> {
                final HistoryData historyData = row.getItem();
                if (row.isHover() && historyData != null) {
                    setTableSel(historyData);
                } else {
                    setTableSel(tableAboOrHistory.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });

        sortedList.comparatorProperty().bind(tableAboOrHistory.comparatorProperty());
        tableAboOrHistory.setItems(sortedList);
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
    public void filter() {
        filteredList.setPredicate(MediaSearchPredicateFactory.getPredicateHistoryData(
                mediaDataDto.searchInWhat, txtSearch.getText()));
        lblHits.setText(filteredList.size() + "");
    }
}
