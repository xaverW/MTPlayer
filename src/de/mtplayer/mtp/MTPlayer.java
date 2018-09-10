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

import de.mtplayer.mLib.tools.Functions;
import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.ProgQuit;
import de.mtplayer.mtp.controller.ProgSave;
import de.mtplayer.mtp.controller.ProgStart;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ListePsetVorlagen;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.gui.startDialog.StartDialogController;
import de.mtplayer.mtp.res.GetIcon;
import de.mtplayer.mtp.tools.storedFilter.ProgInitFilter;
import de.mtplayer.mtp.tools.update.SearchProgramUpdate;
import de.p2tools.p2Lib.PInit;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PGuiSize;
import de.p2tools.p2Lib.tools.log.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Date;

import static java.lang.Thread.sleep;

public class MTPlayer extends Application {

    private Stage primaryStage;
    private MTPlayerController mtPlayerController;

    private static final String ICON_NAME = "Icon.png";
    private static final String ICON_PATH = "/de/mtplayer/mtp/res/";
    private static final int ICON_WIDTH = 58;
    private static final int ICON_HEIGHT = 58;

    private static final String LOG_TEXT_PROGRAMSTART = "Dauer Programmstart";
    private static final String TITLE_TEXT_PROGRAM_VERSION_IS_UPTODATE = "Programmversion ist aktuell";
    private static final String TITLE_TEXT_PROGRAMMUPDATE_EXISTS = "Ein Programmupdate ist verfügbar";

    protected ProgData progData;
    ProgStart progStart;
    Scene scene = null;
    private boolean onlyOne = false;
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

        initP2();
        loadData();
        initRootLayout();
        losGehts();
        PDuration.counterStop(LOG_TEXT_PROGRAMSTART);
    }

    private void initP2() {
        PButton.setHlpImage(GetIcon.getImage("button-help.png", 16, 16));
        PInit.initLib(primaryStage, ProgConst.PROGRAMNAME, ProgConst.CSS_FILE, "", ProgData.debug);
    }

    private void loadData() {
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
            PDuration.onlyPing("Erster Start: PSet");
            Platform.runLater(() -> {
                // kann ein Dialog aufgehen
                final SetList pSet = ListePsetVorlagen.getStandarset(true /*replaceMuster*/);
                if (pSet != null) {
                    progData.setList.addPset(pSet);
                    ProgConfig.SYSTEM_UPDATE_PROGSET_VERSION.setValue(pSet.version);
                }
                PDuration.onlyPing("Erster Start: PSet geladen");
            });

            ProgInitFilter.setProgInitFilter();
        }
        progData.initDialogs();
    }

    private void initRootLayout() {
        try {
            mtPlayerController = new MTPlayerController();
            progData.mtPlayerController = mtPlayerController;
            scene = new Scene(mtPlayerController,
                    PGuiSize.getWidth(ProgConfig.SYSTEM_SIZE_GUI.getStringProperty()),
                    PGuiSize.getHeight(ProgConfig.SYSTEM_SIZE_GUI.getStringProperty()));

            String css = this.getClass().getResource(ProgConst.CSS_FILE).toExternalForm();
            scene.getStylesheets().add(css);

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

    private void losGehts() {
        primaryStage.getIcons().add(GetIcon.getImage(ICON_NAME, ICON_PATH, ICON_WIDTH, ICON_HEIGHT));

        progStart.startMsg();

        PDuration.onlyPing("Erster Start");
        setOrgTitle();
        initProg();

        PDuration.onlyPing("Gui steht!");
        progStart.loadDataProgStart(firstProgramStart);
    }


    private void setOrgTitle() {
        primaryStage.setTitle(ProgConst.PROGRAMNAME + " " + Functions.getProgVersion());
    }

    private void setUpdateTitle() {
        primaryStage.setTitle(TITLE_TEXT_PROGRAMMUPDATE_EXISTS);
    }

    private void setNoUpdateTitle() {
        primaryStage.setTitle(TITLE_TEXT_PROGRAM_VERSION_IS_UPTODATE);
    }

    private void initProg() {
        progData.startTimer();
        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                new ProgSave().saveAll(); // damit nichts verlorengeht

                if (!onlyOne) {
                    onlyOne = true;
                    progData.mediaDataList.createMediaDb();
                    checkProgUpdate();
                }

            }
        });
    }

    private void checkProgUpdate() {
        // Prüfen obs ein Programmupdate gibt
        PDuration.onlyPing("check update");
        if (!Boolean.parseBoolean(ProgConfig.SYSTEM_UPDATE_SEARCH.get()) ||
                ProgConfig.SYSTEM_UPDATE_BUILD_NR.get().equals(Functions.getProgVersion() /*Start mit neuer Version*/)
                        && ProgConfig.SYSTEM_UPDATE_DATE.get().equals(StringFormatters.FORMATTER_yyyyMMdd.format(new Date()))) {
            // will der User nicht --oder-- keine neue Version und heute schon gemacht
            PLog.sysLog("Kein Update-Check");
            return;
        }

        Thread th = new Thread(() -> {
            try {
                if (new SearchProgramUpdate(primaryStage).checkVersion(false, false /* immer anzeigen */)) {
                    Platform.runLater(() -> setUpdateTitle());
                } else {
                    Platform.runLater(() -> setNoUpdateTitle());
                }

                sleep(10_000);

                Platform.runLater(() -> setOrgTitle());

            } catch (final Exception ex) {
                PLog.errorLog(794612801, ex);
            }
        });
        th.setName("check");
        th.start();
    }

}
