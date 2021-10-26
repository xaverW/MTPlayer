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
import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class ChartFactoryGenerateData {
//    private static int runChart = 0;
//    private static long[] bandwidthSumArr = new long[ChartFactory.MAX_CHART_DATA_PER_SCREEN];

    private ChartFactoryGenerateData() {
    }

    public static synchronized void setChartDataShowing(ChartData chartData) {
        //was ist noch sichtbar
        final boolean chartOnlyRunning = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBool();
        final boolean chartOnlyExisting = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBool();

        for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
            if (bandwidthData.allValuesEmpty()) {
                //hat dann keine sichtbaren Daten mehr
                bandwidthData.setShowing(false);
                continue;
            }

            boolean downExist = bandwidthData.getDownload() != null;
            boolean downRunning = bandwidthData.getDownload() != null && bandwidthData.getDownload().isStateStartedRun();
            if (chartOnlyRunning && !downRunning) {
                //dann gibts den Download nicht mehr und soll auch nicht angezeigt werden
                bandwidthData.setShowing(false);
                continue;

            } else if (chartOnlyExisting && !downExist) {
                //sollen nur laufende angezeigt werden
                bandwidthData.setShowing(false);
                continue;
            }

            bandwidthData.setShowing(true);
        }
    }

    public static synchronized void generateYScale(LineChart<Number, Number> lineChart, ChartData chartData) {
        double max = 0;
        int scale = 1;

        for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
            if (!bandwidthData.isShowing()) {
                continue;
            }

            long m = bandwidthData.getMaxValue();
            if (m > max) {
                max = m;
            }
        }

        while (max > 25_000) {
            max /= 1_000;
            scale *= 1_000;
        }
        chartData.setyScale(scale);
        setYAxisLabel(lineChart, scale);
//        System.out.println("Scale: " + max + " - " + scale);
    }

    private static void setYAxisLabel(LineChart<Number, Number> lineChart, int scale) {
        switch (scale) {
            case 1:
                lineChart.getYAxis().setLabel("Bandbreite [B/s]");
                break;
            case 1_000:
                lineChart.getYAxis().setLabel("Bandbreite [kB/s]");
                break;
            case 1_000_000:
                lineChart.getYAxis().setLabel("Bandbreite [MB/s]");
                break;
        }
    }

    public static void genChartSeries(ChartData chartData) {
        //Anzahl der XYChart.Series<Number, Number> wird angelegt
        int countChartSeries = 0;

        for (int bi = 0; bi < chartData.getBandwidthDataList().size(); ++bi) {
            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(bi);
            if (bandwidthData.isShowing()) {
                ++countChartSeries;
            }
        }

        int sumChartSeries = chartData.getChartSeriesList_SeparateCharts().size();
        if (sumChartSeries > countChartSeries) {
            //zu viele
            chartData.getChartSeriesList_SeparateCharts().remove(0, sumChartSeries - countChartSeries);
        }

        while (sumChartSeries < countChartSeries) {
            // zu wenige
            final XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>("", FXCollections.observableArrayList());
            ChartFactory.initChartSeries(chartSeries);
            chartData.getChartSeriesList_SeparateCharts().add(chartSeries);
            ++sumChartSeries;
        }
    }

    public static synchronized void zoomXAxis(LineChart<Number, Number> lineChart, ChartData chartData) {
        final double secondsPerPixel = chartData.getSecondsPerPixel();

        final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        double lower, upper;
        final double MIN = chartData.getCountProgRunningTimeMinutes() - chartData.getDownloadChartMaxTimeMinutes();
        if (MIN <= 0) {
            lower = 0;
        } else {
            lower = MIN;
        }
//        upper = Math.ceil(chartData.getCountMinutes());
        upper = chartData.getCountProgRunningTimeMinutes();

//        xAxis.setUpperBound(upper);
//        xAxis.setLowerBound(lower);

        double res = 1.0 * secondsPerPixel / 60.0;
        if (xAxis.getUpperBound() < upper || xAxis.getUpperBound() > upper + res) {
            //nur wenn zu klein oder viel zu groß!
            xAxis.setUpperBound(upper + res);
        }

        res = 0.5 * secondsPerPixel / 60.0;
        if (xAxis.getLowerBound() < lower + res || xAxis.getLowerBound() > lower) {
            //nur wenn viel zu klein oder zu groß!
            xAxis.setLowerBound(lower - res < 0 ? 0 : lower - res);
        }
    }
}
