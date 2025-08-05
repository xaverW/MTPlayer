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

package de.p2tools.mtplayer.gui.configdialog.paneblacklist;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackList;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableBlacklist;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.*;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.List;

public class PaneBlackList {

    private SplitPane splitPane;
    private final TableBlacklist tableView;
    private final RadioButton rbBlack = new RadioButton("Blacklist");
    private final RadioButton rbWhite = new RadioButton("Whitelist");
    private final RadioButton rbOff = new RadioButton("Alles anzeigen");
    private final GridPane gridPane = new GridPane();

    private final P2MenuButton mbChannel;
    private final StringProperty mbChannelProp = new SimpleStringProperty();
    private final P2ToggleSwitch tgThemeExact = new P2ToggleSwitch("exakt:");
    private final TextField txtTheme = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtThemeTitle = new TextField();
    private final P2ToggleSwitch tglActive = new P2ToggleSwitch("Aktiv:");

    private final BlackList list;
    private final SortedList<BlackData> sortedList;
    private BlackData blackData = null;
    private final BooleanProperty blackDataChanged;
    private boolean selectedBlackDataChanged = false;
    private final boolean controlBlackListNotFilmFilter;
    private final PanelButton panelButton;
    private final PanelFilterGrid panelFilterGrid;

    private final Stage stage;
    private final ProgData progData;

    public PaneBlackList(Stage stage, ProgData progData, boolean controlBlackListNotFilmFilter, BooleanProperty blackDataChanged) {
        this.stage = stage;
        this.progData = progData;
        this.controlBlackListNotFilmFilter = controlBlackListNotFilmFilter;
        this.blackDataChanged = blackDataChanged;
        this.panelButton = new PanelButton();

        mbChannel = new P2MenuButton(mbChannelProp,
                ThemeListFactory.allChannelListFilm, true);

        if (controlBlackListNotFilmFilter) {
            tableView = new TableBlacklist(Table.TABLE_ENUM.BLACKLIST);
            Table.setTable(tableView);

            sortedList = progData.blackList.getSortedList();
            list = progData.blackList;
        } else {
            tableView = new TableBlacklist(Table.TABLE_ENUM.FILMFILTER);
            Table.setTable(tableView);

            sortedList = progData.filmListFilter.getSortedList();
            list = progData.filmListFilter;
        }
        panelFilterGrid = new PanelFilterGrid(tableView, list,
                controlBlackListNotFilmFilter ? progData.blackListFilterBlackList : progData.blackListFilterFilmList);
    }

