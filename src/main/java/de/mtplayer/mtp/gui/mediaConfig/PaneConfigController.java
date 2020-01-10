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
import de.p2tools.p2Lib.dialogs.accordion.PAccordionPane;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class PaneConfigController extends PAccordionPane {

    private final RadioButton rbWithOutSuff = new RadioButton("keine Dateien mit diesem Suffix (z.B.: txt,xml,jpg");
    private final RadioButton rbWithSuff = new RadioButton("nur Dateien mit diesem Suffix  (z.B.: mp4,flv,m4v");
    private TextField txtSuff = new TextField();
    private final PToggleSwitch tglNoHiddenFiles = new PToggleSwitch("keine versteckten Dateien suchen:");

    BooleanProperty propSuff = ProgConfig.MEDIA_DB_WITH_OUT_SUFFIX.getBooleanProperty();
    StringProperty propSuffStr = ProgConfig.MEDIA_DB_SUFFIX.getStringProperty();
    BooleanProperty propNoHiddenFiles = ProgConfig.MEDIA_DB_NO_HIDDEN_FILES.getBooleanProperty();

    PaneConfigPath pane1;
    PaneConfigPath pane2;

    private final ProgData progData;
    private final Stage stage;

    public PaneConfigController(Stage stage) {
        super(stage, ProgConfig.MEDIA_CONFIG_DIALOG_ACCORDION.getBooleanProperty(), ProgConfig.SYSTEM_MEDIA_DIALOG_CONFIG);
        this.stage = stage;
        progData = ProgData.getInstance();
        init();
    }

    public void close() {
        super.close();
        rbWithOutSuff.selectedProperty().unbindBidirectional(propSuff);
        txtSuff.textProperty().unbindBidirectional(propSuffStr);
        tglNoHiddenFiles.selectedProperty().unbindBidirectional(propNoHiddenFiles);

        pane1.close();
        pane2.close();
    }

    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        makeConfig(result);
        pane1 = new PaneConfigPath(stage, false);
        pane1.make(result);
        pane2 = new PaneConfigPath(stage, true);
        pane2.make(result);
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

        rbWithSuff.setSelected(!propSuff.getValue());
        rbWithOutSuff.selectedProperty().bindBidirectional(propSuff);

        final ToggleGroup tg = new ToggleGroup();
        rbWithOutSuff.setToggleGroup(tg);
        rbWithSuff.setToggleGroup(tg);

        final Button btnHelp = PButton.helpButton(stage,
                "Mediensammlungen verwalten", HelpText.MEDIA_COLLECTION);

        txtSuff.textProperty().bindBidirectional(propSuffStr);
        tglNoHiddenFiles.selectedProperty().bindBidirectional(propNoHiddenFiles);

        int row = 0;
        gridPane.add(rbWithOutSuff, 0, row);
        gridPane.add(btnHelp, 1, row);
        gridPane.add(rbWithSuff, 0, ++row);
        gridPane.add(txtSuff, 0, ++row, 2, 1);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglNoHiddenFiles, 0, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        vBox.getChildren().addAll(gridPane);
    }

}
