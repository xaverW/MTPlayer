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
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.gui.AboGuiPack;
import de.mtplayer.mtp.gui.DownloadGuiPack;
import de.mtplayer.mtp.gui.FilmGuiPack;
import de.mtplayer.mtp.gui.StatusBarController;
import de.mtplayer.mtp.gui.configDialog.ConfigDialogController;
import de.mtplayer.mtp.gui.dialog.AboutDialogController;
import de.mtplayer.mtp.gui.dialog.ResetDialogController;
import de.mtplayer.mtp.gui.mediaConfig.MediaConfigDialogController;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MTPlayerController extends StackPane {

    Button btnFilmlist = new Button("Filmliste");
    Button btnFilm = new Button("Filme");
    Button btnDownload = new Button("Downloads");
    Button btnAbo = new Button("Abos");

    MenuButton menuButton = new MenuButton("");

    BorderPane borderPane = new BorderPane();
    StackPane stackPaneCont = new StackPane();

    private PMaskerPane maskerPane = new PMaskerPane();
    private StatusBarController statusBarController;

    private SplitPane splitPaneFilm;
    private SplitPane splitPaneDownoad;
    private SplitPane splitPaneAbo;

    private final ProgData progData;

    FilmGuiPack filmGuiPack = new FilmGuiPack();
    DownloadGuiPack downloadGuiPack = new DownloadGuiPack();
    AboGuiPack aboGuiPack = new AboGuiPack();


    public MTPlayerController() {
        progData = ProgData.getInstance();
        init();
    }

    public void setButtonFilmlistUpdate() {
        btnFilmlist.getStyleClass().add("btnFilmlist_");
    }

    private void init() {
        try {
            // Toolbar
            HBox hBoxTop = new HBox();
            hBoxTop.setPadding(new Insets(10));
            hBoxTop.setSpacing(20);
            hBoxTop.setAlignment(Pos.CENTER);

            TilePane tilePaneFilmDownloadAbo = new TilePane();
            tilePaneFilmDownloadAbo.setHgap(20);
            tilePaneFilmDownloadAbo.setAlignment(Pos.CENTER);
            tilePaneFilmDownloadAbo.getChildren().addAll(btnFilm, btnDownload, btnAbo);
            HBox.setHgrow(tilePaneFilmDownloadAbo, Priority.ALWAYS);
            hBoxTop.getChildren().addAll(btnFilmlist, tilePaneFilmDownloadAbo, menuButton);

            // Center
            splitPaneFilm = filmGuiPack.pack();
            splitPaneDownoad = downloadGuiPack.pack();
            splitPaneAbo = aboGuiPack.pack();
            stackPaneCont.getChildren().addAll(splitPaneFilm, splitPaneDownoad, splitPaneAbo);

            // Statusbar
            statusBarController = new StatusBarController(progData);

            // Gui zusammenbauen
            borderPane.setTop(hBoxTop);
            borderPane.setCenter(stackPaneCont);
            borderPane.setBottom(statusBarController);
            this.setPadding(new Insets(0));
            this.getChildren().addAll(borderPane, maskerPane);

            initMaskerPane();
            initButton();
            selPanelFilm();
        } catch (Exception ex) {
            PLog.errorLog(597841023, ex);
        }
    }

    private void initMaskerPane() {
        StackPane.setAlignment(maskerPane, Pos.CENTER);
        progData.maskerPane = maskerPane;
        maskerPane.setPadding(new Insets(4, 1, 1, 1));
        maskerPane.toFront();
        Button btnStop = maskerPane.getButton();
        maskerPane.setButtonText("");
        btnStop.setGraphic(new ProgIcons().ICON_BUTTON_STOP);
        btnStop.setOnAction(a -> progData.loadFilmlist.setStop(true));

    }

    private void initButton() {
        btnFilmlist.setMinWidth(Region.USE_PREF_SIZE);
        btnFilmlist.getStyleClass().add("btnFilmlist");
        btnFilmlist.setTooltip(new Tooltip("neue Filmliste laden"));
        btnFilmlist.setOnAction(e -> {
            progData.loadFilmlist.loadNewFilmlistFromServer();
        });

        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {

            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                // falls "neue Filmliste" aktiv ist
                btnFilmlist.getStyleClass().clear();
                btnFilmlist.getStyleClass().add("btnFilmlist");
            }
        });

        btnFilm.setTooltip(new Tooltip("Filme anzeigen"));
        btnFilm.setOnAction(e -> selPanelFilm());
        btnFilm.setMaxWidth(Double.MAX_VALUE);

        btnDownload.setTooltip(new Tooltip("Downloads anzeigen"));
        btnDownload.setOnAction(e -> selPanelDownload());
        btnDownload.setMaxWidth(Double.MAX_VALUE);

        btnAbo.setTooltip(new Tooltip("Abos anzeigen"));
        btnAbo.setOnAction(e -> selPanelAbo());
        btnAbo.setMaxWidth(Double.MAX_VALUE);


        // Menü
        final MenuItem miConfig = new MenuItem("Einstellungen");
        miConfig.setOnAction(e -> new ConfigDialogController());

        final MenuItem miMedia = new MenuItem("Mediensammlung");
        miMedia.setOnAction(e -> new MediaConfigDialogController());

        final MenuItem miQuit = new MenuItem("Beenden");
        miQuit.setOnAction(e -> new ProgQuit().quit(true, false));

        final MenuItem miAbout = new MenuItem("Über dieses Programm");
        miAbout.setOnAction(event -> new AboutDialogController(progData));

        final MenuItem miLog = new MenuItem("Logdatei öffnen");
        miLog.setOnAction(event -> {
            PLogger.openLogFile();
        });

        final MenuItem miUrlHelp = new MenuItem("Anleitung im Web");
        miUrlHelp.setOnAction(event -> {
            POpen.openURL(ProgConst.ADRESSE_WEBSITE_HELP,
                    ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
        });

        final MenuItem miReset = new MenuItem("Einstellungen zurücksetzen");
        miReset.setOnAction(event -> new ResetDialogController(progData));

        final Menu mHelp = new Menu("Hilfe");
        mHelp.getItems().addAll(miUrlHelp, miLog, miReset, new SeparatorMenuItem(), miAbout);

        // ProgInfoDialog
        if (ProgData.debug) {
            MTPTester mtpTester = new MTPTester(progData);
            final MenuItem miDebug = new MenuItem("Debugtools");
            miDebug.setOnAction(event -> mtpTester.showDialog());
            mHelp.getItems().addAll(new SeparatorMenuItem(), miDebug);
        }

        menuButton.setTooltip(new Tooltip("Programmeinstellungen anzeigen"));
        menuButton.setMinWidth(Region.USE_PREF_SIZE);
        menuButton.getStyleClass().add("btnFunction");
        menuButton.setText("");
        menuButton.setGraphic(new ProgIcons().FX_ICON_TOOLBAR_MENU_TOP);
        menuButton.getItems().addAll(miConfig, miMedia, mHelp, new SeparatorMenuItem(), miQuit);
    }

    private void selPanelFilm() {
        if (maskerPane.isVisible()) {
            return;
        }

        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
            // dann ist der 2. Klick
            filmGuiPack.closeSplit();
            return;
        }

        setButtonStyle(btnFilm);

        splitPaneFilm.toFront();
        progData.filmGuiController.isShown();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.FILM);
    }

    private void selPanelDownload() {
        if (maskerPane.isVisible()) {
            return;
        }

        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
            // dann ist der 2. Klick
            downloadGuiPack.closeSplit();
            return;
        }

        setButtonStyle(btnDownload);

        progData.downloadGuiController.isShown();
        splitPaneDownoad.toFront();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.DOWNLOAD);
    }

    private void selPanelAbo() {
        if (maskerPane.isVisible()) {
            return;
        }

        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
            // dann ist der 2. Klick
            aboGuiPack.closeSplit();
            return;
        }

        setButtonStyle(btnAbo);

        progData.aboGuiController.isShown();
        splitPaneAbo.toFront();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.ABO);
    }

    private void setButtonStyle(Button btnSel) {
        btnFilm.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();

        if (btnSel.equals(btnFilm)) {
            btnFilm.getStyleClass().add("btnTab-sel");
        } else {
            btnFilm.getStyleClass().add("btnTab");
        }
        if (btnSel.equals(btnDownload)) {
            btnDownload.getStyleClass().add("btnTab-sel");
        } else {
            btnDownload.getStyleClass().add("btnTab");
        }
        if (btnSel.equals(btnAbo)) {
            btnAbo.getStyleClass().add("btnTab-sel");
        } else {
            btnAbo.getStyleClass().add("btnTab");
        }
    }

}
