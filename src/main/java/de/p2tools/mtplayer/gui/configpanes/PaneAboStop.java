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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneAboStop {

    private final ToggleGroup groupOnlyStop = new ToggleGroup();
    private final RadioButton rbOnlyStopAsk = new RadioButton("Vor dem Löschen fragen");
    private final RadioButton rbOnlyStopDelete = new RadioButton("Abo sofort löschen ohne zu fragen");

    private final Stage stage;

    public PaneAboStop(Stage stage) {
        this.stage = stage;
        initRadio();
    }

    public void close() {
    }

    public void makeAbo(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        TitledPane tpConfig = new TitledPane("Abo stoppen", gridPane);
        result.add(tpConfig);

        final Button btnHelpStop = PButton.helpButton(stage, "Abo",
                HelpText.ABO_DELETE_CONFIG);

        GridPane.setHalignment(btnHelpStop, HPos.RIGHT);

        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        Text text = new Text("Abos löschen");
        text.setFont(Font.font(null, FontWeight.BOLD, -1));
        text.getStyleClass().add("downloadGuiMediaText");

        int row = 0;
        gridPane.add(text, 0, row);
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