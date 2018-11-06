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
import de.mtplayer.mtp.gui.tools.Table;
import de.p2tools.p2Lib.tools.PException;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;

public class AboGuiContextMenue {

    private final ProgData progData;
    private final AboGuiController aboGuiController;
    private final TableView tableView;

    public AboGuiContextMenue(ProgData progData, AboGuiController aboGuiController, TableView tableView) {

        this.progData = progData;
        this.aboGuiController = aboGuiController;
        this.tableView = tableView;

    }

    public ContextMenu getContextMenu(Abo abo) {
        if (abo == null) {
            PException.throwPException(97420202, AboGuiContextMenue.class.toString());
        }

        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, abo);
        return contextMenu;
    }


    private void getMenu(ContextMenu contextMenu, Abo abo) {

        if (abo.isActive()) {
            final MenuItem mbOff = new MenuItem("ausschalten");
            mbOff.setOnAction(e -> aboGuiController.setAboActive(false));
            contextMenu.getItems().add(mbOff);
        } else {
            final MenuItem mbOn = new MenuItem("einschalten");
            mbOn.setOnAction(a -> aboGuiController.setAboActive(true));
            contextMenu.getItems().add(mbOn);
        }

        final MenuItem miDel = new MenuItem("löschen");
        miDel.setOnAction(a -> aboGuiController.deleteAbo());

        final MenuItem miChange = new MenuItem("ändern");
        miChange.setOnAction(a -> aboGuiController.changeAbo());

        final MenuItem miNew = new MenuItem("neues Abo anlegen");
        miNew.setOnAction(a -> aboGuiController.addNewAbo());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> aboGuiController.invertSelection());

        contextMenu.getItems().addAll(miDel, miChange, miNew, new SeparatorMenuItem(), miSelection);

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(e -> new Table().resetTable(tableView, Table.TABLE.ABO));
        contextMenu.getItems().add(resetTable);
    }

}

