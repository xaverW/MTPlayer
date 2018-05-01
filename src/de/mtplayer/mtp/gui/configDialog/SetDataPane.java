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
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.ToggleSwitch;

import java.util.Collection;

public class SetDataPane {


    //name
    private final TextField txtName = new TextField("");
    private final TextArea txtDescription = new TextArea("");
    private final ToggleSwitch tglSave = new ToggleSwitch("Speichern");
    private final ToggleSwitch tglButton = new ToggleSwitch("Button");
    private final ToggleSwitch tglAbo = new ToggleSwitch("Abo");
    private final ColorPicker colorPicker = new ColorPicker();
    //destination
    private final ToggleSwitch tglSubdir = new ToggleSwitch("Unterordner anlegen");
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
    private final ToggleSwitch tglInfo = new ToggleSwitch("Infodatei anlegen: \"Filmname.txt\"");
    private final ToggleSwitch tglSubtitle = new ToggleSwitch("Untertitel speichern: \"Filmname.xxx\"");

    private SetData setData = null;
    private Collection<TitledPane> result;
    private ProgPane progPane = new ProgPane();
    private final ColumnConstraints ccTxt = new ColumnConstraints();
    private ChangeListener cl;

    public void makeSetPane(Collection<TitledPane> result) {
        this.result = result;

        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);

        makeConfig(result);
        makeDest(result);
        makeDownload(result);
        progPane.makeProgs(result);

