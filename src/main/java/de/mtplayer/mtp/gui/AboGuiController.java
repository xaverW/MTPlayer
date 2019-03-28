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
import de.mtplayer.mtp.controller.data.abo.AboConstants;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.gui.tools.table.Table;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PTableViewTools;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.util.Optional;

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

    public void setFilmFilterFromAbo() {
        Optional<Abo> abo = getSel();
        progData.storedFilter.loadStoredFilterFromAbo(abo);
    }

    public void setAboFromFilmFilter() {
        Optional<Abo> abo = getSel();
        progData.aboList.changeAboFromFilter(abo, progData.storedFilter.getSelectedFilter());
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
        progData.aboList.addNewAbo("Neu" /* Aboname */);
    }

    public void invertSelection() {
        PTableViewTools.invertSelection(tableView);
    }


    public void saveTable() {
        new Table().saveTable(tableView, Table.TABLE.ABO);
    }

    private void initListener() {
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            tableView.refresh();
        });
        Listener.addListener(new Listener(Listener.EREIGNIS_SETDATA_CHANGED, AboGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                tableView.refresh();
            }
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
            SplitPane.setResizableWithParent(infoPane, false);
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
                final Optional<Abo> abo = getSel();
                if (abo.isPresent()) {
                    tableView.setContextMenu(new AboGuiTableContextMenue(progData, this, tableView).getContextMenu(abo.get()));
                } else {
                    tableView.setContextMenu(null);
                }
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            final Abo abo = tableView.getSelectionModel().getSelectedItem();
            aboGuiInfoController.setAbo(abo);
        });
    }

    private ObservableList<Abo> getSelList() {
        final ObservableList<Abo> ret = tableView.getSelectionModel().getSelectedItems();
        if (ret == null || ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    private Optional<Abo> getSel() {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            PAlert.showInfoNoSelection();
            return Optional.empty();
        }
    }

    private void setFilterProperty() {
        ProgConfig.FILTER_ABO_SENDER.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_ABO_KIND.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_ABO_DESCRIPTION.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
    }

    private void setFilter() {
        final String sender = ProgConfig.FILTER_ABO_SENDER.get();
        final String kind = ProgConfig.FILTER_ABO_KIND.get();
        final String description = ProgConfig.FILTER_ABO_DESCRIPTION.get().trim().toLowerCase();

        filteredAbos.setPredicate(abo ->
                (sender.isEmpty() || abo.getChannel().equals(sender)) &&
                        (description.isEmpty() || abo.getDescription().toLowerCase().contains(description)) &&
                        (kind.isEmpty() ||
                                kind.equals(AboConstants.ABO_ON) && abo.isActive() ||
                                kind.equals(AboConstants.ABO_OFF) && !abo.isActive())
        );
    }
}
