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
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PAccordion;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class BlackListPaneController extends AnchorPane {

    private final ProgData progData;
    private final VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final ScrollPane scrollPane = new ScrollPane();
    private final Slider slSize = new Slider();
    private final Label lblSize = new Label("");
    private final Slider slDays = new Slider();
    private final Label lblDays = new Label("");

    BooleanProperty accordionProp = ProgConfig.CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    IntegerProperty propSize = ProgConfig.SYSTEM_BLACKLIST_MIN_FILM_DURATION.getIntegerProperty();
    IntegerProperty propDay = ProgConfig.SYSTEM_BLACKLIST_MAX_FILM_DAYS.getIntegerProperty();
    BooleanProperty propGeo = ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_GEO.getBooleanProperty();
    BooleanProperty propAbo = ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getBooleanProperty();
    BooleanProperty propFuture = ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_FUTURE.getBooleanProperty();
    IntegerProperty selectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_BLACKLIST;
    private final BooleanProperty blackChanged;

    private final Stage stage;

    public BlackListPaneController(Stage stage, BooleanProperty blackChanged) {
        this.stage = stage;
        this.blackChanged = blackChanged;
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
        makeBlack(result);
        new BlackPane(stage, blackChanged).makeBlackTable(result);
        return result;
    }

    private void makeBlack(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Blacklist allgemein", gridPane);
        result.add(tpConfig);

        final PToggleSwitch tglAbo = new PToggleSwitch("die Blacklist beim Suchen der Abos berücksichtigen");
        tglAbo.selectedProperty().bindBidirectional(propAbo);
        tglAbo.selectedProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));

        final Button btnHelp = PButton.helpButton(stage, "Blacklist",
                HelpText.BLACKLIST_ABO);


        final PToggleSwitch tglFuture = new PToggleSwitch("Filme mit Datum in der Zukunft nicht anzeigen");
        tglFuture.selectedProperty().bindBidirectional(propFuture);
        tglFuture.selectedProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));

        final Button btnHelpFuture = PButton.helpButton(stage, "Blacklist",
                HelpText.BLACKLIST_FUTURE);


        final PToggleSwitch tglGeo = new PToggleSwitch("Filme, die per Geoblocking gesperrt sind, nicht anzeigen");
        tglGeo.selectedProperty().bindBidirectional(propGeo);
        tglGeo.selectedProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));

        final Button btnHelpGeo = PButton.helpButton(stage, "Blacklist",
                HelpText.BLACKLIST_GEO);


        initDays();

        final Button btnHelpSize = PButton.helpButton(stage, "Blacklist",
                HelpText.BLACKLIST_SIZE);

        final Button btnHelpDays = PButton.helpButton(stage, "Blacklist",
                HelpText.BLACKLIST_DAYS);

        lblDays.setMinWidth(Region.USE_PREF_SIZE);
        lblSize.setMinWidth(Region.USE_PREF_SIZE);

        int row = 0;
        gridPane.add(tglAbo, 0, row, 3, 1);
        gridPane.add(btnHelp, 3, row);

        gridPane.add(tglFuture, 0, ++row, 3, 1);
        gridPane.add(btnHelpFuture, 3, row);

        gridPane.add(tglGeo, 0, ++row, 3, 1);
        gridPane.add(btnHelpGeo, 3, row);


        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("nur Filme der letzten Tage anzeigen:"), 0, ++row, 2, 1);

        gridPane.add(new Label("Filme anzeigen:"), 0, ++row);
        gridPane.add(slDays, 1, row);
        gridPane.add(lblDays, 2, row);
        gridPane.add(btnHelpDays, 3, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("nur Filme mit Mindestlänge anzeigen:"), 0, ++row, 2, 1);

        gridPane.add(new Label("Filme anzeigen:"), 0, ++row);
        gridPane.add(slSize, 1, row);
        gridPane.add(lblSize, 2, row);
        gridPane.add(btnHelpSize, 3, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());
    }

    private void initDays() {
        slDays.setMin(0);
        slDays.setMax(ProgConst.SYSTEM_BLACKLIST_MAX_FILM_DAYS);
        slDays.setShowTickLabels(false);
        slDays.setMajorTickUnit(10);
        slDays.setBlockIncrement(10);

        slDays.valueProperty().bindBidirectional(propDay);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> {
            setValueSlider();
            blackChanged.set(true);
        });

        slSize.setMin(0);
        slSize.setMax(ProgConst.SYSTEM_BLACKLIST_MIN_FILM_DURATION);
        slSize.setShowTickLabels(false);
        slSize.setMajorTickUnit(10);
        slSize.setBlockIncrement(10);

        slSize.valueProperty().bindBidirectional(propSize);
        slSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            setValueSlider();
            blackChanged.set(true);
        });

        setValueSlider();
    }

    private void setValueSlider() {
        int min = (int) slSize.getValue();
        lblSize.setText(min == 0 ? "alles anzeigen" : "nur Filme mit mindestens " + min + " Minuten Länge");

        min = (int) slDays.getValue();
        lblDays.setText(min == 0 ? "alles anzeigen" : "nur Filme der letzten " + min + " Tage");
    }
}