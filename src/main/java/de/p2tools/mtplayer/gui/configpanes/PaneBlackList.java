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

package de.p2tools.mtplayer.gui.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.BlackList;
import de.p2tools.mtplayer.controller.data.BlackListFactory;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFactory;
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFilterFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.PGuiTools;
import de.p2tools.p2lib.guitools.PTableFactory;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerLoadFilmlist;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PaneBlackList {

    private final TableView<BlackData> tableView = new TableView<>();
    private final GridPane gridPane = new GridPane();
    private final PToggleSwitch tgThemeExact = new PToggleSwitch("exakt:");
    private final TextField txtTheme = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtThemeTitle = new TextField();
    private BlackData blackData = null;

    private final RadioButton rbBlack = new RadioButton();
    private final RadioButton rbWhite = new RadioButton();
    private final RadioButton rbOff = new RadioButton();

    private final MenuButton mbChannel = new MenuButton("");
    private final ArrayList<CheckMenuItem> checkMenuItemsList = new ArrayList<>();
    private ListenerLoadFilmlist listener;

    private final BooleanProperty blackChanged;
    private boolean blackChange = false;
    private Stage stage;
    private final ProgData progData;
    private final boolean controlBlackList;
    private final BlackList list;

    public PaneBlackList(Stage stage, ProgData progData, boolean controlBlackList, BooleanProperty blackChanged) {
        this.stage = stage;
        this.progData = progData;
        this.controlBlackList = controlBlackList;
        this.blackChanged = blackChanged;
        if (controlBlackList) {
            list = progData.blackList;
        } else {
            list = progData.filmListFilter;
        }
    }

    public void close() {
        LoadFilmFactory.getInstance().loadFilmlist.removeListenerLoadFilmlist(listener);
    }

    public void make(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));

        makeConfigBlackList(vBox);

        initTable();
        addButton(vBox);
        addMoveButton(vBox);
        vBox.getChildren().add(PGuiTools.getVDistance(10));
        addConfigs(vBox);
        if (!controlBlackList) {
            addLoadButton(vBox);
        }

        TitledPane tpBlack = new TitledPane(controlBlackList ? "Blacklist" : "Filme ausschließen", vBox);
        result.add(tpBlack);
        tpBlack.setMaxHeight(Double.MAX_VALUE);
    }

    private void makeConfigBlackList(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);

        final ToggleGroup group = new ToggleGroup();
        rbBlack.setToggleGroup(group);
        rbWhite.setToggleGroup(group);
        rbOff.setToggleGroup(group);

        group.selectedToggleProperty().addListener((u, o, n) -> setBlackProp());

        setRb();
        progData.actFilmFilterWorker.getActFilterSettings().blacklistOnOffProperty().addListener((u, o, n) -> {
            setRb();
        });

        int row = 0;
        gridPane.add(rbBlack, 0, ++row);
        gridPane.add(new Label("\"Filter\" nicht anzeigen, (Blacklist)"), 1, row);
        final Button btnHelp = PButton.helpButton(stage, "Blacklist / Whitelist",
                HelpText.BLACKLIST_WHITELIST);
        gridPane.add(btnHelp, 2, row, 1, 2);

        gridPane.add(rbWhite, 0, ++row);
        gridPane.add(new Label("Nur diese \"Filter\" anzeigen, (Whitelist)"), 1, row);

        gridPane.add(rbOff, 0, ++row);
        gridPane.add(new Label("Alles anzeigen"), 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());
    }

    private void setRb() {
        if (controlBlackList) {
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
        if (controlBlackList) {
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

    private void initTable() {
        final TableColumn<BlackData, String> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        nrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<BlackData, String> channelColumn = new TableColumn<>("Sender");
        channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        channelColumn.getStyleClass().add("alignCenter");

        final TableColumn<BlackData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        final TableColumn<BlackData, Boolean> themeExactColumn = new TableColumn<>("Thema exakt");
        themeExactColumn.setCellValueFactory(new PropertyValueFactory<>("themeExact"));
        themeExactColumn.setCellFactory(CheckBoxTableCell.forTableColumn(themeExactColumn));

        final TableColumn<BlackData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        final TableColumn<BlackData, String> themeTitleColumn = new TableColumn<>("Thema-Titel");
        themeTitleColumn.setCellValueFactory(new PropertyValueFactory<>("themeTitle"));

        final TableColumn<BlackData, Integer> hitsColumn = new TableColumn<>("Treffer");
        hitsColumn.setCellValueFactory(new PropertyValueFactory<>("countHits"));
        hitsColumn.getStyleClass().add("alignCenterRightPadding_10");

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setMinHeight(150);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(nrColumn, channelColumn, themeColumn, themeExactColumn,
                titleColumn, themeTitleColumn, hitsColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setActBlackData());

        SortedList<BlackData> sortedList;
        sortedList = new SortedList<>(list);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    private void addButton(VBox vBox) {
        Button btnDel = new Button("");
        btnDel.setGraphic(ProgIcons.Icons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                blackChanged.set(true);
                list.removeAll(selected);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(ProgIcons.Icons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            blackChanged.set(true);
            BlackData blackData = new BlackData();
            list.add(blackData);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(blackData);
            tableView.scrollTo(blackData);
        });

        final Button btnHelpCount = PButton.helpButton(stage, "Treffer zählen",
                HelpText.BLACKLIST_COUNT);

        Button btnCountHits = new Button("_Treffer zählen");
        btnCountHits.setTooltip(new Tooltip("Damit wird die Filmliste nach \"Treffern\" durchsucht.\n" +
                "Für jeden Eintrag in der Blacklist wird gezählt,\n" +
                "wie viele Filme damit geblockt werden."));
        btnCountHits.setOnAction(a -> {
            BlacklistFactory.countHits(list);
            PTableFactory.refreshTable(tableView);
        });

        Button btnAddStandards = new Button("_Standards einfügen");
        btnAddStandards.setTooltip(new Tooltip("Die Standardeinträge der Liste anfügen"));
        btnAddStandards.setOnAction(event -> {
            BlackListFactory.addStandardsList(list);
        });

        Button btnCleanList = new Button("_Putzen");
        btnCleanList.setTooltip(new Tooltip("In der Liste werden doppelte und leere Einträge gelöscht"));
        btnCleanList.setOnAction(event -> {
            list.cleanTheList();
        });

        Button btnClear = new Button("_Alle löschen");
        btnClear.setTooltip(new Tooltip("Alle Einträge in der Liste werden gelöscht"));
        btnClear.setOnAction(event -> {
            if (list.size() > 0) {
                if (!PAlert.showAlertOkCancel(stage, "Liste löschen", "Sollen alle Tabelleneinträge gelöscht werden?",
                        "Die Tabelle wird komplett gelöscht und alle Einträge gehen verloren.")) {
                    return;
                }
            }
            list.clear();
        });
        listener = new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                btnCountHits.setDisable(true);
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                btnCountHits.setDisable(false);
            }
        };
        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(listener);

        HBox hBoxButton = new HBox(P2LibConst.DIST_BUTTON);
        hBoxButton.getChildren().addAll(btnNew, btnDel, btnClear);

        hBoxButton.getChildren().addAll(PGuiTools.getHBoxGrower(), btnCountHits, btnAddStandards, btnCleanList);
        hBoxButton.getChildren().addAll(btnHelpCount);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        VBox vb = new VBox(P2LibConst.DIST_BUTTON);
        vb.getChildren().addAll(tableView, hBoxButton/*, hBoxCount*/);
        vBox.getChildren().add(vb);
    }

    private void addMoveButton(VBox vBox) {
        final Button btnHelpCount = PButton.helpButton(stage, "Filter kopieren oder verschieben",
                HelpText.BLACKLIST_MOVE);

        Button btnCopy = new Button(controlBlackList ? "_Kopieren nach \"Filmliste laden\"" : "_Kopieren nach \"Blacklist\"");
        btnCopy.setTooltip(new Tooltip("Damit werden die markierten Filter in den " +
                "anderen Filter (Filmfilter/Blacklist) kopiert"));
        btnCopy.setOnAction(a -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();

            } else {
                for (BlackData bl : selected) {
                    BlackData cpy = bl.getCopy();
                    if (controlBlackList) {
                        //dann in den FilmListFilter einfügen
                        progData.filmListFilter.addAll(cpy);
                    } else {
                        //dann in die Blacklist einfügen
                        blackChanged.set(true);
                        progData.blackList.addAll(cpy);
                    }
                }
            }
        });

        Button btnMove = new Button(controlBlackList ? "_Verschieben zu \"Filmliste laden\"" : "_Verschieben zu \"Blacklist\"");
        btnMove.setTooltip(new Tooltip("Damit werden die markierten Filter in den " +
                "anderen Filter (Filmfilter/Blacklist) verschoben"));
        btnMove.setOnAction(a -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                blackChanged.set(true);
                if (controlBlackList) {
                    //dann in den FilmListFilter verschieben
                    progData.filmListFilter.addAll(selected);
                } else {
                    //dann in die Blacklist verschieben
                    progData.blackList.addAll(selected);
                }
                list.removeAll(selected);
                tableView.getSelectionModel().clearSelection();
            }
        });

        HBox hBoxButton = new HBox(P2LibConst.DIST_BUTTON);
        hBoxButton.getChildren().addAll(PGuiTools.getHBoxGrower(), btnCopy, btnMove, btnHelpCount);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        VBox vb = new VBox(P2LibConst.DIST_BUTTON);
        vb.getChildren().addAll(hBoxButton);
        vBox.getChildren().add(vb);
    }


    private void addConfigs(VBox vBox) {
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        int row = 0;
        mbChannel.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(new Label("Sender:"), 0, row);
        gridPane.add(mbChannel, 1, row);

        gridPane.add(new Label("Thema:"), 0, ++row);
        gridPane.add(txtTheme, 1, row);
        gridPane.add(tgThemeExact, 2, row);

        gridPane.add(new Label("Titel:"), 0, ++row);
        gridPane.add(txtTitle, 1, row);

        gridPane.add(new Label("Thema/Titel:"), 0, ++row);
        gridPane.add(txtThemeTitle, 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
        gridPane.setDisable(true);
        gridPane.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        mbChannel.textProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());
        txtTheme.textProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());
        tgThemeExact.selectedProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());
        txtTitle.textProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());
        txtThemeTitle.textProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());

        vBox.getChildren().add(gridPane);
    }

    private void setBlackChanged() {
        if (blackChange) {
            blackChanged.set(true);
        }
    }

    private void addLoadButton(VBox vBox) {
        Button btnLoad = new Button("_Filmliste mit diesen Einstellungen neu laden");
        btnLoad.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoad.setOnAction(event -> {
            LoadFilmFactory.getInstance().loadNewListFromWeb(true);
        });

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(0, 0, 10, 0));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnLoad);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        vBox.getChildren().addAll(hBox);
    }

    private void setActBlackData() {
        blackChange = false;

        BlackData blackDataAct = tableView.getSelectionModel().getSelectedItem();
        if (blackDataAct == blackData) {
            return;
        }

        if (blackData != null) {
            mbChannel.textProperty().unbindBidirectional(blackData.channelProperty());
            clearMenuText();
            tgThemeExact.selectedProperty().unbindBidirectional(blackData.themeExactProperty());
            txtTheme.textProperty().unbindBidirectional(blackData.themeProperty());
            txtTitle.textProperty().unbindBidirectional(blackData.titleProperty());
            txtThemeTitle.textProperty().unbindBidirectional(blackData.themeTitleProperty());
            txtTheme.setText("");
            txtTitle.setText("");
            txtThemeTitle.setText("");
        }

        blackData = blackDataAct;
        if (blackData != null) {
            initSenderMenu();
            mbChannel.textProperty().bindBidirectional(blackData.channelProperty());
            txtTheme.textProperty().bindBidirectional(blackData.themeProperty());
            tgThemeExact.selectedProperty().bindBidirectional(blackData.themeExactProperty());
            txtTitle.textProperty().bindBidirectional(blackData.titleProperty());
            txtThemeTitle.textProperty().bindBidirectional(blackData.themeTitleProperty());
            blackChange = true;
        }
    }

    private void initSenderMenu() {
        mbChannel.getItems().clear();
        checkMenuItemsList.clear();
        mbChannel.getStyleClass().add("cbo-menu");

        List<String> senderArr = new ArrayList<>();
        String sender = blackData.channelProperty().get();
        if (sender != null) {
            if (sender.contains(",")) {
                senderArr.addAll(Arrays.asList(sender.replace(" ", "").toLowerCase().split(",")));
            } else {
                senderArr.add(sender.toLowerCase());
            }
            senderArr.stream().forEach(s -> s = s.trim());
        }

        MenuItem mi = new MenuItem("Auswahl löschen");
        mi.setOnAction(a -> clearMenuText());
        mbChannel.getItems().add(mi);

        for (String s : ProgData.getInstance().worker.getAllChannelList()) {
            if (s.isEmpty()) {
                continue;
            }
            CheckMenuItem miCheck = new CheckMenuItem(s);
            if (senderArr.contains(s.toLowerCase())) {
                miCheck.setSelected(true);
            }
            miCheck.setOnAction(a -> setMenuText());

            checkMenuItemsList.add(miCheck);
            mbChannel.getItems().add(miCheck);
        }
        setMenuText();
    }

    private void clearMenuText() {
        for (CheckMenuItem cmi : checkMenuItemsList) {
            cmi.setSelected(false);
        }
        mbChannel.setText("");
    }

    private void setMenuText() {
        String text = "";
        for (CheckMenuItem cmi : checkMenuItemsList) {
            if (cmi.isSelected()) {
                text = text + (text.isEmpty() ? "" : ", ") + cmi.getText();
            }
        }
        mbChannel.setText(text);
    }
}
