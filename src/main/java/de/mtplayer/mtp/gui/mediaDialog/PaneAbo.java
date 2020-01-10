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

package de.mtplayer.mtp.gui.mediaDialog;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.history.HistoryData;
import de.mtplayer.mtp.tools.storedFilter.Filter;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Date;
import java.util.regex.Pattern;

public class PaneAbo extends ScrollPane {

    private Label lblGesamtAbo = new Label();
    private Label lblTrefferAbo = new Label();
    private TableView<HistoryData> tableAbo = new TableView();
    private TextField txtTitleAbo = new TextField();
    private TextField txtUrlAbo = new TextField();

    private ProgData progData = ProgData.getInstance();
    private String searchStr = "";

    private ListChangeListener<HistoryData> listener;

    public PaneAbo(Stage stage) {
        initPanel();
    }

    public void make() {
        listener = c -> Platform.runLater(() -> {
            lblGesamtAbo.setText(progData.erledigteAbos.size() + "");
            filter();
        });
        progData.erledigteAbos.addListener(listener);
        initTableAbo();
    }

    public void close() {
        progData.erledigteAbos.removeListener(listener);
    }

    public void filter(String searchStr) {
        this.searchStr = searchStr;
        progData.erledigteAbos.filteredListSetPred(media -> {
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
        lblTrefferAbo.setText(progData.erledigteAbos.getFilteredList().size() + "");
    }

    private void initPanel() {
        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(10));
        hBox.getChildren().addAll(new Label("Treffer:"), lblTrefferAbo, PGuiTools.getHBoxGrower(),
                new Label("Anzahl Medien gesamt:"), lblGesamtAbo);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        GridPane.setHgrow(txtTitleAbo, Priority.ALWAYS);
        txtTitleAbo.setEditable(false);

        GridPane.setHgrow(txtUrlAbo, Priority.ALWAYS);
        txtUrlAbo.setEditable(false);

        gridPane.add(new Label("Titel:"), 0, 0);
        gridPane.add(txtTitleAbo, 1, 0);
        gridPane.add(new Label("Url:"), 0, 1);
        gridPane.add(txtUrlAbo, 1, 1);

        VBox vBoxAbo = new VBox();
        VBox.setVgrow(tableAbo, Priority.ALWAYS);
        vBoxAbo.getChildren().addAll(hBox, tableAbo, gridPane);

        this.setContent(vBoxAbo);
    }

    private void initTableAbo() {
        txtTitleAbo.setText("");
        txtUrlAbo.setText("");
        tableAbo.getColumns().clear();

        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Thema");
        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        final TableColumn<HistoryData, Date> dateColumn = new TableColumn<>("Datum");
        final TableColumn<HistoryData, String> pathColumn = new TableColumn<>("Url");

        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        tableAbo.getColumns().addAll(themeColumn, titleColumn, dateColumn, pathColumn);
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);

        themeColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(20.0 / 100));
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

        SortedList<HistoryData> sortedList = progData.erledigteAbos.getSortedList();
        tableAbo.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableAbo.comparatorProperty());

        lblGesamtAbo.setText(progData.erledigteAbos.size() + "");
    }


    private void filter() {
        filter(searchStr);
    }

    private boolean filterAbo(HistoryData historyData, Pattern p) {
        return p.matcher(historyData.getTheme()).matches() || p.matcher(historyData.getTitle()).matches();
    }

    private boolean filterAbo(HistoryData historyData, String search) {
        return (historyData.getTheme().toLowerCase().contains(search)
                || historyData.getTitle().toLowerCase().contains(search));
    }
}
