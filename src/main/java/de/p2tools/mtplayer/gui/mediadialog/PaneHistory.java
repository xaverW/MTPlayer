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

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.history.HistoryData;
import de.p2tools.mtplayer.controller.mediadb.MediaSearchPredicateFactory;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.tools.table.CellHistorySource;
import de.p2tools.mtplayer.gui.tools.table.TableHistoryFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.table.P2RowFactory;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import de.p2tools.p2lib.mediathek.filter.FilterCheckRegEx;
import de.p2tools.p2lib.p2event.P2Listener;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Predicate;

public class PaneHistory extends ScrollPane {

    private final TextField txtSearch = new TextField();
    private final TitledPane tpDel = new TitledPane();
    private final PaneHistoryDel paneHistoryDel;
    private final Accordion accordion = new Accordion();
    private final ComboBox<String> cboAbo = new ComboBox<>();

    private final Label lblGesamtMedia = new Label();
    private final Label lblHits = new Label();
    private final TableView<HistoryData> tableHistory = new TableView<>();

    private final TextField txtTitleMedia = new TextField();
    private final TextField txtPathMedia = new TextField();

    private final P2Listener listenerDbStart;
    private final P2Listener listenerDbStop;

    private final boolean mediaDataExist;
    private final ProgData progData = ProgData.getInstance();

    ListChangeListener<HistoryData> listener;
    final MediaDataDto mediaDataDto;
    private final Stage stage;
    private final FilteredList<HistoryData> filteredList;
    private final SortedList<HistoryData> sortedList;
    private final RadioButton rbAll = new RadioButton("Alles");
    private final RadioButton rbDownload = new RadioButton("Downloads");
    private final RadioButton rbShown = new RadioButton("Gesehen");
    private final RadioButton rbOnlyShown = new RadioButton("Gesehen aber nicht gespeichert");
    private final RadioButton rbAllLists = new RadioButton("Alles");
    private final RadioButton rbFilm = new RadioButton("Filmliste");
    private final RadioButton rbAudio = new RadioButton("Audioliste");

    public PaneHistory(Stage stage, MediaDataDto mediaDataDto) {
        // nur im MediaDialog
        this.mediaDataDto = mediaDataDto;
        this.mediaDataExist = !mediaDataDto.searchTheme.isEmpty() || !mediaDataDto.searchTitle.isEmpty();
        this.paneHistoryDel = new PaneHistoryDel(progData, stage, tableHistory);

        listenerDbStart = new P2Listener(PEvents.EVENT_MEDIA_DB_START) {
            @Override
            public void pingGui() {
                // neue DB suchen
                txtSearch.setDisable(true);
            }
        };
        listenerDbStop = new P2Listener(PEvents.EVENT_MEDIA_DB_STOP) {
            @Override
            public void pingGui() {
                // neue DB liegt vor
                txtSearch.setDisable(false);
                filter();
            }
        };
        this.stage = stage;
        this.filteredList = new FilteredList<>(progData.historyListJson, p -> true);
        this.sortedList = new SortedList<>(filteredList);
    }

    public void close() {
        progData.pEventHandler.removeListener(listenerDbStart);
        progData.pEventHandler.removeListener(listenerDbStop);
        progData.historyListJson.removeListener(listener);
    }

    public void make() {
        initPanel();
        initCboAbo();
        initTable();
        initAction();
        initAccordion();
        filter();
    }

    private void initPanel() {
        txtTitleMedia.setEditable(false);
        txtPathMedia.setEditable(false);

        VBox vBoxMedia = new VBox(P2LibConst.PADDING);

        tableHistory.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        VBox.setVgrow(tableHistory, Priority.ALWAYS);
        vBoxMedia.getChildren().addAll(getVBoxSearch(), tableHistory, getTextFieldGrid(), accordion);

        this.setPadding(new Insets(P2LibConst.PADDING));
        this.setFitToHeight(true);
        this.setFitToWidth(true);
        this.setContent(vBoxMedia);
    }

