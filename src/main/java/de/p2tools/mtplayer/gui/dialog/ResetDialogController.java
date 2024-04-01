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


import de.p2tools.mtplayer.controller.ProgQuit;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2BigButton;
import de.p2tools.p2lib.guitools.P2Button;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ResetDialogController extends P2DialogExtra {

    final ProgData progData;
    final StackPane stackPane;

    public ResetDialogController(ProgData progData) {
        super(progData.primaryStage, null, "Programm zurücksetzen",
                true, false);

        this.progData = progData;
        stackPane = new StackPane();

        init(true);
    }

    @Override
    public void make() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(25);

        Label headerLabel = new Label("Einstellungen können komplett oder" + P2LibConst.LINE_SEPARATOR +
                "nur die Sets zum Abspielen/Speichern" + P2LibConst.LINE_SEPARATOR +
                "zurückgesetzt werden!");
        headerLabel.setStyle("-fx-font-size: 1.5em;");

        // Set zurücksetzen
        P2BigButton cancelButton = new P2BigButton(ProgIcons.ICON_BUTTON_QUIT.getImageView(),
                "Nichts ändern", "");
        cancelButton.setOnAction(e -> close());

        final Button btnHelp = P2Button.helpButton(this.getStage(), "Programm zurücksetzen",
                HelpText.RESET_DIALOG);

        P2BigButton setButton = new P2BigButton(ProgIcons.ICON_BUTTON_QUIT.getImageView(),
                "Einstellungen zum Abspielen/Aufzeichnen zurücksetzen",
                "Es werden alle Programmsets (auch eigene)" + P2LibConst.LINE_SEPARATOR +
                        "zum Abspielen und Aufzeichnen gelöscht" + P2LibConst.LINE_SEPARATOR +
                        "und die Standardsets wieder angelegt." +
                        P2LibConst.LINE_SEPARATORx2 +
                        "Abos und Blacklist bleiben erhalten.");
        setButton.setOnAction(e -> {
            Platform.runLater(() -> {
                ImportSetDialogController importSetDialogController = new ImportSetDialogController(progData);
                importSetDialogController.close();
            });
            close();
        });

        // alle Einstellungen
        P2BigButton allButton = new P2BigButton(ProgIcons.ICON_BUTTON_QUIT.getImageView(), "" +
                "Alle Einstellungen zurücksetzen!",
                "Alle Einstellungen gehen verloren." + P2LibConst.LINE_SEPARATORx2 +
                        "ACHTUNG" + P2LibConst.LINE_SEPARATOR +
                        "es werden auch eigene Buttons, Abos" + P2LibConst.LINE_SEPARATOR +
                        "und die Blacklist gelöscht.");
        allButton.setOnAction(e -> {
            Text t1 = new Text("Es werden ");
            t1.getStyleClass().add("downloadGuiMediaText");

            Text t2 = new Text("ALLE");
            t2.setFont(Font.font(null, FontWeight.BOLD, -1));
            t2.getStyleClass().add("downloadGuiMediaText");

            Text t3 = new Text(" von Ihnen erzeugten Änderungen gelöscht." + P2LibConst.LINE_SEPARATORx2 +
                    "Möchten Sie wirklich alle Einstellungen zurücksetzen?");
            t3.getStyleClass().add("downloadGuiMediaText");

            TextFlow tf = new TextFlow();
            tf.getChildren().addAll(t1, t2, t3);

            if (PAlert.showAlert_yes_no_cancel("Einstellungen zurücksetzen",
                    "alle Einstellungen zurücksetzen!", tf, false) == PAlert.BUTTON.YES) {
                // damit wird vor dem Beenden das Konfig-Verzeichnis umbenannt und so startet das
                // Programm wie beim ersten Start
                ProgData.reset = true;
                ProgQuit.quit();
            }
        });

        gridPane.add(ProgIcons.ICON_DIALOG_QUIT.getImageView(), 0, 0, 1, 1);
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

        getVBoxCont().getChildren().addAll(gridPane);
    }
}