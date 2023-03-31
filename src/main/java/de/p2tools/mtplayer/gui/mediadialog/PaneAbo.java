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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.gui.mediaconfig.SearchPredicateWorker;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;

public class PaneAbo extends PaneDialog {

    private ProgData progData = ProgData.getInstance();

    public PaneAbo(String searchStrOrg, StringProperty searchStrProp) {
        super(searchStrOrg, searchStrProp, false);
    }

    @Override
    public void close() {
        progData.erledigteAbos.removeListener(listener);
    }

    @Override
    void initTable() {
        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(20.0 / 100));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(50.0 / 100));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        final TableColumn<HistoryData, Date> dateColumn = new TableColumn<>("Datum");
        dateColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(15.0 / 100));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        dateColumn.getStyleClass().add("alignCenter");

        final TableColumn<HistoryData, String> pathColumn = new TableColumn<>("Url");
        pathColumn.prefWidthProperty().bind(tableAbo.widthProperty().multiply(14.0 / 100));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        tableAbo.getColumns().addAll(themeColumn, titleColumn, dateColumn, pathColumn);

        tableAbo.getSelectionModel().selectedItemProperty().addListener((observableValue, dataOld, dataNew) -> {
            if (dataNew != null) {
                txtTitleMedia.setText(dataNew.getTitle());
                txtPathMedia.setText(dataNew.getUrl());
            } else {
                txtTitleMedia.setText("");
                txtPathMedia.setText("");
            }
        });

        SortedList<HistoryData> sortedList = progData.erledigteAbos.getSortedList();
        sortedList.comparatorProperty().bind(tableAbo.comparatorProperty());
        tableAbo.setItems(sortedList);
    }

    @Override
    void initAction() {
        super.initAction();
        btnAndOr.setOnAction(a -> {
            ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO.setValue(!ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO.getValue());
            if (ProgConfig.DOWNLOAD_GUI_MEDIA_AND_OR_ABO.getValue()) {
                txtSearch.setText(txtSearch.getText().replace(",", ":"));
            } else {
                txtSearch.setText(txtSearch.getText().replace(":", ","));
            }
        });

        lblGesamtMedia.setText(progData.erledigteAbos.size() + "");
        listener = c -> Platform.runLater(() -> {
            lblGesamtMedia.setText(progData.erledigteAbos.size() + "");
            filter();
        });
        progData.erledigteAbos.addListener(listener);

        rbTheme.selectedProperty().addListener((o, ol, ne) -> filter());
        rbTitle.selectedProperty().addListener((o, ol, ne) -> filter());
        rbTt.selectedProperty().addListener((o, ol, ne) -> filter());
    }

    @Override
    void filter() {
        if (rbTheme.isSelected()) {
            ProgConfig.MEDIA_DIALOG_SEARCH_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEMA);
        } else if (rbTitle.isSelected()) {
            ProgConfig.MEDIA_DIALOG_SEARCH_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_TITEL);
        } else {
            ProgConfig.MEDIA_DIALOG_SEARCH_ABO.setValue(ProgConst.MEDIA_COLLECTION_SEARCH_THEMA_TITEL);
        }

        progData.erledigteAbos.filteredListSetPredicate(SearchPredicateWorker.getPredicateHistoryData(rbTheme.isSelected(), rbTitle.isSelected(),
                txtSearch.getText(), false));
        lblHits.setText(progData.erledigteAbos.getFilteredList().size() + "");
    }
}
