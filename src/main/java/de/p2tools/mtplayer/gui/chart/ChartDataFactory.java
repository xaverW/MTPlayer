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
import de.p2tools.p2lib.p2event.P2EventHandler;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class ChartDataFactory {
    private ChartDataFactory() {
    }

    public static synchronized void runChart(LineChart<Number, Number> lineChart) {
        // startet jede Sekunde, wenn angezeigt
        BandwidthDataFactory.setsBandwidthDataShowingOrNot(ProgData.getInstance().chartData);
        ChartFactory.generateYScale(lineChart, ProgData.getInstance().chartData);

        ChartGenerateFactory.generateChartData(lineChart, ProgData.getInstance().chartData);
        ChartFactory.zoomXAxis(lineChart, ProgData.getInstance().chartData);
    }

    public static void initChartSeries(XYChart.Series<Number, Number> chartSeries) {
        chartSeries.getData().clear();
        for (int i = 0; i < BandwidthDataFactory.CHART_SUM_PIXEL; ++i) {
            chartSeries.getData().add(new XYChart.Data<>(i, 0));
        }
    }

    public static void genActShowingTimeValues(ChartData chartData) {
        //Zeitbereich der angezeigt werden soll
        int oldest = 0;
        for (int bi = 0; bi < chartData.getBandwidthDataList().size(); ++bi) {
            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(bi);
            if (bandwidthData.isEmpty()) {
                //alte oder noch leere
                continue;
            }
            if (oldest > bandwidthData.getStartTimeSec()) {
                oldest = (int) bandwidthData.getStartTimeSec();
            }
        }

        final int maxTimeToShowSeconds;
        long timeToShowSeconds = P2EventHandler.countRunningTimeSeconds - oldest;
        final int maxTimeSelectedSeconds = ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN.get() * 60;

        if (maxTimeSelectedSeconds < timeToShowSeconds) {
            // dann auf den vorgegebenen Wert begrenzen
            maxTimeToShowSeconds = maxTimeSelectedSeconds;

        } else {
            // dann wird der max. vorhandene Wert angezeigt
            if (timeToShowSeconds < 60) {
                // 1 min.
                maxTimeToShowSeconds = 60;
            } else if (timeToShowSeconds < 2 * 60) {
                // 2 min.
                maxTimeToShowSeconds = 2 * 60;
            } else if (timeToShowSeconds < 10 * 60) {
                // 10 min.
                maxTimeToShowSeconds = 10 * 60;
            } else if (timeToShowSeconds < 30 * 60) {
                // 30 min.
                maxTimeToShowSeconds = 30 * 60;
            } else if (timeToShowSeconds < 60 * 60) {
                maxTimeToShowSeconds = 60 * 60;
            } else if (timeToShowSeconds < 100 * 60) {
                maxTimeToShowSeconds = 100 * 60;
            } else if (timeToShowSeconds < 120 * 60) {
                maxTimeToShowSeconds = 120 * 60;
            } else if (timeToShowSeconds < 150 * 60) {
                maxTimeToShowSeconds = 150 * 60;
            } else if (timeToShowSeconds < 200 * 60) {
                maxTimeToShowSeconds = 200 * 60;
            } else {
                maxTimeToShowSeconds = BandwidthDataFactory.MAX_SECONDS_SHOWING;
            }
        }
        BandwidthDataFactory.SHOW_MINUTES.setValue(timeToShowSeconds >= 2 * 60);

        //Anzahl der Sekunden/Pixel
        chartData.setSecondsPerPixel(1.0 * maxTimeToShowSeconds / BandwidthDataFactory.CHART_SUM_PIXEL);
        // System.out.println("sPerPixel: " + chartData.getSecondsPerPixel());

        //Anzahl der Daten/Pixel
        chartData.setDataPerPixel(chartData.getSecondsPerPixel() / BandwidthDataFactory.DATA_ALL_SECONDS);
        // System.out.println("dPerPixel: " + chartData.getDataPerPixel());
    }
}
