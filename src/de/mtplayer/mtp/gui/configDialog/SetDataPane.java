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

package de.mtplayer.mtp.gui.configDialog;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class SetDataPane {


    //name
    private final TextField txtName = new TextField("");
    private final TextArea txtDescription = new TextArea("");
    private final PToggleSwitch tglSave = new PToggleSwitch("Speichern");
    private final PToggleSwitch tglButton = new PToggleSwitch("Button");
    private final PToggleSwitch tglAbo = new PToggleSwitch("Abo");
    private final ColorPicker colorPicker = new ColorPicker();
    //destination
    private final PToggleSwitch tglSubdir = new PToggleSwitch("Bei Abos Unterordner anlegen");
    private final TextField txtDestPath = new TextField();
    private final TextField txtDestName = new TextField();
    private final Slider slCut = new Slider();
    private final Slider slCutField = new Slider();
    //download
    private final TextField txtPraefix = new TextField();
    private final TextField txtSuffix = new TextField();
    private final RadioButton rbHd = new RadioButton("Film in HD laden");
    private final RadioButton rbHeight = new RadioButton("Film in hoher Auflösung laden");
    private final RadioButton rbLow = new RadioButton("Film in kleiner Auflösung laden");
    private final PToggleSwitch tglInfo = new PToggleSwitch("Infodatei anlegen: \"Filmname.txt\"");
    private final PToggleSwitch tglSubtitle = new PToggleSwitch("Untertitel speichern: \"Filmname.xxx\"");

    private SetData setData = null;
    private Collection<TitledPane> result;
    private ProgramPane programPane;
    private ChangeListener changeListener;
    private final Stage stage;

    public SetDataPane(Stage stage) {
        this.stage = stage;
        programPane = new ProgramPane(stage);
    }

    public void makeSetPane(Collection<TitledPane> result) {
        this.result = result;

        makeConfig(result);
        makeDest(result);
        makeDownload(result);
        programPane.makeProgs(result);

        changeListener = (observable, oldValue, newValue) -> ProgData.getInstance().setList.setListChanged();
        setDisable();
    }

    public void bindProgData(SetData setData) {
        unBindProgData();

        this.setData = setData;
        if (setData != null) {
            txtName.textProperty().bindBidirectional(setData.nameProperty());
            txtName.textProperty().addListener(changeListener);
            txtDescription.textProperty().bindBidirectional(setData.descriptionProperty());
            tglSave.selectedProperty().bindBidirectional(setData.saveProperty());
            tglButton.selectedProperty().bindBidirectional(setData.buttonProperty());
            tglAbo.selectedProperty().bindBidirectional(setData.aboProperty());
            colorPicker.valueProperty().bindBidirectional(setData.colorProperty());

            tglSubdir.selectedProperty().bindBidirectional(setData.genThemeProperty());
            txtDestPath.textProperty().bindBidirectional(setData.destPathProperty());
            txtDestName.textProperty().bindBidirectional(setData.destNameProperty());
            slCut.valueProperty().bindBidirectional(setData.maxSizeProperty());
            slCutField.valueProperty().bindBidirectional(setData.maxFieldProperty());

            txtPraefix.textProperty().bindBidirectional(setData.praefixProperty());
            txtSuffix.textProperty().bindBidirectional(setData.suffixProperty());

            switch (setData.getResolution()) {
                case Film.RESOLUTION_HD:
                    rbHd.setSelected(true);
                    break;
                case Film.RESOLUTION_SMALL:
                    rbLow.setSelected(true);
                    break;
                default:
                    rbHeight.setSelected(true);
                    break;
            }

            tglInfo.selectedProperty().bindBidirectional(setData.infoFileProperty());
            tglSubtitle.selectedProperty().bindBidirectional(setData.subtitleProperty());

            programPane.setSetDate(setData);
        }
        setDisable();
    }

    private void unBindProgData() {
        if (setData != null) {
            txtName.textProperty().unbindBidirectional(setData.nameProperty());
            txtName.textProperty().removeListener(changeListener);
            txtDescription.textProperty().unbindBidirectional(setData.descriptionProperty());
            tglSave.selectedProperty().unbindBidirectional(setData.saveProperty());
            tglButton.selectedProperty().unbindBidirectional(setData.buttonProperty());
            tglAbo.selectedProperty().unbindBidirectional(setData.aboProperty());
            colorPicker.valueProperty().unbindBidirectional(setData.colorProperty());

            tglSubdir.selectedProperty().unbindBidirectional(setData.genThemeProperty());
            txtDestPath.textProperty().unbindBidirectional(setData.destPathProperty());
            txtDestName.textProperty().unbindBidirectional(setData.destNameProperty());
            slCut.valueProperty().unbindBidirectional(setData.maxSizeProperty());
            slCutField.valueProperty().unbindBidirectional(setData.maxFieldProperty());

            txtPraefix.textProperty().unbindBidirectional(setData.praefixProperty());
            txtSuffix.textProperty().unbindBidirectional(setData.suffixProperty());

            tglInfo.selectedProperty().unbindBidirectional(setData.infoFileProperty());
            tglSubtitle.selectedProperty().unbindBidirectional(setData.subtitleProperty());
        }
    }

    public void setDisable() {
        for (TitledPane tp : result) {
            tp.setDisable(setData == null);
        }
    }

    private void makeConfig(Collection<TitledPane> result) {
        VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(10));

        TitledPane tpConfig = new TitledPane("Set Einstellungen", vBox);
        result.add(tpConfig);

        // Name, Beschreibung
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        gridPane.add(new Label("Set Name:"), 0, 0);
        gridPane.add(txtName, 1, 0);

        gridPane.add(new Label("Beschreibung:"), 0, 1);
        gridPane.add(txtDescription, 1, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());


        //Speichern, Button, Abo
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        gridPane.add(tglSave, 0, 0);
        gridPane.add(tglButton, 0, 1);
        gridPane.add(tglAbo, 0, 2);


        //Farbe
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        Button btnResetColor = new Button("Standardfarbe");
        btnResetColor.setOnAction(event -> {
            setData.setColor(SetData.RESET_COLOR);
        });
        final Button btnHelpColor = new PButton().helpButton(stage, "Schriftfarbe auswählen",
                HelpText.SETDATA_RESET_COLOR);

        gridPane.add(new Label("Farbe des Button:"), 0, 0);
        gridPane.add(colorPicker, 1, 0);
        gridPane.add(btnResetColor, 2, 0);
        gridPane.add(btnHelpColor, 3, 0);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcComputedSizeAndHgrow());
    }

    private void makeDest(Collection<TitledPane> result) {
        VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(10));

        TitledPane tpConfig = new TitledPane("Speicherziel", vBox);
        result.add(tpConfig);

        // Unterordner
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        final Button btnHelpColor = new PButton().helpButton(stage, "Unterordner anlegen",
                HelpText.SETDATA_SUBDIR);
        GridPane.setHalignment(btnHelpColor, HPos.RIGHT);

        gridPane.add(tglSubdir, 0, 0);
        gridPane.add(btnHelpColor, 1, 0);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        // path/name
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);


        final Button btnFile = new Button();
        btnFile.setOnAction(event -> DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtDestPath));
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Ordner zum Speichern der Filme auswählen."));


        gridPane.add(new Label("Zielpfad:"), 0, 0);
        gridPane.add(txtDestPath, 1, 0);
        gridPane.add(btnFile, 2, 0);

        gridPane.add(new Label("Zieldateiname:"), 0, 1);
        gridPane.add(txtDestName, 1, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());


        // cut
        gridPane = new GridPane();
        gridPane.setHgap(25);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        slCut.setMin(0);
        slCut.setMax(ProgConst.LAENGE_DATEINAME_MAX);
        slCut.setShowTickLabels(true);
        slCut.setMinorTickCount(50);
        slCut.setMajorTickUnit(100);
        slCut.setBlockIncrement(10);
        slCut.setSnapToTicks(true);

        Label lblTxtAll = new Label("Länge des ganzen Dateinamen:");
        Label lblSizeAll = new Label();
        slCut.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider(slCut, lblSizeAll, ""));
        setValueSlider(slCut, lblSizeAll, "");


        slCutField.setMin(0);
        slCutField.setMax(ProgConst.LAENGE_FELD_MAX);
        slCutField.setShowTickLabels(true);
        slCutField.setMinorTickCount(25);
        slCutField.setMajorTickUnit(50);
        slCutField.setBlockIncrement(10);
        slCutField.setSnapToTicks(true);

        Label lblTxtField = new Label("Länge einzelner Felder:");
        Label lblSizeField = new Label();
        slCutField.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider(slCutField, lblSizeField, ""));
        setValueSlider(slCutField, lblSizeField, "");

        GridPane.setValignment(lblTxtAll, VPos.CENTER);
        GridPane.setValignment(lblSizeAll, VPos.CENTER);
        GridPane.setValignment(slCutField, VPos.CENTER);
        GridPane.setValignment(lblTxtField, VPos.CENTER);
        GridPane.setValignment(lblSizeField, VPos.CENTER);
        GridPane.setValignment(slCutField, VPos.CENTER);

        gridPane.add(lblTxtAll, 0, 0);
        gridPane.add(slCut, 1, 0);
        gridPane.add(lblSizeAll, 2, 0);

        gridPane.add(lblTxtField, 0, 1);
        gridPane.add(slCutField, 1, 1);
        gridPane.add(lblSizeField, 2, 1);
    }

    private void makeDownload(Collection<TitledPane> result) {
        VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(10));

        TitledPane tpConfig = new TitledPane("Download", vBox);
        result.add(tpConfig);

        // praefix/suffix
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        final Button btnHelpPraefix = new PButton().helpButton(stage, "Direkt speichern",
                HelpText.SETDATA_PRAEFIX);
        GridPane.setHalignment(btnHelpPraefix, HPos.RIGHT);
        Label lbl = new Label("direkt Speichern (vom Programm selbst):");

        int row = 0;
        gridPane.add(lbl, 0, row, 2, 1);
        gridPane.add(btnHelpPraefix, 2, row);

        gridPane.add(new Label("Präfix (z.B. http):"), 0, ++row);
        gridPane.add(txtPraefix, 1, row, 2, 1);

        gridPane.add(new Label("Suffix (z.B. mp4,mp3):"), 0, ++row);
        gridPane.add(txtSuffix, 1, row, 2, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        // Auflösung
        ToggleGroup tg = new ToggleGroup();
        rbHd.setToggleGroup(tg);
        rbHeight.setToggleGroup(tg);
        rbLow.setToggleGroup(tg);
        rbHd.setOnAction(event -> setResolution());
        rbHeight.setOnAction(event -> setResolution());
        rbLow.setOnAction(event -> setResolution());

        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        final Button btnHelpRes = new PButton().helpButton(stage, "Auflösung",
                HelpText.SETDATA_RES);
        GridPane.setHalignment(btnHelpRes, HPos.RIGHT);

        gridPane.add(rbHd, 0, 0);
        gridPane.add(btnHelpRes, 1, 0);
        gridPane.add(rbHeight, 0, 1);
        gridPane.add(rbLow, 0, 2);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        // Infodateien
        gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        gridPane.add(tglInfo, 0, 0);
        gridPane.add(tglSubtitle, 0, 1);
    }

    private void setResolution() {
        if (rbHeight.isSelected()) {
            setData.setResolution(Film.RESOLUTION_NORMAL);
        }
        if (rbHd.isSelected()) {
            setData.setResolution(Film.RESOLUTION_HD);
        }
        if (rbLow.isSelected()) {
            setData.setResolution(Film.RESOLUTION_SMALL);
        }
    }


    private void setValueSlider(Slider sl, Label lb, String pre) {
        int days = (int) sl.getValue();
        lb.setText(pre + (days == 0 ? "nicht beschränken" : "auf " + days + " Zeichen beschränken"));
    }

}
