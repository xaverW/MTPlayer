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

package de.p2tools.mtplayer.controller;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.dialog.QuitDialogController;
import de.p2tools.p2Lib.guiTools.PGuiSize;
import de.p2tools.p2Lib.tools.PShutDown;
import de.p2tools.p2Lib.tools.ProgramTools;
import de.p2tools.p2Lib.tools.log.LogMessage;
import javafx.application.Platform;

public class ProgQuit {

    private ProgQuit() {
    }

    /**
     * Quit the MTPlayer application
     */
    public static void quit() {
        saveConfig();
        exitProg();
    }

    /**
     * Quit the MTPlayer application and shutDown the computer
     */
    public static void quitShutDown() {
        saveConfig();
        PShutDown.shutDown();
        exitProg();
    }

    /**
     * Quit the MTPlayer application and show QuitDialog
     *
     * @param startWithWaiting starts the dialog with the masker pane
     */
    public static void quit(boolean startWithWaiting) {
        final ProgData progData = ProgData.getInstance();

        // erst mal prüfen ob noch Downloads gestartet sind oder laufen
        if (progData.downloadList.countStartedAndRunningDownloads() > 0) {
            QuitDialogController quitDialogController;
            if (progData.quitDialogController != null) {
                progData.quitDialogController.getStage().toFront();
                quitDialogController = progData.quitDialogController;
                if (startWithWaiting) {
                    quitDialogController.startWaiting();
                }
            } else {
                quitDialogController = new QuitDialogController(startWithWaiting);
            }

        } else {
            //dann Programm beenden
            saveConfig();
            exitProg();
        }
    }

    private static void saveConfig() {
        ProgConfig.SYSTEM_PROG_VERSION.setValue(ProgramTools.getProgVersion());
        ProgConfig.SYSTEM_PROG_BUILD_NO.setValue(ProgramTools.getBuild());
        ProgConfig.SYSTEM_PROG_BUILD_DATE.setValue(ProgramTools.getCompileDate());

        stopAllDownloads();
        writeTabSettings();
        new ProgSave().saveAll();
        LogMessage.endMsg();
    }

    private static void exitProg() {
        // dann jetzt beenden -> Thüss
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private static void stopAllDownloads() {
        ProgData.getInstance().downloadList.forEach(download -> {
            if (download.isStateStartedRun())
                download.stopDownload();
        });
    }

    private static void writeTabSettings() {
        // Tabelleneinstellungen merken
        final ProgData progData = ProgData.getInstance();
        progData.filmGuiController.saveTable();
        progData.downloadGuiController.saveTable();
        progData.aboGuiController.saveTable();
        // Hauptfenster
        PGuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI.getStringProperty(), ProgData.getInstance().primaryStage);
    }
}
