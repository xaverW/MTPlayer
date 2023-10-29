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

package de.p2tools.mtplayer.gui.dialog.downloadadd;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class DownloadAddDialogGui {

    private final AddDto addDto;
    private final ProgData progData;
    private final VBox vBoxCont;
    private final HBox hBoxTop = new HBox();

    public DownloadAddDialogGui(ProgData progData, AddDto addDto, VBox vBoxCont) {
        this.progData = progData;
        this.addDto = addDto;
        this.vBoxCont = vBoxCont;
    }

    public void addCont() {
        addDto.lblFilm.setStyle("-fx-font-weight: bold;");
        addDto.lblFilmTitle.setStyle("-fx-font-weight: bold;");

        // Top
        hBoxTop.getStyleClass().add("downloadDialog");
        hBoxTop.setSpacing(20);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setPadding(new Insets(5));
        hBoxTop.getChildren().addAll(addDto.btnPrev, addDto.lblSum, addDto.btnNext);

        vBoxCont.getChildren().add(hBoxTop);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(5));
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        vBoxCont.setPadding(new Insets(10));

        gridPane.setDisable(addDto.getAct().downloadIsRunning());
        addDto.actFilmIsShown.addListener((u, o, n) -> gridPane.setDisable(addDto.getAct().downloadIsRunning()));

        int row = 0;
        // Titel
        gridPane.add(addDto.lblFilm, 0, row);
        gridPane.add(addDto.lblFilmTitle, 1, row);
        gridPane.add(addDto.lblAll, 3, row, 1, 2);
        addDto.lblAll.setMinHeight(Region.USE_PREF_SIZE);
        GridPane.setValignment(addDto.lblAll, VPos.TOP);
        // Datum - Zeit - Länge
        gridPane.add(addDto.lblFilmDateTime, 1, ++row);

        ++row;
        // Auflösung
        gridPane.add(DownloadAddDialogFactory.getText("Auflösung:"), 0, ++row);
        final HBox hBoxSize = new HBox();
        hBoxSize.getStyleClass().add("downloadDialog");
        hBoxSize.setSpacing(20);
        hBoxSize.setPadding(new Insets(5));
        hBoxSize.getChildren().addAll(addDto.rbHd, addDto.rbHigh, addDto.rbSmall);
        gridPane.add(hBoxSize, 1, row, 2, 1);
        gridPane.add(addDto.chkResolutionAll, 3, row);

        // Set
        gridPane.add(addDto.textSet, 0, ++row);
        addDto.cboSetData.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(addDto.cboSetData, 1, row, 2, 1);
        gridPane.add(addDto.chkSetAll, 3, row);

        // Programmaufruf
        GridPane gCall = new GridPane();
        // -> und wenn Download
        gCall.add(addDto.lblProgramIsDownload, 0, 0);

        // -> oder für Downloads über ein Programm
        addDto.btnProgramCallReset.setTooltip(new Tooltip("Reset"));
        addDto.btnProgramCallReset.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_RESET.getImageView());

        HBox hBoxArray1 = new HBox(P2LibConst.DIST_HBOX);
        HBox.setHgrow(addDto.textAreaProg, Priority.ALWAYS);
        hBoxArray1.setAlignment(Pos.CENTER_LEFT);
        hBoxArray1.getChildren().addAll(addDto.btnProgramCallHelp, addDto.textAreaProg);

        HBox hBoxArray2 = new HBox(P2LibConst.DIST_HBOX);
        HBox.setHgrow(addDto.textAreaCallArray, Priority.ALWAYS);
        hBoxArray2.setAlignment(Pos.CENTER_LEFT);
        hBoxArray2.getChildren().addAll(addDto.btnProgramCallReset, addDto.textAreaCallArray);

        addDto.textAreaCallArray.setMaxHeight(Double.MAX_VALUE);
        addDto.textAreaCallArray.setPrefRowCount(6);
        addDto.textAreaCallArray.setWrapText(true);

        addDto.vBoxProgramCall.getChildren().addAll(hBoxArray1, hBoxArray2);

        gCall.add(addDto.vBoxProgramCall, 0, 1);

        TitledPane tpCall = new TitledPane("", new HBox());
        tpCall.setExpanded(false);
        tpCall.setContent(gCall);
        Text text = DownloadAddDialogFactory.getText("Programmaufruf:");
        gridPane.add(text, 0, ++row);
        GridPane.setValignment(text, VPos.TOP);
        gridPane.add(tpCall, 1, row, 2, 1);
        // -> Programmaufruf

        // URL
        addDto.p2HyperlinkUrlFilm.setWrapText(true);
        addDto.p2HyperlinkUrlFilm.setMinHeight(Region.USE_PREF_SIZE);
        addDto.p2HyperlinkUrlFilm.setPadding(new Insets(5));

        addDto.p2HyperlinkUrlDownload.setWrapText(true);
        addDto.p2HyperlinkUrlDownload.setMinHeight(Region.USE_PREF_SIZE);
        addDto.p2HyperlinkUrlDownload.setPadding(new Insets(5));

        TitledPane tpUrl = new TitledPane("", new HBox());
        tpUrl.setExpanded(false);
        GridPane g = new GridPane();
        g.add(new Label("Film-URL: "), 0, row);
        g.add(addDto.p2HyperlinkUrlFilm, 1, row++, 3, 1);
        g.add(new Label("URL: "), 0, row);
        g.add(addDto.p2HyperlinkUrlDownload, 1, row++, 3, 1);
        tpUrl.setContent(g);

        gridPane.add(new Label("URL:"), 0, ++row);
        gridPane.add(tpUrl, 1, row, 2, 1);

        // Dateiname
        gridPane.add(DownloadAddDialogFactory.getText("Dateiname:"), 0, ++row);
        gridPane.add(addDto.txtName, 1, row, 2, 1);

        // Pfad
        gridPane.add(DownloadAddDialogFactory.getText("Pfad:"), 0, ++row);
        addDto.cboPath.setMaxWidth(Double.MAX_VALUE);
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.getChildren().addAll(addDto.btnDest, addDto.btnPropose, addDto.btnClean);

        gridPane.add(addDto.cboPath, 1, row);
        gridPane.add(hBox, 2, row);
        gridPane.add(addDto.chkPathAll, 3, row);

        HBox hBox2 = new HBox();
        hBox2.getChildren().add(addDto.lblFree);
        hBox2.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(hBox2, 1, ++row, 2, 1);

        // Subtitle
        gridPane.add(DownloadAddDialogFactory.getText("Untertitel:"), 0, ++row);
        gridPane.add(addDto.chkSubtitle, 1, row);
        gridPane.add(addDto.chkSubTitleAll, 3, row);

        // Info
        gridPane.add(DownloadAddDialogFactory.getText("Infodatei:"), 0, ++row);
        gridPane.add(addDto.chkInfo, 1, row);
        gridPane.add(addDto.chkInfoAll, 3, row);

        // Startzeit
        final HBox hBoxStartTime = new HBox();
        hBoxStartTime.getStyleClass().add("downloadDialog");
        hBoxStartTime.setAlignment(Pos.CENTER_LEFT);
        hBoxStartTime.setSpacing(20);
        hBoxStartTime.setPadding(new Insets(5));
        hBoxStartTime.getChildren().addAll(addDto.rbStartNotYet,
                addDto.rbStartNow, addDto.rbStartAtTime,
                addDto.p2TimePicker);
        gridPane.add(DownloadAddDialogFactory.getText("Startzeit:"), 0, ++row);
        gridPane.add(hBoxStartTime, 1, row, 2, 1);
        gridPane.add(addDto.chkStartTimeAll, 3, row);


        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSizeCenter());
        vBoxCont.getChildren().add(gridPane);
    }

    public void init() {
        if (progData.setDataList.getSetDataListSave().size() == 1) {
            // wenns nur ein Set gibt, macht dann keinen Sinn
            addDto.textSet.setVisible(false);
            addDto.textSet.setManaged(false);
            addDto.cboSetData.setVisible(false);
            addDto.cboSetData.setManaged(false);
            addDto.chkSetAll.setVisible(false);
            addDto.chkSetAll.setManaged(false);
        }

        if (addDto.downloadAddData.length == 1) {
            // wenns nur einen Download gibt, macht dann keinen Sinn
            hBoxTop.setVisible(false);
            hBoxTop.setManaged(false);
            addDto.lblAll.setVisible(false);
            addDto.lblAll.setManaged(false);

            addDto.chkSetAll.setVisible(false);
            addDto.chkSetAll.setManaged(false);
            addDto.chkResolutionAll.setVisible(false);
            addDto.chkResolutionAll.setManaged(false);
            addDto.chkPathAll.setVisible(false);
            addDto.chkPathAll.setManaged(false);
            addDto.chkSubTitleAll.setVisible(false);
            addDto.chkSubTitleAll.setManaged(false);
            addDto.chkInfoAll.setVisible(false);
            addDto.chkInfoAll.setManaged(false);
            addDto.chkStartTimeAll.setVisible(false);
            addDto.chkStartTimeAll.setManaged(false);
        }
    }
}