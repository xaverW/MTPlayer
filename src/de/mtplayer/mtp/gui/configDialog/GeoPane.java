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

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.controlsfx.control.ToggleSwitch;

public class GeoPane {
    RadioButton rbDe = new RadioButton("DE - Deutschland");
    RadioButton rbCh = new RadioButton("CH - Schweiz");
    RadioButton rbAt = new RadioButton("AT - Österreich");
    RadioButton rbEu = new RadioButton("EU (EUB - European Broadcasting Union");
    RadioButton rbSonst = new RadioButton("sonst");

    BooleanProperty geoProperty = Config.SYSTEM_MARK_GEO.getBooleanProperty();

    public TitledPane makeGeo() {
        int row = 0;

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Geogeblockte Filme", gridPane);

        final ToggleSwitch tglGeo = new ToggleSwitch("geblockte Sendungen gelb markieren");
        tglGeo.selectedProperty().bindBidirectional(geoProperty);
        gridPane.add(tglGeo, 0, row);


        final Button btnHelpGeo = new Button("");
        btnHelpGeo.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpGeo.setOnAction(a -> new MTAlert().showHelpAlert("Geogeblockte Filme",
                HelpText.CONFIG_GEO));
        gridPane.add(btnHelpGeo, 1, row);
        GridPane.setHalignment(btnHelpGeo, HPos.RIGHT);

        // eigener Standort angeben
        gridPane.add(new Label("Mein Standort:"), 0, ++row);

        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(rbDe, rbCh, rbAt, rbEu, rbSonst);

        switch (Config.SYSTEM_GEO_HOME_PLACE.get()) {
            case FilmXml.GEO_CH:
                rbCh.setSelected(true);
                break;
            case FilmXml.GEO_AT:
                rbAt.setSelected(true);
                break;
            case FilmXml.GEO_EU:
                rbEu.setSelected(true);
                break;
            case FilmXml.GEO_WELT:
                rbSonst.setSelected(true);
                break;
            default:
                rbDe.setSelected(true);
        }
        rbDe.setOnAction(e -> {
            Config.SYSTEM_GEO_HOME_PLACE.setValue(FilmXml.GEO_DE);
        });
        rbCh.setOnAction(e -> {
            Config.SYSTEM_GEO_HOME_PLACE.setValue(FilmXml.GEO_CH);
        });
        rbAt.setOnAction(e -> {
            Config.SYSTEM_GEO_HOME_PLACE.setValue(FilmXml.GEO_AT);
        });
        rbEu.setOnAction(e -> {
            Config.SYSTEM_GEO_HOME_PLACE.setValue(FilmXml.GEO_EU);
        });
        rbSonst.setOnAction(e -> {
            Config.SYSTEM_GEO_HOME_PLACE.setValue(FilmXml.GEO_WELT);
        });
        gridPane.add(rbDe, 0, ++row);
        gridPane.add(rbCh, 0, ++row);
        gridPane.add(rbAt, 0, ++row);
        gridPane.add(rbEu, 0, ++row);
        gridPane.add(rbSonst, 0, ++row);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);

        return tpConfig;
    }

}
