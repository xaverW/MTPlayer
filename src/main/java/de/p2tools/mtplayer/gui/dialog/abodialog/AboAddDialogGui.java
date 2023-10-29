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

package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.ArrayList;

public class AboEditDialogGui {

    private final AddAboDto addAboDto;
    private final ProgData progData;
    private final VBox vBoxCont;
    private final HBox hBoxTop = new HBox();
    private final GridPane gridPane = new GridPane();

    public AboEditDialogGui(ProgData progData, AddAboDto addAboDto, VBox vBoxCont) {
        //hier wird ein neues Abo angelegt -> Button, abo ist immer neu
        this.addAboDto = addAboDto;
        this.progData = progData;
        this.vBoxCont = vBoxCont;
    }

    public AboEditDialogGui(ProgData progData, AddAboDto addAboDto, FilmFilter filmFilter, VBox vBoxCont) {
        //hier wird ein bestehendes Abo an dem Filter angepasst -> Button
        this.addAboDto = addAboDto;
        this.progData = progData;
        this.vBoxCont = vBoxCont;
    }

    public void addCont() {
        // Top
        hBoxTop.getStyleClass().add("downloadDialog");
        hBoxTop.setSpacing(20);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setPadding(new Insets(5));
        hBoxTop.getChildren().addAll(addAboDto.btnPrev, addAboDto.lblSum, addAboDto.btnNext);
        vBoxCont.getChildren().add(hBoxTop);

        // Grid
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());


