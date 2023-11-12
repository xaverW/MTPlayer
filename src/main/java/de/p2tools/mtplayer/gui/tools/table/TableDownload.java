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

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.ptable.P2CellCheckBox;
import de.p2tools.p2lib.guitools.ptable.P2CellIntMax;
import de.p2tools.p2lib.guitools.ptable.P2CellIntNull;
import de.p2tools.p2lib.mtdownload.DownloadSizeData;
import de.p2tools.p2lib.tools.GermanStringIntSorter;
import de.p2tools.p2lib.tools.date.PDate;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Comparator;

public class TableDownload extends PTable<DownloadData> {
    int i = 0;

    public TableDownload(Table.TABLE_ENUM table_enum) {
        super(table_enum);
        this.table_enum = table_enum;
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
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> P2TableFactory.refreshTable(this));
        ProgColorList.FILM_GEOBLOCK.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_WAIT.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_RUN.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_FINISHED.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_ERROR.colorProperty().addListener((a, b, c) -> refresh());

        final TableColumn<DownloadData, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        nrColumn.setCellFactory(new P2CellIntMax().cellFactory);
        nrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<DownloadData, Integer> filmNrColumn = new TableColumn<>("Filmnr");
        filmNrColumn.setCellValueFactory(new PropertyValueFactory<>("filmNo"));
        filmNrColumn.setCellFactory(new P2CellIntMax().cellFactory);
        filmNrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<DownloadData, String> aboColumn = new TableColumn<>("Abo");
        aboColumn.setCellValueFactory(new PropertyValueFactory<>("aboName"));
        aboColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, Integer> buttonColumn = new TableColumn<>("");
        buttonColumn.setCellValueFactory(new PropertyValueFactory<>("guiState"));
        buttonColumn.setCellFactory(new CellDownloadButton().cellFactory);
        buttonColumn.getStyleClass().add("alignCenter");

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

        final TableColumn<DownloadData, Double> progressColumn = new TableColumn<>("Fortschritt"); //müssen sich unterscheiden!!
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("guiProgress"));
        progressColumn.setCellFactory(new CellDownloadProgress().cellFactory);
        progressColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, Integer> remainingColumn = new TableColumn<>("Restzeit");
        remainingColumn.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remainingColumn.setCellFactory(new CellDownloadRemaining<>().cellFactory);
        remainingColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, Long> speedColumn = new TableColumn<>("Geschwindigkeit");
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("bandwidth"));
        speedColumn.setCellFactory(new CellDownloadBandwidth<>().cellFactory);
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
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("filmTime"));
        timeColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Integer> durationColumn = new TableColumn<>("Dauer [min]");
        durationColumn.setCellFactory(new P2CellIntNull().cellFactory);
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinute"));
        durationColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, Boolean> hdColumn = new TableColumn<>("HD");
        hdColumn.setCellValueFactory(new PropertyValueFactory<>("hd"));
        hdColumn.setCellFactory(new P2CellCheckBox().cellFactory);
        hdColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Boolean> utColumn = new TableColumn<>("UT");
        utColumn.setCellValueFactory(new PropertyValueFactory<>("ut"));
        utColumn.setCellFactory(new P2CellCheckBox().cellFactory);
        utColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Boolean> geoColumn = new TableColumn<>("Geo");
        geoColumn.setCellValueFactory(new PropertyValueFactory<>("geoBlocked"));
        geoColumn.setCellFactory(new P2CellCheckBox().cellFactory);
        geoColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> artColumn = new TableColumn<>("Art");
        artColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        artColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, String> srcColumn = new TableColumn<>("Quelle");
        srcColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        srcColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, String> programColumn = new TableColumn<>("Programm");
        programColumn.setCellValueFactory(new PropertyValueFactory<>("programName"));
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
                aboColumn, buttonColumn, senderColumn, themeColumn, titleColumn,
                progressColumn, remainingColumn, speedColumn, startTimeColumn, sizeColumn,
                datumColumn, timeColumn, durationColumn,
                hdColumn, utColumn, geoColumn, artColumn, srcColumn, /*placedBackColumn,*/
                programColumn, setColumn, urlColumn, fileNameColumn, pathColumn);

        PListener.addListener(new PListener(PListener.EVENT_TIMER_SECOND, TableDownload.class.getSimpleName()) {
            @Override
            public void ping() {
                if (!getSortOrder().isEmpty() &&
                        (getSortOrder().get(0).equals(progressColumn) ||
                                getSortOrder().get(0).equals(remainingColumn) ||
                                getSortOrder().get(0).equals(speedColumn)
                        )) {
                    // dann sind es die Spalten die sich ändern
                    sort();
                    //todo ??
//                    System.out.println("sort " + i++);
                }
            }
        });
    }
}
