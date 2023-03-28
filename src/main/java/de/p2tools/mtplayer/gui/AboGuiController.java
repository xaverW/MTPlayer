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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboConstants;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableAbo;
import de.p2tools.mtplayer.gui.tools.table.TableRowAbo;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.PTableFactory;
import de.p2tools.p2lib.guitools.pclosepane.PClosePaneH;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.Predicate;

public class AboGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private AboGuiInfoController aboGuiInfoController;
    private final TableAbo tableView;
    private final PClosePaneH pClosePaneH;

    private final ProgData progData;
    private boolean bound = false;
    private final FilteredList<AboData> filteredAbos;
    private final SortedList<AboData> sortedAbos;

    public AboGuiController() {
        progData = ProgData.getInstance();
        pClosePaneH = new PClosePaneH(ProgConfig.ABO_GUI_DIVIDER_ON, true);
        tableView = new TableAbo(Table.TABLE_ENUM.ABO);

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);

        aboGuiInfoController = new AboGuiInfoController();
        VBox.setVgrow(aboGuiInfoController, Priority.ALWAYS);
        pClosePaneH.getVBoxAll().getChildren().add(aboGuiInfoController);
        ProgConfig.ABO_GUI_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setInfoPane());

        filteredAbos = new FilteredList<>(progData.aboList, p -> true);
        sortedAbos = new SortedList<>(filteredAbos);

        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();
    }

    public void isShown() {
        tableView.requestFocus();
        FilmInfoDialogController.getInstance().setFilm(null);
    }

    public int getAboCount() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    public void aboEditDialog() {
        //Abos aus Tab Abo (Menü, Doppelklick Tabelle) ändern
        progData.aboList.aboEditDialog(getSelList());
    }

    public void setFilmFilterFromAbo() {
        Optional<AboData> abo = getSel();
        progData.actFilmFilterWorker.loadStoredFilterFromAbo(abo);
    }

    public void setAboFromFilmFilterButton() {
        Optional<AboData> abo = getSel();
        progData.aboList.changeAboFromFilterButton(abo, progData.actFilmFilterWorker.getActFilterSettings());
    }

    public void setAboActive(boolean on) {
        ObservableList<AboData> lAbo = getSelList();
        progData.aboList.setAboActive(lAbo, on);
    }

    public void deleteAbo() {
        ObservableList<AboData> lAbo = getSelList();
        progData.aboList.deleteAbo(lAbo);
    }

    public void selectAll() {
        tableView.getSelectionModel().selectAll();
    }

    public void invertSelection() {
        PTableFactory.invertSelection(tableView);
    }


    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.ABO);
    }

    private void initListener() {
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            tableView.refresh();
        });
        Listener.addListener(new Listener(Listener.EVEMT_SETDATA_CHANGED, AboGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                tableView.refresh();
            }
        });
    }

    private void setInfoPane() {
        pClosePaneH.setVisible(ProgConfig.ABO_GUI_DIVIDER_ON.getValue());
        pClosePaneH.setManaged(ProgConfig.ABO_GUI_DIVIDER_ON.getValue());

        if (!ProgConfig.ABO_GUI_DIVIDER_ON.getValue()) {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.ABO_GUI_DIVIDER);
            }

            splitPane.getItems().clear();
            splitPane.getItems().add(scrollPane);

        } else {
            bound = true;
            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPane, pClosePaneH);
            SplitPane.setResizableWithParent(pClosePaneH, false);
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.ABO_GUI_DIVIDER);
        }
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(sortedAbos);
        sortedAbos.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowAbo<AboData> row = new TableRowAbo<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    aboEditDialog();
                }
            });

            row.hoverProperty().addListener((observable) -> {
                final AboData aboData = (AboData) row.getItem();
                if (row.isHover() && aboData != null) {
                    aboGuiInfoController.setAbo(aboData);
                } else {
                    aboGuiInfoController.setAbo(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            aboGuiInfoController.setAbo(tableView.getSelectionModel().getSelectedItem());
        });

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<AboData> optionalAbo = getSel(false);
                AboData abo;
                if (optionalAbo.isPresent()) {
                    abo = optionalAbo.get();
                } else {
                    abo = null;
                }
                ContextMenu contextMenu = new AboGuiTableContextMenue(progData, this, tableView).getContextMenu(abo);
                tableView.setContextMenu(contextMenu);
            }
        });

        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (PTableFactory.SPACE.match(event)) {
                PTableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (PTableFactory.SPACE_SHIFT.match(event)) {
                PTableFactory.scrollVisibleRangeUp(tableView);
                event.consume();
            }
        });
    }

    private ObservableList<AboData> getSelList() {
        final ObservableList<AboData> ret = tableView.getSelectionModel().getSelectedItems();
        if (ret == null || ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    private Optional<AboData> getSel() {
        return getSel(true);
    }

    private Optional<AboData> getSel(boolean show) {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            if (show) {
                PAlert.showInfoNoSelection();
            }
            return Optional.empty();
        }
    }

    private void setFilterProperty() {
        ProgConfig.FILTER_ABO_CHANNEL.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_ABO_TYPE.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_ABO_NAME.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_ABO_DESCRIPTION.addListener((observable, oldValue, newValue) -> setFilter());
    }

//    private void setFilter() {
//        final String sender = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();
//        final String type = ProgConfig.FILTER_ABO_TYPE.getValueSafe();
//        final String name = ProgConfig.FILTER_ABO_NAME.getValueSafe().trim().toLowerCase();
//        final String description = ProgConfig.FILTER_ABO_DESCRIPTION.get().trim().toLowerCase();
//
//        filteredAbos.setPredicate(abo ->
//                (sender.isEmpty() || abo.getChannel().contains(sender)) &&
//                        (name.isEmpty() || abo.getName().toLowerCase().contains(name)) &&
//                        (description.isEmpty() || abo.getDescription().toLowerCase().contains(description)) &&
//                        (type.isEmpty() ||
//                                type.equals(AboConstants.ABO_ON) && abo.isActive() ||
//                                type.equals(AboConstants.ABO_OFF) && !abo.isActive())
//        );
//    }

    private void setFilter() {
        Predicate<AboData> predicate = downloadData -> true;
        final String sender = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();
        final String type = ProgConfig.FILTER_ABO_TYPE.getValueSafe();
        final String name = ProgConfig.FILTER_ABO_NAME.getValueSafe().trim();
        final String description = ProgConfig.FILTER_ABO_DESCRIPTION.get().trim();

        if (!sender.isEmpty()) {
            Filter filter = new Filter(sender, true);
            predicate = predicate.and(aboData -> FilterCheck.check(filter, aboData.getChannel()));
        }

        if (!type.isEmpty()) {
            predicate = predicate.and(aboData -> type.isEmpty() ||
                    type.equals(AboConstants.ABO_ON) && aboData.isActive() ||
                    type.equals(AboConstants.ABO_OFF) && !aboData.isActive());
        }
        if (!name.isEmpty()) {
            Filter filter = new Filter(name, true);
            predicate = predicate.and(aboData -> FilterCheck.check(filter, aboData.getName()));
        }
        if (!description.isEmpty()) {
            Filter filter = new Filter(description, true);
            predicate = predicate.and(aboData -> FilterCheck.check(filter, aboData.getDescription()));
        }
        filteredAbos.setPredicate(predicate);
    }
}
