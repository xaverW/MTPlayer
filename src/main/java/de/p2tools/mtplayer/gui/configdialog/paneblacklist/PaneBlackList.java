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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackList;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.load.LoadAudioFactory;
import de.p2tools.mtplayer.controller.load.LoadFilmFactory;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableBlacklist;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.pcbo.P2CboCheckBoxListString;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.guitools.table.P2RowFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.List;

public class PaneBlackList {

    private final RadioButton rbBlackFilm = new RadioButton("Blacklist");
    private final RadioButton rbWhiteFilm = new RadioButton("Whitelist");
    private final RadioButton rbOffFilm = new RadioButton("Alles anzeigen");

    private final RadioButton rbBlackAudio = new RadioButton("Blacklist");
    private final RadioButton rbWhiteAudio = new RadioButton("Whitelist");
    private final RadioButton rbOffAudio = new RadioButton("Alles anzeigen");

    public final RadioButton rbFilm = new RadioButton("Film");
    public final RadioButton rbAudio = new RadioButton("Audio");
    public final RadioButton rbFilmAudio = new RadioButton("Film und Audio");

    private SplitPane splitPane;
    private final TableBlacklist tableView;
    private final GridPane gridPane = new GridPane();
    private final P2ToggleSwitch tglActive = new P2ToggleSwitch("Aktiv:");

    private final P2CboCheckBoxListString mbChannel;
    private final StringProperty mbChannelProp = new SimpleStringProperty();
    private final P2ToggleSwitch tgThemeExact = new P2ToggleSwitch("exakt:");
    private final TextField txtTheme = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtThemeTitle = new TextField();

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
    private final ToggleGroup toggleGroupList = new ToggleGroup();
    private final ChangeListener<Toggle> changeListener = (u, o, n) -> {
        if (blackData != null) {
            if (rbFilmAudio.isSelected()) {
                blackData.setList(ProgConst.LIST_FILM_AUDIO);
            } else if (rbFilm.isSelected()) {
                blackData.setList(ProgConst.LIST_FILM);
            } else {
                blackData.setList(ProgConst.LIST_AUDIO);
            }
        }
    };

    public PaneBlackList(Stage stage, ProgData progData, boolean controlBlackListNotFilmFilter, BooleanProperty blackDataChanged) {
        this.stage = stage;
        this.progData = progData;
        this.controlBlackListNotFilmFilter = controlBlackListNotFilmFilter;
        this.blackDataChanged = blackDataChanged;
        this.panelButton = new PanelButton();

        mbChannel = new P2CboCheckBoxListString(mbChannelProp,
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

        Button btnLoadAudio = new Button("_Audioliste mit diesen Einstellungen neu laden");
        btnLoadAudio.setTooltip(new Tooltip("Eine komplette neue Audioliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Audioliste werden so sofort übernommen"));
        btnLoadAudio.setOnAction(event -> {
            LoadAudioFactory.loadAudioListFromWeb(true, true);
        });
        btnLoadAudio.setMaxWidth(Double.MAX_VALUE);
        btnLoadAudio.disableProperty().bind(ProgConfig.SYSTEM_USE_AUDIOLIST.not());
        HBox.setHgrow(btnLoadAudio, Priority.ALWAYS);

        Button btnLoadFilm = new Button("_Filmliste mit diesen Einstellungen neu laden");
        btnLoadFilm.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoadFilm.setOnAction(event -> {
            LoadFilmFactory.loadFilmListFromWeb(true, true);
        });
        btnLoadFilm.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnLoadFilm, Priority.ALWAYS);

        HBox hBoxBtn = new HBox(5);
        hBoxBtn.setAlignment(Pos.CENTER_RIGHT);
        hBoxBtn.getChildren().addAll(btnLoadAudio, btnLoadFilm);

        if (!controlBlackListNotFilmFilter) {
            vBox.getChildren().addAll(P2GuiTools.getVBoxGrower(), hBoxBtn);
        }

        vBox.getChildren().add(ProgData.busy.getBusyHbox(Busy.BUSY_SRC.PANE_BLACKLIST));

        TitledPane tpBlack = new TitledPane(controlBlackListNotFilmFilter ? "Blacklist" : "Beiträge ausschließen", vBox);
        result.add(tpBlack);
        tpBlack.setMaxHeight(Double.MAX_VALUE);
    }

