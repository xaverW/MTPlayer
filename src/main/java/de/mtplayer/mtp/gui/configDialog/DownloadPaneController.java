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

package de.mtplayer.mtp.gui.configDialog;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PAccordion;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class DownloadPaneController extends AnchorPane {

    private final ProgData progData;
    private final VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");

    BooleanProperty accordionProp = ProgConfig.CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    BooleanProperty propNotify = ProgConfig.DOWNLOAD_SHOW_NOTIFICATION.getBooleanProperty();
    BooleanProperty propErr = ProgConfig.DOWNLOAD_ERROR_MSG.getBooleanProperty();
    BooleanProperty propOne = ProgConfig.DOWNLOAD_MAX_ONE_PER_SERVER.getBooleanProperty();
    BooleanProperty propBeep = ProgConfig.DOWNLOAD_BEEP.getBooleanProperty();
    IntegerProperty selectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_DOWNLOAD;

    private final ScrollPane scrollPane = new ScrollPane();
    private final Stage stage;

    public DownloadPaneController(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();

        cbxAccordion.selectedProperty().bindBidirectional(accordionProp);
        cbxAccordion.selectedProperty().addListener((observable, oldValue, newValue) -> setAccordion());

        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        hBox.getChildren().addAll(cbxAccordion, scrollPane);
        getChildren().addAll(hBox);

        accordion.setPadding(new Insets(1));
        noaccordion.setPadding(new Insets(1));
        noaccordion.setSpacing(1);

        AnchorPane.setLeftAnchor(hBox, 10.0);
        AnchorPane.setBottomAnchor(hBox, 10.0);
        AnchorPane.setRightAnchor(hBox, 10.0);
        AnchorPane.setTopAnchor(hBox, 10.0);

        PAccordion.initAccordionPane(accordion, selectedTab);
        setAccordion();
    }

    private void setAccordion() {
        if (cbxAccordion.isSelected()) {
            noaccordion.getChildren().clear();
            accordion.getPanes().addAll(createPanes());
            scrollPane.setContent(accordion);

            PAccordion.setAccordionPane(accordion, selectedTab);

        } else {
            accordion.getPanes().clear();
            noaccordion.getChildren().addAll(createPanes());
            scrollPane.setContent(noaccordion);
        }
    }

    private Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        makeDownload(result);
        new ReplacePane(stage).makeReplaceListTable(result);
        return result;
    }

    private void makeDownload(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Download", gridPane);
        result.add(tpConfig);


        final PToggleSwitch tglFinished = new PToggleSwitch("Benachrichtigung wenn abgeschlossen");
        tglFinished.selectedProperty().bindBidirectional(propNotify);

        final Button btnHelpFinished = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_FINISHED);


        final PToggleSwitch tglError = new PToggleSwitch("bei Downloadfehler Fehlermeldung anzeigen");
        tglError.selectedProperty().bindBidirectional(propErr);

        final Button btnHelpError = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_ERROR);


        final PToggleSwitch tglOne = new PToggleSwitch("nur ein Download pro Downloadserver");
        tglOne.selectedProperty().bindBidirectional(propOne);

        final Button btnHelpOne = PButton.helpButton(stage, "Download",
                HelpText.DOWNLOAD_ONE_SERVER);


        final PToggleSwitch tglBeep = new PToggleSwitch("nach jedem Download einen \"Beep\" ausgeben");
        tglBeep.selectedProperty().bindBidirectional(propBeep);

        final Button btnBeep = new Button("_Testen");
        btnBeep.setOnAction(a -> Toolkit.getDefaultToolkit().beep());


        GridPane.setHalignment(btnHelpFinished, HPos.RIGHT);
        GridPane.setHalignment(btnHelpError, HPos.RIGHT);
        GridPane.setHalignment(btnHelpOne, HPos.RIGHT);

        int row = 0;
        gridPane.add(tglFinished, 0, row);
        gridPane.add(btnHelpFinished, 1, row);

        gridPane.add(tglError, 0, ++row);
        gridPane.add(btnHelpError, 1, row);

        gridPane.add(tglOne, 0, ++row);
        gridPane.add(btnHelpOne, 1, row);

        gridPane.add(tglBeep, 0, ++row);
        gridPane.add(btnBeep, 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize());
    }


}