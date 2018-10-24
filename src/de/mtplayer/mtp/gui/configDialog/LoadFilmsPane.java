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
import de.p2tools.p2Lib.guiTools.PButton;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class LoadFilmsPane {

    private final Slider slDays = new Slider();
    private final Label lblDays = new Label("");
    private final int FILTER_DAYS_MAX = 300;
    private final Stage stage;
    private final ProgData progData;
    StringProperty propChannel = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getStringProperty();
    IntegerProperty propDay = ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getIntegerProperty();

    public LoadFilmsPane(Stage stage) {
        this.stage = stage;
        this.progData = null;
    }

    public LoadFilmsPane(Stage stage, ProgData progData) {
        this.stage = stage;
        this.progData = progData;
    }

    public TitledPane make() {
        final VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(25, 20, 20, 20));

        initDays();
        final Button btnHelpDays = new PButton().helpButton(stage, "nur Filme der letzten Tage laden",
                HelpText.LOAD_FILM_ONLY_DAYS);

        HBox hBoxLoad1 = new HBox();
        hBoxLoad1.setAlignment(Pos.CENTER_LEFT);
        hBoxLoad1.getChildren().add(lblDays);
        HBox.setHgrow(hBoxLoad1, Priority.ALWAYS);

        HBox hBoxLoad2 = new HBox(10);
        hBoxLoad2.setAlignment(Pos.CENTER);
        hBoxLoad2.getChildren().addAll(new Label("Filme laden:"), slDays, hBoxLoad1, btnHelpDays);

        vBox.getChildren().addAll(new Label("nur aktuelle Filme laden"), hBoxLoad2);


        final TilePane tilePane = new TilePane();
        tilePane.setHgap(10);
        tilePane.setVgap(10);

        ArrayList aListChannel = new ArrayList(Arrays.asList(propChannel.getValue().split(",")));
        ArrayList<CheckBox> aListCb = new ArrayList<>();

        for (String s : ProgConst.SENDER) {
            final CheckBox cb = new CheckBox(s);
            aListCb.add(cb);
            cb.setSelected(aListChannel.contains(s));
            cb.setOnAction(a -> makePropSender(aListCb));

            tilePane.getChildren().add(cb);
            TilePane.setAlignment(cb, Pos.CENTER_LEFT);
        }


        final Button btnHelpSender = new PButton().helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_SENDER);

        HBox hBoxSender1 = new HBox(10);
        hBoxSender1.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(hBoxSender1, Priority.ALWAYS);
        hBoxSender1.getChildren().add(btnHelpSender);

        HBox hBoxSender2 = new HBox(10);
        hBoxSender2.setAlignment(Pos.CENTER);
        hBoxSender2.getChildren().addAll(new Label("diese Sender nicht laden"), hBoxSender1);

        vBox.getChildren().addAll(new Label(" "), hBoxSender2);
        vBox.getChildren().add(tilePane);


        if (progData != null) {
            // im Startdialog brauchts das noch nicht
            Button btnLoad = new Button("Filmliste jetzt laden");
            btnLoad.setOnAction(event -> {
                progData.loadFilmlist.loadFilmlist(true);
            });

            HBox hBoxLoadBtn = new HBox();
            hBoxLoadBtn.setAlignment(Pos.CENTER_LEFT);
            hBoxLoadBtn.getChildren().add(btnLoad);

            VBox vBoxLoadBtn = new VBox();
            vBoxLoadBtn.setAlignment(Pos.BOTTOM_LEFT);
            VBox.setVgrow(vBoxLoadBtn, Priority.ALWAYS);
            vBoxLoadBtn.getChildren().add(hBoxLoadBtn);
            vBox.getChildren().add(vBoxLoadBtn);
        }

        TitledPane tpConfig = new TitledPane("Filme und Sender laden", vBox);
        return tpConfig;
    }

    private void initDays() {
        slDays.setMin(0);
        slDays.setMax(FILTER_DAYS_MAX);
        slDays.setShowTickLabels(false);
        slDays.setMajorTickUnit(10);
        slDays.setBlockIncrement(10);

        slDays.valueProperty().bindBidirectional(propDay);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());

        setValueSlider();
    }

    private void setValueSlider() {
        int days = (int) slDays.getValue();
        lblDays.setText(days == 0 ? "alles laden" : "nur Filme der letzten " + days + " Tage laden");
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
    }
}
