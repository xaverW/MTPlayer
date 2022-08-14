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

package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadSizeData;
import de.p2tools.p2Lib.guiTools.PCheckBoxCell;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.guiTools.PTableFactory;
import de.p2tools.p2Lib.tools.GermanStringIntSorter;
import de.p2tools.p2Lib.tools.date.PDate;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TableDownload extends PTable<DownloadData> {

    private final BooleanProperty geoMelden;
    private final BooleanProperty small;

    public TableDownload(Table.TABLE_ENUM table_enum) {
        super(table_enum);
        this.table_enum = table_enum;
        geoMelden = ProgConfig.SYSTEM_MARK_GEO;
        small = ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD;

        initFileRunnerColumn();
    }

    @Override
    public Table.TABLE_ENUM getETable() {
        return table_enum;
    }

    public void resetTable() {
        initFileRunnerColumn();
        Table.resetTable(this);
    }

    private void initFileRunnerColumn() {
        getColumns().clear();

        setTableMenuButtonVisible(true);
        setEditable(false);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        final Comparator<String> sorter = GermanStringIntSorter.getInstance();
        ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD.addListener((observableValue, s, t1) -> refresh());
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> {
            PTableFactory.refreshTable(this);
        });
        ProgColorList.FILM_GEOBLOCK.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_WAIT.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_RUN.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_FINISHED.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_ERROR.colorProperty().addListener((a, b, c) -> refresh());

        final TableColumn<DownloadData, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        nrColumn.setCellFactory(cellFactoryNr);
        nrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<DownloadData, Integer> filmNrColumn = new TableColumn<>("Filmnr");
        filmNrColumn.setCellValueFactory(new PropertyValueFactory<>("filmNr"));
        filmNrColumn.setCellFactory(cellFactoryFilmNr);
        filmNrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<DownloadData, String> aboColumn = new TableColumn<>("Abo");
        aboColumn.setCellValueFactory(new PropertyValueFactory<>("aboName"));
        aboColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");
        themeColumn.setComparator(sorter);

        final TableColumn<DownloadData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");
        titleColumn.setComparator(sorter);

        // die zwei Spalten mit eigenen propertys
        final TableColumn<DownloadData, Integer> startColumn = new TableColumn<>("");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("guiState"));
        startColumn.setCellFactory(cellFactoryState);
        startColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Double> progressColumn = new TableColumn<>("Fortschritt"); //müssen sich unterscheiden!!
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("guiProgress"));
        progressColumn.setCellFactory(cellFactoryProgress);
        progressColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, Integer> remainingColumn = new TableColumn<>("Restzeit");
        remainingColumn.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remainingColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, Integer> speedColumn = new TableColumn<>("Geschwindigkeit");
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("bandwidth"));
        speedColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, String> startTimeColumn = new TableColumn<>("Startzeit");
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        startTimeColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, DownloadSizeData> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("downloadSize"));
        sizeColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, PDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("filmDate"));
        datumColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> timeColumn = new TableColumn<>("Zeit");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Integer> durationColumn = new TableColumn<>("Dauer [min]");
        durationColumn.setCellFactory(cellFactoryDuration);
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinute"));
        durationColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, Boolean> hdColumn = new TableColumn<>("HD");
        hdColumn.setCellValueFactory(new PropertyValueFactory<>("hd"));
        hdColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
        hdColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Boolean> utColumn = new TableColumn<>("UT");
        utColumn.setCellValueFactory(new PropertyValueFactory<>("ut"));
        utColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
        utColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Boolean> geoColumn = new TableColumn<>("Geo");
        geoColumn.setCellValueFactory(new PropertyValueFactory<>("geoBlocked"));
        geoColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
        geoColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> artColumn = new TableColumn<>("Art");
        artColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        artColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, String> srcColumn = new TableColumn<>("Quelle");
        srcColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        srcColumn.getStyleClass().add("alignCenterLeft");

