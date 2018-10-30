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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.gui.tools.Table;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PTableViewTools;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

public class AboGuiController extends AnchorPane {
    TableView<Abo> table = new TableView<>();

    private final FilteredList<Abo> filteredAbos;
    private final SortedList<Abo> sortedAbos;

    private final ProgData progData;

    public AboGuiController() {
        progData = ProgData.getInstance();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        getChildren().addAll(scrollPane);

        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setTopAnchor(scrollPane, 0.0);

        scrollPane.setContent(table);

        filteredAbos = new FilteredList<>(progData.aboList, p -> true);
        sortedAbos = new SortedList<>(filteredAbos);
        setFilterProperty();

        initListener();
        initTable();
    }

    public void isShown() {
        progData.filmInfoDialogController.setFilm(null);
    }

    public int getAboCount() {
        return table.getItems().size();
    }

    public int getSelCount() {
        return table.getSelectionModel().getSelectedItems().size();
    }

    public void changeAbo() {
        ObservableList<Abo> lAbo = getSelList();
        progData.aboList.changeAbo(lAbo);
    }

    public void setAboActive(boolean on) {
        ObservableList<Abo> lAbo = getSelList();
        progData.aboList.setAboActive(lAbo, on);
    }

    public void deleteAbo() {
        ObservableList<Abo> lAbo = getSelList();
        progData.aboList.deleteAbo(lAbo);
    }


    public void neu() {
        progData.aboList.addAbo("Neu" /* Aboname */);
    }

    public void invertSelection() {
        PTableViewTools.invertSelection(table);
    }


    public void saveTable() {
        new Table().saveTable(table, Table.TABLE.ABO);
    }

    private ObservableList<Abo> getSelList() {
        final ObservableList<Abo> ret;
        ret = table.getSelectionModel().getSelectedItems();
        if (ret == null || ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    private void initListener() {
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            table.refresh();
        });
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
                changeAbo();
            }
        });

        table.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                makeContextMenu();
            }
        });

    }

    private void makeContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu);
        table.setContextMenu(contextMenu);

    }

    private void getMenu(ContextMenu contextMenu) {
        Abo abo = table.getSelectionModel().getSelectedItem();
        if (abo != null && abo.isActive()) {
            final MenuItem mbOff = new MenuItem("ausschalten");
            mbOff.setOnAction(e -> setAboActive(false));
            contextMenu.getItems().add(mbOff);
        } else {
            final MenuItem mbOn = new MenuItem("einschalten");
            mbOn.setOnAction(a -> setAboActive(true));
            contextMenu.getItems().add(mbOn);
        }
        final MenuItem miDel = new MenuItem("löschen");
        miDel.setOnAction(a -> deleteAbo());

        final MenuItem miChange = new MenuItem("ändern");
        miChange.setOnAction(a -> changeAbo());

        final MenuItem miNew = new MenuItem("neues Abo anlegen");
        miNew.setOnAction(a -> neu());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> invertSelection());

        contextMenu.getItems().addAll(miDel, miChange, miNew, new SeparatorMenuItem(), miSelection);

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(e -> new Table().resetTable(table, Table.TABLE.ABO));
        contextMenu.getItems().add(resetTable);

    }

    private void setFilterProperty() {
        ProgConfig.FILTER_ABO_SENDER.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_ABO_INFO.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
    }

    private void setFilter() {
        final String sender = ProgConfig.FILTER_ABO_SENDER.get();
        final String info = ProgConfig.FILTER_ABO_INFO.get().toLowerCase();

        filteredAbos.setPredicate(abo -> (sender.isEmpty() ? true : abo.getChannel().equals(sender)) &&
                (info.isEmpty() ? true : abo.getInfo().toLowerCase().contains(info))
        );

    }
}
