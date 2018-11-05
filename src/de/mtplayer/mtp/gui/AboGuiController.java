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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

public class AboGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final AnchorPane infoPane = new AnchorPane();

    private AboGuiInfoController aboGuiInfoController;
    private final TableView<Abo> tableView = new TableView<>();

    private final ProgData progData;
    private boolean bound = false;
    private final FilteredList<Abo> filteredAbos;
    private final SortedList<Abo> sortedAbos;

    DoubleProperty splitPaneProperty = ProgConfig.ABO_GUI_DIVIDER.getDoubleProperty();
    BooleanProperty boolInfoOn = ProgConfig.ABO_GUI_DIVIDER_ON.getBooleanProperty();

    public AboGuiController() {
        progData = ProgData.getInstance();

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);

        aboGuiInfoController = new AboGuiInfoController(infoPane);
        boolInfoOn.addListener((observable, oldValue, newValue) -> setInfoPane());
        filteredAbos = new FilteredList<>(progData.aboList, p -> true);
        sortedAbos = new SortedList<>(filteredAbos);

        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();
    }

    public void isShown() {
        progData.filmInfoDialogController.setFilm(null);
    }

    public int getAboCount() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
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


    public void addNewAbo() {
        progData.aboList.addAbo("Neu" /* Aboname */);
    }

    public void invertSelection() {
        PTableViewTools.invertSelection(tableView);
    }


    public void saveTable() {
        new Table().saveTable(tableView, Table.TABLE.ABO);
    }

    private ObservableList<Abo> getSelList() {
        final ObservableList<Abo> ret;
        ret = tableView.getSelectionModel().getSelectedItems();
        if (ret == null || ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    private void initListener() {
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            tableView.refresh();
        });
    }

    private void setInfoPane() {
        infoPane.setVisible(boolInfoOn.getValue());
        infoPane.setManaged(boolInfoOn.getValue());

        if (!boolInfoOn.getValue()) {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(splitPaneProperty);
            }

            splitPane.getItems().clear();
            splitPane.getItems().add(scrollPane);

        } else {
            bound = true;
            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPane, infoPane);
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(splitPaneProperty);
        }
    }

    private void initTable() {
        tableView.setTableMenuButtonVisible(true);
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        new Table().setTable(tableView, Table.TABLE.ABO);

        tableView.setItems(sortedAbos);
        sortedAbos.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setOnMouseClicked(m -> {
            if (m.getButton().equals(MouseButton.PRIMARY) && m.getClickCount() == 2) {
                changeAbo();
            }
        });

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                makeContextMenu();
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            final Abo abo = tableView.getSelectionModel().getSelectedItem();
            aboGuiInfoController.setAbo(abo);
        });
    }


    private void makeContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu);
        tableView.setContextMenu(contextMenu);

    }

    private void getMenu(ContextMenu contextMenu) {
        Abo abo = tableView.getSelectionModel().getSelectedItem();
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
        miNew.setOnAction(a -> addNewAbo());

        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> invertSelection());

        contextMenu.getItems().addAll(miDel, miChange, miNew, new SeparatorMenuItem(), miSelection);

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(e -> new Table().resetTable(tableView, Table.TABLE.ABO));
        contextMenu.getItems().add(resetTable);

    }

    private void setFilterProperty() {
        ProgConfig.FILTER_ABO_SENDER.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_ABO_DESCRIPTION.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
    }

    private void setFilter() {
        final String sender = ProgConfig.FILTER_ABO_SENDER.get();
        final String description = ProgConfig.FILTER_ABO_DESCRIPTION.get().trim().toLowerCase();

        filteredAbos.setPredicate(abo -> (sender.isEmpty() ? true : abo.getChannel().equals(sender)) &&
                (description.isEmpty() ? true : abo.getDescription().toLowerCase().contains(description))
        );

    }
}
