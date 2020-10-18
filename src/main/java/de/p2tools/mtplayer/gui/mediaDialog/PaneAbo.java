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

package de.p2tools.mtplayer.gui.mediaDialog;

import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.tools.storedFilter.FilterCheckRegEx;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.gui.mediaConfig.SearchPredicateWorker;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
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

public class PaneAbo extends ScrollPane {

    private Label lblGesamtAbo = new Label();
    private Label lblTrefferAbo = new Label();
    private TableView<HistoryData> tableAbo = new TableView();
    private TextField txtTitleAbo = new TextField();
    private TextField txtUrlAbo = new TextField();
    private final TextField txtSearch = new TextField();
    private final Button btnReset = new Button("");
    private final RadioButton rbTheme = new RadioButton("Thema");
    private final RadioButton rbTitle = new RadioButton("Titel");
    private final RadioButton rbTt = new RadioButton("Thema oder Titel");
    private final IntegerProperty search;
    private final Listener listenerDbStart;
    private final Listener listenerDbStop;
    private ProgData progData = ProgData.getInstance();

    private final String searchStrOrg;
    private StringProperty searchStrProp;

    private ListChangeListener<HistoryData> listener;

    public PaneAbo(Stage stage, String searchStrOrg, StringProperty searchStrProp) {
        this.searchStrOrg = searchStrOrg;
        this.searchStrProp = searchStrProp;
        search = ProgConfig.MEDIA_DIALOG_SEARCH_ABO.getIntegerProperty();
        listenerDbStart = new Listener(Listener.EREIGNIS_MEDIA_DB_START, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB suchen
                txtSearch.setDisable(true);
            }
        };
        listenerDbStop = new Listener(Listener.EREIGNIS_MEDIA_DB_STOP, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB liegt vor
                txtSearch.setDisable(false);
            }
        };
    }

    public void make() {
        initPanel();
        initTableAbo();
        initAction();
        filter();
    }

    public void close() {
        Listener.removeListener(listenerDbStart);
        Listener.removeListener(listenerDbStop);
        progData.erledigteAbos.removeListener(listener);
    }

    private void initPanel() {
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(rbTheme, rbTitle, rbTt);
        switch (search.get()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEMA:
                rbTheme.setSelected(true);
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_TITEL:
                rbTitle.setSelected(true);
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_THEMA_TITEL:
            default:
                rbTt.setSelected(true);
                break;
        }

        btnReset.setGraphic(new ProgIcons().ICON_BUTTON_RESET);
        btnReset.setTooltip(new Tooltip("Suchtext wieder herstellen"));

        GridPane gridPaneSearch = new GridPane();
        gridPaneSearch.setPadding(new Insets(10));
        gridPaneSearch.setHgap(10);
        gridPaneSearch.setVgap(10);
        txtSearch.setPrefWidth(Double.MAX_VALUE);
        GridPane.setHgrow(txtSearch, Priority.ALWAYS);
        gridPaneSearch.getStyleClass().add("extra-pane");

        gridPaneSearch.add(new Label("Suchen: "), 0, 0);
        gridPaneSearch.add(txtSearch, 1, 0, 3, 1);
        gridPaneSearch.add(btnReset, 4, 0);

        gridPaneSearch.add(rbTheme, 1, 1);
        gridPaneSearch.add(rbTitle, 2, 1);
        gridPaneSearch.add(rbTt, 3, 1);

        HBox hBoxSum = new HBox(10);
        hBoxSum.setPadding(new Insets(10));
        hBoxSum.getChildren().addAll(new Label("Treffer:"), lblTrefferAbo, PGuiTools.getHBoxGrower(),
                new Label("Anzahl Medien gesamt:"), lblGesamtAbo);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        GridPane.setHgrow(txtUrlAbo, Priority.ALWAYS);
        txtUrlAbo.setEditable(false);

        gridPane.add(new Label("Titel:"), 0, 0);
        gridPane.add(txtTitleAbo, 1, 0);
        gridPane.add(new Label("Url:"), 0, 1);
        gridPane.add(txtUrlAbo, 1, 1);

        VBox vBoxAbo = new VBox();
        VBox.setVgrow(tableAbo, Priority.ALWAYS);
        vBoxAbo.getChildren().addAll(gridPaneSearch, PGuiTools.getHDistance(10), tableAbo, hBoxSum, gridPane);
        this.setContent(vBoxAbo);
    }

    private void initTableAbo() {
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
        sortedList.comparatorProperty().bind(tableAbo.comparatorProperty());
        tableAbo.setItems(sortedList);
    }

    private void initAction() {
        FilterCheckRegEx fTT = new FilterCheckRegEx(txtSearch);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            fTT.checkPattern();
            filter();
            searchStrProp.setValue(txtSearch.getText());
        });
        txtSearch.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearch.getSelectedText();
                txtSearch.setText(sel);
            }
        });
        btnReset.setOnAction(a -> txtSearch.setText(searchStrOrg));

        Listener.addListener(listenerDbStart);
        Listener.addListener(listenerDbStop);

        lblGesamtAbo.setText(progData.erledigteAbos.size() + "");
        listener = c -> Platform.runLater(() -> {
            lblGesamtAbo.setText(progData.erledigteAbos.size() + "");
            filter();
        });
        progData.erledigteAbos.addListener(listener);

        rbTheme.selectedProperty().addListener((o, ol, ne) -> filter());
        rbTitle.selectedProperty().addListener((o, ol, ne) -> filter());
        rbTt.selectedProperty().addListener((o, ol, ne) -> filter());
    }

    public void filter(String searStr) {
        txtSearch.setText(searStr);
        filter();
    }

    private void filter() {
        if (rbTheme.isSelected()) {
            search.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEMA);
        } else if (rbTitle.isSelected()) {
            search.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_TITEL);
        } else {
            search.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEMA_TITEL);
        }

        progData.erledigteAbos.filteredListSetPredicate(SearchPredicateWorker.getPredicateHistoryData(rbTheme.isSelected(), rbTitle.isSelected(),
                txtSearch.getText(), false));
        lblTrefferAbo.setText(progData.erledigteAbos.getFilteredList().size() + "");
    }

}
