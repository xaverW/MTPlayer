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
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DownloadAddDialogGui {

    private final AddDownloadDto addDownloadDto;
    private final ProgData progData;
    private final VBox vBoxCont;
    private final HBox hBoxTop = new HBox();
    private final GridPane gridPane = new GridPane();

    public DownloadAddDialogGui(ProgData progData, AddDownloadDto addDownloadDto, VBox vBoxCont) {
        this.progData = progData;
        this.addDownloadDto = addDownloadDto;
        this.vBoxCont = vBoxCont;
        addDownloadDto.actFilmIsShown.addListener((u, o, n) ->
                gridPane.setDisable(addDownloadDto.getAct().downloadIsRunning()));
    }

    public void addCont() {
        addDownloadDto.lblFilm.setStyle("-fx-font-weight: bold;");
        addDownloadDto.lblFilmTitle.setStyle("-fx-font-weight: bold;");

        // Top
        hBoxTop.getStyleClass().add("downloadDialog");
        hBoxTop.setSpacing(20);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setPadding(new Insets(5));
        hBoxTop.getChildren().addAll(addDownloadDto.btnPrev, addDownloadDto.lblSum, addDownloadDto.btnNext);
        vBoxCont.getChildren().add(hBoxTop);

        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(5));
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        vBoxCont.setPadding(new Insets(10));

        gridPane.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.actFilmIsShown.addListener((u, o, n) -> gridPane.setDisable(addDownloadDto.getAct().downloadIsRunning()));

        int row = 0;

        // Titel
        Font font = Font.font(null, FontWeight.BOLD, -1);
        addDownloadDto.btnAll.setFont(font);
        addDownloadDto.btnAll.setWrapText(true);
        addDownloadDto.btnAll.setMinHeight(Region.USE_PREF_SIZE);

        gridPane.add(addDownloadDto.lblFilm, 0, row);
        gridPane.add(addDownloadDto.lblFilmTitle, 1, row);
        gridPane.add(addDownloadDto.btnAll, 3, row, 1, 2);
        GridPane.setValignment(addDownloadDto.btnAll, VPos.TOP);
        // Datum - Zeit - Länge
        gridPane.add(addDownloadDto.lblFilmDateTime, 1, ++row);

        ++row;
        // Auflösung
        gridPane.add(DownloadAddDialogFactory.getText("Auflösung:"), 0, ++row);
        final HBox hBoxSize = new HBox();
        hBoxSize.getStyleClass().add("downloadDialog");
        hBoxSize.setSpacing(20);
        hBoxSize.setPadding(new Insets(5));
        hBoxSize.getChildren().addAll(addDownloadDto.rbHd, addDownloadDto.rbHigh, addDownloadDto.rbSmall);

        final ToggleGroup toggleGroupSize = new ToggleGroup();
        addDownloadDto.rbHd.setToggleGroup(toggleGroupSize);
        addDownloadDto.rbHigh.setToggleGroup(toggleGroupSize);
        addDownloadDto.rbSmall.setToggleGroup(toggleGroupSize);

        addDownloadDto.rbHd.setTextAlignment(TextAlignment.CENTER);
        addDownloadDto.rbHd.setMinHeight(Region.USE_PREF_SIZE);
        addDownloadDto.rbHigh.setTextAlignment(TextAlignment.CENTER);
        addDownloadDto.rbHigh.setMinHeight(Region.USE_PREF_SIZE);
        addDownloadDto.rbSmall.setTextAlignment(TextAlignment.CENTER);
        addDownloadDto.rbSmall.setMinHeight(Region.USE_PREF_SIZE);

        gridPane.add(hBoxSize, 1, row, 2, 1);
        gridPane.add(addDownloadDto.chkResolutionAll, 3, row);

        // Set
        gridPane.add(addDownloadDto.textSet, 0, ++row);
        addDownloadDto.cboSetData.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(addDownloadDto.cboSetData, 1, row, 2, 1);
        gridPane.add(addDownloadDto.chkSetAll, 3, row);

        // Programmaufruf
        GridPane gCall = new GridPane();
        // -> und wenn Download
        gCall.add(addDownloadDto.lblProgramIsDownload, 0, 0);

        // -> oder für Downloads über ein Programm
        addDownloadDto.btnProgramCallReset.setTooltip(new Tooltip("Reset"));
        addDownloadDto.btnProgramCallReset.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());

        HBox hBoxArray1 = new HBox(P2LibConst.PADDING_HBOX);
        HBox.setHgrow(addDownloadDto.textAreaProg, Priority.ALWAYS);
        hBoxArray1.setAlignment(Pos.CENTER_LEFT);
        hBoxArray1.getChildren().addAll(addDownloadDto.btnProgramCallHelp, addDownloadDto.textAreaProg);

        HBox hBoxArray2 = new HBox(P2LibConst.PADDING_HBOX);
        HBox.setHgrow(addDownloadDto.textAreaCallArray, Priority.ALWAYS);
        hBoxArray2.setAlignment(Pos.CENTER_LEFT);
        hBoxArray2.getChildren().addAll(addDownloadDto.btnProgramCallReset, addDownloadDto.textAreaCallArray);

        addDownloadDto.textAreaCallArray.setMaxHeight(Double.MAX_VALUE);
        addDownloadDto.textAreaCallArray.setPrefRowCount(6);
        addDownloadDto.textAreaCallArray.setWrapText(true);

        addDownloadDto.vBoxProgramCall.getChildren().addAll(hBoxArray1, hBoxArray2);

        gCall.add(addDownloadDto.vBoxProgramCall, 0, 1);

        TitledPane tpCall = new TitledPane("", new HBox());
        tpCall.setExpanded(false);
        tpCall.setContent(gCall);
        Text text = DownloadAddDialogFactory.getText("Programmaufruf:");
        gridPane.add(text, 0, ++row);
        GridPane.setValignment(text, VPos.TOP);
        gridPane.add(tpCall, 1, row, 2, 1);
        // -> Programmaufruf

        // URL
        addDownloadDto.p2HyperlinkUrlFilm.setWrapText(true);
        addDownloadDto.p2HyperlinkUrlFilm.setMinHeight(Region.USE_PREF_SIZE);
        addDownloadDto.p2HyperlinkUrlFilm.setPadding(new Insets(5));

        addDownloadDto.p2HyperlinkUrlDownload.setWrapText(true);
        addDownloadDto.p2HyperlinkUrlDownload.setMinHeight(Region.USE_PREF_SIZE);
        addDownloadDto.p2HyperlinkUrlDownload.setPadding(new Insets(5));

        TitledPane tpUrl = new TitledPane("", new HBox());
        tpUrl.setExpanded(false);
        GridPane gUrl = new GridPane();
        gUrl.add(new Label("Film-URL: "), 0, 0);
        gUrl.add(addDownloadDto.p2HyperlinkUrlFilm, 1, 0, 3, 1);
        gUrl.add(new Label("URL: "), 0, 1);
        gUrl.add(addDownloadDto.p2HyperlinkUrlDownload, 1, 1, 3, 1);
        tpUrl.setContent(gUrl);

        gridPane.add(new Label("URL:"), 0, ++row);
        gridPane.add(tpUrl, 1, row, 2, 1);

        ++row;
        ++row;
        // Dateiname
        gridPane.add(DownloadAddDialogFactory.getText("Dateiname:"), 0, ++row);
        gridPane.add(addDownloadDto.txtName, 1, row, 2, 1);

        // Pfad
        gridPane.add(DownloadAddDialogFactory.getText("Pfad:"), 0, ++row);
        addDownloadDto.cboPath.setMaxWidth(Double.MAX_VALUE);
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.getChildren().addAll(addDownloadDto.btnDest, addDownloadDto.btnPropose, addDownloadDto.btnClean);

        gridPane.add(addDownloadDto.cboPath, 1, row);
        gridPane.add(hBox, 2, row);
        gridPane.add(addDownloadDto.chkPathAll, 3, row);

        HBox hBox2 = new HBox();
        hBox2.getChildren().add(addDownloadDto.lblFree);
        hBox2.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(hBox2, 1, ++row, 2, 1);

        // Subtitle
        gridPane.add(DownloadAddDialogFactory.getText("Untertitel:"), 0, ++row);
        gridPane.add(addDownloadDto.chkSubtitle, 1, row);
        gridPane.add(addDownloadDto.chkSubTitleAll, 3, row);

        // Info
        gridPane.add(DownloadAddDialogFactory.getText("Infodatei:"), 0, ++row);
        gridPane.add(addDownloadDto.chkInfo, 1, row);
        gridPane.add(addDownloadDto.chkInfoAll, 3, row);

        // Startzeit
        final HBox hBoxStartTime = new HBox();
        hBoxStartTime.getStyleClass().add("downloadDialog");
        hBoxStartTime.setAlignment(Pos.CENTER_LEFT);
        hBoxStartTime.setSpacing(20);
        hBoxStartTime.setPadding(new Insets(5));
        hBoxStartTime.getChildren().addAll(addDownloadDto.rbStartNotYet,
                addDownloadDto.rbStartNow, addDownloadDto.rbStartAtTime,
                addDownloadDto.p2TimePicker);
        gridPane.add(DownloadAddDialogFactory.getText("Startzeit:"), 0, ++row);
        gridPane.add(hBoxStartTime, 1, row, 2, 1);
        gridPane.add(addDownloadDto.chkStartTimeAll, 3, row);


        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSizeCenter());
        vBoxCont.getChildren().add(gridPane);
    }

    public void init() {
        if (addDownloadDto.addDownloadData.length == 1) {
            // wenns nur einen Download gibt, macht dann keinen Sinn
            hBoxTop.setVisible(false);
            hBoxTop.setManaged(false);
        }
        DownloadAddAllFactory.init(addDownloadDto);
    }
}
