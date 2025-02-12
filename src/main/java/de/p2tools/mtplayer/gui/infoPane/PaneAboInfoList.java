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

package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PaneAboInfoList extends VBox {

    private final VBox vBoxHeader = new VBox();
    private final GridPane gridPane = new GridPane();
    private final ProgData progData;

    public PaneAboInfoList() {
        progData = ProgData.getInstance();
        VBox.setVgrow(this, Priority.ALWAYS);

        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getStyleClass().add("downloadInfoGrid");
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSizeRight(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());

        VBox vBoxAll = new VBox();
        vBoxAll.setSpacing(10);
        vBoxAll.setPadding(new Insets(10));
        vBoxAll.getChildren().addAll(vBoxHeader, gridPane);
        getChildren().add(vBoxAll);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        progData.aboList.listChangedProperty().addListener((u, o, n) -> setInfoText());
        PListener.addListener(new PListener(PListener.EVENT_ABO_HIT_CHANGED, PaneAboInfoList.class.getSimpleName()) {
            @Override
            public void pingFx() {
                setInfoText();
            }
        });

        setInfoText();
    }

    public void setInfoText() {
        vBoxHeader.getChildren().clear();
        gridPane.getChildren().clear();

        if (progData.aboList.isEmpty()) {
            // dann gibts keine :)
            Text text1 = new Text("Keine Abos");
            text1.setFont(Font.font(null, FontWeight.BOLD, -1));
            text1.getStyleClass().add("downloadGuiMediaText");
            vBoxHeader.getChildren().add(text1);
            gridPane.setVisible(false);
            return;
        }

        int aboActive = 0, aboInactive = 0, aboActiveHits = 0, aboCountNoHits = 0;
        for (AboData aboData : ProgData.getInstance().aboList) {
            if (aboData.isActive()) {
                ++aboActive;
                aboActiveHits += aboData.getHit();
                if (aboData.getHit() == 0) {
                    ++aboCountNoHits;
                }

            } else {
                ++aboInactive;
            }
        }

        gridPane.setVisible(true);
        int row = 0;

        Text text = new Text("Aktive Abos:");
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(text, 0, row);
        gridPane.add(new Label(aboActive + ""), 1, row);
        gridPane.add(new Label(" "), 2, row);

        text = new Text("Treffer:");
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(new Label("( Treffer:"), 3, row);
        gridPane.add(new Label(aboActiveHits + " )"), 4, row);

        ++row;
        text = new Text("Aktive Abos ohne Treffer:");
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(text, 0, ++row);
        gridPane.add(new Label(aboCountNoHits + ""), 1, row);

        ++row;
        text = new Text("Inaktive Abos:");
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
        text.getStyleClass().add("downloadGuiMediaText");
        gridPane.add(text, 0, ++row);
        gridPane.add(new Label(aboInactive + ""), 1, row);
    }
}
