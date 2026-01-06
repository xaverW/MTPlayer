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

package de.p2tools.mtplayer.controller.tips;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TipsDialog extends P2DialogExtra {

    public static boolean TIPS_DIALOG_OPEN = false; // Dialog ist schon geöffnet

    private final ProgData progData;
    private final HBox hBoxAll = new HBox(P2LibConst.SPACING_HBOX);
    private final VBox vBoxButton = new VBox(P2LibConst.SPACING_VBOX);
    private final TipBox tipBox = new TipBox();
    private final Button btnFirst;
    private final Button btnLast;
    private final Button btnNext;
    private final Button btnPrev;
    private final Label lblTipsName = new Label();
    private final CheckBox chkShow = new CheckBox("Hilfe beim Programmstart anzeigen");


    private final Button btnInfo = new Button(TipsFactory.TIPPS.INFOS.getName());
    private final Button btnGui = new Button(TipsFactory.TIPPS.GUI.getName());
    private final Button btnFilm = new Button(TipsFactory.TIPPS.FILME.getName());
    private final Button btnDownload = new Button(TipsFactory.TIPPS.DOWNLOAD.getName());
    private final Button btnAbo = new Button(TipsFactory.TIPPS.ABO.getName());
    private final Button btnFilter = new Button(TipsFactory.TIPPS.FILTER.getName());
    private final Button btnSet = new Button(TipsFactory.TIPPS.SET.getName());


    private TipsFactory.TIPPS tips = TipsFactory.TIPPS.INFOS;
    private int actTipNo = 0;

    public TipsDialog(ProgData progData) {
        super(progData.primaryStage, ProgConfig.TIPS_DIALOG_SIZE, "Tipps",
                false, true, false, DECO.BORDER_VERY_SMALL);
        this.progData = progData;
        TIPS_DIALOG_OPEN = true;

        this.btnFirst = P2Button.getButton(PIconFactory.PICON.BTN_TIP_FIRST.getFontIcon(), "ErsteSeite");
        this.btnLast = P2Button.getButton(PIconFactory.PICON.BTN_TIP_LAST.getFontIcon(), "Letzte Seite");
        this.btnNext = P2Button.getButton(PIconFactory.PICON.BTN_TIP_NEXT.getFontIcon(), "Nächste Seite");
        this.btnPrev = P2Button.getButton(PIconFactory.PICON.BTN_TIP_PREV.getFontIcon(), "Vorherige Seite");

        tipBox.getStyleClass().add("tipsTipBox");

        initButton();
        TipData to = tips.getTipsList().get(actTipNo);
        tipBox.setTips(to);
        init(true);
    }

    @Override
    public void make() {
        HBox hBoxTop = new HBox(P2LibConst.SPACING_HBOX);
        hBoxTop.getStyleClass().add("tipsInfoTop");
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.getChildren().addAll(P2GuiTools.getHBoxGrower(), lblTipsName,
                P2GuiTools.getHBoxGrower());
        getVBoxCont().getChildren().add(hBoxTop);

        hBoxAll.getChildren().addAll(vBoxButton, tipBox);
        HBox.setHgrow(tipBox, Priority.ALWAYS);
        HBox.setHgrow(vBoxButton, Priority.NEVER);
        VBox.setVgrow(hBoxAll, Priority.ALWAYS);

        getVBoxCont().getChildren().add(hBoxAll);

        chkShow.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SHOW_TIPS);
        getHboxLeft().getChildren().add(chkShow);
        getHboxLeft().setAlignment(Pos.CENTER_LEFT);

        Button btnOk = new Button("OK");
        final Button btnHelp = PIconFactory.getHelpButton(getStage(), "Hilfe-Dialog",
                "In dem Dialog werden Tipps zum Programm angezeigt. Wenn nicht " +
                        "abgeschaltet, wird der Dialog beim Start angezeigt.");
        addOkButton(btnOk);
        addHlpButton(btnHelp);
        btnOk.setOnAction(a -> {
            close();
        });
    }

    @Override
    public void close() {
        chkShow.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SHOW_TIPS);
        TIPS_DIALOG_OPEN = false;
        super.close();
    }

    private void initButton() {
        lblTipsName.setText(tips.getName());

        setSel(btnInfo);
        btnInfo.getStyleClass().add("pFuncBtn");
        btnInfo.setOnAction(a -> {
            tips = TipsFactory.TIPPS.INFOS;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            TipData to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
            setSel(btnInfo);
        });
        btnInfo.setMaxWidth(Double.MAX_VALUE);

        btnGui.getStyleClass().add("pFuncBtn");
        btnGui.setOnAction(a -> {
            tips = TipsFactory.TIPPS.GUI;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            TipData to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
            setSel(btnGui);
        });
        btnGui.setMaxWidth(Double.MAX_VALUE);

        btnFilm.getStyleClass().add("pFuncBtn");
        btnFilm.setOnAction(a -> {
            tips = TipsFactory.TIPPS.FILME;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            TipData to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
            setSel(btnFilm);
        });
        btnFilm.setMaxWidth(Double.MAX_VALUE);

        btnDownload.getStyleClass().add("pFuncBtn");
        btnDownload.setOnAction(a -> {
            tips = TipsFactory.TIPPS.DOWNLOAD;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            TipData to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
            setSel(btnDownload);
        });
        btnDownload.setMaxWidth(Double.MAX_VALUE);

        btnAbo.getStyleClass().add("pFuncBtn");
        btnAbo.setOnAction(a -> {
            tips = TipsFactory.TIPPS.ABO;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            TipData to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
            setSel(btnAbo);
        });
        btnAbo.setMaxWidth(Double.MAX_VALUE);

        btnFilter.getStyleClass().add("pFuncBtn");
        btnFilter.setOnAction(a -> {
            tips = TipsFactory.TIPPS.FILTER;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            TipData to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
            setSel(btnFilter);
        });
        btnFilter.setMaxWidth(Double.MAX_VALUE);

        btnSet.getStyleClass().add("pFuncBtn");
        btnSet.setOnAction(a -> {
            tips = TipsFactory.TIPPS.SET;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            TipData to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
            setSel(btnSet);
        });
        btnSet.setMaxWidth(Double.MAX_VALUE);


        GridPane gridPane1 = new GridPane();
        gridPane1.setVgap(10);
        gridPane1.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow());
