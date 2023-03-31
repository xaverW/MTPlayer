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
import de.p2tools.mtplayer.gui.mediaconfig.SearchPredicateWorker;
import de.p2tools.p2lib.guitools.POpen;
import de.p2tools.p2lib.guitools.ptable.CellCheckBox;
import de.p2tools.p2lib.tools.file.PFileUtils;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

public class PaneMedia extends PaneDialog {

    private ProgData progData = ProgData.getInstance();

    public PaneMedia(String searchStrOrg, StringProperty searchStrProp) {
        super(searchStrOrg, searchStrProp, true);
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
            if (dataNew != null) {
                txtPathMedia.setText(dataNew.getPath());
                txtTitleMedia.setText(dataNew.getName());
            } else {
                txtPathMedia.setText("");
                txtTitleMedia.setText("");
            }
        });

        SortedList<MediaData> sortedList = progData.mediaDataList.getSortedList();
        sortedList.comparatorProperty().bind(tableMedia.comparatorProperty());
        tableMedia.setItems(sortedList);
    }

    @Override
    void initAction() {
        super.initAction();
        btnAndOr.setOnAction(a -> {
            ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA.setValue(!ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA.getValue());
            if (ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_MEDIA.getValue()) {
                txtSearch.setText(txtSearch.getText().replace(",", ":"));
            } else {
                txtSearch.setText(txtSearch.getText().replace(":", ","));
            }
        });

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
        progData.mediaDataList.filteredListSetPredicate(SearchPredicateWorker.getPredicateMediaData(txtSearch.getText(), false));
        lblHits.setText(progData.mediaDataList.getFilteredList().size() + "");
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
