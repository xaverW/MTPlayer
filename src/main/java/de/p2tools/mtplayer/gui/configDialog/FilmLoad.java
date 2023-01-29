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
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class FilmLoad {

    private final PToggleSwitch tglLoad = new PToggleSwitch("Filmliste beim Programmstart laden");
    private TextField txtUrl;
    private final BooleanProperty diacriticChanged;

    private final ProgData progData;
    private final Stage stage;

    public FilmLoad(Stage stage, ProgData progData, BooleanProperty diacriticChanged) {
        this.stage = stage;
        this.diacriticChanged = diacriticChanged;
        this.progData = progData;
    }

    public void close() {
        tglLoad.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_ON_START);
        txtUrl.textProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY);
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20));

        tglLoad.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_ON_START);
        final Button btnHelpLoad = PButton.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART);


        txtUrl = new TextField("");
        txtUrl.textProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtUrl);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Eine Filmliste die geladen werden soll, manuell auswählen."));

        final Button btnHelp = PButton.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_MANUAL);

        //Diacritic
        PToggleSwitch tglRemoveDiacritic = new PToggleSwitch("Diakritische Zeichen ändern");
        tglRemoveDiacritic.setMaxWidth(Double.MAX_VALUE);
        tglRemoveDiacritic.setSelected(!ProgConfig.SYSTEM_SHOW_DIACRITICS.getValue());
        tglRemoveDiacritic.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            diacriticChanged.setValue(true);
            ProgConfig.SYSTEM_SHOW_DIACRITICS.setValue(!tglRemoveDiacritic.isSelected());
        });
        final Button btnHelpDia = PButton.helpButton(stage, "Diakritische Zeichen",
                HelpText.DIAKRITISCHE_ZEICHEN);


        Separator sp2 = new Separator();
        sp2.getStyleClass().add("pseperator2");
        sp2.setMinHeight(0);


        int row = 0;
        gridPane.add(tglLoad, 0, ++row, 2, 1);
        gridPane.add(btnHelpLoad, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("Adresse (Datei oder URL) zum Laden der Filmliste:"),
                0, ++row);

        gridPane.add(txtUrl, 0, ++row);
        gridPane.add(btnFile, 1, row);
        gridPane.add(btnHelp, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglRemoveDiacritic, 0, ++row, 2, 1);
        gridPane.add(btnHelpDia, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize());

        TitledPane tpConfig = new TitledPane("Filmliste laden", gridPane);
        result.add(tpConfig);
        return tpConfig;
    }
}