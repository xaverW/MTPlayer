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

package de.p2tools.mtplayer.gui.configDialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.update.SearchProgramUpdate;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneUpdate {

    private final PToggleSwitch tglSearch = new PToggleSwitch("Einmal am Tag nach einer neuen Programmversion suchen");
    private final PToggleSwitch tglSearchBeta = new PToggleSwitch("Auch nach neuen Vorabversionen suchen");
    private final CheckBox chkDaily = new CheckBox("Zwischenschritte (Dailys) mit einbeziehen");
    private final Button btnNow = new Button("_Jetzt suchen");
    private Button btnHelpBeta;
    private final PToggleSwitch tglEnableLog = new PToggleSwitch("Ein Logfile anlegen:");
    private final Stage stage;
    private final ProgData progData;

    public PaneUpdate(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();
    }

    public void close() {
        tglEnableLog.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOG_ON);
        tglSearch.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_ACT);
        tglSearchBeta.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_BETA);
        chkDaily.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_DAILY);
    }

    public void makeUpdate(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        TitledPane tpConfig = new TitledPane("Programmupdate", gridPane);
        result.add(tpConfig);

        //einmal am Tag Update suchen
        tglSearch.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_ACT);
        final Button btnHelp = PButton.helpButton(stage, "Programmupdate suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Version des Programms gibt. " +
                        "Ist eine aktualisierte Version vorhanden, dann wird das gemeldet."
                        + P2LibConst.LINE_SEPARATOR +
                        "Das Programm wird aber nicht ungefragt ersetzt.");

        tglSearchBeta.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_BETA);
        chkDaily.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_DAILY);
        btnHelpBeta = PButton.helpButton(stage, "Vorabversionen suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Vorabversion des Programms gibt. " +
                        P2LibConst.LINE_SEPARATORx2 +
                        "Das sind \"Zwischenschritte\" auf dem Weg zur nächsten Version. Hier ist die " +
                        "Entwicklung noch nicht abgeschlossen und das Programm kann noch Fehler enthalten. Wer Lust hat " +
                        "einen Blick auf die nächste Version zu werfen, ist eingeladen, die Vorabversionen zu testen." +
                        P2LibConst.LINE_SEPARATORx2 +
                        "Ist eine aktualisierte Vorabversion vorhanden, dann wird das gemeldet."
                        + P2LibConst.LINE_SEPARATOR +
                        "Das Programm wird aber nicht ungefragt ersetzt.");

        //jetzt suchen
        checkBeta();
        tglSearch.selectedProperty().addListener((ob, ol, ne) -> checkBeta());
        tglSearchBeta.selectedProperty().addListener((ob, ol, ne) -> checkBeta());

        btnNow.setOnAction(event -> new SearchProgramUpdate(progData, stage).searchNewProgramVersion(true));
        PHyperlink hyperlink = new PHyperlink(ProgConst.URL_WEBSITE,
                ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        HBox hBoxHyper = new HBox();
        hBoxHyper.setAlignment(Pos.CENTER_LEFT);
        hBoxHyper.setPadding(new Insets(10, 0, 0, 0));
        hBoxHyper.setSpacing(10);
        hBoxHyper.getChildren().addAll(new Label("Infos auch auf der Website:"), hyperlink);

        int row = 0;
        gridPane.add(tglSearch, 0, row);
        gridPane.add(btnHelp, 1, row);

        gridPane.add(tglSearchBeta, 0, ++row);
        gridPane.add(btnHelpBeta, 1, row);
        gridPane.add(chkDaily, 0, ++row, 2, 1);
        GridPane.setHalignment(chkDaily, HPos.RIGHT);

        gridPane.add(btnNow, 0, ++row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(hBoxHyper, 0, ++row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
        gridPane.getRowConstraints().addAll(PColumnConstraints.getRcPrefSize(), PColumnConstraints.getRcPrefSize(),
                PColumnConstraints.getRcPrefSize(), PColumnConstraints.getRcVgrow(), PColumnConstraints.getRcPrefSize());
    }

    private void checkBeta() {
        tglSearchBeta.setDisable(!tglSearch.isSelected());
        btnHelpBeta.setDisable(!tglSearch.isSelected());

        if (!tglSearchBeta.isSelected()) {
            chkDaily.setSelected(false);
        }
        chkDaily.setDisable(!tglSearchBeta.isSelected() || tglSearchBeta.isDisabled());
    }
}
