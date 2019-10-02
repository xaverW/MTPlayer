/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.gui.tools.table.Table;
import de.p2tools.p2Lib.tools.PException;
import javafx.scene.control.*;

public class AboGuiTableContextMenue {

    private final ProgData progData;
    private final AboGuiController aboGuiController;
    private final TableView tableView;

    public AboGuiTableContextMenue(ProgData progData, AboGuiController aboGuiController, TableView tableView) {

        this.progData = progData;
        this.aboGuiController = aboGuiController;
        this.tableView = tableView;

    }

    public ContextMenu getContextMenu(Abo abo) {
        if (abo == null) {
            PException.throwPException(97420202, AboGuiTableContextMenue.class.toString());
        }

        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, abo);
        return contextMenu;
    }


    private void getMenu(ContextMenu contextMenu, Abo abo) {

        final MenuItem mbOnOff;
        if (abo.isActive()) {
            mbOnOff = new MenuItem("Abo ausschalten");
            mbOnOff.setOnAction(e -> aboGuiController.setAboActive(false));
        } else {
            mbOnOff = new MenuItem("Abo einschalten");
            mbOnOff.setOnAction(a -> aboGuiController.setAboActive(true));
        }

        final MenuItem miDel = new MenuItem("Abo löschen");
        miDel.setOnAction(a -> aboGuiController.deleteAbo());

        final MenuItem miChange = new MenuItem("Abo ändern");
        miChange.setOnAction(a -> aboGuiController.changeAbo());

        final MenuItem miNew = new MenuItem("neues Abo anlegen");
        miNew.setOnAction(a -> aboGuiController.addNewAbo());


        // Submenu "Filter"
        final MenuItem miAboToFilter = new MenuItem("Abo  -->  Filmfilter (Filmfilter aus Abo setzen)");
        miAboToFilter.setOnAction(a -> aboGuiController.setFilmFilterFromAbo());

        final MenuItem miFilterToAbo = new MenuItem("Filmfilter  -->  Abo (Abo aus Filmfilter setzen)");
        miFilterToAbo.setOnAction(a -> aboGuiController.setAboFromFilmFilter());

        Menu mFilter = new Menu("Filmfilter - Abo");
        mFilter.getItems().addAll(miAboToFilter, miFilterToAbo);


        // Auswahl
        final MenuItem miSelectAll = new MenuItem("alles auswählen");
        miSelectAll.setOnAction(a -> tableView.getSelectionModel().selectAll());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> aboGuiController.invertSelection());

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(e -> new Table().resetTable(tableView, Table.TABLE.ABO));


        contextMenu.getItems().addAll(
                mbOnOff, miDel, miChange, miNew,

                new SeparatorMenuItem(),
                mFilter,

                new SeparatorMenuItem(),
                miSelectAll, miSelection, resetTable);
    }

}

