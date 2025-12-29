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
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TipsDialog extends P2DialogExtra {

    private final ProgData progData;
    private final HBox hBoxAll = new HBox(P2LibConst.SPACING_HBOX);
    private final VBox vBoxButton = new VBox(P2LibConst.SPACING_VBOX);
    private final VBox vBoxTipps = new VBox(P2LibConst.SPACING_VBOX);
    private final TipBox tipBox = new TipBox();
    private final Button btnFirst;
    private final Button btnLast;
    private final Button btnNext;
    private final Button btnPrev;
    private final Label lblTipsName = new Label();

    private TipsFactory.TIPPS tips = TipsFactory.TIPPS.INFOS;
    private int actTipNo = 0;

    public TipsDialog(ProgData progData) {
        super(progData.primaryStage, ProgConfig.TIPPS_DIALOG_SIZE, "Tipps",
                true, true, false, DECO.BORDER_VERY_SMALL);
        this.progData = progData;

        this.btnFirst = P2Button.getButton(PIconFactory.PICON.BTN_TIP_FIRST.getFontIcon(), "ErsteSeite");
        this.btnLast = P2Button.getButton(PIconFactory.PICON.BTN_TIP_LAST.getFontIcon(), "Letzte Seite");
        this.btnNext = P2Button.getButton(PIconFactory.PICON.BTN_TIP_NEXT.getFontIcon(), "Nächste Seite");
        this.btnPrev = P2Button.getButton(PIconFactory.PICON.BTN_TIP_PREV.getFontIcon(), "Vorherige Seite");

        initButton();
        PTipOfDay to = tips.getTipsList().get(actTipNo);
        tipBox.setTips(to);
        init(true);
    }

    @Override
    public void make() {
        HBox hBoxTop = new HBox(P2LibConst.SPACING_HBOX);
        hBoxTop.getStyleClass().add("startInfoTop");
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.getChildren().addAll(P2GuiTools.getHBoxGrower(), lblTipsName,
                P2GuiTools.getHBoxGrower());
        getVBoxCont().getChildren().add(hBoxTop);

        hBoxAll.getChildren().addAll(vBoxButton, tipBox);
        HBox.setHgrow(tipBox, Priority.ALWAYS);
        HBox.setHgrow(vBoxButton, Priority.NEVER);
        VBox.setVgrow(hBoxAll, Priority.ALWAYS);

        getVBoxCont().getChildren().add(hBoxAll);
    }

    private void initButton() {
        lblTipsName.setText(tips.getName());
        Button btnInfo = new Button(TipsFactory.TIPPS.INFOS.getName());
        btnInfo.getStyleClass().add("btnTipsDialog");
        btnInfo.setOnAction(a -> {
            tips = TipsFactory.TIPPS.INFOS;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            PTipOfDay to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
        });
        btnInfo.setMaxWidth(Double.MAX_VALUE);

        Button btnGui = new Button(TipsFactory.TIPPS.GUI.getName());
        btnGui.getStyleClass().add("btnTipsDialog");
        btnGui.setOnAction(a -> {
            tips = TipsFactory.TIPPS.GUI;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            PTipOfDay to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
        });
        btnGui.setMaxWidth(Double.MAX_VALUE);

        Button btnAbo = new Button(TipsFactory.TIPPS.ABO.getName());
        btnAbo.getStyleClass().add("btnTipsDialog");
        btnAbo.setOnAction(a -> {
            tips = TipsFactory.TIPPS.ABO;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            PTipOfDay to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
        });
        btnAbo.setMaxWidth(Double.MAX_VALUE);

        Button btnFilter = new Button(TipsFactory.TIPPS.FILTER.getName());
        btnFilter.getStyleClass().add("btnTipsDialog");
        btnFilter.setOnAction(a -> {
            tips = TipsFactory.TIPPS.FILTER;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            PTipOfDay to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
        });
        btnFilter.setMaxWidth(Double.MAX_VALUE);

        Button btnAll = new Button(TipsFactory.TIPPS.ALL.getName());
        btnAll.getStyleClass().add("btnTipsDialog");
        btnAll.setOnAction(a -> {
            tips = TipsFactory.TIPPS.ALL;
            lblTipsName.setText(tips.getName());
            actTipNo = 0;
            PTipOfDay to = tips.getTipsList().get(actTipNo);
            tipBox.setTips(to);
        });
        btnAll.setMaxWidth(Double.MAX_VALUE);


        GridPane gridPane1 = new GridPane();
        gridPane1.setVgap(10);
        gridPane1.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow());
//        gridPane1.setGridLinesVisible(true);

        int row = 0;
        gridPane1.add(btnInfo, 0, row, 2, 1);
        gridPane1.add(btnGui, 0, ++row, 2, 1);
        gridPane1.add(btnFilter, 0, ++row, 2, 1);
        gridPane1.add(btnAbo, 0, ++row, 2, 1);
        gridPane1.add(btnAll, 0, ++row, 2, 1);


        btnPrev.getStyleClass().add("btnTipsNext");
        btnNext.getStyleClass().add("btnTipsNext");
        btnFirst.getStyleClass().add("btnTipsNext");
        btnLast.getStyleClass().add("btnTipsNext");

        GridPane gridPane2 = new GridPane();
        gridPane2.setHgap(2);
        gridPane2.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize());
//        gridPane2.setGridLinesVisible(true);

        gridPane2.add(btnFirst, 0, 0);
        gridPane2.add(btnPrev, 1, 0);
        gridPane2.add(btnNext, 2, 0);
        gridPane2.add(btnLast, 3, 0);


        vBoxButton.getChildren().addAll(gridPane2, P2GuiTools.getHDistance(10), gridPane1);
        HBox.setHgrow(vBoxButton, Priority.ALWAYS);

        btnFirst.setOnAction(a -> tipBox.setTips(tips.getTipsList().get(0)));
        btnLast.setOnAction(a -> tipBox.setTips(tips.getTipsList().get(tips.getTipsList().size() - 1)));
        btnNext.setOnAction(a -> selectActToolTip(true));
        btnPrev.setOnAction(a -> selectActToolTip(false));
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

        PTipOfDay to = tips.getTipsList().get(actTipNo);
        tipBox.setTips(to);
    }
}
