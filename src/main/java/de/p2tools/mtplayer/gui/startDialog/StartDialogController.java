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

package de.p2tools.mtplayer.gui.startDialog;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.gui.configDialog.GeoPane;
import de.p2tools.mtplayer.gui.configDialog.LoadFilmsPane;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
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
    private GeoPane geoPane;
    private LoadFilmsPane loadFilmsPane;
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
        geoPane.close();
        loadFilmsPane.close();
        downPathPane.close();
        pathPane.close();
        super.close();
    }

    public boolean isOk() {
        return ok;
    }

    private void initTopButton() {
        getvBoxCont().getChildren().add(tilePane);
        tilePane.getChildren().addAll(btnStart1, btnStart2, btnUpdate, btnGeo, btnFilm, btnDown, btnPath);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setPadding(new Insets(10, 10, 20, 10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);

        setButton(btnStart1, State.START_1);
        setButton(btnStart2, State.START_2);
        setButton(btnUpdate, State.UPDATE);
        setButton(btnGeo, State.GEO);
        setButton(btnFilm, State.FILM);
        setButton(btnDown, State.DOWN);
        setButton(btnPath, State.PATH);
    }

    private void setButton(Button btn, State state) {
        btn.getStyleClass().add("btnStartDialog");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(a -> {
            aktState = state;
            selectActPane();
        });
    }

    private void initStack() {
        stackpane = new StackPane();
        VBox.setVgrow(stackpane, Priority.ALWAYS);
        getvBoxCont().getChildren().add(stackpane);

        //startPane 1
        startPane1 = new StartPane(getStage());
        tStart1 = startPane1.makeStart1();
        tStart1.setMaxHeight(Double.MAX_VALUE);
        tStart1.setCollapsible(false);

        //startPane 2
        startPane2 = new StartPane(getStage());
        tStart2 = startPane2.makeStart2();
        tStart2.setMaxHeight(Double.MAX_VALUE);
        tStart2.setCollapsible(false);

        //updatePane
        updatePane = new UpdatePane(getStage());
        tUpdate = updatePane.makeStart();
        tUpdate.setMaxHeight(Double.MAX_VALUE);
        tUpdate.setCollapsible(false);

        //geoPane
        geoPane = new GeoPane(getStage());
        tGeo = geoPane.makeGeo();
        tGeo.setMaxHeight(Double.MAX_VALUE);
        tGeo.setCollapsible(false);

        //filmPane
        loadFilmsPane = new LoadFilmsPane(getStage());
        tFilm = loadFilmsPane.make();
        tFilm.setMaxHeight(Double.MAX_VALUE);
        tFilm.setCollapsible(false);

        //downPane
        downPathPane = new DownPathPane(getStage());
        tDown = downPathPane.makePath();
        tDown.setMaxHeight(Double.MAX_VALUE);
        tDown.setCollapsible(false);

        //pathPane
        pathPane = new PathPane(getStage());
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

        btnNext = PButton.getButton(new ProgIcons().ICON_BUTTON_NEXT, "nächste Seite");
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
        btnPrev = PButton.getButton(new ProgIcons().ICON_BUTTON_PREV, "vorherige Seite");
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

        btnOk.getStyleClass().add("btnStartDialog");
        btnCancel.getStyleClass().add("btnStartDialog");
        btnNext.getStyleClass().add("btnStartDialog");
        btnPrev.getStyleClass().add("btnStartDialog");

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
        btnStart1.getStyleClass().retainAll("btnStartDialog");
        btnStart2.getStyleClass().retainAll("btnStartDialog");
        btnUpdate.getStyleClass().retainAll("btnStartDialog");
        btnGeo.getStyleClass().retainAll("btnStartDialog");
        btnFilm.getStyleClass().retainAll("btnStartDialog");
        btnDown.getStyleClass().retainAll("btnStartDialog");
        btnPath.getStyleClass().retainAll("btnStartDialog");
        btnSel.getStyleClass().add("btnStartDialogSel");
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
