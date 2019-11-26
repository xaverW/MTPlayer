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
import de.p2tools.p2Lib.dialog.PDialog;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class NoSetDialogController extends PDialog {

    final ProgData progData;
    final TEXT text;
    VBox vbox;
    Button btnCancel = new Button("Abbrechen");
    Button btnImport = new Button("Standarsets wieder herstellen");

    public enum TEXT {SAVE, PLAY, ABO}

    public NoSetDialogController(ProgData progData, TEXT text) {
        super(null,
                text == TEXT.SAVE || text == TEXT.ABO ? "Set zum Speichern" : "Kein Videoplayer!", true);

        this.progData = progData;
        this.text = text;

        vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(30);

        init(vbox, true);
    }


    @Override
    public void make() {
        btnCancel.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
        btnCancel.setOnAction(a -> close());

        btnImport.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
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

        VBox vBoxCont = new VBox();
        vBoxCont.setSpacing(20);
        vBoxCont.getStyleClass().add("dialog-only-border");
        VBox.setVgrow(vBoxCont, Priority.ALWAYS);

        final int prefRowCount = 14;
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setPrefRowCount(prefRowCount);
        VBox.setVgrow(textArea, Priority.ALWAYS);
        vBoxCont.getChildren().addAll(textHeaderSave, textArea);


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


        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().addAll(btnImport, btnCancel);

        vbox.getChildren().addAll(vBoxCont, hBox);
    }

    private void importSet() {
        Platform.runLater(() -> new ImportSetDialogController(progData));
    }
}
