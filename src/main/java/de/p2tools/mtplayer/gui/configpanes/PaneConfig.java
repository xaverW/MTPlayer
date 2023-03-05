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
import de.p2tools.p2lib.dialogs.dialog.PDialog;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import de.p2tools.p2lib.tools.PStringUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Collection;

public class PaneConfig {

    private final PToggleSwitch tglSearch = new PToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");
    private final PToggleSwitch tglSearchBeta = new PToggleSwitch("auch nach neuen Vorabversionen suchen");
    private final CheckBox chkDaily = new CheckBox("Zwischenschritte (Dailys) mit einbeziehen");
    private final PToggleSwitch tglSearchAbo = new PToggleSwitch("Abos automatisch suchen:");
    private final PToggleSwitch tglStartDownload = new PToggleSwitch("Downloads aus Abos sofort starten:");
    private final PToggleSwitch tglSmallFilm = new PToggleSwitch("In der Tabelle \"Film\" nur kleine Button anzeigen:");
    private final PToggleSwitch tglSmallDownload = new PToggleSwitch("In der Tabelle \"Download\" nur kleine Button anzeigen:");
    private final PToggleSwitch tglTipOfDay = new PToggleSwitch("Tip des Tages anzeigen");
    private TextField txtUserAgent;
    private final PToggleSwitch tglEnableLog = new PToggleSwitch("Ein Logfile anlegen:");

    private final PDialog pDialog;

    public PaneConfig(PDialog pDialog) {
        this.pDialog = pDialog;
    }

    public void close() {
        tglSearchAbo.selectedProperty().unbindBidirectional(ProgConfig.ABO_SEARCH_NOW);
        tglStartDownload.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_START_NOW);
        tglSmallFilm.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM);
        tglSmallDownload.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD);
        tglTipOfDay.selectedProperty().unbindBidirectional(ProgConfig.TIP_OF_DAY_SHOW);
        txtUserAgent.textProperty().unbindBidirectional(ProgConfig.SYSTEM_USERAGENT);
        tglEnableLog.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOG_ON);
        tglSearch.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_ACT);
        tglSearchBeta.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_BETA);
        chkDaily.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_UPDATE_SEARCH_DAILY);
    }

    public void makeConfig(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        TitledPane tpConfig = new TitledPane("Allgemein", gridPane);
        result.add(tpConfig);

        tglSearchAbo.selectedProperty().bindBidirectional(ProgConfig.ABO_SEARCH_NOW);
        final Button btnHelpAbo = PButton.helpButton(pDialog.getStage(), "Abos automatisch suchen",
                HelpText.SEARCH_ABOS_IMMEDIATELY);
        GridPane.setHalignment(btnHelpAbo, HPos.RIGHT);


        tglStartDownload.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_START_NOW);
        final Button btnHelpDownload = PButton.helpButton(pDialog.getStage(), "Downloads sofort starten",
                HelpText.START_DOWNLOADS_FROM_ABOS_IMMEDIATELY);
        GridPane.setHalignment(btnHelpDownload, HPos.RIGHT);


        tglSmallFilm.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM);
        tglSmallDownload.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD);
        final Button btnHelpSize = PButton.helpButton(pDialog.getStage(), "Nur kleine Button anzeigen",
                HelpText.SMALL_BUTTON);
        GridPane.setHalignment(btnHelpSize, HPos.RIGHT);

        tglTipOfDay.selectedProperty().bindBidirectional(ProgConfig.TIP_OF_DAY_SHOW);
        final Button btnHelpTipOfDay = PButton.helpButton(pDialog.getStage(), "Tip des Tages anzeigen",
                HelpText.TIP_OF_DAY);
        GridPane.setHalignment(btnHelpTipOfDay, HPos.RIGHT);

        final Button btnHelpUserAgent = PButton.helpButton(pDialog.getStage(), "User Agent festlegen",
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
        gridPane.add(tglSearchAbo, 0, row, 2, 1);
        gridPane.add(btnHelpAbo, 2, row);
        gridPane.add(tglStartDownload, 0, ++row, 2, 1);
        gridPane.add(btnHelpDownload, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglSmallFilm, 0, ++row, 2, 1);
        gridPane.add(btnHelpSize, 2, row);
        gridPane.add(tglSmallDownload, 0, ++row, 2, 1);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglTipOfDay, 0, ++row, 2, 1);
        gridPane.add(btnHelpTipOfDay, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("User Agent:"), 0, ++row);
        gridPane.add(txtUserAgent, 1, row);
        gridPane.add(btnHelpUserAgent, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize());
    }
}
