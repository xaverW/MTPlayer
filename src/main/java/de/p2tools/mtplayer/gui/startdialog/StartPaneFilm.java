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

package de.p2tools.mtplayer.gui.startdialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartPaneFilm {

    private final Slider slDays = new Slider();
    private final Slider slDuration = new Slider();
    private final Label lblDays = new Label("");
    private final Label lblDuration = new Label("");
    private final Stage stage;

    public StartPaneFilm(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        slDays.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDuration.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION);
    }

    public TitledPane make() {
        initSlider();
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
        makeOnly(vBox);

        return new TitledPane("Filmliste bereits beim Laden filtern", vBox);
    }

    private void makeOnly(VBox vBox) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Die Filmliste enthält inzwischen ~ 700_000 Filme. Davon sind auch " +
                "einige doppelt (z.B. ARD und BR). Es ist also eine gute Idee, die doppelten nicht zu laden " +
                "und eventuell auch alte Filme nicht mehr zu laden. Auf älteren Rechnern ist " +
                "die Suche dann schneller.");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(5), hBox, P2GuiTools.getVDistance(20));

        final Button btnHelpDouble = P2Button.helpButton(stage, "Filmliste beim Laden filtern",
                HelpText.LOAD_FILMLIST_ONLY_MARK_DOUBLE);
        final Button btnHelpDays = P2Button.helpButton(stage, "Filmliste beim Laden filtern",
                HelpText.LOAD_ONLY_FILMS_STARTDIALOG);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));

        int row = 0;
        final P2ToggleSwitch tglRemove = new P2ToggleSwitch("Doppelte Filme beim Laden der Filmliste ausschließen");
        tglRemove.setSelected(ProgConfig.SYSTEM_FILMLIST_REMOVE_DOUBLE.getValue());
        tglRemove.selectedProperty().addListener((u, o, n) -> ProgConfig.SYSTEM_FILMLIST_REMOVE_DOUBLE.setValue(tglRemove.isSelected()));
        gridPane.add(tglRemove, 0, row, 3, 1);
        gridPane.add(btnHelpDouble, 3, row);
        gridPane.add(new Label(), 0, ++row);
        ++row;

        gridPane.add(new Label("Nur Filme der letzten Tage laden:"), 0, row, 2, 1);
        gridPane.add(new Label("Filme laden:"), 0, ++row);
        gridPane.add(slDays, 1, row);
        gridPane.add(lblDays, 2, row);
        gridPane.add(btnHelpDays, 3, row, 1, 2);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(new Label("Nur Filme mit Mindestlänge laden:"), 0, ++row, 2, 1);
        gridPane.add(new Label("Filme laden:"), 0, ++row);
        gridPane.add(slDuration, 1, row);
        gridPane.add(lblDuration, 2, row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());

        vBox.getChildren().add(gridPane);
    }

    private void initSlider() {
        slDays.setMin(0);
        slDays.setMax(ProgConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDays.setShowTickLabels(false);
        slDays.setMajorTickUnit(100);
        slDays.setBlockIncrement(5);

        slDays.valueProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());
        if (ProgData.debug) {
            slDays.setValue(100);
        }

        slDuration.setMin(0);
        slDuration.setMax(ProgConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION);
        slDuration.setShowTickLabels(false);
        slDuration.setMajorTickUnit(10);
        slDuration.setBlockIncrement(1);

        slDuration.valueProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION);
        slDuration.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());

        setValueSlider();
    }

    private void setValueSlider() {
        int days = (int) slDays.getValue();
        lblDays.setText(days == 0 ? "alles laden" : "nur Filme der letzten " + days + " Tage");

        int duration = (int) slDuration.getValue();
        lblDuration.setText(duration == 0 ? "alles laden" : "nur Filme mit mindestens " + duration + " Minuten Länge");
    }
}
