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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.controller.mediadb.MediaCleaningFactory;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.mtplayer.controller.mediadb.MediaSearchPredicateFactory;
import de.p2tools.mtplayer.gui.mediacleaning.MediaCleaningDialogController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.mediadialog.PaneMediaContextMenu;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PGuiTools;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Date;

public class DownloadGuiMedia extends VBox {

    private TableView<MediaData> tableMedia = new TableView();
    private TableView<HistoryData> tableAbo = new TableView();
    private TextField txtSearchMedia = new TextField();
    private TextField txtSearchAbo = new TextField();
    private final Button btnConfig = new Button();
    private final Button btnDialogMedia = new Button();
    private final Button btnClear = new Button();
    private DownloadData downloadData = null;
    private final Label lblSumMedia = new Label();
    private final Label lblSumAbo = new Label();

    private final ProgData progData;

    public DownloadGuiMedia() {
        progData = ProgData.getInstance();
        init();
        initMenu(true);
        initMenu(false);
        initTableMedia();
        initTableAbo();
        initSearch();
        this.visibleProperty().addListener((u, o, n) -> {
            if (this.isVisible()) {
                System.out.println("==>DownloadGuiMedia visible");
            }
        });
    }

    public void setSearchPredicate(DownloadData downloadData) {
        if (!this.isVisible()) {
            return;
        }

        this.downloadData = downloadData;
        getSearchString(downloadData, true);
        getSearchString(downloadData, false);
    }

    private void getSearchString(DownloadData downloadData, boolean media) {
        String searchString = "";
        if (downloadData == null) {
            return;
        }

        searchString = MediaCleaningFactory.cleanSearchText(downloadData, media);
        if (media) {
            txtSearchMedia.setText(searchString);
        } else {
            txtSearchAbo.setText(searchString);
        }
    }

