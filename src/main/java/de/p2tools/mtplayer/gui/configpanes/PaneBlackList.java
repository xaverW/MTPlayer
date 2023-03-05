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
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.dialog.PDialog;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.PGuiTools;
import de.p2tools.p2lib.guitools.PTableFactory;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerLoadFilmlist;
import javafx.application.Platform;
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
    private final PToggleSwitch tglNot = new PToggleSwitch("Filme ausschließen");

    private final MenuButton mbChannel = new MenuButton("");
    private final ArrayList<CheckMenuItem> checkMenuItemsList = new ArrayList<>();
    private ListenerLoadFilmlist listener;

    private final BooleanProperty blackChanged;
    private boolean blackChange = false;
    private PDialog pDialog;
    private final ProgData progData;
    private final boolean black;
    private final BlackList list;

    public PaneBlackList(PDialog pDialog, ProgData progData, boolean black, BooleanProperty blackChanged) {
        this.pDialog = pDialog;
        this.progData = progData;
        this.black = black;
        this.blackChanged = blackChanged;
        if (black) {
            list = progData.blackList;
        } else {
            list = progData.filmLoadBlackList;
        }
    }

//    public void setStage(Stage stage) {
//        this.stage = stage;
//    }

    public void close() {
        rbWhite.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST);
        LoadFilmFactory.getInstance().loadFilmlist.removeListenerLoadFilmlist(listener);

//        if (list.size() > 0 && blackChanged.getValue()) {
//            if (!PAlert.showAlertOkCancel(stage, "Liste sortieren", "Soll die " +
//                            (black ? "Blacklist" : "Liste zum Filtern der Filme beim Neuladen der Filmliste") +
//                            " sortiert werden?",
//                    "Die Liste der Filter wird nach Anzahl der Treffer sortiert. Das beschleunigt die Filterung " +
//                            "der Filmliste.")) {
//                return;
//
////            } else {
////                //dann die Liste sortieren
////                list.sortTheListWithCounter();
//            }
//        }
    }

    public void make(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));

        if (black) {
            makeConfig(vBox);
        } else {
            makeToggle(vBox);
        }
        initTable();
        addButton(vBox);
        vBox.getChildren().add(PGuiTools.getVDistance(10));
        addConfigs(vBox);
        if (!black) {
            addLoadButton(vBox);
        }

        TitledPane tpBlack = new TitledPane(black ? "Blacklist" : "Filme ausschließen", vBox);
        result.add(tpBlack);
        tpBlack.setMaxHeight(Double.MAX_VALUE);
    }

    private void makeConfig(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);

        final ToggleGroup group = new ToggleGroup();
        rbBlack.setToggleGroup(group);
        rbWhite.setToggleGroup(group);
        rbBlack.setSelected(!ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getValue());

        int row = 0;
        gridPane.add(rbBlack, 0, row);
        gridPane.add(new Label("\"Sender / Thema / Titel\" werden nicht angezeigt (Blacklist)"), 1, row);
        final Button btnHelp = PButton.helpButton(pDialog.getStage(), "Blacklist / Whitelist",
                HelpText.BLACKLIST_WHITELIST);
        gridPane.add(btnHelp, 2, row);

        rbWhite.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST);
        rbWhite.selectedProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));
        gridPane.add(rbWhite, 0, ++row);
        gridPane.add(new Label("Nur diese \"Sender / Thema / Titel\" anzeigen (Whitelist)"), 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());
    }

    private void makeToggle(VBox vBox) {
        tglNot.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_USE_FILTER_LOAD_FILMLIST);
        final Button btnHelpReplace = PButton.helpButton(pDialog.getStage(), "Filme ausschließen",
                HelpText.FILMTITEL_NOT_LOAD);

        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        HBox.setHgrow(tglNot, Priority.ALWAYS);
        hBox.getChildren().addAll(tglNot, btnHelpReplace);
        vBox.getChildren().add(hBox);
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
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActBlackData));

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

//        final Button btnHelpCount = PButton.helpButton(stage.getStageProp(), "_Treffer zählen",
        final Button btnHelpCount = PButton.helpButton(pDialog.getStage(), "_Treffer zählen",
                HelpText.BLACKLIST_COUNT);

        Button btnCountHits = new Button("_Treffer zählen");
        btnCountHits.setTooltip(new Tooltip("Damit wird die Filmliste nach \"Treffern\" durchsucht.\n" +
                "Für jeden Eintrag in der Blacklist wird gezählt,\n" +
                "wie viele Filme damit geblockt werden."));
        btnCountHits.setOnAction(a -> {
            BlacklistFactory.countHits(list, false);
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

        Button btnAddBlacklist = new Button("_Blacklist einfügen");
        btnAddBlacklist.setTooltip(new Tooltip("Die Einträge der Blacklist werden in die Liste kopiert"));
        btnAddBlacklist.setOnAction(event -> {
            progData.blackList.stream().forEach(bl -> list.add(bl.getCopy()));
        });
        Button btnClear = new Button("_Alle löschen");
        btnClear.setTooltip(new Tooltip("Alle Einträge in der Liste werden gelöscht"));
        btnClear.setOnAction(event -> {
            if (list.size() > 0) {
                if (!PAlert.showAlertOkCancel(pDialog.getStageProp().getValue(), "Liste löschen", "Sollen alle Tabelleneinträge gelöscht werden?",
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

        if (black) {
            hBoxButton.getChildren().addAll(PGuiTools.getHBoxGrower(), btnCountHits, btnAddStandards, btnCleanList);
        } else {
            hBoxButton.getChildren().addAll(PGuiTools.getHBoxGrower(), btnCountHits, btnAddStandards, btnCleanList, btnAddBlacklist);
            tableView.disableProperty().bind(ProgConfig.SYSTEM_USE_FILTER_LOAD_FILMLIST.not());
            hBoxButton.disableProperty().bind(ProgConfig.SYSTEM_USE_FILTER_LOAD_FILMLIST.not());
        }
        hBoxButton.getChildren().addAll(btnHelpCount);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        VBox vb = new VBox(P2LibConst.DIST_BUTTON);
        vb.getChildren().addAll(tableView, hBoxButton/*, hBoxCount*/);
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
        if (!black) {
            gridPane.disableProperty().bind(ProgConfig.SYSTEM_USE_FILTER_LOAD_FILMLIST.not());
        }

        mbChannel.textProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());
        txtTheme.textProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());
        tgThemeExact.selectedProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());
        txtTitle.textProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());
        txtThemeTitle.textProperty().addListener((observable, oldValue, newValue) -> setBlackChanged());

        vBox.getChildren().add(gridPane);
    }

    private void setBlackChanged() {
        if (blackChange) {
            blackChanged.setValue(true);
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
        if (black) {
            gridPane.setDisable(blackData == null);
        }
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
