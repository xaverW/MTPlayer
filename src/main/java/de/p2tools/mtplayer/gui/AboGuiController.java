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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboConstants;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.infoPane.InfoPaneFactory;
import de.p2tools.mtplayer.gui.infoPane.PaneAboInfo;
import de.p2tools.mtplayer.gui.infoPane.PaneAboInfoList;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableAbo;
import de.p2tools.mtplayer.gui.tools.table.TableRowAbo;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneController;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneDto;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import de.p2tools.p2lib.guitools.ptable.P2TableFactory;
import de.p2tools.p2lib.guitools.table.P2RowMoveFactory;
import de.p2tools.p2lib.mediathek.filter.Filter;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import de.p2tools.p2lib.p2event.P2Listener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class AboGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    public final TableAbo tableView;
    private final ProgData progData;
    private final FilteredList<AboData> filteredAbos;
    private final SortedList<AboData> sortedAbos;

    private final PaneAboInfo paneAboInfo;
    private final PaneAboInfoList paneAboInfoList;
    private final P2ClosePaneController infoController;
    private final BooleanProperty boundInfo = new SimpleBooleanProperty(false);

    public AboGuiController() {
        progData = ProgData.getInstance();
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

        paneAboInfo = new PaneAboInfo();
        paneAboInfoList = new PaneAboInfoList();

        filteredAbos = new FilteredList<>(progData.aboList, p -> true);
        sortedAbos = new SortedList<>(filteredAbos);

        ArrayList<P2ClosePaneDto> list = new ArrayList<>();
        P2ClosePaneDto infoDto = new P2ClosePaneDto(paneAboInfo,
                ProgConfig.ABO__INFO_PANE_IS_RIP,
                ProgConfig.ABO__INFO__DIALOG_SIZE, MTPlayerController.TAB_ABO_ON,
                "Beschreibung", "Beschreibung", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneAboInfoList,
                ProgConfig.ABO__LIST_PANE_IS_RIP,
                ProgConfig.ABO__LIST_DIALOG_SIZE, MTPlayerController.TAB_ABO_ON,
                "Infos", "Infos", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);
        infoController = new P2ClosePaneController(list, ProgConfig.ABO__INFO_IS_SHOWING);

        ProgConfig.ABO__INFO_IS_SHOWING.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.ABO__INFO_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.ABO__LIST_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());

        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();
    }

    public ObservableList<AboData> getSelList() {
        final ObservableList<AboData> ret = tableView.getSelectionModel().getSelectedItems();
        if (ret == null || ret.isEmpty()) {
            P2Alert.showInfoNoSelection();
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
                P2Alert.showInfoNoSelection();
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
//        PListener.addListener(new PListener(PListener.EVENT_SET_DATA_CHANGED, AboGuiController.class.getSimpleName()) {
//            @Override
//            public void pingFx() {
//                tableView.refresh();
//            }
//        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_SET_DATA_CHANGED) {
            @Override
            public void pingGui() {
                tableView.refresh();
            }
        });
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(sortedAbos);
        sortedAbos.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(new P2RowMoveFactory<>(tv -> {
            TableRowAbo<AboData> row = new TableRowAbo<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 &&
                        !row.isEmpty()) {
                    AboListFactory.editAbo();
                }
            });

            row.hoverProperty().addListener((observable) -> {
                final AboData aboData = row.getItem();
                if (row.isHover() && aboData != null) {
                    if (InfoPaneFactory.paneIsVisible(MTPlayerController.TAB_ABO_ON, paneAboInfo)) {
                        paneAboInfo.setAbo(aboData);
                    }
                } else {
                    if (InfoPaneFactory.paneIsVisible(MTPlayerController.TAB_ABO_ON, paneAboInfo)) {
                        paneAboInfo.setAbo(tableView.getSelectionModel().getSelectedItem());
                    }
                }
            });
            return row;
        }, progData.aboList));

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (InfoPaneFactory.paneIsVisible(MTPlayerController.TAB_ABO_ON, paneAboInfo)) {
                paneAboInfo.setAbo(tableView.getSelectionModel().getSelectedItem());
            }
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
        ProgConfig.FILTER_ABO_SEARCH_TEXT.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_ABO_DESCRIPTION.addListener((observable, oldValue, newValue) -> setFilter());
    }

    private void setFilter() {
        final String sender = ProgConfig.FILTER_ABO_CHANNEL.getValueSafe();
        final String type = ProgConfig.FILTER_ABO_TYPE.getValueSafe();
        final String name = ProgConfig.FILTER_ABO_NAME.getValueSafe().trim();
        final String searchText = ProgConfig.FILTER_ABO_SEARCH_TEXT.getValueSafe().trim();
        final String description = ProgConfig.FILTER_ABO_DESCRIPTION.get().trim();

        Predicate<AboData> predicate = aboData -> true;
        if (!sender.isEmpty()) {
            Filter filter = new Filter(sender, true);
            predicate = predicate.and(aboData -> FilterCheck.check(filter, aboData.getChannel()));
        }
        if (!type.isEmpty()) {
            predicate = predicate.and(aboData -> type.equals(AboConstants.ABO_ON) && aboData.isActive() ||
                    type.equals(AboConstants.ABO_OFF) && !aboData.isActive());
        }
        if (!name.isEmpty()) {
            Filter filter = new Filter(name, true);
            predicate = predicate.and(aboData -> FilterCheck.check(filter, aboData.getName()));
        }
        if (!searchText.isEmpty()) {
            Filter filter = new Filter(searchText, true);
            predicate = predicate.and(aboData -> FilterCheck.check(filter, aboData.getTheme()) ||
                    FilterCheck.check(filter, aboData.getThemeTitle()) ||
                    FilterCheck.check(filter, aboData.getTitle()) ||
                    FilterCheck.check(filter, aboData.getSomewhere())
            );
        }
        if (!description.isEmpty()) {
            Filter filter = new Filter(description, true);
            predicate = predicate.and(aboData -> FilterCheck.check(filter, aboData.getDescription()));
        }
        filteredAbos.setPredicate(predicate);
    }

    private void setInfoPane() {
        P2ClosePaneFactory.setSplit(boundInfo, splitPane,
                infoController, false, scrollPane,
                ProgConfig.ABO__INFO_DIVIDER, ProgConfig.ABO__INFO_IS_SHOWING);
    }
}
