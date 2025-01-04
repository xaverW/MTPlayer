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

package de.p2tools.mtplayer.gui.configdialog.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneFilmLoad {

    private final P2ToggleSwitch tglLoad = new P2ToggleSwitch("Beim Programmstart eine neue Filmliste laden");
    private final P2ToggleSwitch tglLoadNewList = new P2ToggleSwitch("Neue Filmlisten sofort laden");
    private final BooleanProperty diacriticChanged;

    private final ProgData progData;
    private final Stage stage;

    public PaneFilmLoad(Stage stage, ProgData progData, BooleanProperty diacriticChanged) {
        this.stage = stage;
        this.diacriticChanged = diacriticChanged;
        this.progData = progData;
    }

    public void close() {
        tglLoad.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART);
        tglLoadNewList.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_NEW_FILMLIST_IMMEDIATELY);
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        tglLoad.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART);
        tglLoadNewList.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_NEW_FILMLIST_IMMEDIATELY);

        final Button btnHelpLoad = P2Button.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART);

        final Button btnHelpNewList = P2Button.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_IMMEDIATELY);

        //Diacritic
        P2ToggleSwitch tglRemoveDiacritic = new P2ToggleSwitch("Diakritische Zeichen ändern");
        tglRemoveDiacritic.setMaxWidth(Double.MAX_VALUE);
        tglRemoveDiacritic.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_REMOVE_DIACRITICS);
        tglRemoveDiacritic.selectedProperty().addListener((u, o, n) -> diacriticChanged.setValue(true));
        final Button btnHelpDia = P2Button.helpButton(stage, "Diakritische Zeichen",
                HelpText.DIAKRITISCHE_ZEICHEN);


        Button btnLoad = new Button("_Filmliste mit diesen Einstellungen neu laden");
        btnLoad.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoad.setOnAction(event -> {
            LoadFilmFactory.getInstance().loadNewListFromWeb(true);
        });


        Separator sp2 = new Separator();
        sp2.getStyleClass().add("pseperator2");
        sp2.setMinHeight(0);

        int row = 0;
        gridPane.add(tglLoad, 0, row, 2, 1);
        gridPane.add(btnHelpLoad, 2, row);

        gridPane.add(tglLoadNewList, 0, ++row, 2, 1);
        gridPane.add(btnHelpNewList, 2, row);

        gridPane.add(tglRemoveDiacritic, 0, ++row, 2, 1);
        gridPane.add(btnHelpDia, 2, row);

        gridPane.add(new Label(), 0, ++row, 3, 1);
        gridPane.add(btnLoad, 0, ++row, 3, 1);
        GridPane.setHalignment(btnLoad, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize());

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        hBox.getChildren().add(new Label("Änderungen in diesem Tab\n" +
                "wirken sich erst nach dem Neuladen einer Filmliste aus"));

        final VBox vBox = new VBox(P2LibConst.PADDING);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
        vBox.getChildren().addAll(hBox, gridPane);

        TitledPane tpConfig = new TitledPane("Filmliste laden", vBox);
        result.add(tpConfig);
        return tpConfig;
    }
}