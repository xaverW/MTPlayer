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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.text.NumberFormat;
import java.util.Locale;

public class StatusBarController extends AnchorPane {

    StackPane stackPane = new StackPane();

    // loadPane
    Label lblProgress = new Label();
    ProgressBar progress = new ProgressBar();
    Button btnStop = new Button("");

    //Film
    Label lblSelFilm = new Label();
    Label lblLeftFilm = new Label();
    Label lblRightFilm = new Label();

    //Download
    Label lblSelDownload = new Label();
    Label lblLeftDownload = new Label();
    Label lblRightDownload = new Label();

    //Abo
    Label lblSelAbo = new Label();
    Label lblLeftAbo = new Label();
    Label lblRightAbo = new Label();

    //nonePane
    Label lblLeftNone = new Label();
    Label lblRightNone = new Label();

    AnchorPane loadPane = new AnchorPane();
    AnchorPane nonePane = new AnchorPane();
    AnchorPane filmPane = new AnchorPane();
    AnchorPane downloadPane = new AnchorPane();
    AnchorPane aboPane = new AnchorPane();


    public enum StatusbarIndex {

        NONE, FILM, DOWNLOAD, ABO
    }

    private StatusbarIndex statusbarIndex = StatusbarIndex.NONE;
    private boolean loadList = false;

    private final ProgData progData;
    private boolean stopTimer = false;
    private static final String SEPARATOR = "  ||  ";

    public StatusBarController(ProgData progData) {
        this.progData = progData;

        getChildren().addAll(stackPane);
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
        AnchorPane.setRightAnchor(stackPane, 0.0);
        AnchorPane.setTopAnchor(stackPane, 0.0);


        HBox hBox = getHbox();
        lblLeftNone.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeftNone, Priority.ALWAYS);
        hBox.getChildren().addAll(lblLeftNone, lblRightNone);
        nonePane.getChildren().add(hBox);
        nonePane.setStyle("-fx-background-color: -fx-background ;");

        hBox = getHbox();
        btnStop.setGraphic(new Icons().ICON_BUTTON_STOP);
        hBox.getChildren().addAll(lblProgress, progress, btnStop);
        progress.setPrefWidth(200);
        loadPane.getChildren().add(hBox);
        loadPane.setStyle("-fx-background-color: -fx-background ;");

