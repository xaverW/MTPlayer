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
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class SetPane {
    private final TextField txtVisibleName = new TextField("");
    private final TextArea txtDescription = new TextArea("");
    private final RadioButton rbPlay = new RadioButton("");
    private final PToggleSwitch tglSave = new PToggleSwitch("Speichern:");
    private final PToggleSwitch tglButton = new PToggleSwitch("Button:");
    private final PToggleSwitch tglAbo = new PToggleSwitch("Abo:");
    private final ColorPicker colorPicker = new ColorPicker();
    private ChangeListener changeListener;

    private final Stage stage;
    private SetData setData = null;
    private final ProgData progData;

    SetPane(Stage stage) {
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

        TitledPane tpConfig = new TitledPane("Set Einstellungen", vBox);
        result.add(tpConfig);

        rbPlay.setTooltip(new Tooltip("Dieses Set zum Abspielen auswählen"));
        rbPlay.setOnAction(event -> {
            progData.setDataList.setPlay(setData);
        });


        // Name, Beschreibung
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        gridPane.add(new Label("Set Name:"), 0, 0);
        gridPane.add(txtVisibleName, 1, 0);

        gridPane.add(new Label("Beschreibung:"), 0, 1);
        gridPane.add(txtDescription, 1, 1);

        for (int i = 0; i < gridPane.getRowCount(); ++i) {
            RowConstraints rowC = new RowConstraints();
            rowC.setValignment(VPos.CENTER);
            gridPane.getRowConstraints().add(rowC);
        }
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());


        //Speichern, Button, Abo
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);
        gridPane.add(new Label("Funktionen des Sets:"), 0, 0, 4, 1);

        gridPane.add(new Label(" "), 0, 1);
        gridPane.add(new Label("Abspielen:"), 1, 1);
        gridPane.add(rbPlay, 2, 1);
        gridPane.add(PButton.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_PLAY), 3, 1);

        gridPane.add(tglSave, 1, 2, 2, 1);
        gridPane.add(PButton.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_SAVE), 3, 2);

        gridPane.add(tglAbo, 1, 3, 2, 1);
        gridPane.add(PButton.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_ABO), 3, 3);

        gridPane.add(tglButton, 1, 4, 2, 1);
        gridPane.add(PButton.helpButton(stage, "Funktion des Sets",
                HelpTextPset.HELP_PSET_BUTTON), 3, 4);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSizeCenter(), PColumnConstraints.getCcComputedSizeAndHgrowRight());

        //Farbe
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        Button btnResetColor = new Button("_Standardfarbe");
        btnResetColor.setOnAction(event -> {
            setData.setColor(SetData.RESET_COLOR);
        });
        final Button btnHelpColor = PButton.helpButton(stage, "Schriftfarbe auswählen",
                HelpText.SETDATA_RESET_COLOR);

        Label lblColor = new Label("Farbe des Button:");

        lblColor.disableProperty().bind(tglButton.selectedProperty().not());
        colorPicker.disableProperty().bind(tglButton.selectedProperty().not());
        btnResetColor.disableProperty().bind(tglButton.selectedProperty().not());

        gridPane.add(lblColor, 0, 0);
        gridPane.add(colorPicker, 1, 0);
        gridPane.add(btnResetColor, 2, 0);
        gridPane.add(btnHelpColor, 3, 0);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcComputedSizeAndHgrow());
    }

    public void bindProgData(SetData setData) {
        unBindProgData();

        this.setData = setData;
        if (setData != null) {
            txtVisibleName.textProperty().bindBidirectional(setData.visibleNameProperty());
            txtVisibleName.textProperty().addListener(changeListener);

            txtDescription.textProperty().bindBidirectional(setData.descriptionProperty());

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
            txtVisibleName.textProperty().unbindBidirectional(setData.visibleNameProperty());
            txtVisibleName.textProperty().removeListener(changeListener);

            txtDescription.textProperty().unbindBidirectional(setData.descriptionProperty());

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
