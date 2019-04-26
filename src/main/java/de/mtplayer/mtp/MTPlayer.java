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
package de.mtplayer.mtp;

import de.mtplayer.mtp.controller.ProgQuit;
import de.mtplayer.mtp.controller.ProgStart;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ListePsetVorlagen;
import de.mtplayer.mtp.controller.data.SetDataList;
import de.mtplayer.mtp.gui.dialog.FilmInfoDialogController;
import de.mtplayer.mtp.gui.startDialog.StartDialogController;
import de.mtplayer.mtp.res.GetIcon;
import de.mtplayer.mtp.tools.storedFilter.ProgInitFilter;
import de.p2tools.p2Lib.PInit;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PGuiSize;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MTPlayer extends Application {

    private Stage primaryStage;

    private static final String LOG_TEXT_PROGRAMSTART = "Dauer Programmstart";
//    private static final String TITLE_TEXT_PROGRAM_VERSION_IS_UPTODATE = "Programmversion ist aktuell";
//    private static final String TITLE_TEXT_PROGRAMMUPDATE_EXISTS = "Ein Programmupdate ist verfügbar";

    protected ProgData progData;
    ProgStart progStart;
    Scene scene = null;
    private boolean firstProgramStart = false;

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        PDuration.counterStart(LOG_TEXT_PROGRAMSTART);
        progData = ProgData.getInstance();
        progData.primaryStage = primaryStage;
        progStart = new ProgStart(progData);

        initP2lib();
        workBeforeGui();

        initRootLayout();

        workAfterGui();
        PDuration.counterStop(LOG_TEXT_PROGRAMSTART);
    }

    private void initP2lib() {
        PButton.setHlpImage(GetIcon.getImage("button-help.png", 16, 16));
        PInit.initLib(primaryStage, ProgConst.PROGRAMNAME,
                ProgConst.CSS_FILE, "",
                ProgData.debug, ProgData.duration);
    }

    private void workBeforeGui() {
        if (!progStart.loadAll()) {
            PDuration.onlyPing("Erster Start");

            firstProgramStart = true;
            // einmal ein Muster anlegen, für Linux ist es bereits aktiv!
            progData.replaceList.init();

            StartDialogController startDialogController = new StartDialogController();
            if (!startDialogController.isOk()) {
                // dann jetzt beenden -> Thüss
                Platform.exit();
                System.exit(0);
            }


            //todo das ist noch nicht ganz klar ob dahin
            Platform.runLater(() -> {
                PDuration.onlyPing("Erster Start: PSet");

                // kann ein Dialog aufgehen
                final SetDataList pSet = ListePsetVorlagen.getStandarset(true /*replaceMuster*/);
                if (pSet != null) {
                    progData.setDataList.addSetData(pSet);
                    ProgConfig.SYSTEM_UPDATE_PROGSET_VERSION.setValue(pSet.version);
                }

                PDuration.onlyPing("Erster Start: PSet geladen");
            });

            ProgInitFilter.setProgInitFilter();
        }

    }

    private void initRootLayout() {
        try {
            progData.filmInfoDialogController = new FilmInfoDialogController();
            progData.mtPlayerController = new MTPlayerController();

            scene = new Scene(progData.mtPlayerController,
                    PGuiSize.getWidth(ProgConfig.SYSTEM_SIZE_GUI.getStringProperty()),
                    PGuiSize.getHeight(ProgConfig.SYSTEM_SIZE_GUI.getStringProperty()));

            String css = this.getClass().getResource(ProgConst.CSS_FILE).toExternalForm();
            scene.getStylesheets().add(css);
            PInit.addP2LibCss(scene);

            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                e.consume();
                new ProgQuit().quit(true, false);
            });

            if (!PGuiSize.setPos(ProgConfig.SYSTEM_SIZE_GUI.getStringProperty(), primaryStage)) {
                primaryStage.centerOnScreen();
            }
            primaryStage.show();

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void workAfterGui() {
        progStart.doWorkAfterGui(firstProgramStart);
        PDuration.onlyPing("Gui steht!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}