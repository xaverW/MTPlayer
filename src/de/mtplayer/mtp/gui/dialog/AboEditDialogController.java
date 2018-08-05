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
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.abo.AboXml;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.pRange.PRangeBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class AboEditDialogController extends PDialogExtra {

    final GridPane gridPane = new GridPane();
    Button btnOk = new Button("Ok");
    Button btnCancel = new Button("Abbrechen");
    ComboBox<String> cboPset = new ComboBox<>();
    ComboBox<String> cboChannel = new ComboBox<>();
    ComboBox<String> cboDestination = new ComboBox<>();
    PRangeBox pRangeBoxTime = new PRangeBox(0, SelectedFilter.FILTER_DURATION_MAX_MIN);
    //    Label lblTimeMin = new Label();
//    Label lblTimeMax = new Label();
    CheckBox cbxOn = new CheckBox();
    Label[] lbl = new Label[AboXml.MAX_ELEM];
    TextField[] txt = new TextField[AboXml.MAX_ELEM];
    CheckBox[] cbx = new CheckBox[AboXml.MAX_ELEM];
    CheckBox[] cbxForAll = new CheckBox[AboXml.MAX_ELEM];
    private RadioButton rbHd = new RadioButton("HD");
    private RadioButton rbHigh = new RadioButton("hohe Auflösung");
    private RadioButton rbLow = new RadioButton("niedrige Auflösung");

    final String ALLES = "Alles";
    private boolean ok = false;

    private final ObservableList<Abo> lAbo;
    private final Abo aboCopy;
    private ProgData progData;

    public AboEditDialogController(ProgData progData, Abo abo) {
        super(null, ProgConfig.ABO_DIALOG_EDIT_SIZE.getStringProperty(),
                "Abo ändern", true);

        this.progData = progData;
        lAbo = FXCollections.observableArrayList();
        lAbo.add(abo);

        aboCopy = lAbo.get(0).getCopy();

        initDialog();
    }

    public AboEditDialogController(ProgData progData, ObservableList<Abo> lAbo) {
        super(null, ProgConfig.ABO_DIALOG_EDIT_SIZE.getStringProperty(),
                "Abo ändern", true);
        this.lAbo = lAbo;
        this.progData = progData;
        aboCopy = lAbo.get(0).getCopy();

        initDialog();
    }

    private void initDialog() {
        getVboxCont().getChildren().add(gridPane);

        btnOk.setMaxWidth(Double.MAX_VALUE);
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        getHboxOk().getChildren().addAll(btnOk, btnCancel);

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

        final ColumnConstraints ccLabel = new ColumnConstraints();
        ccLabel.setFillWidth(true);
        ccLabel.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccLabel.setHgrow(Priority.NEVER);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);

        final ColumnConstraints ccCbx = new ColumnConstraints();
        ccCbx.setHalignment(HPos.CENTER);
        ccCbx.setFillWidth(true);
        ccCbx.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccCbx.setHgrow(Priority.NEVER);

        gridPane.getColumnConstraints().add(ccLabel);
        gridPane.getColumnConstraints().add(ccTxt);
        gridPane.getColumnConstraints().add(ccCbx);

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
                GridPane g = new GridPane();
                g.setHgap(10);
                g.setVgap(10);

                final Button btnHelpRes = new Button("");
                btnHelpRes.setGraphic(new Icons().ICON_BUTTON_HELP);
                btnHelpRes.setOnAction(a -> PAlert.showHelpAlert("Auflösung",
                        HelpText.ABO_RES));

                g.add(rbHd, 0, 0);
                g.add(btnHelpRes, 1, 0);
                g.add(rbHigh, 0, 1);
                g.add(rbLow, 0, 2);

                final ColumnConstraints ccTxt = new ColumnConstraints();
                ccTxt.setFillWidth(true);
                ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
                ccTxt.setHgrow(Priority.ALWAYS);
                g.getColumnConstraints().add(ccTxt);
                GridPane.setHgrow(g, Priority.ALWAYS);

                gridPane.add(g, 1, grid);
                break;

            case AboXml.ABO_CHANNEL_EXACT:
            case AboXml.ABO_THEME_EXACT:
                gridPane.add(cbx[i], 1, grid);
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
                cbxOn.setOnAction(a -> {
                    cbxForAll[i].setSelected(true);
                });
                gridPane.add(cbxOn, 1, grid);
                break;

            case AboXml.ABO_PSET:
                cboPset.getItems().addAll(progData.setList.getListAbo().getPsetNameList());
                if (aboCopy.getPset().isEmpty()) {
                    cboPset.getSelectionModel().selectFirst();
                    aboCopy.setPset(cboPset.getSelectionModel().getSelectedItem());
                } else {
                    cboPset.getSelectionModel().select(aboCopy.getPset());
                }
                cboPset.valueProperty().bindBidirectional(aboCopy.psetProperty());
                cboPset.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> cbxForAll[i].setSelected(true));
                gridPane.add(cboPset, 1, grid);
                break;

            case AboXml.ABO_CHANNEL:
                cboChannel.setItems(progData.nameLists.getObsAllChannel());
                cboChannel.setEditable(true);
                cboChannel.valueProperty().bindBidirectional(aboCopy.channelProperty());
                cboChannel.valueProperty().addListener((observable, oldValue, newValue) -> cbxForAll[i].setSelected(true));
                gridPane.add(cboChannel, 1, grid);
                break;

            case AboXml.ABO_DEST_PATH:
                ArrayList<String> path = progData.aboList.getPath();
                if (!path.contains(aboCopy.getDestination())) {
                    path.add(0, aboCopy.getDestination());
                }
                cboDestination.setMaxWidth(Double.MAX_VALUE);
                cboDestination.setItems(FXCollections.observableArrayList(path));
                cboDestination.setEditable(true);
                cboDestination.valueProperty().bindBidirectional(aboCopy.destinationProperty());
                cboDestination.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> cbxForAll[i].setSelected(true));
                gridPane.add(cboDestination, 1, grid);
                break;

            case AboXml.ABO_MIN_DURATION:
                initDur();
