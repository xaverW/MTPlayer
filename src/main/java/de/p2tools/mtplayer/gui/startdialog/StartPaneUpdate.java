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
import de.p2tools.p2lib.dialogs.dialog.P2Dialog;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class StartPaneUpdate {
    private final P2Dialog pDialog;
    private final P2ToggleSwitch tglSearch = new P2ToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");

    public StartPaneUpdate(P2Dialog pDialog) {
        this.pDialog = pDialog;
    }

    public void close() {
        tglSearch.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SEARCH_UPDATE);
    }

    public TitledPane makeStart() {
        VBox vBox = new VBox(10);

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Das Programm kann einmal täglich nach einem Update suchen. " +
                "Gibt es eins, wird darüber informiert und es kann geladen werden. " +
                "Es wird nichts automatisch geändert.");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(5), hBox, P2GuiTools.getVDistance(20));

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        //einmal am Tag Update suchen
        tglSearch.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SEARCH_UPDATE);

        final Button btnHelp = P2Button.helpButton(pDialog.getStage(), "Programmupdate suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Version des Programms gibt. Wenn es " +
                        "eine neue Version gibt, wird das mit einer Nachricht mitgeteilt. Es wird nicht " +
                        "automatisch das Programm verändert.");

        gridPane.add(tglSearch, 0, 0);
        gridPane.add(btnHelp, 1, 0);
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().add(gridPane);

        return new TitledPane("Programmupdate", vBox);
    }
}
