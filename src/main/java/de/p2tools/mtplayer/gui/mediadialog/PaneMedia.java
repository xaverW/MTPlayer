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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.mtplayer.controller.mediadb.MediaDataWorker;
import de.p2tools.mtplayer.controller.mediadb.MediaFileSize;
import de.p2tools.mtplayer.controller.mediadb.MediaSearchPredicateFactory;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.POpen;
import de.p2tools.p2lib.guitools.ptable.CellCheckBox;
import de.p2tools.p2lib.tools.file.PFileUtils;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class PaneMedia extends PaneDialog {

    private final Stage stage;
    private final FilteredList<MediaData> filteredList;
    private final SortedList<MediaData> sortedList;

    private ProgData progData = ProgData.getInstance();

    public PaneMedia(Stage stage, String searchThemeOrg, String searchTitelOrg, StringProperty searchStringProp) {
        super(searchThemeOrg, searchTitelOrg, searchStringProp, true);
        this.stage = stage;
        this.filteredList = new FilteredList<>(progData.mediaDataList, p -> true);
        this.sortedList = new SortedList<>(filteredList);
    }

    @Override
    public void close() {
        progData.mediaDataList.sizeProperty().removeListener(sizeListener);
        progress.visibleProperty().unbind();
        btnCreateMediaDB.disableProperty().unbind();
    }

    @Override
    void initTable() {
        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(50.0 / 100));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<MediaData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(25.0 / 100));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaData, MediaFileSize> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(15.0 / 100));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<MediaData, Boolean> externalColumn = new TableColumn<>("extern");
        externalColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(10.0 / 100));
        externalColumn.setCellValueFactory(new PropertyValueFactory<>("external"));
        externalColumn.setCellFactory(new CellCheckBox().cellFactoryBool);

        tableMedia.getColumns().addAll(nameColumn, pathColumn, sizeColumn, externalColumn);

        tableMedia.getSelectionModel().selectedItemProperty().addListener((observableValue, dataOld, dataNew) -> {
            setTableSel(dataNew);
        });
        tableMedia.setOnMousePressed(m -> {
            if (tableMedia.getItems().isEmpty()) {
                return;
            }
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                MediaData mediaData = tableMedia.getSelectionModel().getSelectedItem();
                if (mediaData == null) {
                    PAlert.showInfoNoSelection();

                } else {
                    ContextMenu contextMenu = new PaneMediaContextMenu(stage, mediaData).getContextMenu();
                    tableMedia.setContextMenu(contextMenu);
                }
            }
        });
        tableMedia.setRowFactory(tv -> {
            TableRow<MediaData> row = new TableRow<>();
            row.hoverProperty().addListener((observable) -> {
                final MediaData mediaData = row.getItem();
                if (row.isHover() && mediaData != null) {
                    setTableSel(mediaData);
                } else {
                    setTableSel(tableMedia.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });

        sortedList.comparatorProperty().bind(tableMedia.comparatorProperty());
        tableMedia.setItems(sortedList);
    }

    private void setTableSel(MediaData mediaData) {
        if (mediaData != null) {
            txtPathMedia.setText(mediaData.getPath());
            txtTitleMedia.setText(mediaData.getName());
        } else {
            txtPathMedia.setText("");
            txtTitleMedia.setText("");
        }
    }

    @Override
    void initAction() {
        super.initAction();

        lblGesamtMedia.setText(progData.mediaDataList.size() + "");
        sizeListener = (observable, oldValue, newValue) -> {
            Platform.runLater(() -> lblGesamtMedia.setText(progData.mediaDataList.size() + ""));
        };
        progData.mediaDataList.sizeProperty().addListener(sizeListener);

        progress.visibleProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.disableProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.setOnAction(e -> MediaDataWorker.createMediaDb());

        btnOpen.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnOpen.setTooltip(new Tooltip("Ausgewählten Pfad im Dateimanager öffnen"));
        btnOpen.setOnAction(e -> open());
        btnOpen.disableProperty().bind(txtPathMedia.textProperty().isEmpty().and(txtTitleMedia.textProperty().isEmpty()));

        btnPlay.setGraphic(ProgIcons.Icons.ICON_BUTTON_PLAY.getImageView());
        btnPlay.setTooltip(new Tooltip("Ausgewählten Film abspielen"));
        btnPlay.setOnAction(e -> play());
        btnPlay.disableProperty().bind(txtPathMedia.textProperty().isEmpty().and(txtTitleMedia.textProperty().isEmpty()));
    }

    @Override
    void filter() {
        filteredList.setPredicate(MediaSearchPredicateFactory.getPredicateMediaData(txtSearch.getText()));
        lblHits.setText(filteredList.size() + "");
    }

    private void play() {
        final String path = txtPathMedia.getText();
        final String name = txtTitleMedia.getText();
        if (!name.isEmpty() && !path.isEmpty()) {
            POpen.playStoredFilm(PFileUtils.addsPath(path, name),
                    ProgConfig.SYSTEM_PROG_PLAY_FILME, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        }
    }

    private void open() {
        final String s = txtPathMedia.getText();
        POpen.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
    }
}
