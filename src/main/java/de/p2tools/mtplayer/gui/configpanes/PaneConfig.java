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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.tools.PStringUtils;
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
    private final P2ToggleSwitch tglSmallFilm = new P2ToggleSwitch("In der Tabelle \"Film\" nur kleine Button anzeigen:");
    private final P2ToggleSwitch tglSmallDownload = new P2ToggleSwitch("In der Tabelle \"Download\" nur kleine Button anzeigen:");
    private final P2ToggleSwitch tglSmallAbo = new P2ToggleSwitch("In der Tabelle \"Abo\" nur kleine Button anzeigen:");
    private final P2ToggleSwitch tglTipOfDay = new P2ToggleSwitch("Tip des Tages anzeigen");
    private TextField txtUserAgent;

    private final Stage stage;

    public PaneConfig(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglOnlyOneInstance.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_ONLY_ONE_INSTANCE);
        tglStartMaximised.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_GUI_START_ALWAYS_MAXIMISED);
        tglSmallFilm.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM);
        tglSmallDownload.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD);
        tglSmallAbo.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_ABO);
        tglTipOfDay.selectedProperty().unbindBidirectional(ProgConfig.TIP_OF_DAY_SHOW);
        txtUserAgent.textProperty().unbindBidirectional(ProgConfig.SYSTEM_USERAGENT);
    }

    public void makeConfig(Collection<TitledPane> result) {
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


        tglSmallFilm.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM);
        tglSmallDownload.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD);
        tglSmallAbo.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_ABO);
        final Button btnHelpSize = P2Button.helpButton(stage, "Nur kleine Button anzeigen",
                HelpText.SMALL_BUTTON);
        GridPane.setHalignment(btnHelpSize, HPos.RIGHT);

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
                String str = PStringUtils.convertToASCIIEncoding(text);
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
        gridPane.add(tglSmallFilm, 0, ++row, 2, 1);
        gridPane.add(btnHelpSize, 2, row, 1, 2);
        gridPane.add(tglSmallDownload, 0, ++row, 2, 1);
        gridPane.add(tglSmallAbo, 0, ++row, 2, 1);
        GridPane.setValignment(btnHelpSize, VPos.TOP);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglTipOfDay, 0, ++row, 2, 1);
        gridPane.add(btnHelpTipOfDay, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("User Agent:"), 0, ++row);
        gridPane.add(txtUserAgent, 1, row);
        gridPane.add(btnHelpUserAgent, 2, row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(), P2ColumnConstraints.getCcPrefSize());
    }
}
