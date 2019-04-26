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
import de.p2tools.p2Lib.guiTools.PAccordion;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class FilmPaneController extends AnchorPane {

    private final ProgData progData;
    private final VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final ScrollPane scrollPane = new ScrollPane();
    private final int FILTER_DAYS_MAX = 150;

    BooleanProperty accordionProp = ProgConfig.CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    BooleanProperty propLoad = ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getBooleanProperty();
    StringProperty propUrl = ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.getStringProperty();
    IntegerProperty selectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_FILM;

    private final Stage stage;

    public FilmPaneController(Stage stage) {
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
        makeConfig(result);
        result.add(new LoadFilmsPane(stage, progData).make());

        return result;
    }

    private void makeConfig(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Filmliste laden", gridPane);
        result.add(tpConfig);

        final PToggleSwitch tglLoad = new PToggleSwitch("Filmliste beim Programmstart laden");
        tglLoad.setHGrow(false);
        tglLoad.selectedProperty().bindBidirectional(propLoad);
        HBox hBoxTgl = new HBox(10);
        hBoxTgl.getChildren().add(tglLoad);

        final Button btnHelpLoad = PButton.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART);
        GridPane.setHalignment(btnHelpLoad, HPos.RIGHT);

        TextField txtUrl = new TextField("");
        txtUrl.textProperty().bindBidirectional(propUrl);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtUrl);
        });
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Eine Filmliste die geladen werden soll, manuell auswählen."));

        final Button btnHelp = PButton.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_MANUAL);

        int row = 0;
        gridPane.add(hBoxTgl, 0, row);
        gridPane.add(btnHelpLoad, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("Adresse (Datei oder URL) zum Laden der Filmliste:\n" +
                "(wenn leer, wird die Standard-URL verwendet)"), 0, ++row, 2, 1);

        gridPane.add(txtUrl, 0, ++row);
        gridPane.add(btnFile, 1, row);
        gridPane.add(btnHelp, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize());
    }
    
}