//        final TableColumn<Download, Boolean> placedBackColumn = new TableColumn<>("Zurückgestellt");
//        placedBackColumn.setCellValueFactory(new PropertyValueFactory<>("placedBack"));
//        placedBackColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
//        placedBackColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> programColumn = new TableColumn<>("Programm");
        programColumn.setCellValueFactory(new PropertyValueFactory<>("program"));
        programColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, Integer> setColumn = new TableColumn<>("Programmset");
        setColumn.setCellValueFactory(new PropertyValueFactory<>("setData"));
        setColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, String> fileNameColumn = new TableColumn<>("Dateiname");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("destFileName"));
        fileNameColumn.getStyleClass().add("alignCenterLeft");
        fileNameColumn.setComparator(sorter);

        final TableColumn<DownloadData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("destPath"));
        pathColumn.getStyleClass().add("alignCenterLeft");
        pathColumn.setComparator(sorter);

        nrColumn.setPrefWidth(50);
        filmNrColumn.setPrefWidth(70);
        senderColumn.setPrefWidth(80);
        themeColumn.setPrefWidth(180);
        titleColumn.setPrefWidth(230);

        getColumns().addAll(
                nrColumn, filmNrColumn,
                aboColumn, senderColumn, themeColumn, titleColumn, startColumn,
                progressColumn, remainingColumn, speedColumn, startTimeColumn, sizeColumn,
                datumColumn, timeColumn, durationColumn,
                hdColumn, utColumn, geoColumn, artColumn, srcColumn, /*placedBackColumn,*/
                programColumn, setColumn, urlColumn, fileNameColumn, pathColumn);
    }

    private Callback<TableColumn<DownloadData, Integer>, TableCell<DownloadData, Integer>> cellFactoryState
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
                    btnDownStart.setTooltip(new Tooltip("Download starten"));
                    btnDownStart.setGraphic(ProgIcons.Icons.IMAGE_TABLE_DOWNLOAD_START.getImageView());
                    btnDownStart.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.startDownloads(download);
                    });

                    btnDownDel = new Button("");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(ProgIcons.Icons.IMAGE_TABLE_DOWNLOAD_DEL.getImageView());
                    btnDownDel.setOnAction(event -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);
                    });

                    if (small.get()) {
                        btnDownStart.setMinHeight(18);
                        btnDownStart.setMaxHeight(18);
                        btnDownDel.setMinHeight(18);
                        btnDownDel.setMaxHeight(18);
                    }
                    hbox.getChildren().addAll(btnDownStart, btnDownDel);
                    setGraphic(hbox);

                } else if (item < DownloadConstants.STATE_FINISHED) {
                    btnDownStop = new Button("");
                    btnDownStop.setTooltip(new Tooltip("Download stoppen"));
                    btnDownStop.setGraphic(ProgIcons.Icons.IMAGE_TABLE_DOWNLOAD_STOP.getImageView());
                    btnDownStop.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        download.stopDownload();
                    });

                    btnDownDel = new Button("");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(ProgIcons.Icons.IMAGE_TABLE_DOWNLOAD_DEL.getImageView());
                    btnDownDel.setOnAction(event -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);
                    });

                    if (small.get()) {
                        btnDownStop.setMinHeight(18);
                        btnDownStop.setMaxHeight(18);
                        btnDownDel.setMinHeight(18);
                        btnDownDel.setMaxHeight(18);
                    }
                    hbox.getChildren().addAll(btnDownStop, btnDownDel);
                    setGraphic(hbox);

                } else if (item == DownloadConstants.STATE_FINISHED) {
                    btnFilmStart = new Button("");
                    btnFilmStart.setTooltip(new Tooltip("gespeicherten Film abspielen"));
                    btnFilmStart.setGraphic(ProgIcons.Icons.IMAGE_TABLE_FILM_PLAY.getImageView());
                    btnFilmStart.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        POpen.playStoredFilm(download.getDestPathFile(),
                                ProgConfig.SYSTEM_PROG_PLAY_FILME, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
                    });

                    btnOpenDirectory = new Button();
                    btnOpenDirectory.setTooltip(new Tooltip("Ordner mit gespeichertem Film öffnen"));
                    btnOpenDirectory.setGraphic(ProgIcons.Icons.IMAGE_TABLE_DOWNLOAD_OPEN_DIR.getImageView());
                    btnOpenDirectory.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        POpen.openDir(download.getDestPath(),
                                ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
                    });

                    if (small.get()) {
                        btnFilmStart.setMinHeight(18);
                        btnFilmStart.setMaxHeight(18);
                        btnOpenDirectory.setMinHeight(18);
                        btnOpenDirectory.setMaxHeight(18);
                    }
                    hbox.getChildren().addAll(btnFilmStart, btnOpenDirectory);
                    setGraphic(hbox);

                } else if (item == DownloadConstants.STATE_ERROR) {
                    btnDownStart = new Button("");
                    btnDownStart.setTooltip(new Tooltip("Download wieder starten"));
                    btnDownStart.setGraphic(ProgIcons.Icons.IMAGE_TABLE_DOWNLOAD_START.getImageView());
                    btnDownStart.setOnAction((ActionEvent event) -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        List<DownloadData> list = new ArrayList<>();
                        list.add(download);
                        ProgData.getInstance().downloadList.startDownloads(list, true);
                    });

                    btnDownDel = new Button("");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(ProgIcons.Icons.IMAGE_TABLE_DOWNLOAD_DEL.getImageView());
                    btnDownDel.setOnAction(event -> {
                        DownloadData download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);
                    });

                    if (small.get()) {
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
                currentRow.setStyle(ProgColorList.DOWNLOAD_WAIT.getCssBackgroundAndSel());
                break;
            case DownloadConstants.STATE_STARTED_RUN:
                currentRow.setStyle(ProgColorList.DOWNLOAD_RUN.getCssBackgroundAndSel());
                break;
            case DownloadConstants.STATE_FINISHED:
                currentRow.setStyle(ProgColorList.DOWNLOAD_FINISHED.getCssBackgroundAndSel());
                break;
            case DownloadConstants.STATE_ERROR:
                currentRow.setStyle(ProgColorList.DOWNLOAD_ERROR.getCssBackgroundAndSel());
                break;
        }
    }

    private Callback<TableColumn<DownloadData, Integer>, TableCell<DownloadData, Integer>> cellFactoryNr
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

                if (item == DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item + "");
                }
            }
        };
        return cell;
    };

    private Callback<TableColumn<DownloadData, Integer>, TableCell<DownloadData, Integer>> cellFactoryFilmNr
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

                if (item == DownloadConstants.FILM_NUMBER_NOT_FOUND) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item + "");
                }
            }
        };
        return cell;
    };

    private Callback<TableColumn<DownloadData, Double>, TableCell<DownloadData, Double>> cellFactoryProgress
            = (final TableColumn<DownloadData, Double> param) -> {

        final ProgressBarTableCell<DownloadData> cell = new ProgressBarTableCell<>() {

            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    DownloadData download = getTableView().getItems().get(getIndex());
                    if (download.getProgramDownloadmanager()) {
                        final String text = DownloadConstants.getTextProgress(true, download.getState(), item.doubleValue());
                        Label label = new Label(text);
                        setGraphic(label);

                    } else if (item <= DownloadConstants.PROGRESS_STARTED || item >= DownloadConstants.PROGRESS_FINISHED) {
                        String text = DownloadConstants.getTextProgress(false, download.getState(), item.doubleValue());
                        Label label = new Label(text);
                        if (geoMelden.get() && download.getGeoBlocked()) {
                            // geogeblockt
                            label.setStyle("");
                            label.setStyle(ProgColorList.FILM_GEOBLOCK.getCssFontBold());
                        }
                        setGraphic(label);
                    }
                }
            }
        };
        return cell;
    };

    private Callback<TableColumn<DownloadData, Integer>, TableCell<DownloadData, Integer>> cellFactoryDuration
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

                if (item == 0) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item + "");
                }
            }
        };
        return cell;
    };
}
