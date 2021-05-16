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
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class ChartFactoryGenerateData {
    private static int runChart = 0;
    private static long[] bandwidthSumArr = new long[ChartFactory.MAX_CHART_DATA_PER_SCREEN];

    private ChartFactoryGenerateData() {
    }

    public static void setYAxisLabel(LineChart<Number, Number> lineChart, ChartData chartData) {
        switch (chartData.getScale()) {
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

    public static synchronized void generateChartData(LineChart<Number, Number> lineChart, ChartData chartData) {
        // und jetzt die sichtbaren Daten eintragen
        final boolean separatBool = ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool();
        if (separatBool) {
            ChartFactoryGenerateChartData.generateChartDataSeparated(lineChart, chartData);
        } else {
            ChartFactoryGenerateChartData.generateChartDataAll(lineChart, chartData);
        }
    }

    public static synchronized void zoomXAxis(LineChart<Number, Number> lineChart, ChartData chartData) {
        final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setUpperBound(chartData.getCountMin());
        final double MIN = chartData.getCountMin() - chartData.getDownloadChartMaxTimeMinutes();
        if (MIN <= 0) {
            xAxis.setLowerBound(0);
            return;
        }
        xAxis.setLowerBound(MIN);
    }

    public static synchronized void zoomYAxis(LineChart<Number, Number> lineChart, ChartData chartData) {
        double max = 0;
        int scale = 1;
        final ObservableList<XYChart.Series<Number, Number>> list;

        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            list = chartData.getChartSeriesList_SeparateCharts();
        } else {
            list = chartData.getChartSeriesList_OneSumChart();
        }

        for (final XYChart.Series<Number, Number> cSeries : list) {
            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                if (date.getYValue().longValue() > max) {
                    max = date.getYValue().longValue();
                }
            }
        }

        while (max > 25_000) {
            max /= 1_000;
            scale *= 1_000;
            chartData.setScale(chartData.getScale() * 1000);
        }

        double absMax = getMaxYChartData(chartData);
        absMax /= scale;
        if (absMax > max) {
            PLog.sysLog("=====> absMax: " + absMax + "  max: " + max);
            max = absMax;
        }

        int lowDiv = max > 250 ? 100 : 50;
        long upper = Math.round(max / lowDiv);
        upper *= lowDiv;
        upper = upper < max ? upper + lowDiv : upper;
        upper = upper <= 0 ? lowDiv : upper;
        int unit = Math.round(upper / 5);

        if (upper < max) {
            PLog.sysLog("=====> max: " + max + "  upper: " + upper + "  unit: " + unit);
        }
        PLog.sysLog("absMax: " + absMax + "  max: " + max + "  upper: " + upper + "  unit: " + unit);

        NumberAxis axis = (NumberAxis) lineChart.getYAxis();
        axis.setUpperBound(upper);
        axis.setTickUnit(unit);
        setYAxisLabel(lineChart, chartData);

        for (final XYChart.Series<Number, Number> cSeries : list) {
            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                date.setYValue(date.getYValue().longValue() / chartData.getScale());
            }
        }
    }

    private static synchronized long getMaxYChartData(ChartData chartData) {
        // und jetzt die sichtbaren Daten eintragen
        final boolean chartOnlyRunning = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBool();
        final boolean chartOnlyExisting = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBool();

        long maxValue = 0;
        for (int bi = 0; bi < chartData.getBandwidthDataList().size(); ++bi) {
            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(bi);
            if (!bandwidthData.isShowing()) {
                continue;
            }

            boolean downL = bandwidthData.getDownload() != null;
            boolean downLRunning = bandwidthData.getDownload() != null && bandwidthData.getDownload().isStateStartedRun();
            if (chartOnlyRunning && !downLRunning) {
                //dann gibts den Download nicht mehr und soll auch nicht angezeigt werden
                continue;
            } else if (chartOnlyExisting && !downL) {
                //sollen nur laufende angezeigt werden
                continue;
            }

            final double aktTime_sec = (chartData.getCountSek());
            int bandwidthIdx = (int) Math.round((aktTime_sec - bandwidthData.getStartTimeSec()) / ChartFactory.DATA_ALL_SECONDS);
            if (bandwidthIdx < 0) {
                bandwidthIdx = 0;
            }

            for (int i = bandwidthIdx; i < bandwidthData.size(); ++i) {
                long actVal = bandwidthData.get(i);
                if (actVal > maxValue) {
                    maxValue = actVal;
                }
            }
        }
        return maxValue;
    }
}
