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
import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.mtplayer.controller.update.SearchProgramUpdate;
import de.p2tools.mtplayer.gui.configdialog.ConfigDialogController;
import de.p2tools.mtplayer.gui.dialog.AboutDialogController;
import de.p2tools.mtplayer.gui.dialog.ImportMVDialog;
import de.p2tools.mtplayer.gui.dialog.ResetDialogController;
import de.p2tools.mtplayer.gui.mediaconfig.MediaConfigDialogController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.ProgTipOfDayFactory;
import de.p2tools.p2lib.guitools.POpen;
import de.p2tools.p2lib.tools.log.PLogger;
import de.p2tools.p2lib.tools.shortcut.PShortcutWorker;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

public class MTPlayerMenu extends MenuButton {
    public MTPlayerMenu() {
        makeButton();
    }

    private void makeButton() {
        ProgData progData = ProgData.getInstance();
        // Menü
        final MenuItem miConfig = new MenuItem("Einstellungen des Programms");
        miConfig.setOnAction(e -> new ConfigDialogController(ProgData.getInstance()).showDialog());
        miConfig.disableProperty().bind(ConfigDialogController.dialogIsRunning);

        final MenuItem miMediaCollectionConfig = new MenuItem("Einstellungen der Mediensammlung");
        miMediaCollectionConfig.setOnAction(e -> new MediaConfigDialogController());

        final MenuItem miSearchMediaCollection = new MenuItem("Mediensammlung durchsuchen");
        miSearchMediaCollection.setOnAction(a -> new MediaDialogController(""));
        PShortcutWorker.addShortCut(miSearchMediaCollection, ProgShortcut.SHORTCUT_SEARCH_MEDIACOLLECTION);

        final MenuItem miQuit = new MenuItem("Beenden");
        miQuit.setOnAction(e -> ProgQuit.quit(false));
        PShortcutWorker.addShortCut(miQuit, ProgShortcut.SHORTCUT_QUIT_PROGRAM);

        //Menüpunkt ist nur, dass das Tastenkürzel dafür funkt., wird aber sonst nicht angezeigt
        final MenuItem miQuitWait = new MenuItem("Beenden, laufende Downloads abwarten");
        miQuitWait.setVisible(false); //ist wegen des shortcuts, der zusätzliche Menüpunkt verwirrt nur
        miQuitWait.setOnAction(e -> ProgQuit.quit(true));
        PShortcutWorker.addShortCut(miQuitWait, ProgShortcut.SHORTCUT_QUIT_PROGRAM_WAIT);

        setTooltip(new Tooltip("Programmeinstellungen anzeigen"));
        setMinWidth(Region.USE_PREF_SIZE);
        getStyleClass().addAll("btnFunction", "btnFunc-1");
        setText("");
        setGraphic(ProgIcons.Icons.ICON_TOOLBAR_MENU_TOP.getImageView());
        getItems().addAll(miConfig, miMediaCollectionConfig, miSearchMediaCollection, addHelp(progData),
                new SeparatorMenuItem(), miQuit, miQuitWait);
    }

    private Menu addHelp(ProgData progData) {
        final MenuItem miUrlHelp = new MenuItem("Anleitung im Web");
        miUrlHelp.setOnAction(event -> {
            POpen.openURL(ProgConst.URL_WEBSITE_HELP,
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        });
        final MenuItem miLog = new MenuItem("Logdatei öffnen");
        miLog.setOnAction(event -> {
            PLogger.openLogFile();
        });
        final MenuItem miReset = new MenuItem("Einstellungen zurücksetzen");
        miReset.setOnAction(event -> new ResetDialogController(progData));
        final MenuItem miImportMV = new MenuItem("MediathekView Einstellungen importieren");
        miImportMV.setOnAction(event -> new ImportMVDialog(progData));

        final MenuItem miToolTip = new MenuItem("Tip des Tages");
        miToolTip.setOnAction(a -> ProgTipOfDayFactory.showDialog(progData, true));
        final MenuItem miSearchUpdate = new MenuItem("Gibt's ein Update?");
        miSearchUpdate.setOnAction(a -> new SearchProgramUpdate(progData, progData.primaryStage).searchNewProgramVersion(true));
        final MenuItem miAbout = new MenuItem("Über dieses Programm");
        miAbout.setOnAction(event -> new AboutDialogController(progData).showDialog());

        final Menu mHelp = new Menu("Hilfe");
        mHelp.getItems().addAll(miUrlHelp, miLog, miReset, miImportMV,
                miToolTip, miSearchUpdate, new SeparatorMenuItem(), miAbout);

        if (ProgData.debug) {
            final MenuItem miDebug = new MenuItem("Debugtools");
            miDebug.setOnAction(event -> {
                MTPTester mtpTester = new MTPTester(progData);
                mtpTester.showDialog();
            });
            final MenuItem miSave = new MenuItem("Alles Speichern");
            miSave.setOnAction(a -> ProgSave.saveAll());

            mHelp.getItems().addAll(new SeparatorMenuItem(), miDebug, miSave);
        }
        return mHelp;
    }
}
