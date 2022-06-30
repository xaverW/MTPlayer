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
import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.MTShortcut;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.mtplayer.gui.AboGuiPack;
import de.p2tools.mtplayer.gui.DownloadGuiPack;
import de.p2tools.mtplayer.gui.FilmGuiPack;
import de.p2tools.mtplayer.gui.StatusBarController;
import de.p2tools.mtplayer.gui.configDialog.ConfigDialogController;
import de.p2tools.mtplayer.gui.dialog.AboutDialogController;
import de.p2tools.mtplayer.gui.dialog.ResetDialogController;
import de.p2tools.mtplayer.gui.mediaConfig.MediaConfigDialogController;
import de.p2tools.mtplayer.gui.mediaDialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.ProgTipOfDay;
import de.p2tools.mtplayer.tools.update.SearchProgramUpdate;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;
import de.p2tools.p2Lib.tools.shortcut.PShortcutWorker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
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
        btnStop.setGraphic(ProgIcons.Icons.ICON_BUTTON_STOP.getImageView());
        btnStop.setOnAction(a -> progData.loadFilmlist.setStop(true));
    }

    private void initButton() {
        btnFilmlist.setMinWidth(Region.USE_PREF_SIZE);
        btnFilmlist.getStyleClass().add("btnFilmlist");
        btnFilmlist.setTooltip(new Tooltip("Eine neue Filmliste laden.\n" +
                "Wenn die Filmliste nicht zu alt ist, wird nur ein Update geladen.\n" +
                "Mit der rechten Maustaste wird immer die komplette Filmliste geladen."));
        btnFilmlist.setOnAction(e -> {
            progData.loadFilmlist.loadNewFilmlistFromServer();
        });
        btnFilmlist.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                progData.loadFilmlist.loadNewFilmlistFromServer(true);
            }
        });


        progData.loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {

            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                // falls "neue Filmliste" aktiv ist
                btnFilmlist.getStyleClass().clear();
                btnFilmlist.getStyleClass().add("btnFilmlist");
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                if (stackPaneCont.getChildren().size() == 0) {
                    return;
                }
                setFocus();
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

        infoPane();

        // Menü
        final MenuItem miConfig = new MenuItem("Einstellungen des Programms");
        miConfig.setOnAction(e -> ConfigDialogController.getInstanceAndShow());

        final MenuItem miMediaCollectionConfig = new MenuItem("Einstellungen der Mediensammlung");
        miMediaCollectionConfig.setOnAction(e -> new MediaConfigDialogController());

        final MenuItem miSearchMediaCollection = new MenuItem("Mediensammlung durchsuchen");
        miSearchMediaCollection.setOnAction(a -> new MediaDialogController(""));
        PShortcutWorker.addShortCut(miSearchMediaCollection, MTShortcut.SHORTCUT_SEARCH_MEDIACOLLECTION);

        final MenuItem miQuit = new MenuItem("Beenden");
        miQuit.setOnAction(e -> ProgQuit.quit(false));
        PShortcutWorker.addShortCut(miQuit, MTShortcut.SHORTCUT_QUIT_PROGRAM);

        final MenuItem miQuitWait = new MenuItem("Beenden, laufende Downloads abwarten");
        miQuitWait.setVisible(false); // wegen dem shortcut, aber der zusätzliche Menüpunkt verwirrt nur
        miQuitWait.setOnAction(e -> ProgQuit.quit(true));
        PShortcutWorker.addShortCut(miQuitWait, MTShortcut.SHORTCUT_QUIT_PROGRAM_WAIT);

        final MenuItem miAbout = new MenuItem("Über dieses Programm");
        miAbout.setOnAction(event -> AboutDialogController.getInstanceAndShow());

        final MenuItem miLog = new MenuItem("Logdatei öffnen");
        miLog.setOnAction(event -> {
            PLogger.openLogFile();
        });

        final MenuItem miUrlHelp = new MenuItem("Anleitung im Web");
        miUrlHelp.setOnAction(event -> {
            POpen.openURL(ProgConst.URL_WEBSITE_HELP,
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        });

        final MenuItem miReset = new MenuItem("Einstellungen zurücksetzen");
        miReset.setOnAction(event -> new ResetDialogController(progData));

        final MenuItem miToolTip = new MenuItem("Tip des Tages");
        miToolTip.setOnAction(a -> new ProgTipOfDay().showDialog(progData, true));

        final MenuItem miSearchUpdate = new MenuItem("Gibts ein Update?");
        miSearchUpdate.setOnAction(a -> new SearchProgramUpdate(progData, progData.primaryStage).searchNewProgramVersion(true));

        final Menu mHelp = new Menu("Hilfe");
        mHelp.getItems().addAll(miUrlHelp, miLog, miReset, miToolTip, miSearchUpdate, new SeparatorMenuItem(), miAbout);

        final MenuItem mbExternProgram = new MenuItem("Externes Programm starten");
        mbExternProgram.setVisible(false); //vorerst mal noch nicht anzeigen???
        mbExternProgram.setOnAction(e ->
                POpen.openExternProgram(progData.primaryStage,
                        ProgConfig.SYSTEM_PROG_EXTERN_PROGRAM, ProgIcons.Icons.ICON_BUTTON_EXTERN_PROGRAM.getImageView())
        );
        PShortcutWorker.addShortCut(mbExternProgram, MTShortcut.SHORTCUT_EXTERN_PROGRAM);

        // ProgInfoDialog
        if (ProgData.debug) {
            final MenuItem miDebug = new MenuItem("Debugtools");
            miDebug.setOnAction(event -> {
                MTPTester mtpTester = new MTPTester(progData);
                mtpTester.showDialog();
            });
            final MenuItem miSave = new MenuItem("Alles Speichern");
            miSave.setOnAction(a -> new ProgSave().saveAll());

            mHelp.getItems().addAll(new SeparatorMenuItem(), miDebug, miSave);
        }

        menuButton.setTooltip(new Tooltip("Programmeinstellungen anzeigen"));
        menuButton.setMinWidth(Region.USE_PREF_SIZE);
        menuButton.getStyleClass().add("btnFunctionWide");
        menuButton.setText("");
        menuButton.setGraphic(ProgIcons.Icons.FX_ICON_TOOLBAR_MENU_TOP.getImageView());
        menuButton.getItems().addAll(miConfig, miMediaCollectionConfig, miSearchMediaCollection, mHelp,
                new SeparatorMenuItem(), miQuit, miQuitWait, mbExternProgram);
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
        splitPaneDownoad.toFront();
        progData.downloadGuiController.isShown();
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
        splitPaneAbo.toFront();
        progData.aboGuiController.isShown();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.ABO);
    }

    private void infoPane() {
        btnFilm.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() ||
                    !stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.FILM_GUI_DIVIDER_ON.setValue(!ProgConfig.FILM_GUI_DIVIDER_ON.getValue());
            }
        });
        btnDownload.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() ||
                    !stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue());
            }
        });
        btnAbo.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() ||
                    !stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.ABO_GUI_DIVIDER_ON.setValue(!ProgConfig.ABO_GUI_DIVIDER_ON.getValue());
            }
        });
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

    public void setFilter() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
            ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.getValue());
        } else if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
            ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.getValue());
        } else if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
            ProgConfig.ABO_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.ABO_GUI_FILTER_DIVIDER_ON.getValue());
        }
    }

    public void setInfos() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
            ProgConfig.FILM_GUI_DIVIDER_ON.setValue(!ProgConfig.FILM_GUI_DIVIDER_ON.getValue());
        } else if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
            ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue());
        } else if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
            ProgConfig.ABO_GUI_DIVIDER_ON.setValue(!ProgConfig.ABO_GUI_DIVIDER_ON.getValue());
        }
    }

    public boolean isFilmPaneShown() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDownloadPaneShown() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAboPaneShown() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
            return true;
        } else {
            return false;
        }
    }

    public void setFocus() {
        Node node = stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1);
        if (node != null && node == splitPaneFilm) {
            progData.filmGuiController.isShown();
        }
        if (node != null && node == splitPaneDownoad) {
            progData.downloadGuiController.isShown();
        }
        if (node != null && node == splitPaneAbo) {
            progData.aboGuiController.isShown();
        }
    }
}
