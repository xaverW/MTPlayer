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
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartPaneGeo {
    private final RadioButton rbDe = new RadioButton("DE - Deutschland");
    private final RadioButton rbFr = new RadioButton("FR - Frankreich");
    private final RadioButton rbCh = new RadioButton("CH - Schweiz");
    private final RadioButton rbAt = new RadioButton("AT - Österreich");
    private final RadioButton rbEu = new RadioButton("EU (EBU - European Broadcasting Union)");
    private final RadioButton rbSonst = new RadioButton("sonst");
    private final P2ToggleSwitch tglGeo = new P2ToggleSwitch("Geblockte Sendungen gelb markieren");
    private final Stage stage;

    public StartPaneGeo(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglGeo.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_MARK_GEO);
    }

    public TitledPane make() {
        VBox vBox = new VBox(10);

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Hier kann ausgewählt werden, in welchem Land das Programm " +
                "genutzt wird. Filme die in diesem Land nicht abgerufen werden können, " +
                "werden dann markiert. Die Geo-Sperre kann damit aber nicht " +
                "umgangen werden.");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(5), hBox, P2GuiTools.getVDistance(20));

        tglGeo.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_MARK_GEO);
        final Button btnHelpGeo = P2Button.helpButton(stage, "Geogeblockte Filme", HelpText.CONFIG_GEO);

        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(rbDe, rbFr, rbCh, rbAt, rbEu, rbSonst);

        switch (ProgConfig.SYSTEM_GEO_HOME_PLACE.get()) {
            case FilmDataMTP.GEO_FR:
                rbFr.setSelected(true);
                break;
            case FilmDataMTP.GEO_CH:
                rbCh.setSelected(true);
                break;
            case FilmDataMTP.GEO_AT:
                rbAt.setSelected(true);
                break;
            case FilmDataMTP.GEO_EU:
                rbEu.setSelected(true);
                break;
            case FilmDataMTP.GEO_WELT:
                rbSonst.setSelected(true);
                break;
            default:
                rbDe.setSelected(true);
        }
        rbDe.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmDataMTP.GEO_DE);
        });
        rbFr.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmDataMTP.GEO_FR);
        });
        rbCh.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmDataMTP.GEO_CH);
        });
        rbAt.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmDataMTP.GEO_AT);
        });
        rbEu.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmDataMTP.GEO_EU);
        });
        rbSonst.setOnAction(e -> {
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmDataMTP.GEO_WELT);
        });

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        int row = 0;
        gridPane.add(tglGeo, 0, row, 2, 1);
        gridPane.add(btnHelpGeo, 2, row);

        gridPane.add(new Label(" "), 0, ++row);

        // eigener Standort angeben
        gridPane.add(new Label("Mein Standort:"), 0, ++row);

        gridPane.add(rbDe, 1, row);
        gridPane.add(rbFr, 1, ++row);
        gridPane.add(rbCh, 1, ++row);
        gridPane.add(rbAt, 1, ++row);
        gridPane.add(rbEu, 1, ++row);
        gridPane.add(rbSonst, 1, ++row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());
        vBox.getChildren().add(gridPane);

        return new TitledPane("Geogeblockte Filme", vBox);
    }
}
