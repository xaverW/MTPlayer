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

package de.p2tools.mtplayer.gui.configdialog.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.offer.OfferData;
import de.p2tools.mtplayer.controller.data.offer.PCellCheckBoxOfferData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

public class PaneFilter {

    private final TextField txtOffer = new TextField();
    private final CheckBox chkActive = new CheckBox();
    private final Label lblActive = new Label();
    private final GridPane gridPane = new GridPane();

    private final TableView<OfferData> tableView = new TableView<>();
    private final ObjectProperty<OfferData> offerDateProp = new SimpleObjectProperty<>(null);
    private final P2ToggleSwitch tglOffer = new P2ToggleSwitch("Vorschläge anzeigen");
    private final P2ToggleSwitch tglRegEx = new P2ToggleSwitch("Bei der Suche mit RegEx muss der Suchtext nur enthalten sein");

    private final Stage stage;

    public PaneFilter(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        ProgData.getInstance().offerList.getUndoList().clear();
        unbindText();
        tglOffer.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_USE_OFFERTABLE);
        tglRegEx.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_FILTER_REG_EX_ONLY_CONTAIN);
    }

    public void make(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(P2LibConst.PADDING));

        make(vBox);
        initTable(vBox);
        initBtn(vBox);
        addConfigs(vBox);

        TitledPane tpOffer = new TitledPane("Filter", vBox);
        result.add(tpOffer);
        tpOffer.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpOffer, Priority.ALWAYS);
    }

    private void make(VBox vBox) {
        final Button btnHelp = P2Button.helpButton(stage, "Filtervorschläge",
                HelpText.FILTER_OFFER_TABLE);
        final Button btnHelpRegEx = P2Button.helpButton(stage, "Suche mit RegEx",
                HelpText.FILTER_REG_EX_ONLY_CONTAIN);
        tglOffer.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_USE_OFFERTABLE);
        tglRegEx.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILTER_REG_EX_ONLY_CONTAIN);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);

        gridPane.add(tglRegEx, 0, 0);
        gridPane.add(btnHelpRegEx, 1, 0);

        gridPane.add(new Label(), 0, 1);
        gridPane.add(tglOffer, 0, 2);
        gridPane.add(btnHelp, 1, 2);
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());
    }

    private void initTable(VBox vBox) {
        final TableColumn<OfferData, String> offerColumn = new TableColumn<>("Vorschlag");
        offerColumn.setCellValueFactory(new PropertyValueFactory<>("offer"));

        final TableColumn<OfferData, Boolean> activeColumn = new TableColumn<>("Aktiv");
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeColumn.setCellFactory(new PCellCheckBoxOfferData<>().cellFactory);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(offerColumn, activeColumn);
        tableView.setItems(ProgData.getInstance().offerList);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActOfferData));
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                ContextMenu contextMenu = getContextMenu();
                tableView.setContextMenu(contextMenu);
            }
        });

        tableView.disableProperty().bind(ProgConfig.SYSTEM_USE_OFFERTABLE.not());
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);
    }

    private void initBtn(VBox vBox) {
        chkActive.selectedProperty().addListener((u, o, n) -> setLblText());

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Eintrag löschen"));
        btnDel.setGraphic(ProgIcons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<OfferData> sels = tableView.getSelectionModel().getSelectedItems();
            if (sels == null || sels.isEmpty()) {
                P2Alert.showInfoNoSelection(stage);
            } else {
                ProgData.getInstance().offerList.addDataToUndoList(sels);
                ProgData.getInstance().offerList.removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setTooltip(new Tooltip("Einen neuen Eintrag erstellen"));
        btnNew.setGraphic(ProgIcons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            OfferData offerData = new OfferData();
            ProgData.getInstance().offerList.add(offerData);

            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(offerData);
            tableView.scrollTo(offerData);
        });

        Button btnUp = new Button("");
        btnUp.setTooltip(new Tooltip("Eintrag nach oben schieben"));
        btnUp.setGraphic(ProgIcons.ICON_BUTTON_MOVE_UP.getImageView());
        btnUp.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                P2Alert.showInfoNoSelection(stage);
            } else {
                int res = ProgData.getInstance().offerList.up(sel, true);
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
                P2Alert.showInfoNoSelection(stage);
            } else {
                int res = ProgData.getInstance().offerList.up(sel, false);
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(res);
                tableView.scrollTo(res);
            }
        });

        Button btnTop = new Button();
        btnTop.setTooltip(new Tooltip("Eintrag an den Anfang verschieben"));
        btnTop.setGraphic(ProgIcons.ICON_BUTTON_MOVE_TOP.getImageView());
        btnTop.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                P2Alert.showInfoNoSelection(stage);
            } else {
                int res = ProgData.getInstance().offerList.top(sel, true);
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(res);
                tableView.scrollTo(res);
            }
        });

        Button btnBottom = new Button();
        btnBottom.setTooltip(new Tooltip("Eintrag an das Ende verschieben"));
        btnBottom.setGraphic(ProgIcons.ICON_BUTTON_MOVE_BOTTOM.getImageView());
        btnBottom.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                P2Alert.showInfoNoSelection(stage);
            } else {
                int res = ProgData.getInstance().offerList.top(sel, false);
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(res);
                tableView.scrollTo(res);
            }
        });

        Button btnReset = new Button("_Tabelle zurücksetzen");
        btnReset.setTooltip(new Tooltip("Alle Einträge löschen und Standardeinträge wieder herstellen"));
        btnReset.setOnAction(event -> {
            if (P2Alert.BUTTON.YES.equals(P2Alert.showAlert_yes_no(stage,
                    "Tabelle löschen",
                    "Alle Einträge in der Tabelle werden gelöscht!",
                    "Soll die Tabelle gelöscht und " +
                            "die Standardeinträge wieder eingefügt werden?"))) {
                ProgData.getInstance().offerList.init();
            }
        });

        HBox hBox = new HBox();
        hBox.setSpacing(P2LibConst.DIST_BUTTON);
        hBox.disableProperty().bind(ProgConfig.SYSTEM_USE_OFFERTABLE.not());
        hBox.getChildren().addAll(btnNew, btnDel, P2GuiTools.getVDistance(P2LibConst.DIST_BUTTON_BLOCK),
                btnTop, btnUp, btnDown, btnBottom, P2GuiTools.getHBoxGrower(), btnReset);
        vBox.getChildren().addAll(hBox);
    }

    private ContextMenu getContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen");
        miUndo.setOnAction(a -> ProgData.getInstance().offerList.undoData());
        miUndo.setDisable(ProgData.getInstance().offerList.getUndoList().isEmpty());
        contextMenu.getItems().addAll(miUndo);
        return contextMenu;
    }

    private void addConfigs(VBox vBox) {
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        int row = 0;
        gridPane.add(new Label("Vorschlag: "), 0, row);
        gridPane.add(txtOffer, 1, row);
        gridPane.setAlignment(Pos.CENTER_RIGHT);

        gridPane.add(new Label("Aktiv:"), 0, ++row);
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(chkActive, lblActive);
        gridPane.add(hBox, 1, row);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(), P2GridConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);
        gridPane.disableProperty().bind(
                Bindings.createBooleanBinding(() -> offerDateProp.getValue() == null, offerDateProp)
                        .or(ProgConfig.SYSTEM_USE_OFFERTABLE.not()));
    }

    private void setActOfferData() {
        OfferData selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == offerDateProp.getValue()) {
            return;
        }

        unbindText();
        offerDateProp.setValue(selectedItem);

        if (offerDateProp.getValue() != null) {
            txtOffer.textProperty().bindBidirectional(offerDateProp.getValue().offerProperty());
            chkActive.selectedProperty().bindBidirectional(offerDateProp.getValue().activeProperty());
        }
        setLblText();
    }

    private void unbindText() {
        if (offerDateProp.getValue() != null) {
            txtOffer.textProperty().unbindBidirectional(offerDateProp.getValue().offerProperty());
            chkActive.selectedProperty().unbindBidirectional(offerDateProp.getValue().activeProperty());
        }
        txtOffer.setText("");
        chkActive.setSelected(false);
    }

    private void setLblText() {
        if (offerDateProp.getValue() != null) {
            lblActive.setText(offerDateProp.getValue().isActive() ? "" : "Der Eintrag wird nicht verwendet");
        } else {
            lblActive.setText("");
        }
    }
}
