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

package de.p2tools.mtplayer.gui.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneBlack {

    private final ProgData progData;
    private final Slider slSize = new Slider();
    private final Label lblSize = new Label("");
    private final Slider slDays = new Slider();
    private final Label lblDays = new Label("");

    private final PToggleSwitch tglAbo = new PToggleSwitch("Die Blacklist beim Suchen der Abos berücksichtigen");
    private final PToggleSwitch tglFuture = new PToggleSwitch("Filme mit Datum in der Zukunft nicht anzeigen");
    private final PToggleSwitch tglGeo = new PToggleSwitch("Filme, die per Geoblocking gesperrt sind, nicht anzeigen");

    private final BooleanProperty blackChanged;
    private Stage stage;

    public PaneBlack(Stage stage, BooleanProperty blackChanged) {
        this.stage = stage;
        this.blackChanged = blackChanged;
        progData = ProgData.getInstance();
    }

    public void close() {
        tglAbo.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO);
        tglFuture.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_FUTURE);
        tglGeo.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_GEO);
        slDays.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_BLACKLIST_MAX_FILM_DAYS);
        slSize.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_BLACKLIST_MIN_FILM_DURATION);
    }

    public void makeBlack(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));
        makeBlack(vBox);

        TitledPane tpBlack = new TitledPane("Blacklist allgemein", vBox);
        result.add(tpBlack);
        tpBlack.setMaxHeight(Double.MAX_VALUE);
    }

    private void makeBlack(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        tglAbo.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO);
        tglAbo.selectedProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));

        final Button btnHelp = PButton.helpButton(stage, "Blacklist",
                HelpText.BLACKLIST_ABO);


        tglFuture.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_FUTURE);
        tglFuture.selectedProperty().addListener((observable, oldValue, newValue) -> blackChanged.set(true));

        final Button btnHelpFuture = PButton.helpButton(stage, "Blacklist",
                HelpText.BLACKLIST_FUTURE);


        tglGeo.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_GEO);
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
        gridPane.add(new Label("Nur Filme der letzten Tage anzeigen:"), 0, ++row, 2, 1);

        Label lbl = new Label("Filme anzeigen:");
        gridPane.add(lbl, 0, ++row);
        gridPane.add(slDays, 1, row);
        gridPane.add(lblDays, 2, row);
        gridPane.add(btnHelpDays, 3, row);
        GridPane.setHgrow(slDays, Priority.ALWAYS);
        GridPane.setValignment(lbl, VPos.TOP);
        GridPane.setValignment(slDays, VPos.TOP);
        GridPane.setValignment(lblDays, VPos.TOP);
        GridPane.setValignment(btnHelpDays, VPos.TOP);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("Nur Filme mit Mindestlänge anzeigen:"), 0, ++row, 2, 1);
        lbl = new Label("Filme anzeigen:");
        gridPane.add(lbl, 0, ++row);
        gridPane.add(slSize, 1, row);
        gridPane.add(lblSize, 2, row);
        gridPane.add(btnHelpSize, 3, row);
        GridPane.setHgrow(slSize, Priority.ALWAYS);
        GridPane.setValignment(lbl, VPos.TOP);
        GridPane.setValignment(slSize, VPos.TOP);
        GridPane.setValignment(lblSize, VPos.TOP);
        GridPane.setValignment(btnHelpSize, VPos.TOP);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        vBox.getChildren().add(gridPane);
    }

    private void initDays() {
        slDays.setMin(0);
        slDays.setMax(ProgConst.SYSTEM_BLACKLIST_MAX_FILM_DAYS);

        slDays.setMinorTickCount(4);
        slDays.setMajorTickUnit(100);
        slDays.setBlockIncrement(10);
        slDays.setShowTickLabels(true);
        slDays.setShowTickMarks(true);

        slDays.valueProperty().bindBidirectional(ProgConfig.SYSTEM_BLACKLIST_MAX_FILM_DAYS);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> {
            setValueSlider();
            blackChanged.set(true);
        });

        slSize.setMin(0);
        slSize.setMax(ProgConst.SYSTEM_BLACKLIST_MIN_FILM_DURATION);

        slSize.setMinorTickCount(4);
        slSize.setMajorTickUnit(25);
        slSize.setBlockIncrement(10);
        slSize.setShowTickLabels(true);
        slSize.setShowTickMarks(true);

        slSize.valueProperty().bindBidirectional(ProgConfig.SYSTEM_BLACKLIST_MIN_FILM_DURATION);
        slSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            setValueSlider();
            blackChanged.set(true);
        });

        setValueSlider();
    }

    private void setValueSlider() {
        int min = (int) slSize.getValue();
        lblSize.setText(min == 0 ? "alles anzeigen" : "nur Filme mit\nmindestens " + min + " Minuten Länge");

        min = (int) slDays.getValue();
        lblDays.setText(min == 0 ? "alles anzeigen" : "nur Filme der\nletzten " + min + " Tage");
    }
}