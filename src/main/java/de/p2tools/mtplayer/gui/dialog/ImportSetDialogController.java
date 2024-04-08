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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.worker.ImportStandardSet;
import de.p2tools.mtplayer.gui.configdialog.panesetdata.ControllerSet;
import de.p2tools.mtplayer.gui.startdialog.DownPathPane;
import de.p2tools.mtplayer.gui.startdialog.PathPane;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ImportSetDialogController extends P2DialogExtra {

    private final ProgData progData;
    Button btnCancel = new Button("_Abbrechen");
    Button btnImport = new Button("_Set importieren");
    private boolean im = false;
    private StackPane stackPane;
    private VBox vBoxPath = new VBox();
    private ControllerSet controllerSet;

    public ImportSetDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_IMPORT_SET_SIZE,
                "Set importieren", true, false, DECO.BORDER_SMALL);

        this.progData = progData;
        init(true);
    }

    @Override
    public void close() {
        controllerSet.close();
        super.close();
    }

    @Override
    public void make() {
        btnCancel.setOnAction(a -> close());
        final Button btnHelp = P2Button.helpButton("Set zurücksetzen",
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
        TitledPane tpDownPath = new DownPathPane(this.getStage()).makePath();
        tpDownPath.setMaxHeight(Double.MAX_VALUE);
        tpDownPath.setCollapsible(false);

        TitledPane tpPath = new PathPane(this.getStage()).makePath();
        tpPath.setMaxHeight(Double.MAX_VALUE);
        tpPath.setCollapsible(false);

        vBoxPath.setSpacing(10);
        vBoxPath.getChildren().addAll(tpDownPath, tpPath);
        vBoxPath.setStyle("-fx-background-color: -fx-background;");

        // nach Import
        controllerSet = new ControllerSet(this.getStage());
        controllerSet.setMaxWidth(Double.MAX_VALUE);
        controllerSet.setMaxHeight(Double.MAX_VALUE);
        controllerSet.setStyle("-fx-background-color: -fx-background;");

        stackPane = new StackPane();
        stackPane.getChildren().addAll(vBoxPath, controllerSet);
        vBoxPath.toFront();
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(stackPane);

        addOkButton(btnImport);
        addCancelButton(btnCancel);
        addHlpButton(btnHelp);
    }

    private void importSet() {
        im = true;
        btnCancel.setText("Ok");
        btnImport.setDisable(true);

        progData.setDataList.clear();
        if (ImportStandardSet.getStandardSet()) {
            P2Alert.showInfoAlert("Set", "Set importieren", "Sets wurden importiert!", false);
        } else {
            P2Alert.showErrorAlert("Set importieren", "Sets konnten nicht importiert werden!");
        }

        controllerSet.toFront();
    }
}
