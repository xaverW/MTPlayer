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
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.gui.dialog.downloadadd.DownloadAddDialogFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AboAddDialogGui {

    private final AddAboDto addAboDto;
    private final ProgData progData;
    private final VBox vBoxCont;
    private final HBox hBoxTop = new HBox();

    public AboAddDialogGui(ProgData progData, AddAboDto addAboDto, VBox vBoxCont) {
        //hier wird ein neues Abo angelegt -> Button, abo ist immer neu
        this.addAboDto = addAboDto;
        this.progData = progData;
        this.vBoxCont = vBoxCont;
    }

    public AboAddDialogGui(ProgData progData, AddAboDto addAboDto, FilmFilter filmFilter, VBox vBoxCont) {
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
        // Titel
        HBox hHit = new HBox(P2LibConst.PADDING_HBOX);
        hHit.getChildren().addAll(new Label(AboFieldNames.ABO_NO + ":"),
                addAboDto.lblAboNo,
                P2GuiTools.getHDistance(20),
                new Label(AboFieldNames.ABO_HIT + ":"), addAboDto.lblHit);
        gridPane.add(hHit, 1, row);
        gridPane.getRowConstraints().add(row, P2ColumnConstraints.getRcPrefSizeTop());

        Font font = Font.font(null, FontWeight.BOLD, -1);
        addAboDto.btnAll.setFont(font);
        addAboDto.btnAll.setWrapText(true);
        addAboDto.btnAll.setMinHeight(Region.USE_PREF_SIZE);

        gridPane.add(addAboDto.btnAll, 2, row);
        GridPane.setValignment(addAboDto.btnAll, VPos.TOP);

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

        final Button btnHelpRes = P2Button.helpButton(addAboDto.stage,
                "Auflösung", HelpText.ABO_RES);

        HBox hResAll = new HBox(P2LibConst.PADDING_HBOX);
        hResAll.getChildren().addAll(hRes, btnHelpRes);
        HBox.setHgrow(hRes, Priority.ALWAYS);

        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO__RESOLUTION + ":"), 0, ++row);
        gridPane.add(hResAll, 1, row);
        gridPane.add(addAboDto.chkResolutionAll, 2, row);
        GridPane.setHgrow(hRes, Priority.ALWAYS);

        // Sender
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_CHANNEL + ":"), 0, ++row);
        gridPane.add(addAboDto.mbChannel, 1, row);
        gridPane.add(addAboDto.chkChannelAll, 2, row);
        GridPane.setHgrow(addAboDto.mbChannel, Priority.ALWAYS);
        addAboDto.mbChannel.setMaxWidth(Double.MAX_VALUE);

        // Thema
        setTextArea(addAboDto.textAreaTheme);
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_THEME + ":"), 0, ++row);
        gridPane.add(addAboDto.textAreaTheme, 1, row);
        gridPane.add(addAboDto.chkThemeAll, 2, row);

        // Thema-Exakt
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_THEME_EXACT + ":"), 0, ++row);
        gridPane.add(addAboDto.chkThemeExact, 1, row);
        gridPane.add(addAboDto.chkThemeExactAll, 2, row);

        // Thema-Titel
        setTextArea(addAboDto.textAreaThemeTitle);
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_THEME_TITLE + ":"), 0, ++row);
        gridPane.add(addAboDto.textAreaThemeTitle, 1, row);
        gridPane.add(addAboDto.chkThemeTitleAll, 2, row);

        // Titel
        setTextArea(addAboDto.textAreaTitle);
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_TITLE + ":"), 0, ++row);
        gridPane.add(addAboDto.textAreaTitle, 1, row);
        gridPane.add(addAboDto.chkTitleAll, 2, row);

        // Irgendwo
        setTextArea(addAboDto.textAreaSomewhere);
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_SOMEWHERE + ":"), 0, ++row);
        gridPane.add(addAboDto.textAreaSomewhere, 1, row);
        gridPane.add(addAboDto.chkSomewhereAll, 2, row);

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
        final Button btnHelpStartTime = P2Button.helpButton(addAboDto.stage, "Startzeit",
                HelpText.ABO_START_TIME);

        HBox hBoxTime = new HBox(10);
        hBoxTime.setAlignment(Pos.CENTER_LEFT);
        hBoxTime.getChildren().addAll(addAboDto.chkStartTime, addAboDto.p2TimePicker, P2GuiTools.getHBoxGrower(), btnHelpStartTime);

        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_START_TIME + ":"), 0, ++row);
        gridPane.add(hBoxTime, 1, row);
        gridPane.add(addAboDto.chkStartTimeAll, 2, row);

        // Zielpfad
        final Button btnHelp = P2Button.helpButton(addAboDto.stage, "Unterordner anlegen",
                HelpText.ABO_SUBDIR);

        addAboDto.cboDestination.setMaxWidth(Double.MAX_VALUE);
        addAboDto.cboDestination.setEditable(true);

        final StackPane sp = new StackPane();
        sp.getChildren().addAll(addAboDto.lblDestination, addAboDto.cboDestination);
        sp.setPrefWidth(20);

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(addAboDto.chkDestination, sp, btnHelp);
        HBox.setHgrow(sp, Priority.ALWAYS);

        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_DEST_DIR + ":"), 0, ++row);
        gridPane.add(hbox, 1, row);
        gridPane.add(addAboDto.chkDestinationAll, 2, row);

        // ProgrammSet -> mind. 1 Set gibts immer, Kontrolle oben bereits
        gridPane.add(addAboDto.textSet, 0, ++row);
        addAboDto.cboSetData.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(addAboDto.cboSetData, 1, row);
        gridPane.add(addAboDto.chkSetAll, 2, row);

        // Letztes Abo
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_DATE_LAST_ABO + ":"), 0, ++row);
        gridPane.add(addAboDto.lblLastAbo, 1, row);

        // Angelegt
        gridPane.add(DownloadAddDialogFactory.getText(AboFieldNames.ABO_GEN_DATE + ":"), 0, ++row);
        gridPane.add(addAboDto.lblGenDate, 1, row);
    }

    public void init() {
        if (addAboDto.addAboData.length == 1) {
            // wenns nur einen Download gibt, macht dann keinen Sinn
            hBoxTop.setVisible(false);
            hBoxTop.setManaged(false);
        }
        AboAddAllFactory.init(addAboDto);
    }

    private void setTextArea(TextArea textArea) {
        textArea.setWrapText(true);
        textArea.setPrefRowCount(2);
        textArea.setPrefColumnCount(1);
    }
}
