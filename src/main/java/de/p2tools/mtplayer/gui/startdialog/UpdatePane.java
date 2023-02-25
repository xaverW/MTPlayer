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

package de.p2tools.mtplayer.gui.startdialog;


import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UpdatePane {
    private final Stage stage;
    private final PToggleSwitch tglSearch = new PToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");
    BooleanProperty updateProp = ProgConfig.SYSTEM_UPDATE_SEARCH_ACT;

    public UpdatePane(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglSearch.selectedProperty().unbindBidirectional(updateProp);
    }

    public TitledPane makeStart() {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        //einmal am Tag Update suchen
        tglSearch.selectedProperty().bindBidirectional(updateProp);


        final Button btnHelp = PButton.helpButton(stage, "Programmupdate suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Version des Programms gibt. Wenn es " +
                        "eine neue Version gibt, wird das mit einer Nachricht mitgeteilt. Es wird nicht " +
                        "automatisch das Programm verändert.");

        gridPane.add(tglSearch, 0, 0);
        gridPane.add(btnHelp, 1, 0);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

        TitledPane tpConfig = new TitledPane("Programmupdate", gridPane);
        return tpConfig;
    }
}