    private void initCboAbo() {
        cboAbo.setItems(progData.historyListJson.getAboList());
        cboAbo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filter();
            mediaDataDto.searchStringProp.setValue(txtSearch.getText());
        });
    }

    private void initTable() {
        tableHistory.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableHistory.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableHistory.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableHistory.setEditable(true);

        final TableColumn<HistoryData, Boolean> audioColumn = new TableColumn<>("Liste");
        audioColumn.setCellValueFactory(new PropertyValueFactory<>("audio"));
        TableHistoryFactory.columnFactoryList(audioColumn);
        audioColumn.getStyleClass().add("alignCenter");

        final TableColumn<HistoryData, Integer> downloadColumn = new TableColumn<>("Downloads");
        downloadColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        downloadColumn.setCellFactory(new CellHistorySource<>(false).cellFactory);

        final TableColumn<HistoryData, Integer> shownColumn = new TableColumn<>("Gesehen");
        shownColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        shownColumn.setCellFactory(new CellHistorySource<>(true).cellFactory);

        final TableColumn<HistoryData, String> aboColumn = new TableColumn<>("Abo");
        aboColumn.setCellValueFactory(new PropertyValueFactory<>("abo"));

        final TableColumn<HistoryData, String> channelColumn = new TableColumn<>("Sender");
        channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));

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

        tableHistory.getColumns().addAll(audioColumn, downloadColumn, shownColumn, aboColumn,
                channelColumn, themeColumn, titleColumn, dateColumn, pathColumn);

        tableHistory.getSelectionModel().selectedItemProperty().addListener((observableValue, dataOld, dataNew) -> {
            setTableSel(dataNew);
        });
        tableHistory.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                ArrayList<HistoryData> historyDataArrayList = new ArrayList<>();
                HistoryData historyData = tableHistory.getSelectionModel().getSelectedItem();
                if (historyData == null) {
                    P2Alert.showInfoNoSelection();

                } else {
                    historyDataArrayList.add(historyData);
                    ContextMenu contextMenu =
                            new PaneHistoryContextMenu(stage, historyDataArrayList,
                                    mediaDataDto.whatToShow == MediaDataDto.SHOW_WHAT.SHOW_HISTORY).getContextMenu();
                    tableHistory.setContextMenu(contextMenu);
                }
            }
        });
        tableHistory.setRowFactory(new P2RowFactory<>(tv -> {
            TableRow<HistoryData> row = new TableRow<>();
            row.hoverProperty().addListener((observable) -> {
                final HistoryData historyData = row.getItem();
                if (row.isHover() && historyData != null) {
                    setTableSel(historyData);
                } else {
                    setTableSel(tableHistory.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        }));

        sortedList.comparatorProperty().bind(tableHistory.comparatorProperty());
        tableHistory.setItems(sortedList);
    }

    void initAction() {
        new FilterCheckRegEx(txtSearch);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filter();
            mediaDataDto.searchStringProp.setValue(txtSearch.getText());
        });
        txtSearch.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearch.getSelectedText();
                txtSearch.setText(sel);
            }
        });

        progData.pEventHandler.addListener(listenerDbStart);
        progData.pEventHandler.addListener(listenerDbStop);

        lblGesamtMedia.setText(progData.historyListJson.size() + "");
        listener = c -> Platform.runLater(() -> {
            lblGesamtMedia.setText(progData.historyListJson.size() + "");
            filter();
        });
        progData.historyListJson.addListener(listener);
    }

    // ==============================================
    private VBox getVBoxSearch() {
        ToggleGroup tgHistory = new ToggleGroup();
        rbAll.setToggleGroup(tgHistory);
        rbShown.setToggleGroup(tgHistory);
        rbOnlyShown.setToggleGroup(tgHistory);
        rbDownload.setToggleGroup(tgHistory);

        rbAll.setSelected(true);
        rbAll.selectedProperty().addListener((u, o, n) -> filter());
        rbShown.setOnAction(a -> filter());
        rbOnlyShown.setOnAction(a -> filter());
        rbDownload.setOnAction(a -> filter());

        ToggleGroup tgList = new ToggleGroup();
        rbAllLists.setToggleGroup(tgList);
        rbFilm.setToggleGroup(tgList);
        rbAudio.setToggleGroup(tgList);

        rbAllLists.setSelected(true);
        rbAllLists.selectedProperty().addListener((u, o, n) -> filter());
        rbFilm.setOnAction(a -> filter());
        rbAudio.setOnAction(a -> filter());

        // Suchen was
        final Button btnReset = new Button("");
        btnReset.setGraphic(P2IconFactory.P2ICON.BTN_ROTATE_3D.getFontIcon());
        btnReset.setTooltip(new Tooltip("Suchtext wieder herstellen"));
        btnReset.setOnAction(a ->
                txtSearch.setText(mediaDataDto.searchTheme + " " + mediaDataDto.searchTitle)
        );

        // ============
        GridPane searchGrid = getSearchGrid(mediaDataDto, txtSearch, btnReset, cboAbo);
        mediaDataDto.searchInWhat.addListener((u, o, n) -> filter());

        // ============
        VBox vBox = new VBox(P2LibConst.SPACING_VBOX);
        vBox.getChildren().addAll(P2Text.getLblTextBold("Suchen"),
                searchGrid);

        return vBox;
    }

    private GridPane getTextFieldGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        // Titel
        gridPane.add(new Label("Titel:"), 0, 0);
        gridPane.add(txtTitleMedia, 1, 0);

        // Pfad/URL
        gridPane.add(new Label("Url:"), 0, 1);
        gridPane.add(txtPathMedia, 1, 1);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow());
        return gridPane;
    }

    void filter(String searStr) {
        txtSearch.setText(searStr);
        filter();
    }

    private synchronized void filter() {
        Predicate<HistoryData> pred = historyData -> true;

        if (!txtSearch.getText().isEmpty()) {
            pred = pred.and(MediaSearchPredicateFactory.getPredicateHistoryData(
                    mediaDataDto.searchInWhat, txtSearch.getText()));
        }

        if (rbShown.isSelected()) {
            pred = pred.and(h ->
                    (h.getSource() == HistoryData.SOURCE_SHOWN_DOWNLOAD || h.getSource() == HistoryData.SOURCE_SHOWN));
        } else if (rbOnlyShown.isSelected()) {
            pred = pred.and(h ->
                    (h.getSource() == HistoryData.SOURCE_SHOWN));
        } else if (rbDownload.isSelected()) {
            pred = pred.and(h ->
                    (h.getSource() == HistoryData.SOURCE_SHOWN_DOWNLOAD || h.getSource() == HistoryData.SOURCE_DOWNLOAD));
        }

        if (rbFilm.isSelected()) {
            pred = pred.and(h -> (!h.isAudio()));
        } else if (rbAudio.isSelected()) {
            pred = pred.and(HistoryData::isAudio);
        }

        String abo = cboAbo.getSelectionModel().getSelectedItem();
        if (abo != null && !abo.isEmpty()) {
            pred = pred.and(h -> (h.getAbo().equals(abo)));
        }

        filteredList.setPredicate(pred);
        lblHits.setText(filteredList.size() + "");
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

    private int getSource() {
        int source = HistoryData.SOURCE_SHOWN_DOWNLOAD;
        if (rbShown.isSelected()) {
            source = HistoryData.SOURCE_SHOWN;
        }
        if (rbDownload.isSelected()) {
            source = HistoryData.SOURCE_DOWNLOAD;
        }
        return source;
    }

    private void initAccordion() {
        tpDel.setText("Löschen");
        tpDel.setContent(paneHistoryDel);
        accordion.getPanes().addAll(tpDel);
    }

    private GridPane getSearchGrid(MediaDataDto mediaDataDto, TextField txtSearch, Button btnReset,
                                   ComboBox<String> comboBox) {
        final boolean mediaDataExist = !mediaDataDto.searchTheme.isEmpty() || !mediaDataDto.searchTitle.isEmpty();

        // ============
        HBox hBoxRadioList = new HBox(P2LibConst.PADDING_HBOX);
        hBoxRadioList.setAlignment(Pos.CENTER_LEFT);
        hBoxRadioList.getChildren().addAll(rbAllLists, rbFilm, rbAudio);

        // ============
        HBox hBoxRadioHistory = new HBox(P2LibConst.PADDING_HBOX);
        hBoxRadioHistory.getChildren().addAll(rbAll, rbDownload, rbShown, rbOnlyShown, P2GuiTools.getHBoxGrower(),
                lblHits, new Label(" von: "), lblGesamtMedia);

        final Button btnClearFilter = new Button();
        btnClearFilter.setGraphic(P2IconFactory.P2ICON.BTN_CLEAR.getFontIcon());
        btnClearFilter.setTooltip(new Tooltip("Den Filter löschen"));
        btnClearFilter.setOnAction(a -> {
            txtSearch.clear();
            comboBox.getSelectionModel().clearSelection();
            rbAll.setSelected(true);
            rbAllLists.setSelected(true);
        });

        Button btnChange = new Button();
        btnChange.setTooltip(new Tooltip("Einstellung wo gesucht wird"));
        btnChange.setGraphic(P2IconFactory.P2ICON.BTN_ROTATE_3D.getFontIcon());
        btnChange.setOnAction(a -> {
            if (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_THEME_OR_PATH) {
                mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_TITEL_OR_NAME);
            } else if (mediaDataDto.searchInWhat.getValue() == ProgConst.MEDIA_SEARCH_TITEL_OR_NAME) {
                mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_TT_OR_PN);
            } else {
                mediaDataDto.searchInWhat.setValue(ProgConst.MEDIA_SEARCH_THEME_OR_PATH);
            }
        });

        Label lblText = new Label(getTextSearchInWhat(mediaDataDto));
        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER);
        mediaDataDto.searchInWhat.addListener((u, o, n) ->
                lblText.setText(getTextSearchInWhat(mediaDataDto)));

        if (mediaDataExist) {
            hBox.getChildren().addAll(lblText, txtSearch, btnReset, btnChange, btnClearFilter);
        } else {
            // wenns keine MediaData gibt, dann brauchts den Reset auch nicht
            hBox.getChildren().addAll(lblText, txtSearch, btnChange, btnClearFilter);
        }

        comboBox.setMaxWidth(Double.MAX_VALUE);
        GridPane gridPane = new GridPane(P2LibConst.DIST_GRIDPANE_HGAP, P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSizeLeft(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        gridPane.add(lblText, 0, 0);
        gridPane.add(txtSearch, 1, 0);
        gridPane.add(hBox, 2, 0);
        gridPane.add(new Label("Abos:"), 0, 1);
        gridPane.add(comboBox, 1, 1);

        gridPane.add(hBoxRadioList, 1, 2, 2, 1);
        gridPane.add(hBoxRadioHistory, 1, 3, 2, 1);

        return gridPane;
    }

    private String getTextSearchInWhat(MediaDataDto mediaDataDto) {
        switch (mediaDataDto.searchInWhat.getValue()) {
            case ProgConst.MEDIA_SEARCH_THEME_OR_PATH:
                return "Thema:";
            case ProgConst.MEDIA_SEARCH_TITEL_OR_NAME:
                return "Titel:";
            default:
                return "Thema oder Titel:";
        }
    }
}
