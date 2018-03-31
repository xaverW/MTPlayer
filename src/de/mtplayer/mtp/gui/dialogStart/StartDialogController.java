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

package de.mtplayer.mtp.gui.dialogStart;

import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.configDialog.GeoPane;
import de.mtplayer.mtp.gui.dialog.MTDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.*;
import org.controlsfx.control.BreadCrumbBar;


public class StartDialogController extends MTDialog {

    private AnchorPane rootPane = new AnchorPane();
    private VBox vBoxDialog = new VBox();
    private VBox vBoxCont = new VBox();

    private BreadCrumbBar<String> bread;
    private StackPane stackpane;
    private Button btnOk;
    private Button btnPrev, btnNext;


    private static final String STR_START_1 = "Start 1";
    private static final String STR_START_2 = "Start 2";
    private static final String STR_UPDATE = "Update";
    private static final String STR_GEO = "Geo";
    private static final String STR_DOWN = "Zielverzeichnis";
    private static final String STR_PATH = "Programmpfade";
    private static final String STR_QUITT = "Quitt";

    private enum State {START_1, START_2, UPDATE, GEO, DOWN, PATH, QUITT}

    private TreeItem<String> tiStart_1 = new TreeItem<>(STR_START_1);
    private TreeItem<String> tiStart_2 = new TreeItem<>(STR_START_2);
    private TreeItem<String> tiUpdate = new TreeItem<>(STR_UPDATE);
    private TreeItem<String> tiGeo = new TreeItem<>(STR_GEO);
    private TreeItem<String> tiDown = new TreeItem<>(STR_DOWN);
    private TreeItem<String> tiPath = new TreeItem<>(STR_PATH);

    private ScrollPane startPane_1;
    private ScrollPane startPane_2;
    private ScrollPane updatePane;
    private ScrollPane geoPane;
    private ScrollPane downPane;
    private ScrollPane pathPane;

    private final Daten daten;
    private State aktState = State.START_1;

    public StartDialogController() {
        super("Starteinstellungen", true);

        this.daten = Daten.getInstance();
        init(rootPane, true);
    }

    @Override
    public void make() {
        initPanel();
        setState();
    }

    public void close() {
        super.close();
    }

    private void setState() {
        switch (aktState) {
            case START_1:
                btnPrev.setDisable(true);
                btnNext.setDisable(false);
                bread.setSelectedCrumb(tiStart_1);
                startPane_1.toFront();
                break;
            case START_2:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                bread.setSelectedCrumb(tiStart_2);
                startPane_2.toFront();
                break;
            case UPDATE:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                bread.setSelectedCrumb(tiUpdate);
                updatePane.toFront();
                break;
            case GEO:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                bread.setSelectedCrumb(tiGeo);
                geoPane.toFront();
                break;
            case DOWN:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                bread.setSelectedCrumb(tiDown);
                downPane.toFront();
                break;
            case PATH:
                btnPrev.setDisable(false);
                btnNext.setDisable(true);
                btnOk.setDisable(false);
                bread.setSelectedCrumb(tiPath);
                pathPane.toFront();
                break;
            case QUITT:
            default:
                btnOk.setDisable(false);

        }
    }

    private void initBread() {
        HBox hBox = new HBox();
        bread = new BreadCrumbBar<>();
        hBox.getChildren().addAll(bread);
        vBoxDialog.getChildren().add(hBox);

        tiStart_1.getChildren().add(tiStart_2);
        tiStart_2.getChildren().add(tiUpdate);
        tiUpdate.getChildren().add(tiGeo);
        tiGeo.getChildren().add(tiDown);
        tiDown.getChildren().add(tiPath);

        bread.setSelectedCrumb(tiStart_1);
        bread.setOnCrumbAction(bae -> {
            switch (bae.getSelectedCrumb().getValue()) {
                case STR_START_1:
                    aktState = State.START_1;
                    break;
                case STR_START_2:
                    aktState = State.START_2;
                    break;
                case STR_UPDATE:
                    aktState = State.UPDATE;
                    break;
                case STR_GEO:
                    aktState = State.GEO;
                    break;
                case STR_DOWN:
                    aktState = State.DOWN;
                    break;
                case STR_PATH:
                    aktState = State.PATH;
                    break;
                case STR_QUITT:
                default:
                    aktState = State.QUITT;
                    break;
            }
            setState();
        });
    }

    private void initStack() {
        stackpane = new StackPane();
        VBox.setVgrow(stackpane, Priority.ALWAYS);
        vBoxCont.getChildren().add(stackpane);

        //startPane 1
        startPane_1 = new ScrollPane();
        startPane_1.setFitToHeight(true);
        startPane_1.setFitToWidth(true);

        TitledPane tStart1 = new StartPane().makeStart1();
        tStart1.setMaxHeight(Double.MAX_VALUE);
        tStart1.setCollapsible(false);
        tStart1.setText(STR_START_1);
        startPane_1.setContent(tStart1);

        //startPane 2
        startPane_2 = new ScrollPane();
        startPane_2.setFitToHeight(true);
        startPane_2.setFitToWidth(true);

        TitledPane tStart2 = new StartPane().makeStart2();
        tStart2.setMaxHeight(Double.MAX_VALUE);
        tStart2.setCollapsible(false);
        tStart2.setText(STR_START_2);
        startPane_2.setContent(tStart2);

        //updatePane
        updatePane = new ScrollPane();
        updatePane.setFitToHeight(true);
        updatePane.setFitToWidth(true);

        TitledPane tUpdate = new UpdatePane().makeStart();
        tUpdate.setMaxHeight(Double.MAX_VALUE);
        tUpdate.setCollapsible(false);
        tUpdate.setText(STR_UPDATE);
        updatePane.setContent(tUpdate);

        //geoPane
        geoPane = new ScrollPane();
        geoPane.setFitToHeight(true);
        geoPane.setFitToWidth(true);

        TitledPane tGeo = new GeoPane().makeGeo();
        tGeo.setMaxHeight(Double.MAX_VALUE);
        tGeo.setCollapsible(false);
        tGeo.setText(STR_GEO);
        geoPane.setContent(tGeo);

        //downPane
        downPane = new ScrollPane();
        downPane.setFitToHeight(true);
        downPane.setFitToWidth(true);

        TitledPane tDown = new DownPathPane().makePath();
        tDown.setMaxHeight(Double.MAX_VALUE);
        tDown.setCollapsible(false);
        tDown.setText(STR_DOWN);
        downPane.setContent(tDown);

        //pathPane
        pathPane = new ScrollPane();
        pathPane.setFitToHeight(true);
        pathPane.setFitToWidth(true);

        TitledPane tPath = new PathPane().makePath();
        tPath.setMaxHeight(Double.MAX_VALUE);
        tPath.setCollapsible(false);
        tPath.setText(STR_PATH);
        pathPane.setContent(tPath);
        stackpane.getChildren().addAll(startPane_1, startPane_2, updatePane, geoPane, downPane, pathPane);
    }

    private void initButton() {
        btnOk = new Button("");
        btnOk.setText("Ok");
        btnOk.setDisable(true);
        btnOk.setOnAction(a -> close());

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
            setState();
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
            setState();
        });

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.getChildren().addAll(btnPrev, btnNext);
        HBox.setHgrow(hBox1, Priority.ALWAYS);

        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(hBox1, btnOk);

        vBoxDialog.getChildren().add(hBox2);
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

            //Bread
            initBread();

            vBoxDialog.getChildren().add(vBoxCont);

            //Stackpane
            initStack();

            //Button OK
            initButton();
        } catch (final Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
