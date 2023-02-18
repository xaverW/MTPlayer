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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.P2LibInit;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import de.p2tools.p2Lib.tools.IoReadWriteStyle;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneStyle {

    private final PToggleSwitch tglStyle = new PToggleSwitch("die Schriftgröße im Programm ändern:");
    private Spinner<Integer> spinnerAnz = new Spinner<>();
    boolean changed = false;

    private final Stage stage;
    private final ProgData progData;

    public PaneStyle(Stage stage, ProgData progData) {
        this.stage = stage;
        this.progData = progData;
    }

    public void close() {
        tglStyle.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_STYLE);
        int size = spinnerAnz.getValue();
        ProgConfig.SYSTEM_STYLE_SIZE.setValue(size);

        if (changed) {
            if (ProgConfig.SYSTEM_STYLE.get()) {
                IoReadWriteStyle.writeStyle(ProgInfos.getStyleFile(), size);
                P2LibInit.setStyleFile(ProgInfos.getStyleFile().toString());
                PLog.sysLog("Schriftgröße ändern: " + size);
            } else {
                IoReadWriteStyle.writeStyle(ProgInfos.getStyleFile(), -1);
                P2LibInit.setStyleFile("");
                PLog.sysLog("Schriftgröße nicht mehr ändern.");
            }
            IoReadWriteStyle.readStyle(ProgInfos.getStyleFile(), progData.primaryStage.getScene());
        }
    }

    public TitledPane makeStyle(Collection<TitledPane> result) {
        tglStyle.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STYLE);
        final Button btnHelpStyle = PButton.helpButton(stage, "Schriftgröße anpassen", HelpText.CONFIG_STYLE);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        int row = 0;
        gridPane.add(tglStyle, 0, row, 2, 1);
        gridPane.add(btnHelpStyle, 2, row);

        gridPane.add(new Label(" "), 0, ++row);

        // eigener Standort angeben
        Label lbl = new Label("Schriftgröße:");
        gridPane.add(lbl, 0, ++row);
        gridPane.add(spinnerAnz, 1, row);

        lbl.disableProperty().bind(tglStyle.selectedProperty().not());
        spinnerAnz.disableProperty().bind(tglStyle.selectedProperty().not());

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        TitledPane tpConfig = new TitledPane("Schriftgröße", gridPane);
        if (result != null) {
            result.add(tpConfig);
        }
        init();
        return tpConfig;
    }

    private void init() {
        spinnerAnz.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 30, 1));
        spinnerAnz.getValueFactory().setValue(ProgConfig.SYSTEM_STYLE_SIZE.getValue());
        spinnerAnz.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                changed = true;
            }
        });
        tglStyle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                changed = true;
            }
        });
    }
}
