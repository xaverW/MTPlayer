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

package de.mtplayer.mtp.gui.dialog;

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.download.Download;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class DownloadErrorDialogController extends MTDialog {

    @FXML
    private HBox hboxTitle;
    @FXML
    private Label lblHeader;
    @FXML
    private Button btnOk;

    @FXML
    private Label lblFilmTitle;
    @FXML
    private Label lblUrl;
    @FXML
    private TextArea txtCont;

    @FXML
    private Label lblTime;

    @FXML
    private ImageView imageView;
    @FXML
    private GridPane gridPane;


    private Timeline timeline = null;
    private Integer timeSeconds = Config.SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SEKUNDEN.getInt();

    private final String message;
    private final Download download;

    public DownloadErrorDialogController(Download download, String message) {
        super("/de/mtplayer/mtp/gui/dialog/DownloadErrorDialog.fxml",
                Config.DOWNOAD_DIALOG_ERROR_GROESSE,
                "Fehler", true);

        this.download = download;
        this.message = message;

        init(true);

    }

    @Override
    public void make() {
        gridPane.getStyleClass().add("dialog-border");

        hboxTitle.getStyleClass().add("dialog-title-border");
        lblHeader.setStyle("-fx-font-weight: bold;");

        lblFilmTitle.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setText(download.getTitel());

        lblUrl.setText(download.getUrl());

        txtCont.setEditable(false);
        txtCont.setText(message);

        btnOk.setOnAction(event -> {
            stopCounter();
            beenden();
        });

        imageView.setImage(new Icons().IMAGE_ACHTUNG_32);

        //start the countdown...
        lblTime.setText("");
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                new CountdownAction()));
        timeline.playFromStart();

    }


    private class CountdownAction implements EventHandler {

        @Override
        public void handle(Event event) {
            timeSeconds--;
            if (timeSeconds > 0) {
                lblTime.setText(timeSeconds + "");
            } else {
                stopCounter();
                beenden();
            }
        }
    }

    private void stopCounter() {
        if (timeline != null) {
            timeline.stop();
        }

    }

    private void beenden() {
        close();
    }


}
