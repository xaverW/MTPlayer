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
package de.p2tools.mtplayer;

import de.p2tools.mtplayer.controller.ProgQuit;
import de.p2tools.mtplayer.controller.ProgStart;
import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.mtplayer.controller.data.ListePsetVorlagen;
import de.p2tools.mtplayer.controller.data.SetDataList;
import de.p2tools.mtplayer.controller.filmFilter.FilmFilterFactory;
import de.p2tools.mtplayer.gui.startDialog.StartDialogController;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.P2LibInit;
import de.p2tools.p2Lib.configFile.IoReadWriteStyle;
import de.p2tools.p2Lib.guiTools.PGuiSize;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MTPlayer extends Application {

    private Stage primaryStage;
    private static final String LOG_TEXT_PROGRAMSTART = "Dauer Programmstart";
    protected ProgData progData;
    ProgStart progStart = new ProgStart();
    Scene scene = null;
    private boolean firstProgramStart = false; // ist der allererste Programmstart: Programminit wird gemacht

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        PDuration.counterStart(LOG_TEXT_PROGRAMSTART);
        progData = ProgData.getInstance();
        progData.primaryStage = primaryStage;

        initP2lib();

        workBeforeGui();
        initRootLayout();
        progStart.doWorkAfterGui(progData, firstProgramStart);

        PDuration.onlyPing("Gui steht!");
        PDuration.counterStop(LOG_TEXT_PROGRAMSTART);
    }

    private void initP2lib() {
        P2LibInit.initLib(primaryStage, ProgConst.PROGRAM_NAME,
                "", ProgData.debug, ProgData.duration);
        P2LibInit.addCssFile(P2LibConst.CSS_GUI);
        P2LibInit.addCssFile(ProgConst.CSS_FILE);
    }

    private void workBeforeGui() {
        if (!progStart.loadAll()) {
            PDuration.onlyPing("Erster Start");
            firstProgramStart = true;

            UpdateConfig.setUpdateDone(); // dann ists ja kein Programmupdate
            progData.replaceList.init(); // einmal ein Muster anlegen, für Linux ist es bereits aktiv!

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

            FilmFilterFactory.addStandardFilter();
        }
    }

    private void initRootLayout() {
        try {
            addThemeCss(); // damit es für die 2 schon mal stimmt
            progData.mtPlayerController = new MTPlayerController();

            scene = new Scene(progData.mtPlayerController,
                    PGuiSize.getWidth(ProgConfig.SYSTEM_SIZE_GUI),
                    PGuiSize.getHeight(ProgConfig.SYSTEM_SIZE_GUI));//Größe der scene!= Größe stage!!!
            addThemeCss(); // und jetzt noch für die neue Scene

            if (ProgConfig.SYSTEM_STYLE.getValue()) {
                P2LibInit.setStyleFile(ProgInfos.getStyleFile().toString());
                IoReadWriteStyle.readStyle(ProgInfos.getStyleFile(), scene);
            }

            ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> {
                addThemeCss();
                //erst css ändern, dann
                ProgColorList.setColorTheme();
                ProgConfig.SYSTEM_THEME_CHANGED.setValue(!ProgConfig.SYSTEM_THEME_CHANGED.getValue());
            });

            primaryStage.setScene(scene);
//            primaryStage.setOnHiding(event -> {
//                //beim einklappen durchs Tray
//                PGuiSize.getSizeStage(ProgConfig.SYSTEM_SIZE_GUI, ProgData.getInstance().primaryStage);
//            });
            primaryStage.setOnCloseRequest(e -> {
                //beim Beenden
                e.consume();
                ProgQuit.quit(false);
            });

            scene.heightProperty().addListener((v, o, n) -> PGuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            scene.widthProperty().addListener((v, o, n) -> PGuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            primaryStage.xProperty().addListener((v, o, n) -> PGuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            primaryStage.yProperty().addListener((v, o, n) -> PGuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));

            //Pos setzen
            if (!PGuiSize.setPos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage)) {
                primaryStage.centerOnScreen();
            }
            primaryStage.show();

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void addThemeCss() {
        if (ProgConfig.SYSTEM_DARK_THEME.getValue()) {
            P2LibInit.addCssFile(P2LibConst.CSS_GUI_DARK);
            P2LibInit.addCssFile(ProgConst.CSS_FILE_DARK_THEME);
        } else {
            P2LibInit.removeCssFile(P2LibConst.CSS_GUI_DARK);
            P2LibInit.removeCssFile(ProgConst.CSS_FILE_DARK_THEME);
        }
        P2LibInit.addP2LibCssToScene(scene);
    }
}
