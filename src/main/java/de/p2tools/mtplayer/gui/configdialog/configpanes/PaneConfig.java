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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.tools.P2StringUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneConfig {

    private final P2ToggleSwitch tglOnlyOneInstance = new P2ToggleSwitch("Nur eine Instanz des Programms öffnen");
    private final P2ToggleSwitch tglStartMaximised = new P2ToggleSwitch("Programm immer \"Maximiert\" starten");
    private final P2ToggleSwitch tglCheckStart = new P2ToggleSwitch("Einstellungen zum Speichern beim Programmstart prüfen");
    private final P2ToggleSwitch tglTipOfDay = new P2ToggleSwitch("Tip des Tages anzeigen");
    private TextField txtUserAgent;

    private final Stage stage;

    public PaneConfig(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglOnlyOneInstance.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_ONLY_ONE_INSTANCE);
        tglStartMaximised.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_START_ALWAYS_MAXIMISED);
        tglCheckStart.selectedProperty().unbindBidirectional(ProgConfig.CHECK_SET_PROGRAM_START);
        tglTipOfDay.selectedProperty().unbindBidirectional(ProgConfig.TIP_OF_DAY_SHOW);
        txtUserAgent.textProperty().unbindBidirectional(ProgConfig.SYSTEM_USERAGENT);
    }

    public void make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        TitledPane tpConfig = new TitledPane("Allgemein", gridPane);
        result.add(tpConfig);

        tglOnlyOneInstance.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_ONLY_ONE_INSTANCE);
        final Button btnHelpOnlyOneInstance = P2Button.helpButton(stage, "Nur eine Instanz des Programms öffnen",
                HelpText.ONLY_ONE_INSTANCE);
        GridPane.setHalignment(btnHelpOnlyOneInstance, HPos.RIGHT);

        tglStartMaximised.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_START_ALWAYS_MAXIMISED);
        final Button btnHelpStartMaximised = P2Button.helpButton(stage, "Programm immer \"Maximiert\" starten",
                HelpText.START_MAXIMISED);
        GridPane.setHalignment(btnHelpStartMaximised, HPos.RIGHT);

        tglCheckStart.selectedProperty().bindBidirectional(ProgConfig.CHECK_SET_PROGRAM_START);

        final Button btnHelpCheck = P2Button.helpButton(stage, "Download-Einstellungen prüfen",
                HelpText.CHECK_SET_PROGRAM_START);

        tglTipOfDay.selectedProperty().bindBidirectional(ProgConfig.TIP_OF_DAY_SHOW);
        final Button btnHelpTipOfDay = P2Button.helpButton(stage, "Tip des Tages anzeigen",
                HelpText.TIP_OF_DAY);
        GridPane.setHalignment(btnHelpTipOfDay, HPos.RIGHT);

        final Button btnHelpUserAgent = P2Button.helpButton(stage, "User Agent festlegen",
                HelpText.USER_AGENT);
        GridPane.setHalignment(btnHelpUserAgent, HPos.RIGHT);
        txtUserAgent = new TextField() {
            @Override
            public void replaceText(int start, int end, String text) {
                if (check(text)) {
                    super.replaceText(start, end, text);
                }
            }

            @Override
            public void replaceSelection(String text) {
                if (check(text)) {
                    super.replaceSelection(text);
                }
            }

            private boolean check(String text) {
                String str = P2StringUtils.convertToASCIIEncoding(text);
                final int size = getText().length() + text.length();

                if (text.isEmpty() || (size < ProgConst.MAX_USER_AGENT_SIZE) && text.equals(str)) {
                    return true;
                }
                return false;
            }
        };
        txtUserAgent.textProperty().bindBidirectional(ProgConfig.SYSTEM_USERAGENT);

        int row = 0;
        gridPane.add(tglOnlyOneInstance, 0, ++row, 2, 1);
        gridPane.add(btnHelpOnlyOneInstance, 2, row);

        gridPane.add(tglStartMaximised, 0, ++row, 2, 1);
        gridPane.add(btnHelpStartMaximised, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglCheckStart, 0, ++row, 2, 1);
        gridPane.add(btnHelpCheck, 2, row);

        GridPane.setHalignment(btnHelpCheck, HPos.RIGHT);
        GridPane.setValignment(btnHelpCheck, VPos.CENTER);

        gridPane.add(tglTipOfDay, 0, ++row, 2, 1);
        gridPane.add(btnHelpTipOfDay, 2, row);

        gridPane.add(new Label("User Agent:"), 0, ++row);
        gridPane.add(txtUserAgent, 1, row);
        gridPane.add(btnHelpUserAgent, 2, row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(), P2ColumnConstraints.getCcPrefSize());
    }
}