    private void makeConfigBlackList(VBox vBox) {
        final Button btnHelp = P2Button.helpButton(stage, "Blacklist / Whitelist",
                HelpText.BLACKLIST_WHITELIST);

        final ToggleGroup toggleGroupFilm = new ToggleGroup();
        rbBlackFilm.setToggleGroup(toggleGroupFilm);
        rbWhiteFilm.setToggleGroup(toggleGroupFilm);
        rbOffFilm.setToggleGroup(toggleGroupFilm);
        toggleGroupFilm.selectedToggleProperty().addListener((u, o, n) -> setBlackPropOnOff());

        final ToggleGroup toggleGroupAudio = new ToggleGroup();
        rbBlackAudio.setToggleGroup(toggleGroupAudio);
        rbWhiteAudio.setToggleGroup(toggleGroupAudio);
        rbOffAudio.setToggleGroup(toggleGroupAudio);
        toggleGroupAudio.selectedToggleProperty().addListener((u, o, n) -> setBlackPropOnOff());

        rbFilmAudio.setToggleGroup(toggleGroupList);
        rbFilm.setToggleGroup(toggleGroupList);
        rbAudio.setToggleGroup(toggleGroupList);

        progData.filterWorkerFilm.getActFilterSettings().blacklistOnOffProperty().addListener((u, o, n) -> {
            setBlackOnOff();
        });
        progData.filterWorkerAudio.getActFilterSettings().blacklistOnOffProperty().addListener((u, o, n) -> {
            setBlackOnOff();
        });
        setBlackOnOff();

        GridPane gridPaneBlack = new GridPane();
        gridPaneBlack.setHgap(20);
        gridPaneBlack.setVgap(10);
        gridPaneBlack.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow());

        GridPane.setValignment(btnHelp, VPos.TOP);
        GridPane.setHalignment(btnHelp, HPos.RIGHT);
        gridPaneBlack.add(P2Text.getTextBold("Filme:"), 0, 0);
        gridPaneBlack.add(rbBlackFilm, 1, 0);
        gridPaneBlack.add(rbWhiteFilm, 2, 0);
        gridPaneBlack.add(rbOffFilm, 3, 0);
        gridPaneBlack.add(btnHelp, 4, 0, 1, 2);

        if (controlBlackListNotFilmFilter) {
            gridPaneBlack.add(P2Text.getTextBold("Audios:"), 0, 1);
            gridPaneBlack.add(rbBlackAudio, 1, 1);
            gridPaneBlack.add(rbWhiteAudio, 2, 1);
            gridPaneBlack.add(rbOffAudio, 3, 1);
        }
        vBox.getChildren().add(gridPaneBlack);
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
                        case "Liste":
                            if (rbFilmAudio.isSelected()) {
                                rbFilm.setSelected(true);
                            } else if (rbFilm.isSelected()) {
                                rbAudio.setSelected(true);
                            } else {
                                rbFilmAudio.setSelected(true);
                            }
                            break;
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

        HBox hBoxChannel = new HBox(P2LibConst.SPACING_HBOX);
        hBoxChannel.setAlignment(Pos.CENTER_LEFT);
        hBoxChannel.getChildren().addAll(mbChannel, P2GuiTools.getVDistance(50),
                tglActive, P2GuiTools.getHBoxGrower(), rbFilmAudio, rbFilm, rbAudio);
        gridPane.add(new Label("Sender:"), 0, row);
        gridPane.add(hBoxChannel, 1, row);

