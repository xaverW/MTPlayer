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
import de.p2tools.p2Lib.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AutomodeContinueDialogController extends PDialogExtra {

    private final VBox vBoxCont;
    private final HBox hBoxOk;

    private final Button btnCancel;
    private final Button btnContinue;

    private GridPane gridPane = new GridPane();
    private boolean continueAutomode = true;

    private Timeline timeline = null;
    private Integer timeSeconds = ProgConfig.SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS.getInt();

    public AutomodeContinueDialogController() {
        super(null, "Automodus", true);

        vBoxCont = getVboxCont();
        hBoxOk = getHboxOk();

        btnCancel = new Button("Programm nicht beenden");
        btnContinue = new Button("Beenden in " + timeSeconds + " s");

        init(getVBoxCompleteDialog(), true);
    }

    @Override
    public void make() {
        initCont();
        initButton();

        //start the countdown...
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> handleCountDownAction()));
        timeline.playFromStart();

    }

    private void initCont() {
        // Gridpane
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        Label lblHeader1 = new Label("Automodus und keine Downloads,");
        Label lblHeader2 = new Label("das Programm wird sofort wieder beendet");
        GridPane.setHalignment(lblHeader1, HPos.CENTER);
        GridPane.setHalignment(lblHeader2, HPos.CENTER);

        int row = 0;
        gridPane.add(lblHeader1, 0, row);
        gridPane.add(lblHeader2, 0, ++row);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

        vBoxCont.setPadding(new Insets(15));
        vBoxCont.getChildren().addAll(gridPane);

        HBox hBox = new HBox();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(new Label("Wie möchten Sie forfahren?"));
        hBoxOk.getChildren().addAll(hBox, btnContinue, btnCancel);
    }

    public boolean isContinueAutomode() {
        return continueAutomode;
    }

    private void handleCountDownAction() {
        timeSeconds--;
        if (timeSeconds > 0) {
            btnContinue.setText("Beenden in " + timeSeconds + " s");
        } else {
            timeline.stop();
            continueAutomode = true;
            quit();
        }
    }

    private void stopCounter() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void quit() {
        stopCounter();
        close();
    }


    private void initButton() {
        btnCancel.setOnAction(event -> {
            continueAutomode = false;
            quit();
        });

        btnContinue.setOnAction(event -> {
            continueAutomode = true;
            quit();
        });
    }

}

