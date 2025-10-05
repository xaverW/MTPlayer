/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.starter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.SizeTools;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.tools.date.P2Date;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.log.P2Log;

import java.awt.*;
import java.util.ArrayList;

public class LogMsgFactory {
    private LogMsgFactory() {
    }

    public static void addNewDownloadMsg(DownloadData download, boolean start, boolean changeDownload) {
        P2Log.sysLog("");
        final ArrayList<String> list = new ArrayList<>();
        list.add(P2Log.LILNE3);

        if (changeDownload) {
            if (start) {
                list.add("Download ÄNDERN und STARTEN");
            } else {
                list.add("Download ÄNDERN und noch NICHT STARTEN");
            }
        } else {
            if (start) {
                list.add("Download ANLEGEN und STARTEN");
            } else {
                list.add("Download ANLEGEN und noch NICHT STARTEN");
            }
        }

        list.add("Programmset: " + (download.getSetData() == null ? "" : download.getSetData().getVisibleName()));
        list.add("Ziel: " + download.getDestPathFile());
        list.add("Titel: " + (download.getTitle()));
        list.add("URL: " + download.getUrl());
        list.add("Vorgegebene Startzeit: " + download.getStartTime());
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            list.add(DownloadConstants.TYPE_DOWNLOAD);
        } else {
            list.add("Programmaufruf: " + download.getProgramCall());
            list.add("Programmaufruf[]: " + download.getProgramCallArray());
        }

