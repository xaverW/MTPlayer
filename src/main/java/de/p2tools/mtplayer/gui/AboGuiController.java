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
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.infoPane.AboInfoController;
import de.p2tools.mtplayer.gui.tools.PListener;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableAbo;
import de.p2tools.mtplayer.gui.tools.table.TableRowAbo;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.P2TableFactory;
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

import java.util.Optional;
import java.util.function.Predicate;

public class AboGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final TableAbo tableView;
    private final AboInfoController aboInfoController;

    private final ProgData progData;
    private boolean bound = false;
    private final FilteredList<AboData> filteredAbos;
    private final SortedList<AboData> sortedAbos;

    public AboGuiController() {
        progData = ProgData.getInstance();
        aboInfoController = new AboInfoController();
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

        ProgConfig.ABO_GUI_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setInfoPane());

        filteredAbos = new FilteredList<>(progData.aboList, p -> true);
        sortedAbos = new SortedList<>(filteredAbos);

        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();
    }

    public ObservableList<AboData> getSelList() {
        final ObservableList<AboData> ret = tableView.getSelectionModel().getSelectedItems();
        if (ret == null || ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    public Optional<AboData> getSel() {
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

    public void selectAll() {
        tableView.getSelectionModel().selectAll();
    }

    public void invertSelection() {
        P2TableFactory.invertSelection(tableView);
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.ABO);
    }

    private void initListener() {
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            tableView.refresh();
        });
        PListener.addListener(new PListener(PListener.EVENT_SET_DATA_CHANGED, AboGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                tableView.refresh();
            }
        });
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(sortedAbos);
        sortedAbos.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowAbo<AboData> row = new TableRowAbo<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    AboListFactory.editAbo();
                }
            });

            row.hoverProperty().addListener((observable) -> {
                final AboData aboData = (AboData) row.getItem();
                if (row.isHover() && aboData != null) {
                    aboInfoController.setAboInfos(aboData);
                } else {
                    aboInfoController.setAboInfos(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            aboInfoController.setAboInfos(tableView.getSelectionModel().getSelectedItem());
        });

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<AboData> optionalAbo = getSel(false);
                AboData abo = optionalAbo.orElse(null);
                ContextMenu contextMenu = new AboTableContextMenu(progData, this, tableView).getContextMenu(abo);
                tableView.setContextMenu(contextMenu);
            }
        });

        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (P2TableFactory.SPACE.match(event)) {
                P2TableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (P2TableFactory.SPACE_SHIFT.match(event)) {
                P2TableFactory.scrollVisibleRangeUp(tableView);
                event.consume();
            }
        });
    }

    private void setFilterProperty() {
        ProgConfig.FILTER_ABO_CHANNEL.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_ABO_TYPE.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_ABO_NAME.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_ABO_DESCRIPTION.addListener((observable, oldValue, newValue) -> setFilter());
    }

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

    private void setInfoPane() {
        if (!ProgConfig.ABO_GUI_DIVIDER_ON.getValue()) {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.ABO_GUI_DIVIDER);
            }

            splitPane.getItems().clear();
            splitPane.getItems().add(scrollPane);

        } else {
            bound = true;
            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPane, aboInfoController);
            SplitPane.setResizableWithParent(aboInfoController, false);
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.ABO_GUI_DIVIDER);
        }
    }
}
