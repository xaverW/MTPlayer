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
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.tools.filmListFilter.FilmFilter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class TableAbo {

    public static TableColumn[] initAboColumn(TableView table) {
        table.getColumns().clear();

        MTColor.ABO_SWITCHED_OFF.colorProperty().addListener((a, b, c) -> table.refresh());

        final TableColumn<Abo, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        nrColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, Boolean> activColumn = new TableColumn<>("Aktiv");
        activColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        activColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
        activColumn.setCellFactory(callbackAktiv);
        activColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, Integer> hitColumn = new TableColumn<>("Treffer");
        hitColumn.setCellValueFactory(new PropertyValueFactory<>("hit"));
        hitColumn.setCellFactory(callbackHits);
        hitColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, String> descriptionColumn = new TableColumn<>("Beschreibung");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, String> resColumn = new TableColumn<>("Auflösung");
        resColumn.setCellValueFactory(new PropertyValueFactory<>("resolution"));
        resColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, Boolean> senderExactColumn = new TableColumn<>("Sender exakt");
        senderExactColumn.setCellValueFactory(new PropertyValueFactory<>("channelExact"));
        senderExactColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
//        senderExactColumn.setCellFactory(CheckBoxTableCell.forTableColumn(senderExactColumn));
        senderExactColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, Boolean> themeExactColumn = new TableColumn<>("Thema exakt");
        themeExactColumn.setCellValueFactory(new PropertyValueFactory<>("themeExact"));
        themeExactColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
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
        timeRange.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, Integer> minColumn = new TableColumn<>("min");
        minColumn.setCellFactory(cellFactoryMin);
        minColumn.setCellValueFactory(new PropertyValueFactory<>("minDurationMinute"));
        minColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, Integer> maxColumn = new TableColumn<>("max");
        maxColumn.setCellFactory(cellFactoryMax);
        maxColumn.setCellValueFactory(new PropertyValueFactory<>("maxDurationMinute"));
        maxColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, String> destinationColumn = new TableColumn<>("Ziel");
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        destinationColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Abo, MDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        datumColumn.getStyleClass().add("alignCenter");

        final TableColumn<Abo, String> psetColumn = new TableColumn<>("Programmset");
        psetColumn.setCellValueFactory(new PropertyValueFactory<>("setData"));
        psetColumn.getStyleClass().add("alignCenterLeft");

        return new TableColumn[]{
                nrColumn, activColumn, hitColumn, nameColumn, descriptionColumn, resColumn, senderColumn, senderExactColumn,
                themeColumn, themeExactColumn, themeTitleColumn, titleColumn,
                somewhereColumn, timeRange, minColumn, maxColumn, destinationColumn, datumColumn, psetColumn};

    }

    private static Callback<TableColumn<Abo, Boolean>, TableCell<Abo, Boolean>> callbackAktiv =
            (final TableColumn<Abo, Boolean> param) -> {

                final CheckBoxCell<Abo, Boolean> cell = new CheckBoxCell<Abo, Boolean>() {

                    @Override
                    public void updateItem(Boolean item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        initCell(item);

                        TableRow<Abo> currentRow = getTableRow();
                        if (currentRow != null) {
                            if (!item.booleanValue()) {
                                currentRow.setStyle(MTColor.ABO_SWITCHED_OFF.getCssBackgroundSel());
                            } else {
                                currentRow.setStyle("");
                            }
                        }
                    }
                };
                return cell;
            };

    private static Callback<TableColumn<Abo, Integer>, TableCell<Abo, Integer>> callbackHits =
            (final TableColumn<Abo, Integer> param) -> {

                final TableCell<Abo, Integer> cell = new TableCell<Abo, Integer>() {

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

        final TableCell<Abo, Integer> cell = new TableCell<Abo, Integer>() {

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

        final TableCell<Abo, Integer> cell = new TableCell<Abo, Integer>() {

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
