/*
 * P2tools Copyright (C) 2020 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.gui.chart;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Iterator;

public class BandwidthDataFactory {
    // Daten
    public static int MAX_SECONDS_SHOWING = 250 * 60; // 15_000
    public static int DATA_ALL_SECONDS = 5; // -> 3000 Daten
    public static int MAX_DATA = MAX_SECONDS_SHOWING / DATA_ALL_SECONDS; // 3_000
    public static int GET_DATA_COUNT = 0;

    // Chart
    public static int CHART_SUM_PIXEL = 500;
    public static BooleanProperty SHOW_MINUTES = new SimpleBooleanProperty(false);
    private static int count = 0;

    private BandwidthDataFactory() {
    }

    public static synchronized void addBandwidthData() {
        // wird alle 1s aufgerufen, ist der Start
        // bei langer DownloadListe alle
        ProgData progData = ProgData.getInstance();
        if (progData.downloadList.size() > 1000) {
            // dann nur alle 3s
            if (count < 2) {
                ++count;
                return;
            }
            count = 0;
        }

//        System.out.println("BandwidthDataFactory: " + P2LDateTimeFactory.toString(LocalDateTime.now()));
//        System.out.println("BandSize: " + progData.chartData.getBandwidthDataList().size());

        P2Duration.counterStart("addBandwidthData");
        inputDownloadsAndBandwidthValue(progData);
        P2Duration.counterStop("addBandwidthData");
    }

    private static synchronized void inputDownloadsAndBandwidthValue(ProgData progData) {
        // Downloads in BandwidthData eintragen und jeden Download prüfen
        boolean foundDownload;

        for (final BandwidthData bandwidthData : progData.chartData.getBandwidthDataList()) {
            // erst mal zurücksetzen
            bandwidthData.setDownloading(false);
        }

        for (final DownloadData download : progData.downloadList) {
            foundDownload = false;
            for (final BandwidthData bandwidthData : progData.chartData.getBandwidthDataList()) {
                if (bandwidthData.getDownload() != null && bandwidthData.getDownload().equals(download)) {
                    bandwidthData.setDownloading(true);
                    foundDownload = true;
                    break;
                }
            }

            // nur gestartete eintragen, die anderen haben ja noch keine Werte und werden
            // dann auch nicht angezeigt
            if (!foundDownload && download.isStateStartedRun()) {
                // dann ist es ein neu gestarteter
                BandwidthData bwd = new BandwidthData(download);
                progData.chartData.getBandwidthDataList().add(bwd);
            }
//            // dann nehmen wir noch alles
//            if (!foundDownload && download.isStarted()) {
//                // dann ist es ein neu gestarteter
//                BandwidthData bwd = new BandwidthData(download);
//                progData.chartData.getBandwidthDataList().add(bwd);
//            }
        }

        // aufräumen
        cleanBandwidthDate(progData);
        // aktuelle Bandbreite eintragen
        readBandwidthDate(progData);

        ++BandwidthDataFactory.GET_DATA_COUNT;
        if (BandwidthDataFactory.GET_DATA_COUNT >= BandwidthDataFactory.DATA_ALL_SECONDS) {
            BandwidthDataFactory.GET_DATA_COUNT = 0;
        }
    }

    private static void cleanBandwidthDate(ProgData progData) {
        // aufräumen
        Iterator<BandwidthData> it = progData.chartData.getBandwidthDataList().listIterator();
        while (it.hasNext()) {
            final BandwidthData bandwidthData = it.next();
            if (!bandwidthData.isDownloading()) {
                // dann gibst den Download nicht mehr
                bandwidthData.setDownload(null);
                if (bandwidthData.isEmpty()) {
                    it.remove();
                }
            }
        }
    }

    private static void readBandwidthDate(ProgData progData) {
        // Bandbreite eintragen
        for (final BandwidthData bandwidthData : progData.chartData.getBandwidthDataList()) {
            final DownloadData download = bandwidthData.getDownload();
            if (download != null && download.isStateStartedRun()) {
                // sonst läuft er noch nicht/nicht mehr
                final long bandwidth = download.getBandwidth();
                bandwidthData.addData(bandwidth);
            } else {
                bandwidthData.addData(0L);
            }

            //damit beim Pausieren die Nummer nicht verloren geht
            if (download != null && download.getNo() != P2LibConst.NUMBER_NOT_STARTED) {
                bandwidthData.setName(String.valueOf(download.getNo()));

            } else if (download != null) {
                bandwidthData.setName("[" + download.getFilmNo() + "]");
            }
        }
    }

    private static synchronized void inputDownloadsAndBandwidthValue_(ProgData progData) {
        // Downloads in BandwidthData eintragen und jeden Download prüfen
        boolean foundDownload;
        for (final DownloadData download : progData.downloadList) {
            foundDownload = false;
            for (final BandwidthData bandwidthData : progData.chartData.getBandwidthDataList()) {
                if (bandwidthData.getDownload() != null && bandwidthData.getDownload().equals(download)) {
                    foundDownload = true;
                    break;
                }
            }
            if (!foundDownload && download.isStarted()) {
                // dann ist es ein neu gestarteter
                BandwidthData bwd = new BandwidthData(download);
                progData.chartData.getBandwidthDataList().add(bwd);
            }
        }

        // und jetzt noch die aktuelle Bandbreite eintragen
        readBandwidthDate(progData);
        ++BandwidthDataFactory.GET_DATA_COUNT;
        if (BandwidthDataFactory.GET_DATA_COUNT >= BandwidthDataFactory.DATA_ALL_SECONDS) {
            BandwidthDataFactory.GET_DATA_COUNT = 0;
        }
    }

    private static synchronized void cleanUpBandwidthData(ProgData progData) {
        boolean foundDownload;

        Iterator<BandwidthData> it = progData.chartData.getBandwidthDataList().listIterator();
        while (it.hasNext()) {
            foundDownload = false;
            final BandwidthData bandwidthData = it.next();
            // Downloads, die es nicht mehr gibt, entfernen
            // wurde bereits alles aus "BandwidthData" gelöscht, kommts auch weg
            for (final DownloadData download : ProgData.getInstance().downloadList) {
                if (bandwidthData.getDownload() != null && bandwidthData.getDownload().equals(download)) {
                    foundDownload = true;
                    break;
                }
            }
            if (!foundDownload) {
                bandwidthData.setDownload(null);
                if (bandwidthData.isEmpty()) {
                    it.remove();
                }
            }
        }
    }

    public static synchronized void setsBandwidthDataShowingOrNot(ChartData chartData) {
        // was ist noch sichtbar
        for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
            if (bandwidthData.allValuesEmpty()) {
                //hat dann keine sichtbaren Daten mehr
                bandwidthData.setShowing(false);
                continue;
            }

            boolean downRunning = bandwidthData.getDownload() != null && bandwidthData.getDownload().isStateStartedRun();
            boolean downExist = bandwidthData.getDownload() != null;
            if (!downRunning && ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getValue()) {
                //dann gibts den Download nicht mehr und soll auch nicht angezeigt werden
                bandwidthData.setShowing(false);
                continue;

            } else if (!downExist && ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getValue()) {
                //sollen nur laufende angezeigt werden
                bandwidthData.setShowing(false);
                continue;
            }

            bandwidthData.setShowing(true);
        }
    }
}
