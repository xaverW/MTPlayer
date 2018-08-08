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

package de.mtplayer.mtp.gui.startDialog;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.configDialog.GeoPane;
import de.p2tools.p2Lib.dialog.PDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class StartDialogController extends PDialog {

    private Stage stage = null;
    private boolean ok = false;
    private AnchorPane rootPane = new AnchorPane();
    private VBox vBoxDialog = new VBox();
    private VBox vBoxCont = new VBox();

    private TilePane tilePane = new TilePane();
    private StackPane stackpane;
    private Button btnOk, btnCancel;
    private Button btnPrev, btnNext;
    private Button btnStart1 = new Button(STR_START_1), btnStart2 = new Button(STR_START_2),
            btnUpdate = new Button(STR_UPDATE), btnGeo = new Button(STR_GEO),
            btnDown = new Button(STR_DOWN),
            btnPath = new Button(STR_PATH);

    private static final String STR_START_1 = "Infos 1";
    private static final String STR_START_2 = "Infos 2";
    private static final String STR_UPDATE = "Update";
    private static final String STR_GEO = "Geo";
    private static final String STR_DOWN = "Zielverzeichnis";
    private static final String STR_PATH = "Programmpfade";

    private enum State {START_1, START_2, UPDATE, GEO, DOWN, PATH}

    private ScrollPane startPane_1;
    private ScrollPane startPane_2;
    private ScrollPane updatePane;
    private ScrollPane geoPane;
    private ScrollPane downPane;
    private ScrollPane pathPane;

    private final ProgData progData;
    private State aktState = State.START_1;

    public StartDialogController() {
        super(null, null, "Starteinstellungen", true);

        this.progData = ProgData.getInstance();
        init(rootPane, true);
    }

    @Override
    public void make() {
        stage = getStage();
        initPanel();
        addButton();
        initStack();
        initButton();
        selectActPane();
        initTooltip();
    }

    public void close() {
        super.close();
    }

    public boolean isOk() {
        return ok;
    }

    private void initPanel() {
        try {
            vBoxDialog.setPadding(new Insets(20));
            vBoxDialog.setSpacing(20);

            vBoxCont.getStyleClass().add("dialog-border");
            vBoxCont.setSpacing(10);
            VBox.setVgrow(vBoxCont, Priority.ALWAYS);

            rootPane.getChildren().addAll(vBoxDialog);
            AnchorPane.setLeftAnchor(vBoxDialog, 0.0);
            AnchorPane.setBottomAnchor(vBoxDialog, 0.0);
            AnchorPane.setRightAnchor(vBoxDialog, 0.0);
            AnchorPane.setTopAnchor(vBoxDialog, 0.0);

            vBoxDialog.getChildren().add(vBoxCont);

        } catch (final Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void addButton() {
        vBoxCont.getChildren().add(tilePane);
        tilePane.getChildren().addAll(btnStart1, btnStart2, btnUpdate, btnGeo, btnDown, btnPath);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setPadding(new Insets(10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        setButton(btnStart1, State.START_1);
        setButton(btnStart2, State.START_2);
        setButton(btnUpdate, State.UPDATE);
        setButton(btnGeo, State.GEO);
        setButton(btnDown, State.DOWN);
        setButton(btnPath, State.PATH);
        btnStart1.getStyleClass().add("btnStartDialogSel");
    }

    private void setButton(Button btn, State state) {
        btn.getStyleClass().add("btnStartDialog");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(a -> {
            aktState = state;
            btnStart1.getStyleClass().retainAll("btnStartDialog");
            btnStart2.getStyleClass().retainAll("btnStartDialog");
            btnUpdate.getStyleClass().retainAll("btnStartDialog");
            btnGeo.getStyleClass().retainAll("btnStartDialog");
            btnDown.getStyleClass().retainAll("btnStartDialog");
            btnPath.getStyleClass().retainAll("btnStartDialog");
            btn.getStyleClass().add("btnStartDialogSel");
            setButtonStyle(btnStart1);
            selectActPane();
        });
    }

    private void setButtonStyle(Button btnSel) {
        btnStart1.getStyleClass().retainAll("btnStartDialog");
        btnStart2.getStyleClass().retainAll("btnStartDialog");
        btnUpdate.getStyleClass().retainAll("btnStartDialog");
        btnGeo.getStyleClass().retainAll("btnStartDialog");
        btnDown.getStyleClass().retainAll("btnStartDialog");
        btnPath.getStyleClass().retainAll("btnStartDialog");
        btnSel.getStyleClass().add("btnStartDialogSel");
    }

    private void initStack() {
        stackpane = new StackPane();
        VBox.setVgrow(stackpane, Priority.ALWAYS);
        vBoxCont.getChildren().add(stackpane);

        //startPane 1
        startPane_1 = new ScrollPane();
        startPane_1.setFitToHeight(true);
        startPane_1.setFitToWidth(true);

        TitledPane tStart1 = new StartPane(stage).makeStart1();
        tStart1.setMaxHeight(Double.MAX_VALUE);
        tStart1.setCollapsible(false);
        tStart1.setText(STR_START_1);
        startPane_1.setContent(tStart1);

        //startPane 2
        startPane_2 = new ScrollPane();
        startPane_2.setFitToHeight(true);
        startPane_2.setFitToWidth(true);

        TitledPane tStart2 = new StartPane(stage).makeStart2();
        tStart2.setMaxHeight(Double.MAX_VALUE);
        tStart2.setCollapsible(false);
        tStart2.setText(STR_START_2);
        startPane_2.setContent(tStart2);

        //updatePane
        updatePane = new ScrollPane();
        updatePane.setFitToHeight(true);
        updatePane.setFitToWidth(true);

        TitledPane tUpdate = new UpdatePane(stage).makeStart();
        tUpdate.setMaxHeight(Double.MAX_VALUE);
        tUpdate.setCollapsible(false);
        tUpdate.setText(STR_UPDATE);
        updatePane.setContent(tUpdate);

        //geoPane
        geoPane = new ScrollPane();
        geoPane.setFitToHeight(true);
        geoPane.setFitToWidth(true);

        TitledPane tGeo = new GeoPane(stage).makeGeo();
        tGeo.setMaxHeight(Double.MAX_VALUE);
        tGeo.setCollapsible(false);
        tGeo.setText(STR_GEO);
        geoPane.setContent(tGeo);

        //downPane
        downPane = new ScrollPane();
        downPane.setFitToHeight(true);
        downPane.setFitToWidth(true);

        TitledPane tDown = new DownPathPane(stage).makePath();
        tDown.setMaxHeight(Double.MAX_VALUE);
        tDown.setCollapsible(false);
        tDown.setText(STR_DOWN);
        downPane.setContent(tDown);

        //pathPane
        pathPane = new ScrollPane();
        pathPane.setFitToHeight(true);
        pathPane.setFitToWidth(true);

        TitledPane tPath = new PathPane(stage).makePath();
        tPath.setMaxHeight(Double.MAX_VALUE);
        tPath.setCollapsible(false);
        tPath.setText(STR_PATH);
        pathPane.setContent(tPath);
        stackpane.getChildren().addAll(startPane_1, startPane_2, updatePane, geoPane, downPane, pathPane);
    }

    private void initButton() {
        btnOk = new Button("Ok");
        btnOk.setDisable(true);
        btnOk.setOnAction(a -> {
            ok = true;
            close();
        });

        btnCancel = new Button("Abbrechen");
        btnCancel.setOnAction(a -> close());

        btnNext = new Button("");
        btnNext.setGraphic(new Icons().ICON_BUTTON_NEXT);
        btnNext.setOnAction(event -> {
            switch (aktState) {
                case START_1:
                    aktState = State.START_2;
                    break;
                case START_2:
                    aktState = State.UPDATE;
                    break;
                case UPDATE:
                    aktState = State.GEO;
                    break;
                case GEO:
                    aktState = State.DOWN;
                    break;
                case DOWN:
                    aktState = State.PATH;
                    break;
                case PATH:
                    break;
            }
            selectActPane();
        });
        btnPrev = new Button("");
        btnPrev.setGraphic(new Icons().ICON_BUTTON_PREV);
        btnPrev.setOnAction(event -> {
            switch (aktState) {
                case START_1:
                    break;
                case START_2:
                    aktState = State.START_1;
                    break;
                case UPDATE:
                    aktState = State.START_2;
                    break;
                case GEO:
                    aktState = State.UPDATE;
                    break;
                case DOWN:
                    aktState = State.GEO;
                    break;
                case PATH:
                    aktState = State.DOWN;
                    break;
            }
            selectActPane();
        });

        btnOk.getStyleClass().add("btnStartDialog");
        btnCancel.getStyleClass().add("btnStartDialog");
        btnNext.getStyleClass().add("btnStartDialog");
        btnPrev.getStyleClass().add("btnStartDialog");

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.getChildren().addAll(btnPrev, btnNext);
        HBox.setHgrow(hBox1, Priority.ALWAYS);

        HBox hBox2 = new HBox();
        hBox2.setSpacing(10);
        hBox2.getChildren().addAll(hBox1, btnOk, btnCancel);

        vBoxDialog.getChildren().add(hBox2);
    }

    private void selectActPane() {
        switch (aktState) {
            case START_1:
                btnPrev.setDisable(true);
                btnNext.setDisable(false);
                startPane_1.toFront();
                setButtonStyle(btnStart1);
                break;
            case START_2:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                startPane_2.toFront();
                setButtonStyle(btnStart2);
                break;
            case UPDATE:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                updatePane.toFront();
                setButtonStyle(btnUpdate);
                break;
            case GEO:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                geoPane.toFront();
                setButtonStyle(btnGeo);
                break;
            case DOWN:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                downPane.toFront();
                setButtonStyle(btnDown);
                break;
            case PATH:
                btnPrev.setDisable(false);
                btnNext.setDisable(true);
                btnOk.setDisable(false);
                pathPane.toFront();
                setButtonStyle(btnPath);
                break;
            default:
                btnOk.setDisable(false);
        }
    }

    private void initTooltip() {
        btnStart1.setTooltip(new Tooltip("Infos über das Programm."));
        btnStart2.setTooltip(new Tooltip("Infos über das Programm."));
        btnUpdate.setTooltip(new Tooltip("Soll das Programm nach Updates suchen!"));
        btnGeo.setTooltip(new Tooltip("Einstellung des eigenen Standorts\n" +
                "und der Markierung geblockter Filme."));
        btnDown.setTooltip(new Tooltip("Auswahl des Verzeichniss zum Speichern der Filme."));
        btnPath.setTooltip(new Tooltip("Angabe von Programmen zum Ansehen\n" +
                "und Speichern der Filme."));

        btnOk.setTooltip(new Tooltip("Programm mit den gewählten Einstellungen starten."));
        btnCancel.setTooltip(new Tooltip("Das Programm nicht einrichten\n" +
                "und starten sondern Dialog wieder beenden."));
        btnNext.setTooltip(new Tooltip("Nächste Einstellmöglichkeit."));
        btnPrev.setTooltip(new Tooltip("Vorherige Einstellmöglichkeit."));
    }
}
