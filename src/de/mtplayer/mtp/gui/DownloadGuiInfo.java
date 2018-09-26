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

import de.mtplayer.mLib.tools.SizeTools;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.PConst;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class DownloadGuiInfo {

    private final GridPane gridPane = new GridPane();
    private final ProgData progData;
    private int row = 0;


    public DownloadGuiInfo(AnchorPane anchorPane) {
        progData = ProgData.getInstance();

        gridPane.setHgap(5);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        anchorPane.getChildren().add(gridPane);

        AnchorPane.setLeftAnchor(gridPane, 10.0);
        AnchorPane.setBottomAnchor(gridPane, 10.0);
        AnchorPane.setRightAnchor(gridPane, 10.0);
        AnchorPane.setTopAnchor(gridPane, 10.0);

        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, DownloadGuiInfo.class.getSimpleName()) {
            @Override
            public void ping() {
                setInfoText();
            }
        });

    }

    private void setInfoText() {
        final int[] starts = progData.downloadList.getDownloadInfoAll().downloadStarts;
        Text text1, text2;
        gridPane.getChildren().clear();
        row = 0;

        if (starts[0] == 0) {
            return;
        }

        // Downloads
        getInfoText();

        // Größe
        if (progData.downloadList.getDownloadInfoAll().byteAllDownloads > 0 || progData.downloadList.getDownloadInfoAll().byteAktDownloads > 0) {
            text1 = new Text("Größe: ");
            text1.setFont(Font.font(null, FontWeight.BOLD, -1));

            if (progData.downloadList.getDownloadInfoAll().byteAktDownloads > 0) {

                text2 = new Text(SizeTools.getSize(progData.downloadList.getDownloadInfoAll().byteAktDownloads) + " von "
                        + SizeTools.getSize(progData.downloadList.getDownloadInfoAll().byteAllDownloads) + " MByte");
            } else {
                text2 = new Text(SizeTools.getSize(progData.downloadList.getDownloadInfoAll().byteAllDownloads) + " MByte");
            }
            ++row;
            gridPane.add(text1, 0, ++row);
            gridPane.add(text2, 1, row);
        }

        // Restzeit
        if (progData.downloadList.getDownloadInfoAll().timeRestAktDownloads > 0 && progData.downloadList.getDownloadInfoAll().timeRestAllDownloads > 0) {
            text1 = new Text("Restzeit: ");
            text1.setFont(Font.font(null, FontWeight.BOLD, -1));
            text2 = new Text("laufende: "
                    + progData.downloadList.getDownloadInfoAll().getTimeLeft() + "," +
                    PConst.LINE_SEPARATOR +
                    "alle: " + progData.downloadList.getDownloadInfoAll().getSumeTimeLeft());
            gridPane.add(text1, 0, ++row);
            gridPane.add(text2, 1, row);

        } else if (progData.downloadList.getDownloadInfoAll().timeRestAktDownloads > 0) {
            text1 = new Text("Restzeit: ");
            text1.setFont(Font.font(null, FontWeight.BOLD, -1));
            text2 = new Text("laufende: " + progData.downloadList.getDownloadInfoAll().getTimeLeft());
            gridPane.add(text1, 0, ++row);
            gridPane.add(text2, 1, row);

        } else if (progData.downloadList.getDownloadInfoAll().timeRestAllDownloads > 0) {
            text1 = new Text("Restzeit: ");
            text1.setFont(Font.font(null, FontWeight.BOLD, -1));
            text2 = new Text("alle: " + progData.downloadList.getDownloadInfoAll().getSumeTimeLeft());
            gridPane.add(text1, 0, ++row);
            gridPane.add(text2, 1, row);
        }

        // Bandbreite
        if (progData.downloadList.getDownloadInfoAll().bandwidth > 0) {
            text1 = new Text("Bandbreite: ");
            text1.setFont(Font.font(null, FontWeight.BOLD, -1));
            text2 = new Text(progData.downloadList.getDownloadInfoAll().bandwidthStr);
            gridPane.add(text1, 0, ++row);
            gridPane.add(text2, 1, row);
        }
    }

    private void getInfoText() {
        String textLinks;
        // Text links: Zeilen Tabelle
        // nicht gestarted, laufen, fertig OK, fertig fehler
        final int[] starts = progData.downloadList.getDownloadInfoAll().downloadStarts;

        VBox vBox = new VBox(2);
        Text text1 = new Text("Downloads: " + starts[0]);
        text1.setFont(Font.font(null, FontWeight.BOLD, -1));
        vBox.getChildren().add(text1);
        gridPane.add(vBox, 0, row, 2, 1);

        boolean print = false;
        for (int ii = 1; ii < starts.length; ++ii) {
            if (starts[ii] > 0) {
                print = true;
                break;
            }
        }
        if (print) {
            String txt;
            txt = "( ";
            if (starts[4] == 1) {
                txt += "1 läuft";
            } else {
                txt += starts[4] + " laufen";
            }
            if (starts[3] == 1) {
                txt += ", 1 wartet";
            } else {
                txt += ", " + starts[3] + " warten";
            }
            if (starts[5] > 0) {
                if (starts[5] == 1) {
                    txt += ", 1 fertig";
                } else {
                    txt += ", " + starts[5] + " fertig";
                }
            }
            if (starts[6] > 0) {
                if (starts[6] == 1) {
                    txt += ", 1 fehlerhaft";
                } else {
                    txt += ", " + starts[6] + " fehlerhaft";
                }
            }
            txt += " )";
            Text text2 = new Text(txt);
            vBox.getChildren().add(text2);
        }


    }

}
