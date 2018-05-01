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

package de.mtplayer.mtp.gui.tools;

import de.mtplayer.mLib.tools.CheckBoxCell;
import de.mtplayer.mLib.tools.MDate;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import de.mtplayer.mtp.controller.data.download.DownloadSizeData;
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

public class TableDownload {

    private final BooleanProperty geoMelden;

    public TableDownload() {
        geoMelden = ProgConfig.SYSTEM_MARK_GEO.getBooleanProperty();
    }

    public TableColumn[] initDownloadColumn(TableView table) {
        table.getColumns().clear();

        final TableColumn<Download, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        nrColumn.setCellFactory(cellFactoryNr);

        final TableColumn<Download, Integer> filmnrColumn = new TableColumn<>("Filmnr");
        filmnrColumn.setCellValueFactory(new PropertyValueFactory<>("filmNr"));
        filmnrColumn.setCellFactory(cellFactoryFilmNr);

        final TableColumn<Download, String> aboColumn = new TableColumn<>("Abo");
        aboColumn.setCellValueFactory(new PropertyValueFactory<>("aboName"));
        final TableColumn<Download, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        final TableColumn<Download, String> themaColumn = new TableColumn<>("Thema");
        themaColumn.setCellValueFactory(new PropertyValueFactory<>("thema"));
        final TableColumn<Download, String> titelColumn = new TableColumn<>("Titel");
        titelColumn.setCellValueFactory(new PropertyValueFactory<>("titel"));

        final TableColumn<Download, Integer> startColumn = new TableColumn<>("");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        startColumn.setCellFactory(cellFactoryStart);
        startColumn.getStyleClass().add("center");

        final TableColumn<Download, Double> fortschrittColumn = new TableColumn<>("Fortschritt"); //müssen sich unterscheiden!!
        fortschrittColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        fortschrittColumn.setCellFactory(cellFactoryProgress);

        final TableColumn<Download, Integer> restzeitColumn = new TableColumn<>("Restzeit");
        restzeitColumn.setCellValueFactory(new PropertyValueFactory<>("restzeit"));
        final TableColumn<Download, Integer> geschwindigkeitColumn = new TableColumn<>("Geschwindigkeit");
        geschwindigkeitColumn.setCellValueFactory(new PropertyValueFactory<>("bandbreite"));

        final TableColumn<Download, DownloadSizeData> groesseColumn = new TableColumn<>("Größe [MB]");
        groesseColumn.setCellValueFactory(new PropertyValueFactory<>("downloadSize"));
        final TableColumn<Download, MDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("filmDate"));
        final TableColumn<Download, String> zeitColumn = new TableColumn<>("Zeit");
        zeitColumn.setCellValueFactory(new PropertyValueFactory<>("zeit"));
        final TableColumn<Download, Integer> dauerColumn = new TableColumn<>("Dauer");
        dauerColumn.setCellValueFactory(new PropertyValueFactory<>("dauer"));

