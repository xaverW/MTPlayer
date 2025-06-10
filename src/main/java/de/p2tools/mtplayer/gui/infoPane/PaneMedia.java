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

package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.cleaningdata.CleaningMediaFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.history.HistoryData;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.mtplayer.controller.mediadb.MediaFileSize;
import de.p2tools.mtplayer.controller.mediadb.MediaSearchPredicateFactory;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.mediaSearch.MediaSearchFactory;
import de.p2tools.mtplayer.gui.mediacleaningdialog.MediaCleaningDialogController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.mediadialog.PaneMediaContextMenu;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
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

import java.util.Date;

public class PaneMedia extends VBox {

    private final TableView<MediaData> tableMedia = new TableView<>();
    private final TableView<HistoryData> tableAbo = new TableView<>();
    private final TextField txtSearchMedia = new TextField();
    private final TextField txtSearchAbo = new TextField();
    private final Button btnConfig = new Button();
    private final Button btnDialogMedia = new Button();
    private final Button btnClear = new Button();

    private final Label lblSumMedia = new Label();
    private final Label lblSumAbo = new Label();
    private final MediaDataDto mediaDataDtoMedia;
    private final MediaDataDto mediaDataDtoAbo;

    private final ProgData progData;

    public PaneMedia(MediaDataDto mediaDataDtoMedia, MediaDataDto mediaDataDtoAbo) {
        // sind die Media im Infobereich unter "Filme" und "Download", wird also 2x verwendet
        progData = ProgData.getInstance();
        VBox.setVgrow(this, Priority.ALWAYS);

        this.mediaDataDtoMedia = mediaDataDtoMedia;
        this.mediaDataDtoAbo = mediaDataDtoAbo;
        init();
        initMenu();
        initTableMedia();
        initTableAbo();
        initSearch();
    }

    public void setSearchPredicate(FilmData filmDataMTP) {
        mediaDataDtoMedia.searchTheme = filmDataMTP == null ? "" : filmDataMTP.getTheme().trim();
        mediaDataDtoMedia.searchTitle = filmDataMTP == null ? "" : filmDataMTP.getTitle().trim();
        mediaDataDtoAbo.searchTheme = filmDataMTP == null ? "" : filmDataMTP.getTheme().trim();
        mediaDataDtoAbo.searchTitle = filmDataMTP == null ? "" : filmDataMTP.getTitle().trim();
        setSearchStringMedia();
        setSearchStringAbo();
    }

    public void setSearchPredicate(DownloadData downloadData) {
        mediaDataDtoMedia.searchTheme = downloadData == null ? "" : downloadData.getTheme().trim();
        mediaDataDtoMedia.searchTitle = downloadData == null ? "" : downloadData.getTitle().trim();
        mediaDataDtoAbo.searchTheme = downloadData == null ? "" : downloadData.getTheme().trim();
        mediaDataDtoAbo.searchTitle = downloadData == null ? "" : downloadData.getTitle().trim();
        setSearchStringMedia();
        setSearchStringAbo();
    }

    private void setSearchStringMedia() {
        if (mediaDataDtoMedia.searchTheme.isEmpty() && mediaDataDtoMedia.searchTitle.isEmpty()) {
            return;
        }
        txtSearchMedia.setText(CleaningMediaFactory.cleanSearchText(mediaDataDtoMedia));
    }

    private void setSearchStringAbo() {
        if (mediaDataDtoAbo.searchTheme.isEmpty() && mediaDataDtoAbo.searchTitle.isEmpty()) {
            return;
        }
        txtSearchAbo.setText(CleaningMediaFactory.cleanSearchText(mediaDataDtoAbo));
    }

    private void init() {
        final Button btnHelpMedia = P2Button.helpButton("Mediensammlung", HelpText.DOWNLOAD_GUI_MEDIA);
        HBox hBoxTop = new HBox(P2LibConst.PADDING);
        hBoxTop.setPadding(new Insets(0));
        hBoxTop.setAlignment(Pos.CENTER);

        HBox hBoxButton = new HBox(P2LibConst.DIST_BUTTON);
        hBoxButton.getChildren().addAll(btnConfig, btnClear, btnHelpMedia, P2GuiTools.getVDistance(5), btnDialogMedia);
        hBoxTop.getChildren().addAll(txtSearchMedia, hBoxButton, txtSearchAbo);
        HBox.setHgrow(txtSearchMedia, Priority.ALWAYS);
        HBox.setHgrow(txtSearchAbo, Priority.ALWAYS);

        // Suchen in den Medien
        VBox vLeft = MediaSearchFactory.getSearchVbox(mediaDataDtoMedia, lblSumMedia, true);
        vLeft.getChildren().add(tableMedia);
        VBox vRight = MediaSearchFactory.getSearchVbox(mediaDataDtoAbo, lblSumAbo, true);
        vRight.getChildren().add(tableAbo);

        mediaDataDtoMedia.searchInWhat.addListener((u, o, n) -> filter(mediaDataDtoMedia));
        mediaDataDtoAbo.searchInWhat.addListener((u, o, n) -> filter(mediaDataDtoAbo));
        mediaDataDtoMedia.buildSearchFrom.addListener((u, o, n) -> {
            setSearchStringMedia();
            filter(mediaDataDtoMedia);
        });
        mediaDataDtoAbo.buildSearchFrom.addListener((u, o, n) -> {
            setSearchStringAbo();
            filter(mediaDataDtoAbo);
        });

        tableMedia.setStyle("-fx-border-width: 1px; -fx-border-color: -text-color-blue;");
        tableAbo.setStyle("-fx-border-width: 1px; -fx-border-color: -text-color-blue;");
        VBox.setVgrow(tableMedia, Priority.ALWAYS);
        VBox.setVgrow(tableAbo, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane();
        splitPane.setPadding(new Insets(0));
        splitPane.getItems().addAll(vLeft, vRight);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_MEDIA_DIVIDER);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        setSpacing(P2LibConst.DIST_BUTTON);
        setPadding(new Insets(P2LibConst.PADDING));
        getChildren().addAll(hBoxTop, splitPane);
    }

