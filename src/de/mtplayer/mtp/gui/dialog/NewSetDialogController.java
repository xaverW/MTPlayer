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
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class NewSetDialogController extends PDialog {

    final ProgData progData;
    final StackPane stackPane;
    final VBox vbox;

    boolean addNewSet = false;
    boolean replaceSet = false;
    boolean askAgain = true;

    public NewSetDialogController(ProgData progData) {
        super(null,
                "Das Standardset wurde aktualisiert", true);

        this.progData = progData;

        stackPane = new StackPane();
        vbox = new VBox();

        init(vbox, true);
    }

    public boolean getAddNewSet() {
        return addNewSet;
    }

    public boolean getReplaceSet() {
        return replaceSet;
    }

    public boolean getAskAgain() {
        return askAgain;
    }

    @Override
    public void make() {
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(30);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(25);

        Label headerLabel = new Label("Es gibt ein neues Standardset der Videoplayer" + PConst.LINE_SEPARATOR +
                "für den Download und das Abspielen der Filme.");
        headerLabel.setStyle("-fx-font-size: 1.5em;");

        BigButton cancelButton = new BigButton(new Icons().ICON_BUTTON_QUIT, "Nichts ändern",
                "");
        cancelButton.setOnAction(e -> close());

        BigButton addButton = new BigButton(new Icons().ICON_BUTTON_QUIT, "Neue Sets nur anfügen",
                "Die bestehenden Einstellungen werden nicht verändert." + PConst.LINE_SEPARATOR +
                        "Das neue Set wird nur angefügt und muss dann" + PConst.LINE_SEPARATOR +
                        "erst noch in den Einstellungen aktiviert werden." + PConst.LINE_SEPARATOR +
                        "  \"->Einstellungen->Set bearbeiten\"");
        addButton.setOnAction(e -> {
            addNewSet = true;
            close();
        });

        BigButton replaceButton = new BigButton(new Icons().ICON_BUTTON_QUIT, "Aktuelle Sets durch neue ersetzen",
                "Es werden alle Programmsets (auch eigene) gelöscht" + PConst.LINE_SEPARATOR +
                        "und die neuen Standardsets wieder angelegt." + PConst.LINE_SEPARATORx2 +
                        "(Wenn Sie die Einstellungen nicht verändert haben" + PConst.LINE_SEPARATOR +
                        "ist das die Empfehlung)");
        replaceButton.setOnAction(e -> {
            replaceSet = true;
            close();
        });


        gridPane.add(new Icons().ICON_DIALOG_QUIT, 0, 0, 1, 1);
        gridPane.add(headerLabel, 1, 0);
        gridPane.add(cancelButton, 1, 1);
        gridPane.add(addButton, 1, 2);
        gridPane.add(replaceButton, 1, 3);

        PToggleSwitch tg = new PToggleSwitch();
        tg.setSelected(askAgain);
        tg.selectedProperty().addListener((observable, oldValue, newValue) -> askAgain = newValue);

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.setPadding(new Insets(10, 0, 10, 0));

        hBox.getChildren().addAll(tg, new Label("Morgen wieder fragen"));
        gridPane.add(hBox, 1, 4);

        ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
        gridPane.getStyleClass().add("dialog-only-border");

        vbox.getChildren().addAll(gridPane);
    }


}