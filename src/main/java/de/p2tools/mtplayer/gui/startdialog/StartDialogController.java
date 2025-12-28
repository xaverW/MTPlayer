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
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;


public class StartDialogController extends P2DialogExtra {

    private boolean ok = false;

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

    private enum State {
        START_1, START_2,
        COLOR_MODE, UPDATE, GEO, FILM, STATION, DOWN, PATH
    }

    private State aktState = State.START_1;

    private StartPane startPane1;
    private StartPane startPane2;
    private StartPaneColorMode startPaneColorMode;
    private StartPaneUpdate startPaneUpdate;
    private StartPaneGeo paneGeo;
    private StartPaneFilm startPaneFilm;
    private StartPaneStation startPaneStation;
    private StartPaneDownloadPath startPaneDownloadPath;
    private StartPanePath startPanePath;
    private VBox vBoxCont = new VBox();

    public StartDialogController() {
        super(null, null, "Starteinstellungen",
                true, false, false, DECO.BORDER_VERY_SMALL);

        ProgData progData = ProgData.getInstance();
        init(true);
    }

    @Override
    public void make() {
        init();
        initStack();
        initButton();
        initTooltip();
        selectActPane();
    }

    private void closeDialog(boolean ok) {
        this.ok = ok;
        startPane1.close();
        startPane2.close();
        startPaneColorMode.close();
        startPaneUpdate.close();
        paneGeo.close();
        startPaneFilm.close();
        startPaneStation.close();
        startPaneDownloadPath.close();
        startPanePath.close();
        super.close();
    }

    public boolean isOk() {
        return ok;
    }

    private void init() {
        final TilePane tilePane1 = new TilePane();
        tilePane1.setAlignment(Pos.CENTER);
        tilePane1.setHgap(10);
        tilePane1.setVgap(10);

        final TilePane tilePane2 = new TilePane();
        tilePane2.setAlignment(Pos.CENTER);
        tilePane2.setHgap(10);
        tilePane2.setVgap(10);

        tilePane1.getChildren().addAll(btnStart1, btnStart2);
        tilePane2.getChildren().addAll(btnColorMode, btnUpdate, btnGeo, btnFilm, btnStation, btnDown, btnPath);

        initTopButton(btnStart1, State.START_1);
        initTopButton(btnStart2, State.START_2);
        initTopButton(btnColorMode, State.COLOR_MODE);
        initTopButton(btnUpdate, State.UPDATE);
        initTopButton(btnGeo, State.GEO);
        initTopButton(btnFilm, State.FILM);
        initTopButton(btnStation, State.STATION);
        initTopButton(btnDown, State.DOWN);
        initTopButton(btnPath, State.PATH);

        VBox.setVgrow(vBoxCont, Priority.ALWAYS);
        getVBoxCont().setPadding(new Insets(5));
        getVBoxCont().getChildren().addAll(tilePane1, tilePane2, P2GuiTools.getHDistance(5), vBoxCont);
    }

    private void initTopButton(Button btn, State state) {
        btn.getStyleClass().addAll("btnStartDialog");
        btn.setAlignment(Pos.CENTER);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(a -> {
            aktState = state;
            selectActPane();
        });
    }

    private void initStack() {
        startPane1 = new StartPane();
        startPane1.makeStart1();

        startPane2 = new StartPane();
        startPane2.makeStart2();

        startPaneColorMode = new StartPaneColorMode(this.getStage());
        startPaneColorMode.make();

        startPaneUpdate = new StartPaneUpdate(this);
        startPaneUpdate.makeStart();

        paneGeo = new StartPaneGeo(this.getStage());
        paneGeo.make();

        startPaneFilm = new StartPaneFilm(this.getStage());
        startPaneFilm.make();

        startPaneStation = new StartPaneStation(this.getStage());
        startPaneStation.make();

        startPaneDownloadPath = new StartPaneDownloadPath(this.getStage());
        startPaneDownloadPath.makePath();

        startPanePath = new StartPanePath(this.getStage());
        startPanePath.makePath();
    }

    private void initButton() {
        btnOk = new Button("_Ok");
        btnOk.setDisable(true);
        btnOk.setOnAction(a -> {
            closeDialog(true);
        });

        btnCancel = new Button("_Abbrechen");
        btnCancel.setOnAction(a -> closeDialog(false));

        btnNext = P2Button.getButton(PIconFactory.PICON.BTN_NEXT.getFontIcon(), "nächste Seite");
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
        btnPrev = P2Button.getButton(PIconFactory.PICON.BTN_PREV.getFontIcon(), "vorherige Seite");
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
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPane1);
                setButtonStyle(btnStart1);
                break;
            case START_2:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPane2);
                setButtonStyle(btnStart2);
                break;
            case COLOR_MODE:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPaneColorMode);
                setButtonStyle(btnColorMode);
                break;
            case UPDATE:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPaneUpdate);
                setButtonStyle(btnUpdate);
                break;
            case GEO:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                paneGeo.toFront();
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(paneGeo);
                setButtonStyle(btnGeo);
                break;
            case FILM:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPaneFilm);
                setButtonStyle(btnFilm);
                break;
            case STATION:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPaneStation);
                setButtonStyle(btnStation);
                break;
            case DOWN:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPaneDownloadPath);
                setButtonStyle(btnDown);
                break;
            case PATH:
                btnPrev.setDisable(false);
                btnNext.setDisable(true);
                btnOk.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPanePath);
                setButtonStyle(btnPath);
                break;
            default:
                btnOk.setDisable(false);
        }
    }

    private void setButtonStyle(Button btnSel) {
        btnStart1.getStyleClass().setAll("btnStartDialog");
        btnStart2.getStyleClass().setAll("btnStartDialog");
        btnColorMode.getStyleClass().setAll("btnStartDialog");
        btnUpdate.getStyleClass().setAll("btnStartDialog");
        btnGeo.getStyleClass().setAll("btnStartDialog");
        btnFilm.getStyleClass().setAll("btnStartDialog");
        btnStation.getStyleClass().setAll("btnStartDialog");
        btnDown.getStyleClass().setAll("btnStartDialog");
        btnPath.getStyleClass().setAll("btnStartDialog");
        btnSel.getStyleClass().setAll("btnStartDialog", "btnStartDialogSel");
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
        btnDown.setTooltip(new Tooltip("Auswahl des Verzeichnis zum Speichern der Filme"));
        btnPath.setTooltip(new Tooltip("Angabe von Programmen zum Ansehen\n" +
                "und Speichern der Filme"));

        btnOk.setTooltip(new Tooltip("Programm mit den gewählten Einstellungen starten"));
        btnCancel.setTooltip(new Tooltip("Das Programm nicht einrichten\n" +
                "und starten sondern Dialog wieder beenden"));
        btnNext.setTooltip(new Tooltip("Nächste Einstellmöglichkeit"));
        btnPrev.setTooltip(new Tooltip("Vorherige Einstellmöglichkeit"));
    }
}
