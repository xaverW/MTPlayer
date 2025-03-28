/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.configdialog.panesetdata;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.mtplayer.gui.tools.HelpTextPset;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneSetFunction {
    private final Button btnPlay = new Button("Abspielen setzen");
    private final P2ToggleSwitch tglSave = new P2ToggleSwitch("Speichern:");
    private final P2ToggleSwitch tglButton = new P2ToggleSwitch("Button:");
    private final P2ToggleSwitch tglAbo = new P2ToggleSwitch("Abo:");
    private final ColorPicker colorPicker = new ColorPicker();
    private final Label lblPlay = new Label("Abspielen:");
    private ChangeListener changeListener;

    private final Stage stage;
    private final ProgData progData;
    private SetData setData = null;
    private final ObjectProperty<SetData> setDataObjectProperty;

    PaneSetFunction(Stage stage, ObjectProperty<SetData> setDataObjectProperty) {
        this.stage = stage;
        progData = ProgData.getInstance();
        this.setDataObjectProperty = setDataObjectProperty;
    }

    public void close() {
        unBindProgData();
    }

    public void makePane(Collection<TitledPane> result) {
        changeListener = (observable, oldValue, newValue) -> ProgData.getInstance().setDataList.setListChanged();

        VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(P2LibConst.PADDING));

        TitledPane tpConfig = new TitledPane("Funktionen", vBox);
        result.add(tpConfig);

        btnPlay.setTooltip(new Tooltip("Dieses Set zum Abspielen auswählen"));
        btnPlay.setOnAction(event -> {
            progData.setDataList.setPlay(setDataObjectProperty.getValue());
            playSetText();
        });
        playSetText();

        int row = 0;
        //Speichern, Button, Abo
        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);
        gridPane.add(new Label("Funktionen des Sets:"), 0, row, 3, 1);

        HBox hBox = new HBox(15);
        HBox hBoxSpace = new HBox();
        HBox.setHgrow(hBoxSpace, Priority.ALWAYS);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(lblPlay, hBoxSpace, btnPlay);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(hBox, 1, row);
        gridPane.add(P2Button.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_PLAY), 2, row);

        gridPane.add(tglSave, 1, ++row);
        gridPane.add(P2Button.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_SAVE), 2, row);

        gridPane.add(tglAbo, 1, ++row);
        gridPane.add(P2Button.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_ABO), 2, row);

        gridPane.add(tglButton, 1, ++row);
        gridPane.add(P2Button.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_BUTTON), 2, row);

        colorPicker.setOnAction(a -> {
//                    PListener.notify(PListener.EVENT_FILM_BUTTON_CHANGED, PaneSetFunction.class.getSimpleName());
                    progData.pEventHandler.notifyListener(PEvents.EVENT_FILM_BUTTON_CHANGED);
                }
        );
        Label lblColor = new Label("Farbe:");
        Button btnResetColor = new Button("_Standardfarbe");
        btnResetColor.setOnAction(event -> {
            setDataObjectProperty.getValue().setColor(SetData.RESET_COLOR);
//            PListener.notify(PListener.EVENT_FILM_BUTTON_CHANGED, PaneSetFunction.class.getSimpleName());
            progData.pEventHandler.notifyListener(PEvents.EVENT_FILM_BUTTON_CHANGED);
        });
        final Button btnHelpColor = P2Button.helpButton(stage, "Schriftfarbe auswählen",
                HelpText.SETDATA_RESET_COLOR);

        lblColor.disableProperty().bind(tglButton.selectedProperty().not());
        colorPicker.disableProperty().bind(tglButton.selectedProperty().not());
        btnResetColor.disableProperty().bind(tglButton.selectedProperty().not());

        HBox hBoxColor = new HBox(10);
        hBoxColor.getChildren().addAll(lblColor, colorPicker, btnResetColor);
        hBoxColor.setAlignment(Pos.CENTER_LEFT);
        gridPane.add(hBoxColor, 1, ++row);
        gridPane.add(btnHelpColor, 2, row);

        gridPane.getColumnConstraints().addAll(
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());

        setDataObjectProperty.addListener((u, o, n) -> {
            tpConfig.setDisable(setDataObjectProperty.getValue() == null);
            bindProgData();
        });
        tpConfig.setDisable(setDataObjectProperty.getValue() == null);
        bindProgData();
    }

    private void playSetText() {
        if (setData != null && setData.isPlay()) {
            lblPlay.getStyleClass().add("markSetPlay");
        } else {
            lblPlay.getStyleClass().removeAll("markSetPlay");
        }
    }

    private void bindProgData() {
        unBindProgData();
        setData = setDataObjectProperty.getValue();
        if (setData != null) {
            playSetText();
            //rbPlay.selectedProperty().bindBidirectional(setdata.playProperty());

            tglSave.selectedProperty().bindBidirectional(setData.saveProperty());
            tglAbo.selectedProperty().bindBidirectional(setData.aboProperty());

            tglButton.selectedProperty().bindBidirectional(setData.buttonProperty());
            tglButton.selectedProperty().addListener(changeListener);

            colorPicker.valueProperty().bindBidirectional(setData.colorProperty());
            colorPicker.valueProperty().addListener(changeListener);
        }
    }

    private void unBindProgData() {
        if (setData != null) {
            playSetText();
            //rbPlay.selectedProperty().unbindBidirectional(setdata.playProperty());

            tglSave.selectedProperty().unbindBidirectional(setData.saveProperty());
            tglAbo.selectedProperty().unbindBidirectional(setData.aboProperty());

            tglButton.selectedProperty().unbindBidirectional(setData.buttonProperty());
            tglButton.selectedProperty().removeListener(changeListener);

            colorPicker.valueProperty().unbindBidirectional(setData.colorProperty());
            colorPicker.valueProperty().removeListener(changeListener);
        }
    }
}
