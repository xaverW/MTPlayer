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
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.p2Lib.tools.file.PFileSize;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class DownloadGuiInfo extends AnchorPane {

    private final VBox vBoxAll = new VBox();
    private final VBox vBoxHeader = new VBox();
    private final GridPane gridPane = new GridPane();
    private final ProgData progData;

    public DownloadGuiInfo() {
        progData = ProgData.getInstance();

        gridPane.setHgap(30);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.setId("infoGrid");

        vBoxAll.setSpacing(10);
        vBoxAll.setPadding(new Insets(10));
        vBoxAll.getChildren().addAll(vBoxHeader, gridPane);
        getChildren().add(vBoxAll);

        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);
        AnchorPane.setTopAnchor(gridPane, 0.0);

        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, DownloadGuiInfo.class.getSimpleName()) {
            @Override
            public void pingFx() {
                setInfoText();
            }
        });

    }

    private void setInfoText() {
        Text text1, text2;
        vBoxHeader.getChildren().clear();
        gridPane.getChildren().clear();

        if (progData.downloadList.size() == 0) {
            // dann gibts keine :)
            text1 = new Text("keine Downloads");
            text1.setFont(Font.font(null, FontWeight.BOLD, -1));
            vBoxHeader.getChildren().add(text1);
            gridPane.setVisible(false);
            return;
        }


        int row = 0;
        gridPane.setVisible(true);
        getInfoText();

        // Beschriftung erste Zeile
        text1 = new Text("laufende Downloads: " + progData.downloadInfos.getLoading());
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));
        gridPane.add(text1, 1, row);

        text1 = new Text("wartende Downloads: " + progData.downloadInfos.getStartedNotLoading());
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));
        gridPane.add(text1, 2, row);

        text1 = new Text("nicht gestartete Downloads: " + progData.downloadInfos.getNotStarted());
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));
        gridPane.add(text1, 3, row);

        // Beschriftung weitere Zeilen
        text1 = new Text("Größe: ");
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));
        gridPane.add(text1, 0, ++row);

        text1 = new Text("Restzeit: ");
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));
        gridPane.add(text1, 0, ++row);

        text1 = new Text("Bandbreite: ");
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));
        gridPane.add(text1, 0, ++row);

        //Downloadgröße
        row = 1;
        getSizeText(row);

        //Restzeit Downloads, wartende
        ++row;
        getRestzeit(row);

        // Bandbreite
        ++row;
        if (progData.downloadInfos.getBandwidth() > 0) {
            text2 = new Text(progData.downloadInfos.getBandwidthStr());
            gridPane.add(text2, 1, row);
        }
    }

    private void getInfoText() {
        Text text1 = new Text("Downloads: " + progData.downloadList.size());
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));
        vBoxHeader.getChildren().add(text1);

        if (progData.downloadList.size() > 0) {
            String txt;
            txt = "( ";

            if (progData.downloadInfos.getPlacedBack() != 0) {
                txt += progData.downloadInfos.getPlacedBack() + " zurückgestellt, ";
            }

            if (progData.downloadInfos.getLoading() == 1) {
                txt += "1 läuft";
            } else {
                txt += progData.downloadInfos.getLoading() + " laufen";
            }

            if (progData.downloadInfos.getStartedNotLoading() == 1) {
                txt += ", 1 wartet";
            } else {
                txt += ", " + progData.downloadInfos.getStartedNotLoading() + " warten";
            }

            if (progData.downloadInfos.getFinishedOk() > 0) {
                if (progData.downloadInfos.getFinishedOk() == 1) {
                    txt += ", 1 fertig";
                } else {
                    txt += ", " + progData.downloadInfos.getFinishedOk() + " fertig";
                }
            }

            if (progData.downloadInfos.getFinishedError() > 0) {
                if (progData.downloadInfos.getFinishedError() == 1) {
                    txt += ", 1 fehlerhaft";
                } else {
                    txt += ", " + progData.downloadInfos.getFinishedError() + " fehlerhaft";
                }
            }

            int m3u8 = progData.downloadInfos.getLoadingM3u8();
            if (m3u8 == 1) {
                txt += ", ein Download ist ein Stream, Zeit- und Größenangaben sind nur Schätzungen";
            } else if (m3u8 > 1) {
                txt += ", " + m3u8 + " Downloads sind Streams, Zeit- und Größenangaben sind nur Schätzungen";
            }

            txt += " )";
            Text text2 = new Text(txt);
            vBoxHeader.getChildren().add(text2);
        }
    }

    private void getSizeText(int row) {
        Text text2;
        // Größe laufende Downloads
        if (progData.downloadInfos.getByteLoadingDownloads() > 0 ||
                progData.downloadInfos.getByteLoadingDownloadsAlreadyLoaded() > 0) {

            if (progData.downloadInfos.getByteLoadingDownloads() > 0) {
                text2 = new Text(PFileSize.convertToStr(progData.downloadInfos.getByteLoadingDownloadsAlreadyLoaded()) + " von "
                        + PFileSize.convertToStr(progData.downloadInfos.getByteLoadingDownloads()));

            } else {
                text2 = new Text(PFileSize.convertToStr(progData.downloadInfos.getByteLoadingDownloadsAlreadyLoaded()));
            }
            gridPane.add(text2, 1, row);
        }

        // Größe wartende Downloads
        if (progData.downloadInfos.getByteWaitingDownloads() > 0) {
            text2 = new Text(PFileSize.convertToStr(progData.downloadInfos.getByteWaitingDownloads()));
            gridPane.add(text2, 2, row);
        }

        // Größe nicht gestartete Downloads
        if (progData.downloadInfos.getByteNotStartedDownloads() > 0) {
            text2 = new Text(PFileSize.convertToStr(progData.downloadInfos.getByteNotStartedDownloads()));
            gridPane.add(text2, 3, row);
        }
    }

    private void getRestzeit(int row) {
        Text text2;
        // Restzeit laufende Downloads
        if (progData.downloadInfos.getTimeLeftLoadingDownloads() > 0 &&
                progData.downloadInfos.getTimeLeftLoadingDownloads() > 0) {
            text2 = new Text(progData.downloadInfos.getTimeLeftLoading());
            gridPane.add(text2, 1, row);
        }

        // Restzeit wartende Downloads
        if (progData.downloadInfos.getTimeLeftWaitingDownloads() > 0) {
            text2 = new Text(progData.downloadInfos.getTimeLeftWaiting());
            gridPane.add(text2, 2, row);
        }

        // Restzeit nicht gestartete Downloads
        if (progData.downloadInfos.getTimeLeftNotStartedDownloads() > 0) {
            text2 = new Text(progData.downloadInfos.getTimeLeftNotStarted());
            gridPane.add(text2, 3, row);
        }
    }
}
