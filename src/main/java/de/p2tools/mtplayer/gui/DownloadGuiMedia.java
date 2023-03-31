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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.mtplayer.controller.mediadb.MediaFactory;
import de.p2tools.mtplayer.gui.mediaconfig.SearchPredicateWorker;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PButton;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Date;

public class DownloadGuiMedia extends HBox {

    public final static int SHOW_TITEL = 0;
    public final static int SHOW_THEME = 1;
    public final static int SHOW_TT = 2;

    private TableView<MediaData> tableMedia = new TableView();
    private TableView<HistoryData> tableAbo = new TableView();
    private TextField txtSearchMedia = new TextField();
    private TextField txtSearchAbo = new TextField();
    private final MenuButton menuMedia = new MenuButton();
    private final MenuButton menuAbo = new MenuButton();
    private DownloadData downloadData = null;

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
                System.out.println("==> visible");
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

        //Media
        if (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_ABO.getValue()) {
            switch (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.getValue()) {
                case SHOW_TITEL:
                    searchString = MediaFactory.cleanSearchText(downloadData.getTitle(),
                            media ? ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO.getValue());
                    break;
                case SHOW_THEME:
                    searchString = MediaFactory.cleanSearchText(downloadData.getTheme(),
                            media ? ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO.getValue());
                    break;
                default:
                    searchString = MediaFactory.cleanSearchText(downloadData.getTheme() + " " + downloadData.getTitle(),
                            media ? ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO.getValue());
            }

        } else {
            //Abo
            switch (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.getValue()) {
                case SHOW_TITEL:
                    searchString = downloadData.getTitle();
                    break;
                case SHOW_THEME:
                    searchString = downloadData.getTheme();
                    break;
                default:
                    searchString = downloadData.getTheme() + " " + downloadData.getTitle();
            }
        }
        if (media) {
            txtSearchMedia.setText(searchString);
        } else {
            txtSearchAbo.setText(searchString);
        }
    }

    private void init() {
        menuMedia.setTooltip(new Tooltip("Menü für die Mediensammlung anzeigen"));
        menuMedia.setGraphic(ProgIcons.Icons.ICON_TOOLBAR_MENU_SMALL.getImageView());
        menuMedia.getStyleClass().addAll("btnFunction", "btnFunc-5");

        menuAbo.setTooltip(new Tooltip("Menü für die Anzeige erledigter Abos anzeigen"));
        menuAbo.setGraphic(ProgIcons.Icons.ICON_TOOLBAR_MENU_SMALL.getImageView());
        menuAbo.getStyleClass().addAll("btnFunction", "btnFunc-5");

        final Button btnHelpMedia = PButton.helpButton("Mediensammlung", HelpText.DOWNLOAD_GUI_MEDIA);
        VBox vLeft = new VBox(2);
        vLeft.setPadding(new Insets(P2LibConst.DIST_BUTTON));
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(menuMedia, txtSearchMedia, btnHelpMedia);
        HBox.setHgrow(txtSearchMedia, Priority.ALWAYS);
        vLeft.getChildren().add(hBox);

        final Button btnHelpAbo = PButton.helpButton("Mediensammlung", HelpText.DOWNLOAD_GUI_ABO);
        VBox vRight = new VBox(2);
        vRight.setPadding(new Insets(P2LibConst.DIST_BUTTON));
        hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(menuAbo, txtSearchAbo, btnHelpAbo);
        HBox.setHgrow(txtSearchAbo, Priority.ALWAYS);
        vRight.getChildren().add(hBox);

        vLeft.getChildren().add(tableMedia);
        vRight.getChildren().add(tableAbo);

        tableMedia.setStyle("-fx-border-width: 1px;");
        tableMedia.setStyle("-fx-border-color: -text-color-blue;");
        tableAbo.setStyle("-fx-border-width: 1px;");
        tableAbo.setStyle("-fx-border-color: -text-color-blue;");

        SplitPane splitPane = new SplitPane();
        splitPane.setPadding(new Insets(0));
        splitPane.getItems().addAll(vLeft, vRight);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_MEDIA_DIVIDER);
        HBox.setHgrow(splitPane, Priority.ALWAYS);

        super.setSpacing(0);
        super.setPadding(new Insets(0));
        super.getChildren().addAll(splitPane);
    }

    private void initMenu(boolean media) {
        //Media
        RadioMenuItem miTitel = new RadioMenuItem("Titel");
        RadioMenuItem miTheme = new RadioMenuItem("Thema");
        RadioMenuItem miTT = new RadioMenuItem("Titel-Thema");
        ToggleGroup tg = new ToggleGroup();
        miTitel.setToggleGroup(tg);
        miTheme.setToggleGroup(tg);
        miTT.setToggleGroup(tg);
        switch (media ? ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.getValue() : ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.getValue()) {
            case SHOW_TITEL:
                miTitel.setSelected(true);
                break;
            case SHOW_THEME:
                miTheme.setSelected(true);
                break;
            default:
                miTT.setSelected(true);
                break;
        }
        if (media) {
            menuMedia.getItems().addAll(miTitel, miTheme, miTT);
        } else {
            menuAbo.getItems().addAll(miTitel, miTheme, miTT);
        }
        miTitel.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.setValue(SHOW_TITEL);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.setValue(SHOW_TITEL);
                }
            }
            getSearchString(downloadData, media);
        });
        miTheme.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.setValue(SHOW_THEME);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.setValue(SHOW_THEME);
                }
            }
            getSearchString(downloadData, media);
        });
        miTT.selectedProperty().addListener((u, o, n) -> {
            if (n) {
                if (media) {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_MEDIA.setValue(SHOW_TT);
                } else {
                    ProgConfig.DOWNLOAD_GUI_MEDIA_SHOW_TT_ABO.setValue(SHOW_TT);
                }
            }
            getSearchString(downloadData, media);
        });

        final MenuItem miDialog = new MenuItem("Dialog öffnen");
        miDialog.setOnAction(a -> new MediaDialogController(media ? txtSearchMedia.getText() : txtSearchAbo.getText(), true));
        if (media) {
            menuMedia.getItems().add(miDialog);
        } else {
            menuAbo.getItems().add(miDialog);
        }

        CheckMenuItem chkClean = new CheckMenuItem("Putzen");
        chkClean.selectedProperty().bindBidirectional(media ? ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_CLEAN_ABO);
        chkClean.setOnAction(a -> {
            getSearchString(downloadData, media);
        });
        if (media) {
            menuMedia.getItems().add(chkClean);
        } else {
            menuAbo.getItems().add(chkClean);
        }

        CheckMenuItem chkAndOr = new CheckMenuItem("Verknüpfen mit UND [sonst ODER]");
        chkAndOr.disableProperty().bind(chkClean.selectedProperty().not());
        chkAndOr.selectedProperty().bindBidirectional(media ? ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA : ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO);
        chkAndOr.setOnAction(a -> {
            if (media) {
                //Media
                if (ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA.getValue()) {
                    txtSearchMedia.setText(txtSearchMedia.getText().replace(",", ":"));
                } else {
                    txtSearchMedia.setText(txtSearchMedia.getText().replace(":", ","));
                }
            } else {
                //Abo
                if (ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO.getValue()) {
                    txtSearchAbo.setText(txtSearchAbo.getText().replace(",", ":"));
                } else {
                    txtSearchAbo.setText(txtSearchAbo.getText().replace(":", ","));
                }
            }
            getSearchString(downloadData, media);
        });
        if (media) {
            menuMedia.getItems().add(chkAndOr);
        } else {
            menuAbo.getItems().add(chkAndOr);
        }
    }

    private void initSearch() {
        txtSearchMedia.textProperty().addListener((u, o, n) -> {
            progData.mediaDataList.filteredListSetPredicate(SearchPredicateWorker.
                    getPredicateMediaData(txtSearchMedia.getText(), false));
        });
        txtSearchAbo.textProperty().addListener((u, o, n) -> {
            progData.erledigteAbos.filteredListSetPredicate(SearchPredicateWorker.getPredicateHistoryData(false, true,
                    txtSearchAbo.getText(), true));
        });
    }

    private void initTableMedia() {
        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Mediensammlung: Dateiname");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.getStyleClass().add("special-column-style");

        tableMedia.getColumns().addAll(nameColumn);
        tableMedia.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        SortedList<MediaData> sortedList = progData.mediaDataList.getSortedList();
        sortedList.comparatorProperty().bind(tableMedia.comparatorProperty());
        tableMedia.setItems(sortedList);
    }

    private void initTableAbo() {
        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Abos: Thema");
        themeColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(45.0 / 100));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("special-column-style");

        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Abos: Titel");
        titleColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(45.0 / 100));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("special-column-style");

        final TableColumn<HistoryData, Date> dateColumn = new TableColumn<>("Abos: Datum");
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
