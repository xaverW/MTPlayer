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

        // mind. Anzeigedauer auf geraden Wert legen
        int maxTimeSecondsAct;
        long timeToShow = ProgData.countRunningTimeSeconds - oldest;

        timeToShow = Math.min(timeToShow, chartData.getMaxTimeSeconds());
        BandwidthDataFactory.SHOW_MINUTES.setValue(timeToShow >= 2 * 60);

        if (timeToShow < 60) {
            // 1 min.
            maxTimeSecondsAct = 60;
        } else if (timeToShow < 2 * 60) {
            // 2 min.
            maxTimeSecondsAct = 2 * 60;
        } else if (timeToShow < 10 * 60) {
            // 10 min.
            maxTimeSecondsAct = 10 * 60;
        } else if (timeToShow < 30 * 60) {
            maxTimeSecondsAct = 30 * 60;
        } else if (timeToShow < 60 * 60) {
            maxTimeSecondsAct = 60 * 60;
        } else if (timeToShow < 100 * 60) {
            maxTimeSecondsAct = 100 * 60;
        } else if (timeToShow < 120 * 60) {
            maxTimeSecondsAct = 120 * 60;
        } else if (timeToShow < 150 * 60) {
            maxTimeSecondsAct = 150 * 60;
        } else if (timeToShow < 200 * 60) {
            maxTimeSecondsAct = 200 * 60;
        } else {
            maxTimeSecondsAct = BandwidthDataFactory.MAX_SECONDS_SHOWING;
        }

        //Anzahl der Sekunden/Pixel
        chartData.setSecondsPerPixel(1.0 * maxTimeSecondsAct / BandwidthDataFactory.CHART_SUM_PIXEL);
//        System.out.println("sPerPixel: " + chartData.getSecondsPerPixel());

        //Anzahl der Daten/Pixel
        chartData.setDataPerPixel(chartData.getSecondsPerPixel() / BandwidthDataFactory.DATA_ALL_SECONDS);
//        System.out.println("dPerPixel: " + chartData.getDataPerPixel());
    }
}
