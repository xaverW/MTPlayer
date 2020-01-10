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
import de.mtplayer.mtp.gui.configDialog.setData.SetPaneController;
import de.mtplayer.mtp.gui.startDialog.DownPathPane;
import de.mtplayer.mtp.gui.startDialog.PathPane;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ImportSetDialogController extends PDialogExtra {

    private final ProgData progData;
    Button btnCancel = new Button("_Abbrechen");
    Button btnImport = new Button("_Set importieren");
    private boolean im = false;
    private StackPane stackPane;
    private VBox vBoxPath = new VBox();
    private SetPaneController setPaneController;


    public ImportSetDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_IMPORT_SET_SIZE.getStringProperty(),
                "Set importieren", true, false);

        this.progData = progData;
        init(true);
    }

    public void close() {
        setPaneController.close();
    }

    @Override
    public void make() {
        btnCancel.setOnAction(a -> close());

        final Button btnHelp = PButton.helpButton("Set zurücksetzen",
                "\"Bestehende Sets durch neue ersetzen\"" +
                        P2LibConst.LINE_SEPARATORx2 +
                        "Damit werden alle Sets (auch eigene), die zum Abspielen" + P2LibConst.LINE_SEPARATOR +
                        "und Aufzeichnen der Filme gebraucht werden, gelöscht." + P2LibConst.LINE_SEPARATOR +
                        "Anschließend werden die aktuellen Standardsets eingerichtet." + P2LibConst.LINE_SEPARATOR +
                        "Damit kann dann direkt weitergearbeitet werden.");

        btnImport.setOnAction(event -> {
            importSet();
        });


        // vor import
        TitledPane tpDownPath = new DownPathPane(progData.primaryStage).makePath();
        tpDownPath.setMaxHeight(Double.MAX_VALUE);
        tpDownPath.setCollapsible(false);

        TitledPane tpPath = new PathPane(progData.primaryStage).makePath();
        tpPath.setMaxHeight(Double.MAX_VALUE);
        tpPath.setCollapsible(false);

        vBoxPath.setSpacing(10);
        vBoxPath.getChildren().addAll(tpDownPath, tpPath);
        vBoxPath.setStyle("-fx-background-color: -fx-background;");

        // nach Import
        setPaneController = new SetPaneController(P2LibConst.primaryStage);
        setPaneController.setMaxWidth(Double.MAX_VALUE);
        setPaneController.setMaxHeight(Double.MAX_VALUE);
        setPaneController.setStyle("-fx-background-color: -fx-background;");

        stackPane = new StackPane();
        stackPane.getChildren().addAll(vBoxPath, setPaneController);
        vBoxPath.toFront();
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        getvBoxCont().getChildren().add(stackPane);

        addOkButton(btnImport);
        addCancelButton(btnCancel);
        addHlpButton(btnHelp);
    }

    private void importSet() {
        im = true;
        btnCancel.setText("Ok");
        btnImport.setDisable(true);

        progData.setDataList.clear();

        if (SetsPrograms.addSetTemplate(ListePsetVorlagen.getStandarset(true /*replaceMuster*/))) {
            PAlert.showInfoAlert("Set", "Set importieren", "Sets wurden importiert!", false);
        } else {
            PAlert.showErrorAlert("Set importieren", "Sets konnten nicht importiert werden!");
        }

        setPaneController.toFront();
        setPaneController.selectTableFirst();
    }
}
