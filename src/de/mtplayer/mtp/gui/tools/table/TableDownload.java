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

package de.mtplayer.mtp.gui.tools.table;

import de.mtplayer.mLib.tools.CheckBoxCell;
import de.mtplayer.mLib.tools.MDate;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;
import de.mtplayer.mtp.controller.data.download.DownloadSizeData;
import de.p2tools.p2Lib.guiTools.POpen;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class TableDownload {

    private final BooleanProperty geoMelden;
    private final BooleanProperty small;

    public TableDownload() {
        geoMelden = ProgConfig.SYSTEM_MARK_GEO.getBooleanProperty();
        small = ProgConfig.SYSTEM_SMALL_TABLE_ROW.getBooleanProperty();
    }

    public TableColumn[] initDownloadColumn(TableView table) {
        table.getColumns().clear();

        MTColor.FILM_GEOBLOCK.colorProperty().addListener((a, b, c) -> {
            table.refresh();
        });
        MTColor.DOWNLOAD_WAIT.colorProperty().addListener((a, b, c) -> {
            table.refresh();
        });
        MTColor.DOWNLOAD_RUN.colorProperty().addListener((a, b, c) -> {
            table.refresh();
        });
        MTColor.DOWNLOAD_FINISHED.colorProperty().addListener((a, b, c) -> {
            table.refresh();
        });
        MTColor.DOWNLOAD_ERROR.colorProperty().addListener((a, b, c) -> {
            table.refresh();
        });


        final TableColumn<Download, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        nrColumn.setCellFactory(cellFactoryNr);
        nrColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, Integer> filmNrColumn = new TableColumn<>("Filmnr");
        filmNrColumn.setCellValueFactory(new PropertyValueFactory<>("filmNr"));
        filmNrColumn.setCellFactory(cellFactoryFilmNr);
        filmNrColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, String> aboColumn = new TableColumn<>("Abo");
        aboColumn.setCellValueFactory(new PropertyValueFactory<>("aboName"));
        aboColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");


        // die zwei Spalten mit eigenen propertys
        final TableColumn<Download, Integer> startColumn = new TableColumn<>("");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("guiState"));
        startColumn.setCellFactory(cellFactoryState);
        startColumn.getStyleClass().add("alignCenter");

        final TableColumn<Download, Double> progressColumn = new TableColumn<>("Fortschritt"); //müssen sich unterscheiden!!
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("guiProgress"));
        progressColumn.setCellFactory(cellFactoryProgress);
        progressColumn.getStyleClass().add("alignCenterLeft");


        final TableColumn<Download, Integer> remainingColumn = new TableColumn<>("Restzeit");
        remainingColumn.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remainingColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, Integer> speedColumn = new TableColumn<>("Geschwindigkeit");
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("bandwidth"));
        speedColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, DownloadSizeData> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("downloadSize"));
        sizeColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, MDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("filmDate"));
        datumColumn.getStyleClass().add("alignCenter");

        final TableColumn<Download, String> timeColumn = new TableColumn<>("Zeit");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeColumn.getStyleClass().add("alignCenter");

        final TableColumn<Download, Integer> durationColumn = new TableColumn<>("Dauer");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, Boolean> hdColumn = new TableColumn<>("HD");
        hdColumn.setCellValueFactory(new PropertyValueFactory<>("hd"));
        hdColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
        hdColumn.getStyleClass().add("alignCenter");

        final TableColumn<Download, Boolean> utColumn = new TableColumn<>("UT");
        utColumn.setCellValueFactory(new PropertyValueFactory<>("ut"));
        utColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
        utColumn.getStyleClass().add("alignCenter");

        final TableColumn<Download, Boolean> geoColumn = new TableColumn<>("Geo");
        geoColumn.setCellValueFactory(new PropertyValueFactory<>("geoBlocked"));
        geoColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
        geoColumn.getStyleClass().add("alignCenter");

        final TableColumn<Download, String> artColumn = new TableColumn<>("Art");
        artColumn.setCellValueFactory(new PropertyValueFactory<>("art"));
        artColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, String> srcColumn = new TableColumn<>("Quelle");
        srcColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        srcColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, Boolean> placedBackColumn = new TableColumn<>("Zurückgestellt");
        placedBackColumn.setCellValueFactory(new PropertyValueFactory<>("placedBack"));
        placedBackColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
        placedBackColumn.getStyleClass().add("alignCenter");

        final TableColumn<Download, String> programColumn = new TableColumn<>("Programm");
        programColumn.setCellValueFactory(new PropertyValueFactory<>("program"));
        programColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, Integer> setColumn = new TableColumn<>("Programmset");
        setColumn.setCellValueFactory(new PropertyValueFactory<>("setData"));
        setColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, String> fileNameColumn = new TableColumn<>("Dateiname");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("destFileName"));
        fileNameColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Download, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("destPath"));
        pathColumn.getStyleClass().add("alignCenterLeft");

        addRowFact(table);

        return new TableColumn[]{
                nrColumn, filmNrColumn,
                aboColumn, senderColumn, themeColumn, titleColumn, startColumn,
                progressColumn, remainingColumn, speedColumn, sizeColumn,
                datumColumn, timeColumn, durationColumn,
                hdColumn, utColumn, geoColumn, artColumn, srcColumn, placedBackColumn,
                programColumn, setColumn, urlColumn, fileNameColumn, pathColumn
        };

    }

    private void addRowFact(TableView<Download> table) {

        table.setRowFactory(tableview -> new TableRow<Download>() {
            @Override
            public void updateItem(Download download, boolean empty) {
                super.updateItem(download, empty);

                if (download == null || empty) {
                    setStyle("");
                } else {

                    if (geoMelden.get() && download.getGeoBlocked()) {
                        // geogeblockt
                        for (int i = 0; i < getChildren().size(); i++) {
                            getChildren().get(i).setStyle("");
                            getChildren().get(i).setStyle(MTColor.FILM_GEOBLOCK.getCssFontBold());
                        }


                    } else {
                        for (int i = 0; i < getChildren().size(); i++) {
                            getChildren().get(i).setStyle("");
                        }
                    }


//                    int item = download.getState();
//                    switch (item) {
//                        case DownloadConstants.STATE_INIT:
//                        case DownloadConstants.STATE_STOPPED:
//                            setStyle("");
//                            break;
//                        case DownloadConstants.STATE_STARTED_WAITING:
//                            setStyle(MTColor.DOWNLOAD_WAIT.getCssBackground());
//                            break;
//                        case DownloadConstants.STATE_STARTED_RUN:
//                            setStyle(MTColor.DOWNLOAD_RUN.getCssBackground());
//                            break;
//                        case DownloadConstants.STATE_FINISHED:
//                            setStyle(MTColor.DOWNLOAD_FINISHED.getCssBackground());
//                            break;
//                        case DownloadConstants.STATE_ERROR:
//                            setStyle(MTColor.DOWNLOAD_ERROR.getCssBackground());
//                            break;
//                    }

                }

            }
        });

    }

    private Callback<TableColumn<Download, Integer>, TableCell<Download, Integer>> cellFactoryState
            = (final TableColumn<Download, Integer> param) -> {

        final TableCell<Download, Integer> cell = new TableCell<Download, Integer>() {

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
                    btnDownStart.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_DOWNLOAD_START));
                    btnDownStart.setOnAction((ActionEvent event) -> {
                        Download download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.startDownloads(download);
                    });

                    btnDownDel = new Button("");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_DOWNLOAD_DEL));
                    btnDownDel.setOnAction(event -> {
                        Download download = getTableView().getItems().get(getIndex());
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
                    btnDownStop.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_DOWNLOAD_STOP));
                    btnDownStop.setOnAction((ActionEvent event) -> {
                        Download download = getTableView().getItems().get(getIndex());
                        download.stopDownload();
                    });

                    btnDownDel = new Button("");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_DOWNLOAD_DEL));
                    btnDownDel.setOnAction(event -> {
                        Download download = getTableView().getItems().get(getIndex());
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
                    btnFilmStart.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_FILM_PLAY));
                    btnFilmStart.setOnAction((ActionEvent event) -> {
                        Download download = getTableView().getItems().get(getIndex());
                        POpen.playStoredFilm(download.getDestPathFile(),
                                ProgConfig.SYSTEM_PROG_PLAY_FILME.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
                    });

                    btnOpenDirectory = new Button();
                    btnOpenDirectory.setTooltip(new Tooltip("Ordner mit gespeichertem Film öffnen"));
                    btnOpenDirectory.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_DOWNLOAD_OPEN_DIR));
                    btnOpenDirectory.setOnAction((ActionEvent event) -> {
                        Download download = getTableView().getItems().get(getIndex());
                        POpen.openDir(download.getDestPath(),
                                ProgConfig.SYSTEM_PROG_OPEN_DIR.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
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
                    btnDownStart.setTooltip(new Tooltip("Download wider starten"));
                    btnDownStart.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_DOWNLOAD_START));
                    btnDownStart.setOnAction((ActionEvent event) -> {
                        Download download = getTableView().getItems().get(getIndex());
                        List<Download> list = new ArrayList<>();
                        list.add(download);
                        ProgData.getInstance().downloadList.startDownloads(list, true);
                    });

                    btnDownDel = new Button("");
                    btnDownDel.setTooltip(new Tooltip("Download löschen"));
                    btnDownDel.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_DOWNLOAD_DEL));
                    btnDownDel.setOnAction(event -> {
                        Download download = getTableView().getItems().get(getIndex());
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

    private void setCellStyle(TableCell<Download, Integer> cell, Integer item) {
        TableRow<Download> currentRow = cell.getTableRow();
        if (currentRow == null) {
            return;
        }
        switch (item) {
            case DownloadConstants.STATE_INIT:
            case DownloadConstants.STATE_STOPPED:
                currentRow.setStyle("");
                break;
            case DownloadConstants.STATE_STARTED_WAITING:
                currentRow.setStyle(MTColor.DOWNLOAD_WAIT.getCssBackgroundSel());
//                currentRow.setStyle("-fx-selection-bar: red;");
                break;
            case DownloadConstants.STATE_STARTED_RUN:
                currentRow.setStyle(MTColor.DOWNLOAD_RUN.getCssBackgroundSel());
                break;
            case DownloadConstants.STATE_FINISHED:
                currentRow.setStyle(MTColor.DOWNLOAD_FINISHED.getCssBackgroundSel());
                break;
            case DownloadConstants.STATE_ERROR:
                currentRow.setStyle(MTColor.DOWNLOAD_ERROR.getCssBackgroundSel());
                break;
        }
    }

    private Callback<TableColumn<Download, Integer>, TableCell<Download, Integer>> cellFactoryNr
            = (final TableColumn<Download, Integer> param) -> {

        final TableCell<Download, Integer> cell = new TableCell<Download, Integer>() {

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

    private Callback<TableColumn<Download, Integer>, TableCell<Download, Integer>> cellFactoryFilmNr
            = (final TableColumn<Download, Integer> param) -> {

        final TableCell<Download, Integer> cell = new TableCell<Download, Integer>() {

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

    private Callback<TableColumn<Download, Double>, TableCell<Download, Double>> cellFactoryProgress
            = (final TableColumn<Download, Double> param) -> {

        final ProgressBarTableCell<Download> cell = new ProgressBarTableCell<Download>() {

            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    Download download = getTableView().getItems().get(getIndex());
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
                            label.setStyle(MTColor.FILM_GEOBLOCK.getCssFontBold());
                        }
                        setGraphic(label);
                    }
                }
            }
        };
        return cell;
    };
}
