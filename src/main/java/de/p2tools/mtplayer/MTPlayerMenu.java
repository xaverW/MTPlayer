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
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.MTShortcut;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.update.SearchProgramUpdate;
import de.p2tools.mtplayer.gui.configDialog.ConfigDialogController;
import de.p2tools.mtplayer.gui.dialog.AboutDialogController;
import de.p2tools.mtplayer.gui.dialog.ResetDialogController;
import de.p2tools.mtplayer.gui.mediaConfig.MediaConfigDialogController;
import de.p2tools.mtplayer.gui.mediaDialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.ProgTipOfDay;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.tools.log.PLogger;
import de.p2tools.p2Lib.tools.shortcut.PShortcutWorker;
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
        miAbout.setOnAction(event -> new AboutDialogController(progData).showDialog());

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

        final MenuItem miSearchUpdate = new MenuItem("Gibt's ein Update?");
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

        setTooltip(new Tooltip("Programmeinstellungen anzeigen"));
        setMinWidth(Region.USE_PREF_SIZE);
        getStyleClass().addAll("btnFunction", "btnFunc-1");
        setText("");
        setGraphic(ProgIcons.Icons.FX_ICON_TOOLBAR_MENU_TOP.getImageView());
        getItems().addAll(miConfig, miMediaCollectionConfig, miSearchMediaCollection, mHelp,
                new SeparatorMenuItem(), miQuit, miQuitWait, mbExternProgram);
    }
}
