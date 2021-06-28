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

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.mtplayer.gui.tools.HelpTextPset;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PComboBoxObject;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class DestinationPane {
    private final PToggleSwitch tglSubdir = new PToggleSwitch("Bei Abos Unterordner anlegen:");
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

        final Button btnFile = new Button();
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Ordner zum Speichern der Filme auswählen"));
        btnFile.setOnAction(event -> PDirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtDestPath));

        final Button btnHelSubDir = PButton.helpButton(stage, "Unterordner anlegen",
                HelpText.SETDATA_ABO_SUBDIR);
        final Button btnHelpDestName = PButton.helpButton(stage, "Zieldateiname",
                HelpTextPset.PSET_FILE_NAME);

        cboDest.init(FXCollections.observableArrayList(AboSubDir.DirName.values()));
        cboDest.getSelValueProperty().addListener((m, o, n) -> {
            if (changeTgl) {
                tglSubdir.setSelected(true);
            }
        });
        cboDest.setMaxWidth(Double.MAX_VALUE);
        cboDest.disableProperty().bind(tglSubdir.selectedProperty().not());

        int row = 0;
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        // Unterordner
//        HBox hBox = new HBox(25);
//        HBox.setHgrow(cboDest, Priority.ALWAYS);
//        hBox.getChildren().addAll(tglSubdir, cboDest);
//        gridPane.add(hBox, 0, row, 2, 1);

        gridPane.add(tglSubdir, 0, row, 2, 1);
        gridPane.add(btnHelSubDir, 2, row);

        gridPane.add(new Label("Ordnername:"), 0, ++row);
        gridPane.add(cboDest, 1, row);

        // path/name
        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("Zielpfad:"), 0, ++row);
        gridPane.add(txtDestPath, 1, row);
        gridPane.add(btnFile, 2, row);

        gridPane.add(new Label("Zieldateiname:"), 0, ++row);
        gridPane.add(txtDestName, 1, row);
        gridPane.add(btnHelpDestName, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        makeCut(vBox);
    }

    private void makeCut(VBox vBox) {
        // cut
        Label lblTxtAll = new Label("Länge\nganzer Dateiname:");
        Label lblSizeAll = new Label();
        Label lblTxtField = new Label("Länge\neinzelne Felder:");
        Label lblSizeField = new Label();

        final Button btnHelpDestSize = PButton.helpButton(stage, "Länge des Zieldateinamens",
                HelpTextPset.PSET_DEST_FILE_SIZE);

        slCut.setMin(0);
        slCut.setMax(ProgConst.LAENGE_DATEINAME_MAX);
        slCut.setShowTickLabels(true);
        slCut.setMinorTickCount(50);
        slCut.setMajorTickUnit(100);
        slCut.setBlockIncrement(10);
        slCut.setSnapToTicks(true);

        slCutField.setMin(0);
        slCutField.setMax(ProgConst.LAENGE_FELD_MAX);
        slCutField.setShowTickLabels(true);
        slCutField.setMinorTickCount(25);
        slCutField.setMajorTickUnit(50);
        slCutField.setBlockIncrement(10);
        slCutField.setSnapToTicks(true);

        setValueSlider(slCut, lblSizeAll, "");
        slCut.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider(slCut, lblSizeAll, ""));
        setValueSlider(slCutField, lblSizeField, "");
        slCutField.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider(slCutField, lblSizeField, ""));

        GridPane.setValignment(btnHelpDestSize, VPos.CENTER);
        GridPane.setValignment(lblTxtAll, VPos.CENTER);
        GridPane.setValignment(lblSizeAll, VPos.CENTER);
        GridPane.setValignment(slCutField, VPos.CENTER);
        GridPane.setValignment(lblTxtField, VPos.CENTER);
        GridPane.setValignment(lblSizeField, VPos.CENTER);
        GridPane.setValignment(slCutField, VPos.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(25);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        gridPane.add(lblTxtAll, 0, 0);
        gridPane.add(slCut, 1, 0);
        gridPane.add(lblSizeAll, 2, 0);
        gridPane.add(btnHelpDestSize, 3, 0);

//        gridPane.add(new Label(" "), 0, 1);

        gridPane.add(lblTxtField, 0, 1);
        gridPane.add(slCutField, 1, 1);
        gridPane.add(lblSizeField, 2, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize());
    }

    private void setValueSlider(Slider sl, Label lb, String pre) {
        int days = (int) sl.getValue();
        lb.setText(pre + (days == 0 ? "nicht\nbeschränken" : "auf " + days + "\nZeichen beschränken"));
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
