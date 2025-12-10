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

package de.p2tools.mtplayer.gui.configdialog.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneStatusBar {

    private final Stage stage;
    private final P2ToggleSwitch tglOn = new P2ToggleSwitch("Statusleiste anzeigen");
    private final P2ToggleSwitch tglSelOn = new P2ToggleSwitch("Anzeige der Anzahl der markierten Zeilen");
    private final P2ToggleSwitch tglLeftOn = new P2ToggleSwitch("Anzeige des Infobereichs links");
    private final P2ToggleSwitch tglDotOn = new P2ToggleSwitch("Anzeige eines Farbpunktes für den Zustand der Downloads");
    private final P2ToggleSwitch tglRightOn = new P2ToggleSwitch("Anzeige der Infos über die Filmliste");
    private final GridPane gridPane = new GridPane();


    public PaneStatusBar(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglOn.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_ON);
        tglSelOn.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        tglLeftOn.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        tglDotOn.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);
        tglRightOn.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);
    }

    public void make(Collection<TitledPane> result) {
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        TitledPane tpConfig = new TitledPane("Statusleiste", gridPane);
        result.add(tpConfig);
        make();
    }

    private void make() {
        tglOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_ON);
        tglSelOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        tglLeftOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        tglDotOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);
        tglRightOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);

        final Button btnHelp = P2Button.helpButton(stage, "Statusleiste anpassen",
                "Hier kann die Statusleiste ein-/ausgeschaltet werden. Die angezeigten " +
                        "Infos können ein-/ausgeschaltet werden." +
                        "\n\n" +
                        "Der Farbpunkt zeigt den Zustand der Downloads an:\n" +
                        "  Der Farbpunkt blinkt: Es sind noch nicht gestartete Downloads vorhanden\n" +
                        "  Der Farbpunkt ist grün: Es sind fertige Downloads vorhanden\n" +
                        "  Der Farbpunkt ist rot: Es sind Downloads mit Fehler vorhanden\n" +
                        "  Der Farbpunkt ist grau: Es sind keine fertigen oder fehlerhaften Downloads vorhanden");


        int row = 0;
        gridPane.add(tglOn, 0, row);
        gridPane.add(btnHelp, 1, row);

        ++row;
        gridPane.add(tglSelOn, 0, ++row);
        gridPane.add(tglLeftOn, 0, ++row);
        gridPane.add(tglDotOn, 0, ++row);
        gridPane.add(tglRightOn, 0, ++row);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow(),
                P2GridConstraints.getCcPrefSize());
    }
}
