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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class FeatureTabDialog extends P2DialogExtra {

    public FeatureTabDialog() {
        super(new SimpleStringProperty("450:350")/*wird nur einmal aufgerufen*/, "Filter löschen");
        init(true);
    }

    @Override
    public void make() {

        final GridPane gridPane1 = new GridPane();
        gridPane1.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane1.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane1.setPadding(new Insets(P2LibConst.PADDING));
        gridPane1.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow());

        P2Text.getLblTextBold("Tab klicken");
        Label lblText = new Label();
        lblText.setWrapText(true);
        lblText.setText("""
                Mit dem ersten Klick auf einen  >>Tab<<   wird er ausgewählt. \
                Jeder weitre Klick blendet die Spalte mit den Filtern neben der Tabelle ein/aus
                
                Ein Klick mit der "RECHTEN Maustaste" blendet das Infopanel unter der \
                Tabelle ein/aus.
                
                Dieses Feature kann auch in den Einstellungen ein/ausgeschaltet werden.
                
                Soll dieses Feature benutzt werden?""");
        getVBoxCont().getChildren().add(lblText);


        Button btnUse = new Button("Verwenden");
        Button btnDontUse = new Button("Nicht verwenden");
        btnUse.setOnAction(a -> {
            ProgConfig.SYSTEM_TAB_SECOND_KLICK.set(true);
            ProgConfig.SYSTEM_TAB_SECOND_KLICK_ASK.set(false);
            close();
        });

        btnDontUse.setOnAction(a -> {
            ProgConfig.SYSTEM_TAB_SECOND_KLICK.set(false);
            ProgConfig.SYSTEM_TAB_SECOND_KLICK_ASK.set(false);
            close();
        });
        addOkCancelButtons(btnDontUse, btnUse);
    }
}
