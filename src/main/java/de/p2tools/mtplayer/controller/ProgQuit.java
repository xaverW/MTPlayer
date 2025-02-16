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
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.mtplayer.gui.dialog.QuitDialogController;
import de.p2tools.p2lib.guitools.P2GuiSize;
import de.p2tools.p2lib.mtdownload.HttpDownload;
import de.p2tools.p2lib.tools.P2ShutDown;
import de.p2tools.p2lib.tools.log.P2Log;
import de.p2tools.p2lib.tools.log.P2LogMessage;
import javafx.application.Platform;

public class ProgQuit {

    private ProgQuit() {
    }

    /**
     * Quit the MTPlayer application
     */
    public static void quit() {
        // ResetDialog, QuittDialog (wenn noch Downloads laufen)
        // kann aus der Reihe kommen
        save(false);
    }

    /**
     * Quit the MTPlayer application and shutDown the computer
     */
    public static void quitShutDown() {
        // QuittDialog (wenn noch Downloads laufen)
        // kann aus der Reihe kommen
        save(true);
    }

    /**
     * Quit the MTPlayer application and show QuitDialog
     *
     * @param startWithWaiting starts the dialog with the masker pane
     */
    public static void quit(boolean startWithWaiting) {
        final ProgData progData = ProgData.getInstance();

        // erst mal prüfen, ob noch Downloads (Filme) gestartet sind oder laufen
        if (progData.downloadList.countStartedAndRunningDownloads() > 0 ||
                HttpDownload.downloadRunning > 0) {
            new QuitDialogController(startWithWaiting);

        } else {
            //dann Programm beenden
            save(false);
        }
    }

    private static void save(boolean shutDown) {
        // ProgQuitt-> vor dem Beenden
        // kann aus der Reihe kommen
        ProgData.busy.busyOnFx(Busy.BUSY_SRC.GUI, "Speichern:", -1.0, false);

        if (ProgData.getInstance().primaryStage.isShowing()) {
            P2GuiSize.getSize(ProgConfig.SYSTEM_SIZE_GUI, ProgData.getInstance().primaryStage);
        }

        Platform.runLater(() -> {
            stopAllDownloads();
            writeTabSettings();
            ProgSave.saveAll();
            P2LogMessage.endMsg();


            // ============
            if (ProgConfig.SYSTEM_SIZE_GUI.getValue().equals(ProgData.gui)) {
                P2Log.sysLog("GUI ist gleich");
                P2Log.sysLog("GUI: " + ProgConfig.SYSTEM_SIZE_GUI.getValue());
            } else {
                P2Log.sysLog("GUI: " + ProgData.gui);
                P2Log.sysLog("GUI: " + ProgConfig.SYSTEM_SIZE_GUI.getValue());
            }

            P2Log.sysLog("");
            if (ProgConfig.FILM__FILTER_DIALOG_SIZE.getValue().equals(ProgData.dialog)) {
                P2Log.sysLog("DIALOG ist gleich");
                P2Log.sysLog("DIALOG: " + ProgConfig.FILM__FILTER_DIALOG_SIZE.getValue());
            } else {
                P2Log.sysLog("DIALOG: " + ProgData.dialog);
                P2Log.sysLog("DIALOG: " + ProgConfig.FILM__FILTER_DIALOG_SIZE.getValue());
            }
            // ============


            if (shutDown) {
                P2ShutDown.shutDown(ProgConfig.SYSTEM_SHUT_DOWN_CALL.getValueSafe());
            }

            exitProg();
        });
    }

    private static void exitProg() {
        // dann jetzt beenden -> Tschüss
        Platform.exit();
        System.exit(0);
    }

    private static void stopAllDownloads() {
        // erst mal alle Downloads stoppen und da evtl. auch aus AUTO: kein Dialog
        ProgData.getInstance().downloadList.forEach(download -> {
            if (download.isStateStartedRun()) {
                download.stopDownload(false);
            }
        });
    }

    private static void writeTabSettings() {
        // Tabelleneinstellungen merken
        final ProgData progData = ProgData.getInstance();

        ProgConfig.SYSTEM_GUI_LAST_START_WAS_MAXIMISED.set(progData.primaryStage.isMaximized());
        progData.filmGuiController.saveTable();
        progData.liveFilmGuiController.saveTable();
        progData.downloadGuiController.saveTable();
        progData.aboGuiController.saveTable();
    }
}
