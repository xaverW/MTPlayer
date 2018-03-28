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

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.ListePsetVorlagen;
import de.mtplayer.mtp.gui.configDialog.SetPaneController;
import de.mtplayer.mtp.gui.dialogStart.DownPathPane;
import de.mtplayer.mtp.gui.dialogStart.PathPane;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;

public class ImportSetDialogController extends MTDialog {

    private final Daten daten;
    VBox vbox, vBoxCont;
    Button btnOk = new Button("Abbrechen");
    Button btnImport = new Button("Set importieren");
    private boolean im = false;
    private StackPane stackPane;
    private ScrollPane pathPane, setPane;

    public ImportSetDialogController(Daten daten) {
        super("", Config.CONFIG_DIALOG_IMPORT_SET_GROESSE,
                "Set importieren", true);

        this.daten = daten;

        vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(30);

        vBoxCont = new VBox();
        vBoxCont.getStyleClass().add("dialog-only-border");

        init(vbox, true);
    }


    @Override
    public void make() {
        btnOk.setOnAction(a -> close());

        final Button btnHelp = new Button("");
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Set zurücksetzen",
                "\"Bestehende Sets durch die neuen ersetzen\"" +
                        "\n\n" +
                        "Damit werden alle Sets (auch eigene), die zum Abspielen\n" +
                        "und Aufzeichnen der Filme gebraucht werden, gelöscht.\n" +
                        "Anschließend werden die aktuellen Standardsets\n" +
                        "eingerichtet.\n" +
                        "Es kann dann direkt damit weitergearbeitet werden."));

        btnImport.setOnAction(event -> {
            importSet();
        });

        stackPane = new StackPane();
        VBox.setVgrow(stackPane, Priority.ALWAYS);


        // vor import
        pathPane = new ScrollPane();
        pathPane.setFitToHeight(true);
        pathPane.setFitToWidth(true);

        TitledPane tpDownPath = new DownPathPane().makePath();
        tpDownPath.setMaxHeight(Double.MAX_VALUE);
        tpDownPath.setCollapsible(false);

        TitledPane tpPath = new PathPane().makePath();
        tpPath.setMaxHeight(Double.MAX_VALUE);
        tpPath.setCollapsible(false);

        VBox vBoxPath = new VBox();
        vBoxPath.setSpacing(10);
        vBoxPath.getChildren().addAll(tpDownPath, tpPath);
        pathPane.setContent(vBoxPath);


        // nach Import
        setPane = new ScrollPane();
        setPane.setFitToHeight(true);
        setPane.setFitToWidth(true);

        AnchorPane setP = new SetPaneController();
        setP.setMaxWidth(Double.MAX_VALUE);
        setP.setMaxHeight(Double.MAX_VALUE);
        setPane.setContent(setP);


        stackPane.getChildren().addAll(pathPane, setPane);
        pathPane.toFront();

        vBoxCont.getChildren().add(stackPane);
        VBox.setVgrow(vBoxCont, Priority.ALWAYS);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().addAll(btnImport, btnOk, btnHelp);

        vbox.getChildren().addAll(vBoxCont, hBox);
    }

    private void importSet() {
        im = true;
        btnOk.setText("Ok");
        btnImport.setDisable(true);

        daten.setList.clear();

        if (SetsPrograms.addSetVorlagen(ListePsetVorlagen.getStandarset(true /*replaceMuster*/))) {
            new MTAlert().showInfoAlert("Set", "Set importieren", "Sets wurden importiert!", false);
        } else {
            new MTAlert().showErrorAlert("Set importieren", "Set wurde nicht importiert!");
        }

        setPane.toFront();
    }
}
