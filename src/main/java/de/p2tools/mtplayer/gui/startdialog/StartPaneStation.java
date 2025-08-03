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
import de.p2tools.mtplayer.controller.data.film.FilmToolsFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadConst;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class StartPaneStation {

    private final Button btnClearAll = new Button("_Wieder alle Sender laden");
    private final Stage stage;

    public StartPaneStation(Stage stage) {
        this.stage = stage;
    }

    public void close() {
    }

    public TitledPane make() {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
        makeSender(vBox);

        return new TitledPane("Filmliste bereits beim Laden filtern", vBox);
    }

    private void makeSender(VBox vBox) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Hier können Sender die *nicht* interessieren, beim Laden " +
                "der Filmliste, ausgenommen werden.");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(5), hBox, P2GuiTools.getVDistance(20));


        final Button btnHelpSender = P2Button.helpButton(stage, "Filmliste beim Laden filtern",
                HelpText.LOAD_FILMLIST_SENDER_STARTDIALOG);
        HBox hBoxStation = new HBox(P2LibConst.DIST_BUTTON);
        hBoxStation.setAlignment(Pos.CENTER_LEFT);
        Label lblStation = new Label("Diese Sender  *nicht*  laden:");
        hBoxStation.getChildren().addAll(lblStation, P2GuiTools.getHBoxGrower(), btnClearAll, btnHelpSender);
        vBox.getChildren().add(hBoxStation);

        final TilePane tilePaneSender = getTilePaneSender();
        vBox.getChildren().addAll(tilePaneSender);
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
