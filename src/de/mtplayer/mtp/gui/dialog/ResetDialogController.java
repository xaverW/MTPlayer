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


import de.mtplayer.mLib.tools.BigButton;
import de.mtplayer.mtp.controller.ProgQuit;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.tools.file.GetFile;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.dialog.PDialog;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ResetDialogController extends PDialog {

    final ProgData progData;
    final StackPane stackPane;
    final VBox vbox;

    public ResetDialogController(ProgData progData) {
        super("", null,
                "Programm zurücksetzen", true);

        this.progData = progData;

        stackPane = new StackPane();
        vbox = new VBox();

        init(vbox, true);
    }


    @Override
    public void make() {
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(30);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(25);


        Label headerLabel = new Label("Einstellungen können komplett oder\n" +
                "nur die Sets zum Abspielen/Speichern\n" +
                "zurückgesetzt werden!");
        headerLabel.setStyle("-fx-font-size: 1.5em;");


        // Set zurücksetzen
        BigButton cancelButton = new BigButton(new Icons().ICON_BUTTON_QUIT,
                "Nichts ändern", "");
        cancelButton.setOnAction(e -> close());

        final Button btnHelp = new Button("");
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> PAlert.showHelpAlert("Programm zurücksetzen",
                new GetFile().getHelpSearch(GetFile.PATH_HELPTEXT_RESET)));

        BigButton setButton = new BigButton(new Icons().ICON_BUTTON_QUIT,
                "Einstellungen zum Abspielen/Aufzeichnen zurücksetzen",
                "Es werden alle Programmsets (auch eigene)\n" +
                        "zum Abspielen und Aufzeichnen gelöscht\n" +
                        "und die Standardsets wieder angelegt." +
                        "\n\n" +
                        "Abos und Blacklist bleiben erhalten.");
        setButton.setOnAction(e -> {
            Platform.runLater(() -> new ImportSetDialogController(progData));
            close();
        });


        // alle Einstellungen
        BigButton allButton = new BigButton(new Icons().ICON_BUTTON_QUIT, "" +
                "Alle Einstellungen zurücksetzen!",
                "Alle Einstellungen gehen verloren.\n\n" +
                        "ACHTUNG\n" +
                        "es werden auch eigene Buttons, Abos\n" +
                        "und die Blacklist gelöscht.");
        allButton.setOnAction(e -> {
            Text t = new Text("ALLE");
            t.setFont(Font.font(null, FontWeight.BOLD, -1));

            TextFlow tf = new TextFlow();
            tf.getChildren().addAll(new Text("Es werden "), t, new Text(" von Ihnen erzeugten Änderungen gelöscht.\n\n" +
                    "Möchten Sie wirklich alle Einstellungen zurücksetzen?"));

            if (PAlert.showAlert_yes_no_cancel("Einstellungen zurücksetzen",
                    "alle Einstellungen zurücksetzen!", tf, false) == PAlert.BUTTON.YES) {
                // damit wird vor dem Beenden das Konfig-Verzeichnis umbenannt und so startet das
                // Programm wie beim ersten Start
                ProgData.reset = true;
                new ProgQuit().quit(false, false);
            }
        });


        gridPane.add(new Icons().ICON_DIALOG_QUIT, 0, 0, 1, 1);
        gridPane.add(headerLabel, 1, 0);
        gridPane.add(cancelButton, 1, 1);
        gridPane.add(btnHelp, 2, 1);
        gridPane.add(setButton, 1, 2);
        gridPane.add(allButton, 1, 3);


        ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
        gridPane.getStyleClass().add("dialog-only-border");

        vbox.getChildren().addAll(gridPane);
    }


}