        cl = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                ProgData.getInstance().setList.setListChanged();
            }
        };

        setDisable();
    }

    public void bindProgData(SetData setData) {
        unBindProgData();

        this.setData = setData;
        if (setData != null) {
            txtName.textProperty().bindBidirectional(setData.nameProperty());
            txtName.textProperty().addListener(cl);
            txtDescription.textProperty().bindBidirectional(setData.descriptonProperty());
            tglSave.selectedProperty().bindBidirectional(setData.saveProperty());
            tglButton.selectedProperty().bindBidirectional(setData.buttonProperty());
            tglAbo.selectedProperty().bindBidirectional(setData.aboProperty());
            colorPicker.valueProperty().bindBidirectional(setData.colorProperty());

            tglSubdir.selectedProperty().bindBidirectional(setData.genThemaProperty());
            txtDestPath.textProperty().bindBidirectional(setData.destPathProperty());
            txtDestName.textProperty().bindBidirectional(setData.destNameProperty());
            slCut.valueProperty().bindBidirectional(setData.maxSizeProperty());
            slCutField.valueProperty().bindBidirectional(setData.maxFieldProperty());

            txtPraefix.textProperty().bindBidirectional(setData.praefixProperty());
            txtSuffix.textProperty().bindBidirectional(setData.suffixProperty());

            switch (setData.getResolution()) {
                case FilmXml.AUFLOESUNG_HD:
                    rbHd.setSelected(true);
                    break;
                case FilmXml.AUFLOESUNG_KLEIN:
                    rbLow.setSelected(true);
                    break;
                default:
                    rbHeight.setSelected(true);
                    break;
            }

            tglInfo.selectedProperty().bindBidirectional(setData.infoFileProperty());
            tglSubtitle.selectedProperty().bindBidirectional(setData.subtitleProperty());

            progPane.setSetDate(setData);
        }
        setDisable();
    }

    private void unBindProgData() {
        if (setData != null) {
            txtName.textProperty().unbindBidirectional(setData.nameProperty());
            txtName.textProperty().removeListener(cl);
            txtDescription.textProperty().unbindBidirectional(setData.descriptonProperty());
            tglSave.selectedProperty().unbindBidirectional(setData.saveProperty());
            tglButton.selectedProperty().unbindBidirectional(setData.buttonProperty());
            tglAbo.selectedProperty().unbindBidirectional(setData.aboProperty());
            colorPicker.valueProperty().unbindBidirectional(setData.colorProperty());

            tglSubdir.selectedProperty().unbindBidirectional(setData.genThemaProperty());
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

        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        TitledPane tpConfig = new TitledPane("Set Einstellungen", vBox);
        result.add(tpConfig);

        // Name, Beschreibung
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        gridPane.add(new Label("Set Name:"), 0, 0);
        gridPane.add(txtName, 1, 0);

        gridPane.add(new Label("Beschreibung:"), 0, 1);
        gridPane.add(txtDescription, 1, 1);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);

        //Speichern, Button, Abo
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        tglSave.setMaxWidth(Double.MAX_VALUE);
        tglButton.setMaxWidth(Double.MAX_VALUE);
        tglAbo.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(tglSave, 0, 0);
        gridPane.add(tglButton, 0, 1);
        gridPane.add(tglAbo, 0, 2);


        //Farbe
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        gridPane.add(new Label("Farbe des Button:"), 0, 0);
        gridPane.add(colorPicker, 1, 0);
        Button btnResetColor = new Button("Standardfarbe");
        btnResetColor.setOnAction(event -> {
            setData.setColor(SetData.RESET_COLOR);
        });
        gridPane.add(btnResetColor, 2, 0);
        final Button btnHelpColor = new Button("");
        btnHelpColor.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpColor.setOnAction(a -> new MTAlert().showHelpAlert("Schriftfarbe auswählen",
                HelpText.SETDATA_RESET_COLOR));
        gridPane.add(btnHelpColor, 3, 0);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(), ccTxt);
    }

    private void makeDest(Collection<TitledPane> result) {

        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        TitledPane tpConfig = new TitledPane("Speicherziel", vBox);
        result.add(tpConfig);

        // Unterordner
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        gridPane.add(tglSubdir, 0, 0);
        final Button btnHelpColor = new Button("");
        btnHelpColor.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpColor.setOnAction(a -> new MTAlert().showHelpAlert("Unterordner anlegen",
                HelpText.SETDATA_SUBDIR));

        GridPane.setHalignment(btnHelpColor, HPos.RIGHT);
        gridPane.add(btnHelpColor, 1, 0);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);

        // path/name
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        gridPane.add(new Label("Zielpfad:"), 0, 0);
        gridPane.add(txtDestPath, 1, 0);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtDestPath);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        gridPane.add(btnFile, 2, 0);

        gridPane.add(new Label("Zieldateiname:"), 0, 1);
        gridPane.add(txtDestName, 1, 1);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);


        // cut
        gridPane = new GridPane();
        gridPane.setHgap(25);
        gridPane.setVgap(25);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        Label lblSizeAll = new Label();
        slCut.setMin(0);
        slCut.setMax(ProgConst.LAENGE_DATEINAME_MAX);
        slCut.setShowTickLabels(true);
        slCut.setMinorTickCount(50);
        slCut.setMajorTickUnit(100);
        slCut.setBlockIncrement(10);
        slCut.setSnapToTicks(true);

        slCut.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider(slCut, lblSizeAll, ""));
        setValueSlider(slCut, lblSizeAll, "");

        Label lblTxtAll = new Label("Länge des ganzen Dateinamen:");
        GridPane.setValignment(lblTxtAll, VPos.CENTER);
        gridPane.add(lblTxtAll, 0, 0);
        gridPane.add(slCut, 1, 0);
        GridPane.setValignment(lblSizeAll, VPos.CENTER);
        gridPane.add(lblSizeAll, 2, 0);


        Label lblSizeField = new Label();
        slCutField.setMin(0);
        slCutField.setMax(ProgConst.LAENGE_FELD_MAX);
        slCutField.setShowTickLabels(true);
        slCutField.setMinorTickCount(25);
        slCutField.setMajorTickUnit(50);
        slCutField.setBlockIncrement(10);
        slCutField.setSnapToTicks(true);

        slCutField.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider(slCutField, lblSizeField, ""));
        setValueSlider(slCutField, lblSizeField, "");

        Label lblTxtField = new Label("Länge einzelner Felder:");
        GridPane.setValignment(lblTxtField, VPos.CENTER);
        gridPane.add(lblTxtField, 0, 1);
        gridPane.add(slCutField, 1, 1);
        GridPane.setValignment(lblSizeField, VPos.CENTER);
        gridPane.add(lblSizeField, 2, 1);

    }

    private void makeDownload(Collection<TitledPane> result) {

        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        TitledPane tpConfig = new TitledPane("Download", vBox);
        result.add(tpConfig);

        // praefix/suffix
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        final Button btnHelpPraefix = new Button("");
        btnHelpPraefix.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpPraefix.setOnAction(a -> new MTAlert().showHelpAlert("Direkt speichern",
                HelpText.SETDATA_PRAEFIX));

        Label lbl = new Label("direkt Speichern (vom Programm selbst):");
        lbl.setMaxWidth(Double.MAX_VALUE);

        HBox hBox1 = new HBox();
        hBox1.setMaxWidth(Double.MAX_VALUE);
        hBox1.getChildren().addAll(lbl, btnHelpPraefix);
        HBox.setHgrow(lbl, Priority.ALWAYS);

        gridPane.add(hBox1, 0, 0, 2, 1);

        gridPane.add(new Label("Präfix (z.B. http):"), 0, 1);
        gridPane.add(txtPraefix, 1, 1);

        gridPane.add(new Label("Suffix (z.B. mp4,mp3):"), 0, 2);
        gridPane.add(txtSuffix, 1, 2);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);

        // Auflösung
        ToggleGroup tg = new ToggleGroup();
        rbHd.setToggleGroup(tg);
        rbHeight.setToggleGroup(tg);
        rbLow.setToggleGroup(tg);
        rbHd.setOnAction(event -> setAufloesung());
        rbHeight.setOnAction(event -> setAufloesung());
        rbLow.setOnAction(event -> setAufloesung());

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        final Button btnHelpRes = new Button("");
        btnHelpRes.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpRes.setOnAction(a -> new MTAlert().showHelpAlert("Auflösung",
                HelpText.SETDATA_RES));

        gridPane.add(rbHd, 0, 0);
        gridPane.add(btnHelpRes, 1, 0);
        gridPane.add(rbHeight, 0, 1);
        gridPane.add(rbLow, 0, 2);

        gridPane.getColumnConstraints().addAll(ccTxt);

        // Infodateien
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        vBox.getChildren().add(gridPane);

        tglInfo.setMaxWidth(Double.MAX_VALUE);
        tglSubtitle.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(tglInfo, 0, 0);
        gridPane.add(tglSubtitle, 0, 1);
    }

    private void setAufloesung() {
        if (rbHeight.isSelected()) {
            setData.setResolution(FilmXml.AUFLOESUNG_NORMAL);
        }
        if (rbHd.isSelected()) {
            setData.setResolution(FilmXml.AUFLOESUNG_HD);
        }
        if (rbLow.isSelected()) {
            setData.setResolution(FilmXml.AUFLOESUNG_KLEIN);
        }
    }


    private void setValueSlider(Slider sl, Label lb, String pre) {
        int days = (int) sl.getValue();
        lb.setText(pre + (days == 0 ? "nicht beschränken" : "auf " + days + " Zeichen beschränken"));
    }

}
