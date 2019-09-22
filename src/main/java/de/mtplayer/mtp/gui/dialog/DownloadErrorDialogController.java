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

package de.mtplayer.mtp.gui.dialog;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.download.Download;
import de.p2tools.p2Lib.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DownloadErrorDialogController extends PDialogExtra {

    private final HBox hBoxTitle;
    private final VBox vBoxCont;
    private final HBox hBoxOk;

    private Label lblHeader = new Label("Downloadfehler");
    private Button btnOk = new Button("Ok");

    private Label lblFilmTitle = new Label("ARD: Tatort, ..");
    private Label lblUrl = new Label();
    private TextArea txtCont = new TextArea();

    private Label lblTime = new Label("");

    private ImageView imageView = new ImageView();
    private GridPane gridPane = new GridPane();


    private Timeline timeline = null;
    private Integer timeSeconds = ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getInt();

    private final String message;
    private final Download download;

    public DownloadErrorDialogController(Download download, String message) {
        super(ProgConfig.DOWNLOAD_DIALOG_ERROR_SIZE.getStringProperty(),
                "Fehler", true);

        this.download = download;
        this.message = message;

        hBoxTitle = getHBoxTitle();
        vBoxCont = getVboxCont();
        hBoxOk = getHboxOk();

        init(getVBoxCompleteDialog(), true);
    }

    @Override
    public void make() {
        initCont();

        lblFilmTitle.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setText(download.getTitle());

        lblUrl.setText(download.getUrl());

        txtCont.setEditable(false);
        txtCont.setText(message);

        btnOk.setOnAction(event -> {
            stopCounter();
            quit();
        });

        imageView.setImage(new ProgIcons().IMAGE_ACHTUNG_64);

        //start the countdown...
        lblTime.setText("");
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                new CountdownAction()));
        timeline.playFromStart();

    }

    private void initCont() {
        hBoxTitle.getChildren().add(lblHeader);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20));
        vBox.getChildren().add(imageView);


        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        int row = 0;
        gridPane.add(new Label("Film:"), 0, row);
        gridPane.add(lblFilmTitle, 1, row);

        gridPane.add(new Label("URL:"), 0, ++row);
        gridPane.add(lblUrl, 1, row);

        GridPane.setHgrow(txtCont, Priority.ALWAYS);
        GridPane.setVgrow(txtCont, Priority.ALWAYS);
        gridPane.add(new Label("Fehler:"), 0, ++row);
        gridPane.add(txtCont, 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        HBox hBox = new HBox(10);
        VBox.setVgrow(hBox, Priority.ALWAYS);
        hBox.getChildren().addAll(vBox, gridPane);

        vBoxCont.setPadding(new Insets(5));
        vBoxCont.setSpacing(10);
        vBoxCont.getChildren().add(hBox);

        hBoxOk.getChildren().addAll(lblTime, btnOk);
    }

    private class CountdownAction implements EventHandler {

        @Override
        public void handle(Event event) {
            timeSeconds--;
            if (timeSeconds > 0) {
                lblTime.setText(timeSeconds + "");
            } else {
                stopCounter();
                quit();
            }
        }
    }

    private void stopCounter() {
        if (timeline != null) {
            timeline.stop();
        }

    }

    private void quit() {
        close();
    }


}
