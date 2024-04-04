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
import de.p2tools.mtplayer.controller.data.abo.AboFieldNames;
import de.p2tools.mtplayer.gui.dialog.downloadadd.DownloadAddDialogFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AboAddDialogGuiAbo {

    private final AddAboDto addAboDto;
    private final ProgData progData;
    private final VBox vBoxCont;
    private final Stage stage;

    public AboAddDialogGuiAbo(ProgData progData, Stage stage, AddAboDto addAboDto, VBox vBoxCont) {
        //hier wird ein neues Abo angelegt -> Button, abo ist immer neu
        this.progData = progData;
        this.stage = stage;
        this.addAboDto = addAboDto;
        this.vBoxCont = vBoxCont;
    }

    public void addCont() {
        // Grid
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSizeCenter());

        vBoxCont.getChildren().add(gridPane);

        int row = 0;

        // Aktiv
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_ACTIVE + ":"), 0, ++row);
        gridPane.add(addAboDto.chkActive, 1, row);
        gridPane.add(addAboDto.chkActiveAll, 2, row);

        // Name
        addAboDto.txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (addAboDto.txtName.getText().isEmpty()) {
                addAboDto.txtName.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                addAboDto.txtName.setStyle("");
            }
        });
        if (addAboDto.txtName.getText().isEmpty()) {
            addAboDto.txtName.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
        } else {
            addAboDto.txtName.setStyle("");
        }
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_NAME + ":"), 0, ++row);
        gridPane.add(addAboDto.txtName, 1, row);

        // Beschreibung
        addAboDto.textAreaDescription.setWrapText(true);
        addAboDto.textAreaDescription.setPrefRowCount(4);
        addAboDto.textAreaDescription.setPrefColumnCount(1);
        addAboDto.textAreaDescription.setText(addAboDto.getAct().abo.getDescription());
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_DESCRIPTION + ":"), 0, ++row);
        gridPane.add(addAboDto.textAreaDescription, 1, row);
        gridPane.add(addAboDto.chkDescriptionAll, 2, row);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(new Label(), 0, ++row);

        // Auflösung
        HBox hRes = new HBox(10);
        hRes.setPadding(new Insets(2));
        hRes.getStyleClass().add("downloadDialog");
        hRes.setAlignment(Pos.CENTER_LEFT);
        hRes.getChildren().addAll(addAboDto.rbHd, addAboDto.rbHigh, addAboDto.rbLow);

        ToggleGroup tg = new ToggleGroup();
        addAboDto.rbHd.setToggleGroup(tg);
        addAboDto.rbHigh.setToggleGroup(tg);
        addAboDto.rbLow.setToggleGroup(tg);

        final Button btnHelpRes = P2Button.helpButton(stage,
                "Auflösung", HelpText.ABO_RES);

        HBox hResAll = new HBox(P2LibConst.PADDING_HBOX);
        hResAll.getChildren().addAll(hRes, btnHelpRes);
        HBox.setHgrow(hRes, Priority.ALWAYS);

        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO__RESOLUTION + ":"), 0, ++row);
        gridPane.add(hResAll, 1, row);
        gridPane.add(addAboDto.chkResolutionAll, 2, row);
        GridPane.setHgrow(hRes, Priority.ALWAYS);

        // Zeitraum
        VBox vBox = new VBox(2);
        vBox.getChildren().addAll(addAboDto.lblTimeRange, addAboDto.slTimeRange);
        vBox.setAlignment(Pos.CENTER_RIGHT);

        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_TIME_RANGE + ":"), 0, ++row);
        gridPane.add(vBox, 1, row);
        gridPane.add(addAboDto.chkTimeRangeAll, 2, row);

        // Dauer
        gridPane.add(DownloadAddDialogFactory.getText("Dauer:"), 0, ++row);
        gridPane.add(addAboDto.p2RangeBoxDuration, 1, row);
        gridPane.add(addAboDto.chkDurationAll, 2, row);
        GridPane.setHgrow(addAboDto.p2RangeBoxDuration, Priority.ALWAYS);

        // Startzeit
        final Button btnHelpStartTime = P2Button.helpButton(stage, "Startzeit",
                HelpText.ABO_START_TIME);

        HBox hBoxTime = new HBox(10);
        hBoxTime.setAlignment(Pos.CENTER_LEFT);
        hBoxTime.getChildren().addAll(addAboDto.chkStartTime, addAboDto.p2TimePicker, P2GuiTools.getHBoxGrower(), btnHelpStartTime);

        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_START_TIME + ":"), 0, ++row);
        gridPane.add(hBoxTime, 1, row);
        gridPane.add(addAboDto.chkStartTimeAll, 2, row);
    }

    public void init() {
        AboAddAllFactory.init(addAboDto);
    }

    private void setTextArea(TextArea textArea) {
        textArea.setWrapText(true);
        textArea.setPrefRowCount(2);
        textArea.setPrefColumnCount(1);
    }
}
