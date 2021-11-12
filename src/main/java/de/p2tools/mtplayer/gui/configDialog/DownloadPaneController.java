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
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.dialogs.accordion.PAccordionPane;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class DownloadPaneController extends PAccordionPane {

    BooleanProperty propNotify = ProgConfig.DOWNLOAD_SHOW_NOTIFICATION;
    BooleanProperty propErr = ProgConfig.DOWNLOAD_ERROR_MSG;
    BooleanProperty propOne = ProgConfig.DOWNLOAD_MAX_ONE_PER_SERVER;
    BooleanProperty propSSL = ProgConfig.SYSTEM_SSL_ALWAYS_TRUE;
    BooleanProperty propBeep = ProgConfig.DOWNLOAD_BEEP;

    private final PToggleSwitch tglFinished = new PToggleSwitch("Benachrichtigung wenn abgeschlossen");
    private final PToggleSwitch tglError = new PToggleSwitch("bei Downloadfehler Fehlermeldung anzeigen");
    private final PToggleSwitch tglOne = new PToggleSwitch("nur ein Download pro Downloadserver");
    private final PToggleSwitch tglSSL = new PToggleSwitch("SSL-Download-URLs: Bei Problemen SSL abschalten");
    private final PToggleSwitch tglBeep = new PToggleSwitch("nach jedem Download einen \"Beep\" ausgeben");
    private ReplacePane replacePane;

    private final ProgData progData;
    private final Stage stage;

    public DownloadPaneController(Stage stage) {
        super(stage, ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_DOWNLOAD);
        this.stage = stage;
        progData = ProgData.getInstance();

        init();
    }

    @Override
    public void close() {
        super.close();
        replacePane.close();
        tglFinished.selectedProperty().unbindBidirectional(propNotify);
        tglError.selectedProperty().unbindBidirectional(propErr);
        tglOne.selectedProperty().unbindBidirectional(propOne);
        tglSSL.selectedProperty().unbindBidirectional(propSSL);
        tglBeep.selectedProperty().unbindBidirectional(propBeep);
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        makeDownload(result);
        replacePane = new ReplacePane(stage);
        replacePane.makeReplaceListTable(result);
        return result;
    }

    private void makeDownload(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Download", gridPane);
        result.add(tpConfig);

        tglFinished.selectedProperty().bindBidirectional(propNotify);
        final Button btnHelpFinished = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_FINISHED);

        tglError.selectedProperty().bindBidirectional(propErr);
        final Button btnHelpError = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_ERROR);

        tglOne.selectedProperty().bindBidirectional(propOne);
        final Button btnHelpOne = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_ONE_SERVER);

        tglSSL.selectedProperty().bindBidirectional(propSSL);
        final Button btnHelpSSL = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_SSL_ALWAYS_TRUE);

        tglBeep.selectedProperty().bindBidirectional(propBeep);
        final Button btnBeep = new Button("_Testen");
        btnBeep.setOnAction(a -> Toolkit.getDefaultToolkit().beep());

        GridPane.setHalignment(btnHelpFinished, HPos.RIGHT);
        GridPane.setHalignment(btnHelpError, HPos.RIGHT);
        GridPane.setHalignment(btnHelpOne, HPos.RIGHT);
        GridPane.setHalignment(btnHelpSSL, HPos.RIGHT);

        int row = 0;
        gridPane.add(tglFinished, 0, row);
        gridPane.add(btnHelpFinished, 1, row);

        gridPane.add(tglError, 0, ++row);
        gridPane.add(btnHelpError, 1, row);

        gridPane.add(tglOne, 0, ++row);
        gridPane.add(btnHelpOne, 1, row);

        gridPane.add(tglSSL, 0, ++row);
        gridPane.add(btnHelpSSL, 1, row);

        gridPane.add(tglBeep, 0, ++row);
        gridPane.add(btnBeep, 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize());
    }
}