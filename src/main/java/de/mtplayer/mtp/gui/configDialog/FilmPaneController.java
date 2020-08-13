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

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class FilmPaneController extends PAccordionPane {

    private final int FILTER_DAYS_MAX = 150;

    BooleanProperty propLoad = ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getBooleanProperty();
    StringProperty propUrl = ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.getStringProperty();

    private LoadFilmsPane loadFilmsPane;
    private final PToggleSwitch tglLoad = new PToggleSwitch("Filmliste beim Programmstart laden");
    private TextField txtUrl;

    private final ProgData progData;
    private final Stage stage;

    public FilmPaneController(Stage stage) {
        super(stage, ProgConfig.CONFIG_DIALOG_ACCORDION.getBooleanProperty(), ProgConfig.SYSTEM_CONFIG_DIALOG_FILM);
        this.stage = stage;
        progData = ProgData.getInstance();

        init();
    }

    public void close() {
        super.close();
        tglLoad.selectedProperty().unbindBidirectional(propLoad);
        txtUrl.textProperty().unbindBidirectional(propUrl);
    }

    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        makeConfig(result);

        loadFilmsPane = new LoadFilmsPane(stage, progData);
        loadFilmsPane.make(result);

        return result;
    }

    private void makeConfig(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Filmliste laden", gridPane);
        result.add(tpConfig);

        tglLoad.selectedProperty().bindBidirectional(propLoad);
        final Button btnHelpLoad = PButton.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART);


        txtUrl = new TextField("");
        txtUrl.textProperty().bindBidirectional(propUrl);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtUrl);
        });
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Eine Filmliste die geladen werden soll, manuell auswählen."));

        final Button btnHelp = PButton.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_MANUAL);

        int row = 0;

        gridPane.add(tglLoad, 0, ++row, 2, 1);
        gridPane.add(btnHelpLoad, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("Adresse (Datei oder URL) zum Laden der Filmliste:"),
                0, ++row);

        gridPane.add(txtUrl, 0, ++row);
        gridPane.add(btnFile, 1, row);
        gridPane.add(btnHelp, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize());
    }

}