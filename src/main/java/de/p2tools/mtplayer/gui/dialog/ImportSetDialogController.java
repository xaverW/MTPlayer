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
import de.p2tools.mtplayer.controller.data.setdata.SetImportFactory;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.configdialog.panesetdata.ControllerSet;
import de.p2tools.mtplayer.gui.startdialog.StartPaneDownloadPath;
import de.p2tools.mtplayer.gui.startdialog.StartPanePath;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ImportSetDialogController extends P2DialogExtra {

    private final ProgData progData;
    Button btnCancel = new Button("_Abbrechen");
    Button btnImport = new Button("_Set importieren");
    private final VBox vBoxPath = new VBox();
    private ControllerSet controllerSet;

    public ImportSetDialogController(ProgData progData) {
        // beim Anlegen von Abos, Downloads, ..... wenn kein Set vorhanden ist
        // ResetDialog
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_IMPORT_SET_SIZE,
                "Set importieren", true, true, true, DECO.BORDER_SMALL);

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
        final Button btnHelp = PIconFactory.getHelpButton(getStage(), "Set zurücksetzen",
                "Es werden die Standard-Sets eingerichtet, bestehende werden " +
                        "durch neue ersetzen\n" +
                        "\n" +
                        "Damit werden die Sets die zum Abspielen " +
                        "und Aufzeichnen der Filme gebraucht werden angelegt.\n" +
                        "Eventuell noch bestehende Sets werden gelöscht. Es wird der " +
                        "Standard-Zustand wie nach dem ersten Start wieder hergestellt. " +
                        "Damit kann dann direkt weitergearbeitet werden.");

        btnImport.setOnAction(event -> {
            importSet();
        });

        // vor Import
        TitledPane tpDownPath = new StartPaneDownloadPath(this.getStage()).makePath();
        tpDownPath.setMaxHeight(Double.MAX_VALUE);
        tpDownPath.setCollapsible(false);

        TitledPane tpPath = new StartPanePath(this.getStage()).makePath();
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

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(vBoxPath, controllerSet);
        vBoxPath.toFront();
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(stackPane);

        addOkButton(btnImport);
        addCancelButton(btnCancel);
        addHlpButton(btnHelp);
    }

    private void importSet() {
        btnCancel.setText("Ok");
        btnImport.setVisible(false);
        btnImport.setManaged(false);

        progData.setDataList.clear();
        if (SetImportFactory.getStandardSet(getStage())) {
            P2Alert.showInfoAlert(getStage(), "Set", "Set importieren", "Sets wurden importiert!", false);
        } else {
            P2Alert.showErrorAlert(getStage(), "Set importieren", "Sets konnten nicht importiert werden!");
        }

        controllerSet.toFront();
    }
}
