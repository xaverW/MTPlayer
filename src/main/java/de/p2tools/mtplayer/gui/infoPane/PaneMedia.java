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
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.controller.mediadb.MediaCleaningFactory;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.mtplayer.controller.mediadb.MediaFileSize;
import de.p2tools.mtplayer.controller.mediadb.MediaSearchPredicateFactory;
import de.p2tools.mtplayer.gui.mediacleaning.MediaCleaningDialogController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.mediadialog.PaneMediaContextMenu;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.PButton;
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
    private boolean searchIsNull = true;
    private String searchTheme = "";
    private String searchTitle = "";

    private final Label lblSumMedia = new Label();
    private final Label lblSumAbo = new Label();

    private final ProgData progData;

    public PaneMedia() {
        progData = ProgData.getInstance();
        init();
        initMenu(true);
        initMenu(false);
        initTableMedia();
        initTableAbo();
        initSearch();
    }

    public void setSearchPredicate(FilmDataMTP filmDataMTP) {
        if (!this.isVisible()) {
            return;
        }

        this.searchIsNull = filmDataMTP == null;
        this.searchTheme = searchIsNull ? "" : filmDataMTP.getTheme();
        this.searchTitle = searchIsNull ? "" : filmDataMTP.getTitle();
        setSearchString(true);
        setSearchString(false);
    }

    public void setSearchPredicate(DownloadData downloadData) {
        if (!this.isVisible()) {
            return;
        }

        this.searchIsNull = downloadData == null;
        this.searchTheme = searchIsNull ? "" : downloadData.getTheme();
        this.searchTitle = searchIsNull ? "" : downloadData.getTitle();
        setSearchString(true);
        setSearchString(false);
    }

    private void setSearchString(boolean media) {
        if (searchIsNull) {
            return;
        }

        if (media) {
            txtSearchMedia.setText(MediaCleaningFactory.cleanSearchText(searchTheme, searchTitle, media));
        } else {
            txtSearchAbo.setText(MediaCleaningFactory.cleanSearchText(searchTheme, searchTitle, media));
        }
    }

    private void init() {
        final Button btnHelpMedia = PButton.helpButton("Mediensammlung", HelpText.DOWNLOAD_GUI_MEDIA);
        HBox hBoxTop = new HBox(P2LibConst.DIST_EDGE);
        hBoxTop.setPadding(new Insets(0));
        hBoxTop.setAlignment(Pos.CENTER);

        HBox hBoxButton = new HBox(P2LibConst.DIST_BUTTON);
        hBoxButton.getChildren().addAll(btnConfig, btnClear, btnHelpMedia, P2GuiTools.getVDistance(5), btnDialogMedia);
        hBoxTop.getChildren().addAll(txtSearchMedia, hBoxButton, txtSearchAbo);
        HBox.setHgrow(txtSearchMedia, Priority.ALWAYS);
        HBox.setHgrow(txtSearchAbo, Priority.ALWAYS);

        // Suchen in den Medien
        VBox vLeft = MediaSearchFactory.getSearchMedia(lblSumMedia, true);
        vLeft.getChildren().add(tableMedia);
        VBox vRight = MediaSearchFactory.getSearchAbo(lblSumAbo, true, true);
        vRight.getChildren().add(tableAbo);

        ProgConfig.GUI_MEDIA_SEARCH_IN_MEDIA.addListener((u, o, n) -> filter(true));
        ProgConfig.GUI_MEDIA_SEARCH_IN_ABO.addListener((u, o, n) -> filter(false));
        ProgConfig.GUI_MEDIA_BUILD_SEARCH_MEDIA.addListener((u, o, n) -> setSearchString(true));
        ProgConfig.GUI_MEDIA_BUILD_SEARCH_ABO.addListener((u, o, n) -> setSearchString(false));

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
                " -> linke Maustaste: Mediensammlung voreingestellt,\n" +
                " -> rechte Maustaste: Abos voreingestellt"));
        btnConfig.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_EDIT.getImageView());
        btnConfig.setOnAction(a -> {
            new MediaCleaningDialogController(true);
            setSearchString(true);
            setSearchString(false);
        });
        btnConfig.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                new MediaCleaningDialogController(false);
                setSearchString(true);
                setSearchString(false);
            }
        });

        btnDialogMedia.setTooltip(new Tooltip("Dialog Mediensammlung öffnen:\n" +
                " -> linke Maustaste: Mediensammlung voreingestellt,\n" +
                " -> rechte Maustaste: Abos voreingestellt"));
        btnDialogMedia.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_MENU.getImageView());
        btnDialogMedia.setOnAction(a -> {
            new MediaDialogController(
                    searchTheme,
                    searchIsNull ? txtSearchMedia.getText() : searchTitle, true);
            setSearchString(true);
            setSearchString(false);
            filter(true);// wegen Dialog, wenn vorher schon leer
            filter(false);
        });
        btnDialogMedia.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                new MediaDialogController(
                        searchTheme,
                        searchIsNull ? txtSearchAbo.getText() : searchTitle, false);
                setSearchString(true);
                setSearchString(false);
                filter(true);// wegen Dialog, wenn vorher schon leer
                filter(false);
            }
        });

        btnClear.setTooltip(new Tooltip("Die Suchfelder löschen"));
        btnClear.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_STOP.getImageView());
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

    void filter(boolean media) {
        if (media) {
            progData.mediaDataList.filteredListSetPredicate(
                    MediaSearchPredicateFactory.getPredicateMediaData(txtSearchMedia.getText()));
        } else {
            progData.historyListAbos.filteredListSetPredicate(
                    MediaSearchPredicateFactory.getPredicateHistoryData(txtSearchAbo.getText()));
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
        tableAbo.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        SortedList<HistoryData> sortedList = progData.historyListAbos.getSortedList();
        sortedList.comparatorProperty().bind(tableAbo.comparatorProperty());
        tableAbo.setItems(sortedList);
    }
}
