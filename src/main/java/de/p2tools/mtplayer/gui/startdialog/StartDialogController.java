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

package de.p2tools.mtplayer.gui.startdialog;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.gui.configpanes.PaneGeo;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;


public class StartDialogController extends P2DialogExtra {

    private boolean ok = false;

    private final TilePane tilePane = new TilePane();
    private Button btnOk, btnCancel;
    private Button btnPrev, btnNext;
    private final Button btnStart1 = new Button(STR_START_1);
    private final Button btnStart2 = new Button(STR_START_2);
    private final Button btnColorMode = new Button(STR_COLOR_MODE);
    private final Button btnUpdate = new Button(STR_UPDATE);
    private final Button btnGeo = new Button(STR_GEO);
    private final Button btnFilm = new Button(STR_FILM);
    private final Button btnStation = new Button(STR_STATION);
    private final Button btnDown = new Button(STR_DOWN);
    private final Button btnPath = new Button(STR_PATH);

    private static final String STR_START_1 = "Infos";
    private static final String STR_START_2 = "Infos";
    private static final String STR_COLOR_MODE = "Farbe";
    private static final String STR_UPDATE = "Update";
    private static final String STR_GEO = "Geo";
    private static final String STR_FILM = "Filme";
    private static final String STR_STATION = "Sender";
    private static final String STR_DOWN = "Ziel";
    private static final String STR_PATH = "Pfade";

    private enum State {START_1, START_2, COLOR_MODE, UPDATE, GEO, FILM, STATION, DOWN, PATH}

    private State aktState = State.START_1;

    private TitledPane tStart1;
    private TitledPane tStart2;
    private TitledPane tColorMode;
    private TitledPane tUpdate;
    private TitledPane tGeo;
    private TitledPane tFilm;
    private TitledPane tStation;
    private TitledPane tDown;
    private TitledPane tPath;

    private StartPane startPane1;
    private StartPane startPane2;
    private PaneColorMode paneColorMode;
    private UpdatePane updatePane;
    private PaneGeo paneGeo;
    private PaneFilm paneFilm;
    private PaneStation paneStation;
    private DownPathPane downPathPane;
    private PathPane pathPane;

    public StartDialogController() {
        super(null, null, "Starteinstellungen", true, false);

        ProgData progData = ProgData.getInstance();
        init(true);
    }

    @Override
    public void make() {
        initTopButton();
        initStack();
        initButton();
        initTooltip();
        selectActPane();
    }

    private void closeDialog(boolean ok) {
        this.ok = ok;
        startPane1.close();
        startPane2.close();
        paneColorMode.close();
        updatePane.close();
        paneGeo.close();
        paneFilm.close();
        paneStation.close();
        downPathPane.close();
        pathPane.close();
        super.close();
    }

    public boolean isOk() {
        return ok;
    }