        hBox = getHbox();
        lblLeftFilm.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeftFilm, Priority.ALWAYS);
        hBox.getChildren().addAll(lblSelFilm, lblLeftFilm, lblRightFilm);
        filmPane.getChildren().add(hBox);
        filmPane.setStyle("-fx-background-color: -fx-background ;");

        hBox = getHbox();
        lblLeftDownload.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeftDownload, Priority.ALWAYS);
        hBox.getChildren().addAll(lblSelDownload, lblLeftDownload, lblRightDownload);
        downloadPane.getChildren().add(hBox);
        downloadPane.setStyle("-fx-background-color: -fx-background ;");

        hBox = getHbox();
        lblLeftAbo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeftAbo, Priority.ALWAYS);
        hBox.getChildren().addAll(lblSelAbo, lblLeftAbo, lblRightAbo);
        aboPane.getChildren().add(hBox);
        aboPane.setStyle("-fx-background-color: -fx-background ;");

        make();
    }

    private HBox getHbox() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2, 5, 2, 5));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        AnchorPane.setLeftAnchor(hBox, 0.0);
        AnchorPane.setBottomAnchor(hBox, 0.0);
        AnchorPane.setRightAnchor(hBox, 0.0);
        AnchorPane.setTopAnchor(hBox, 0.0);
        return hBox;
    }


    private void make() {
        stackPane.getChildren().addAll(nonePane, loadPane, filmPane, downloadPane, aboPane);
        nonePane.toFront();

        lblSelFilm.getStyleClass().add("lblSelectedLines");
        lblSelDownload.getStyleClass().add("lblSelectedLines");
        lblSelAbo.getStyleClass().add("lblSelectedLines");

        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                loadList = true;
                setStatusbar();
            }

            @Override
            public void progress(ListenerFilmlistLoadEvent event) {
                updateProgressBar(event);
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                stopTimer = false;
                loadList = false;
                setStatusbar();
            }
        });
        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, StatusBarController.class.getSimpleName()) {
            @Override
            public void ping() {
                try {
                    if (!stopTimer) {
                        setStatusbar();
                    }
                } catch (final Exception ex) {
                    PLog.errorLog(936251087, ex);
                }
            }
        });
        btnStop.setOnAction(a -> progData.loadFilmlist.setStop(true));
    }

    public void setStatusbar() {
        setStatusbarIndex(statusbarIndex);
    }

    public void setStatusbarIndex(StatusbarIndex statusbarIndex) {
        this.statusbarIndex = statusbarIndex;
        if (loadList) {
            loadPane.toFront();
            return;
        }

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
                setTextNone();
                setTextForRightDisplay();
                break;
        }
    }

    private void updateProgressBar(ListenerFilmlistLoadEvent event) {
        stopTimer = true;
        progress.setProgress(event.progress);
        lblProgress.setText(event.text);
    }


    private void setTextNone() {
        final int anzAll = progData.filmlist.size();
        lblLeftNone.setText("Anzahl Filme: " + anzAll);
    }

    private void setInfoFilm() {
        String textLinks;
        final int gesamt = progData.filmlist.size();
        final int anzListe = progData.filmGuiController.getFilmCount();
        final int runs = progData.downloadListButton.getListOfStartsNotFinished(DownloadInfos.SRC_BUTTON).size();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        String anzListeStr = numberFormat.format(anzListe);
        String gesamtStr = numberFormat.format(gesamt);


        // Anzahl der Filme
        if (gesamt == anzListe) {
            if (anzListe == 1) {
                textLinks = "1 Film";
            } else {
                textLinks = anzListeStr + " Filme";
            }
        } else {
            if (anzListe == 1) {
                textLinks = "1 Film";
            } else {
                textLinks = anzListeStr + " Filme";
            }
            textLinks += " (Insgesamt: " + gesamtStr + " )";
        }
        // laufende Programme
        if (runs == 1) {
            textLinks += SEPARATOR;
            textLinks += (runs + " laufender Film");
        } else if (runs > 1) {
            textLinks += SEPARATOR;
            textLinks += (runs + " laufende Filme");
        }
        // auch die Downloads anzeigen
        textLinks += SEPARATOR;
        textLinks += getInfoTextDownloads(false);
        lblLeftFilm.setText(textLinks);

        final int selCount = progData.filmGuiController.getSelCount();
        lblSelFilm.setText(selCount > 0 ? selCount + "" : " ");
    }

    private void setInfoDownload() {
        final String textLinks = getInfoTextDownloads(true /* mitAbo */);
        lblLeftDownload.setText(textLinks);

        final int selCount = progData.downloadGuiController.getSelCount();
        lblSelDownload.setText(selCount > 0 ? selCount + "" : " ");
    }

    private void setInfoAbo() {

        String textLinks;
        int ein = 0;
        int aus = 0;
        final int gesamt = progData.aboList.size();
        for (final Abo abo : progData.aboList) {
            if (abo.getActive()) {
                ++ein;
            } else {
                ++aus;
            }
        }
        if (gesamt == 1) {
            textLinks = "1 Abo";
        } else {
            textLinks = gesamt + " Abos";
        }
        textLinks += SEPARATOR + ein + " eingeschaltet, " + aus + " ausgeschaltet";

        lblLeftAbo.setText(textLinks);

        final int selCount = progData.aboGuiController.getSelCount();
        lblSelAbo.setText(selCount > 0 ? selCount + "" : " ");

    }

    private String getInfoTextDownloads(boolean download) {
        String textLinks;
        // Text links: Zeilen Tabelle
        // nicht gestarted, laufen, fertig OK, fertig fehler
        final int[] starts = progData.downloadList.getDownloadInfoAll().downloadStarts;
        final int anz = progData.downloadList.size();
        final int diff = anz - starts[0];

        boolean print = false;
        for (int ii = 1; ii < starts.length; ++ii) {
            if (starts[ii] > 0) {
                print = true;
                break;
            }
        }

        if (anz == 1) {
            textLinks = "1 Download";
        } else {
            textLinks = anz + " Downloads";
        }
        if (download) {
            if (diff == 1) {
                textLinks += " (1 zurückgestellt)";
            } else if (diff > 1) {
                textLinks += " (" + diff + " zurückgestellt)";
            }
            textLinks += SEPARATOR;
            if (starts[1] == 1) {
                textLinks += "1 Abo, ";
            } else {
                textLinks += "" + starts[1] + " Abos, ";
            }
            if (starts[2] == 1) {
                textLinks += "1 Download";
            } else {
                textLinks += starts[2] + " Downloads";
            }
            textLinks += SEPARATOR;
        } else if (print) {
            textLinks += ": ";
        }

        if (print) {
            if (starts[4] == 1) {
                textLinks += "1 läuft";
            } else {
                textLinks += starts[4] + " laufen";
            }

            if (starts[4] > 0) {
                textLinks += " (" + progData.downloadList.getDownloadInfoAll().bandwidthStr + ')';
            }

            if (starts[3] == 1) {
                textLinks += ", 1 wartet";
            } else {
                textLinks += ", " + starts[3] + " warten";
            }
            if (starts[5] > 0) {
                if (starts[5] == 1) {
                    textLinks += ", 1 fertig";
                } else {
                    textLinks += ", " + starts[5] + " fertig";
                }
            }
            if (starts[6] > 0) {
                if (starts[6] == 1) {
                    textLinks += ", 1 fehlerhaft";
                } else {
                    textLinks += ", " + starts[6] + " fehlerhaft";
                }
            }
        }

        return textLinks;
    }

    private void setTextForRightDisplay() {
        // Text rechts: alter/neuladenIn anzeigen
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
        lblRightNone.setText(strText);
    }


}
