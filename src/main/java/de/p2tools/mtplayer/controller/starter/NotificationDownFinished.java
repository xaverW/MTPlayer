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

package de.p2tools.mtplayer.controller.starter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.tools.SizeTools;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.guitools.pnotification.P2Notification;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;


public class NotificationDownFinished {

    public void addNotification(DownloadData download, boolean error) {
        String text = ("Film:   " + download.getTitle() + P2LibConst.LINE_SEPARATOR +
                "Sender: " + download.getChannel() + P2LibConst.LINE_SEPARATOR +
                "Größe:  " + SizeTools.humanReadableByteCount(download.getDownloadSize().getFileTargetSize(), true) + P2LibConst.LINE_SEPARATOR +
                (error ? "Download war fehlerhaft" : "Download war erfolgreich"));

        Button btnFilmStart = new Button();
        btnFilmStart.getStyleClass().addAll("btnFunction", "btnFuncTable");
        btnFilmStart.setTooltip(new Tooltip("Gespeicherten Film abspielen"));
        btnFilmStart.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_FILM_PLAY.getImageView());
        btnFilmStart.setOnAction((ActionEvent event) -> {
            P2Open.playStoredFilm(download.getDestPathFile(),
                    ProgConfig.SYSTEM_PROG_PLAY_FILME, ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
        });

        Button btnOpenDirectory = new Button();
        btnOpenDirectory.getStyleClass().addAll("btnFunction", "btnFuncTable");
        btnOpenDirectory.setTooltip(new Tooltip("Ordner mit gespeichertem Film öffnen"));
        btnOpenDirectory.setGraphic(ProgIconsMTPlayer.IMAGE_TABLE_DOWNLOAD_OPEN_DIR.getImageView());
        btnOpenDirectory.setOnAction((ActionEvent event) -> {
            P2Open.openDir(download.getDestPath(),
                    ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
        });

        HBox hBoxBottom = new HBox();
        hBoxBottom.setSpacing(P2LibConst.DIST_HBOX);
        hBoxBottom.setAlignment(Pos.CENTER_RIGHT);
        hBoxBottom.getChildren().addAll(btnFilmStart, btnOpenDirectory);
        add(text, error, hBoxBottom);
    }

    private void add(String text, boolean error, HBox hBoxBottom) {
        if (!ProgConfig.DOWNLOAD_SHOW_NOTIFICATION.getValue()) {
            return;
        }

        P2Notification.addNotification("Download beendet", text,
                error ? P2Notification.STATE.ERROR : P2Notification.STATE.INFO, hBoxBottom);
    }
}





