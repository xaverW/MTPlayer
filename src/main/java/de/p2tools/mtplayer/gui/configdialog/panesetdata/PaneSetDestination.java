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

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.mtplayer.gui.tools.HelpTextPset;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.pcbo.P2CboObject;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneSetDestination {
    private final P2ToggleSwitch tglSubdir = new P2ToggleSwitch("Bei Abos Unterordner anlegen:");
    private final P2CboObject<AboSubDir.ENSubDir> cboDest = new P2CboObject<>();
    private final TextField txtDestPath = new TextField();
    private final TextField txtDestName = new TextField();
    private final Slider slCut = new Slider();
    private final Slider slCutField = new Slider();
    private boolean changeTgl = false;

    private final Stage stage;
    private SetData setData = null;
    private final ObjectProperty<SetData> setDataObjectProperty;

    PaneSetDestination(Stage stage, ObjectProperty<SetData> setDataObjectProperty) {
        this.stage = stage;
        this.setDataObjectProperty = setDataObjectProperty;
    }

    public void close() {
        unBindProgData();
    }

    public void makePane(Collection<TitledPane> result) {
        VBox vBox = new VBox(25);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(P2LibConst.PADDING));

        TitledPane titledPane = new TitledPane("Speicherziel", vBox);
        result.add(titledPane);

        final Button btnFile = new Button();
        btnFile.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Ordner zum Speichern der Filme auswählen"));
        btnFile.setOnAction(event -> P2DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtDestPath));

        final Button btnHelSubDir = P2Button.helpButton(stage, "Unterordner anlegen",
                HelpText.SETDATA_ABO_SUBDIR);
        final Button btnHelpDestName = P2Button.helpButton(stage, "Zieldateiname",
                HelpTextPset.PSET_PARAMETER_FILE_NAME);

        cboDest.init(FXCollections.observableArrayList(AboSubDir.ENSubDir.values()));
        cboDest.getSelValueProperty().addListener((m, o, n) -> {
            if (changeTgl) {
                tglSubdir.setSelected(true);
            }
            if (setDataObjectProperty.getValue() != null && cboDest.getSelValue() != null) {
                setDataObjectProperty.getValue().setAboSubDir_ENSubDirNo(cboDest.getSelValue().getNo());
            }
        });
        cboDest.setMaxWidth(Double.MAX_VALUE);
        cboDest.disableProperty().bind(tglSubdir.selectedProperty().not());

        int row = 0;
        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);

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

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());

        makeCut(vBox);

        setDataObjectProperty.addListener((u, o, n) -> {
            titledPane.setDisable(setDataObjectProperty.getValue() == null);
            bindProgData();
        });
        bindProgData();
        titledPane.setDisable(setDataObjectProperty.getValue() == null);
    }

    private void makeCut(VBox vBox) {
        //cut
        Label lblTxtAll = new Label("Länge\nganzer Dateiname:");
        Label lblSizeAll = new Label();
        Label lblTxtField = new Label("Länge\neinzelne Felder:");
        Label lblSizeField = new Label();

        final Button btnHelpDestSize = P2Button.helpButton(stage, "Länge des Zieldateinamens",
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
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);

        int row = 0;
        gridPane.add(lblTxtAll, 0, ++row); // Platz nach oben!
        gridPane.add(slCut, 1, row);
        gridPane.add(lblSizeAll, 2, row);
        gridPane.add(btnHelpDestSize, 3, row);

        ++row;
        gridPane.add(lblTxtField, 0, ++row);
        gridPane.add(slCutField, 1, row);
        gridPane.add(lblSizeField, 2, row);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(), P2GridConstraints.getCcPrefSize());
    }

    private void setValueSlider(Slider sl, Label lb, String pre) {
        int days = (int) sl.getValue();
        lb.setText(pre + (days == 0 ? "nicht\nbeschränken" : "auf " + days + "\nZeichen beschränken"));
    }

    private void bindProgData() {
        unBindProgData();
        setData = setDataObjectProperty.getValue();
        if (setData != null) {
            tglSubdir.selectedProperty().bindBidirectional(setData.genAboSubDirProperty());
            txtDestPath.textProperty().bindBidirectional(setData.destPathProperty());
            txtDestName.textProperty().bindBidirectional(setData.destNameProperty());
            slCut.valueProperty().bindBidirectional(setData.maxSizeProperty());
            slCutField.valueProperty().bindBidirectional(setData.maxFieldProperty());

            cboDest.getSelectionModel().select(setData.getAboSubDir_ENSubDir());
            changeTgl = true;
        }
    }

    private void unBindProgData() {
        changeTgl = false;
        if (setData != null) {
            tglSubdir.selectedProperty().unbindBidirectional(setData.genAboSubDirProperty());
            txtDestPath.textProperty().unbindBidirectional(setData.destPathProperty());
            txtDestName.textProperty().unbindBidirectional(setData.destNameProperty());

            txtDestPath.setText("");
            txtDestName.setText("");

            slCut.valueProperty().unbindBidirectional(setData.maxSizeProperty());
            slCutField.valueProperty().unbindBidirectional(setData.maxFieldProperty());
        }
    }
}
