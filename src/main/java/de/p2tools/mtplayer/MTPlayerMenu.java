/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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
import de.p2tools.mtplayer.controller.config.PShortcut;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.picon.IconRunner;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.controller.picon.PIconShow;
import de.p2tools.mtplayer.controller.update.SearchProgramUpdate;
import de.p2tools.mtplayer.controller.update.WhatsNewFactory;
import de.p2tools.mtplayer.gui.configdialog.ConfigDialogController;
import de.p2tools.mtplayer.gui.dialog.AboutDialogController;
import de.p2tools.mtplayer.gui.dialog.ImportMVDialog;
import de.p2tools.mtplayer.gui.dialog.ResetDialogController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.ProgTipOfDayFactory;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.tools.log.P2Logger;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutWorker;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.javafx.FontIcon;

public class MTPlayerMenu extends MenuButton {
    public MTPlayerMenu() {
        makeButton();
        setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() > 1) {
                    ProgConfig.SYSTEM_GUI_THEME_1.set(!ProgConfig.SYSTEM_GUI_THEME_1.get());
                }
            }

            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.SYSTEM_DARK_THEME.set(!ProgConfig.SYSTEM_DARK_THEME.get());
            }
        });
    }

    private void makeButton() {
        ProgData progData = ProgData.getInstance();
        // Menü
        final MenuItem miConfig = new MenuItem("Einstellungen des Programms");
        miConfig.setOnAction(e -> new ConfigDialogController(ProgData.getInstance()));
        miConfig.disableProperty().bind(ConfigDialogController.dialogIsRunning);

        final MenuItem miSearchMediaCollection = new MenuItem("Mediensammlung");
        miSearchMediaCollection.setOnAction(a -> new MediaDialogController("", ""));
        P2ShortcutWorker.addShortCut(miSearchMediaCollection, PShortcut.SHORTCUT_SEARCH_MEDIACOLLECTION);

        final CheckMenuItem miDarkMode = new CheckMenuItem("Dunkle Oberfläche");
        miDarkMode.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_DARK_THEME);

        final CheckMenuItem miColorMode = new CheckMenuItem("Farb-Modus-1");
        miColorMode.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_THEME_1);

        final MenuItem miQuit = new MenuItem("Beenden");
        miQuit.setOnAction(e -> ProgQuit.quit(false));
        P2ShortcutWorker.addShortCut(miQuit, PShortcut.SHORTCUT_QUIT_PROGRAM);

        setTooltip(new Tooltip("Programmeinstellungen anzeigen"));
        setMinWidth(Region.USE_PREF_SIZE);
        getStyleClass().addAll("pFuncBtn", "btnProgMenu");
        setText("");
        FontIcon node = PIconFactory.PICON.PROG_MENU.getFontIcon();
        node.setScaleX(1.5);
        setGraphic(node);

        getItems().addAll(miConfig, miSearchMediaCollection, miDarkMode, miColorMode);
        addMenuButton();
        getItems().addAll(addHelp(progData), new SeparatorMenuItem(), miQuit);
    }

    private void addMenuButton() {
        final CheckMenuItem miFilm = new CheckMenuItem("Rechte Menüleiste");
        miFilm.visibleProperty().bind(MTPlayerController.TAB_FILM_ON);
        miFilm.selectedProperty().bindBidirectional(ProgConfig.FILM_GUI_SHOW_MENU);

        final CheckMenuItem miLiveFilm = new CheckMenuItem("Rechte Menüleiste");
        miLiveFilm.visibleProperty().bind(MTPlayerController.TAB_LIVE_ON);
        miLiveFilm.selectedProperty().bindBidirectional(ProgConfig.LIVE_FILM_GUI_SHOW_MENU);

        final CheckMenuItem miDownload = new CheckMenuItem("Rechte Menüleiste");
        miDownload.visibleProperty().bind(MTPlayerController.TAB_DOWNLOAD_ON);
        miDownload.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_SHOW_MENU);

        final CheckMenuItem miAbo = new CheckMenuItem("Rechte Menüleiste");
        miAbo.visibleProperty().bind(MTPlayerController.TAB_ABO_ON);
        miAbo.selectedProperty().bindBidirectional(ProgConfig.ABO_GUI_SHOW_MENU);

        getItems().addAll(miFilm, miLiveFilm, miDownload, miAbo);
    }

    private Menu addHelp(ProgData progData) {
        final MenuItem miUrlHelp = new MenuItem("Anleitung im Web");
        miUrlHelp.setOnAction(event -> {
            P2Open.openURL(ProgConst.URL_WEBSITE_HELP,
                    ProgConfig.SYSTEM_PROG_OPEN_URL, PIconFactory.PICON.BTN_DIR_OPEN.getFontIcon());
        });
        final MenuItem miLog = new MenuItem("Logdatei öffnen");
        miLog.setOnAction(event -> {
            P2Logger.openLogFile();
        });
        final MenuItem miShortCut = new MenuItem("Tastaturbefehle festlegen");
        miShortCut.setOnAction(event -> {
            ProgConfig.SYSTEM_CONFIG_DIALOG_TAB.setValue(0);
            ProgConfig.SYSTEM_CONFIG_DIALOG_CONFIG.setValue(8);
            new ConfigDialogController(ProgData.getInstance());
        });
        final MenuItem miReset = new MenuItem("Einstellungen zurücksetzen");
        miReset.setOnAction(event -> new ResetDialogController(progData));
        final MenuItem miImportMV = new MenuItem("MediathekView Einstellungen importieren");
        miImportMV.setOnAction(event -> new ImportMVDialog(progData));

        final MenuItem miToolTip = new MenuItem("Tip des Tages");
        miToolTip.setOnAction(a -> ProgTipOfDayFactory.showDialog(progData, true));
        final MenuItem miWhatsNew = new MenuItem("Was ist neu?");
        miWhatsNew.setOnAction(a -> WhatsNewFactory.showWhatsNew(true));
        final MenuItem miSearchUpdate = new MenuItem("Gibt's ein Update?");
        miSearchUpdate.setOnAction(a -> new SearchProgramUpdate(progData).searchNewProgramVersion(true));
        final MenuItem miAbout = new MenuItem("Über dieses Programm");
        miAbout.setOnAction(event -> new AboutDialogController(progData).showDialog());

        final Menu mHelp = new Menu("Hilfe");
        mHelp.getItems().addAll(miUrlHelp, miLog, miShortCut, miReset, miImportMV,
                miToolTip, new SeparatorMenuItem(), miWhatsNew, miSearchUpdate, miAbout);

        if (ProgData.debug) {
            final MenuItem miDebug = new MenuItem("Debugtools");
            miDebug.setOnAction(event -> {
                MTPTester mtpTester = new MTPTester(progData);
                mtpTester.showDialog();
            });
            final MenuItem miFilmlist = new MenuItem("Filmliste");
            miFilmlist.setOnAction(a -> progData.checkForNewFilmlist.foundNewListProperty()
                    .set(!progData.checkForNewFilmlist.foundNewListProperty().get()));

            final MenuItem miSearchAllUpdate = new MenuItem("Alle Programm-Downloads anzeigen");
            miSearchAllUpdate.setOnAction(a -> new SearchProgramUpdate(progData)
                    .searchNewProgramVersion());

            final MenuItem miIcon = new MenuItem("Icons");
            miIcon.setOnAction(a -> new PIconShow());

            final MenuItem miIconRunner = new MenuItem("Icon-Zirkus");
            miIconRunner.setOnAction(a -> IconRunner.run());

            final MenuItem miResetTodayDone = new MenuItem("<SYSTEM_SEARCH_UPDATE_TODAY_DONE> zurücksetzen");
            miResetTodayDone.setOnAction(a -> {
                ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.set("2020.01.01"); // heute noch nicht gemacht
            });
            final MenuItem miResetLastSearch = new MenuItem("<SYSTEM_SEARCH_UPDATE_LAST_DATE> zurücksetzen");
            miResetLastSearch.setOnAction(a -> {
                ProgConfig.SYSTEM_SEARCH_UPDATE_LAST_DATE.set("2020.01.01"); // letztes Datum, bis zu dem geprüft wurde, wenn leer wird das buildDate genommen
            });
            final MenuItem miResetUpdate = new MenuItem("<SYSTEM_SEARCH_UPDATE_TODAY_DONE und \n" +
                    "SYSTEM_SEARCH_UPDATE_LAST_DATE> zurücksetzen");
            miResetUpdate.setOnAction(a -> {
                ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.set("2020.01.01"); // heute noch nicht gemacht
                ProgConfig.SYSTEM_SEARCH_UPDATE_LAST_DATE.set("2020.01.01"); // letztes Datum, bis zu dem geprüft wurde, wenn leer wird das buildDate genommen
            });

            final MenuItem miSave = new MenuItem("Alles Speichern");
            miSave.setOnAction(a -> ProgSave.saveAll());

            mHelp.getItems().addAll(new SeparatorMenuItem(), miDebug, miFilmlist, miSearchAllUpdate, miIcon, miIconRunner,
                    miResetTodayDone, miResetLastSearch, miResetUpdate, miSave);
        }
        return mHelp;
    }
}
