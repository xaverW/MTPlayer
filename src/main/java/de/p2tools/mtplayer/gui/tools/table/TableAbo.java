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

import de.p2tools.mtplayer.controller.data.MTColor;
import de.p2tools.mtplayer.controller.data.abo.Abo;
import de.p2tools.mtplayer.tools.filmListFilter.FilmFilter;
import de.p2tools.p2Lib.guiTools.PCheckBoxCell;
import de.p2tools.p2Lib.tools.date.PDate;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class TableAbo {

    public static TableColumn[] initAboColumn(TableView table) {
        table.getColumns().clear();

        MTColor.ABO_SWITCHED_OFF.colorProperty().addListener((a, b, c) -> table.refresh());

        final TableColumn<Abo, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        nrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<Abo, Boolean> activColumn = new TableColumn<>("Aktiv");
        activColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        activColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
        activColumn.setCellFactory(callbackAktiv);
        activColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, Integer> hitColumn = new TableColumn<>("Treffer");
        hitColumn.setCellValueFactory(new PropertyValueFactory<>("hit"));
        hitColumn.setCellFactory(callbackHits);
        hitColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<Abo, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.getStyleClass().add("alignCenterLeft");

//        final TableColumn<Abo, String> descriptionColumn = new TableColumn<>("Beschreibung");
//        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
//        descriptionColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, String> resColumn = new TableColumn<>("Auflösung");
        resColumn.setCellValueFactory(new PropertyValueFactory<>("resolution"));
        resColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, Boolean> themeExactColumn = new TableColumn<>("Thema exakt");
        themeExactColumn.setCellValueFactory(new PropertyValueFactory<>("themeExact"));
        themeExactColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);
//        themeExactColumn.setCellFactory(CheckBoxTableCell.forTableColumn(themeExactColumn));
        themeExactColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, String> themeTitleColumn = new TableColumn<>("Thema-Titel");
        themeTitleColumn.setCellValueFactory(new PropertyValueFactory<>("themeTitle"));
        themeTitleColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, String> somewhereColumn = new TableColumn<>("irgendwo");
        somewhereColumn.setCellValueFactory(new PropertyValueFactory<>("somewhere"));
        somewhereColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, Integer> timeRange = new TableColumn<>("Zeitraum");
        timeRange.setCellFactory(cellFactoryMin);
        timeRange.setCellValueFactory(new PropertyValueFactory<>("timeRange"));
        timeRange.getStyleClass().add("alignCenter");

        final TableColumn<Abo, Integer> minColumn = new TableColumn<>("min");
        minColumn.setCellFactory(cellFactoryMin);
        minColumn.setCellValueFactory(new PropertyValueFactory<>("minDurationMinute"));
        minColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, Integer> maxColumn = new TableColumn<>("max");
        maxColumn.setCellFactory(cellFactoryMax);
        maxColumn.setCellValueFactory(new PropertyValueFactory<>("maxDurationMinute"));
        maxColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, String> startTimeColumn = new TableColumn<>("Startzeit");
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        startTimeColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, String> destinationColumn = new TableColumn<>("Ziel");
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("aboSubDir"));
        destinationColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, PDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        datumColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, String> psetColumn = new TableColumn<>("Programmset");
        psetColumn.setCellValueFactory(new PropertyValueFactory<>("setData"));
        psetColumn.getStyleClass().add("alignCenterLeft");

        return new TableColumn[]{
                nrColumn, activColumn, hitColumn, nameColumn, /*descriptionColumn,*/ resColumn, senderColumn,
                themeColumn, themeExactColumn, themeTitleColumn, titleColumn,
                somewhereColumn, timeRange, minColumn, maxColumn, startTimeColumn, destinationColumn, datumColumn, psetColumn};

    }

    private static Callback<TableColumn<Abo, Boolean>, TableCell<Abo, Boolean>> callbackAktiv =
            (final TableColumn<Abo, Boolean> param) -> {

                final PCheckBoxCell<Abo, Boolean> cell = new PCheckBoxCell<>() {

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

    private static Callback<TableColumn<Abo, Integer>, TableCell<Abo, Integer>> callbackHits =
            (final TableColumn<Abo, Integer> param) -> {

                final TableCell<Abo, Integer> cell = new TableCell<>() {

                    @Override
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        Abo abo = getTableView().getItems().get(getIndex());
                        if (abo.isActive()) {
                            setText(item.toString());
                        } else {
                            setText("");
                        }
                    }
                };
                return cell;
            };

    private static Callback<TableColumn<Abo, Integer>, TableCell<Abo, Integer>> cellFactoryMin
            = (final TableColumn<Abo, Integer> param) -> {

        final TableCell<Abo, Integer> cell = new TableCell<>() {

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

    private static Callback<TableColumn<Abo, Integer>, TableCell<Abo, Integer>> cellFactoryMax
            = (final TableColumn<Abo, Integer> param) -> {

        final TableCell<Abo, Integer> cell = new TableCell<>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (item == FilmFilter.FILTER_DURATION_MAX_MINUTE) {
                    setText("alles");
                } else {
                    setText(item + "");
                }
            }
        };
        return cell;
    };
}
