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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import de.p2tools.mtplayer.gui.tools.table.TableAbo;
import javafx.scene.control.*;

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

        contextMenu.getItems().addAll(miOnOff, miDel, miChange, miNew,
                new SeparatorMenuItem(),
                miUndo);

        // Submenu "Filter"
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(getSubMenuFilter(abo));


        // Auswahl
        final MenuItem miSelectAll = new MenuItem("alles auswählen");
        miSelectAll.setOnAction(a -> tableView.getSelectionModel().selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> aboGuiController.invertSelection());
        final MenuItem miSort = new MenuItem("Abos alphabetisch sortieren");
        miSort.setOnAction(a -> progData.aboList.sortAlphabetically());

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miSelectAll, miSelection, miSort);

        contextMenu.getItems().add(new SeparatorMenuItem());
        CheckMenuItem smallTableRow = new CheckMenuItem("Nur kleine Button anzeigen");
        smallTableRow.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_ABO);
        CheckMenuItem toolTipTable = new CheckMenuItem("Infos beim Überfahren einer Zeile anzeigen");
        toolTipTable.selectedProperty().bindBidirectional(ProgConfig.ABO_GUI_SHOW_TABLE_TOOL_TIP);
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(e -> tableView.resetTable());
        contextMenu.getItems().addAll(smallTableRow, toolTipTable, resetTable);
    }

    private Menu getSubMenuFilter(AboData abo) {
        final MenuItem miAboAddFilter = new MenuItem("Neues Abo aus dem Film-Filter erstellen");
        miAboAddFilter.setOnAction(a -> AboListFactory.addNewAboFromFilterButton());
        final MenuItem miAboToFilter = new MenuItem("Filmfilter aus dem Abo setzen");
        miAboToFilter.setOnAction(a -> AboListFactory.setFilmFilterFromAbo());
        final MenuItem miFilterToAbo = new MenuItem("Abo aus dem Filmfilter setzen");
        miFilterToAbo.setOnAction(a -> AboListFactory.changeAboFromFilterButton());

        Menu mFilter = new Menu("Filmfilter - Abo");
        mFilter.setDisable(abo == null);
        mFilter.getItems().addAll(miAboAddFilter, miAboToFilter, miFilterToAbo);
        return mFilter;
    }
}

