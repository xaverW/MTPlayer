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
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.filmFilter.CheckFilmFilter;
import de.p2tools.p2Lib.guiTools.PCheckBoxCell;
import de.p2tools.p2Lib.guiTools.PTableFactory;
import de.p2tools.p2Lib.tools.date.PDate;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class TableAbo extends PTable<AboData> {

    public TableAbo(Table.TABLE_ENUM table_enum) {
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

        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> {
            PTableFactory.refreshTable(this);
        });
        ProgColorList.ABO_SWITCHED_OFF.colorProperty().addListener((a, b, c) -> this.refresh());

        final TableColumn<AboData, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        nrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<AboData, Boolean> activColumn = new TableColumn<>("Aktiv");
        activColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        activColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
        activColumn.setCellFactory(callbackAktiv);
        activColumn.getStyleClass().add("alignCenter");

        final TableColumn<AboData, Integer> hitColumn = new TableColumn<>("Treffer");
        hitColumn.setCellValueFactory(new PropertyValueFactory<>("hit"));
        hitColumn.setCellFactory(callbackHits);
        hitColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<AboData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<AboData, String> resColumn = new TableColumn<>("Auflösung");
        resColumn.setCellValueFactory(new PropertyValueFactory<>("resolution"));
        resColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<AboData, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenter");

        final TableColumn<AboData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<AboData, Boolean> themeExactColumn = new TableColumn<>("Thema exakt");
        themeExactColumn.setCellValueFactory(new PropertyValueFactory<>("themeExact"));
        themeExactColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
        themeExactColumn.getStyleClass().add("alignCenter");

        final TableColumn<AboData, String> themeTitleColumn = new TableColumn<>("Thema-Titel");
        themeTitleColumn.setCellValueFactory(new PropertyValueFactory<>("themeTitle"));
        themeTitleColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<AboData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<AboData, String> somewhereColumn = new TableColumn<>("Irgendwo");
        somewhereColumn.setCellValueFactory(new PropertyValueFactory<>("somewhere"));
        somewhereColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<AboData, Integer> timeRange = new TableColumn<>("Zeitraum");
        timeRange.setCellFactory(cellFactoryMin);
        timeRange.setCellValueFactory(new PropertyValueFactory<>("timeRange"));
        timeRange.getStyleClass().add("alignCenter");

        final TableColumn<AboData, Integer> minColumn = new TableColumn<>("Min");
        minColumn.setCellFactory(cellFactoryMin);
        minColumn.setCellValueFactory(new PropertyValueFactory<>("minDurationMinute"));
        minColumn.getStyleClass().add("alignCenter");

        final TableColumn<AboData, Integer> maxColumn = new TableColumn<>("Max");
        maxColumn.setCellFactory(cellFactoryMax);
        maxColumn.setCellValueFactory(new PropertyValueFactory<>("maxDurationMinute"));
        maxColumn.getStyleClass().add("alignCenter");

        final TableColumn<AboData, String> startTimeColumn = new TableColumn<>("Startzeit");
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        startTimeColumn.getStyleClass().add("alignCenter");

        final TableColumn<AboData, String> destinationColumn = new TableColumn<>("Ziel");
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("aboSubDir"));
        destinationColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<AboData, PDate> datumColumn = new TableColumn<>("Letztes Abo");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        datumColumn.getStyleClass().add("alignCenter");

        final TableColumn<AboData, String> psetColumn = new TableColumn<>("Programmset");
        psetColumn.setCellValueFactory(new PropertyValueFactory<>("setData"));//liefert den Namen: toString()
        psetColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<AboData, PDate> genDateColumn = new TableColumn<>("Angelegt");
        genDateColumn.setCellValueFactory(new PropertyValueFactory<>("genDate"));
        genDateColumn.getStyleClass().add("alignCenter");

        getColumns().addAll(
                nrColumn, activColumn, hitColumn, nameColumn, resColumn, senderColumn,
                themeColumn, themeExactColumn, themeTitleColumn, titleColumn,
                somewhereColumn, timeRange, minColumn, maxColumn, startTimeColumn,
                destinationColumn, datumColumn, psetColumn, genDateColumn);

    }

    private static Callback<TableColumn<AboData, Boolean>, TableCell<AboData, Boolean>> callbackAktiv =
            (final TableColumn<AboData, Boolean> param) -> {

                final PCheckBoxCell<AboData, Boolean> cell = new PCheckBoxCell<>() {

                    @Override
                    public void updateItem(Boolean item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        initCell(item);
                    }
                };
                return cell;
            };

    private static Callback<TableColumn<AboData, Integer>, TableCell<AboData, Integer>> callbackHits =
            (final TableColumn<AboData, Integer> param) -> {

                final TableCell<AboData, Integer> cell = new TableCell<>() {

                    @Override
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        AboData aboData = getTableView().getItems().get(getIndex());
                        if (aboData.isActive()) {
                            setText(item.toString());
                        } else {
                            setText("");
                        }
                    }
                };
                return cell;
            };

    private static Callback<TableColumn<AboData, Integer>, TableCell<AboData, Integer>> cellFactoryMin
            = (final TableColumn<AboData, Integer> param) -> {

        final TableCell<AboData, Integer> cell = new TableCell<>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (item == 0) {
                    setText("alles");
                } else {
                    setText(item + "");
                }
            }
        };
        return cell;
    };

    private static Callback<TableColumn<AboData, Integer>, TableCell<AboData, Integer>> cellFactoryMax
            = (final TableColumn<AboData, Integer> param) -> {

        final TableCell<AboData, Integer> cell = new TableCell<>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (item == CheckFilmFilter.FILTER_DURATION_MAX_MINUTE) {
                    setText("alles");
                } else {
                    setText(item + "");
                }
            }
        };
        return cell;
    };
}