//        gridPane1.setGridLinesVisible(true);

        int row = 0;
        gridPane1.add(btnInfo, 0, row, 2, 1);
        gridPane1.add(btnGui, 0, ++row, 2, 1);
        gridPane1.add(btnFilm, 0, ++row, 2, 1);
        gridPane1.add(btnDownload, 0, ++row, 2, 1);
        gridPane1.add(btnAbo, 0, ++row, 2, 1);
        gridPane1.add(btnFilter, 0, ++row, 2, 1);
        gridPane1.add(btnSet, 0, ++row, 2, 1);


        btnPrev.getStyleClass().add("btnTipsNext");
        btnNext.getStyleClass().add("btnTipsNext");
        btnFirst.getStyleClass().add("btnTipsNext");
        btnLast.getStyleClass().add("btnTipsNext");

        GridPane gridPane2 = new GridPane();
        gridPane2.getStyleClass().add("tipsButtonBoxTop");
        gridPane2.setHgap(2);
        gridPane2.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize());

        gridPane2.add(btnFirst, 0, 0);
        gridPane2.add(btnPrev, 1, 0);
        gridPane2.add(btnNext, 2, 0);
        gridPane2.add(btnLast, 3, 0);


        vBoxButton.getChildren().addAll(gridPane2, P2GuiTools.getHDistance(10), gridPane1);
        HBox.setHgrow(vBoxButton, Priority.ALWAYS);

        btnFirst.setOnAction(a -> {
            actTipNo = 0;
            tipBox.setTips(tips.getTipsList().get(actTipNo));
            setName();
        });
        btnLast.setOnAction(a -> {
            actTipNo = tips.getTipsList().size() - 1;
            tipBox.setTips(tips.getTipsList().get(actTipNo));
            setName();
        });
        btnNext.setOnAction(a -> selectActToolTip(true));
        btnPrev.setOnAction(a -> selectActToolTip(false));
    }

    private void setSel(Button btn) {
        btnInfo.getStyleClass().remove("btnTipsDialogSel");
        btnGui.getStyleClass().remove("btnTipsDialogSel");
        btnFilm.getStyleClass().remove("btnTipsDialogSel");
        btnDownload.getStyleClass().remove("btnTipsDialogSel");
        btnAbo.getStyleClass().remove("btnTipsDialogSel");
        btnFilter.getStyleClass().remove("btnTipsDialogSel");
        btnSet.getStyleClass().remove("btnTipsDialogSel");

        btn.getStyleClass().add("btnTipsDialogSel");
    }

    private void selectActToolTip(boolean next) {
        if (next) {
            //next
            if (actTipNo < tips.getTipsList().size() - 1) {
                ++actTipNo;
            } else {
                actTipNo = 0;
            }

        } else {
            //!next
            if ((actTipNo > 0)) {
                --actTipNo;
            } else {
                actTipNo = tips.getTipsList().size() - 1;
            }
        }

        TipData to = tips.getTipsList().get(actTipNo);
        tipBox.setTips(to);
        setName();
    }

    private void setName() {
        lblTipsName.setText(tips.getName() +
                (actTipNo == 0 ? "" : "  -  " + (actTipNo + 1) + " von " + tips.getTipsList().size()));

    }
}
