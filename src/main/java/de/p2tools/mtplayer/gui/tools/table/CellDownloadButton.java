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

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactoryStopDownload;
import de.p2tools.p2lib.guitools.POpen;
import de.p2tools.p2lib.tools.PGetList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class CellDownloadButton<S, T> extends TableCell<S, T> {

    public final Callback<TableColumn<DownloadData, Integer>, TableCell<DownloadData, Integer>> cellFactory
            = (final TableColumn<DownloadData, Integer> param) -> {

        final TableCell<DownloadData, Integer> cell = new TableCell<DownloadData, Integer>() {

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
                    btnDownStart.getStyleClass().addAll("btnFunction", "btnFuncTable");
                    btnDownStart.setTooltip(new Tooltip("Download starten"));
                    btnDownStart.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_DOWNLOAD_START.getImageView());
                    btnDownStart.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.startDownloads(download);
                    });

                    btnDownDel = new Button("");
                    btnDownDel.getStyleClass().addAll("btnFunction", "btnFuncTable");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_DOWNLOAD_DEL.getImageView());
                    btnDownDel.setOnAction(event -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);
                    });

                    if (ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD.get()) {
                        btnDownStart.setMinHeight(18);
                        btnDownStart.setMaxHeight(18);
                        btnDownDel.setMinHeight(18);
                        btnDownDel.setMaxHeight(18);
                    }
                    hbox.getChildren().addAll(btnDownStart, btnDownDel);
                    setGraphic(hbox);

                } else if (item < DownloadConstants.STATE_FINISHED) {
                    btnDownStop = new Button("");
                    btnDownStop.getStyleClass().addAll("btnFunction", "btnFuncTable");
                    btnDownStop.setTooltip(new Tooltip("Download stoppen"));
                    btnDownStop.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_DOWNLOAD_STOP.getImageView());
                    btnDownStop.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        DownloadFactoryStopDownload.stopDownloads(new PGetList<DownloadData>().getArrayList(download));
                        getTableView().refresh();// damit neuer Status/Fortschritt angezeigt wird
                    });

                    btnDownDel = new Button("");
                    btnDownDel.getStyleClass().addAll("btnFunction", "btnFuncTable");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_DOWNLOAD_DEL.getImageView());
                    btnDownDel.setOnAction(event -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);
                    });

                    if (ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD.get()) {
                        btnDownStop.setMinHeight(18);
                        btnDownStop.setMaxHeight(18);
                        btnDownDel.setMinHeight(18);
                        btnDownDel.setMaxHeight(18);
                    }
                    hbox.getChildren().addAll(btnDownStop, btnDownDel);
                    setGraphic(hbox);

                } else if (item == DownloadConstants.STATE_FINISHED) {
                    btnFilmStart = new Button("");
                    btnFilmStart.getStyleClass().addAll("btnFunction", "btnFuncTable");
                    btnFilmStart.setTooltip(new Tooltip("gespeicherten Film abspielen"));
                    btnFilmStart.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_FILM_PLAY.getImageView());
                    btnFilmStart.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        POpen.playStoredFilm(download.getDestPathFile(),
                                ProgConfig.SYSTEM_PROG_PLAY_FILME, ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
                    });

                    btnOpenDirectory = new Button();
                    btnOpenDirectory.getStyleClass().addAll("btnFunction", "btnFuncTable");
                    btnOpenDirectory.setTooltip(new Tooltip("Ordner mit gespeichertem Film öffnen"));
                    btnOpenDirectory.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_DOWNLOAD_OPEN_DIR.getImageView());
                    btnOpenDirectory.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        POpen.openDir(download.getDestPath(),
                                ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
                    });

                    if (ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD.get()) {
                        btnFilmStart.setMinHeight(18);
                        btnFilmStart.setMaxHeight(18);
                        btnOpenDirectory.setMinHeight(18);
                        btnOpenDirectory.setMaxHeight(18);
                    }
                    hbox.getChildren().addAll(btnFilmStart, btnOpenDirectory);
                    setGraphic(hbox);

                } else if (item == DownloadConstants.STATE_ERROR) {
                    btnDownStart = new Button("");
                    btnDownStart.getStyleClass().addAll("btnFunction", "btnFuncTable");
                    btnDownStart.setTooltip(new Tooltip("Download wieder starten"));
                    btnDownStart.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_DOWNLOAD_START.getImageView());
                    btnDownStart.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        List<DownloadData> list = new ArrayList<>();
                        list.add(download);
                        ProgData.getInstance().downloadList.startDownloads(list, true);
                    });

                    btnDownDel = new Button("");
                    btnDownDel.getStyleClass().addAll("btnFunction", "btnFuncTable");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_DOWNLOAD_DEL.getImageView());
                    btnDownDel.setOnAction(event -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);
                    });

                    if (ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD.get()) {
                        btnDownStart.setMinHeight(18);
                        btnDownStart.setMaxHeight(18);
                        btnDownDel.setMinHeight(18);
                        btnDownDel.setMaxHeight(18);
                    }
                    hbox.getChildren().addAll(btnDownStart, btnDownDel);
                    setGraphic(hbox);

                } else {
                    setGraphic(null);
                    setText(null);
                }

                setCellStyle(this, item);
            }
        };
        return cell;
    };

    private void setCellStyle(TableCell<DownloadData, Integer> cell, Integer item) {
        TableRow<DownloadData> currentRow = cell.getTableRow();
        if (currentRow == null) {
            return;
        }
        switch (item) {
            case DownloadConstants.STATE_INIT:
            case DownloadConstants.STATE_STOPPED:
                currentRow.setStyle("");
                break;
            case DownloadConstants.STATE_STARTED_WAITING:
                currentRow.setStyle(ProgColorList.DOWNLOAD_WAIT.getCssBackground());
                break;
            case DownloadConstants.STATE_STARTED_RUN:
                currentRow.setStyle(ProgColorList.DOWNLOAD_RUN.getCssBackground());
                break;
            case DownloadConstants.STATE_FINISHED:
                currentRow.setStyle(ProgColorList.DOWNLOAD_FINISHED.getCssBackground());
                break;
            case DownloadConstants.STATE_ERROR:
                currentRow.setStyle(ProgColorList.DOWNLOAD_ERROR.getCssBackground());
                break;
        }
    }
}