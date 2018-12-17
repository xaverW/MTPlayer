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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ListePsetVorlagen;
import de.mtplayer.mtp.gui.configDialog.SetPaneController;
import de.mtplayer.mtp.gui.startDialog.DownPathPane;
import de.mtplayer.mtp.gui.startDialog.PathPane;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.guiTools.PButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;

public class ImportSetDialogController extends PDialog {

    private final ProgData progData;
    VBox vbox, vBoxCont;
    Button btnOk = new Button("Abbrechen");
    Button btnImport = new Button("Set importieren");
    private boolean im = false;
    private StackPane stackPane;
    private ScrollPane pathPane, setPane;

    public ImportSetDialogController(ProgData progData) {
        super(ProgConfig.CONFIG_DIALOG_IMPORT_SET_SIZE.getStringProperty(),
                "Set importieren", true);

        this.progData = progData;

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

        final Button btnHelp = PButton.helpButton("Set zurücksetzen",
                "\"Bestehende Sets durch die neuen ersetzen\"" +
                        PConst.LINE_SEPARATORx2 +
                        "Damit werden alle Sets (auch eigene), die zum Abspielen" + PConst.LINE_SEPARATOR +
                        "und Aufzeichnen der Filme gebraucht werden, gelöscht." + PConst.LINE_SEPARATOR +
                        "Anschließend werden die aktuellen Standardsets" + PConst.LINE_SEPARATOR +
                        "eingerichtet." + PConst.LINE_SEPARATOR +
                        "Es kann dann direkt damit weitergearbeitet werden.");

        btnImport.setOnAction(event -> {
            importSet();
        });

        stackPane = new StackPane();
        VBox.setVgrow(stackPane, Priority.ALWAYS);


        // vor import
        pathPane = new ScrollPane();
        pathPane.setFitToHeight(true);
        pathPane.setFitToWidth(true);

        TitledPane tpDownPath = new DownPathPane(progData.primaryStage).makePath();
        tpDownPath.setMaxHeight(Double.MAX_VALUE);
        tpDownPath.setCollapsible(false);

        TitledPane tpPath = new PathPane(progData.primaryStage).makePath();
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

        AnchorPane setP = new SetPaneController(PConst.primaryStage);
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

        progData.setList.clear();

        if (SetsPrograms.addSetTemplate(ListePsetVorlagen.getStandarset(true /*replaceMuster*/))) {
            PAlert.showInfoAlert("Set", "Set importieren", "Sets wurden importiert!", false);
        } else {
            PAlert.showErrorAlert("Set importieren", "Set wurde nicht importiert!");
        }

        setPane.toFront();
    }
}
