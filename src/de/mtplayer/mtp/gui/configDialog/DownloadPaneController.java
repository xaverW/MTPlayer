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
import de.p2tools.p2Lib.guiTools.PButton;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.controlsfx.control.ToggleSwitch;

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

    private final ScrollPane scrollPane = new ScrollPane();

    public DownloadPaneController() {
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

        setAccordion();
    }

    private void setAccordion() {
        if (cbxAccordion.isSelected()) {
            noaccordion.getChildren().clear();
            accordion.getPanes().addAll(createPanes());
            scrollPane.setContent(accordion);
        } else {
            accordion.getPanes().clear();
            noaccordion.getChildren().addAll(createPanes());
            scrollPane.setContent(noaccordion);
        }
    }

    private Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        makeDownload(result);
        new ReplacePane().makeReplaceListTable(result);
        return result;
    }

    private void makeDownload(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Download", gridPane);
        result.add(tpConfig);


        final ToggleSwitch tglFinished = new ToggleSwitch("Benachrichtigung wenn abgeschlossen");
        tglFinished.setMaxWidth(Double.MAX_VALUE);
        tglFinished.selectedProperty().bindBidirectional(propNotify);

        final Button btnHelpFinished = new PButton().helpButton("Download",
                HelpText.DOWNLOAD_FINISHED);


        final ToggleSwitch tglError = new ToggleSwitch("bei Downloadfehler, Fehlermeldung anzeigen");
        tglError.setMaxWidth(Double.MAX_VALUE);
        tglError.selectedProperty().bindBidirectional(propErr);

        final Button btnHelpError = new PButton().helpButton("Download",
                HelpText.DOWNLOAD_ERROR);


        final ToggleSwitch tglOne = new ToggleSwitch("nur ein Download pro Downloadserver");
        tglOne.setMaxWidth(Double.MAX_VALUE);
        tglOne.selectedProperty().bindBidirectional(propOne);

        final Button btnHelpOne = new PButton().helpButton("Download",
                HelpText.DOWNLOAD_ONE_SERVER);


        final ToggleSwitch tglBeep = new ToggleSwitch("nach jedem Download einen \"Beep\" ausgeben");
        tglBeep.setMaxWidth(Double.MAX_VALUE);
        tglBeep.selectedProperty().bindBidirectional(propBeep);

        final Button btnBeep = new Button("Testen");
        btnBeep.setOnAction(a -> Toolkit.getDefaultToolkit().beep());


        GridPane.setHalignment(btnHelpFinished, HPos.RIGHT);
        GridPane.setHalignment(btnHelpError, HPos.RIGHT);
        GridPane.setHalignment(btnHelpOne, HPos.RIGHT);

        gridPane.add(tglFinished, 0, 0);
        gridPane.add(btnHelpFinished, 2, 0);

        gridPane.add(tglError, 0, 1);
        gridPane.add(btnHelpError, 2, 1);

        gridPane.add(tglOne, 0, 2);
        gridPane.add(btnHelpOne, 2, 2);

        gridPane.add(tglBeep, 0, 3);
        gridPane.add(btnBeep, 2, 3);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
    }


}