    private void init() {
        final Button btnHelpMedia = PButton.helpButton("Mediensammlung", HelpText.DOWNLOAD_GUI_MEDIA);
        HBox hBoxTop = new HBox(P2LibConst.DIST_EDGE);
        hBoxTop.setPadding(new Insets(0));
        hBoxTop.setAlignment(Pos.CENTER);

        HBox hBoxButton = new HBox(P2LibConst.DIST_BUTTON);
        hBoxButton.getChildren().addAll(btnConfig, btnClear, btnHelpMedia, PGuiTools.getVDistance(5), btnDialogMedia);
        hBoxTop.getChildren().addAll(txtSearchMedia, hBoxButton, txtSearchAbo);
        HBox.setHgrow(txtSearchMedia, Priority.ALWAYS);
        HBox.setHgrow(txtSearchAbo, Priority.ALWAYS);


        HBox hBox = new HBox(P2LibConst.DIST_EDGE);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(P2LibConst.DIST_BUTTON));
        final Text text1 = new Text("Mediensammlung, suchen im: " +
                (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_MEDIA.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL ?
                        "Dateinamen" : "Pfad und Dateinamen"));
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));
        text1.getStyleClass().add("downloadGuiMediaText");
        ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_MEDIA.addListener((u, o, n) -> text1.setText("Mediensammlung, suchen im: " +
                (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_MEDIA.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL ?
                        "Dateinamen" : "Pfad und Dateinamen")));
        Button btnChangeMedia = new Button();
        btnChangeMedia.getStyleClass().add("buttonVerySmall");
        btnChangeMedia.setTooltip(new Tooltip("Einstellung wo gesucht wird, ändern"));
        btnChangeMedia.setGraphic(ProgIcons.Icons.ICON_BUTTON_CHANGE.getImageView());
        btnChangeMedia.setOnAction(a -> {
            if (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_MEDIA.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL) {
                ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT);
            } else {
                ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_MEDIA.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL);
            }
            filter(true);
        });

        hBox.getChildren().addAll(text1, PGuiTools.getHBoxGrower(), lblSumMedia, btnChangeMedia);

        VBox vLeft = new VBox(0);
        vLeft.setPadding(new Insets(0));
        vLeft.getChildren().addAll(hBox, tableMedia);

        hBox = new HBox(P2LibConst.DIST_EDGE);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(P2LibConst.DIST_BUTTON));
        Text text2 = new Text("Abos, suchen im: " +
                (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL ?
                        "Titel des Abos" : "Thema oder Titel des Abos"));
        text2.setFont(Font.font(null, FontWeight.BOLD, -1));
        text2.getStyleClass().add("downloadGuiMediaText");
        ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.addListener((u, o, n) -> text2.setText("Abos, suchen im: " +
                (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL ?
                        "Titel des Abos" : "Thema oder Titel des Abos")));

        Button btnChangeAbo = new Button();
        btnChangeAbo.getStyleClass().add("buttonVerySmall");
        btnChangeAbo.setTooltip(new Tooltip("Einstellung wo gesucht wird, ändern"));
        btnChangeAbo.setGraphic(ProgIcons.Icons.ICON_BUTTON_CHANGE.getImageView());
        btnChangeAbo.setOnAction(a -> {
            if (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.getValue() == ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL) {
                ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT);
            } else {
                ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL);
            }
            filter(false);
        });

        hBox.getChildren().addAll(text2, PGuiTools.getHBoxGrower(), lblSumAbo, btnChangeAbo);

        VBox vRight = new VBox(0);
        vRight.setPadding(new Insets(0));
        vRight.getChildren().addAll(hBox, tableAbo);

        tableMedia.setStyle("-fx-border-width: 1px;");
        tableMedia.setStyle("-fx-border-color: -text-color-blue;");
        tableAbo.setStyle("-fx-border-width: 1px;");
        tableAbo.setStyle("-fx-border-color: -text-color-blue;");

        SplitPane splitPane = new SplitPane();
        splitPane.setPadding(new Insets(0));
        splitPane.getItems().addAll(vLeft, vRight);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_MEDIA_DIVIDER);

        super.setSpacing(P2LibConst.DIST_BUTTON);
        super.setPadding(new Insets(P2LibConst.DIST_EDGE));
        super.getChildren().addAll(hBoxTop, splitPane);
    }

    private void initMenu(boolean media) {
        btnConfig.setTooltip(new Tooltip("Einstellungen anzeigen:\n" +
                " -> rechte Maustaste: Mediensammlung voreingestellt,\n" +
                " -> linke Maustaste: Abos voreingestellt"));
        btnConfig.setGraphic(ProgIcons.Icons.ICON_BUTTON_EDIT.getImageView());
        btnConfig.setOnAction(a -> {
            new MediaCleaningDialogController(true);
            getSearchString(downloadData, true);
            getSearchString(downloadData, false);
            filter(true);
            filter(false);
        });
        btnConfig.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                new MediaCleaningDialogController(false);
                getSearchString(downloadData, true);
                getSearchString(downloadData, false);
                filter(true);
                filter(false);
            }
        });

        btnDialogMedia.setTooltip(new Tooltip("Dialog Mediensammlung öffnen:\n" +
                " -> rechte Maustaste: Mediensammlung voreingestellt,\n" +
                " -> linke Maustaste: Abos voreingestellt"));
        btnDialogMedia.setGraphic(ProgIcons.Icons.ICON_BUTTON_MENU.getImageView());
        btnDialogMedia.setOnAction(a -> {
            new MediaDialogController(
                    downloadData == null ? "" : downloadData.getTheme(),
                    downloadData == null ? txtSearchMedia.getText() : downloadData.getTitle(), true);
            filter(true);
            filter(false);
        });
        btnDialogMedia.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                new MediaDialogController(
                        downloadData == null ? "" : downloadData.getTheme(),
                        downloadData == null ? txtSearchAbo.getText() : downloadData.getTitle(), false);
            }
            filter(true);
            filter(false);
        });

        btnClear.setTooltip(new Tooltip("Die Suchfelder löschen"));
        btnClear.setGraphic(ProgIcons.Icons.ICON_BUTTON_STOP.getImageView());
        btnClear.setOnAction(a -> {
            txtSearchMedia.clear();
            txtSearchAbo.clear();
        });
    }

    private void initSearch() {
        lblSumMedia.setText(progData.mediaDataList.getFilteredList().size() + "");
        lblSumAbo.setText(progData.erledigteAbos.getFilteredList().size() + "");
        progData.mediaDataList.getFilteredList().addListener((ListChangeListener<MediaData>) c ->
                Platform.runLater(() -> lblSumMedia.setText(progData.mediaDataList.getFilteredList().size() + "")));
        progData.erledigteAbos.getFilteredList().addListener((ListChangeListener<HistoryData>) c ->
                Platform.runLater(() -> lblSumAbo.setText(progData.erledigteAbos.getFilteredList().size() + "")));

        txtSearchMedia.textProperty().addListener((u, o, n) -> {
            filter(true);
        });
        txtSearchMedia.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearchMedia.getSelectedText();
                txtSearchMedia.setText(sel);
            }
        });
        txtSearchAbo.textProperty().addListener((u, o, n) -> {
            filter(false);
        });
        txtSearchAbo.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearchAbo.getSelectedText();
                txtSearchAbo.setText(sel);
            }
        });
    }

    private void filter(boolean media) {
        if (media) {
            progData.mediaDataList.filteredListSetPredicate(
                    MediaSearchPredicateFactory.getPredicateMediaData(txtSearchMedia.getText()));
        } else {
            progData.erledigteAbos.filteredListSetPredicate(
                    MediaSearchPredicateFactory.getPredicateHistoryData(txtSearchAbo.getText()));
        }
    }

    private void initTableMedia() {
        final TableColumn<MediaData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        pathColumn.getStyleClass().add("special-column-style");

        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Dateiname");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.getStyleClass().add("special-column-style");

        tableMedia.getColumns().addAll(nameColumn, pathColumn);
        tableMedia.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        SortedList<MediaData> sortedList = progData.mediaDataList.getSortedList();
        sortedList.comparatorProperty().bind(tableMedia.comparatorProperty());
        tableMedia.setItems(sortedList);

        tableMedia.setOnMousePressed(m -> {
            if (tableMedia.getItems().isEmpty()) {
                return;
            }
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                MediaData mediaData = tableMedia.getSelectionModel().getSelectedItem();
                if (mediaData == null) {
                    PAlert.showInfoNoSelection();

                } else {
                    ContextMenu contextMenu = new PaneMediaContextMenu(progData.primaryStage, mediaData).getContextMenu();
                    tableMedia.setContextMenu(contextMenu);
                }
            }
        });
    }

    private void initTableAbo() {
        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(45.0 / 100));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("special-column-style");

        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(45.0 / 100));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("special-column-style");

        final TableColumn<HistoryData, Date> dateColumn = new TableColumn<>("Datum");
        dateColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(10.0 / 100));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        dateColumn.getStyleClass().add("alignCenter");

        tableAbo.getColumns().addAll(themeColumn, titleColumn, dateColumn);
        tableAbo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        SortedList<HistoryData> sortedList = progData.erledigteAbos.getSortedList();
        sortedList.comparatorProperty().bind(tableAbo.comparatorProperty());
        tableAbo.setItems(sortedList);
    }
}
