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
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.gui.tools.Listener;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

public class DownloadGuiInfo {

    final private AnchorPane anchorPane;
    final private WebView webView;
    final private Daten daten;

    public DownloadGuiInfo(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        this.webView = new WebView();
        daten = Daten.getInstance();

        anchorPane.getChildren().add(webView);

        AnchorPane.setLeftAnchor(webView, 10.0);
        AnchorPane.setBottomAnchor(webView, 10.0);
        AnchorPane.setRightAnchor(webView, 10.0);
        AnchorPane.setTopAnchor(webView, 10.0);

        webView.getEngine().loadContent("");
        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, StatusBarController.class.getSimpleName()) {
            @Override
            public void ping() {
                setInfoText();
            }
        });

    }

    private void setInfoText() {
        final int[] starts = daten.downloadList.getDownload_infosAll().downloadStarts;
        if (starts[0] == 0) {
            webView.getEngine().loadContent("");
            return;
        }
        final String HEAD = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "<head><style type=\"text/css\"> .sans { font-family: Verdana, Geneva, sans-serif; }</style></head>\n"
                + "<body>\n";
        final String END = "</body></html>";

        String info = HEAD;

        // Downloads
        info += getInfoText();
        // Größe
        if (daten.downloadList.getDownload_infosAll().byteAlleDownloads > 0 || daten.downloadList.getDownload_infosAll().byteAktDownloads > 0) {
            info += "<br />";
            info += "<span class=\"sans\"><b>Größe:</b><br />";
            if (daten.downloadList.getDownload_infosAll().byteAktDownloads > 0) {
                info += SizeTools.getGroesse(daten.downloadList.getDownload_infosAll().byteAktDownloads) + " von "
                        + SizeTools.getGroesse(daten.downloadList.getDownload_infosAll().byteAlleDownloads) + " MByte" + "</span>";
            } else {
                info += SizeTools.getGroesse(daten.downloadList.getDownload_infosAll().byteAlleDownloads) + " MByte" + "</span>";
            }
        }
        // Restzeit
        if (daten.downloadList.getDownload_infosAll().timeRestAktDownloads > 0 && daten.downloadList.getDownload_infosAll().timeRestAllDownloads > 0) {
            info += "<br />";
            info += "<span class=\"sans\"><b>Restzeit:</b><br />" + "laufende: "
                    + daten.downloadList.getDownload_infosAll().getRestzeit() + ",<br />alle: " + daten.downloadList.getDownload_infosAll().getGesamtRestzeit() + "</span>";
        } else if (daten.downloadList.getDownload_infosAll().timeRestAktDownloads > 0) {
            info += "<br />";
            info += "<span class=\"sans\"><b>Restzeit:</b><br />laufende: " + daten.downloadList.getDownload_infosAll().getRestzeit() + "</span>";
        } else if (daten.downloadList.getDownload_infosAll().timeRestAllDownloads > 0) {
            info += "<br />";
            info += "<span class=\"sans\"><b>Restzeit:</b><br />alle: " + daten.downloadList.getDownload_infosAll().getGesamtRestzeit() + "</span>";
        }
        // Bandbreite
        if (daten.downloadList.getDownload_infosAll().bandwidth > 0) {
            info += "<br />";
            info += "<span class=\"sans\"><b>Bandbreite:</b><br />";
            info += daten.downloadList.getDownload_infosAll().bandwidthStr + "</span>";
        }
        info += END;

        webView.getEngine().loadContent(info);
    }

    private String getInfoText() {
        String textLinks;
        // Text links: Zeilen Tabelle
        // nicht gestarted, laufen, fertig OK, fertig fehler
        final int[] starts = daten.downloadList.getDownload_infosAll().downloadStarts;
        textLinks = "<span class=\"sans\"><b>Downloads:  </b>" + starts[0] + "<br />";
        boolean print = false;
        for (int ii = 1; ii < starts.length; ++ii) {
            if (starts[ii] > 0) {
                print = true;
                break;
            }
        }
        if (print) {
            textLinks += "( ";
            if (starts[4] == 1) {
                textLinks += "1 läuft";
            } else {
                textLinks += starts[4] + " laufen";
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
            textLinks += " )";
        }
        textLinks += "<br /></span>";
        return textLinks;
    }

}
