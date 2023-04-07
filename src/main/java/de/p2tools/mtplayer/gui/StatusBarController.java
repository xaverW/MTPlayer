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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadInfosFactory;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PGuiTools;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerLoadFilmlist;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class StatusBarController extends AnchorPane {

    //Film
    private final Label lblSelFilm = new Label();
    private final Label lblLeftFilm = new Label();
    private final Label lblRightFilm = new Label();
    private final Circle circleFilm = new Circle(6);

    //Download
    private final Label lblSelDownload = new Label();
    private final Label lblLeftDownload = new Label();
    private final Label lblRightDownload = new Label();
    private final Circle circleDownload = new Circle(6);

    //Abo
    private final Label lblSelAbo = new Label();
    private final Label lblLeftAbo = new Label();
    private final Label lblRightAbo = new Label();
    private final Circle circleAbo = new Circle(6);

    private final StackPane stackPane = new StackPane();
    private final Pane nonePane;
    private final Pane filmPane;
    private final Pane downloadPane;
    private final Pane aboPane;

    public enum StatusbarIndex {NONE, FILM, DOWNLOAD, ABO}

    private StatusbarIndex statusbarIndex = StatusbarIndex.NONE;
    private final ProgData progData;
    private boolean stopTimer = false;

    public StatusBarController(ProgData progData) {
        this.progData = progData;

        getChildren().addAll(stackPane);
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
        AnchorPane.setRightAnchor(stackPane, 0.0);
        AnchorPane.setTopAnchor(stackPane, 0.0);

        nonePane = new HBox();
        lblSelFilm.setTooltip(new Tooltip("Anzahl markierter Filme"));
        lblSelDownload.setTooltip(new Tooltip("Anzahl markierter Downloads"));
        lblSelAbo.setTooltip(new Tooltip("Anzahl markierter Abos"));
        filmPane = getHBox(lblSelFilm, lblLeftFilm, circleFilm, lblRightFilm);
        downloadPane = getHBox(lblSelDownload, lblLeftDownload, circleDownload, lblRightDownload);
        aboPane = getHBox(lblSelAbo, lblLeftAbo, circleAbo, lblRightAbo);

        make();
    }

    private HBox getHBox(Label lblSel, Label lblLeft, Circle circle, Label lblRight) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2, 5, 2, 5));
        hBox.setSpacing(P2LibConst.DIST_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        lblSel.setPadding(new Insets(1, 5, 1, 5));
        lblSel.getStyleClass().add("lblSelectedLines");

        hBox.getChildren().addAll(lblSel, lblLeft, circle, PGuiTools.getHBoxGrower(), lblRight);
        hBox.setStyle("-fx-background-color: -fx-background ;");
        return hBox;
    }


    private void make() {
        stackPane.getChildren().addAll(nonePane, filmPane, downloadPane, aboPane);
        stackPane.setPadding(new Insets(2, 5, 2, 5));
        nonePane.toFront();
        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                stopTimer = true;
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                stopTimer = false;
                setStatusbarIndex(statusbarIndex);
            }
        });

        Listener.addListener(new Listener(Listener.EVENT_TIMER, StatusBarController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                try {
                    if (!stopTimer) {
                        setStatusbarIndex(statusbarIndex);
                    }
                } catch (final Exception ex) {
                    PLog.errorLog(936251087, ex);
                }
            }
        });
    }

    public void setStatusbarIndex(StatusbarIndex statusbarIndex) {
        this.statusbarIndex = statusbarIndex;
        switch (statusbarIndex) {
            case FILM:
                filmPane.toFront();
                setInfoFilm();
                setTextForRightDisplay();
                break;
            case DOWNLOAD:
                downloadPane.toFront();
                setInfoDownload();
                setTextForRightDisplay();
                break;
            case ABO:
                aboPane.toFront();
                setInfoAbo();
                setTextForRightDisplay();
                break;
            case NONE:
            default:
                nonePane.toFront();
                break;
        }
    }

    private void setInfoFilm() {
        setCircleStyle();
        lblLeftFilm.setText(DownloadInfosFactory.getStatusInfosFilm() + "  ||");
        final int selCount = progData.filmGuiController.getSelCount();
        lblSelFilm.setText(selCount > 0 ? selCount + "" : " ");
    }

    private void setInfoDownload() {
        setCircleStyle();
        lblLeftDownload.setText(DownloadInfosFactory.getStatusInfosDownload() + "  ||");
        final int selCount = progData.downloadGuiController.getSelCount();
        lblSelDownload.setText(selCount > 0 ? selCount + "" : " ");
    }

    private void setInfoAbo() {
        setCircleStyle();
        lblLeftAbo.setText(DownloadInfosFactory.getStatusInfosAbo() + "  ||");

        final int selCount = progData.aboGuiController.getSelCount();
        lblSelAbo.setText(selCount > 0 ? selCount + "" : " ");
    }

    private boolean setCircleStyle() {
        if (progData.downloadInfos.getFinishedError() > 0) {
            circleFilm.setFill(Paint.valueOf("red"));
            circleDownload.setFill(Paint.valueOf("red"));
            circleAbo.setFill(Paint.valueOf("red"));
            return true;

        } else if (progData.downloadInfos.getLoading() > 0) {
            circleFilm.setFill(Paint.valueOf("green"));
            circleDownload.setFill(Paint.valueOf("green"));
            circleAbo.setFill(Paint.valueOf("green"));
            return true;

        } else {
            circleFilm.setFill(Paint.valueOf("#666666"));
            circleDownload.setFill(Paint.valueOf("#666666"));
            circleAbo.setFill(Paint.valueOf("#666666"));
            return false;
        }
    }

    private void setTextForRightDisplay() {
        // Text rechts: alter anzeigen
        String strText = "Filmliste erstellt: ";
        strText += progData.filmlist.genDate();
        strText += " Uhr  ";

        final int second = progData.filmlist.getAge();
        if (second != 0) {
            strText += "||  Alter: ";
            final int minute = second / 60;
            String strSecond = String.valueOf(second % 60);
            String strMinute = String.valueOf(minute % 60);
            String strHour = String.valueOf(minute / 60);
            if (strSecond.length() < 2) {
                strSecond = '0' + strSecond;
            }
            if (strMinute.length() < 2) {
                strMinute = '0' + strMinute;
            }
            if (strHour.length() < 2) {
                strHour = '0' + strHour;
            }
            strText += strHour + ':' + strMinute + ':' + strSecond + ' ';
        }
        // Infopanel setzen
        lblRightFilm.setText(strText);
        lblRightDownload.setText(strText);
        lblRightAbo.setText(strText);
    }
}
