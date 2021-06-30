/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.configDialog.setData;

import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.film.Film;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class DownloadPane {
    private final TextField txtPrefix = new TextField();
    private final TextField txtSuffix = new TextField();
    private final RadioButton rbHd = new RadioButton("Film in HD laden");
    private final RadioButton rbHeight = new RadioButton("Film in hoher Auflösung laden");
    private final RadioButton rbLow = new RadioButton("Film in kleiner Auflösung laden");
    private final PToggleSwitch tglInfo = new PToggleSwitch("Infodatei anlegen: \"Filmname.txt\"");
    private final PToggleSwitch tglSubtitle = new PToggleSwitch("Untertitel speichern: \"Filmname.xxx\"");

    private final Stage stage;
    private SetData setData = null;

    DownloadPane(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        unBindProgData();
    }

    public void makePane(Collection<TitledPane> result) {
        VBox vBox = new VBox(25);
        vBox.setPadding(new Insets(10));

        TitledPane tpConfig = new TitledPane("Download", vBox);
        result.add(tpConfig);

        makePraef(vBox);
        makeReolution(vBox);
        makeInfo(vBox);
    }

    private void makePraef(VBox vBox) {
        // praefix/suffix
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(10);
        vBox.getChildren().add(gridPane);

        final Button btnHelpPraefix = PButton.helpButton(stage, "Direkt speichern",
                HelpText.SETDATA_PRAEFIX);
        final Label lbl = new Label("direkt Speichern (Download durch dieses Programm):");

        int row = 0;
        gridPane.add(lbl, 0, row, 2, 1);
        gridPane.add(btnHelpPraefix, 2, row);

        gridPane.add(new Label("Präfix (z.B. http):"), 0, ++row);
        gridPane.add(txtPrefix, 1, row);

        gridPane.add(new Label("Suffix (z.B. mp4,mp3):"), 0, ++row);
        gridPane.add(txtSuffix, 1, row);

        gridPane.getColumnConstraints().addAll(
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
    }

    private void makeReolution(VBox vBox) {
        // Auflösung
        ToggleGroup tg = new ToggleGroup();
        rbHd.setToggleGroup(tg);
        rbHeight.setToggleGroup(tg);
        rbLow.setToggleGroup(tg);
        rbHd.setOnAction(event -> setResolution());
        rbHeight.setOnAction(event -> setResolution());
        rbLow.setOnAction(event -> setResolution());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(10);
        vBox.getChildren().add(gridPane);

        final Button btnHelpRes = PButton.helpButton(stage, "Auflösung",
                HelpText.SETDATA_RES);

        int row = 0;
        gridPane.add(rbHd, 0, ++row);//Platz nach oben!
        gridPane.add(btnHelpRes, 1, row);
        gridPane.add(rbHeight, 0, ++row);
        gridPane.add(rbLow, 0, ++row);

        gridPane.getColumnConstraints().addAll(
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrowRight());
    }

    private void makeInfo(VBox vBox) {
        // Infodateien
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(10);

        int row = 0;
        gridPane.add(tglInfo, 0, ++row);//Platz nach oben!
        gridPane.add(tglSubtitle, 0, ++row);
        vBox.getChildren().add(gridPane);
    }

    private void setResolution() {
        if (rbHeight.isSelected()) {
            setData.setResolution(Film.RESOLUTION_NORMAL);
        }
        if (rbHd.isSelected()) {
            setData.setResolution(Film.RESOLUTION_HD);
        }
        if (rbLow.isSelected()) {
            setData.setResolution(Film.RESOLUTION_SMALL);
        }
    }

    public void bindProgData(SetData setData) {
        unBindProgData();

        this.setData = setData;
        if (setData != null) {
            txtPrefix.textProperty().bindBidirectional(setData.prefixProperty());
            txtSuffix.textProperty().bindBidirectional(setData.suffixProperty());

            switch (setData.getResolution()) {
                case Film.RESOLUTION_HD:
                    rbHd.setSelected(true);
                    break;
                case Film.RESOLUTION_SMALL:
                    rbLow.setSelected(true);
                    break;
                default:
                    rbHeight.setSelected(true);
                    break;
            }

            tglInfo.selectedProperty().bindBidirectional(setData.infoFileProperty());
            tglSubtitle.selectedProperty().bindBidirectional(setData.subtitleProperty());
        }
    }

    void unBindProgData() {
        if (setData != null) {
            txtPrefix.textProperty().unbindBidirectional(setData.prefixProperty());
            txtSuffix.textProperty().unbindBidirectional(setData.suffixProperty());

            tglInfo.selectedProperty().unbindBidirectional(setData.infoFileProperty());
            tglSubtitle.selectedProperty().unbindBidirectional(setData.subtitleProperty());
        }
    }
}
