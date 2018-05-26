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

import de.mtplayer.mLib.tools.MDate;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.abo.Abo;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class TableAbo {

    public static TableColumn[] initAboColumn(TableView table) {
        table.getColumns().clear();

        final TableColumn<Abo, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));

        final TableColumn<Abo, Boolean> aktivColumn = new TableColumn<>("Aktiv");
        aktivColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        aktivColumn.setCellFactory(callbackAktiv);

        final TableColumn<Abo, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<Abo, String> resColumn = new TableColumn<>("Auflösung");
        resColumn.setCellValueFactory(new PropertyValueFactory<>("resolution"));

        final TableColumn<Abo, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));

        final TableColumn<Abo, Boolean> senderExaktColumn = new TableColumn<>("Sender exakt");
        senderExaktColumn.setCellValueFactory(new PropertyValueFactory<>("channelExact"));
        senderExaktColumn.setCellFactory(CheckBoxTableCell.forTableColumn(senderExaktColumn));

        final TableColumn<Abo, String> themaColumn = new TableColumn<>("Thema");
        themaColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        final TableColumn<Abo, Boolean> themaExaktColumn = new TableColumn<>("Thema exakt");
        themaExaktColumn.setCellValueFactory(new PropertyValueFactory<>("themeExact"));
        themaExaktColumn.setCellFactory(CheckBoxTableCell.forTableColumn(themaExaktColumn));

        final TableColumn<Abo, String> titelColumn = new TableColumn<>("Titel");
        titelColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        final TableColumn<Abo, String> themeTitleColumn = new TableColumn<>("Thema-Titel");
        themeTitleColumn.setCellValueFactory(new PropertyValueFactory<>("themeTitle"));

        final TableColumn<Abo, String> somewhereColumn = new TableColumn<>("irgendwo");
        somewhereColumn.setCellValueFactory(new PropertyValueFactory<>("somewhere"));

        final TableColumn<Abo, Integer> minColumn = new TableColumn<>("min");
        minColumn.setCellValueFactory(new PropertyValueFactory<>("minDuration"));

        final TableColumn<Abo, Integer> maxColumn = new TableColumn<>("max");
        maxColumn.setCellValueFactory(new PropertyValueFactory<>("maxDuration"));

        final TableColumn<Abo, String> destinationColumn = new TableColumn<>("Ziel");
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));

        final TableColumn<Abo, MDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        final TableColumn<Abo, String> psetColumn = new TableColumn<>("Set");
        psetColumn.setCellValueFactory(new PropertyValueFactory<>("pset"));

        return new TableColumn[]{
                nrColumn, aktivColumn, nameColumn, resColumn, senderColumn, senderExaktColumn,
                themaColumn, themaExaktColumn, titelColumn, themeTitleColumn,
                somewhereColumn, minColumn, maxColumn, destinationColumn, datumColumn, psetColumn};

    }

    private static Callback<TableColumn<Abo, Boolean>, TableCell<Abo, Boolean>> callbackAktiv =
            (final TableColumn<Abo, Boolean> param) -> {

                final CheckBoxTableCell<Abo, Boolean> cell = new CheckBoxTableCell<Abo, Boolean>() {

                    @Override
                    public void updateItem(Boolean item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        TableRow<Abo> currentRow = getTableRow();
                        if (currentRow != null) {
                            if (!item.booleanValue()) {
                                currentRow.setStyle(MTColor.ABO_SWITCHED_OFF.getCssBackground());
                            } else {
                                currentRow.setStyle("");
                            }
                        }
                    }
                };
                return cell;
            };


}