    public void close() {
        list.getUndoList().clear();
        panelButton.close();
        panelFilterGrid.close();
        if (controlBlackListNotFilmFilter) {
            Table.saveTable(tableView, Table.TABLE_ENUM.BLACKLIST);
            splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.CONFIG_DIALOG_BLACKLIST_SPLITPANE);
        } else {
            Table.saveTable(tableView, Table.TABLE_ENUM.FILMFILTER);
            splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.CONFIG_DIALOG_FILMLIST_FILTER_SPLITPANE);
        }
    }

    public void make(Collection<TitledPane> result) {
        final VBox vBox = new VBox(P2LibConst.PADDING);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
        vBox.setAlignment(Pos.TOP_RIGHT);

        makeConfigBlackList(vBox);
        initTable();
        splitPane = panelFilterGrid.addFilterGrid(vBox, controlBlackListNotFilmFilter);
        panelButton.addButton(stage, vBox, tableView, blackDataChanged, list);
        PanelMoveButton.addMoveButton(stage, vBox, tableView, progData, controlBlackListNotFilmFilter, blackDataChanged, list);
        addConfigs(vBox);
        vBox.getChildren().add(ProgData.busy.getBusyHbox(Busy.BUSY_SRC.PANE_BLACKLIST));

        TitledPane tpBlack = new TitledPane(controlBlackListNotFilmFilter ? "Blacklist" : "Filme ausschließen", vBox);
        result.add(tpBlack);
        tpBlack.setMaxHeight(Double.MAX_VALUE);
    }

    private void makeConfigBlackList(VBox vBox) {
        final Button btnHelp = P2Button.helpButton(stage, "Blacklist / Whitelist",
                HelpText.BLACKLIST_WHITELIST);

        final ToggleGroup toggleGroup = new ToggleGroup();
        rbBlack.setToggleGroup(toggleGroup);
        rbWhite.setToggleGroup(toggleGroup);
        rbOff.setToggleGroup(toggleGroup);
        toggleGroup.selectedToggleProperty().addListener((u, o, n) -> setBlackProp());
        setRb();
        progData.filterWorkerFilm.getActFilterSettings().blacklistOnOffProperty().addListener((u, o, n) -> {
            setRb();
        });

        HBox hBox = new HBox(P2LibConst.PADDING);
        hBox.getChildren().addAll(rbBlack, rbWhite, rbOff, P2GuiTools.getHBoxGrower(), btnHelp);
        vBox.getChildren().add(hBox);
    }

    private void initTable() {
        tableView.setRowFactory(new P2RowFactory<>());
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                ContextMenu contextMenu = getContextMenu(list);
                tableView.setContextMenu(contextMenu);
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                List<TablePosition> cells = tableView.getSelectionModel().getSelectedCells();
                if (cells != null && !cells.isEmpty()) {
                    String cel = cells.get(0).getTableColumn().getText();
                    switch (cel) {
                        case "Sender":
                            mbChannel.requestFocus();
                            mbChannel.show();
                            break;
                        case "Thema":
                            txtTheme.requestFocus();
                            break;
                        case "Thema exakt":
                            tgThemeExact.setSelected(!tgThemeExact.isSelected());
                            break;
                        case "Titel":
                            txtTitle.requestFocus();
                            break;
                        case "Thema-Titel":
                            txtThemeTitle.requestFocus();
                            break;
                        case "Aktiv":
                            tglActive.setSelected(!tglActive.isSelected());
                            break;
                        default:
                            txtTheme.requestFocus();
                    }
                }
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setActBlackData());
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    private ContextMenu getContextMenu(BlackList list) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen");
        miUndo.setOnAction(a -> list.undoBlackData());
        miUndo.setDisable(list.getUndoList().isEmpty());
        contextMenu.getItems().addAll(miUndo);

        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(e -> tableView.resetTable());
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(resetTable);

        return contextMenu;
    }

    private void addConfigs(VBox vBox) {
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(2);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        int row = 0;
        gridPane.add(new Label("Sender:"), 0, row);
        gridPane.add(mbChannel, 1, row);

        gridPane.add(new Label("Thema:"), 0, ++row);
        gridPane.add(txtTheme, 1, row);
        gridPane.add(tgThemeExact, 2, row);

        gridPane.add(new Label("Titel:"), 0, ++row);
        gridPane.add(txtTitle, 1, row);

        gridPane.add(new Label("Thema-Titel:"), 0, ++row);
        gridPane.add(txtThemeTitle, 1, row);

        gridPane.add(tglActive, 0, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());
        gridPane.setDisable(true);
        gridPane.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        mbChannel.textProperty().addListener((observable, oldValue, newValue) -> setChanged());
        txtTheme.textProperty().addListener((observable, oldValue, newValue) -> setChanged());
        tgThemeExact.selectedProperty().addListener((observable, oldValue, newValue) -> setChanged());
        txtTitle.textProperty().addListener((observable, oldValue, newValue) -> setChanged());
        txtThemeTitle.textProperty().addListener((observable, oldValue, newValue) -> setChanged());
        tglActive.selectedProperty().addListener((observable, oldValue, newValue) -> setChanged());
        vBox.getChildren().add(gridPane);
    }

    private void setChanged() {
        if (selectedBlackDataChanged) {
            blackDataChanged.set(true);
        }
    }

    private void setRb() {
        if (controlBlackListNotFilmFilter) {
            //dann wird die BlackList gesteuert
            switch (progData.filterWorkerFilm.getActFilterSettings().getBlacklistOnOff()) {
                case BlacklistFilterFactory.BLACKLILST_FILTER_OFF:
                    //OFF
                    rbOff.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_ON:
                    //BLACK
                    rbBlack.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_INVERS:
                    //WHITE, also invers
                    rbWhite.setSelected(true);
                    break;
            }
        } else {
            //dann wird der FilmListFilter gesteuert
            switch (ProgConfig.SYSTEM_FILMLIST_FILTER.getValue()) {
                case BlacklistFilterFactory.BLACKLILST_FILTER_OFF:
                    //OFF
                    rbOff.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_ON:
                    //BLACK
                    rbBlack.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_INVERS:
                    //WHITE, also invers
                    rbWhite.setSelected(true);
                    break;
            }
        }
    }

    private void setBlackProp() {
        if (controlBlackListNotFilmFilter) {
            //dann wird die BlackList gesteuert
            if (rbBlack.isSelected()) {
                //BLACK
                progData.filterWorkerFilm.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_ON);
            } else if (rbWhite.isSelected()) {
                //WHITE, also invers
                progData.filterWorkerFilm.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_INVERS);
            } else {
                //OFF
                progData.filterWorkerFilm.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_OFF);
            }

        } else {
            //dann wird der FilmList Filter gesteuert
            if (rbBlack.isSelected()) {
                //BLACK
                ProgConfig.SYSTEM_FILMLIST_FILTER.setValue(BlacklistFilterFactory.BLACKLILST_FILTER_ON);
            } else if (rbWhite.isSelected()) {
                //WHITE, also invers
                ProgConfig.SYSTEM_FILMLIST_FILTER.setValue(BlacklistFilterFactory.BLACKLILST_FILTER_INVERS);
            } else {
                //OFF
                ProgConfig.SYSTEM_FILMLIST_FILTER.setValue(BlacklistFilterFactory.BLACKLILST_FILTER_OFF);
            }
        }
    }

    private void setActBlackData() {
        selectedBlackDataChanged = false;
        BlackData blackDataAct = tableView.getSelectionModel().getSelectedItem();
        if (blackDataAct == blackData) {
            return;
        }

        if (blackData != null) {
            mbChannelProp.unbindBidirectional(blackData.channelProperty());
            tgThemeExact.selectedProperty().unbindBidirectional(blackData.themeExactProperty());
            txtTheme.textProperty().unbindBidirectional(blackData.themeProperty());
            txtTitle.textProperty().unbindBidirectional(blackData.titleProperty());
            txtThemeTitle.textProperty().unbindBidirectional(blackData.themeTitleProperty());
            tglActive.selectedProperty().unbindBidirectional(blackData.activeProperty());
            txtTheme.setText("");
            txtTitle.setText("");
            txtThemeTitle.setText("");
        }

        blackData = blackDataAct;
        if (blackData != null) {
            mbChannelProp.bindBidirectional(blackData.channelProperty());
            txtTheme.textProperty().bindBidirectional(blackData.themeProperty());
            tgThemeExact.selectedProperty().bindBidirectional(blackData.themeExactProperty());
            txtTitle.textProperty().bindBidirectional(blackData.titleProperty());
            txtThemeTitle.textProperty().bindBidirectional(blackData.themeTitleProperty());
            tglActive.selectedProperty().bindBidirectional(blackData.activeProperty());
            selectedBlackDataChanged = true;
        }
    }
}
