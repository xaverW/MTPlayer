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

package de.mtplayer.mtp.controller.starter;

import de.mtplayer.mLib.tools.SizeTools;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.data.download.Download;
import javafx.application.Platform;
import org.controlsfx.control.Notifications;


public class MTNotification {

    public static void addNotification(Download download, boolean error) {
        String text = ("Film:   " + download.getTitel() + "\n" +
                "Sender: " + download.getSender() + "\n" +
                "Größe:  " + SizeTools.humanReadableByteCount(download.getDownloadSize().getFilmSize(), true) + "\n" +
                (error ? "Download war fehlerhaft" : "Download war erfolgreich"));

        add(text, error);
    }

    private static void add(String text, boolean error) {
        if (Boolean.parseBoolean(Config.DOWNLOAD_NOTIFICATION.get())) {

            Platform.runLater(() -> {
                if (error) {
                    Notifications.create()
                            .title("Download beendet")
                            .text(text).darkStyle().showError();
                } else {
                    Notifications.create()
                            .title("Download beendet")
                            .text(text).darkStyle().showInformation();
                }
            });

        }
    }
}



