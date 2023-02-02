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

package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AutomodeContinueDialogController extends PDialogExtra {

    private final VBox vBoxCont;
//    private final HBox hBoxOk;

    private final Button btnCancel;
    private final Button btnContinue;

    private GridPane gridPane = new GridPane();
    private boolean continueAutomode = true;

    private Timeline timeline = null;
    private Integer timeSeconds = ProgConfig.SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS.getValue();

    public AutomodeContinueDialogController() {
        super(ProgData.getInstance().primaryStage, null, "Automodus", true, false);

        vBoxCont = getVBoxCont();
//        hBoxOk = getHboxOk();

        btnCancel = new Button("_Programm nicht beenden");
        btnContinue = new Button("_Beenden in " + timeSeconds + " s");

        init(true);
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
        Label lblHeader2 = new Label("das Programm wird sofort wieder beendet.");
        GridPane.setHalignment(lblHeader1, HPos.CENTER);
        GridPane.setHalignment(lblHeader2, HPos.CENTER);

        int row = 0;
        gridPane.add(lblHeader1, 0, row);
        gridPane.add(lblHeader2, 0, ++row);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

        vBoxCont.setPadding(new Insets(15));
        vBoxCont.getChildren().addAll(gridPane);

        getHboxLeft().getChildren().add(new Label("Wie möchten Sie forfahren?"));
        addOkCancelButtons(btnContinue, btnCancel);
    }

    public boolean isContinueAutomode() {
        return continueAutomode;
    }

    private void handleCountDownAction() {
        timeSeconds--;
        if (timeSeconds > 0) {
            btnContinue.setText("_Beenden in " + timeSeconds + " s");
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

