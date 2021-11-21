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
import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class GeoPane {
    private final RadioButton rbDe = new RadioButton("DE - Deutschland");
    private final RadioButton rbCh = new RadioButton("CH - Schweiz");
    private final RadioButton rbAt = new RadioButton("AT - Österreich");
    private final RadioButton rbEu = new RadioButton("EU (EBU - European Broadcasting Union)");
    private final RadioButton rbSonst = new RadioButton("sonst");

    private final PToggleSwitch tglGeo = new PToggleSwitch("geblockte Sendungen gelb markieren:");
    BooleanProperty geoProperty = ProgConfig.SYSTEM_MARK_GEO;

    private final Stage stage;

    public GeoPane(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglGeo.selectedProperty().unbindBidirectional(geoProperty);
    }

    public TitledPane makeGeo() {
        return makeGeo(null);
    }

    public TitledPane makeGeo(Collection<TitledPane> result) {
        tglGeo.selectedProperty().bindBidirectional(geoProperty);
        final Button btnHelpGeo = PButton.helpButton(stage, "Geogeblockte Filme", HelpText.CONFIG_GEO);

        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(rbDe, rbCh, rbAt, rbEu, rbSonst);

        switch (ProgConfig.SYSTEM_GEO_HOME_PLACE.get()) {
            case FilmData.GEO_CH:
                rbCh.setSelected(true);
                break;
            case FilmData.GEO_AT:
                rbAt.setSelected(true);
                break;
            case FilmData.GEO_EU:
                rbEu.setSelected(true);
                break;
            case FilmData.GEO_WELT:
                rbSonst.setSelected(true);
                break;
            default:
                rbDe.setSelected(true);
        }
        rbDe.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmData.GEO_DE);
        });
        rbCh.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmData.GEO_CH);
        });
        rbAt.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmData.GEO_AT);
        });
        rbEu.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmData.GEO_EU);
        });
        rbSonst.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmData.GEO_WELT);
        });

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        int row = 0;
        gridPane.add(tglGeo, 0, row, 2, 1);
        gridPane.add(btnHelpGeo, 2, row);

        gridPane.add(new Label(" "), 0, ++row);

        // eigener Standort angeben
        gridPane.add(new Label("Mein Standort:"), 0, ++row);

        gridPane.add(rbDe, 1, row);
        gridPane.add(rbCh, 1, ++row);
        gridPane.add(rbAt, 1, ++row);
        gridPane.add(rbEu, 1, ++row);
        gridPane.add(rbSonst, 1, ++row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        TitledPane tpConfig = new TitledPane("Geogeblockte Filme", gridPane);
        if (result != null) {
            result.add(tpConfig);
        }
        return tpConfig;
    }
}
