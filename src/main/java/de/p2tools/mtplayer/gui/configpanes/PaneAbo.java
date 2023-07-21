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

package de.p2tools.mtplayer.gui.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.starter.AskBeforeDeleteState;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneAbo {

    private final PToggleSwitch tglSearchAbo = new PToggleSwitch("Abos automatisch suchen:");
    private final PToggleSwitch tglStartDownload = new PToggleSwitch("Downloads aus Abos sofort starten:");
    private final ToggleGroup groupOnlyStop = new ToggleGroup();
    private final RadioButton rbOnlyStopAsk = new RadioButton("Vor dem Löschen fragen");
    private final RadioButton rbOnlyStopDelete = new RadioButton("Abo sofort löschen ohne zu fragen");
    private final Stage stage;

    public PaneAbo(Stage stage) {
        this.stage = stage;
        initRadio();
    }

    public void close() {
        tglSearchAbo.selectedProperty().unbindBidirectional(ProgConfig.ABO_SEARCH_NOW);
        tglStartDownload.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_START_NOW);
    }

    public void makeAbo(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        TitledPane tpConfig = new TitledPane("Abo", gridPane);
        result.add(tpConfig);

        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        tglSearchAbo.selectedProperty().bindBidirectional(ProgConfig.ABO_SEARCH_NOW);
        final Button btnHelpAbo = PButton.helpButton(stage, "Abos automatisch suchen",
                HelpText.SEARCH_ABOS_IMMEDIATELY);

        tglStartDownload.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_START_NOW);
        final Button btnHelpDownload = PButton.helpButton(stage, "Downloads sofort starten",
                HelpText.START_DOWNLOADS_FROM_ABOS_IMMEDIATELY);

        final Button btnHelpStop = PButton.helpButton(stage, "Abos löschen",
                HelpText.ABO_DELETE_CONFIG);
        GridPane.setValignment(btnHelpStop, VPos.TOP);

        int row = 0;
        gridPane.add(tglSearchAbo, 0, row);
        gridPane.add(btnHelpAbo, 1, row);

        gridPane.add(tglStartDownload, 0, ++row);
        gridPane.add(btnHelpDownload, 1, row);

        gridPane.add(new Label(""), 0, ++row);
        gridPane.add(new Label("Abos löschen"), 0, ++row);
        gridPane.add(btnHelpStop, 1, row, 1, 2);

        final String LEER = "     ";
        VBox vBox = new VBox(5);

        HBox hBox = new HBox(20);
        hBox.getChildren().addAll(new Label(LEER), rbOnlyStopAsk);
        vBox.getChildren().addAll(hBox);

        hBox = new HBox(20);
        hBox.getChildren().addAll(new Label(LEER), rbOnlyStopDelete);
        vBox.getChildren().addAll(hBox);

        ++row;
        gridPane.add(vBox, 0, ++row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
    }

    private void initRadio() {
        setRadio();
        rbOnlyStopAsk.setToggleGroup(groupOnlyStop);
        rbOnlyStopDelete.setToggleGroup(groupOnlyStop);

        ProgConfig.ABO_ONLY_STOP.addListener((v, o, n) -> setRadio());
        rbOnlyStopAsk.setOnAction(a -> ProgConfig.ABO_ONLY_STOP.setValue(AskBeforeDeleteState.ABO_DELETE__ASK));
        rbOnlyStopDelete.setOnAction(a -> ProgConfig.ABO_ONLY_STOP.setValue(AskBeforeDeleteState.ABO_DELETE__DELETE));
    }

    private void setRadio() {
        switch (ProgConfig.ABO_ONLY_STOP.getValue()) {
            case AskBeforeDeleteState.ABO_DELETE__DELETE:
                rbOnlyStopDelete.setSelected(true);
                break;
            case AskBeforeDeleteState.ABO_DELETE__ASK:
            default:
                rbOnlyStopAsk.setSelected(true);
                break;
        }
    }
}