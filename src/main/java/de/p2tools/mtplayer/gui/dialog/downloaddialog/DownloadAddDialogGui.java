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

package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DownloadAddDialogGui {

    private final HBox hBoxSize = new HBox();
    private final Label lblSet;
    private final ComboBox<SetData> cboSet;
    private final ComboBox<String> cboPath;
    private final Button btnDest;
    private final Button btnPropose;
    private final Button btnClean;
    private final TextField txtName;
    private final CheckBox cbxInfo;
    private final CheckBox cbxSubtitle;
    private final RadioButton rbHd;
    private final RadioButton rbHigh;
    private final RadioButton rbSmall;
    private final Label lblFree;
    private final Label lblFilm = new Label("Film:");
    private final Label lblFilmTitle;
    private final VBox vBox;
    private final HBox hBoxTop;
    private final Button btnPrev;
    private final Button btnNext;
    private final Label lblSum;
    Label lblAll;
    CheckBox chkSetAll;
    CheckBox chkSizeAll;
    CheckBox chkPathAll;
    CheckBox chkSubTitleAll;
    CheckBox chkInfoAll;

    public DownloadAddDialogGui(VBox vBox, HBox hBoxTop,
                                Button btnPrev, Button btnNext, Label lblSum, Label lblSet,
                                ComboBox<SetData> cboSet,
                                ComboBox<String> cboPath,
                                Button btnDest,
                                Button btnPropose,
                                Button btnClean,
                                TextField txtName, CheckBox cbxInfo,
                                CheckBox cbxSubtitle,
                                RadioButton rbHd,
                                RadioButton rbHigh,
                                RadioButton rbSmall,
                                Label lblFree,
                                Label lblFilmTitle,

                                Label lblAll,
                                CheckBox chkSetAll,
                                CheckBox chkSizeAll,
                                CheckBox chkPathAll,
                                CheckBox chkSubTitleAll,
                                CheckBox chkInfoAll) {
        this.vBox = vBox;
        this.hBoxTop = hBoxTop;
        this.btnPrev = btnPrev;
        this.btnNext = btnNext;
        this.lblSum = lblSum;
        this.lblSet = lblSet;

        this.cboSet = cboSet;
        this.cboPath = cboPath;
        this.btnDest = btnDest;
        this.btnPropose = btnPropose;
        this.btnClean = btnClean;
        this.txtName = txtName;
        this.cbxInfo = cbxInfo;
        this.cbxSubtitle = cbxSubtitle;
        this.rbHd = rbHd;
        this.rbHigh = rbHigh;
        this.rbSmall = rbSmall;
        this.lblFree = lblFree;
        this.lblFilmTitle = lblFilmTitle;
        this.lblAll = lblAll;
        this.chkSetAll = chkSetAll;
        this.chkSizeAll = chkSizeAll;
        this.chkPathAll = chkPathAll;
        this.chkSubTitleAll = chkSubTitleAll;
        this.chkInfoAll = chkInfoAll;
    }

    public void addCont() {
        final VBox vBoxAllDownloads = new VBox();
        vBoxAllDownloads.getStyleClass().add("downloadDialog");
        hBoxSize.getStyleClass().add("downloadDialog");

        lblFilm.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setStyle("-fx-font-weight: bold;");

        // Top
        hBoxTop.setSpacing(20);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setPadding(new Insets(10));
        hBoxTop.getChildren().addAll(btnPrev, lblSum, btnNext);

        vBoxAllDownloads.getChildren().addAll(hBoxTop);
        vBox.getChildren().add(vBoxAllDownloads);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        int row = 0;
        gridPane.add(lblFilm, 0, row);
        gridPane.add(lblFilmTitle, 1, row);
        lblAll.setMinHeight(Region.USE_PREF_SIZE);

        gridPane.add(lblAll, 3, row);
        // Set
        gridPane.add(lblSet, 0, ++row);
        cboSet.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(cboSet, 1, row, 2, 1);
        gridPane.add(chkSetAll, 3, row);

        // Auflösung
        gridPane.add(new Label("Auflösung:"), 0, ++row);
        hBoxSize.setSpacing(20);
        hBoxSize.setPadding(new Insets(10, 5, 10, 5));
        hBoxSize.getChildren().addAll(rbHd, rbHigh, rbSmall);
        gridPane.add(hBoxSize, 1, row, 2, 1);
        gridPane.add(chkSizeAll, 3, row);

        // Dateiname
        gridPane.add(new Label("Dateiname:"), 0, ++row);
        gridPane.add(txtName, 1, row, 2, 1);

        // Pfad
        gridPane.add(new Label("Zielpfad:"), 0, ++row);
        cboPath.setMaxWidth(Double.MAX_VALUE);
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.getChildren().addAll(btnDest, btnPropose, btnClean);

        gridPane.add(cboPath, 1, row);
        gridPane.add(hBox, 2, row);
        gridPane.add(chkPathAll, 3, row);

        HBox hBox2 = new HBox();
        hBox2.getChildren().add(lblFree);
        hBox2.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(hBox2, 1, ++row, 2, 1);

        // Subtitle
        gridPane.add(cbxSubtitle, 1, ++row);
        gridPane.add(chkSubTitleAll, 3, row);

        // Info
        gridPane.add(cbxInfo, 1, ++row);
        gridPane.add(chkInfoAll, 3, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSizeCenter());
        vBox.getChildren().add(gridPane);
    }

    public void init(ProgData progData, int size) {
        if (progData.setDataList.getSetDataListSave().size() == 1) {
            // macht dann keinen Sinn
            lblSet.setVisible(false);
            lblSet.setManaged(false);
            cboSet.setVisible(false);
            cboSet.setManaged(false);
            chkSetAll.setVisible(false);
            chkSetAll.setManaged(false);
        }

        if (size == 1) {
            // dann ist es nur ein Download
            hBoxTop.setVisible(false);
            hBoxTop.setManaged(false);
            lblAll.setVisible(false);
            lblAll.setManaged(false);

            chkSetAll.setVisible(false);
            chkSetAll.setManaged(false);
            chkSizeAll.setVisible(false);
            chkSizeAll.setManaged(false);
            chkPathAll.setVisible(false);
            chkPathAll.setManaged(false);
            chkSubTitleAll.setVisible(false);
            chkSubTitleAll.setManaged(false);
            chkInfoAll.setVisible(false);
            chkInfoAll.setManaged(false);
        }
    }
}
