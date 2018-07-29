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
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collection;

public class FilmPaneController extends AnchorPane {

    private final ProgData progData;
    private final VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final ScrollPane scrollPane = new ScrollPane();
    private final Slider slDays = new Slider();
    private final Label lblDays = new Label("");
    private final int FILTER_DAYS_MAX = 150;

    BooleanProperty accordionProp = ProgConfig.CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    IntegerProperty propDay = ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getIntegerProperty();
    BooleanProperty propLoad = ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getBooleanProperty();
    StringProperty propUrl = ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.getStringProperty();

    public FilmPaneController() {
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
        makeConfig(result);
        makeLoadManuel(result);

        return result;
    }

    private void makeConfig(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Filme laden", gridPane);
        result.add(tpConfig);

        initDays();

        final Button btnHelpDays = new Button("");
        btnHelpDays.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelpDays.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpDays.setOnAction(a -> new MTAlert().showHelpAlert("nur Filme der letzten Tage laden",
                HelpText.LOAD_FILM_ONLY_DAYS));

        Button btnLoad = new Button("Filmliste jetzt laden");
        btnLoad.setOnAction(event -> {
            progData.loadFilmlist.loadFilmlist("", true);
        });

        final PToggleSwitch tglLoad = new PToggleSwitch("Filmliste beim Programmstart laden");
        tglLoad.selectedProperty().bindBidirectional(propLoad);

        final Button btnHelpLoad = new Button("");
        btnHelpLoad.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelpLoad.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpLoad.setOnAction(a -> new MTAlert().showHelpAlert("Filmliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART));


        gridPane.add(tglLoad, 0, 0);
        GridPane.setHalignment(btnHelpLoad, HPos.RIGHT);
        gridPane.add(btnHelpLoad, 2, 0);


        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(new Label("nur aktuelle Filme laden:"), slDays);
        gridPane.add(vBox, 0, 1);
        GridPane.setValignment(lblDays, VPos.BOTTOM);
        gridPane.add(lblDays, 1, 1);
        GridPane.setHalignment(btnHelpDays, HPos.RIGHT);
        gridPane.add(btnHelpDays, 2, 1);


        GridPane.setMargin(btnLoad, new Insets(20, 0, 0, 0));
        gridPane.add(btnLoad, 0, 2);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
    }


    private void initDays() {
        slDays.setMin(0);
        slDays.setMax(FILTER_DAYS_MAX);
        slDays.setShowTickLabels(false);
        slDays.setMajorTickUnit(10);
        slDays.setBlockIncrement(10);

        slDays.valueProperty().bindBidirectional(propDay);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());

        setValueSlider();
    }

    private void setValueSlider() {
        int days = (int) slDays.getValue();
        lblDays.setText(days == 0 ? "alles laden" : "nur Filme der letzten " + days + " Tage laden");
    }

    private void makeLoadManuel(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Filmliste manuell auswählen", gridPane);
        result.add(tpConfig);

        TextField txtUrl = new TextField("");
        txtUrl.textProperty().bindBidirectional(propUrl);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtUrl);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);

        final Button btnHelp = new Button("");
        btnHelp.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Filmliste laden",
                HelpText.LOAD_FILMLIST_MANUAL));

        Button btnLoad = new Button("Filmliste jetzt laden");
        btnLoad.disableProperty().bind(txtUrl.textProperty().isEmpty());
        btnLoad.setOnAction(event -> progData.loadFilmlist.loadFilmlist(txtUrl.getText()));

        GridPane.setMargin(btnLoad, new Insets(20, 0, 0, 0));

        gridPane.add(new Label("URL/Datei:"), 0, 0);
        gridPane.add(txtUrl, 1, 0);

        gridPane.add(btnFile, 2, 0);
        gridPane.add(btnHelp, 3, 0);

        GridPane.setMargin(btnLoad, new Insets(20, 0, 0, 0));
        gridPane.add(btnLoad, 0, 1, 3, 1);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
    }

}