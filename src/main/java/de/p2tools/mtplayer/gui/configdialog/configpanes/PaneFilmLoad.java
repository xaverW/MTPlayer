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
import de.p2tools.mtplayer.controller.load.LoadAudioFactory;
import de.p2tools.mtplayer.controller.load.LoadFilmFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneFilmLoad {

    private final P2ToggleSwitch tglUseLive = new P2ToggleSwitch("");
    private final P2ToggleSwitch tglUseAudio = new P2ToggleSwitch("");
    private final P2ToggleSwitch tglLoadFilm = new P2ToggleSwitch("Filmliste/Audioliste beim Programmstart neu laden");
    private final P2ToggleSwitch tglLoadNewList = new P2ToggleSwitch("Neue Filmlisten immer sofort laden");
    private final BooleanProperty diacriticChanged;

    private final ProgData progData;
    private final Stage stage;

    public PaneFilmLoad(Stage stage, ProgData progData, BooleanProperty diacriticChanged) {
        this.stage = stage;
        this.diacriticChanged = diacriticChanged;
        this.progData = progData;
    }

    public void close() {
        tglUseLive.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_USE_LIVE);
        tglUseAudio.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_USE_AUDIOLIST);
        tglLoadFilm.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART);
        tglLoadNewList.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_NEW_FILMLIST_IMMEDIATELY);
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        tglUseLive.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_USE_LIVE);
        tglUseAudio.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_USE_AUDIOLIST);
        tglLoadFilm.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_ON_PROGRAMSTART);
        tglLoadNewList.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_NEW_FILMLIST_IMMEDIATELY);

        final Button btnHelpLive = P2Button.helpButton(stage, "Live-Suche im Programm verwenden",
                HelpText.USE_LIVE);

        final Button btnHelpUse = P2Button.helpButton(stage, "Audioliste im Programm verwenden",
                HelpText.USE_AUDIOLIST);

        final Button btnHelpLoadFilm = P2Button.helpButton(stage, "Filmliste/Audioliste laden",
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


        Button btnLoadAudio = new Button("_Audioliste mit diesen Einstellungen neu laden");
        btnLoadAudio.setTooltip(new Tooltip("Eine komplette neue Audioliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Audioliste werden so sofort übernommen"));
        btnLoadAudio.setOnAction(event -> {
            LoadAudioFactory.loadAudioListFromWeb(true, true);
        });
        btnLoadAudio.setMaxWidth(Double.MAX_VALUE);
        btnLoadAudio.disableProperty().bind(tglUseAudio.selectedProperty().not());
        HBox.setHgrow(btnLoadAudio, Priority.ALWAYS);

        Button btnLoadFilm = new Button("_Filmliste mit diesen Einstellungen neu laden");
        btnLoadFilm.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoadFilm.setOnAction(event -> {
            LoadFilmFactory.loadFilmListFromWeb(true, true);
        });
        btnLoadFilm.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnLoadFilm, Priority.ALWAYS);

        HBox hBoxBtn = new HBox(5);
        hBoxBtn.setAlignment(Pos.CENTER_RIGHT);
        hBoxBtn.getChildren().addAll(btnLoadAudio, btnLoadFilm);


        Separator sp2 = new Separator();
        sp2.getStyleClass().add("pseperator2");
        sp2.setMinHeight(0);

        int row = 0;
        gridPane.add(P2Text.getTextBold("ARD-Audiothek im Programm verwenden"), 0, row, 2, 1);
        gridPane.add(tglUseAudio, 1, row);
        gridPane.add(btnHelpUse, 2, row);

        gridPane.add(P2Text.getTextBold("Live-Suche im Programm verwenden"), 0, ++row, 2, 1);
        gridPane.add(tglUseLive, 1, row);
        gridPane.add(btnHelpLive, 2, row);

        gridPane.add(new Label(), 0, ++row, 2, 1);
        gridPane.add(tglLoadFilm, 0, ++row, 2, 1);
        gridPane.add(btnHelpLoadFilm, 2, row);

        gridPane.add(tglLoadNewList, 0, ++row, 2, 1);
        gridPane.add(btnHelpNewList, 2, row);

        gridPane.add(tglRemoveDiacritic, 0, ++row, 2, 1);
        gridPane.add(btnHelpDia, 2, row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize());

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        hBox.getChildren().add(new Label("Änderungen in diesem Tab\n" +
                "wirken sich erst nach dem Neuladen einer Film/Audio-Liste aus"));

        final VBox vBox = new VBox(P2LibConst.PADDING);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
        vBox.getChildren().addAll(hBox, gridPane, P2GuiTools.getVBoxGrower(), hBoxBtn);

        TitledPane tpConfig = new TitledPane("Filme/Audios laden", vBox);
        result.add(tpConfig);
        return tpConfig;
    }
}