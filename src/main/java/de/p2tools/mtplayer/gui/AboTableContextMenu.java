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


package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import de.p2tools.mtplayer.gui.tools.table.TableAbo;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class AboTableContextMenu {

    private final ProgData progData;
    private final AboGuiController aboGuiController;
    private final TableAbo tableView;

    public AboTableContextMenu(ProgData progData, AboGuiController aboGuiController, TableAbo tableView) {

        this.progData = progData;
        this.aboGuiController = aboGuiController;
        this.tableView = tableView;

    }

    public ContextMenu getContextMenu(AboData abo) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, abo);
        return contextMenu;
    }


    private void getMenu(ContextMenu contextMenu, AboData abo) {
        final MenuItem miOnOff;
        if (abo != null && abo.isActive()) {
            miOnOff = new MenuItem("Abos ausschalten");
            miOnOff.setOnAction(e -> AboListFactory.setAboActive(false));
        } else {
            miOnOff = new MenuItem("Abos einschalten");
            miOnOff.setOnAction(a -> AboListFactory.setAboActive(true));
        }
        final MenuItem miDel = new MenuItem("Abos löschen");
        miDel.setOnAction(a -> AboListFactory.deleteAbo());
        final MenuItem miChange = new MenuItem("Abos ändern");
        miChange.setOnAction(a -> AboListFactory.editAbo());
        final MenuItem miNew = new MenuItem("neues Abo anlegen");
        miNew.setOnAction(a -> AboListFactory.addNewAbo("Neu", "", "", ""));

        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen");
        miUndo.setOnAction(a -> progData.aboList.undoAbos());

        miUndo.setDisable(progData.aboList.getUndoList().isEmpty());
        miOnOff.setDisable(abo == null);
        miDel.setDisable(abo == null);
        miChange.setDisable(abo == null);

        contextMenu.getItems().addAll(miOnOff, miDel, miChange, miNew, miUndo);

        // Submenu "Filter"
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(getSubMenuFilter(abo));


        // Auswahl
        final MenuItem miSelectAll = new MenuItem("alles auswählen");
        miSelectAll.setOnAction(a -> tableView.getSelectionModel().selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> aboGuiController.invertSelection());
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(e -> tableView.resetTable());

        miSelectAll.setDisable(abo == null);
        miSelection.setDisable(abo == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miSelectAll, miSelection, resetTable);
    }

    private Menu getSubMenuFilter(AboData abo) {
        final MenuItem miAboToFilter = new MenuItem("Abo  -->  Filmfilter (Filmfilter aus Abo setzen)");
        miAboToFilter.setOnAction(a -> AboListFactory.setFilmFilterFromAbo());
        final MenuItem miFilterToAbo = new MenuItem("Filmfilter  -->  Abo (Abo aus Filmfilter setzen)");
        miFilterToAbo.setOnAction(a -> AboListFactory.setAboFromFilmFilter());

        Menu mFilter = new Menu("Filmfilter - Abo");
        mFilter.setDisable(abo == null);
        mFilter.getItems().addAll(miAboToFilter, miFilterToAbo);
        return mFilter;
    }
}

