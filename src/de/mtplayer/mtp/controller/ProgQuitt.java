/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

import de.mtplayer.mLib.tools.Duration;
import de.mtplayer.mLib.tools.Log;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.gui.dialog.QuittDialogController;
import de.mtplayer.mtp.gui.tools.GuiSize;
import javafx.application.Platform;

public class ProgQuitt {
    final Daten daten;

    public ProgQuitt() {
        daten = Daten.getInstance();
    }

    private void stopAllDownloads() {
        daten.downloadList.forEach(download ->
        {
            if (download.isStateStartedRun())
                download.stopDownload();
        });
    }

    private void writeWindowSizes() {
        // Hauptfenster
        GuiSize.getSizeScene(Config.SYSTEM_GROESSE_GUI, daten.primaryStage);
    }

    private void writeTabSettings() {
        // Tabelleneinstellungen merken
        daten.filmGuiController.saveTable();
        daten.downloadGuiController.saveTable();
        daten.aboGuiController.saveTable();
    }

    /**
     * Quit the MTPlayer application
     *
     * @param showOptionTerminate show options dialog when downloads are running
     * @param shutDown            try to shutdown the computer if requested
     */
    public void beenden(boolean showOptionTerminate, boolean shutDown) {
        if (beenden_(showOptionTerminate, shutDown)) {

            // dann jetzt beenden -> Thüss
            Platform.runLater(() -> {
                Platform.exit();
                System.exit(0);
            });

        }
    }

    private boolean beenden_(boolean showOptionTerminate, boolean shutDown) {
        if (daten.downloadList.countRunningDownloads() > 0) {

            // erst mal prüfen ob noch Downloads laufen
            if (showOptionTerminate) {
                QuittDialogController quittDialogController = new QuittDialogController(daten);
                if (!quittDialogController.canTerminate()) {
                    return false;
                }
            }
        }

        writeTabSettings();
        stopAllDownloads();
        writeWindowSizes();

        new ProgSave().allesSpeichern();

        Log.endMsg();
        Duration.printCounter();

        return true;
    }

}
