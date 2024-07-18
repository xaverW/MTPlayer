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
import de.p2tools.mtplayer.controller.config.ProgConfigAskBeforeDelete;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneAbo {

    private final P2ToggleSwitch tglSearchAbo = new P2ToggleSwitch("Downloads aus Abos automatisch suchen:");
    private final P2ToggleSwitch tglStartDownload = new P2ToggleSwitch("Nach dem Suchen Downloads sofort starten:");
    private final ToggleGroup groupOnlyStop = new ToggleGroup();
    private final RadioButton rbOnlyStopAsk = new RadioButton("Vor dem Löschen fragen");
    private final RadioButton rbOnlyStopDelete = new RadioButton("Abo sofort löschen ohne zu fragen");
    private final Stage stage;
    private final VBox vBoxAll = new VBox();

    public PaneAbo(Stage stage) {
        this.stage = stage;
        initRadio();
    }

    public void close() {
        tglSearchAbo.selectedProperty().unbindBidirectional(ProgConfig.ABO_SEARCH_NOW);
        tglStartDownload.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_START_NOW);
    }

    public void makeAbo(Collection<TitledPane> result) {
        TitledPane tpConfig = new TitledPane("Abo", vBoxAll);
        result.add(tpConfig);
        vBoxAll.setSpacing(P2LibConst.PADDING_VBOX);
        addGridPane();
    }

    private void addGridPane() {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        vBoxAll.getChildren().add(gridPane);

        tglSearchAbo.selectedProperty().bindBidirectional(ProgConfig.ABO_SEARCH_NOW);
        final Button btnHelpAbo = P2Button.helpButton(stage, "Downloads aus Abos automatisch suchen",
                HelpText.SEARCH_ABOS_IMMEDIATELY);

        tglStartDownload.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_START_NOW);
        final Button btnHelpDownload = P2Button.helpButton(stage, "Nach dem Suchen Downloads sofort starten",
                HelpText.START_DOWNLOADS_FROM_ABOS_IMMEDIATELY);

        final Button btnHelpStop = P2Button.helpButton(stage, "Abos löschen",
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

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());
    }

    private void initRadio() {
        setRadio();
        rbOnlyStopAsk.setToggleGroup(groupOnlyStop);
        rbOnlyStopDelete.setToggleGroup(groupOnlyStop);

        ProgConfig.ABO_ONLY_STOP.addListener((v, o, n) -> setRadio());
        rbOnlyStopAsk.setOnAction(a -> ProgConfig.ABO_ONLY_STOP.setValue(ProgConfigAskBeforeDelete.ABO_DELETE__ASK));
        rbOnlyStopDelete.setOnAction(a -> ProgConfig.ABO_ONLY_STOP.setValue(ProgConfigAskBeforeDelete.ABO_DELETE__DELETE));
    }

    private void setRadio() {
        switch (ProgConfig.ABO_ONLY_STOP.getValue()) {
            case ProgConfigAskBeforeDelete.ABO_DELETE__DELETE:
                rbOnlyStopDelete.setSelected(true);
                break;
            case ProgConfigAskBeforeDelete.ABO_DELETE__ASK:
            default:
                rbOnlyStopAsk.setSelected(true);
                break;
        }
    }
}