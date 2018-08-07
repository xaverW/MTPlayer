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

package de.mtplayer.mtp.gui.startDialog;


import de.mtplayer.mtp.controller.config.ProgConfig;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;

public class UpdatePane {

    BooleanProperty updateProp = ProgConfig.SYSTEM_UPDATE_SEARCH.getBooleanProperty();

    public TitledPane makeStart() {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Erster Programmstart", gridPane);

        //einmal am Tag Update suchen
        final PToggleSwitch tglSearch = new PToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");
        tglSearch.selectedProperty().bindBidirectional(updateProp);
        gridPane.add(tglSearch, 0, 0);

        final Button btnHelp = new PButton().helpButton(StartDialogController.stage, "Programmupdate suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Version des Programms gibt. Wenn es " +
                        "eine neue Version gibt, wird das mit einer Nachricht mitgeteilt. Es wird nicht " +
                        "automatisch das Programm verändert.");
        gridPane.add(btnHelp, 1, 0);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

        return tpConfig;
    }

}
