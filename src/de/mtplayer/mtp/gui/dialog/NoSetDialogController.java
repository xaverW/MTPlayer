/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

import de.mtplayer.mtp.controller.config.Daten;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class NoSetDialogController extends MTDialog {

    final Daten daten;
    final TEXT text;
    VBox vbox, vBoxCont;
    Button btnCancel = new Button("Abbrechen");
    Button btnImport = new Button("Set importieren");

    public enum TEXT {SAVE, PLAY, ABO}

    public NoSetDialogController(Daten daten, TEXT text) {
        super("", null,
                text == TEXT.SAVE || text == TEXT.ABO ? "Set zum Speichern" : "Kein Videoplayer!", true);

        this.daten = daten;
        this.text = text;

        vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(30);

        vBoxCont = new VBox();
        vBoxCont.setSpacing(20);
        vBoxCont.getStyleClass().add("dialog-only-border");

        init(vbox, true);
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

        Text textContSave = new Text(
                "Ein Set von Programmen zum Aufzeichnen\n" +
                        "wurde nicht angelegt.\n\n" +
                        "Im Menü unter:\n" +
                        "    ->Einstellungen->Aufzeichnen und Abspielen\n\n" +
                        "ein Programm zum Aufzeichnen festlegen.\n\n\n" +
                        "Oder jetzt die Standardsets importieren.");
        Text textContPlay = new Text(
                "Ein Videoplayer zum Abspielen\n" +
                        "wurde nicht angelegt.\n\n" +
                        "Im Menü unter:\n" +
                        "    ->Einstellungen->Aufzeichnen und Abspielen\n\n" +
                        "einen Videoplayer zum Abspielen festlegen.\n\n\n" +
                        "Oder jetzt die Standardsets importieren.");

        Text textContAbo = new Text(
                "Ein Set von Programmen zum Aufzeichnen\n" +
                        "der Abos wurde nicht angelegt.\n\n" +
                        "Im Menü unter:\n" +
                        "    ->Einstellungen->Aufzeichnen und Abspielen\n\n" +
                        "ein Programm zum Aufzeichnen von Abos festlegen.\n\n\n" +
                        "Oder jetzt die Standardsets importieren.");

        switch (text) {
            case SAVE:
                vBoxCont.getChildren().addAll(textHeaderSave, textContSave);
                break;
            case PLAY:
                vBoxCont.getChildren().addAll(textHeaderPlay, textContPlay);
                break;
            case ABO:
                vBoxCont.getChildren().addAll(textHeaderAbo, textContAbo);
        }

        VBox.setVgrow(vBoxCont, Priority.ALWAYS);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().addAll(btnImport, btnCancel);

        vbox.getChildren().addAll(vBoxCont, hBox);
    }

    private void importSet() {
        Platform.runLater(() -> new ImportSetDialogController(daten));
    }
}