        for (int i = 0; i < AboFieldNames.MAX_ELEM; ++i) {
            initControl(i);
            addLabel(i);
            addTextField(i);

            if (addAboDto.aboList.size() > 1) {
                // nur dann brauchts das
                addCheckBoxEditAll(i);
            }
        }
    }

    public void init() {
        if (addAboDto.addAboData.length == 1) {
            // wenns nur einen Download gibt, macht dann keinen Sinn
            hBoxTop.setVisible(false);
            hBoxTop.setManaged(false);
        }
    }

    private void setTextArea(TextArea textArea) {
        textArea.setWrapText(true);
        textArea.setPrefRowCount(2);
        textArea.setPrefColumnCount(1);
    }

    private void initControl(int i) {
        addAboDto.lbl[i] = new Label(AboFieldNames.COLUMN_NAMES[i] + ":");

        switch (i) {
            case AboFieldNames.ABO_THEME_NO:
                setTextArea(addAboDto.textAreaTheme);
                addAboDto.textAreaTheme.setText(addAboDto.aboCopy.getTheme());
                break;
            case AboFieldNames.ABO_THEME_TITLE_NO:
                setTextArea(addAboDto.textAreaThemeTitle);
                addAboDto.textAreaThemeTitle.setText(addAboDto.aboCopy.getThemeTitle());
                break;
            case AboFieldNames.ABO_TITLE_NO:
                setTextArea(addAboDto.textAreaTitle);
                addAboDto.textAreaTitle.setText(addAboDto.aboCopy.getTitle());
                break;
            case AboFieldNames.ABO_SOMEWHERE_NO:
                setTextArea(addAboDto.textAreaSomewhere);
                addAboDto.textAreaSomewhere.setText(addAboDto.aboCopy.getSomewhere());
                break;

            case AboFieldNames.ABO_DESCRIPTION_NO:
                addAboDto.textAreaDescription.setWrapText(true);
                addAboDto.textAreaDescription.setPrefRowCount(4);
                addAboDto.textAreaDescription.setPrefColumnCount(1);
                addAboDto.textAreaDescription.setText(addAboDto.aboCopy.getDescription());
                break;
            case AboFieldNames.ABO_RESOLUTION_NO:
                ToggleGroup tg = new ToggleGroup();
                addAboDto.rbHd.setToggleGroup(tg);
                addAboDto.rbHigh.setToggleGroup(tg);
                addAboDto.rbLow.setToggleGroup(tg);
                switch (addAboDto.aboCopy.getResolution()) {
                    case FilmDataMTP.RESOLUTION_HD:
                        addAboDto.rbHd.setSelected(true);
                        break;
                    case FilmDataMTP.RESOLUTION_SMALL:
                        addAboDto.rbLow.setSelected(true);
                        break;
                    default:
                        addAboDto.aboCopy.setResolution(FilmDataMTP.RESOLUTION_NORMAL);
                        addAboDto.rbHigh.setSelected(true);
                }
                addAboDto.rbHd.setOnAction(event -> setResolution());
                addAboDto.rbHigh.setOnAction(event -> setResolution());
                addAboDto.rbLow.setOnAction(event -> setResolution());
                break;
            case AboFieldNames.ABO_THEME_EXACT_NO:
                addAboDto.cbx[i] = new CheckBox("");
                addAboDto.cbx[i].setSelected(addAboDto.aboCopy.isThemeExact());
                break;

            default:
                addAboDto.txt[i] = new TextField("");
                addAboDto.txt[i].setText(addAboDto.aboCopy.properties[i].getValue().toString());
        }

        addAboDto.cbxEditAll[i] = new CheckBox();
        addAboDto.cbxEditAll[i].setSelected(false);
        addAboDto.cbxEditAll[i].getStyleClass().add("chk-edit-all");
        addAboDto.cbxEditAll[i].selectedProperty().addListener((u, o, n) -> checkOk());
        GridPane.setHalignment(addAboDto.cbxEditAll[i], HPos.CENTER);
    }

    private void addLabel(int i) {
        final int grid = getGridLine(i);
        switch (i) {
            case AboFieldNames.ABO_NO_NO:
            case AboFieldNames.ABO_HIT_NO:
                if (addAboDto.aboList.size() == 1) {
                    // nur dann macht es Sinn
                    gridPane.add(addAboDto.lbl[i], 0, grid);
                }
                break;
            case AboFieldNames.ABO_THEME_EXACT_NO:
                addAboDto.lbl[i].setText("   Exakt:");
                gridPane.add(addAboDto.lbl[i], 0, grid);
                break;
            case AboFieldNames.ABO_MIN_DURATION_NO:
                addAboDto.lbl[i].setText("Dauer:");
                gridPane.add(addAboDto.lbl[i], 0, grid);
                break;
            case AboFieldNames.ABO_MAX_DURATION_NO:
                break;
            default:
                gridPane.add(addAboDto.lbl[i], 0, grid);
                break;
        }
    }

    private void addTextArea(TextArea textArea, int i, int grid) {
        FilterCheckRegEx fT = new FilterCheckRegEx(textArea);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> fT.checkPattern());
        textArea.textProperty().addListener((observable, oldValue, newValue) -> addAboDto.cbxEditAll[i].setSelected(true));
        textArea.textProperty().bindBidirectional(addAboDto.aboCopy.properties[i]);
        gridPane.add(textArea, 1, grid);
    }

    private void addTextField(int i) {
        final int grid = getGridLine(i);
        switch (i) {
            case AboFieldNames.ABO_THEME_NO:
                addTextArea(addAboDto.textAreaTheme, i, grid);
                break;
            case AboFieldNames.ABO_THEME_TITLE_NO:
                addTextArea(addAboDto.textAreaThemeTitle, i, grid);
                break;
            case AboFieldNames.ABO_TITLE_NO:
                addTextArea(addAboDto.textAreaTitle, i, grid);
                break;
            case AboFieldNames.ABO_SOMEWHERE_NO:
                addTextArea(addAboDto.textAreaSomewhere, i, grid);
                break;
            case AboFieldNames.ABO_DESCRIPTION_NO:
                addAboDto.textAreaDescription.textProperty().addListener((observable, oldValue, newValue) -> addAboDto.cbxEditAll[i].setSelected(true));
                addAboDto.textAreaDescription.textProperty().bindBidirectional(addAboDto.aboCopy.properties[i]);
                gridPane.add(addAboDto.textAreaDescription, 1, grid);
                break;

            case AboFieldNames.ABO_NO_NO:
                if (addAboDto.aboList.size() > 1) {
                    // nur dann brauchts das
                    break;
                }
                final Label lblNo = new Label();
                lblNo.setText(addAboDto.aboList.get(0).getNo() + "");
                gridPane.add(lblNo, 1, grid);
                break;
            case AboFieldNames.ABO_HIT_NO:
                if (addAboDto.aboList.size() > 1) {
                    // nur dann brauchts das
                    break;
                }
                final Label lblHit = new Label();
                lblHit.textProperty().bind(addAboDto.aboList.get(0).hitProperty().asString());
                gridPane.add(lblHit, 1, grid);
                break;
            case AboFieldNames.ABO_NAME_NO:
                setDefaultTxt(i, grid);
                addAboDto.txt[i].textProperty().addListener((observable, oldValue, newValue) -> {
                    if (addAboDto.txt[i].getText().isEmpty()) {
                        addAboDto.txt[i].setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
                    } else {
                        addAboDto.txt[i].setStyle("");
                    }
                });
                if (addAboDto.txt[i].getText().isEmpty()) {
                    addAboDto.txt[i].setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
                } else {
                    addAboDto.txt[i].setStyle("");
                }
                break;
            case AboFieldNames.ABO_RESOLUTION_NO:
                final Button btnHelpRes = P2Button.helpButton(addAboDto.stage,
                        "Auflösung", HelpText.ABO_RES);

                HBox h = new HBox();
                h.setAlignment(Pos.CENTER_RIGHT);
                HBox.setHgrow(h, Priority.ALWAYS);
                h.getChildren().add(btnHelpRes);

                HBox hRes = new HBox(10);
                hRes.setAlignment(Pos.CENTER_LEFT);
                hRes.getChildren().addAll(addAboDto.rbHd, addAboDto.rbHigh, addAboDto.rbLow, h);
                this.gridPane.add(hRes, 1, grid);
                break;
            case AboFieldNames.ABO_THEME_EXACT_NO:
                addAboDto.cbx[i].selectedProperty().bindBidirectional(addAboDto.aboCopy.properties[i]);
                addAboDto.cbx[i].selectedProperty().addListener((observable, oldValue, newValue) -> {
                    addAboDto.cbxEditAll[i].setSelected(true);
                });
                this.gridPane.add(addAboDto.cbx[i], 1, grid);
                break;
            case AboFieldNames.ABO_DATE_LAST_ABO_NO:
                addAboDto.txt[i].setEditable(false);
                addAboDto.txt[i].setDisable(true);
                addAboDto.txt[i].setText(addAboDto.aboCopy.getDate().toString());
                gridPane.add(addAboDto.txt[i], 1, grid);
                break;
            case AboFieldNames.ABO_GEN_DATE_NO:
                addAboDto.txt[i].setEditable(false);
                addAboDto.txt[i].setDisable(true);
                addAboDto.txt[i].setText(addAboDto.aboCopy.getGenDate().toString());
                gridPane.add(addAboDto.txt[i], 1, grid);
                break;
            case AboFieldNames.ABO_ACTIVE_NO:
                addAboDto.cbxOn.selectedProperty().bindBidirectional(addAboDto.aboCopy.activeProperty());
                addAboDto.cbxOn.setOnAction(a -> addAboDto.cbxEditAll[i].setSelected(true));
                this.gridPane.add(addAboDto.cbxOn, 1, grid);
                break;
            case AboFieldNames.ABO_SET_DATA_ID_NO:
                // mind. 1 Set gibts immer, Kontrolle oben bereits
                if (progData.setDataList.getSetDataListAbo().size() <= 1) {
                    // gibt nur ein Set: brauchts keine Auswahl
                    addAboDto.txt[i].setEditable(false);
                    addAboDto.txt[i].setDisable(true);
                    addAboDto.txt[i].setText(addAboDto.aboCopy.getSetData(progData).getVisibleName());
                    this.gridPane.add(addAboDto.txt[i], 1, grid);

                } else {
                    addAboDto.cboSetData.setMaxWidth(Double.MAX_VALUE);
                    addAboDto.cboSetData.setPrefWidth(20);
                    addAboDto.cboSetData.getItems().addAll(progData.setDataList.getSetDataListAbo());
                    addAboDto.cboSetData.valueProperty().bindBidirectional(addAboDto.aboCopy.setDataProperty());
                    addAboDto.cboSetData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                            addAboDto.cbxEditAll[i].setSelected(true));
                    this.gridPane.add(addAboDto.cboSetData, 1, grid);
                }
                break;
            case AboFieldNames.ABO_CHANNEL_NO:
                this.gridPane.add(addAboDto.mbChannel, 1, grid);
                break;
            case AboFieldNames.ABO_DEST_PATH_NO:
                ArrayList<String> path = progData.aboList.getAboDestinationPathList();
                if (!path.contains(addAboDto.aboCopy.getAboSubDir())) {
                    path.add(0, addAboDto.aboCopy.getAboSubDir());
                }
                addAboDto.cboDestination.setMaxWidth(Double.MAX_VALUE);

                addAboDto.cboDestination.setItems(FXCollections.observableArrayList(path));
                addAboDto.cboDestination.setEditable(true);
                addAboDto.cboDestination.getEditor().textProperty().bindBidirectional(addAboDto.aboCopy.aboSubDirProperty());
                addAboDto.cboDestination.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                    addAboDto.cbxEditAll[i].setSelected(true);
                });

                final TextField textField = new TextField("");
                textField.setEditable(false);
                textField.setDisable(true);
                if (addAboDto.aboCopy.getSetData().isGenAboSubDir()) {
                    textField.setText(addAboDto.aboCopy.getSetData().getAboSubDir_ENSubDir_Name());
                }
                addAboDto.aboCopy.setDataProperty().addListener((u, o, n) -> {
                    if (n != null) {
                        if (addAboDto.aboCopy.getSetData().isGenAboSubDir()) {
                            textField.setText(addAboDto.aboCopy.getSetData().getAboSubDir_ENSubDir_Name());
                        } else {
                            textField.setText("");
                        }
                    }
                });

                Button btn = new Button("");
                btn.setTooltip(new Tooltip("Zielpfad für das Abo anpassen"));
                btn.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_EDIT_ABO_PATH.getImageView());
                btn.setOnAction(event -> {
                    if (textField.isVisible()) {
                        textField.setVisible(false);
                        addAboDto.cboDestination.setVisible(true);
                        addAboDto.cboDestination.setValue(addAboDto.memory);
                    } else {
                        textField.setVisible(true);
                        addAboDto.cboDestination.setVisible(false);
                        addAboDto.memory = addAboDto.cboDestination.getValue();
                        addAboDto.cboDestination.setValue("");
                    }
                    addAboDto.cbxEditAll[i].setSelected(true);
                });
                final Button btnHelp = P2Button.helpButton(addAboDto.stage, "Unterordner anlegen",
                        HelpText.ABO_SUBDIR);

                final StackPane sp = new StackPane();
                sp.getChildren().addAll(textField, addAboDto.cboDestination);
                sp.setPrefWidth(20);

                HBox hbox = new HBox(10);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().addAll(sp, btn, btnHelp);
                HBox.setHgrow(sp, Priority.ALWAYS);
                this.gridPane.add(hbox, 1, grid);

                if (addAboDto.aboCopy.getAboSubDir().trim().isEmpty()) {
                    addAboDto.cboDestination.setVisible(false);
                } else {
                    textField.setVisible(false);
                }
                break;
            case AboFieldNames.ABO_TIME_RANGE_NO:
                initTimeRange();
                VBox vBox = new VBox(2);
                vBox.getChildren().addAll(addAboDto.lblTimeRange, addAboDto.slTimeRange);
                vBox.setAlignment(Pos.CENTER_RIGHT);
                this.gridPane.add(vBox, 1, grid);
                break;
            case AboFieldNames.ABO_MIN_DURATION_NO:
                initDur();
                this.gridPane.add(addAboDto.p2RangeBoxTime, 1, grid);
                GridPane.setHgrow(addAboDto.p2RangeBoxTime, Priority.ALWAYS);
                break;
            case AboFieldNames.ABO_MAX_DURATION_NO:
                break;
            case AboFieldNames.ABO_START_TIME_NO:
                HBox hB = new HBox(10);
                hB.setAlignment(Pos.CENTER_LEFT);

                addAboDto.chkStartTime.setSelected(!addAboDto.aboCopy.getStartTime().isEmpty());
                addAboDto.chkStartTime.setOnAction(a -> {
                    addAboDto.cbxEditAll[AboFieldNames.ABO_START_TIME_NO].setSelected(true);
                    if (addAboDto.chkStartTime.isSelected()) {
                        addAboDto.aboCopy.setStartTime(addAboDto.p2TimePicker.getTime());
                    } else {
                        addAboDto.aboCopy.setStartTime("");
                    }
                });

                addAboDto.p2TimePicker.setTime(addAboDto.aboCopy.getStartTime());
                addAboDto.p2TimePicker.setOnAction(a -> {
                    addAboDto.aboCopy.setStartTime(addAboDto.p2TimePicker.getTime());
                });
                addAboDto.p2TimePicker.disableProperty().bind(addAboDto.chkStartTime.selectedProperty().not());

                final Button btnHelpStartTime = P2Button.helpButton(addAboDto.stage, "Startzeit",
                        HelpText.ABO_START_TIME);

                HBox hb = new HBox();
                hb.setAlignment(Pos.CENTER_RIGHT);
                HBox.setHgrow(hb, Priority.ALWAYS);
                hb.getChildren().add(btnHelpStartTime);

                hB.getChildren().addAll(addAboDto.chkStartTime, addAboDto.p2TimePicker, hb);
                gridPane.add(hB, 1, grid);
                break;
            default:
                setDefaultTxt(i, grid);
                break;
        }

    }

    private int getGridLine(int i) {
        int ret = i;
        if (addAboDto.aboList.size() > 1 && i >= AboFieldNames.ABO_HIT_NO) {
            --ret;
        }
        if (i >= AboFieldNames.ABO_MAX_DURATION_NO) {
            --ret;
        }
        return ret;
    }

    private void initDur() {
        addAboDto.p2RangeBoxTime.maxValueProperty().bindBidirectional(addAboDto.aboCopy.maxDurationMinuteProperty());
        addAboDto.p2RangeBoxTime.minValueProperty().bindBidirectional(addAboDto.aboCopy.minDurationMinuteProperty());

        addAboDto.p2RangeBoxTime.maxValueProperty().addListener((observable, oldValue, newValue) ->
                addAboDto.cbxEditAll[AboFieldNames.ABO_MIN_DURATION_NO].setSelected(true));
        addAboDto.p2RangeBoxTime.minValueProperty().addListener((observable, oldValue, newValue) ->
                addAboDto.cbxEditAll[AboFieldNames.ABO_MIN_DURATION_NO].setSelected(true));
    }

    private void initTimeRange() {
        addAboDto.slTimeRange.setMin(FilterCheck.FILTER_ALL_OR_MIN);
        addAboDto.slTimeRange.setMax(FilterCheck.FILTER_TIME_RANGE_MAX_VALUE);
        addAboDto.slTimeRange.setShowTickLabels(true);
        addAboDto.slTimeRange.setMajorTickUnit(10);
        addAboDto.slTimeRange.setBlockIncrement(5);

        addAboDto.slTimeRange.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == FilterCheck.FILTER_ALL_OR_MIN) return "Alles";
                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        addAboDto.slTimeRange.setValue(addAboDto.aboCopy.getTimeRange());
        addAboDto.slTimeRange.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        addAboDto.aboCopy.setTimeRange((int) addAboDto.slTimeRange.getValue());
                        addAboDto.cbxEditAll[AboFieldNames.ABO_TIME_RANGE_NO].setSelected(true);
                    }
                }
        );

        addAboDto.slTimeRange.valueProperty().addListener((observable, oldValue, newValue) -> {
            setLabelSlider();
        });
        setLabelSlider();
    }

    private void setLabelSlider() {
        final String txtAll = "Alles";

        int i = (int) addAboDto.slTimeRange.getValue();
        String tNr = i + "";

        if (i == FilterCheck.FILTER_ALL_OR_MIN) {
            addAboDto.lblTimeRange.setText(txtAll);
        } else {
            addAboDto.lblTimeRange.setText(tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }

    private void setResolution() {
        addAboDto.cbxEditAll[AboFieldNames.ABO_RESOLUTION_NO].setSelected(true);
        if (addAboDto.rbHigh.isSelected()) {
            addAboDto.aboCopy.setResolution(FilmDataMTP.RESOLUTION_NORMAL);
        }
        if (addAboDto.rbHd.isSelected()) {
            addAboDto.aboCopy.setResolution(FilmDataMTP.RESOLUTION_HD);
        }
        if (addAboDto.rbLow.isSelected()) {
            addAboDto.aboCopy.setResolution(FilmDataMTP.RESOLUTION_SMALL);
        }
    }

    private void setDefaultTxt(int i, int grid) {
        addAboDto.txt[i].textProperty().bindBidirectional(addAboDto.aboCopy.properties[i]);
        addAboDto.txt[i].textProperty().addListener((observable, oldValue, newValue) -> addAboDto.cbxEditAll[i].setSelected(true));
        gridPane.add(addAboDto.txt[i], 1, grid);
    }

    private void addCheckBoxEditAll(int i) {
        final int grid = getGridLine(i);

        switch (i) {
            case AboFieldNames.ABO_NO_NO:
            case AboFieldNames.ABO_HIT_NO:
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
                gridPane.add(addAboDto.cbxEditAll[i], 2, grid);
                break;
            case AboFieldNames.ABO_SET_DATA_ID_NO:
                if (progData.setDataList.getSetDataListAbo().size() > 1) {
                    // nur dann kann man was ändern
                    gridPane.add(addAboDto.cbxEditAll[i], 2, grid);
                }
                break;
        }
    }

    private void checkOk() {
        // nur wenn einer geklickt ist, wird auch was geändert -> nur dann macht OK Sinn
//        boolean ok = false;
//        for (int ii = 0; ii < addAboDto.cbxEditAll.length; ++ii) {
//            if (addAboDto.cbxEditAll[ii] == null) {
//                continue;
//            }
//            if (addAboDto.cbxEditAll[ii].isSelected()) {
//                ok = true;
//                break;
//            }
//        }
//        okProp.set(ok);
    }
}
