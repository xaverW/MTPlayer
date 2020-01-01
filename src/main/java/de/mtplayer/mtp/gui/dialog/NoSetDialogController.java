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

package de.mtplayer.mtp.gui.dialog;

import de.mtplayer.mtp.controller.config.ProgData;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class NoSetDialogController extends PDialogExtra {

    final ProgData progData;
    final TEXT text;
    Button btnCancel = new Button("_Abbrechen");
    Button btnImport = new Button("_Standarsets wieder herstellen");

    public enum TEXT {SAVE, PLAY, ABO}

    public NoSetDialogController(ProgData progData, TEXT text) {
        super(progData.primaryStage, null,
                text == TEXT.SAVE || text == TEXT.ABO ? "Set zum Speichern" : "Kein Videoplayer!", true, false);

        this.progData = progData;
        this.text = text;
        init(true);
    }


    @Override
    public void make() {
        btnCancel.setOnAction(a -> close());

        btnImport.setOnAction(event -> {
            importSet();
            close();
        });

        Text textHeaderSave = new Text("Kein Set zum Aufzeichnen!");
        textHeaderSave.setFont(Font.font(null, FontWeight.BOLD, -1));

        Text textHeaderPlay = new Text("Kein Videoplayer zum Abspielen!");
        textHeaderPlay.setFont(Font.font(null, FontWeight.BOLD, -1));

        Text textHeaderAbo = new Text("Kein Set zum Aufzeichnen der Abos!");
        textHeaderAbo.setFont(Font.font(null, FontWeight.BOLD, -1));

        final String txtAdd = "Im Menü Einstellungen unter" + P2LibConst.LINE_SEPARATOR +

                "   ->Aufzeichnen und Abspielen" + P2LibConst.LINE_SEPARATOR +

                "die Programme zum Abspielen von Filmen und " + P2LibConst.LINE_SEPARATOR +
                "Aufzeichnen von Abos korrigieren." + P2LibConst.LINE_SEPARATORx3 +

                "Oder die Einstellungen zurücksetzen und" + P2LibConst.LINE_SEPARATOR +
                "die Standardsets wieder herstellen.";


        final int prefRowCount = 14;
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setPrefRowCount(prefRowCount);
        VBox.setVgrow(textArea, Priority.ALWAYS);
        getvBoxCont().getChildren().addAll(textHeaderSave, textArea);
        getvBoxCont().setSpacing(20);


        switch (text) {
            case SAVE:
                textArea.setText(
                        "Es ist kein Set von Programmen zum" + P2LibConst.LINE_SEPARATOR +
                                "Aufzeichnen der Filme angelegt." + P2LibConst.LINE_SEPARATORx2 +
                                txtAdd);
                break;
            case PLAY:
                textArea.setText(
                        "Es ist kein Videoplayer zum Abspielen" + P2LibConst.LINE_SEPARATOR +
                                "der Filme angelegt." + P2LibConst.LINE_SEPARATORx2 +
                                txtAdd);
                break;
            case ABO:
                textArea.setText(
                        "Es ist kein Set von Programmen zum" + P2LibConst.LINE_SEPARATOR +
                                "Aufzeichnen der Abos angelegt." + P2LibConst.LINE_SEPARATORx2 +
                                txtAdd);
        }

        addOkButton(btnImport);
        addCancelButton(btnCancel);
    }

    private void importSet() {
        Platform.runLater(() -> new ImportSetDialogController(progData));
    }
}
