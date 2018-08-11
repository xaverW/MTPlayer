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

package de.mtplayer.mtp.controller;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.dialog.QuitDialogController;
import de.p2tools.p2Lib.guiTools.PGuiSize;
import de.p2tools.p2Lib.tools.log.LogMsg;
import javafx.application.Platform;

public class ProgQuit {
    final ProgData progData;

    public ProgQuit() {
        progData = ProgData.getInstance();
    }

    private void stopAllDownloads() {
        progData.downloadList.forEach(download ->
        {
            if (download.isStateStartedRun())
                download.stopDownload();
        });
    }

    private void writeWindowSizes() {
        // Hauptfenster
        PGuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI.getStringProperty(), progData.primaryStage);
    }

    private void writeTabSettings() {
        // Tabelleneinstellungen merken
        progData.filmGuiController.saveTable();
        progData.downloadGuiController.saveTable();
        progData.aboGuiController.saveTable();
    }

    /**
     * Quit the MTPlayer application
     *
     * @param showOptionTerminate show options dialog when downloads are running
     * @param shutDown            try to shutdown the computer if requested
     */
    public void quit(boolean showOptionTerminate, boolean shutDown) {
        if (quit_(showOptionTerminate, shutDown)) {

            // dann jetzt beenden -> Thüss
            Platform.runLater(() -> {
                Platform.exit();
                System.exit(0);
            });

        }
    }

    private boolean quit_(boolean showOptionTerminate, boolean shutDown) {
        if (progData.downloadList.countRunningDownloads() > 0) {

            // erst mal prüfen ob noch Downloads laufen
            if (showOptionTerminate) {
                QuitDialogController quitDialogController = new QuitDialogController(progData);
                if (!quitDialogController.canTerminate()) {
                    return false;
                }
            }
        }

        writeTabSettings();
        stopAllDownloads();
        writeWindowSizes();

        new ProgSave().saveAll();

        LogMsg.endMsg();

        return true;
    }

}
