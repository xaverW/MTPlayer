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
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.gui.configpanes.PaneFilmSender;
import de.p2tools.mtplayer.gui.configpanes.PaneGeo;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.PButton;
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


public class StartDialogController extends PDialogExtra {

    private boolean ok = false;

    private TilePane tilePane = new TilePane();
    private StackPane stackpane;
    private Button btnOk, btnCancel;
    private Button btnPrev, btnNext;
    private Button btnStart1 = new Button(STR_START_1), btnStart2 = new Button(STR_START_2),
            btnUpdate = new Button(STR_UPDATE), btnGeo = new Button(STR_GEO),
            btnFilm = new Button(STR_FILM),
            btnDown = new Button(STR_DOWN),
            btnPath = new Button(STR_PATH);

    private static final String STR_START_1 = "Infos";
    private static final String STR_START_2 = "Infos";
    private static final String STR_UPDATE = "Update";
    private static final String STR_GEO = "Geo";
    private static final String STR_FILM = "Filme";
    private static final String STR_DOWN = "Ziel";
    private static final String STR_PATH = "Pfade";

    private enum State {START_1, START_2, UPDATE, GEO, FILM, DOWN, PATH}

    private State aktState = State.START_1;

    private TitledPane tStart1;
    private TitledPane tStart2;
    private TitledPane tUpdate;
    private TitledPane tGeo;
    private TitledPane tFilm;
    private TitledPane tDown;
    private TitledPane tPath;

    private StartPane startPane1;
    private StartPane startPane2;
    private UpdatePane updatePane;
    private PaneGeo paneGeo;
    private PaneFilmSender paneFilmSender;
    private DownPathPane downPathPane;
    private PathPane pathPane;

    private final ProgData progData;

    public StartDialogController() {
        super(null, null, "Starteinstellungen", true, false);

        this.progData = ProgData.getInstance();
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
        updatePane.close();
        paneGeo.close();
        paneFilmSender.close();
        downPathPane.close();
        pathPane.close();
        super.close();
    }

    public boolean isOk() {
        return ok;
    }

    private void initTopButton() {
        getVBoxCont().getChildren().add(tilePane);
        tilePane.getChildren().addAll(btnStart1, btnStart2, btnUpdate, btnGeo, btnFilm, btnDown, btnPath);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setPadding(new Insets(10, 10, 20, 10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);

        initTopButton(btnStart1, State.START_1);
        initTopButton(btnStart2, State.START_2);
        initTopButton(btnUpdate, State.UPDATE);
        initTopButton(btnGeo, State.GEO);
        initTopButton(btnFilm, State.FILM);
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
        stackpane = new StackPane();
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

        //updatePane
        updatePane = new UpdatePane(this);
        tUpdate = updatePane.makeStart();
        tUpdate.setMaxHeight(Double.MAX_VALUE);
        tUpdate.setCollapsible(false);

        //geoPane
        paneGeo = new PaneGeo(this.getStage());
        tGeo = paneGeo.makeGeo();
        tGeo.setMaxHeight(Double.MAX_VALUE);
        tGeo.setCollapsible(false);

        //filmPane
        paneFilmSender = new PaneFilmSender(this.getStage(), true);
        tFilm = paneFilmSender.make(null);
        tFilm.setMaxHeight(Double.MAX_VALUE);
        tFilm.setCollapsible(false);

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

        stackpane.getChildren().addAll(tStart1, tStart2, tUpdate, tGeo, tFilm, tDown, tPath);
    }

    private void initButton() {
        btnOk = new Button("_Ok");
        btnOk.setDisable(true);
        btnOk.setOnAction(a -> {
            closeDialog(true);
        });

        btnCancel = new Button("_Abbrechen");
        btnCancel.setOnAction(a -> closeDialog(false));

        btnNext = PButton.getButton(ProgIcons.Icons.ICON_BUTTON_NEXT.getImageView(), "nächste Seite");
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
                    aktState = State.FILM;
                    break;
                case FILM:
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
        btnPrev = PButton.getButton(ProgIcons.Icons.ICON_BUTTON_PREV.getImageView(), "vorherige Seite");
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
                case FILM:
                    aktState = State.GEO;
                    break;
                case DOWN:
                    aktState = State.FILM;
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
        btnUpdate.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnGeo.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnFilm.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnDown.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnPath.getStyleClass().setAll("btnFunction", "btnFuncStartDialog");
        btnSel.getStyleClass().setAll("btnFunction", "btnFuncStartDialogSel");
    }

    private void initTooltip() {
        btnStart1.setTooltip(new Tooltip("Infos über das Programm"));
        btnStart2.setTooltip(new Tooltip("Infos über das Programm"));
        btnUpdate.setTooltip(new Tooltip("Soll das Programm nach Updates suchen?"));
        btnGeo.setTooltip(new Tooltip("Einstellung des eigenen Standorts\n" +
                "und der Markierung geblockter Filme"));
        btnFilm.setTooltip(new Tooltip("Damit kann man die Größe der\n" +
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
