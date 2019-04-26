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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class LoadFilmsPane {

    private final Slider slDays = new Slider();
    private final Label lblDays = new Label("");
    final Button btnClearall = new Button("wieder alles laden");
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

        final Button btnHelpDays = PButton.helpButton(stage, "nur Filme der letzten Tage laden",
                HelpText.LOAD_FILM_ONLY_DAYS);

        initDays();
        final TilePane tilePane = new TilePane();
        tilePane.setHgap(10);
        tilePane.setVgap(10);

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

            tilePane.getChildren().add(cb);
            TilePane.setAlignment(cb, Pos.CENTER_LEFT);
        }


        final Button btnHelpSender = PButton.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_SENDER);

        btnClearall.setMinWidth(Region.USE_PREF_SIZE);
        btnClearall.setOnAction(a -> {
            aListCb.stream().forEach(checkBox -> checkBox.setSelected(false));
            makePropSender(aListCb);
        });
        checkPropSender(aListCb);


        final VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(25, 20, 20, 20));

        lblDays.setMinWidth(Region.USE_PREF_SIZE);
        HBox hBoxLoad1 = new HBox();
        hBoxLoad1.setAlignment(Pos.CENTER_LEFT);
        hBoxLoad1.getChildren().add(lblDays);
        HBox.setHgrow(hBoxLoad1, Priority.ALWAYS);

        slDays.setMinWidth(Region.USE_PREF_SIZE);
        HBox hBoxLoad2 = new HBox(10);
        hBoxLoad2.setAlignment(Pos.CENTER);
        Label lblLaden = new Label("Filme laden:");
        lblLaden.setMinWidth(Region.USE_PREF_SIZE);
        hBoxLoad2.getChildren().addAll(lblLaden, slDays, hBoxLoad1, btnHelpDays);

        Label lblAkt = new Label("nur Filme der letzten Tage laden");
        lblAkt.setMinWidth(Region.USE_PREF_SIZE);
        vBox.getChildren().addAll(lblAkt, hBoxLoad2);


        HBox hBoxSender1 = new HBox(10);
        hBoxSender1.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(hBoxSender1, Priority.ALWAYS);
        hBoxSender1.getChildren().addAll(btnHelpSender);

        HBox hBoxSender2 = new HBox(10);
        hBoxSender2.setAlignment(Pos.CENTER);
        hBoxSender2.getChildren().addAll(new Label("diese Sender  *nicht*  laden"), hBoxSender1);

        vBox.getChildren().addAll(new Label(" "), hBoxSender2);
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(tilePane, btnClearall);
        HBox.setHgrow(tilePane, Priority.ALWAYS);
        vBox.getChildren().add(hBox);


        if (progData != null) {
            // im Startdialog brauchts das noch nicht
            Button btnLoad = new Button("Filmliste jetzt laden");
            btnLoad.setOnAction(event -> {
                progData.loadFilmlist.loadNewFilmlistFromServer(true);
            });

            HBox hBoxLoadBtn = new HBox();
            hBoxLoadBtn.setAlignment(Pos.CENTER_LEFT);
            hBoxLoadBtn.getChildren().add(btnLoad);

            VBox vBoxLoadBtn = new VBox(25);
            vBoxLoadBtn.setAlignment(Pos.BOTTOM_LEFT);
            VBox.setVgrow(vBoxLoadBtn, Priority.ALWAYS);
            vBoxLoadBtn.getChildren().add(hBoxLoadBtn);
            vBox.getChildren().addAll(new VBox(25), hBoxLoadBtn);
        }

        TitledPane tpConfig = new TitledPane("Filme und Sender laden", vBox);

        return tpConfig;
    }

    private void initDays() {
        slDays.setMin(0);
        slDays.setMax(ProgConst.LOAD_FILMS_MAX_DAYS_MAX);
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
