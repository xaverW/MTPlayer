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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadInfosFactory;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadListener;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
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
    private final HBox hBoxCircleFilm = new HBox(0);

    //LiveFilm
    private final Label lblSelLiveFilm = new Label();
    private final Label lblLeftLiveFilm = new Label();
    private final Label lblRightLiveFilm = new Label();
    private final Circle circleLiveFilm = new Circle(6);
    private final HBox hBoxCircleLiveFilm = new HBox(0);

    //Download
    private final Label lblSelDownload = new Label();
    private final Label lblLeftDownload = new Label();
    private final Label lblRightDownload = new Label();
    private final Circle circleDownload = new Circle(6);
    private final HBox hBoxCircleDownload = new HBox(0);

    //Abo
    private final Label lblSelAbo = new Label();
    private final Label lblLeftAbo = new Label();
    private final Label lblRightAbo = new Label();
    private final Circle circleAbo = new Circle(6);
    private final HBox hBoxCircleAbo = new HBox(0);

    private final StackPane stackPane = new StackPane();
    private final Pane filmPane;
    private final Pane liveFilmPane;
    private final Pane downloadPane;
    private final Pane aboPane;

    private final ProgData progData;
    private boolean stopTimer = false;
    private boolean halfSecond = false;
    private boolean blink = false;

    public StatusBarController(ProgData progData) {
        this.progData = progData;

        getChildren().addAll(stackPane);
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
        AnchorPane.setRightAnchor(stackPane, 0.0);
        AnchorPane.setTopAnchor(stackPane, 0.0);

        lblSelFilm.setTooltip(new Tooltip("Anzahl markierter Filme"));
        lblSelLiveFilm.setTooltip(new Tooltip("Anzahl markierter Filme"));
        lblSelDownload.setTooltip(new Tooltip("Anzahl markierter Downloads"));
        lblSelAbo.setTooltip(new Tooltip("Anzahl markierter Abos"));

        hBoxCircleFilm.getChildren().add(circleFilm);
        hBoxCircleDownload.getChildren().add(circleDownload);
        hBoxCircleAbo.getChildren().add(circleAbo);

        filmPane = getHBox(lblSelFilm, lblLeftFilm, hBoxCircleFilm, lblRightFilm);
        liveFilmPane = getHBox(lblSelLiveFilm, lblLeftLiveFilm, hBoxCircleLiveFilm, lblRightLiveFilm);
        downloadPane = getHBox(lblSelDownload, lblLeftDownload, hBoxCircleDownload, lblRightDownload);
        aboPane = getHBox(lblSelAbo, lblLeftAbo, hBoxCircleAbo, lblRightAbo);

        make();
        setVisProp();
    }

    private HBox getHBox(Label lblSel, Label lblLeft, HBox hBoxCircle, Label lblRight) {
        HBox hBox = new HBox();

        hBoxCircle.setAlignment(Pos.CENTER);
        hBoxCircle.setPadding(new Insets(0, 15, 0, 15));

        hBox.setPadding(new Insets(2, 5, 2, 5));
        hBox.setSpacing(P2LibConst.PADDING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        lblSel.setPadding(new Insets(0, 5, 0, 5));
        lblSel.getStyleClass().add("lblSelectedLines");
        lblLeft.getStyleClass().add("lblInfo");
//        lblRight.getStyleClass().add("lblInfo");

        hBox.setStyle("-fx-background-color: -fx-background;");
        hBox.getChildren().addAll(lblSel, lblLeft, hBoxCircle, P2GuiTools.getHBoxGrower(), lblRight);
        return hBox;
    }

    private void setVisProp() {
        lblSelFilm.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        lblSelFilm.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        lblLeftFilm.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        lblLeftFilm.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        lblRightFilm.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);
        lblRightFilm.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);
        hBoxCircleFilm.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);
        hBoxCircleFilm.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);

        lblSelLiveFilm.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        lblSelLiveFilm.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        lblLeftLiveFilm.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        lblLeftLiveFilm.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        lblRightLiveFilm.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);
        lblRightLiveFilm.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);
        hBoxCircleLiveFilm.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);
        hBoxCircleLiveFilm.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);

        lblSelDownload.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        lblSelDownload.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        lblLeftDownload.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        lblLeftDownload.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        lblRightDownload.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);
        lblRightDownload.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);
        hBoxCircleDownload.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);
        hBoxCircleDownload.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);

        lblSelAbo.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        lblSelAbo.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);
        lblLeftAbo.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        lblLeftAbo.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);
        lblRightAbo.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);
        lblRightAbo.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);
        hBoxCircleAbo.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);
        hBoxCircleAbo.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);
    }

    private void make() {
        this.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final ContextMenu contextMenu = new ContextMenu();
                getMenu(contextMenu);
                contextMenu.show(ProgData.getInstance().primaryStage, m.getScreenX(), m.getScreenY());
            }
        });

        stackPane.getChildren().addAll(filmPane, downloadPane, aboPane);
        stackPane.setPadding(new Insets(0));
        filmPane.toFront();
        LoadFilmFactory.getInstance().loadFilmlist.p2LoadNotifier.addListenerLoadFilmlist(new P2LoadListener() {
            @Override
            public void start(P2LoadEvent event) {
                stopTimer = true;
            }

            @Override
            public void finished(P2LoadEvent event) {
                stopTimer = false;
                setStatusbarIndex();
            }
        });

        PListener.addListener(new PListener(PListener.EVENT_TIMER_SECOND, StatusBarController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                halfSecond = !halfSecond;
                try {
                    if (!stopTimer) {
                        setStatusbarIndex();
                    }
                } catch (final Exception ex) {
                    P2Log.errorLog(936251087, ex);
                }
            }
        });
    }

    private void getMenu(ContextMenu contextMenu) {
        CheckMenuItem miOn = new CheckMenuItem("Statusleiste anzeigen");
        miOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_ON);

        CheckMenuItem miSelOn = new CheckMenuItem("Anzeige der Anzahl der markierten Zeilen");
        miSelOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_SEL);

        CheckMenuItem miLeftOn = new CheckMenuItem("Anzeige des Infobereichs links");
        miLeftOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_LEFT);

        CheckMenuItem miDotOn = new CheckMenuItem("Anzeige des Farbpunktes für die Downloads");
        miDotOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_DOT);

        CheckMenuItem miRightOn = new CheckMenuItem("Anzeige der Infos über die Filmliste");
        miRightOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_STATUS_BAR_FIELD_RIGHT);

        contextMenu.getItems().addAll(miOn, miSelOn, miLeftOn, miDotOn, miRightOn);
    }

    public void setStatusbarIndex() {
        switch (MTPlayerController.paneShown) {
            case FILM:
                filmPane.toFront();
                setInfoFilm();
                setTextForRightDisplay();
                break;
            case LIVE_FILM:
                liveFilmPane.toFront();
                setInfoLiveFilm();
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
        }
    }

    private void setInfoFilm() {
        setCircleStyle();
        lblLeftFilm.setText(DownloadInfosFactory.getStatusInfosFilm());
        final int selCount = progData.filmGuiController.getSelCount();
        lblSelFilm.setText(selCount > 0 ? selCount + "" : " ");
    }

    private void setInfoLiveFilm() {
        setCircleStyle();
        lblLeftFilm.setText(DownloadInfosFactory.getStatusInfosLiveFilm());
        final int selCount = progData.liveFilmGuiController.getSelCount();
        lblSelFilm.setText(selCount > 0 ? selCount + "" : " ");
    }

    private void setInfoDownload() {
        setCircleStyle();
        lblLeftDownload.setText(DownloadInfosFactory.getStatusInfosDownload());
        final int selCount = progData.downloadGuiController.getSelCount();
        lblSelDownload.setText(selCount > 0 ? selCount + "" : " ");
    }

    private void setInfoAbo() {
        setCircleStyle();
        lblLeftAbo.setText(DownloadInfosFactory.getStatusInfosAbo());

        final int selCount = progData.aboGuiController.getSelCount();
        lblSelAbo.setText(selCount > 0 ? selCount + "" : " ");
    }

    private boolean setCircleStyle() {
        if (halfSecond && blink) {
            //dann ausschalten
            circleFilm.setVisible(false);
            circleDownload.setVisible(false);
            circleAbo.setVisible(false);
        } else {
            circleFilm.setVisible(true);
            circleDownload.setVisible(true);
            circleAbo.setVisible(true);
        }
        blink = progData.downloadInfos.getNotStarted() > 0; //dann soll geblinkt werden

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
            circleFilm.setFill(Paint.valueOf(ProgConfig.SYSTEM_DARK_THEME.getValue() ? "#c1c1c1" : "#666666"));
            circleDownload.setFill(Paint.valueOf(ProgConfig.SYSTEM_DARK_THEME.getValue() ? "#c1c1c1" : "#666666"));
            circleAbo.setFill(Paint.valueOf(ProgConfig.SYSTEM_DARK_THEME.getValue() ? "#c1c1c1" : "#666666"));
            return false;
        }
    }

    private void setTextForRightDisplay() {
        // Text rechts: alter anzeigen
        String strText = "Filmliste erstellt: ";
        strText += progData.filmList.genDate();
        strText += " Uhr  ";

        final int second = progData.filmList.getAge();
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
