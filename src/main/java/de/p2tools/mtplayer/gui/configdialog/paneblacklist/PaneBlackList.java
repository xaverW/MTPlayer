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
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFilterFactory;
import de.p2tools.mtplayer.gui.filter.PMenuButton;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneBlackList {

    private SplitPane splitPane;
    private final TableView<BlackData> tableView = new TableView<>();
    private final RadioButton rbBlack = new RadioButton("Blacklist");
    private final RadioButton rbWhite = new RadioButton("Whitelist");
    private final RadioButton rbOff = new RadioButton("Alles anzeigen");
    private final GridPane gridPane = new GridPane();

    private final PMenuButton mbChannel;
    private final StringProperty mbChannelProp = new SimpleStringProperty();
    private final PToggleSwitch tgThemeExact = new PToggleSwitch("exakt:");
    private final TextField txtTheme = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtThemeTitle = new TextField();
    private final PToggleSwitch tglActive = new PToggleSwitch("Aktiv:");

    private final BlackList list;
    private final SortedList<BlackData> sortedList;
    private BlackData blackData = null;
    private final BooleanProperty blackDataChanged;
    private boolean selectedBlackDataChanged = false;
    private final boolean controlBlackListNotFilmFilter;
    private final BlackPaneButton blackPaneButton;

    private final Stage stage;
    private final ProgData progData;

    public PaneBlackList(Stage stage, ProgData progData, boolean controlBlackListNotFilmFilter, BooleanProperty blackDataChanged) {
        this.stage = stage;
        this.progData = progData;
        this.controlBlackListNotFilmFilter = controlBlackListNotFilmFilter;
        this.blackDataChanged = blackDataChanged;
        this.blackPaneButton = new BlackPaneButton();

        mbChannel = new PMenuButton(mbChannelProp,
                ProgData.getInstance().worker.getAllChannelList(), true);

        if (controlBlackListNotFilmFilter) {
            sortedList = progData.blackList.getSortedList();
            list = progData.blackList;
        } else {
            sortedList = progData.filmListFilter.getSortedList();
            list = progData.filmListFilter;
        }
    }

    public void close() {
        list.getUndoList().clear();
        blackPaneButton.close();
        if (controlBlackListNotFilmFilter) {
            splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.CONFIG_DIALOG_BLACKLIST_SPLITPANE);
        } else {
            splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.CONFIG_DIALOG_FILMLIST_FILTER_SPLITPANE);
        }
    }

    public void make(Collection<TitledPane> result) {
        final VBox vBox = new VBox(P2LibConst.DIST_EDGE);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));
        vBox.setAlignment(Pos.TOP_RIGHT);

        makeConfigBlackList(vBox);
        initTable();
        splitPane = new BlackPaneFilterGrid(tableView, list).addFilterGrid(vBox, controlBlackListNotFilmFilter);
        blackPaneButton.addButton(stage, vBox, tableView, blackDataChanged, list);
        BlackPaneMoveButton.addMoveButton(stage, vBox, tableView, progData, controlBlackListNotFilmFilter, blackDataChanged, list);
        addConfigs(vBox);

        TitledPane tpBlack = new TitledPane(controlBlackListNotFilmFilter ? "Blacklist" : "Filme ausschließen", vBox);
        result.add(tpBlack);
        tpBlack.setMaxHeight(Double.MAX_VALUE);
    }

    private void makeConfigBlackList(VBox vBox) {
        final Button btnHelp = PButton.helpButton(stage, "Blacklist / Whitelist",
                HelpText.BLACKLIST_WHITELIST);

        final ToggleGroup toggleGroup = new ToggleGroup();
        rbBlack.setToggleGroup(toggleGroup);
        rbWhite.setToggleGroup(toggleGroup);
        rbOff.setToggleGroup(toggleGroup);
        toggleGroup.selectedToggleProperty().addListener((u, o, n) -> setBlackProp());
        setRb();
        progData.actFilmFilterWorker.getActFilterSettings().blacklistOnOffProperty().addListener((u, o, n) -> {
            setRb();
        });

        HBox hBox = new HBox(P2LibConst.DIST_EDGE);
        hBox.getChildren().addAll(rbBlack, rbWhite, rbOff, P2GuiTools.getHBoxGrower(), btnHelp);
        vBox.getChildren().add(hBox);
    }

    private void initTable() {
        BlackPaneTable.initTable(tableView, list);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setActBlackData());
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    private void addConfigs(VBox vBox) {
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(2);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

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

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
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
            switch (progData.actFilmFilterWorker.getActFilterSettings().getBlacklistOnOff()) {
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
                progData.actFilmFilterWorker.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_ON);
            } else if (rbWhite.isSelected()) {
                //WHITE, also invers
                progData.actFilmFilterWorker.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_INVERS);
            } else {
                //OFF
                progData.actFilmFilterWorker.getActFilterSettings().setBlacklistOnOff(BlacklistFilterFactory.BLACKLILST_FILTER_OFF);
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
