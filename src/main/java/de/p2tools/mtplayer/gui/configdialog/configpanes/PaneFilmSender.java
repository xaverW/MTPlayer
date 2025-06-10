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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.film.FilmToolsFactory;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadConst;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class PaneFilmSender {

    private final Slider slDays = new Slider();
    private final Slider slDuration = new Slider();
    private final Label lblDays = new Label("");
    private final Label lblDuration = new Label("");
    final Button btnClearAll = new Button("_Wieder alle Sender laden");
    private final Stage stage;

    public PaneFilmSender(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        slDays.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDuration.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION);
    }

    public TitledPane make(Collection<TitledPane> result) {
        initSlider();
        final VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(P2LibConst.PADDING));

        makeOnly(vBox);
        makeSender(vBox);

        TitledPane tpConfig = new TitledPane("Filmliste bereits beim Laden filtern", vBox);
        if (result != null) {
            result.add(tpConfig);
        }
        return tpConfig;
    }

    private void makeOnly(VBox vBox) {
        final Button btnHelpDouble = P2Button.helpButton(stage, "Filmliste beim Laden filtern",
                HelpText.LOAD_FILMLIST_ONLY_MARK_DOUBLE);
        final Button btnHelpDays = P2Button.helpButton(stage, "Filmliste beim Laden filtern",
                HelpText.LOAD_ONLY_FILMS);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));

        int row = 0;
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

    private void makeSender(VBox vBox) {
        final Button btnHelpSender = P2Button.helpButton(stage, "Filmliste beim Laden filtern",
                HelpText.LOAD_FILMLIST_SENDER);
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label("Diese Sender  *nicht*  laden:");
        hBox.getChildren().addAll(lbl, P2GuiTools.getHBoxGrower(), btnClearAll, btnHelpSender);

        vBox.getChildren().add(new Label(" "));
        vBox.getChildren().add(hBox);

        final TilePane tilePaneSender = getTilePaneSender();
        vBox.getChildren().addAll(tilePaneSender);

        Button btnLoad = new Button("_Filmliste mit diesen Einstellungen neu laden");
        btnLoad.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoad.setOnAction(event -> {
            LoadFilmFactory.loadNewListFromWeb(true);
        });

        hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnLoad);
        vBox.getChildren().addAll(P2GuiTools.getHDistance(P2LibConst.DIST_BUTTON), hBox);
    }

    private TilePane getTilePaneSender() {
        final TilePane tilePaneSender = new TilePane();
        tilePaneSender.setHgap(5);
        tilePaneSender.setVgap(5);
        ArrayList<String> aListChannel = FilmToolsFactory.getSenderListNotToLoad();
        ArrayList<CheckBox> aListCb = new ArrayList<>();

        for (int i = 0; i < P2LoadConst.SENDER.length; ++i) {
            String s = P2LoadConst.SENDER[i];
            String s_ = P2LoadConst.SENDER_[i];

            final CheckBox cb = new CheckBox(s);
            cb.setTooltip(new Tooltip(s_));
            aListCb.add(cb);
            cb.setSelected(aListChannel.contains(s));
            cb.setOnAction(a -> {
                makePropSender(aListCb);
                // und noch prüfen, dass nicht alle ausgeschaltet sind
                FilmToolsFactory.checkAllSenderSelectedNotToLoad(stage);
            });

            tilePaneSender.getChildren().add(cb);
            TilePane.setAlignment(cb, Pos.CENTER_LEFT);
        }
        btnClearAll.setMinWidth(Region.USE_PREF_SIZE);
        btnClearAll.setOnAction(a -> {
            aListCb.stream().forEach(checkBox -> checkBox.setSelected(false));
            makePropSender(aListCb);
        });
        checkPropSender(aListCb);

        return tilePaneSender;
    }

    private void initSlider() {
        slDays.setMin(0);
        slDays.setMax(ProgConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDays.setShowTickLabels(false);
        slDays.setMajorTickUnit(100);
        slDays.setBlockIncrement(5);

        slDays.valueProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());

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

    private void checkPropSender(ArrayList<CheckBox> aListCb) {
        boolean noneChecked = true;
        for (CheckBox cb : aListCb) {
            if (cb.isSelected()) {
                noneChecked = false;
                break;
            }
        }

        btnClearAll.setDisable(noneChecked);
    }

    private void makePropSender(ArrayList<CheckBox> aListCb) {
        String str = "";
        for (CheckBox cb : aListCb) {
            if (!cb.isSelected()) {
                continue;
            }

            String s = cb.getText();
            str = str.isEmpty() ? s : str + "," + s;
        }
        ProgConfig.SYSTEM_LOAD_NOT_SENDER.setValue(str);
        checkPropSender(aListCb);
    }
}