    private void initMenu() {
        btnConfig.setTooltip(new Tooltip("Einstellungen anzeigen"));
        btnConfig.setGraphic(ProgIcons.ICON_BUTTON_EDIT.getImageView());
        btnConfig.setOnAction(a -> {
            new MediaCleaningDialogController(mediaDataDtoMedia, mediaDataDtoAbo);
            setSearchStringMedia();
            setSearchStringAbo();
        });

        btnDialogMedia.setTooltip(new Tooltip("Dialog Mediensammlung öffnen"));
        btnDialogMedia.setGraphic(ProgIcons.ICON_TOOLBAR_MENU.getImageView());
        btnDialogMedia.setOnAction(a -> {
            new MediaDialogController(mediaDataDtoMedia);
            setSearchStringMedia();
            setSearchStringAbo();
            filter(mediaDataDtoMedia);// wegen Dialog, wenn vorher schon leer
            filter(mediaDataDtoAbo);
        });

        btnClear.setTooltip(new Tooltip("Die Suchfelder löschen"));
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClear.setOnAction(a -> {
            txtSearchMedia.clear();
            txtSearchAbo.clear();
        });
    }

    private void initSearch() {
        lblSumMedia.setText(progData.mediaDataList.getFilteredList().size() + "");
        lblSumAbo.setText(progData.historyListAbos.getFilteredList().size() + "");

        progData.mediaDataList.getFilteredList().addListener((ListChangeListener<MediaData>) c ->
                Platform.runLater(() -> lblSumMedia.setText(progData.mediaDataList.getFilteredList().size() + "")));
        progData.historyListAbos.getFilteredList().addListener((ListChangeListener<HistoryData>) c ->
                Platform.runLater(() -> lblSumAbo.setText(progData.historyListAbos.getFilteredList().size() + "")));

        txtSearchMedia.textProperty().addListener((u, o, n) -> {
            filter(mediaDataDtoMedia);
        });
        txtSearchMedia.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearchMedia.getSelectedText();
                txtSearchMedia.setText(sel);
            }
        });
        txtSearchAbo.textProperty().addListener((u, o, n) -> {
            filter(mediaDataDtoAbo);
        });
        txtSearchAbo.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearchAbo.getSelectedText();
                txtSearchAbo.setText(sel);
            }
        });
    }

    void filter(MediaDataDto mediaDataDto) {
        if (mediaDataDto.whatToShow == MediaDataDto.SHOW_WHAT.SHOW_MEDIA) {
            progData.mediaDataList.filteredListSetPredicate(
                    MediaSearchPredicateFactory.getPredicateMediaData(
                            mediaDataDtoMedia.searchInWhat, txtSearchMedia.getText()));
        } else {
            progData.historyListAbos.filteredListSetPredicate(
                    MediaSearchPredicateFactory.getPredicateHistoryData(
                            mediaDataDtoAbo.searchInWhat, txtSearchAbo.getText()));
        }
    }

    private void initTableMedia() {
        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Dateiname");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.getStyleClass().add("special-column-style");
        nameColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(45.0 / 100));

        final TableColumn<MediaData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        pathColumn.getStyleClass().add("special-column-style");
        pathColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(35.0 / 100));

        final TableColumn<MediaData, MediaFileSize> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(15.0 / 100));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeColumn.getStyleClass().add("alignCenterRightPadding_25");

        tableMedia.getColumns().addAll(nameColumn, pathColumn, sizeColumn);
        tableMedia.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

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
                    P2Alert.showInfoNoSelection();

                } else {
                    ContextMenu contextMenu = new PaneMediaContextMenu(progData.primaryStage, mediaData).getContextMenu();
                    tableMedia.setContextMenu(contextMenu);
                }
            }
        });
    }

    private void initTableAbo() {
        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(35.0 / 100));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("special-column-style");

        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(45.0 / 100));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("special-column-style");

        final TableColumn<HistoryData, Date> dateColumn = new TableColumn<>("Datum");
        dateColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(15.0 / 100));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        dateColumn.getStyleClass().add("alignCenter");

        tableAbo.getColumns().addAll(themeColumn, titleColumn, dateColumn);
        tableAbo.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        SortedList<HistoryData> sortedList = progData.historyListAbos.getSortedList();
        sortedList.comparatorProperty().bind(tableAbo.comparatorProperty());
        tableAbo.setItems(sortedList);
    }
}
