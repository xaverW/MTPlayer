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

package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.filmFilter.FilmFilter;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.mtFilter.FilterCheck;
import de.p2tools.p2Lib.mtFilter.FilterCheckRegEx;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AboEditDialogController extends AboDialogController {

    public AboEditDialogController(ProgData progData, AboData abo) {
        //hier wird ein neues Abo angelegt!
        super(progData, abo);
    }

    public AboEditDialogController(ProgData progData, FilmFilter filmFilter, AboData abo) {
        //hier wird ein Abo an den Filter angepasst
        super(progData, filmFilter, abo);
    }

    public AboEditDialogController(ProgData progData, ObservableList<AboData> aboList) {
        //hier werden Abos geändert
        super(progData, aboList);
    }

    @Override
    void makeControl() {
        initSenderMenu();
        for (int i = 0; i < AboFieldNames.MAX_ELEM; ++i) {
            initControl(i);
            addLabel(i);
            addTextField(i);

            if (aboList.size() > 1) {
                // nur dann brauchts das
                addCheckBoxEditAll(i);
            }
        }
    }

    private void initControl(int i) {
        lbl[i] = new Label(AboFieldNames.COLUMN_NAMES[i] + ":");

        switch (i) {
            case AboFieldNames.ABO_DESCRIPTION_NO:
                textArea.setWrapText(true);
                textArea.setPrefRowCount(4);
                textArea.setMinHeight(60);
                textArea.setPrefColumnCount(1);
                textArea.setText(aboCopy.getDescription());
                break;
            case AboFieldNames.ABO_RESOLUTION_NO:
                ToggleGroup tg = new ToggleGroup();
                rbHd.setToggleGroup(tg);
                rbHigh.setToggleGroup(tg);
                rbLow.setToggleGroup(tg);
                switch (aboCopy.getResolution()) {
                    case FilmDataMTP.RESOLUTION_HD:
                        rbHd.setSelected(true);
                        break;
                    case FilmDataMTP.RESOLUTION_SMALL:
                        rbLow.setSelected(true);
                        break;
                    default:
                        aboCopy.setResolution(FilmDataMTP.RESOLUTION_NORMAL);
                        rbHigh.setSelected(true);
                }
                rbHd.setOnAction(event -> setResolution());
                rbHigh.setOnAction(event -> setResolution());
                rbLow.setOnAction(event -> setResolution());
                break;
            case AboFieldNames.ABO_THEME_EXACT_NO:
                cbx[i] = new CheckBox("");
                cbx[i].setSelected(aboCopy.isThemeExact());
                break;

            default:
                txt[i] = new TextField("");
                txt[i].setText(aboCopy.properties[i].getValue().toString());
        }

        cbxEditAll[i] = new CheckBox();
        cbxEditAll[i].setSelected(false);
        cbxEditAll[i].getStyleClass().add("chk-edit-all");
        cbxEditAll[i].selectedProperty().addListener((u, o, n) -> checkOk());
        GridPane.setHalignment(cbxEditAll[i], HPos.CENTER);
    }

    private void addLabel(int i) {
        final int grid = getGridLine(i);
        switch (i) {
            case AboFieldNames.ABO_THEME_EXACT_NO:
                lbl[i].setText("   Exakt:");
                gridPane.add(lbl[i], 0, grid);
                break;
            case AboFieldNames.ABO_MIN_DURATION_NO:
                lbl[i].setText("Dauer:");
                gridPane.add(lbl[i], 0, grid);
                break;
            case AboFieldNames.ABO_MAX_DURATION_NO:
                break;
            default:
                gridPane.add(lbl[i], 0, grid);
                break;
        }
    }

    private void addTextField(int i) {
        final int grid = getGridLine(i);
        switch (i) {
            case AboFieldNames.ABO_DESCRIPTION_NO:
                textArea.textProperty().bindBidirectional(aboCopy.properties[i]);
                textArea.textProperty().addListener((observable, oldValue, newValue) -> cbxEditAll[i].setSelected(true));
                gridPane.add(textArea, 1, grid);
                break;
            case AboFieldNames.ABO_NO_NO:
                txt[i].setEditable(false);
                txt[i].setDisable(true);
                txt[i].setText(aboCopy.getNo() + "");
                gridPane.add(txt[i], 1, grid);
                break;
            case AboFieldNames.ABO_NAME_NO:
                setDefaultTxt(i, grid);
                txt[i].textProperty().addListener((observable, oldValue, newValue) -> {
                    if (txt[i].getText().isEmpty()) {
                        txt[i].setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
                    } else {
                        txt[i].setStyle("");
                    }
                });
                if (txt[i].getText().isEmpty()) {
                    txt[i].setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
                } else {
                    txt[i].setStyle("");
                }
                break;
            case AboFieldNames.ABO_RESOLUTION_NO:
                final Button btnHelpRes = PButton.helpButton(this.getStage(),
                        "Auflösung", HelpText.ABO_RES);

                HBox h = new HBox();
                h.setAlignment(Pos.CENTER_RIGHT);
                HBox.setHgrow(h, Priority.ALWAYS);
                h.getChildren().add(btnHelpRes);

                HBox hAufloeung = new HBox(10);
                hAufloeung.setAlignment(Pos.CENTER_LEFT);
                hAufloeung.getChildren().addAll(rbHd, rbHigh, rbLow, h);
                this.gridPane.add(hAufloeung, 1, grid);
                break;
            case AboFieldNames.ABO_THEME_EXACT_NO:
                cbx[i].selectedProperty().bindBidirectional(aboCopy.properties[i]);
                cbx[i].selectedProperty().addListener((observable, oldValue, newValue) -> {
                    cbxEditAll[i].setSelected(true);
                });
                this.gridPane.add(cbx[i], 1, grid);
                break;
            case AboFieldNames.ABO_DATE_LAST_ABO_NO:
                txt[i].setEditable(false);
                txt[i].setDisable(true);
                txt[i].setText(aboCopy.getDate().toString());
                gridPane.add(txt[i], 1, grid);
                break;
            case AboFieldNames.ABO_GEN_DATE_NO:
                txt[i].setEditable(false);
                txt[i].setDisable(true);
                txt[i].setText(aboCopy.getGenDate().toString());
                gridPane.add(txt[i], 1, grid);
                break;
            case AboFieldNames.ABO_ACTIVE_NO:
                cbxOn.selectedProperty().bindBidirectional(aboCopy.activeProperty());
                cbxOn.setOnAction(a -> cbxEditAll[i].setSelected(true));
                this.gridPane.add(cbxOn, 1, grid);
                break;
            case AboFieldNames.ABO_SET_DATA_ID_NO:
                // mind. 1 Set gibts immer, Kontrolle oben bereits
                if (progData.setDataList.getSetDataListAbo().size() <= 1) {
                    // gibt nur ein Set: brauchts keine Auswahl
                    txt[i].setEditable(false);
                    txt[i].setDisable(true);
                    txt[i].setText(aboCopy.getSetData(progData).getVisibleName());
                    this.gridPane.add(txt[i], 1, grid);

                } else {
                    cboSetData.setMaxWidth(Double.MAX_VALUE);
                    cboSetData.setPrefWidth(20);
                    cboSetData.getItems().addAll(progData.setDataList.getSetDataListAbo());
                    cboSetData.valueProperty().bindBidirectional(aboCopy.setDataProperty());
                    cboSetData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                            cbxEditAll[i].setSelected(true));
                    this.gridPane.add(cboSetData, 1, grid);
                }
                break;
            case AboFieldNames.ABO_CHANNEL_NO:
                mbChannel.setMaxWidth(Double.MAX_VALUE);
                this.gridPane.add(mbChannel, 1, grid);
                break;
            case AboFieldNames.ABO_DEST_PATH_NO:
                ArrayList<String> path = progData.aboList.getAboDestinationPathList();
                if (!path.contains(aboCopy.getAboSubDir())) {
                    path.add(0, aboCopy.getAboSubDir());
                }
                cboDestination.setMaxWidth(Double.MAX_VALUE);

                cboDestination.setItems(FXCollections.observableArrayList(path));
                cboDestination.setEditable(true);
                cboDestination.getEditor().textProperty().bindBidirectional(aboCopy.aboSubDirProperty());

//                aboCopy.aboSubDirProperty().addListener((v, o, n) ->
//                        cbxEditAll[i].setSelected(true));
                cboDestination.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                    cbxEditAll[i].setSelected(true);
                });

                final TextField textField = new TextField("");
                textField.setEditable(false);
                textField.setDisable(true);
                if (aboCopy.getSetData().isGenAboSubDir()) {
                    textField.setText(aboCopy.getSetData().getAboSubDir_ENSubDir_Name());
                }
                aboCopy.setDataProperty().addListener((u, o, n) -> {
                    if (n != null) {
                        if (aboCopy.getSetData().isGenAboSubDir()) {
                            textField.setText(aboCopy.getSetData().getAboSubDir_ENSubDir_Name());
                        } else {
                            textField.setText("");
                        }
                    }
                });

                Button btn = new Button("");
                btn.setTooltip(new Tooltip("Zielpfad für das Abo anpassen"));
                btn.setGraphic(ProgIcons.Icons.ICON_BUTTON_EDIT_ABO_PATH.getImageView());
                btn.setOnAction(event -> {
                    if (textField.isVisible()) {
                        textField.setVisible(false);
                        cboDestination.setVisible(true);
                        cboDestination.setValue(memory);
                    } else {
                        textField.setVisible(true);
                        cboDestination.setVisible(false);
                        memory = cboDestination.getValue();
                        cboDestination.setValue("");
                    }
                    cbxEditAll[i].setSelected(true);
                });
                final Button btnHelp = PButton.helpButton(getStage(), "Unterordner anlegen",
                        HelpText.ABO_SUBDIR);

                final StackPane sp = new StackPane();
                sp.getChildren().addAll(textField, cboDestination);
                sp.setPrefWidth(20);

                HBox hbox = new HBox(10);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().addAll(sp, btn, btnHelp);
                HBox.setHgrow(sp, Priority.ALWAYS);
                this.gridPane.add(hbox, 1, grid);

                if (aboCopy.getAboSubDir().trim().isEmpty()) {
                    cboDestination.setVisible(false);
                } else {
                    textField.setVisible(false);
                }
                break;
            case AboFieldNames.ABO_TIME_RANGE_NO:
                initTimeRange();

                Label l = new Label("50 Tage");
                l.setVisible(false);
                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(l, lblTimeRange);
                HBox hBox = new HBox(15);
                hBox.getChildren().addAll(slTimeRange, stackPane);
                HBox.setHgrow(slTimeRange, Priority.ALWAYS);
                this.gridPane.add(hBox, 1, grid);
                break;
            case AboFieldNames.ABO_MIN_DURATION_NO:
                initDur();
                this.gridPane.add(pRangeBoxTime, 1, grid);
                break;
            case AboFieldNames.ABO_MAX_DURATION_NO:
                break;
            case AboFieldNames.ABO_START_TIME_NO:
                hBox = new HBox(10);
                hBox.setAlignment(Pos.CENTER_LEFT);

                chkStartTime.setSelected(!aboCopy.getStartTime().isEmpty());
                chkStartTime.setOnAction(a -> {
                    cbxEditAll[AboFieldNames.ABO_START_TIME_NO].setSelected(true);
                    if (chkStartTime.isSelected()) {
                        aboCopy.setStartTime(pTimePicker.getTime());
                    } else {
                        aboCopy.setStartTime("");
                    }
                });

                pTimePicker.setTime(aboCopy.getStartTime());
                pTimePicker.setOnAction(a -> {
                    aboCopy.setStartTime(pTimePicker.getTime());
                });
                pTimePicker.disableProperty().bind(chkStartTime.selectedProperty().not());

                final Button btnHelpStartTime = PButton.helpButton(getStage(), "Startzeit",
                        HelpText.ABO_START_TIME);

                HBox hb = new HBox();
                hb.setAlignment(Pos.CENTER_RIGHT);
                HBox.setHgrow(hb, Priority.ALWAYS);
                hb.getChildren().add(btnHelpStartTime);

                hBox.getChildren().addAll(chkStartTime, pTimePicker, hb);
                gridPane.add(hBox, 1, grid);
                break;
            case AboFieldNames.ABO_THEME_NO:
            case AboFieldNames.ABO_THEME_TITLE_NO:
            case AboFieldNames.ABO_TITLE_NO:
            case AboFieldNames.ABO_SOMEWHERE_NO:
                FilterCheckRegEx fT = new FilterCheckRegEx(txt[i]);
                txt[i].textProperty().addListener((observable, oldValue, newValue) -> fT.checkPattern());
                setDefaultTxt(i, grid);
                break;
            default:
                setDefaultTxt(i, grid);
                break;
        }

    }

    private int getGridLine(int i) {
        if (i >= AboFieldNames.ABO_MAX_DURATION_NO) {
            //gibts nicht
            return i;
        } else {
            return ++i;
        }
    }

    private void initDur() {
        pRangeBoxTime.maxValueProperty().bindBidirectional(aboCopy.maxDurationMinuteProperty());
        pRangeBoxTime.minValueProperty().bindBidirectional(aboCopy.minDurationMinuteProperty());

        pRangeBoxTime.maxValueProperty().addListener((observable, oldValue, newValue) ->
                cbxEditAll[AboFieldNames.ABO_MIN_DURATION_NO].setSelected(true));
        pRangeBoxTime.minValueProperty().addListener((observable, oldValue, newValue) ->
                cbxEditAll[AboFieldNames.ABO_MIN_DURATION_NO].setSelected(true));

        pRangeBoxTime.setValuePrefix("");
    }

    private void initTimeRange() {
        slTimeRange.setMin(FilterCheck.FILTER_ALL_OR_MIN);
        slTimeRange.setMax(FilterCheck.FILTER_TIME_RANGE_MAX_VALUE);
        slTimeRange.setShowTickLabels(true);
        slTimeRange.setMajorTickUnit(10);
        slTimeRange.setBlockIncrement(5);

        slTimeRange.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == FilterCheck.FILTER_ALL_OR_MIN) return "alles";
                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slTimeRange.setValue(aboCopy.getTimeRange());
        slTimeRange.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        aboCopy.setTimeRange((int) slTimeRange.getValue());
                        cbxEditAll[AboFieldNames.ABO_TIME_RANGE_NO].setSelected(true);
                    }
                }
        );

        slTimeRange.valueProperty().addListener((observable, oldValue, newValue) -> {
            setLabelSlider();
        });
        setLabelSlider();
    }

    private void setLabelSlider() {
        final String txtAll = "alles";

        int i = (int) slTimeRange.getValue();
        String tNr = i + "";

        if (i == FilterCheck.FILTER_ALL_OR_MIN) {
            lblTimeRange.setText(txtAll);
        } else {
            lblTimeRange.setText(tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }

    private void setResolution() {
        cbxEditAll[AboFieldNames.ABO_RESOLUTION_NO].setSelected(true);
        if (rbHigh.isSelected()) {
            aboCopy.setResolution(FilmDataMTP.RESOLUTION_NORMAL);
        }
        if (rbHd.isSelected()) {
            aboCopy.setResolution(FilmDataMTP.RESOLUTION_HD);
        }
        if (rbLow.isSelected()) {
            aboCopy.setResolution(FilmDataMTP.RESOLUTION_SMALL);
        }
    }

    private void setDefaultTxt(int i, int grid) {
        txt[i].textProperty().bindBidirectional(aboCopy.properties[i]);
        txt[i].textProperty().addListener((observable, oldValue, newValue) -> cbxEditAll[i].setSelected(true));
        gridPane.add(txt[i], 1, grid);
    }

    private void addCheckBoxEditAll(int i) {
        final int grid = getGridLine(i);

        switch (i) {
            case AboFieldNames.ABO_NO_NO:
            case AboFieldNames.ABO_DATE_LAST_ABO_NO:
            case AboFieldNames.ABO_GEN_DATE_NO:
            case AboFieldNames.ABO_MAX_DURATION_NO:
                break;

            case AboFieldNames.ABO_ACTIVE_NO:
            case AboFieldNames.ABO_NAME_NO:
            case AboFieldNames.ABO_DESCRIPTION_NO:
            case AboFieldNames.ABO_RESOLUTION_NO:
            case AboFieldNames.ABO_CHANNEL_NO:
            case AboFieldNames.ABO_THEME_NO:
            case AboFieldNames.ABO_THEME_EXACT_NO:
            case AboFieldNames.ABO_THEME_TITLE_NO:
            case AboFieldNames.ABO_TITLE_NO:
            case AboFieldNames.ABO_SOMEWHERE_NO:
            case AboFieldNames.ABO_TIME_RANGE_NO:
            case AboFieldNames.ABO_MIN_DURATION_NO:
            case AboFieldNames.ABO_START_TIME_NO:
            case AboFieldNames.ABO_DEST_PATH_NO:
                gridPane.add(cbxEditAll[i], 2, grid);
                break;

            case AboFieldNames.ABO_SET_DATA_ID_NO:
                if (progData.setDataList.getSetDataListAbo().size() > 1) {
                    // nur dann kann man was ändern
                    gridPane.add(cbxEditAll[i], 2, grid);
                }
                break;
        }
    }

    private void checkOk() {
        // nur wenn einer geklickt ist, wird auch was geändert -> nur dann macht OK Sinn
        boolean ok = false;
        for (int ii = 0; ii < cbxEditAll.length; ++ii) {
            if (cbxEditAll[ii] == null) {
                continue;
            }
            if (cbxEditAll[ii].isSelected()) {
                ok = true;
                break;
            }
        }
        okProp.set(ok);
    }

    private void initSenderMenu() {
        mbChannel.getStyleClass().add("cbo-menu");
        mbChannel.setMaxWidth(Double.MAX_VALUE);

        List<String> senderArr = new ArrayList<>();
        String sender = aboCopy.channelProperty().get();
        if (sender != null) {
            if (sender.contains(",")) {
                senderArr.addAll(Arrays.asList(sender.replace(" ", "").toLowerCase().split(",")));
            } else {
                senderArr.add(sender.toLowerCase());
            }
            senderArr.stream().forEach(s -> s = s.trim());
        }

        MenuItem mi = new MenuItem("Auswahl löschen");
        mi.setOnAction(a -> {
            clearMenuText();
            cbxEditAll[AboFieldNames.ABO_CHANNEL_NO].setSelected(true);
        });
        mbChannel.getItems().add(mi);

        for (String s : progData.worker.getAllChannelList()) {
            if (s.isEmpty()) {
                continue;
            }
            CheckMenuItem miCheck = new CheckMenuItem(s);
            if (senderArr.contains(s.toLowerCase())) {
                miCheck.setSelected(true);
            }
            miCheck.setOnAction(a -> {
                setMenuText();
                cbxEditAll[AboFieldNames.ABO_CHANNEL_NO].setSelected(true);
            });

            checkMenuItemsList.add(miCheck);
            mbChannel.getItems().add(miCheck);
        }
        setMenuText();
    }

    private void clearMenuText() {
        for (CheckMenuItem cmi : checkMenuItemsList) {
            cmi.setSelected(false);
        }
        mbChannel.setText("");
        aboCopy.channelProperty().setValue("");
    }

    private void setMenuText() {
        String text = "";
        for (CheckMenuItem cmi : checkMenuItemsList) {
            if (cmi.isSelected()) {
                text = text + (text.isEmpty() ? "" : ", ") + cmi.getText();
            }
        }
        mbChannel.setText(text);
        aboCopy.channelProperty().setValue(text);
    }
}
