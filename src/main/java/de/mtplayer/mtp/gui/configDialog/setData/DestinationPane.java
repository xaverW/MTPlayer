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


package de.mtplayer.mtp.gui.configDialog.setData;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.gui.tools.HelpTextPset;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PComboBoxObject;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class DestinationPane {
    private final PToggleSwitch tglSubdir = new PToggleSwitch("Bei Abos Unterordner anlegen");
    private final PComboBoxObject<AboSubDir.DirName> cboDest = new PComboBoxObject();
    private final TextField txtDestPath = new TextField();
    private final TextField txtDestName = new TextField();
    private final Slider slCut = new Slider();
    private final Slider slCutField = new Slider();
    private boolean changeTgl = false;

    private final Stage stage;
    private SetData setData = null;

    DestinationPane(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        unBindProgData();
    }

    public void makePane(Collection<TitledPane> result) {
        VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(10));

        TitledPane tpConfig = new TitledPane("Speicherziel", vBox);
        result.add(tpConfig);

        final Button btnHelpColor = PButton.helpButton(stage, "Unterordner anlegen",
                HelpText.SETDATA_ABO_SUBDIR);
        GridPane.setHalignment(btnHelpColor, HPos.RIGHT);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtDestPath));
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Ordner zum Speichern der Filme auswählen"));

        final Button btnHelpDest = PButton.helpButton(stage, "Zieldateiname",
                HelpTextPset.PSET_FILE_NAME);

        cboDest.init(FXCollections.observableArrayList(AboSubDir.DirName.values()));
        cboDest.getSelValueProperty().addListener((m, o, n) -> {
            if (changeTgl) {
                tglSubdir.setSelected(true);
            }
        });


        int row = 0;
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        // Unterordner
        gridPane.add(tglSubdir, 0, row, 2, 1);
        gridPane.add(btnHelpColor, 2, row);

        gridPane.add(new Label("Ordnername:"), 0, ++row);
        gridPane.add(cboDest, 1, row);

        // path/name
        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("Zielpfad:"), 0, ++row);
        gridPane.add(txtDestPath, 1, row);
        gridPane.add(btnFile, 2, row);

        gridPane.add(new Label("Zieldateiname:"), 0, ++row);
        gridPane.add(txtDestName, 1, row);
        gridPane.add(btnHelpDest, 2, row);

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

    private void setValueSlider(Slider sl, Label lb, String pre) {
        int days = (int) sl.getValue();
        lb.setText(pre + (days == 0 ? "nicht beschränken" : "auf " + days + " Zeichen beschränken"));
    }

    public void bindProgData(SetData setData) {
        unBindProgData();

        this.setData = setData;
        if (setData != null) {
            tglSubdir.selectedProperty().bindBidirectional(setData.genAboSubDirProperty());
            txtDestPath.textProperty().bindBidirectional(setData.destPathProperty());
            txtDestName.textProperty().bindBidirectional(setData.destNameProperty());
            slCut.valueProperty().bindBidirectional(setData.maxSizeProperty());
            slCutField.valueProperty().bindBidirectional(setData.maxFieldProperty());
            cboDest.bindSelValueProperty(setData.aboSubDirProperty());
            changeTgl = true;
        }
    }

    void unBindProgData() {
        changeTgl = false;
        if (setData != null) {
            tglSubdir.selectedProperty().unbindBidirectional(setData.genAboSubDirProperty());
            txtDestPath.textProperty().unbindBidirectional(setData.destPathProperty());
            txtDestName.textProperty().unbindBidirectional(setData.destNameProperty());
            slCut.valueProperty().unbindBidirectional(setData.maxSizeProperty());
            slCutField.valueProperty().unbindBidirectional(setData.maxFieldProperty());
            cboDest.unbindSelValueProperty();
        }
    }
}