        final TableColumn<Download, Boolean> hdColumn = new TableColumn<>("HD");
        hdColumn.setCellValueFactory(new PropertyValueFactory<>("hd"));
        hdColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);

        final TableColumn<Download, Boolean> utColumn = new TableColumn<>("UT");
        utColumn.setCellValueFactory(new PropertyValueFactory<>("ut"));
        utColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);

        final TableColumn<Download, Boolean> pauseColumn = new TableColumn<>("Pause");
        pauseColumn.setCellValueFactory(new PropertyValueFactory<>("pause"));
        pauseColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);

        final TableColumn<Download, Boolean> geoColumn = new TableColumn<>("Geo");
        geoColumn.setCellValueFactory(new PropertyValueFactory<>("geoBlocked"));
        geoColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);

        final TableColumn<Download, String> artColumn = new TableColumn<>("Art");
        artColumn.setCellValueFactory(new PropertyValueFactory<>("art"));

        final TableColumn<Download, String> srcColumn = new TableColumn<>("Quelle");
        srcColumn.setCellValueFactory(new PropertyValueFactory<>("source"));

        final TableColumn<Download, Boolean> zurueckgestelltColumn = new TableColumn<>("Zurückgestellt");
        zurueckgestelltColumn.setCellValueFactory(new PropertyValueFactory<>("zurueckgestellt"));
        zurueckgestelltColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);

        final TableColumn<Download, String> programColumn = new TableColumn<>("Programm");
        programColumn.setCellValueFactory(new PropertyValueFactory<>("programm"));
        final TableColumn<Download, Integer> setColumn = new TableColumn<>("Programmset");
        setColumn.setCellValueFactory(new PropertyValueFactory<>("set"));
        final TableColumn<Download, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        final TableColumn<Download, String> dateinameColumn = new TableColumn<>("Dateiname");
        dateinameColumn.setCellValueFactory(new PropertyValueFactory<>("zielDateiname"));
        final TableColumn<Download, String> pfadColumn = new TableColumn<>("Pfad");
        pfadColumn.setCellValueFactory(new PropertyValueFactory<>("zielPfad"));

        addRowFact(table);

        return new TableColumn[]{
                nrColumn, filmnrColumn,
                aboColumn, senderColumn, themaColumn, titelColumn, startColumn,
                fortschrittColumn, restzeitColumn, geschwindigkeitColumn, groesseColumn,
                datumColumn, zeitColumn, dauerColumn,
                hdColumn, utColumn, pauseColumn, geoColumn, artColumn, srcColumn, zurueckgestelltColumn,
                programColumn, setColumn, urlColumn, dateinameColumn, pfadColumn
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
                }
            }
        });

    }

    private Callback<TableColumn<Download, Integer>, TableCell<Download, Integer>> cellFactoryStart
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

                if (item <= DownloadInfos.STATE_STOPED) {
                    btnDownStart = new Button("");
                    btnDownStart.setGraphic(new ImageView(Icons.IMAGE_TABLE_DOWNLOAD_START));

                    btnDownStart.setOnAction((ActionEvent event) -> {
                        Download download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.startDownloads(download);
                    });

                    btnDownDel = new Button("");
                    btnDownDel.setGraphic(new ImageView(Icons.IMAGE_TABLE_DOWNLOAD_DEL));

                    btnDownDel.setOnAction(event -> {
                        Download download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);
                    });
                    hbox.getChildren().addAll(btnDownStart, btnDownDel);
                    setGraphic(hbox);
                } else if (item < DownloadInfos.STATE_FINISHED) {
                    btnDownStop = new Button("");
                    btnDownStop.setGraphic(new ImageView(Icons.IMAGE_TABLE_DOWNLOAD_STOP));

                    btnDownStop.setOnAction((ActionEvent event) -> {
                        Download download = getTableView().getItems().get(getIndex());
                        download.stopDownload();
                    });

                    btnDownDel = new Button("");
                    btnDownDel.setGraphic(new ImageView(Icons.IMAGE_TABLE_DOWNLOAD_DEL));

                    btnDownDel.setOnAction(event -> {
                        Download download = getTableView().getItems().get(getIndex());
                        ProgData.getInstance().downloadList.delDownloads(download);
                    });
                    hbox.getChildren().addAll(btnDownStop, btnDownDel);
                    setGraphic(hbox);
                } else if (item == DownloadInfos.STATE_FINISHED) {
                    btnFilmStart = new Button("");
                    btnFilmStart.setGraphic(new ImageView(Icons.IMAGE_TABLE_FILM_PLAY));

                    btnFilmStart.setOnAction((ActionEvent event) -> {
                        Download download = getTableView().getItems().get(getIndex());
                        MTOpen.playStoredFilm(download.getZielPfadDatei());
                    });
                    hbox.getChildren().addAll(btnFilmStart);
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
            case DownloadInfos.STATE_INIT:
            case DownloadInfos.STATE_STOPED:
                currentRow.setStyle("");
                break;
            case DownloadInfos.STATE_STARTED_WAITING:
                currentRow.setStyle(MTColor.DOWNLOAD_WAIT.getCssBackground());
                break;
            case DownloadInfos.STATE_STARTED_RUN:
                currentRow.setStyle(MTColor.DOWNLOAD_RUN.getCssBackground());
                break;
            case DownloadInfos.STATE_FINISHED:
                currentRow.setStyle(MTColor.DOWNLOAD_FINISHED.getCssBackground());
                break;
            case DownloadInfos.STATE_ERROR:
                currentRow.setStyle(MTColor.DOWNLOAD_ERROR.getCssBackground());
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

                if (item == DownloadInfos.DOWNLOAD_NUMBER_NOT_STARTED) {
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

                if (item == DownloadInfos.FILM_NUMBER_NOT_FOUND) {
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
                    if (download.isProgrammDownloadmanager()) {
                        final String text = DownloadInfos.getTextProgress(true, download.getState(), item.doubleValue());
                        Label label = new Label(text);
                        setGraphic(label);
                    } else if (item <= DownloadInfos.PROGRESS_GESTARTET || item >= DownloadInfos.PROGRESS_FERTIG) {
                        String text = DownloadInfos.getTextProgress(false, download.getState(), item.doubleValue());
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
