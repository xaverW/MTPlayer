/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.Table;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

public class AboGuiController extends AnchorPane {
    TableView<Abo> table = new TableView<>();

    private final FilteredList<Abo> filteredAbos;
    private final SortedList<Abo> sortedAbos;

    private final Daten daten;

    public AboGuiController() {
        daten = Daten.getInstance();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        getChildren().addAll(scrollPane);

        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setTopAnchor(scrollPane, 0.0);

        scrollPane.setContent(table);

        filteredAbos = new FilteredList<>(daten.aboList, p -> true);
        sortedAbos = new SortedList<>(filteredAbos);
        setFilterProperty();

        initTable();
    }

    public void isShown() {
        daten.filmInfosDialogController.set(null);
    }

    public int getSelCount() {
        return table.getSelectionModel().getSelectedItems().size();
    }

    public void aendern() {
        ObservableList<Abo> lAbo = getSelList();
        daten.aboList.changeAbo(lAbo);
    }

    public void einAus(boolean on) {
        ObservableList<Abo> lAbo = getSelList();
        daten.aboList.onOffAbo(lAbo, on);
    }

    public void loeschen() {
        ObservableList<Abo> lAbo = getSelList();
        daten.aboList.aboLoeschen(lAbo);
    }


    public void neu() {
        daten.aboList.addAbo("Neu" /* Aboname */);
    }

    public void invertSelection() {
        for (int i = 0; i < table.getItems().size(); ++i)
            if (table.getSelectionModel().isSelected(i)) {
                table.getSelectionModel().clearSelection(i);
            } else {
                table.getSelectionModel().select(i);
            }
    }


    public void saveTable() {
        new Table().saveTable(table, Table.TABLE.ABO);
    }

    private ObservableList<Abo> getSelList() {
        final ObservableList<Abo> ret;
        ret = table.getSelectionModel().getSelectedItems();
        if (ret == null || ret.isEmpty()) {
            new MTAlert().showInfoNoSelection();
        }
        return ret;
    }


    private void initTable() {
        table.setTableMenuButtonVisible(true);
        table.setEditable(false);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        new Table().setTable(table, Table.TABLE.ABO);

        table.setItems(sortedAbos);
        sortedAbos.comparatorProperty().bind(table.comparatorProperty());

        table.setOnMouseClicked(m -> {
            if (m.getButton().equals(MouseButton.PRIMARY) && m.getClickCount() == 2) {
                aendern();
            }
        });

        table.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                makeContextMenue();
            }
        });

    }

    private void makeContextMenue() {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu);
        table.setContextMenu(contextMenu);

    }

    private void getMenu(ContextMenu contextMenu) {
        Abo abo = table.getSelectionModel().getSelectedItem();
        if (abo != null && abo.getActive()) {
            final MenuItem mbOff = new MenuItem("ausschalten");
            mbOff.setOnAction(e -> einAus(false));
            contextMenu.getItems().add(mbOff);
        } else {
            final MenuItem mbOn = new MenuItem("einschalten");
            mbOn.setOnAction(a -> einAus(true));
            contextMenu.getItems().add(mbOn);
        }
        final MenuItem miDel = new MenuItem("löschen");
        miDel.setOnAction(a -> loeschen());

        final MenuItem miChange = new MenuItem("ändern");
        miChange.setOnAction(a -> aendern());

        final MenuItem miNew = new MenuItem("neues Abo anlegen");
        miNew.setOnAction(a -> neu());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> invertSelection());

        contextMenu.getItems().addAll(miDel, miChange, miNew, new SeparatorMenuItem(), miSelection);

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                new Table().resetTable(table, Table.TABLE.ABO);
            }
        });
        contextMenu.getItems().add(resetTable);

    }

    private void setFilterProperty() {
        Config.FILTER_ABO_SENDER.getStringProperty().addListener((observable, oldValue, newValue) -> {
            final String searchStr = newValue == null ? "" : newValue;
            filteredAbos.setPredicate(searchStr.isEmpty() ?
                    s -> true : s -> s.getSender().equals(searchStr));
        });
    }
}
