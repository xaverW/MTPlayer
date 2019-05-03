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

package de.mtplayer.mtp.gui.mediaConfig;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PAccordion;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class PaneConfigController extends AnchorPane {

    private VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final BooleanProperty accordionProp = ProgConfig.MEDIA_CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    private ScrollPane scrollPane = new ScrollPane();

    BooleanProperty propSuff = ProgConfig.MEDIA_DB_WITH_OUT_SUFFIX.getBooleanProperty();
    StringProperty propSuffStr = ProgConfig.MEDIA_DB_SUFFIX.getStringProperty();
    BooleanProperty propNoHiddenFiles = ProgConfig.MEDIA_DB_NO_HIDDEN_FILES.getBooleanProperty();
    IntegerProperty selectedTab = ProgConfig.SYSTEM_MEDIA_DIALOG_CONFIG;


    private final ProgData progData;
    private final Stage stage;

    public PaneConfigController(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();

        cbxAccordion.selectedProperty().bindBidirectional(accordionProp);
        cbxAccordion.selectedProperty().addListener((observable, oldValue, newValue) -> setAccordion());

        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        hBox.getChildren().addAll(cbxAccordion, scrollPane);
        getChildren().addAll(hBox);

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
        makeConfig(result);
        new PaneConfigPath(stage, false).make(result);
        new PaneConfigPath(stage, true).make(result);
        return result;
    }

    private void makeConfig(Collection<TitledPane> result) {
        VBox vBox = new VBox();

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Allgemein", vBox);
        result.add(tpConfig);

        final RadioButton rbWithOutSuff = new RadioButton("keine Dateien mit diesem Suffix (z.B.: txt,xml,jpg");
        final RadioButton rbWithSuff = new RadioButton("nur Dateien mit diesem Suffix  (z.B.: mp4,flv,m4v");

        final Button btnHelp = PButton.helpButton(stage,
                "Mediensammlungen verwalten", HelpText.MEDIA_COLLECTION);

        final ToggleGroup tg = new ToggleGroup();
        rbWithOutSuff.setToggleGroup(tg);
        rbWithSuff.setToggleGroup(tg);

        rbWithSuff.setSelected(!propSuff.getValue());
        rbWithOutSuff.selectedProperty().bindBidirectional(propSuff);

        TextField txtSuff = new TextField();
        txtSuff.textProperty().bindBidirectional(propSuffStr);


        final PToggleSwitch tglNoHiddenFiles = new PToggleSwitch("keine versteckten Dateien suchen:", false, false);
        tglNoHiddenFiles.selectedProperty().bindBidirectional(propNoHiddenFiles);

        int row = 0;
        gridPane.add(rbWithOutSuff, 0, row);
        gridPane.add(btnHelp, 1, row);
        gridPane.add(rbWithSuff, 0, ++row);
        gridPane.add(txtSuff, 0, ++row, 2, 1);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglNoHiddenFiles, 0, ++row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

        vBox.getChildren().addAll(gridPane);
    }

}
