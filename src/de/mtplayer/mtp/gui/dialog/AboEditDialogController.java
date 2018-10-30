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

package de.mtplayer.mtp.gui.dialog;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.abo.AboXml;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pRange.PRangeBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class AboEditDialogController extends PDialogExtra {

    final GridPane gridPane = new GridPane();
    Button btnOk = new Button("Ok");
    Button btnCancel = new Button("Abbrechen");
    ComboBox<String> cboPset = new ComboBox<>();
    ComboBox<String> cboChannel = new ComboBox<>();
    ComboBox<String> cboDestination = new ComboBox<>();
    PRangeBox pRangeBoxTime = new PRangeBox(0, SelectedFilter.FILTER_DURATION_MAX_MIN);
    CheckBox cbxOn = new CheckBox();
    Label[] lbl = new Label[AboXml.MAX_ELEM];
    TextField[] txt = new TextField[AboXml.MAX_ELEM];
    CheckBox[] cbx = new CheckBox[AboXml.MAX_ELEM];
    CheckBox[] cbxForAll = new CheckBox[AboXml.MAX_ELEM];
    private RadioButton rbHd = new RadioButton("HD");
    private RadioButton rbHigh = new RadioButton("hohe Auflösung");
    private RadioButton rbLow = new RadioButton("niedrige Auflösung");

    private boolean ok = false;

    private final ObservableList<Abo> lAbo;
    private final Abo aboCopy;
    private ProgData progData;

    public AboEditDialogController(ProgData progData, Abo abo) {
        super(ProgConfig.ABO_DIALOG_EDIT_SIZE.getStringProperty(),
                "Abo ändern", true);

        this.progData = progData;
        lAbo = FXCollections.observableArrayList();
        lAbo.add(abo);

        aboCopy = lAbo.get(0).getCopy();

        initDialog();
    }

    public AboEditDialogController(ProgData progData, ObservableList<Abo> lAbo) {
        super(ProgConfig.ABO_DIALOG_EDIT_SIZE.getStringProperty(),
                "Abo ändern", true);
        this.lAbo = lAbo;
        this.progData = progData;
        aboCopy = lAbo.get(0).getCopy();

        initDialog();
    }

    private void initDialog() {
        getVboxCont().getChildren().add(gridPane);
        addOkButtons(btnOk, btnCancel);

        init(getvBoxDialog(), true);
    }

    private void quit() {

        ok = true; //Änderungen übernehmen
        if (lAbo.size() == 1) {
            lAbo.get(0).copyToMe(aboCopy);

        } else {
            updateAboList();
        }

        close();
    }

    @Override
    public void close() {
        // WICHTIG!!
        cboChannel.valueProperty().unbindBidirectional(aboCopy.channelProperty());
        super.close();
    }

    private void updateAboList() {
        for (final Abo abo : lAbo) {

            for (int i = 0; i < cbxForAll.length; ++i) {
                if (cbxForAll[i] == null || !cbxForAll[i].isSelected()) {
                    continue;
                }
                abo.properties[i].setValue(aboCopy.properties[i].getValue());
            }

        }
    }

    public boolean getOk() {
        return ok;
    }

    @Override
    public void make() {
        btnOk.setOnAction(a -> quit());
        btnOk.disableProperty().bind(aboCopy.nameProperty().isEmpty());
        btnCancel.setOnAction(a -> close());

        gridPane.setHgap(5);
        gridPane.setVgap(10);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        if (lAbo.size() > 1) {
            Label l1 = new Label("bei allen");
            Label l2 = new Label("ändern");
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(l1, l2);
            gridPane.add(vBox, 2, 0);
        }

        for (int i = 0; i < AboXml.MAX_ELEM; ++i) {

            if (i == AboXml.ABO_NAME && lAbo.size() > 1) {
                continue;
            }

            initControl(i);
            addLabel(i, i + 1);
            addTextField(i, i + 1);

            if (lAbo.size() > 1) {
                // nur dann brauchts das
                addCheckBox(i, i + 1);
            }
        }

    }

    private void initControl(int i) {
        lbl[i] = new Label(AboXml.COLUMN_NAMES[i] + ":");
        lbl[i].setMinHeight(Region.USE_COMPUTED_SIZE);
        lbl[i].setMinWidth(Region.USE_COMPUTED_SIZE);
        lbl[i].setPrefHeight(Region.USE_COMPUTED_SIZE);
        lbl[i].setPrefWidth(Region.USE_COMPUTED_SIZE);
        GridPane.setHgrow(lbl[i], Priority.NEVER);

        switch (i) {
            case AboXml.ABO_RESOLUTION:
                ToggleGroup tg = new ToggleGroup();
                rbHd.setToggleGroup(tg);
                rbHigh.setToggleGroup(tg);
                rbLow.setToggleGroup(tg);
                switch (aboCopy.getResolution()) {
                    case Film.RESOLUTION_HD:
                        rbHd.setSelected(true);
                        break;
                    case Film.RESOLUTION_SMALL:
                        rbLow.setSelected(true);
                        break;
                    default:
                        aboCopy.setResolution(Film.RESOLUTION_NORMAL);
                        rbHigh.setSelected(true);
                }
                rbHd.setOnAction(event -> setResolution());
                rbHigh.setOnAction(event -> setResolution());
                rbLow.setOnAction(event -> setResolution());
                break;
            case AboXml.ABO_CHANNEL_EXACT:
                cbx[i] = new CheckBox("");
                cbx[i].setSelected(aboCopy.getChannelExact());
                break;
            case AboXml.ABO_THEME_EXACT:
                cbx[i] = new CheckBox("");
                cbx[i].setSelected(aboCopy.isThemeExact());
                break;

            default:
                txt[i] = new TextField("");
                txt[i].setMinHeight(Region.USE_COMPUTED_SIZE);
                txt[i].setMinWidth(Region.USE_COMPUTED_SIZE);
                txt[i].setPrefHeight(Region.USE_COMPUTED_SIZE);
                txt[i].setPrefWidth(Region.USE_COMPUTED_SIZE);
                txt[i].setText(aboCopy.getStringOf(i));
                GridPane.setHgrow(txt[i], Priority.ALWAYS);
        }

        cbxForAll[i] = new CheckBox();
        cbxForAll[i].setSelected(false);
        GridPane.setHgrow(cbxForAll[i], Priority.NEVER);
    }

    private void addLabel(int i, int grid) {

        switch (i) {
            case AboXml.ABO_CHANNEL_EXACT:
            case AboXml.ABO_THEME_EXACT:
                lbl[i].setText("  exakt:");
                gridPane.add(lbl[i], 0, grid);
                break;
            case AboXml.ABO_MIN_DURATION:
                lbl[i].setText("Dauer:");
                gridPane.add(lbl[i], 0, grid);
                break;
            case AboXml.ABO_MAX_DURATION:
                break;
            default:
                gridPane.add(lbl[i], 0, grid);
                break;
        }

    }

    private void addTextField(int i, int grid) {

        switch (i) {
            case AboXml.ABO_NR:
                txt[i].setEditable(false);
                txt[i].setDisable(true);
                txt[i].setText(aboCopy.getNr() + "");
                gridPane.add(txt[i], 1, grid);
                break;

            case AboXml.ABO_NAME:
                setDefaultTxt(i, grid);
                txt[i].textProperty().addListener((observable, oldValue, newValue) -> {
                    if (txt[i].getText().isEmpty()) {
                        txt[i].setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
                    } else {
                        txt[i].setStyle("");
                    }
                });
                if (txt[i].getText().isEmpty()) {
                    txt[i].setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
                } else {
                    txt[i].setStyle("");
                }
                break;

            case AboXml.ABO_RESOLUTION:
                GridPane gridPane = new GridPane();
                gridPane.setHgap(10);
                gridPane.setVgap(10);

                final Button btnHelpRes = new PButton().helpButton(this.getStage(),
                        "Auflösung", HelpText.ABO_RES);

                gridPane.add(rbHd, 0, 0);
                gridPane.add(btnHelpRes, 1, 0);
                gridPane.add(rbHigh, 0, 1);
                gridPane.add(rbLow, 0, 2);

                gridPane.getColumnConstraints().add(PColumnConstraints.getCcComputedSizeAndHgrow());
                GridPane.setHgrow(gridPane, Priority.ALWAYS);

                this.gridPane.add(gridPane, 1, grid);
                break;

            case AboXml.ABO_CHANNEL_EXACT:
            case AboXml.ABO_THEME_EXACT:
                this.gridPane.add(cbx[i], 1, grid);
                cbx[i].selectedProperty().bindBidirectional(aboCopy.properties[i]);
                cbx[i].selectedProperty().addListener((observable, oldValue, newValue) -> {
                    cbxForAll[i].setSelected(true);
                });
                break;

            case AboXml.ABO_DOWN_DATE:
                txt[i].setEditable(false);
                txt[i].setDisable(true);
                break;

            case AboXml.ABO_ON:
                cbxOn.selectedProperty().bindBidirectional(aboCopy.activeProperty());
                cbxOn.setOnAction(a -> cbxForAll[i].setSelected(true));
                this.gridPane.add(cbxOn, 1, grid);
                break;

            case AboXml.ABO_PSET_NAME:
                cboPset.getItems().addAll(progData.setList.getListAbo().getPsetNameList());
                if (aboCopy.getPsetName().isEmpty()) {
                    cboPset.getSelectionModel().selectFirst();
                    aboCopy.setPsetName(cboPset.getSelectionModel().getSelectedItem());
                } else {
                    cboPset.getSelectionModel().select(aboCopy.getPsetName());
                }
                cboPset.valueProperty().bindBidirectional(aboCopy.psetNameProperty());
                cboPset.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> cbxForAll[i].setSelected(true));
                this.gridPane.add(cboPset, 1, grid);
                break;

            case AboXml.ABO_CHANNEL:
                cboChannel.setItems(progData.worker.getAllChannelList());
                cboChannel.setEditable(true);
                cboChannel.valueProperty().bindBidirectional(aboCopy.channelProperty());
                cboChannel.valueProperty().addListener((observable, oldValue, newValue) -> cbxForAll[i].setSelected(true));
                this.gridPane.add(cboChannel, 1, grid);
                break;

            case AboXml.ABO_DEST_PATH:
                ArrayList<String> path = progData.aboList.getAboDestinationPathList();
                if (!path.contains(aboCopy.getDestination())) {
                    path.add(0, aboCopy.getDestination());
                }
                cboDestination.setMaxWidth(Double.MAX_VALUE);
                cboDestination.setItems(FXCollections.observableArrayList(path));
                cboDestination.setEditable(true);
                cboDestination.valueProperty().bindBidirectional(aboCopy.destinationProperty());
                cboDestination.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> cbxForAll[i].setSelected(true));
                this.gridPane.add(cboDestination, 1, grid);
                break;

            case AboXml.ABO_MIN_DURATION:
                initDur();
                this.gridPane.add(pRangeBoxTime, 1, grid);
                break;

            case AboXml.ABO_MAX_DURATION:
                break;

            default:
                setDefaultTxt(i, grid);
                break;
        }
    }

    private void initDur() {
        pRangeBoxTime.minValueProperty().bindBidirectional(aboCopy.minDurationProperty());
        pRangeBoxTime.maxValueProperty().bindBidirectional(aboCopy.maxDurationProperty());
        pRangeBoxTime.setValuePrefix("");
    }

    private void setResolution() {
        cbxForAll[AboXml.ABO_RESOLUTION].setSelected(true);
        if (rbHigh.isSelected()) {
            aboCopy.setResolution(Film.RESOLUTION_NORMAL);
        }
        if (rbHd.isSelected()) {
            aboCopy.setResolution(Film.RESOLUTION_HD);
        }
        if (rbLow.isSelected()) {
            aboCopy.setResolution(Film.RESOLUTION_SMALL);
        }
    }

    private void setDefaultTxt(int i, int grid) {
        txt[i].textProperty().bindBidirectional(aboCopy.properties[i]);
        txt[i].textProperty().addListener((observable, oldValue, newValue) -> cbxForAll[i].setSelected(true));
        gridPane.add(txt[i], 1, grid);

    }

    private void addCheckBox(int i, int grid) {

        switch (i) {
            case AboXml.ABO_ON:
            case AboXml.ABO_RESOLUTION:
            case AboXml.ABO_CHANNEL:
            case AboXml.ABO_CHANNEL_EXACT:
            case AboXml.ABO_THEME:
            case AboXml.ABO_THEME_EXACT:
            case AboXml.ABO_TITLE:
            case AboXml.ABO_THEME_TITLE:
            case AboXml.ABO_SOMEWHERE:
            case AboXml.ABO_MIN_DURATION:
            case AboXml.ABO_DEST_PATH:
            case AboXml.ABO_PSET_NAME:
                gridPane.add(cbxForAll[i], 2, grid);
        }
    }
}
