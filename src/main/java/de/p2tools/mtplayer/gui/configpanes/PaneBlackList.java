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
import de.p2tools.mtplayer.gui.filter.PMenuButton;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.*;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerLoadFilmlist;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

import java.util.Collection;
import java.util.function.Predicate;

public class PaneBlackList {

    private final TableView<BlackData> tableView = new TableView<>();
    private final RadioButton rbBlack = new RadioButton();
    private final RadioButton rbWhite = new RadioButton();
    private final RadioButton rbOff = new RadioButton();
    private final GridPane gridPane = new GridPane();
    private final Button btnClearFilter = PButtonClearFilterFactory.getPButtonClear();

    private final PMenuButton mbChannel;
    private final StringProperty mbChannelProp = new SimpleStringProperty();
    private final PToggleSwitch tgThemeExact = new PToggleSwitch("exakt:");
    private final TextField txtTheme = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtThemeTitle = new TextField();

    private final PMenuButton mbFilterChannel;
    private final StringProperty mbFilterChannelProp = new SimpleStringProperty();
    private final TextField txtFilterThema = new TextField();
    private final TextField txtFilterTitel = new TextField();
    private final TextField txtFilterThemaTitel = new TextField();
    private final PToggleSwitch tglFilterExact = new PToggleSwitch("Thema exakt");

    private final BlackList list;
    private final SortedList<BlackData> sortedList;
    private BlackData blackData = null;
    private final BooleanProperty blackDataChanged;
    private boolean selectedBlackDataChanged = false;
    private final boolean controlBlackListNotFilmFilter;
    private ListenerLoadFilmlist listenerLoadFilmlist;

    private final Stage stage;
    private final ProgData progData;

    public PaneBlackList(Stage stage, ProgData progData, boolean controlBlackListNotFilmFilter, BooleanProperty blackDataChanged) {
        this.stage = stage;
        this.progData = progData;
        this.controlBlackListNotFilmFilter = controlBlackListNotFilmFilter;
        this.blackDataChanged = blackDataChanged;
        mbFilterChannel = new PMenuButton(mbFilterChannelProp,
                ProgData.getInstance().worker.getAllChannelList());

        mbChannel = new PMenuButton(mbChannelProp,
                ProgData.getInstance().worker.getAllChannelList());

        if (controlBlackListNotFilmFilter) {
            sortedList = progData.blackList.getSortedList();
            list = progData.blackList;
        } else {
            sortedList = progData.filmListFilter.getSortedList();
            list = progData.filmListFilter;
        }
    }

    public void close() {
        LoadFilmFactory.getInstance().loadFilmlist.removeListenerLoadFilmlist(listenerLoadFilmlist);
    }

