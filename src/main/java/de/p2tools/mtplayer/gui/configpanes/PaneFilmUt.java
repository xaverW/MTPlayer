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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.utdata.UtData;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneFilmUt {

    private final P2ToggleSwitch tglRemove;

    private final ComboBox<String> cboSender = new ComboBox<>();
    private final TextField txtTitle = new TextField();
    private final GridPane gridPane = new GridPane();

    private final TableView<UtData> tableView = new TableView<>();
    private final ObjectProperty<UtData> utDateProp = new SimpleObjectProperty<>(null);
    private final ChangeListener<String> changeListener;
    private final Stage stage;
    private final boolean ut;

    public PaneFilmUt(Stage stage, boolean ut) {
        this.stage = stage;
        this.ut = ut;
        tglRemove = new P2ToggleSwitch(ut ? "Filme mit Untertitel markieren" :
                "Filme mit Gebärdensprache markieren");
        changeListener = (observableValue, s, t1) -> {
            if (utDateProp.getValue() != null) {
                utDateProp.getValue().setChannel(cboSender.getValue());
            }
        };
    }

    public void close() {
        cboSender.getSelectionModel().selectedItemProperty().removeListener(changeListener);
        if (ut) {
            ProgData.getInstance().utDataList.getUndoList().clear();
        } else {
            ProgData.getInstance().signLanguageDataList.getUndoList().clear();
        }
        unbindText();
        tglRemove.selectedProperty().unbindBidirectional(ut ?
                ProgConfig.SYSTEM_FILMLIST_MARK_UT : ProgConfig.SYSTEM_FILMLIST_MARK_SIGN_LANGUAGE);
        cleanList();
    }

    public void make(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(P2LibConst.PADDING));

        initTop(vBox);
        initTable(vBox);
        initButton(vBox);
        initConfigs(vBox);
        addLoadFilmList(vBox);

        TitledPane tpReplace = new TitledPane(ut ? "Filme mit Untertitel markieren" : "Filme mit Gebärdensprache markieren", vBox);
        result.add(tpReplace);
        tpReplace.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpReplace, Priority.ALWAYS);
    }

    private void cleanList() {
        if (ut) {
            ProgData.getInstance().utDataList.removeIf(utData -> utData.getTitle().isEmpty());
        } else {
            ProgData.getInstance().signLanguageDataList.removeIf(utData -> utData.getTitle().isEmpty());
        }
    }

    private void initTop(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        final Button btnHelpMark;
        if (ut) {
            tglRemove.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILMLIST_MARK_UT);
            btnHelpMark = P2Button.helpButton(stage, "Filme mit Untertitel markieren",
                    HelpText.LOAD_FILMLIST_MARK_UT);
        } else {
            tglRemove.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILMLIST_MARK_SIGN_LANGUAGE);
            btnHelpMark = P2Button.helpButton(stage, "Filme mit Gebärdensprache markieren",
                    HelpText.LOAD_FILMLIST_MARK_SIGN_LANGUAGE);
        }

        int row = 0;
        gridPane.add(tglRemove, 0, row, 2, 1);
        gridPane.add(btnHelpMark, 2, row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize());

        vBox.setPadding(new Insets(P2LibConst.PADDING));
        vBox.getChildren().addAll(gridPane);
    }

    private void initTable(VBox vBox) {
        ScrollPane scrollPane = new ScrollPane();
        final TableColumn<UtData, String> channelColumn = new TableColumn<>("Sender");
        channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));

        final TableColumn<UtData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(channelColumn, titleColumn);
        tableView.setItems(ut ? ProgData.getInstance().utDataList : ProgData.getInstance().signLanguageDataList);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActReplaceData));
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                ContextMenu contextMenu = getContextMenu();
                tableView.setContextMenu(contextMenu);
            }
        });

        tableView.disableProperty().bind(ut ? ProgConfig.SYSTEM_FILMLIST_MARK_UT.not() :
                ProgConfig.SYSTEM_FILMLIST_MARK_SIGN_LANGUAGE.not());
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        vBox.getChildren().addAll(scrollPane);
    }

    private void initButton(VBox vBox) {
        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Eintrag löschen"));
        btnDel.setGraphic(ProgIcons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<UtData> sels = tableView.getSelectionModel().getSelectedItems();
            if (sels == null || sels.isEmpty()) {
                P2Alert.showInfoNoSelection();
            } else {
                if (ut) {
                    ProgData.getInstance().utDataList.addDataToUndoList(sels);
                    ProgData.getInstance().utDataList.removeAll(sels);
                } else {
                    ProgData.getInstance().signLanguageDataList.addDataToUndoList(sels);
                    ProgData.getInstance().signLanguageDataList.removeAll(sels);
                }
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setTooltip(new Tooltip("Einen neuen Eintrag erstellen"));
        btnNew.setGraphic(ProgIcons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            UtData utData = new UtData();
            if (ut) {
                ProgData.getInstance().utDataList.add(utData);
            } else {
                ProgData.getInstance().signLanguageDataList.add(utData);
            }
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(utData);
            tableView.scrollTo(utData);
        });

        Button btnUp = new Button("");
        btnUp.setTooltip(new Tooltip("Eintrag nach oben schieben"));
        btnUp.setGraphic(ProgIcons.ICON_BUTTON_MOVE_UP.getImageView());
        btnUp.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res;
                if (ut) {
                    res = ProgData.getInstance().utDataList.up(sel, true);
                } else {
                    res = ProgData.getInstance().signLanguageDataList.up(sel, true);
                }
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(res);
                tableView.scrollTo(res);
            }
        });

        Button btnDown = new Button("");
        btnDown.setTooltip(new Tooltip("Eintrag nach unten schieben"));
        btnDown.setGraphic(ProgIcons.ICON_BUTTON_MOVE_DOWN.getImageView());
        btnDown.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                P2Alert.showInfoNoSelection();
            } else {
                int res;
                if (ut) {
                    res = ProgData.getInstance().utDataList.up(sel, false);
                } else {
                    res = ProgData.getInstance().signLanguageDataList.up(sel, false);
                }
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(res);
                tableView.scrollTo(res);
            }
        });

        Button btnClean = new Button("Tabelle _aufräumen");
        btnClean.setTooltip(new Tooltip("Einträge mit leerem Titel werden gelöscht"));
        btnClean.setOnAction(a -> cleanList());

        Button btnReset = new Button("_Tabelle zurücksetzen");
        btnReset.setTooltip(new Tooltip("Alle Einträge löschen und Standardeinträge wieder herstellen"));
        btnReset.setOnAction(event -> {
            if (ut) {
                ProgData.getInstance().utDataList.init(true);
            } else {
                ProgData.getInstance().signLanguageDataList.init(false);
            }
        });

        HBox hBox = new HBox();
        hBox.setSpacing(P2LibConst.DIST_BUTTON);
        if (ut) {
            hBox.disableProperty().bind(ProgConfig.SYSTEM_FILMLIST_MARK_UT.not());
        } else {
            hBox.disableProperty().bind(ProgConfig.SYSTEM_FILMLIST_MARK_SIGN_LANGUAGE.not());
        }
        hBox.getChildren().addAll(btnNew, btnDel, P2GuiTools.getVDistance(P2LibConst.DIST_BUTTON_BLOCK),
                btnUp, btnDown, P2GuiTools.getHBoxGrower(), btnClean, btnReset);
        vBox.getChildren().addAll(hBox);
    }

    private ContextMenu getContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen");
        if (ut) {
            miUndo.setOnAction(a -> ProgData.getInstance().utDataList.undoData());
            miUndo.setDisable(ProgData.getInstance().utDataList.getUndoList().isEmpty());
        } else {
            miUndo.setOnAction(a -> ProgData.getInstance().signLanguageDataList.undoData());
            miUndo.setDisable(ProgData.getInstance().signLanguageDataList.getUndoList().isEmpty());
        }
        contextMenu.getItems().addAll(miUndo);
        return contextMenu;
    }

    private void initConfigs(VBox vBox) {
        cboSender.setItems(ThemeListFactory.allChannelList);
        cboSender.getSelectionModel().select(0);
        cboSender.setEditable(true);
        cboSender.getSelectionModel().selectedItemProperty().addListener(changeListener);

        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        gridPane.add(new Label("Sender: "), 0, 0);
        gridPane.add(cboSender, 1, 0);
        gridPane.add(new Label("Titel: "), 0, 1);
        gridPane.add(txtTitle, 1, 1);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(), P2ColumnConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);
        if (ut) {
            gridPane.disableProperty().bind(Bindings.createBooleanBinding(() -> utDateProp.getValue() == null, utDateProp)
                    .or(ProgConfig.SYSTEM_FILMLIST_MARK_UT.not()));
        } else {
            gridPane.disableProperty().bind(Bindings.createBooleanBinding(() -> utDateProp.getValue() == null, utDateProp)
                    .or(ProgConfig.SYSTEM_FILMLIST_MARK_SIGN_LANGUAGE.not()));
        }
    }

    private void addLoadFilmList(VBox vBox) {
        Button btnLoad = new Button("_Filmliste mit diesen Einstellungen neu laden");
        btnLoad.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoad.setOnAction(event -> {
            LoadFilmFactory.getInstance().loadNewListFromWeb(true);
        });
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnLoad);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(20), hBox);
    }

    private void setActReplaceData() {
        UtData utData = tableView.getSelectionModel().getSelectedItem();
        if (utData == utDateProp.getValue()) {
            return;
        }

        unbindText();
        utDateProp.setValue(utData);

        if (utDateProp.getValue() != null) {
            cboSender.getEditor().textProperty().bindBidirectional(utDateProp.getValue().channelProperty());
            txtTitle.textProperty().bindBidirectional(utDateProp.getValue().titleProperty());
        }
    }

    private void unbindText() {
        if (utDateProp.getValue() != null) {
            txtTitle.textProperty().unbindBidirectional(utDateProp.getValue().titleProperty());
            cboSender.getEditor().textProperty().unbindBidirectional(utDateProp.getValue().channelProperty());
        }

        txtTitle.setText("");
    }
}
