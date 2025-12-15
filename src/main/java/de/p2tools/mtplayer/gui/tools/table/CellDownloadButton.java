/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactoryStopDownload;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.tools.P2GetList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class CellDownloadButton<S, T> extends TableCell<S, T> {

    public final Callback<TableColumn<DownloadData, Integer>, TableCell<DownloadData, Integer>> cellFactory
            = (final TableColumn<DownloadData, Integer> param) -> {

        final TableCell<DownloadData, Integer> cell = new TableCell<>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnDownStart;
                final Button btnDownDel;
                final Button btnDownStop;
                final Button btnFilmStart;
                final Button btnOpenDirectory;

                if (item <= DownloadConstants.STATE_STOPPED) {
                    btnDownStart = new Button("");
                    btnDownStart.getStyleClass().addAll("pFuncBtn");
                    btnDownStart.setTooltip(new Tooltip("Download starten"));
                    btnDownStart.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_START.getFontIcon());
                    btnDownStart.setOnAction((ActionEvent event) -> {
                        getTableView().getSelectionModel().clearSelection();
                        getTableView().getSelectionModel().select(getIndex());

                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.startDownloads(download);

                        getTableView().refresh();
                        getTableView().requestFocus();
                    });

                    btnDownDel = new Button("");
                    btnDownDel.getStyleClass().addAll("pFuncBtn");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_DEL.getFontIcon());
                    btnDownDel.setOnAction(event -> {
                        getTableView().getSelectionModel().clearSelection();
                        getTableView().getSelectionModel().select(getIndex());

                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);

                        getTableView().refresh();
                        getTableView().requestFocus();
                    });

                    if (ProgConfig.SYSTEM_SMALL_TABLE_ROW_DOWNLOAD.get()) {
                        btnDownStart.setMaxHeight(Table.ROW_HEIGHT_MIN);
                        btnDownStart.setMinHeight(Table.ROW_HEIGHT_MIN);
                        btnDownDel.setMaxHeight(Table.ROW_HEIGHT_MIN);
                        btnDownDel.setMinHeight(Table.ROW_HEIGHT_MIN);

                    } else {
                        btnDownStart.setMaxHeight(Table.ROW_HEIGHT_MAX);
                        btnDownStart.setMinHeight(Table.ROW_HEIGHT_MAX);
                        btnDownDel.setMaxHeight(Table.ROW_HEIGHT_MAX);
                        btnDownDel.setMinHeight(Table.ROW_HEIGHT_MAX);

                        btnDownStart.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_START_BIG.getFontIcon());
                        btnDownDel.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_DEL_BIG.getFontIcon());
                    }

                    hbox.getChildren().addAll(btnDownStart, btnDownDel);
                    setGraphic(hbox);

                } else if (item < DownloadConstants.STATE_FINISHED) {
                    btnDownStop = new Button("");
                    btnDownStop.getStyleClass().addAll("pFuncBtn");
                    btnDownStop.setTooltip(new Tooltip("Download stoppen"));
                    btnDownStop.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_STOP.getFontIcon());
                    btnDownStop.setOnAction((ActionEvent event) -> {
                        getTableView().getSelectionModel().clearSelection();
                        getTableView().getSelectionModel().select(getIndex());

                        DownloadData download = getTableView().getItems().get(getIndex());
                        DownloadFactoryStopDownload.stopDownloads(new P2GetList<DownloadData>().getArrayList(download));

                        getTableView().refresh();
                        getTableView().requestFocus();
                    });

                    btnDownDel = new Button("");
                    btnDownDel.getStyleClass().addAll("pFuncBtn");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_DEL.getFontIcon());
                    btnDownDel.setOnAction(event -> {
                        getTableView().getSelectionModel().clearSelection();
                        getTableView().getSelectionModel().select(getIndex());

                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);

                        getTableView().refresh();
                        getTableView().requestFocus();
                    });

                    if (ProgConfig.SYSTEM_SMALL_TABLE_ROW_DOWNLOAD.get()) {
                        btnDownStop.setMaxHeight(Table.ROW_HEIGHT_MIN);
                        btnDownStop.setMinHeight(Table.ROW_HEIGHT_MIN);
                        btnDownDel.setMaxHeight(Table.ROW_HEIGHT_MIN);
                        btnDownDel.setMinHeight(Table.ROW_HEIGHT_MIN);

                    } else {
                        btnDownStop.setMaxHeight(Table.ROW_HEIGHT_MAX);
                        btnDownStop.setMinHeight(Table.ROW_HEIGHT_MAX);
                        btnDownDel.setMaxHeight(Table.ROW_HEIGHT_MAX);
                        btnDownDel.setMinHeight(Table.ROW_HEIGHT_MAX);

                        btnDownStop.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_STOP_BIG.getFontIcon());
                        btnDownDel.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_DEL_BIG.getFontIcon());
                    }

                    hbox.getChildren().addAll(btnDownStop, btnDownDel);
                    setGraphic(hbox);

                } else if (item == DownloadConstants.STATE_FINISHED) {
                    btnFilmStart = new Button("");
                    btnFilmStart.getStyleClass().addAll("pFuncBtn");
                    btnFilmStart.setTooltip(new Tooltip("Gespeicherten Film abspielen"));
                    btnFilmStart.setGraphic(PIconFactory.PICON.TABLE_FILM_PLAY.getFontIcon());
                    btnFilmStart.setOnAction((ActionEvent event) -> {
                        getTableView().getSelectionModel().clearSelection();
                        getTableView().getSelectionModel().select(getIndex());

                        DownloadData download = getTableView().getItems().get(getIndex());
                        P2Open.playStoredFilm(download.getDestPathFile(),
                                ProgConfig.SYSTEM_PROG_PLAY_FILME, PIconFactory.PICON.BTN_DIR_OPEN.getFontIcon());

                        getTableView().refresh();
                        getTableView().requestFocus();
                    });

                    btnOpenDirectory = new Button();
                    btnOpenDirectory.getStyleClass().addAll("pFuncBtn");
                    btnOpenDirectory.setTooltip(new Tooltip("Ordner mit gespeichertem Film öffnen"));
                    btnOpenDirectory.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_OPEN_DIR.getFontIcon());
                    btnOpenDirectory.setOnAction((ActionEvent event) -> {
                        getTableView().getSelectionModel().clearSelection();
                        getTableView().getSelectionModel().select(getIndex());

                        DownloadData download = getTableView().getItems().get(getIndex());
                        P2Open.openDir(download.getDestPath(),
                                ProgConfig.SYSTEM_PROG_OPEN_DIR, PIconFactory.PICON.BTN_DIR_OPEN.getFontIcon());

                        getTableView().refresh();
                        getTableView().requestFocus();
                    });

                    if (ProgConfig.SYSTEM_SMALL_TABLE_ROW_DOWNLOAD.get()) {
                        btnFilmStart.setMaxHeight(Table.ROW_HEIGHT_MIN);
                        btnFilmStart.setMinHeight(Table.ROW_HEIGHT_MIN);
                        btnOpenDirectory.setMaxHeight(Table.ROW_HEIGHT_MIN);
                        btnOpenDirectory.setMinHeight(Table.ROW_HEIGHT_MIN);

                    } else {
                        btnFilmStart.setMaxHeight(Table.ROW_HEIGHT_MAX);
                        btnFilmStart.setMinHeight(Table.ROW_HEIGHT_MAX);
                        btnOpenDirectory.setMaxHeight(Table.ROW_HEIGHT_MAX);
                        btnOpenDirectory.setMinHeight(Table.ROW_HEIGHT_MAX);

                        btnFilmStart.setGraphic(PIconFactory.PICON.TABLE_FILM_PLAY_BIG.getFontIcon());
                        btnOpenDirectory.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_OPEN_DIR_BIG.getFontIcon());
                    }

                    hbox.getChildren().addAll(btnFilmStart, btnOpenDirectory);
                    setGraphic(hbox);

                } else if (item == DownloadConstants.STATE_ERROR) {
                    btnDownStart = new Button("");
                    btnDownStart.getStyleClass().addAll("pFuncBtn");
                    btnDownStart.setTooltip(new Tooltip("Download wieder starten"));
                    btnDownStart.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_START.getFontIcon());
                    btnDownStart.setOnAction((ActionEvent event) -> {
                        getTableView().getSelectionModel().clearSelection();
                        getTableView().getSelectionModel().select(getIndex());

                        DownloadData download = getTableView().getItems().get(getIndex());
                        List<DownloadData> list = new ArrayList<>();
                        list.add(download);
                        ProgData.getInstance().downloadList.startDownloads(list, true);

                        getTableView().refresh();
                        getTableView().requestFocus();
                    });

                    btnDownDel = new Button("");
                    btnDownDel.getStyleClass().addAll("pFuncBtn");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_DEL.getFontIcon());
                    btnDownDel.setOnAction(event -> {
                        getTableView().getSelectionModel().clearSelection();
                        getTableView().getSelectionModel().select(getIndex());

                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);

                        getTableView().refresh();
                        getTableView().requestFocus();
                    });

                    if (ProgConfig.SYSTEM_SMALL_TABLE_ROW_DOWNLOAD.get()) {
                        btnDownStart.setMaxHeight(Table.ROW_HEIGHT_MIN);
                        btnDownStart.setMinHeight(Table.ROW_HEIGHT_MIN);
                        btnDownDel.setMaxHeight(Table.ROW_HEIGHT_MIN);
                        btnDownDel.setMinHeight(Table.ROW_HEIGHT_MIN);

                    } else {
                        btnDownStart.setMaxHeight(Table.ROW_HEIGHT_MAX);
                        btnDownStart.setMinHeight(Table.ROW_HEIGHT_MAX);
                        btnDownDel.setMaxHeight(Table.ROW_HEIGHT_MAX);
                        btnDownDel.setMinHeight(Table.ROW_HEIGHT_MAX);

                        btnDownStart.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_START_BIG.getFontIcon());
                        btnDownDel.setGraphic(PIconFactory.PICON.TABLE_DOWNLOAD_DEL_BIG.getFontIcon());
                    }

                    hbox.getChildren().addAll(btnDownStart, btnDownDel);
                    setGraphic(hbox);

                } else {
                    setGraphic(null);
                    setText(null);
                }
            }
        };
        return cell;
    };
}