    public void make(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));

        makeConfigBlackList(vBox);
        initTable(vBox);
        addFilterGrid(vBox);
        addButton(vBox);
        addMoveButton(vBox);
        makeFilter();
        vBox.getChildren().add(PGuiTools.getVDistance(10));
        addConfigs(vBox);
        if (!controlBlackListNotFilmFilter) {
            addLoadButton(vBox);
        }

        TitledPane tpBlack = new TitledPane(controlBlackListNotFilmFilter ? "Blacklist" : "Filme ausschließen", vBox);
        result.add(tpBlack);
        tpBlack.setMaxHeight(Double.MAX_VALUE);
    }

    private void makeConfigBlackList(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);

        final ToggleGroup toggleGroup = new ToggleGroup();
        rbBlack.setToggleGroup(toggleGroup);
        rbWhite.setToggleGroup(toggleGroup);
        rbOff.setToggleGroup(toggleGroup);
        toggleGroup.selectedToggleProperty().addListener((u, o, n) -> setBlackProp());

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

    private void initTable(VBox vBox) {
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

        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    private void addFilterGrid(VBox vBox) {
        VBox vbAll = new VBox(P2LibConst.DIST_EDGE);
        vbAll.setAlignment(Pos.TOP_LEFT);
        vbAll.setPadding(new Insets(P2LibConst.DIST_EDGE));
        vbAll.getStyleClass().add("extra-pane");

        Label label = new Label("Suchen:");
        vbAll.getChildren().add(label);

        VBox vb = new VBox(2);
        vb.getChildren().addAll(new Label("Sender"), mbFilterChannel);
        vbAll.getChildren().add(vb);
        HBox.setHgrow(vb, Priority.ALWAYS);

        vb = new VBox(2);
        vb.getChildren().addAll(new Label("Thema"), txtFilterThema);
        vbAll.getChildren().add(vb);
        HBox.setHgrow(vb, Priority.ALWAYS);

        vbAll.getChildren().add(tglFilterExact);

        vb = new VBox(2);
        vb.getChildren().addAll(new Label("Titel"), txtFilterTitel);
        vbAll.getChildren().add(vb);
        HBox.setHgrow(vb, Priority.ALWAYS);

        vb = new VBox(2);
        vb.getChildren().addAll(new Label("Thema/Titel"), txtFilterThemaTitel);
        vbAll.getChildren().add(vb);
        HBox.setHgrow(vb, Priority.ALWAYS);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnClearFilter);
        vbAll.getChildren().add(hBox);

        HBox h = new HBox(P2LibConst.DIST_EDGE);
        h.getChildren().addAll(tableView, vbAll);
        HBox.setHgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().add(h);
    }

    private void addButton(VBox vBox) {
        Button btnDel = new Button("");
        btnDel.setGraphic(ProgIcons.Icons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                blackDataChanged.set(true);
                sortedList.removeAll(selected);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(ProgIcons.Icons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            blackDataChanged.set(true);
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
        listenerLoadFilmlist = new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                btnCountHits.setDisable(true);
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                btnCountHits.setDisable(false);
            }
        };
        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(listenerLoadFilmlist);

        HBox hBoxButton = new HBox(P2LibConst.DIST_BUTTON);
        hBoxButton.getChildren().addAll(btnNew, btnDel, btnClear);

        hBoxButton.getChildren().addAll(PGuiTools.getHBoxGrower(), btnCountHits, btnAddStandards, btnCleanList);
        hBoxButton.getChildren().addAll(btnHelpCount);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        VBox vb = new VBox(P2LibConst.DIST_BUTTON);
        vb.getChildren().addAll(hBoxButton);
        vBox.getChildren().add(vb);
    }

    private void addMoveButton(VBox vBox) {
        final Button btnHelpCount = PButton.helpButton(stage, "Filter kopieren oder verschieben",
                HelpText.BLACKLIST_MOVE);

        Button btnCopy = new Button(controlBlackListNotFilmFilter ? "_Kopieren nach \"Filmliste laden\"" : "_Kopieren nach \"Blacklist\"");
        btnCopy.setTooltip(new Tooltip("Damit werden die markierten Filter in den " +
                "anderen Filter (Filmfilter/Blacklist) kopiert"));
        btnCopy.setOnAction(a -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();

            } else {
                for (BlackData bl : selected) {
                    BlackData cpy = bl.getCopy();
                    if (controlBlackListNotFilmFilter) {
                        //dann in den FilmListFilter einfügen
                        progData.filmListFilter.addAll(cpy);
                    } else {
                        //dann in die Blacklist einfügen
                        blackDataChanged.set(true);
                        progData.blackList.addAll(cpy);
                    }
                }
            }
        });

        Button btnMove = new Button(controlBlackListNotFilmFilter ? "_Verschieben zu \"Filmliste laden\"" : "_Verschieben zu \"Blacklist\"");
        btnMove.setTooltip(new Tooltip("Damit werden die markierten Filter in den " +
                "anderen Filter (Filmfilter/Blacklist) verschoben"));
        btnMove.setOnAction(a -> {
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                blackDataChanged.set(true);
                if (controlBlackListNotFilmFilter) {
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

    private void makeFilter() {
        mbFilterChannelProp.addListener((u, o, n) -> addPredicate());
        txtFilterThema.textProperty().addListener((u, o, n) -> addPredicate());
        txtFilterTitel.textProperty().addListener((u, o, n) -> addPredicate());
        txtFilterThemaTitel.textProperty().addListener((u, o, n) -> addPredicate());

        tglFilterExact.setIndeterminate(true);
        tglFilterExact.setAllowIndeterminate(true);
        tglFilterExact.selectedProperty().addListener((u, o, n) -> addPredicate());
        tglFilterExact.indeterminateProperty().addListener((u, o, n) -> addPredicate());

        btnClearFilter.setOnAction(a -> {
            mbFilterChannelProp.setValue("");
            txtFilterThema.clear();
            tglFilterExact.setSelected(false);
            tglFilterExact.setIndeterminate(true);
            txtFilterTitel.clear();
            txtFilterThemaTitel.clear();
        });
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

        if (selectedBlackDataChanged) {
            mbChannel.textProperty().addListener((observable, oldValue, newValue) -> blackDataChanged.set(true));
            txtTheme.textProperty().addListener((observable, oldValue, newValue) -> blackDataChanged.set(true));
            tgThemeExact.selectedProperty().addListener((observable, oldValue, newValue) -> blackDataChanged.set(true));
            txtTitle.textProperty().addListener((observable, oldValue, newValue) -> blackDataChanged.set(true));
            txtThemeTitle.textProperty().addListener((observable, oldValue, newValue) -> blackDataChanged.set(true));
        }
        vBox.getChildren().add(gridPane);
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

    private void addPredicate() {
        Predicate<BlackData> predicate = blackData -> true;

        if (!mbFilterChannelProp.getValueSafe().isEmpty()) {
            Filter filter = new Filter(mbFilterChannelProp.getValueSafe(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getChannel()));
        }
        if (!txtFilterThema.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterThema.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getTheme()));
        }
        if (!tglFilterExact.isIndeterminate()) {
            predicate = predicate.and(blackData -> {
                if (tglFilterExact.isSelected()) {
                    return blackData.isThemeExact();
                } else {
                    return !blackData.isThemeExact();
                }
            });
        }
        if (!txtFilterTitel.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterTitel.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getTitle()));
        }
        if (!txtFilterThemaTitel.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterThemaTitel.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getThemeTitle()));
        }

        list.filteredListSetPred(predicate);
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
            selectedBlackDataChanged = true;
        }
    }
}
