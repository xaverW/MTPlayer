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

package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
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
    private final Label lblHeader = new Label("Downloadfehler");
    private final Button btnOk = new Button("_Ok");

    private final Label lblFilmTitle = new Label("ARD: Tatort, ..");
    private final TextArea txtUrl = new TextArea();
    private final TextArea txtCont = new TextArea();
    private final Label lblTime = new Label("");
    private final ImageView imageView = new ImageView();
    private final GridPane gridPane = new GridPane();

    private Timeline timeline = null;
    private Integer timeSeconds = ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getValue();

    private final String message;
    private final DownloadData download;

    public DownloadErrorDialogController(DownloadData download, String message) {
        super(ProgData.getInstance().primaryStage, ProgConfig.DOWNLOAD_DIALOG_ERROR_SIZE,
                "Fehler", false, false);

        this.download = download;
        this.message = message;

        hBoxTitle = getHBoxTitle();
        vBoxCont = getVBoxCont();

        if (ProgData.autoMode) {
            // dann schaut ja eh keiner zu
            return;
        } else {
            init(true);
        }
    }

    @Override
    public void make() {
        initCont();

        lblFilmTitle.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setText(download.getTitle());

        txtUrl.setEditable(false);
        txtUrl.setWrapText(true);
        String txt = "Url: \n" + download.getUrl() + "\n" +
                "Datei: \n" + download.getDestPathFile();
        txtUrl.setText(txt);

        txtCont.setEditable(false);
        txtCont.setWrapText(true);
        txtCont.setText("Fehler:" + "\n" + message);

        btnOk.setOnAction(event -> {
            stopCounter();
            quit();
        });

        imageView.setImage(ProgIcons.IMAGE_ACHTUNG_64.getImage());

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

        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(5));
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        int row = 0;
        gridPane.add(new Label("Film:"), 0, row);
        gridPane.add(lblFilmTitle, 1, row);

        gridPane.add(txtUrl, 0, ++row, 2, 1);
        gridPane.add(txtCont, 0, ++row, 2, 1);
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(imageView, gridPane);
        HBox.setHgrow(gridPane, Priority.ALWAYS);

        vBoxCont.setPadding(new Insets(5));
        vBoxCont.getChildren().add(hBox);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        addOkButton(btnOk);
        getHboxLeft().getChildren().add(lblTime);
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
