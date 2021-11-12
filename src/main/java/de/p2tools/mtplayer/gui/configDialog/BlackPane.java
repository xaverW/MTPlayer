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

package de.p2tools.mtplayer.gui.configDialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.tools.filmListFilter.FilmlistBlackFilterCountHits;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlackPane {

    private final TableView<BlackData> tableView = new TableView<>();
    private final GridPane gridPane = new GridPane();
    private final TextField theme = new TextField();
    private final PToggleSwitch tgTheme = new PToggleSwitch("exakt:");
    private final TextField title = new TextField();
    private final TextField themeTitle = new TextField();
    private BlackData blackData = null;

    private final RadioButton rbBlack = new RadioButton();
    private final RadioButton rbWhite = new RadioButton();

    private final MenuButton mbChannel = new MenuButton("");
    private final ArrayList<CheckMenuItem> checkMenuItemsList = new ArrayList<>();

    BooleanProperty propWhite = ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST;
    ListenerLoadFilmlist listener;

    private final BooleanProperty blackChanged;
    private final Stage stage;

    public BlackPane(Stage stage, BooleanProperty blackChanged) {
        this.stage = stage;
        this.blackChanged = blackChanged;
    }

    public void makeBlackTable(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setSpacing(10);

        makeConfig(vBox);
        initTable(vBox);
        addConfigs(vBox);

        TitledPane tpBlack = new TitledPane("Blacklist", vBox);
        result.add(tpBlack);
        tpBlack.setMaxHeight(Double.MAX_VALUE);
    }

    public void close() {
        rbWhite.selectedProperty().unbindBidirectional(propWhite);
        ProgData.getInstance().loadFilmlist.removeListenerLoadFilmlist(listener);
    }

    private void makeConfig(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        vBox.getChildren().add(gridPane);


        final ToggleGroup group = new ToggleGroup();
        rbBlack.setToggleGroup(group);
        rbWhite.setToggleGroup(group);

        rbBlack.setSelected(!ProgConfig.SYSTEM_BLACKLIST_IS_WHITELIST.getValue());

        int row = 0;
        gridPane.add(rbBlack, 0, row);
        gridPane.add(new Label("\"Sender / Thema / Titel\" werden nicht angezeigt (Blacklist)"), 1, row);
        final Button btnHelp = PButton.helpButton(stage, "Blacklist / Whitelist",
                HelpText.BLACKLIST_WHITELIST);
        gridPane.add(btnHelp, 2, row);

        rbWhite.selectedProperty().bindBidirectional(propWhite);
        rbWhite.selectedProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));
        gridPane.add(rbWhite, 0, ++row);
        gridPane.add(new Label("nur diese \"Sender / Thema / Titel\" anzeigen (Whitelist)"), 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());
    }


    private void initTable(VBox vBox) {
        final TableColumn<BlackData, String> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
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
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(nrColumn, channelColumn, themeColumn, themeExactColumn,
                titleColumn, themeTitleColumn, hitsColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActBlackData));

        SortedList<BlackData> sortedList;
        sortedList = new SortedList<>(ProgData.getInstance().blackList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);


        Button btnDel = new Button("");
        btnDel.setGraphic(new ProgIcons().ICON_BUTTON_REMOVE);
        btnDel.setOnAction(event -> {
            blackChanged.set(true);
            final ObservableList<BlackData> selected = tableView.getSelectionModel().getSelectedItems();

            if (selected == null || selected.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                ProgData.getInstance().blackList.removeAll(selected);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setGraphic(new ProgIcons().ICON_BUTTON_ADD);
        btnNew.setOnAction(event -> {
            blackChanged.set(true);
            BlackData blackData = new BlackData();
            ProgData.getInstance().blackList.add(blackData);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(blackData);
            tableView.scrollTo(blackData);
        });

        final Button btnHelpCount = PButton.helpButton(stage, "_Treffer zählen",
                HelpText.BLACKLIST_COUNT);


        Button btnSortList = new Button("_Liste nach Treffer sortieren");
        btnSortList.setTooltip(new Tooltip("Damit kann die Blacklist anhand der \"Treffer\"\n" +
                "sortiert werden."));
        btnSortList.setOnAction(a -> {
            ProgData.getInstance().blackList.sortIncCounter(true);
            Table.refresh_table(tableView);
        });


        Button btnCountHits = new Button("_Treffer zählen");
        btnCountHits.setTooltip(new Tooltip("Damit wird die Filmliste nach \"Treffern\" durchsucht.\n" +
                "Für jeden Eintrag in der Blacklist wird gezählt,\n" +
                "wieviele Filme damit geblockt werden."));
        btnCountHits.setOnAction(a -> {
            FilmlistBlackFilterCountHits.countHits(true);
            Table.refresh_table(tableView);
        });


        // toDo -> vielleicht den ganzen Dialog sperren??
        ListenerLoadFilmlist listener = new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                btnSortList.setDisable(true);
                btnCountHits.setDisable(true);
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                btnSortList.setDisable(false);
                btnCountHits.setDisable(false);
            }
        };
        ProgData.getInstance().loadFilmlist.addListenerLoadFilmlist(listener);


        HBox hBoxCount = new HBox(10);
        hBoxCount.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(hBoxCount, Priority.ALWAYS);
        hBoxCount.getChildren().addAll(btnSortList, btnCountHits, btnHelpCount);

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(btnNew, btnDel, hBoxCount);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView, hBox);
    }

    private void addConfigs(VBox vBox) {
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20));

        int row = 0;

        mbChannel.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(new Label("Sender:"), 0, row);
        gridPane.add(mbChannel, 1, row);

        gridPane.add(new Label("Thema:"), 0, ++row);
        gridPane.add(theme, 1, row);
        gridPane.add(tgTheme, 2, row);

        gridPane.add(new Label("Titel:"), 0, ++row);
        gridPane.add(title, 1, row);

        gridPane.add(new Label("Thema/Titel:"), 0, ++row);
        gridPane.add(themeTitle, 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);

        mbChannel.textProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));
        theme.textProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));
        tgTheme.selectedProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));
        title.textProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));
        themeTitle.textProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));
    }

    private void setActBlackData() {
        BlackData blackDataAct = tableView.getSelectionModel().getSelectedItem();
        if (blackDataAct == blackData) {
            return;
        }

        if (blackData != null) {
            mbChannel.textProperty().unbindBidirectional(blackData.channelProperty());
            clearMenuText();
            theme.textProperty().unbindBidirectional(blackData.themeProperty());
            tgTheme.selectedProperty().unbindBidirectional(blackData.themeExactProperty());
            title.textProperty().unbindBidirectional(blackData.titleProperty());
            themeTitle.textProperty().unbindBidirectional(blackData.themeTitleProperty());
        }

        blackData = blackDataAct;
        gridPane.setDisable(blackData == null);
        if (blackData != null) {
            initSenderMenu();
            mbChannel.textProperty().bindBidirectional(blackData.channelProperty());
            theme.textProperty().bindBidirectional(blackData.themeProperty());
            tgTheme.selectedProperty().bindBidirectional(blackData.themeExactProperty());
            title.textProperty().bindBidirectional(blackData.titleProperty());
            themeTitle.textProperty().bindBidirectional(blackData.themeTitleProperty());
        }
    }

    private void initSenderMenu() {
        mbChannel.getItems().clear();
        checkMenuItemsList.clear();
        mbChannel.getStyleClass().add("channel-menu");

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
