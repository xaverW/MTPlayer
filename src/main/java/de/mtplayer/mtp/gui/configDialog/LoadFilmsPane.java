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
import de.mtplayer.mtp.controller.filmlist.LoadFactory;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class LoadFilmsPane {

    private final Slider slDays = new Slider();
    private final Slider slDuration = new Slider();
    private final Label lblDays = new Label("");
    private final Label lblDuration = new Label("");
    final Button btnClearall = new Button("_wieder alle Sender laden");
    private final Stage stage;
    private final ProgData progData;

    StringProperty propChannel = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getStringProperty();
    IntegerProperty propDay = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getIntegerProperty();
    IntegerProperty propDuration = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getIntegerProperty();

    public LoadFilmsPane(Stage stage) {
        this.stage = stage;
        this.progData = null;
    }

    public LoadFilmsPane(Stage stage, ProgData progData) {
        this.stage = stage;
        this.progData = progData;
    }

    public void close() {
        slDays.valueProperty().unbindBidirectional(propDay);
        slDuration.valueProperty().unbindBidirectional(propDuration);
    }

    public TitledPane make() {
        return make(null);
    }

    public TitledPane make(Collection<TitledPane> result) {

        final Button btnHelpDays = PButton.helpButton(stage, "Filmliste beim Laden filtern",
                HelpText.LOAD_ONLY_FILMS);
        final Button btnHelpSender = PButton.helpButton(stage, "Filmliste beim Laden filtern",
                HelpText.LOAD_FILMLIST_SENDER);

        initSlider();

        final TilePane tilePaneSender = new TilePane();
        tilePaneSender.setHgap(10);
        tilePaneSender.setVgap(10);
        ArrayList aListChannel = LoadFactory.getSenderListNotToLoad();
        ArrayList<CheckBox> aListCb = new ArrayList<>();
        for (String s : ProgConst.SENDER) {
            final CheckBox cb = new CheckBox(s);
            aListCb.add(cb);
            cb.setSelected(aListChannel.contains(s));
            cb.setOnAction(a -> {
                makePropSender(aListCb);
                LoadFactory.checkAllSenderSelectedNotToLoad(stage);
            });

            tilePaneSender.getChildren().add(cb);
            TilePane.setAlignment(cb, Pos.CENTER_LEFT);
        }

        btnClearall.setMinWidth(Region.USE_PREF_SIZE);
        btnClearall.setOnAction(a -> {
            aListCb.stream().forEach(checkBox -> checkBox.setSelected(false));
            makePropSender(aListCb);
        });
        checkPropSender(aListCb);

        final VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(20));

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(0));
        Button btnEmty = new Button(" "); // ist nur für die Zeilenhöhe
        btnEmty.setVisible(false);

        int row = 0;
        gridPane.add(new Label("nur Filme der letzten Tage laden:"), 0, row, 2, 1);
        gridPane.add(btnEmty, 3, row);
        gridPane.add(new Label("Filme laden:"), 0, ++row);
        gridPane.add(slDays, 1, row);
        gridPane.add(lblDays, 2, row);
        gridPane.add(btnHelpDays, 3, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("nur Filme mit Mindestlänge laden:"), 0, ++row, 2, 1);
        gridPane.add(new Label("Filme laden:"), 0, ++row);
        gridPane.add(slDuration, 1, row);
        gridPane.add(lblDuration, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());

        vBox.getChildren().add(gridPane);

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label("diese Sender  *nicht*  laden:");
        lbl.setMaxWidth(Double.MAX_VALUE);
        hBox.getChildren().addAll(lbl, btnClearall, btnHelpSender);
        HBox.setHgrow(lbl, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label(" "), hBox);

        hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(tilePaneSender);
        HBox.setHgrow(tilePaneSender, Priority.ALWAYS);
        vBox.getChildren().addAll(hBox);

        if (progData != null) {
            // im Startdialog brauchts das noch nicht
            Button btnLoad = new Button("Filmliste _jetzt laden");
            btnLoad.setOnAction(event -> {
                progData.loadFilmlist.loadNewFilmlistFromServer(true);
            });

            hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().add(btnLoad);
            vBox.getChildren().addAll(new Label(" "), hBox);
        }

        TitledPane tpConfig = new TitledPane("Filmliste bereits beim Laden filtern", vBox);
        if (result != null) {
            result.add(tpConfig);
        }
        return tpConfig;
    }

    private void initSlider() {
        slDays.setMin(0);
        slDays.setMax(ProgConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDays.setShowTickLabels(false);
        slDays.setMajorTickUnit(100);
        slDays.setBlockIncrement(5);

        slDays.valueProperty().bindBidirectional(propDay);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());

        slDuration.setMin(0);
        slDuration.setMax(ProgConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION);
        slDuration.setShowTickLabels(false);
        slDuration.setMajorTickUnit(10);
        slDuration.setBlockIncrement(1);

        slDuration.valueProperty().bindBidirectional(propDuration);
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

        btnClearall.setDisable(noneChecked);
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
        propChannel.setValue(str);
        checkPropSender(aListCb);
    }
}
