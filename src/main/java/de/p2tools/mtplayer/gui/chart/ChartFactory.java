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

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.P2LibConst;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;

import java.util.Iterator;

public class ChartFactory {
    public static int MAX_CHART_DATA_PER_SCREEN = 60;
    public static int DATA_ALL_SECONDS = 2;

    public static int MAX_MINUTES_SHOWING = 300;
    public static int MAX_SECONDS_SHOWING = MAX_MINUTES_SHOWING * 60; //18_000

    public static int MAX_DATA = MAX_SECONDS_SHOWING / DATA_ALL_SECONDS; // 9_000
    private static int tmpCount = 0;

    private ChartFactory() {
    }

    public static synchronized void runChart(LineChart<Number, Number> lineChart, ChartData chartData,
                                             ProgData progData, boolean visible) {
        chartData.addCountSek(); // Sekunden
        chartData.genActValues();
        cleanUpData(chartData, progData);
        inputDownloadData(chartData, progData);

        ++tmpCount;
        if (tmpCount >= ChartFactory.DATA_ALL_SECONDS) {
            tmpCount = 0;
            addTmpData(chartData);
        }

        if (visible) {
            ChartFactoryGenerateData.setChartDataShowing(chartData);
            ChartFactoryGenerateData.generateYScale(lineChart, chartData);
            ChartFactoryGenerateData.genChartSeries(chartData);

            ChartFactoryGenerateChartData.generateChartData(lineChart, chartData);
//            lineChart.getXAxis().setAutoRanging(true);
            ChartFactoryGenerateData.zoomXAxis(lineChart, chartData);

//            ChartFactoryGenerateData.zoomYAxis(lineChart, chartData);
        }
    }

    public static NumberAxis createXAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLabel("Programmlaufzeit [min]");
        xAxis.setLowerBound(0.0);
        xAxis.setSide(Side.RIGHT);
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() > 60) {
                    int i = (int) object.doubleValue();
                    return i + "";
                } else {
                    int i = (int) (object.doubleValue() * 10);
                    return ((i / 10.0) + "");
                }
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        return xAxis;
    }

    public static NumberAxis createYAxis() {
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLowerBound(0.0);
        yAxis.setLabel("Wert");
        return yAxis;
    }

    private static synchronized void cleanUpData(ChartData chartData, ProgData progData) {
        chartData.setyScale(1);
        boolean foundDownload;

        //alle Messpunkte vor MAX-Zeit löschen
        chartData.getBandwidthDataList().stream().forEach(bandwidthData -> {
            while (bandwidthData.size() > ChartFactory.MAX_DATA) {
                bandwidthData.removeFirst();
            }
            while ((chartData.getCountProgRunningTimeSeconds() - bandwidthData.getStartTimeSec()) > ChartFactory.MAX_SECONDS_SHOWING) {
                if (bandwidthData.isEmpty()) {
                    break;
                }
                bandwidthData.removeFirst();
            }
        });

        //Downloads die es nicht mehr gibt: "Download" entfernen
        //wurde bereits alles aus "BandwidthData" gelöscht, kommts auch weg
        Iterator<BandwidthData> it = chartData.getBandwidthDataList().listIterator();
        while (it.hasNext()) {
            foundDownload = false;
            final BandwidthData bandwidthData = it.next();
            for (final DownloadData download : progData.downloadList) {
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

        //fertige Downloads abschließen, Download gibts nicht mehr oder läuft nicht mehr,
        //dann einen Wert "0" anfügen, wenn noch nicht geschehen
        it = chartData.getBandwidthDataList().listIterator();
        while (it.hasNext()) {
            BandwidthData bandwidthData = it.next();
            foundDownload = false;
            for (final DownloadData download : progData.downloadList.getListOfStartsNotFinished(DownloadConstants.ALL)) {
                if (bandwidthData.getDownload() != null && bandwidthData.getDownload().equals(download)) {
                    foundDownload = true;
                    break;
                }
            }
            if (!foundDownload) {
                bandwidthData.cleanUpData();
            }
        }
    }

    private static synchronized void inputDownloadData(ChartData chartData, ProgData progData) {
        //Downloads in BandwidthData eintragen und jeden Download prüfen
        boolean foundDownload;
        for (final DownloadData download : progData.downloadList) {
            foundDownload = false;
            for (final BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
                if (bandwidthData.getDownload() != null && bandwidthData.getDownload().equals(download)) {
                    foundDownload = true;
                    break;
                }
            }
            if (!foundDownload && download.isStarted()) {
                // dann ist es ein neu gestarteter
                BandwidthData bwd = new BandwidthData(chartData, download);
                chartData.getBandwidthDataList().add(bwd);
            }
        }

        for (final BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
            final DownloadData download = bandwidthData.getDownload();
            if (download != null && download.isStateStartedRun()) {
                // sonst läuft er noch nicht/nicht mehr
                final long bandwidth = download.getStart().getBandwidth();
                bandwidthData.addTmpData(bandwidth);
            } else {
                bandwidthData.addTmpData(0L);
            }

            //damit beim Pausieren die Nummer nicht verloren geht
            if (download != null && download.getNo() != P2LibConst.NUMBER_NOT_STARTED) {
                bandwidthData.setName(download.getNo() + "");

            } else if (download != null && download.getFilm() != null) {
                final String fNo = download.getFilm().getNo() == P2LibConst.NUMBER_NOT_STARTED ?
                        " " : "[" + download.getFilm().getNo() + "]";
                bandwidthData.setName(fNo);
            }
        }
    }

    private static synchronized void addTmpData(ChartData chartData) {
        //temp-data in BandwidthData eintragen
        for (final BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
            bandwidthData.addFromTmpData();
        }
    }

    public static void initChartSeries(XYChart.Series<Number, Number> chartSeries) {
        chartSeries.getData().clear();
        for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {
            chartSeries.getData().add(new XYChart.Data<>(i, 0));
        }
    }
}
