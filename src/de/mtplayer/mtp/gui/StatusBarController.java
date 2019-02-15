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
import de.mtplayer.mtp.controller.data.abo.Abo;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;
import de.mtplayer.mtp.controller.data.download.DownloadFactory;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.text.NumberFormat;
import java.util.Locale;

public class StatusBarController extends AnchorPane {

    private final StackPane stackPane = new StackPane();

    //Film
    private final Label lblSelFilm = new Label();
    private final Label lblLeftFilm = new Label();
    private final Label lblRightFilm = new Label();

    //Download
    private final Label lblSelDownload = new Label();
    private final Label lblLeftDownload = new Label();
    private final Label lblRightDownload = new Label();

    //Abo
    private final Label lblSelAbo = new Label();
    private final Label lblLeftAbo = new Label();
    private final Label lblRightAbo = new Label();

    private final Pane nonePane;
    private final Pane filmPane;
    private final Pane downloadPane;
    private final Pane aboPane;

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);


    public enum StatusbarIndex {NONE, FILM, DOWNLOAD, ABO}

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

        nonePane = new HBox();
        filmPane = getHbox(lblSelFilm, lblLeftFilm, lblRightFilm);
        downloadPane = getHbox(lblSelDownload, lblLeftDownload, lblRightDownload);
        aboPane = getHbox(lblSelAbo, lblLeftAbo, lblRightAbo);

        make();
    }

    private HBox getHbox(Label lblSel, Label lblLeft, Label lblRight) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2, 5, 2, 5));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        lblLeft.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeft, Priority.ALWAYS);
        hBox.getChildren().addAll(lblSel, lblLeft, lblRight);
        hBox.setStyle("-fx-background-color: -fx-background ;");
        lblSel.getStyleClass().add("lblSelectedLines");

        return hBox;
    }


    private void make() {
        stackPane.getChildren().addAll(nonePane, filmPane, downloadPane, aboPane);
        nonePane.toFront();

//        lblSelFilm.getStyleClass().add("lblSelectedLines");
//        lblSelDownload.getStyleClass().add("lblSelectedLines");
//        lblSelAbo.getStyleClass().add("lblSelectedLines");

        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                stopTimer = true;
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                stopTimer = false;
                Platform.runLater(() -> setStatusbarIndex(statusbarIndex));
            }
        });

        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, StatusBarController.class.getSimpleName()) {
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
        String textLinks;
        final int sumFilmlist = progData.filmlist.size();
        final int sumFilmShown = progData.filmGuiController.getFilmCount();
        final int runs = progData.downloadListButton.getListOfStartsNotFinished(DownloadConstants.SRC_BUTTON).size();

        String sumFilmlistStr = numberFormat.format(sumFilmShown);
        String sumFilmShownStr = numberFormat.format(sumFilmlist);


        // Anzahl der Filme
        if (sumFilmShown == 1) {
            textLinks = "1 Film";
        } else {
            textLinks = sumFilmlistStr + " Filme";
        }
        if (sumFilmlist != sumFilmShown) {
            textLinks += " (Insgesamt: " + sumFilmShownStr + " )";
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
        int countOn = 0;
        int countOff = 0;
        final int sumAboList = progData.aboList.size();
        final int sumAboShown = progData.aboGuiController.getAboCount();

        for (final Abo abo : progData.aboList) {
            if (abo.isActive()) {
                ++countOn;
            } else {
                ++countOff;
            }
        }

        String sumAboListStr = numberFormat.format(sumAboList);
        String sumAboShownStr = numberFormat.format(sumAboShown);

        // Anzahl der Abos
        if (sumAboShown == 1) {
            textLinks = "1 Abo";
        } else {
            textLinks = sumAboShownStr + " Abos";
        }
        if (sumAboList != sumAboShown) {
            textLinks += " (Insgesamt: " + sumAboListStr;
            textLinks += ")";
        }

        textLinks += SEPARATOR + countOn + " eingeschaltet, " + countOff + " ausgeschaltet";

        lblLeftAbo.setText(textLinks);


        final int selCount = progData.aboGuiController.getSelCount();
        lblSelAbo.setText(selCount > 0 ? selCount + "" : " ");

    }

    private String getInfoTextDownloads(boolean showMoreInfos) {
        String textLinks;
        // Text links: Zeilen Tabelle
        // nicht gestarted, laufen, fertig OK, fertig fehler
        final int[] downloadInfos = progData.downloadList.getDownloadListInfoAll().downloadInfos;

        final int sumDownloadList = progData.downloadList.size();
        final int sumDownloadsShown = progData.downloadGuiController.getDownloadsShown();

        String sumDownloadListStr = numberFormat.format(sumDownloadList);
        String sumDownloadsShownStr = numberFormat.format(sumDownloadsShown);
        final int diff = sumDownloadList - downloadInfos[DownloadFactory.INFO.AMOUNT.getI()];

        boolean printDownloadStatus = false;
        for (int ii = 1; ii < downloadInfos.length; ++ii) {
            // nur wenn ein Download läuft, wartet, ..
            if (downloadInfos[ii] > 0) {
                printDownloadStatus = true;
                break;
            }
        }

        // Anzahl der Downloads
        if (sumDownloadsShown == 1) {
            textLinks = "1 Download";
        } else {
            textLinks = sumDownloadsShownStr + " Downloads";
        }

        // weitere Infos anzeigen, wenn gewünscht
        if (showMoreInfos && sumDownloadList != sumDownloadsShown) {
            textLinks += " (Insgesamt: " + sumDownloadListStr;
            if (diff >= 1) {
                textLinks += ", zurückgestellt: " + numberFormat.format(diff);
            }
            textLinks += ")";
        }

        if (!showMoreInfos) {
            // Filmtab
            textLinks += ": ";

        } else {
            if (downloadInfos[DownloadFactory.INFO.AMOUNT_ABO.getI()] > 0
                    && downloadInfos[DownloadFactory.INFO.AMOUNT_DOWNLOAD.getI()] == 0) {
                // nur Abos
                textLinks += SEPARATOR;
                textLinks += "nur Abos";
                textLinks += SEPARATOR;

            } else if (downloadInfos[DownloadFactory.INFO.AMOUNT_ABO.getI()] == 0 &&
                    downloadInfos[DownloadFactory.INFO.AMOUNT_DOWNLOAD.getI()] > 0) {
                // keine Abos
                textLinks += SEPARATOR;
                textLinks += "nur direkte Downloads";
                textLinks += SEPARATOR;

            } else if (downloadInfos[DownloadFactory.INFO.AMOUNT_ABO.getI()] > 0
                    && downloadInfos[DownloadFactory.INFO.AMOUNT_DOWNLOAD.getI()] > 0) {

                textLinks += SEPARATOR;

                // Abos
                if (downloadInfos[DownloadFactory.INFO.AMOUNT_ABO.getI()] == 1) {
                    textLinks += " 1 Abo, ";
                } else {
                    textLinks += numberFormat.format(downloadInfos[DownloadFactory.INFO.AMOUNT_ABO.getI()]) + " Abos, ";
                }

                // Downloads
                if (downloadInfos[DownloadFactory.INFO.AMOUNT_DOWNLOAD.getI()] == 1) {
                    textLinks += "1 Download";
                } else if (downloadInfos[DownloadFactory.INFO.AMOUNT_DOWNLOAD.getI()] > 1) {
                    textLinks += numberFormat.format(downloadInfos[DownloadFactory.INFO.AMOUNT_DOWNLOAD.getI()]) + " Downloads";
                }

                textLinks += SEPARATOR;
            }

        }

        if (printDownloadStatus) {
            if (downloadInfos[DownloadFactory.INFO.LOADING.getI()] == 1) {
                textLinks += "1 läuft";
            } else {
                textLinks += downloadInfos[DownloadFactory.INFO.LOADING.getI()] + " laufen";
            }

            if (downloadInfos[DownloadFactory.INFO.LOADING.getI()] > 0) {
                textLinks += " (" + progData.downloadList.getDownloadListInfoAll().bandwidthStr + ')';
            }

            if (downloadInfos[DownloadFactory.INFO.NOT_STARTED.getI()] == 1) {
                textLinks += ", 1 wartet";
            } else {
                textLinks += ", " + numberFormat.format(downloadInfos[DownloadFactory.INFO.NOT_STARTED.getI()]) + " warten";
            }
            if (downloadInfos[DownloadFactory.INFO.FINISHED_OK.getI()] > 0) {
                if (downloadInfos[DownloadFactory.INFO.FINISHED_OK.getI()] == 1) {
                    textLinks += ", 1 fertig";
                } else {
                    textLinks += ", " + numberFormat.format(downloadInfos[DownloadFactory.INFO.FINISHED_OK.getI()]) + " fertig";
                }
            }
            if (downloadInfos[DownloadFactory.INFO.FINISHED_NOT_OK.getI()] > 0) {
                if (downloadInfos[DownloadFactory.INFO.FINISHED_NOT_OK.getI()] == 1) {
                    textLinks += ", 1 fehlerhaft";
                } else {
                    textLinks += ", " + numberFormat.format(downloadInfos[DownloadFactory.INFO.FINISHED_NOT_OK.getI()]) + " fehlerhaft";
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
    }


}
