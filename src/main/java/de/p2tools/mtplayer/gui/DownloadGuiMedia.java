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
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PGuiTools;
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
    private final Button btnMenu = new Button();
    private final Button btnDialogMedia = new Button();
    private final Button btnClear = new Button();
    private DownloadData downloadData = null;
    private final RadioButton rbTheme = new RadioButton("Thema");
    private final RadioButton rbTitle = new RadioButton("Titel");
    private final RadioButton rbTt = new RadioButton("Thema oder Titel");
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
        hBoxButton.getChildren().addAll(btnMenu, btnDialogMedia, btnClear, btnHelpMedia);
        hBoxTop.getChildren().addAll(txtSearchMedia, hBoxButton, txtSearchAbo);
        HBox.setHgrow(txtSearchMedia, Priority.ALWAYS);
        HBox.setHgrow(txtSearchAbo, Priority.ALWAYS);


        HBox hBox = new HBox(P2LibConst.DIST_EDGE);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(P2LibConst.DIST_BUTTON));
        Text text = new Text("Mediensammlung, suchen im Dateinamen");
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
        hBox.getChildren().addAll(text, PGuiTools.getHBoxGrower(), lblSumMedia);
        VBox vLeft = new VBox(0);
        vLeft.setPadding(new Insets(0));
        vLeft.getChildren().addAll(hBox, tableMedia);

        hBox = new HBox(P2LibConst.DIST_EDGE);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(P2LibConst.DIST_BUTTON));
        text = new Text("Abos, suchen im:");
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
        hBox.getChildren().addAll(text, rbTheme, rbTitle, rbTt, PGuiTools.getHBoxGrower(), lblSumAbo);
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
        btnMenu.setTooltip(new Tooltip("Einstellungen anzeigen:\n" +
                " -> rechte Maustaste: Mediensammlung voreingestellt,\n" +
                " -> linke Maustaste: Abos voreingestellt"));
        btnMenu.setGraphic(ProgIcons.Icons.ICON_BUTTON_EDIT.getImageView());
        btnMenu.setOnAction(a -> {
            new MediaCleaningDialogController(true);
        });
        btnMenu.setOnAction(a -> {
            new MediaCleaningDialogController(true);
            getSearchString(downloadData, true);
            getSearchString(downloadData, false);
        });
        btnMenu.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                new MediaCleaningDialogController(false);
                getSearchString(downloadData, true);
                getSearchString(downloadData, false);
            }
        });

        btnDialogMedia.setTooltip(new Tooltip("Dialog Mediensammlung öffnen:\n" +
                " -> rechte Maustaste: Mediensammlung voreingestellt,\n" +
                " -> linke Maustaste: Abos voreingestellt"));
        btnDialogMedia.setGraphic(ProgIcons.Icons.ICON_BUTTON_MENU.getImageView());
        btnDialogMedia.setOnAction(a -> new MediaDialogController(
                downloadData == null ? "" : downloadData.getTheme(),
                downloadData == null ? "" : downloadData.getTitle(), true));
        btnDialogMedia.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                new MediaDialogController(
                        downloadData == null ? "" : downloadData.getTheme(),
                        downloadData == null ? "" : downloadData.getTitle(), false);
            }
        });

        btnClear.setTooltip(new Tooltip("Die Suchfelder löschen"));
        btnClear.setGraphic(ProgIcons.Icons.ICON_BUTTON_STOP.getImageView());
        btnClear.setOnAction(a -> {
            txtSearchMedia.clear();
            txtSearchAbo.clear();
        });
    }

    private void initSearch() {
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
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(rbTheme, rbTitle, rbTt);
        rbTheme.selectedProperty().addListener((o, ol, ne) -> radioChanged());
        rbTitle.selectedProperty().addListener((o, ol, ne) -> radioChanged());
        rbTt.selectedProperty().addListener((o, ol, ne) -> radioChanged());
        switch (ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.get()) {
            case ProgConst.MEDIA_COLLECTION_SEARCH_IN_THEME:
                rbTheme.setSelected(true);
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL:
                rbTitle.setSelected(true);
                break;
            case ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT:
            default:
                rbTt.setSelected(true);
                break;
        }
    }

    private void filter(boolean media) {
        if (media) {
            progData.mediaDataList.filteredListSetPredicate(
                    MediaSearchPredicateFactory.getPredicateMediaData(txtSearchMedia.getText(), true));
            lblSumMedia.setText(progData.mediaDataList.getFilteredList().size() + "");
        } else {
            progData.erledigteAbos.filteredListSetPredicate(
                    MediaSearchPredicateFactory.getPredicateHistoryData(txtSearchAbo.getText(),
                            rbTheme.isSelected() ? ProgConst.MEDIA_COLLECTION_SEARCH_IN_THEME :
                                    (rbTitle.isSelected() ? ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL : ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT)));
            lblSumAbo.setText(progData.erledigteAbos.getFilteredList().size() + "");
        }
    }

    void radioChanged() {
        if (rbTheme.isSelected()) {
            ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_THEME);
        } else if (rbTitle.isSelected()) {
            ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TITEL);
        } else {
            ProgConfig.DOWNLOAD_GUI_MEDIA_SEARCH_IN_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_IN_TT);
        }
        filter(false);
    }

    private void initTableMedia() {
        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Dateiname");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.getStyleClass().add("special-column-style");

        tableMedia.getColumns().addAll(nameColumn);
        tableMedia.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        SortedList<MediaData> sortedList = progData.mediaDataList.getSortedList();
        sortedList.comparatorProperty().bind(tableMedia.comparatorProperty());
        tableMedia.setItems(sortedList);
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
