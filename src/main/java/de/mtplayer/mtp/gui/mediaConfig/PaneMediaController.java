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

package de.mtplayer.mtp.gui.mediaConfig;

import de.mtplayer.mLib.tools.CheckBoxCell;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.mediaDb.MediaData;
import de.mtplayer.mtp.controller.mediaDb.MediaFileSize;
import de.mtplayer.mtp.tools.storedFilter.Filter;
import de.mtplayer.mtp.tools.storedFilter.FilterCheckRegEx;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.dialogs.accordion.PAccordionPane;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class PaneMediaController extends PAccordionPane {

    private VBox vBox = new VBox(10);
    private TextField txtSearch = new TextField();
    private Label lblTreffer = new Label();

    ChangeListener<Number> changeListener;

    private final ProgData progData;
    private final Stage stage;

    public PaneMediaController(Stage stage) {
        super(stage, ProgConfig.MEDIA_CONFIG_DIALOG_ACCORDION.getBooleanProperty(), ProgConfig.SYSTEM_MEDIA_DIALOG_MEDIA);
        progData = ProgData.getInstance();
        this.stage = stage;
        init();
    }

    public void close() {
        super.close();
        progData.mediaDataList.sizeProperty().removeListener(changeListener);
    }


    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        TitledPane tpConfig = new TitledPane("Mediensammlung", vBox);
        result.add(tpConfig);
        tpConfig.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpConfig, Priority.ALWAYS);

        initTable();
        initFilter();
        writeQuantity();

        changeListener = (observable, oldValue, newValue) -> {
            Platform.runLater(() -> PaneMediaController.this.filter());
        };
        progData.mediaDataList.sizeProperty().addListener(changeListener);

        return result;
    }

    private void initTable() {
        TableView<MediaData> tableView = new TableView<>();
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<MediaData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaData, MediaFileSize> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));

        final TableColumn<MediaData, String> collectionNameColumn = new TableColumn<>("Sammlung");
        collectionNameColumn.setCellValueFactory(new PropertyValueFactory<>("collectionName"));

        final TableColumn<MediaData, Boolean> externalColumn = new TableColumn<>("extern");
        externalColumn.setCellValueFactory(new PropertyValueFactory<>("external"));
        externalColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);

        tableView.getColumns().addAll(nameColumn, pathColumn, sizeColumn, collectionNameColumn, externalColumn);

        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(30.0 / 100));
        pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(30.0 / 100));
        sizeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(10.0 / 100));
        collectionNameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(15.0 / 100));
        externalColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(10.0 / 100));

        tableView.setOnMousePressed(m -> {
            if (tableView.getItems().isEmpty()) {
                return;
            }
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                MediaData mediaData = tableView.getSelectionModel().getSelectedItem();
                if (mediaData == null) {
                    PAlert.showInfoNoSelection();

                } else {
                    ContextMenu contextMenu = new PaneMediaContextMenu(stage, mediaData).getContextMenu();
                    tableView.setContextMenu(contextMenu);
                }
            }
        });


        SortedList<MediaData> sortedList = progData.mediaDataList.getSortedList();
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        progData.mediaDataList.filterdListSetPredTrue();
        tableView.setItems(sortedList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void initFilter() {
        Button btnClear = new Button();
        btnClear.setGraphic(new ProgIcons().ICON_BUTTON_STOP);
        btnClear.setOnAction(a -> txtSearch.clear());

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(new Label("Suchen:"), txtSearch, btnClear, lblTreffer);
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        hBox.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().addAll(hBox);

        FilterCheckRegEx fTT = new FilterCheckRegEx(txtSearch);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            fTT.checkPattern();
            filter();
        });
    }

    private void filter() {
        final String searchStr = txtSearch.getText().toLowerCase().trim();
        progData.mediaDataList.filteredListSetPredicate(media -> {
            if (searchStr.isEmpty()) {
                return true;
            }
            final Pattern p = Filter.makePattern(searchStr);
            if (p != null) {
                return filterMedia(media, p);
            } else {
                return filterMedia(media, searchStr);
            }
        });

        writeQuantity();
    }

    private boolean filterMedia(MediaData media, Pattern p) {
        return p.matcher(media.getName()).matches();
    }

    private boolean filterMedia(MediaData media, String search) {
        return media.getName().toLowerCase().contains(search);
    }

    private void writeQuantity() {
        final int filtered = progData.mediaDataList.getFilteredList().size();
        final int sum = progData.mediaDataList.size();
        if (sum != filtered) {
            lblTreffer.setText("Anzahl: " + filtered + " von " + sum);
        } else {
            lblTreffer.setText("Anzahl: " + sum + "");
        }
    }
}