        gridPane.add(new Label("Thema:"), 0, ++row);
        HBox hBoxTheme = new HBox(P2LibConst.SPACING_HBOX);
        hBoxTheme.getChildren().addAll(txtTheme, tgThemeExact);
        HBox.setHgrow(txtTheme, Priority.ALWAYS);
        gridPane.add(hBoxTheme, 1, row);

        gridPane.add(new Label("Titel:"), 0, ++row);
        gridPane.add(txtTitle, 1, row);

        gridPane.add(new Label("Thema-Titel:"), 0, ++row);
        gridPane.add(txtThemeTitle, 1, row);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());
        gridPane.setDisable(true);
        gridPane.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        toggleGroupList.selectedToggleProperty().addListener((o, u, n) -> setChanged());
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

    private void setBlackOnOff() {
        if (controlBlackListNotFilmFilter) {
            //dann wird die BlackList gesteuert
            switch (progData.filterWorkerFilm.getActFilterSettings().getBlacklistOnOff()) {
                case BlacklistFilterFactory.BLACKLILST_FILTER_OFF:
                    //OFF
                    rbOffFilm.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_ON:
                    //BLACK
                    rbBlackFilm.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_INVERS:
                    //WHITE, also invers
                    rbWhiteFilm.setSelected(true);
                    break;
            }
            switch (progData.filterWorkerAudio.getActFilterSettings().getBlacklistOnOff()) {
                case BlacklistFilterFactory.BLACKLILST_FILTER_OFF:
                    //OFF
                    rbOffAudio.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_ON:
                    //BLACK
                    rbBlackAudio.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_INVERS:
                    //WHITE, also invers
                    rbWhiteAudio.setSelected(true);
                    break;
            }

        } else {
            //dann wird der FilmListFilter gesteuert
            switch (ProgConfig.SYSTEM_FILMLIST_FILTER.getValue()) {
                case BlacklistFilterFactory.BLACKLILST_FILTER_OFF:
                    //OFF
                    rbOffFilm.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_ON:
                    //BLACK
                    rbBlackFilm.setSelected(true);
                    break;
                case BlacklistFilterFactory.BLACKLILST_FILTER_INVERS:
                    //WHITE, also invers
                    rbWhiteFilm.setSelected(true);
                    break;
            }
        }
    }

    private void setBlackPropOnOff() {
        if (controlBlackListNotFilmFilter) {
            //dann wird die BlackList gesteuert
            if (rbBlackFilm.isSelected()) {
                //BLACK
                progData.filterWorkerFilm.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_ON);
            } else if (rbWhiteFilm.isSelected()) {
                //WHITE, also invers
                progData.filterWorkerFilm.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_INVERS);
            } else {
                //OFF
                progData.filterWorkerFilm.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_OFF);
            }
            if (rbBlackAudio.isSelected()) {
                //BLACK
                progData.filterWorkerAudio.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_ON);
            } else if (rbWhiteAudio.isSelected()) {
                //WHITE, also invers
                progData.filterWorkerAudio.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_INVERS);
            } else {
                //OFF
                progData.filterWorkerAudio.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_OFF);
            }

        } else {
            //dann wird der FilmList Filter gesteuert
            if (rbBlackFilm.isSelected()) {
                //BLACK
                ProgConfig.SYSTEM_FILMLIST_FILTER.setValue(BlacklistFilterFactory.BLACKLILST_FILTER_ON);
            } else if (rbWhiteFilm.isSelected()) {
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

        toggleGroupList.selectedToggleProperty().removeListener(changeListener);

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
            rbFilmAudio.setSelected(blackData.getList() == ProgConst.LIST_FILM_AUDIO);
            rbFilm.setSelected(blackData.getList() == ProgConst.LIST_FILM);
            rbAudio.setSelected(blackData.getList() == ProgConst.LIST_AUDIO);
            toggleGroupList.selectedToggleProperty().addListener(changeListener);

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
