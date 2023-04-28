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

package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
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
            Platform.runLater(() -> {
                //dadurch schließt sich dieser Dialog schon mal
                ImportSetDialogController importSetDialogController = new ImportSetDialogController(progData);
                importSetDialogController.close();
            });
            close();
        });

        Text textHeaderSave = new Text("Kein Set zum Aufzeichnen!");
        textHeaderSave.setFont(Font.font(null, FontWeight.BOLD, -1));
        textHeaderSave.getStyleClass().add("downloadGuiMediaText");

        Text textHeaderPlay = new Text("Kein Videoplayer zum Abspielen!");
        textHeaderPlay.setFont(Font.font(null, FontWeight.BOLD, -1));
        textHeaderPlay.getStyleClass().add("downloadGuiMediaText");

        Text textHeaderAbo = new Text("Kein Set zum Aufzeichnen der Abos!");
        textHeaderAbo.setFont(Font.font(null, FontWeight.BOLD, -1));
        textHeaderAbo.getStyleClass().add("downloadGuiMediaText");

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
        getVBoxCont().setSpacing(20);

        switch (text) {
            case SAVE:
                getVBoxCont().getChildren().addAll(textHeaderSave, textArea);
                textArea.setText(
                        "Es ist kein Set von Programmen zum" + P2LibConst.LINE_SEPARATOR +
                                "Aufzeichnen der Filme angelegt." + P2LibConst.LINE_SEPARATORx2 +
                                txtAdd);
                break;
            case PLAY:
                getVBoxCont().getChildren().addAll(textHeaderPlay, textArea);
                textArea.setText(
                        "Es ist kein Videoplayer zum Abspielen" + P2LibConst.LINE_SEPARATOR +
                                "der Filme angelegt." + P2LibConst.LINE_SEPARATORx2 +
                                txtAdd);
                break;
            case ABO:
                getVBoxCont().getChildren().addAll(textHeaderAbo, textArea);
                textArea.setText(
                        "Es ist kein Set von Programmen zum" + P2LibConst.LINE_SEPARATOR +
                                "Aufzeichnen der Abos angelegt." + P2LibConst.LINE_SEPARATORx2 +
                                txtAdd);
        }

        addOkButton(btnImport);
        addCancelButton(btnCancel);
    }
}
