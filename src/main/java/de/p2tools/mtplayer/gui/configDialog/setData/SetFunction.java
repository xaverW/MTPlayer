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


package de.p2tools.mtplayer.gui.configDialog.setData;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.mtplayer.gui.tools.HelpTextPset;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
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

public class SetFunction {
    private final RadioButton rbPlay = new RadioButton("");
    private final PToggleSwitch tglSave = new PToggleSwitch("Speichern:");
    private final PToggleSwitch tglButton = new PToggleSwitch("Button:");
    private final PToggleSwitch tglAbo = new PToggleSwitch("Abo:");
    private final ColorPicker colorPicker = new ColorPicker();
    private ChangeListener changeListener;

    private final Stage stage;
    private SetData setData = null;
    private final ProgData progData;

    SetFunction(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();
    }

    public void close() {
        unBindProgData();
    }

    public void makePane(Collection<TitledPane> result) {
        changeListener = (observable, oldValue, newValue) -> ProgData.getInstance().setDataList.setListChanged();

        VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(10));

        TitledPane tpConfig = new TitledPane("Set Funktionen", vBox);
        result.add(tpConfig);

        rbPlay.setTooltip(new Tooltip("Dieses Set zum Abspielen auswählen"));
        rbPlay.setOnAction(event -> {
            progData.setDataList.setPlay(setData);
        });

        int row = 0;
        //Speichern, Button, Abo
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);
        gridPane.add(new Label("Funktionen des Sets:"), 0, row, 4, 1);

        HBox hBox = new HBox(15);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(new Label("Abspielen:"), rbPlay);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(hBox, 1, row);
        gridPane.add(PButton.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_PLAY), 4, row);

        gridPane.add(tglSave, 1, ++row);
        gridPane.add(tglAbo, 2, row);
        gridPane.add(tglButton, 3, row);
        gridPane.add(PButton.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_SAVE_ABO_BUTTON), 4, row);

        Label lblColor = new Label("Farbe des Button:");
        Button btnResetColor = new Button("_Standardfarbe");
        btnResetColor.setOnAction(event -> {
            setData.setColor(SetData.RESET_COLOR);
        });
        final Button btnHelpColor = PButton.helpButton(stage, "Schriftfarbe auswählen",
                HelpText.SETDATA_RESET_COLOR);

        lblColor.disableProperty().bind(tglButton.selectedProperty().not());
        colorPicker.disableProperty().bind(tglButton.selectedProperty().not());
        btnResetColor.disableProperty().bind(tglButton.selectedProperty().not());

        HBox hBoxColor = new HBox(10);
        HBox hBoxSpace = new HBox();
        HBox.setHgrow(hBoxSpace, Priority.ALWAYS);
        hBoxColor.getChildren().addAll(lblColor, colorPicker, btnResetColor, hBoxSpace, btnHelpColor);
        gridPane.add(hBoxColor, 1, ++row, 4, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrowRight());
    }

    public void bindProgData(SetData setData) {
        unBindProgData();

        this.setData = setData;
        if (setData != null) {
            rbPlay.selectedProperty().bindBidirectional(setData.playProperty());

            tglSave.selectedProperty().bindBidirectional(setData.saveProperty());
            tglAbo.selectedProperty().bindBidirectional(setData.aboProperty());

            tglButton.selectedProperty().bindBidirectional(setData.buttonProperty());
            tglButton.selectedProperty().addListener(changeListener);

            colorPicker.valueProperty().bindBidirectional(setData.colorProperty());
            colorPicker.valueProperty().addListener(changeListener);
        }
    }

    void unBindProgData() {
        if (setData != null) {
            rbPlay.selectedProperty().unbindBidirectional(setData.playProperty());

            tglSave.selectedProperty().unbindBidirectional(setData.saveProperty());
            tglAbo.selectedProperty().unbindBidirectional(setData.aboProperty());

            tglButton.selectedProperty().unbindBidirectional(setData.buttonProperty());
            tglButton.selectedProperty().removeListener(changeListener);

            colorPicker.valueProperty().unbindBidirectional(setData.colorProperty());
            colorPicker.valueProperty().removeListener(changeListener);
        }
    }
}