    private void initTopButton() {
        getVBoxCont().getChildren().add(tilePane);
        tilePane.getChildren().addAll(btnStart1, btnStart2, btnColorMode, btnUpdate, btnGeo, btnFilm, btnStation, btnDown, btnPath);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setPadding(new Insets(10, 10, 20, 10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);

        initTopButton(btnStart1, State.START_1);
        initTopButton(btnStart2, State.START_2);
        initTopButton(btnColorMode, State.COLOR_MODE);
        initTopButton(btnUpdate, State.UPDATE);
        initTopButton(btnGeo, State.GEO);
        initTopButton(btnFilm, State.FILM);
        initTopButton(btnStation, State.STATION);
        initTopButton(btnDown, State.DOWN);
        initTopButton(btnPath, State.PATH);
    }

    private void initTopButton(Button btn, State state) {
        btn.getStyleClass().addAll("btnFunction", "btnFuncStartDialog");
        btn.setAlignment(Pos.CENTER);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(a -> {
            aktState = state;
            selectActPane();
        });
    }

    private void initStack() {
        StackPane stackpane = new StackPane();
        VBox.setVgrow(stackpane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(stackpane);

        //startPane 1
        startPane1 = new StartPane();
        tStart1 = startPane1.makeStart1();
        tStart1.setMaxHeight(Double.MAX_VALUE);
        tStart1.setCollapsible(false);

        //startPane 2
        startPane2 = new StartPane();
        tStart2 = startPane2.makeStart2();
        tStart2.setMaxHeight(Double.MAX_VALUE);
        tStart2.setCollapsible(false);

        //colorModePane
        paneColorMode = new PaneColorMode(this.getStage());
        tColorMode = paneColorMode.make();
        tColorMode.setMaxHeight(Double.MAX_VALUE);
        tColorMode.setCollapsible(false);

        //updatePane
        updatePane = new UpdatePane(this);
        tUpdate = updatePane.makeStart();
        tUpdate.setMaxHeight(Double.MAX_VALUE);
        tUpdate.setCollapsible(false);

        //geoPane
        paneGeo = new PaneGeo(this.getStage());
        tGeo = paneGeo.make();
        tGeo.setMaxHeight(Double.MAX_VALUE);
        tGeo.setCollapsible(false);

        //filmPane
        paneFilm = new PaneFilm(this.getStage());
        tFilm = paneFilm.make(null);
        tFilm.setMaxHeight(Double.MAX_VALUE);
        tFilm.setCollapsible(false);

        //stationPane
        paneStation = new PaneStation(this.getStage());
        tStation = paneStation.make(null);
        tStation.setMaxHeight(Double.MAX_VALUE);
        tStation.setCollapsible(false);

        //downPane
        downPathPane = new DownPathPane(this.getStage());
        tDown = downPathPane.makePath();
        tDown.setMaxHeight(Double.MAX_VALUE);
        tDown.setCollapsible(false);

        //pathPane
        pathPane = new PathPane(this.getStage());
        tPath = pathPane.makePath();
        tPath.setMaxHeight(Double.MAX_VALUE);
        tPath.setCollapsible(false);

        stackpane.getChildren().addAll(tStart1, tStart2, tColorMode, tUpdate, tGeo, tFilm, tStation, tDown, tPath);
    }

    private void initButton() {
        btnOk = new Button("_Ok");
        btnOk.setDisable(true);
        btnOk.setOnAction(a -> {
            closeDialog(true);
        });

        btnCancel = new Button("_Abbrechen");
        btnCancel.setOnAction(a -> closeDialog(false));

        btnNext = P2Button.getButton(ProgIcons.ICON_BUTTON_NEXT.getImageView(), "nächste Seite");
        btnNext.setOnAction(event -> {
            switch (aktState) {
                case START_1:
                    aktState = State.START_2;
                    break;
                case START_2:
                    aktState = State.COLOR_MODE;
                    break;
                case COLOR_MODE:
                    aktState = State.UPDATE;
                    break;
                case UPDATE:
                    aktState = State.GEO;
                    break;
                case GEO:
                    aktState = State.FILM;
                    break;
                case FILM:
                    aktState = State.STATION;
                    break;
                case STATION:
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
        btnPrev = P2Button.getButton(ProgIcons.ICON_BUTTON_PREV.getImageView(), "vorherige Seite");
        btnPrev.setOnAction(event -> {
            switch (aktState) {
                case START_1:
                    break;
                case START_2:
                    aktState = State.START_1;
                    break;
                case COLOR_MODE:
                    aktState = State.START_2;
                    break;
                case UPDATE:
                    aktState = State.COLOR_MODE;
                    break;
                case GEO:
                    aktState = State.UPDATE;
                    break;
                case FILM:
                    aktState = State.GEO;
                    break;
                case STATION:
                    aktState = State.FILM;
                    break;
                case DOWN:
                    aktState = State.STATION;
                    break;
                case PATH:
                    aktState = State.DOWN;
                    break;
            }
            selectActPane();
        });

        addOkCancelButtons(btnOk, btnCancel);
        ButtonBar.setButtonData(btnPrev, ButtonBar.ButtonData.BACK_PREVIOUS);
        ButtonBar.setButtonData(btnNext, ButtonBar.ButtonData.NEXT_FORWARD);
        addAnyButton(btnNext);
        addAnyButton(btnPrev);
        getButtonBar().setButtonOrder("BX+CO");
    }

    private void selectActPane() {
        switch (aktState) {
            case START_1:
                btnPrev.setDisable(true);
                btnNext.setDisable(false);
                tStart1.toFront();
                setButtonStyle(btnStart1);
                break;
            case START_2:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                tStart2.toFront();
                setButtonStyle(btnStart2);
                break;
            case COLOR_MODE:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                tColorMode.toFront();
                setButtonStyle(btnColorMode);
                break;
            case UPDATE:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                tUpdate.toFront();
                setButtonStyle(btnUpdate);
                break;
            case GEO:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                tGeo.toFront();
                setButtonStyle(btnGeo);
                break;
            case FILM:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                tFilm.toFront();
                setButtonStyle(btnFilm);
                break;
            case STATION:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                tStation.toFront();
                setButtonStyle(btnStation);
                break;
            case DOWN:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                tDown.toFront();
                setButtonStyle(btnDown);
                break;
            case PATH:
                btnPrev.setDisable(false);
                btnNext.setDisable(true);
                btnOk.setDisable(false);
                tPath.toFront();
                setButtonStyle(btnPath);
                break;
            default:
                btnOk.setDisable(false);
        }
    }

    private void setButtonStyle(Button btnSel) {
        btnStart1.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnStart2.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnColorMode.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnUpdate.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnGeo.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnFilm.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnStation.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnDown.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnPath.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnSel.getStyleClass().setAll("btnFunction", "btnFuncStartDialogSel");
    }

    private void initTooltip() {
        btnStart1.setTooltip(new Tooltip("Infos über das Programm"));
        btnStart2.setTooltip(new Tooltip("Infos über das Programm"));
        btnColorMode.setTooltip(new Tooltip("Wie soll die Programmoberfläche aussehen?"));
        btnUpdate.setTooltip(new Tooltip("Soll das Programm nach Updates suchen?"));
        btnGeo.setTooltip(new Tooltip("Einstellung des eigenen Standorts\n" +
                "und der Markierung geblockter Filme"));
        btnFilm.setTooltip(new Tooltip("Damit kann man die Größe der\n" +
                "Filmliste reduzieren und damit die Geschwindigkeit\n" +
                "des Programms auf langsamen Rechnern verbessern"));
        btnStation.setTooltip(new Tooltip("Damit kann man die Größe der\n" +
                "Filmliste reduzieren und damit die Geschwindigkeit\n" +
                "des Programms auf langsamen Rechnern verbessern"));
        btnDown.setTooltip(new Tooltip("Auswahl des Verzeichniss zum Speichern der Filme"));
        btnPath.setTooltip(new Tooltip("Angabe von Programmen zum Ansehen\n" +
                "und Speichern der Filme"));

        btnOk.setTooltip(new Tooltip("Programm mit den gewählten Einstellungen starten"));
        btnCancel.setTooltip(new Tooltip("Das Programm nicht einrichten\n" +
                "und starten sondern Dialog wieder beenden"));
        btnNext.setTooltip(new Tooltip("Nächste Einstellmöglichkeit"));
        btnPrev.setTooltip(new Tooltip("Vorherige Einstellmöglichkeit"));
    }
}