        list.add(P2Log.LILNE_EMPTY);
        P2Log.sysLog(list.toArray(new String[0]));
    }

    public static void startMsg(DownloadData download) {
        P2Log.sysLog("");
        final ArrayList<String> list = new ArrayList<>();
        final boolean play = download.getSource().equals(DownloadConstants.SRC_BUTTON);
        list.add(P2Log.LILNE3);

        if (play) {
            list.add("Film abspielen");
        } else {
            list.add("Download mit dem LADEN STARTEN");
            list.add("Programmset: " + (download.getSetData() == null ? "" : download.getSetData().getVisibleName()));
            list.add("Ziel: " + download.getDestPathFile());
        }

        list.add("Titel: " + (download.getTitle()));
        list.add("URL: " + download.getUrl());
        list.add("Vorgegebene Startzeit: " + download.getStartTime());
        list.add("Download Startzeit: " + P2DateConst.F_FORMAT_HH__mm__ss.format(download.getDownloadStartDto().getStartTime()));
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            list.add(DownloadConstants.TYPE_DOWNLOAD);
        } else {
            list.add("Programmaufruf: " + download.getProgramCall());
            list.add("Programmaufruf[]: " + download.getProgramCallArray());
        }

        list.add(P2Log.LILNE_EMPTY);
        P2Log.sysLog(list.toArray(new String[0]));
    }

    public static void restartMsg(DownloadData download) {
        P2Log.sysLog("");
        final ArrayList<String> text = new ArrayList<>();
        text.add("Fehlerhaften Download neu starten - Restart (Summe Starts: " + download.getDownloadStartDto().getStartCounter() + ')');
        text.add("Ziel: " + download.getDestPathFile());
        text.add("URL: " + download.getUrl());
        P2Log.sysLog(text.toArray(new String[text.size()]));
    }

    public static void finishedMsg(final DownloadData download) {
        final StartDownloadDto startDownloadDto = download.getDownloadStartDto();
        if (ProgConfig.DOWNLOAD_BEEP.getValue()) {
            try {
                Toolkit.getDefaultToolkit().beep();
            } catch (final Exception ignored) {
            }
        }

        P2Log.sysLog("");
        final ArrayList<String> list = new ArrayList<>();
        list.add(P2Log.LILNE3);
        if (download.isStateStopped()) {
            list.add("Download wurde abgebrochen");
        } else if (download.getSource().equals(DownloadConstants.SRC_BUTTON)) {
            list.add("Film fertig");

        } else {
            if (download.isStateFinished()) {
                // dann ists gut
                list.add("Download ist fertig und hat geklappt");
            } else if (download.isStateError()) {
                list.add("Download ist fertig und war fehlerhaft");

                if (!startDownloadDto.getErrMsgList().isEmpty()) {
                    // dann gabs Fehlermeldungen
                    startDownloadDto.getErrMsgList().forEach(s -> list.add("   > " + s));
                    list.add("");
                }
            }

            list.add("==== ==== ==== ==== ==== ==== ==== ==== ==== ");
            if (download.isProgramDownloadmanager()) {
                list.add("Programm ist ein Downloadmanager");
            }
            list.add("Programmset: " + (download.getSetData() == null ? "" : download.getSetData().getVisibleName()));
            list.add("Ziel: " + download.getDestPathFile());
        }

        list.add("==== ==== ==== ==== ==== ==== ==== ==== ==== ");
        list.add("Startzeit: " + P2DateConst.F_FORMAT_HH__mm__ss.format(startDownloadDto.getStartTime()));
        list.add("Endzeit: " + P2DateConst.F_FORMAT_HH__mm__ss.format(new P2Date().getTime()));

        if (startDownloadDto.getStartCounter() > 1) {
            list.add("Starts: " + startDownloadDto.getStartCounter());
        }

        final long dauer = startDownloadDto.getStartTime().diffInMinutes();
        if (dauer == 0) {
            list.add("Dauer: " + startDownloadDto.getStartTime().diffInSeconds() + " s");
            //list.add("Dauer: <1 Min.");
        } else {
            list.add("Dauer: " + startDownloadDto.getStartTime().diffInMinutes() + " Min");
        }

        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            if (startDownloadDto.getInputStream() != null) {
                list.add("==== ==== ==== ==== ==== ==== ==== ==== ==== ");
                list.add("Bandbreite:    " + SizeTools.humanReadableByteCount(startDownloadDto.getInputStream().getSumBandwidth(), true));
                list.add("Bytes geladen (letzter Ladezyklus): " + SizeTools.humanReadableByteCount(startDownloadDto.getInputStream().getSumByte(), true));
                list.add("Bytes geladen (letzter Ladezyklus): " + startDownloadDto.getInputStream().getSumByte() + " Byte");
                list.add("");
                list.add("Bytes soll (aus der URL):      " + SizeTools.humanReadableByteCount(download.getDownloadSize().getTargetSize(), true));
                list.add("Bytes soll (aus der URL):      " + download.getDownloadSize().getTargetSize() + " Byte");
                list.add("");
                list.add("Bytes ist (aus der Zieldatei): " + SizeTools.humanReadableByteCount(download.getDownloadSize().getActuallySize(), true));
                list.add("Bytes ist (aus der Zieldatei): " + download.getDownloadSize().getActuallySize() + " Byte");
            }

        } else {
            list.add("==== ==== ==== ==== ==== ==== ==== ==== ==== ");
            list.add("Bytes ist (aus der Zieldatei): " + SizeTools.humanReadableByteCount(download.getDownloadSize().getActuallySize(), true));
            list.add("Bytes ist (aus der Zieldatei): " + download.getDownloadSize().getActuallySize() + " Byte");
        }

        list.add("==== ==== ==== ==== ==== ==== ==== ==== ==== ");
        list.add("URL: " + download.getUrl());
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD)) {
            list.add(DownloadConstants.TYPE_DOWNLOAD);
        } else {
            list.add("Programmaufruf: " + download.getProgramCall());
            list.add("Programmaufruf[]: " + download.getProgramCallArray());
        }
        list.add(P2Log.LILNE_EMPTY);
        P2Log.sysLog(list);

        if (!download.getSource().equals(DownloadConstants.SRC_BUTTON) && !download.isStateStopped()) {
            //war ein Abo und wurde nicht abgebrochen
            new NotificationDownFinished().addNotification(download, download.isStateError());
        }
    }
}
