/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

package de.mtplayer.mtp.gui.mediaDialog;

import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.HistoryData;
import de.mtplayer.mtp.tools.storedFilter.Filter;
import javafx.application.Platform;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Date;
import java.util.regex.Pattern;

public class MediaDialogAboPane extends ScrollPane {

    Label lblGesamtAbo = new Label();
    Label lblTrefferAbo = new Label();
    TableView<HistoryData> tableAbo = new TableView();
    TextField txtTitleAbo = new TextField();
    TextField txtUrlAbo = new TextField();

    Daten daten = Daten.getInstance();
    String searchStr = "";

    public MediaDialogAboPane() {
        initPanel();
    }

    private void initPanel() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        hBox.getChildren().addAll(new Label("Treffer:"), lblTrefferAbo, region,
                new Label("Anzahl Medien gesamt:"), lblGesamtAbo);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Titel:"), 0, 0);
        GridPane.setHgrow(txtTitleAbo, Priority.ALWAYS);
        txtTitleAbo.setEditable(false);
        gridPane.add(txtTitleAbo, 1, 0);

        gridPane.add(new Label("Url:"), 0, 1);
        txtUrlAbo.setEditable(false);
        GridPane.setHgrow(txtUrlAbo, Priority.ALWAYS);
        gridPane.add(txtUrlAbo, 1, 1);

        VBox vBoxAbo = new VBox();
        VBox.setVgrow(tableAbo, Priority.ALWAYS);
        vBoxAbo.getChildren().addAll(hBox, tableAbo, gridPane);

        this.setContent(vBoxAbo);
    }

    public void make() {
        daten.erledigteAbos.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                lblGesamtAbo.setText(daten.erledigteAbos.size() + "");
                filter();
            });
        });

        initTableAbo();
        setTableDate();
    }

    private void initTableAbo() {
        txtTitleAbo.setText("");
        txtUrlAbo.setText("");
        tableAbo.getColumns().clear();

        final TableColumn<HistoryData, String> themaColumn = new TableColumn<>("Thema");
        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        final TableColumn<HistoryData, Date> dateColumn = new TableColumn<>("Datum");
        final TableColumn<HistoryData, String> pathColumn = new TableColumn<>("Url");

        themaColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        tableAbo.getColumns().addAll(themaColumn, titleColumn, dateColumn, pathColumn);
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);

        themaColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(20.0 / 100));
        titleColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(50.0 / 100));
        dateColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(15.0 / 100));
        pathColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(14.0 / 100));

        tableAbo.getSelectionModel().selectedItemProperty().addListener((observableValue, dataOld, dataNew) -> {
            if (dataNew != null) {
                txtTitleAbo.setText(dataNew.getTitle());
                txtUrlAbo.setText(dataNew.getUrl());
            } else {
                txtTitleAbo.setText("");
                txtUrlAbo.setText("");
            }
        });

    }

    private void setTableDate() {
        SortedList<HistoryData> sortedList = daten.erledigteAbos.getSortedList();

        lblGesamtAbo.setText(daten.erledigteAbos.size() + "");
        tableAbo.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableAbo.comparatorProperty());
    }


    public void filter() {
        filter(searchStr);
    }

    public void filter(String searchStr) {
        this.searchStr = searchStr;
        daten.erledigteAbos.filterdListSetPred(media -> {
            if (searchStr.isEmpty()) {
                return false;
            }
            final Pattern p = Filter.makePattern(searchStr);
            if (p != null) {
                return filterAbo(media, p);
            } else {
                return filterAbo(media, searchStr);
            }
        });
        lblTrefferAbo.setText(daten.erledigteAbos.getFilteredList().size() + "");
    }

    private boolean filterAbo(HistoryData historyData, Pattern p) {
        return p.matcher(historyData.getTheme()).matches() || p.matcher(historyData.getTitle()).matches();
    }

    private boolean filterAbo(HistoryData historyData, String search) {
        return (historyData.getTheme().toLowerCase().contains(search)
                || historyData.getTitle().toLowerCase().contains(search));
    }

}
