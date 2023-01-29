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
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class FilmTitle {

    private final TextField txtNot = new TextField();
    private final GridPane gridPane = new GridPane();

    private ListView<MyStringProperty> listView = new ListView<>();
    private StringProperty strPropNot = new SimpleStringProperty("");
    private final PToggleSwitch tglNot = new PToggleSwitch("Filmtitel ausschließen");

    private final Stage stage;
    private final ProgData progData;
    private final ObservableList<MyStringProperty> notList = FXCollections.observableArrayList();

    public FilmTitle(Stage stage, ProgData progData) {
        this.stage = stage;
        this.progData = progData;
        ObservableList<String> list = ProgConfig.SYSTEM_LOAD_NOT_FILMLIST_TITEL;
        list.stream().forEach(s -> {
            MyStringProperty st = new MyStringProperty(s);
            notList.add(st);
        });
    }

    public void make(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(10);

        makeToggle(vBox);
        initListView(vBox);
        addConfigs(vBox);

        TitledPane tpReplace = new TitledPane("Filmtitel ausschließen", vBox);
        result.add(tpReplace);
        tpReplace.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpReplace, Priority.ALWAYS);
    }

    public void close() {
        ProgConfig.SYSTEM_LOAD_NOT_FILMLIST_TITEL.clear();
        notList.stream().forEach(sp -> {
            ProgConfig.SYSTEM_LOAD_NOT_FILMLIST_TITEL.add(sp.getValueSafe());
        });
        tglNot.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_USE_FILMTITLE_NOT_LOAD);
        unbindText();
    }

    private void makeToggle(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        tglNot.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_USE_FILMTITLE_NOT_LOAD);
        final Button btnHelpReplace = PButton.helpButton(stage, "Filmtitel ausschließen",
                HelpText.FILMTITEL_NOT_LOAD);

        gridPane.add(tglNot, 0, 1);
        gridPane.add(btnHelpReplace, 1, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
    }

    private void initListView(VBox vBox) {
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        listView.setItems(notList);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setActSelData());
        listView.disableProperty().bind(ProgConfig.SYSTEM_USE_FILMTITLE_NOT_LOAD.not());
        VBox.setVgrow(listView, Priority.ALWAYS);
        vBox.getChildren().addAll(listView);

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Eintrag löschen"));
        btnDel.setGraphic(ProgIcons.Icons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<MyStringProperty> sels = listView.getSelectionModel().getSelectedItems();
            if (sels == null || sels.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                notList.removeAll(sels);
                listView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setTooltip(new Tooltip("Einen neuen Eintrag erstellen"));
        btnNew.setGraphic(ProgIcons.Icons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            MyStringProperty str = new MyStringProperty("");
            notList.add(str);

            listView.getSelectionModel().clearSelection();
            listView.getSelectionModel().select(str);
            listView.scrollTo(str);
        });

        Button btnUp = new Button("");
        btnUp.setTooltip(new Tooltip("Eintrag nach oben schieben"));
        btnUp.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_UP.getImageView());
        btnUp.setOnAction(event -> {
            final int sel = listView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = up(sel, true);
                listView.getSelectionModel().select(res);
            }
        });

        Button btnDown = new Button("");
        btnDown.setTooltip(new Tooltip("Eintrag nach unten schieben"));
        btnDown.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_DOWN.getImageView());
        btnDown.setOnAction(event -> {
            final int sel = listView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = up(sel, false);
                listView.getSelectionModel().select(res);
            }
        });

        Button btnTop = new Button();
        btnTop.setTooltip(new Tooltip("Eintrag an den Anfang verschieben"));
        btnTop.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_TOP.getImageView());
        btnTop.setOnAction(event -> {
            final int sel = listView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = top(sel, true);
                listView.getSelectionModel().select(res);
            }
        });

        Button btnBottom = new Button();
        btnBottom.setTooltip(new Tooltip("Eintrag an das Ende verschieben"));
        btnBottom.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_BOTTOM.getImageView());
        btnBottom.setOnAction(event -> {
            final int sel = listView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = top(sel, false);
                listView.getSelectionModel().select(res);
            }
        });

        Button btnReset = new Button("_Tabelle zurücksetzen");
        btnReset.setTooltip(new Tooltip("Alle Einträge löschen und Standardeinträge wieder herstellen"));
        btnReset.setOnAction(event -> {
            initList();
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.disableProperty().bind(ProgConfig.SYSTEM_USE_FILMTITLE_NOT_LOAD.not());
        hBox.getChildren().addAll(btnNew, btnDel, btnTop, btnUp, btnDown, btnBottom, btnReset);
        vBox.getChildren().addAll(hBox);
    }

    private void addConfigs(VBox vBox) {
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20));

        gridPane.add(new Label("Text ausschließen"), 0, 0);
        gridPane.add(txtNot, 1, 0);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);
        gridPane.disableProperty().bind(
                Bindings.createBooleanBinding(() -> strPropNot.getValue() == null, strPropNot)
                        .or(ProgConfig.SYSTEM_USE_FILMTITLE_NOT_LOAD.not()));
    }

    private void setActSelData() {
        StringProperty str = listView.getSelectionModel().getSelectedItem();
        if (str == strPropNot) {
            return;
        }

        unbindText();
        strPropNot = str;

        if (strPropNot != null && strPropNot.getValue() != null) {
            txtNot.textProperty().bindBidirectional(strPropNot);
        }
    }

    private void unbindText() {
        if (strPropNot != null && strPropNot.getValue() != null) {
            txtNot.textProperty().unbindBidirectional(strPropNot);
        }
        txtNot.setText("");
    }

    private void initList() {
        notList.clear();
        notList.add(new MyStringProperty("(mit Gebärdensprache)"));
        notList.add(new MyStringProperty("(mit Untertitel)"));
    }

    private int top(int idx, boolean up) {
        MyStringProperty replace = notList.remove(idx);
        int ret;
        if (up) {
            notList.add(0, replace);
            ret = 0;
        } else {
            notList.add(replace);
            ret = notList.size() - 1;
        }
        return ret;
    }

    private int up(int idx, boolean up) {
        MyStringProperty replace = notList.remove(idx);
        int neu = idx;
        if (up) {
            if (neu > 0) {
                --neu;
            }
        } else if (neu < notList.size()) {
            ++neu;
        }
        notList.add(neu, replace);
        return neu;
    }

    private class MyStringProperty extends SimpleStringProperty {
        MyStringProperty(String s) {
            setValue(s);
        }

        @Override
        public String toString() {
            return getValueSafe();
        }
    }
}
