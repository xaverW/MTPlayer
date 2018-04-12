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
import de.mtplayer.mtp.controller.ProgQuitt;
import de.mtplayer.mtp.controller.ProgSave;
import de.mtplayer.mtp.controller.ProgStart;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.ListePsetVorlagen;
import de.mtplayer.mtp.controller.data.SetList;
import de.mtplayer.mtp.controller.loadFilmlist.ListenerFilmListLoad;
import de.mtplayer.mtp.controller.loadFilmlist.ListenerFilmListLoadEvent;
import de.mtplayer.mtp.gui.dialogStart.StartDialogController;
import de.mtplayer.mtp.gui.tools.GuiSize;
import de.mtplayer.mtp.res.GetIcon;
import de.mtplayer.mtp.tools.storedFilter.ProgInitFilter;
import de.mtplayer.mtp.tools.update.SearchProgramUpdate;
import de.p2tools.p2Lib.tools.log.Duration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

public class MTFx extends Application {

    private Stage primaryStage;
    private MTFxController root;


    private static final String ICON_NAME = "Icon.png";
    private static final String ICON_PATH = "/de/mtplayer/mtp/res/";
    private static final int ICON_WIDTH = 58;
    private static final int ICON_HEIGHT = 58;

    private static final String LOG_TEXT_PROGRAMMSTART = "***Programmstart***";
    private static final String ARGUMENT_PREFIX = "-";
    private static final String TITLE_TEXT_PROGRAMMVERSION_IST_AKTUELL = "Programmversion ist aktuell";
    private static final String TITLE_TEXT_EIN_PROGRAMMUPDATE_IST_VERFUEGBAR = "Ein Programmupdate ist verfügbar";

    protected Daten daten;
    ProgStart progStart;
    Scene scene = null;

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        final Parameters parameters = getParameters();
        final List<String> rawArguments = parameters.getRaw();
        final String pfad = readPfadFromArguments(rawArguments.toArray(new String[]{}));

        Duration.counterStart(LOG_TEXT_PROGRAMMSTART);
        daten = Daten.getInstance(pfad);
        daten.primaryStage = primaryStage;
        progStart = new ProgStart(daten);

        loadData();
        initRootLayout();
        losGehts();
    }

    private void initRootLayout() {
        try {
            root = new MTFxController();
            daten.mtFxController = root;
            scene = new Scene(root,
                    GuiSize.getWidth(Config.SYSTEM_SIZE_GUI),
                    GuiSize.getHeight(Config.SYSTEM_SIZE_GUI));

            String css = this.getClass().getResource(Const.CSS_FILE).toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                e.consume();
                new ProgQuitt().beenden(true, false);
            });

            GuiSize.setPos(Config.SYSTEM_SIZE_GUI, primaryStage);
            primaryStage.show();

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void losGehts() {
        Duration.counterStop(LOG_TEXT_PROGRAMMSTART);
        primaryStage.getIcons().add(GetIcon.getImage(ICON_NAME, ICON_PATH, ICON_WIDTH, ICON_HEIGHT));

        progStart.startMsg();

        Duration.staticPing("Erster Start");
        setOrgTitel();
        initProg();

        Duration.staticPing("Gui steht!");
        progStart.loadDataProgStart();
    }

    private void loadData() {

        if (!progStart.allesLaden()) {

            // konnte nicht geladen werden
            Duration.staticPing("Erster Start");

            // einmal ein Muster anlegen, für Linux ist es bereits aktiv!
            daten.replaceList.init();

            new StartDialogController();

            //todo das ist noch nicht ganz klar ob dahin
            Platform.runLater(() -> {
                // kann ein Dialog aufgehen
                final SetList pSet = ListePsetVorlagen.getStandarset(true /*replaceMuster*/);
                if (pSet != null) {
                    daten.setList.addPset(pSet);
                    Config.SYSTEM_UPDATE_PROGSET_VERSION.setValue(pSet.version);
                }
            });

//            Config.loadSystemParameter();
            ProgInitFilter.setProgInitFilter();
        }
        daten.initDialogs();
    }

    private String readPfadFromArguments(final String[] aArguments) {
        String pfad;
        if (aArguments == null) {
            pfad = "";
        } else {
            if (aArguments.length > 0) {
                if (!aArguments[0].startsWith(ARGUMENT_PREFIX)) {
                    if (!aArguments[0].endsWith(File.separator)) {
                        aArguments[0] += File.separator;
                    }
                    pfad = aArguments[0];
                } else {
                    pfad = "";
                }
            } else {
                pfad = "";
            }
        }
        return pfad;
    }


    private void setOrgTitel() {
        primaryStage.setTitle(Const.PROGRAMMNAME + " " + Functions.getProgVersion());
    }

    private void setUpdateTitel() {
        primaryStage.setTitle(TITLE_TEXT_EIN_PROGRAMMUPDATE_IST_VERFUEGBAR);
    }

    private void setNoUpdateTitel() {
        primaryStage.setTitle(TITLE_TEXT_PROGRAMMVERSION_IST_AKTUELL);
    }

    private void initProg() {
        daten.loadFilmList.addAdListener(new ListenerFilmListLoad() {
            @Override
            public void start(ListenerFilmListLoadEvent event) {

            }

            @Override
            public void fertig(ListenerFilmListLoadEvent event) {
                new ProgSave().allesSpeichern(); // damit nichts verlorengeht
            }

            @Override
            public void fertigOnlyOne(ListenerFilmListLoadEvent event) {
                daten.mediaDbList.loadSavedList();
                daten.mediaDbList.createMediaDB();
                checkProgUpdate();
            }
        });

    }

    private void checkProgUpdate() {
        // Prüfen obs ein Programmupdate gibt
        Duration.staticPing("check update");
        if (!Boolean.parseBoolean(Config.SYSTEM_UPDATE_SEARCH.get()) ||
                Config.SYSTEM_UPDATE_BUILD_NR.get().equals(Functions.getProgVersion() /*Start mit neuer Version*/)
                        && Config.SYSTEM_UPDATE_DATE.get().equals(StringFormatters.FORMATTER_yyyyMMdd.format(new Date()))) {
            // will der User nicht --oder-- keine neue Version und heute schon gemacht
            PLog.sysLog("Kein Update-Check");
            return;
        }

        check();
    }

    private synchronized void check() {

        new Thread(() -> {
            try {
                if (new SearchProgramUpdate().checkVersion(false, false /* immer anzeigen */)) {
                    Platform.runLater(() -> setUpdateTitel());
                } else {
                    Platform.runLater(() -> setNoUpdateTitel());
                }

                sleep(10_000);

                Platform.runLater(() -> setOrgTitel());

            } catch (final Exception ex) {
                PLog.errorLog(794612801, ex);
            }
        }).start();
    }

}
