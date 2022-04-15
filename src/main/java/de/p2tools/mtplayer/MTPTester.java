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


package de.p2tools.mtplayer;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.film.FilmlistFactory;
import de.p2tools.p2Lib.dialogs.ProgInfoDialog;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashSet;

public class MTPTester {
    private final ProgInfoDialog progInfoDialog;
    private final HashSet<String> hashSet = new HashSet<>();
    private final ProgData progData;
    private final TextArea textArea = new TextArea();
    private String text = "";
    private final PMaskerPane maskerPane = new PMaskerPane();

    public MTPTester(final ProgData progData) {
        this.progData = progData;
        this.progInfoDialog = new ProgInfoDialog(false);
        addProgTest();
    }

    public void showDialog() {
        progInfoDialog.showDialog();
    }

    private void addProgTest() {
        if (progInfoDialog != null) {

            final GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setPadding(new Insets(10));
            gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

            maskerPane.setMaskerVisible(false);
            maskerPane.setButtonText("Abbrechen");
            maskerPane.getButton().setOnAction(a -> close());

            final StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(gridPane, maskerPane);
            progInfoDialog.getvBoxCont().getChildren().addAll(stackPane);


            // Create the ButtonBar instance
            final ButtonBar buttonBar = new ButtonBar();
            final Button okButton = new Button("OK");
            ButtonBar.setButtonData(okButton, ButtonBar.ButtonData.OK_DONE);
            final Button cButton = new Button("Abbrechen");
            ButtonBar.setButtonData(cButton, ButtonBar.ButtonData.CANCEL_CLOSE);
            buttonBar.getButtons().addAll(okButton, cButton);
            progInfoDialog.getvBoxCont().getChildren().add(buttonBar);


            final Text text = new Text("Debugtools");
            text.setFont(Font.font(null, FontWeight.BOLD, 15));

            Button btnMarkFilm = new Button("Diakrit");
            btnMarkFilm.setMaxWidth(Double.MAX_VALUE);
            btnMarkFilm.setOnAction(a -> check());

            int row = 0;
            gridPane.add(text, 0, row, 2, 1);
            gridPane.add(btnMarkFilm, 0, ++row);

            gridPane.add(textArea, 0, ++row, 2, 1);

        }
    }

    String test = "äöü ń ǹ ň ñ ṅ ņ ṇ ṋ    ( ç/č/c => c; a/á/à/â/ă/ȁ/å/ā/ã => a aber ä => ä )";

    private void check() {
        PDuration.counterStart("MTPTester diakritische Zeichen");

        ProgConfig.SYSTEM_SHOW_DIACRITICS.setValue(ProgConfig.SYSTEM_SHOW_DIACRITICS.getValue());

        FilmlistFactory.setDiacritic(false);

//        System.out.println(test);
//        System.out.println(DiacriticFactory.flattenDiacritic(test));

        PDuration.counterStop("MTPTester diakritische Zeichen");
    }

    public void close() {
        maskerPane.switchOffMasker();
    }
}