//                HBox h1 = new HBox();
//                HBox h2 = new HBox();
//                h1.getChildren().add(lblTimeMin);
//                HBox.setHgrow(h1, Priority.ALWAYS);
//                h2.getChildren().addAll(h1, lblTimeMax);
//
//                VBox vBox = new VBox();
//                vBox.setSpacing(5);
//                vBox.getChildren().addAll(pRangeBoxTime, h2);
//                slTime.setShowTickLabels(false);
                gridPane.add(pRangeBoxTime, 1, grid);
                break;

            case AboXml.ABO_MAX_DURATION:
                break;

            default:
                setDefaultTxt(i, grid);
                break;
        }
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
        txt[i].textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                cbxForAll[i].setSelected(true);
            }
        });
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
            case AboXml.ABO_PSET:
                gridPane.add(cbxForAll[i], 2, grid);
        }
    }

    private void initDur() {
        pRangeBoxTime.minValueProperty().bindBidirectional(aboCopy.minDurationProperty());
        pRangeBoxTime.maxValueProperty().bindBidirectional(aboCopy.maxDurationProperty());
        pRangeBoxTime.setVluePrefix("");


//        slTime.setMin(0);
//        slTime.setMax(SelectedFilter.FILTER_DURATION_MAX_MIN);
//        slTime.setShowTickLabels(true);
//        slTime.setMinorTickCount(9);
//        slTime.setMajorTickUnit(50);
//        slTime.setBlockIncrement(10);
//        slTime.setSnapToTicks(true);

        // hightvalue
//        slTime.highValueProperty().bindBidirectional(aboCopy.maxDurationProperty());
//        slTime.highValueProperty().addListener(l -> {
//            setLabelSlider();
//            cbxForAll[AboXml.ABO_MAX_DURATION].setSelected(true);
//        });

        // lowvalue
//        slTime.lowValueProperty().bindBidirectional(aboCopy.minDurationProperty());
//        slTime.lowValueProperty().addListener(l -> {
//            setLabelSlider();
//            cbxForAll[AboXml.ABO_MIN_DURATION].setSelected(true);
//        });

//        setLabelSlider();
    }

//    private void setLabelSlider() {
//        int i;
//        i = (int) pRangeBoxTime.getLowValue();
//        lblTimeMin.setText(i == 0 ? ALLES : i + "");
//
//        i = (int) pRangeBoxTime.getHighValue();
//        lblTimeMax.setText(i == SelectedFilter.FILTER_DURATION_MAX_MIN ? ALLES : i + "");
//    }
}
