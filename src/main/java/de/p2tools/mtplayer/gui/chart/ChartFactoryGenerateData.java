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

        xAxis.setUpperBound(upper);
        xAxis.setLowerBound(lower);

//        double res = 2.0 * secondsPerPixel / 60.0;
//        if (xAxis.getUpperBound() < upper || xAxis.getUpperBound() > upper + res) {
//            //nur wenn zu klein oder viel zu groß!
//            xAxis.setUpperBound(upper + res);
//        }

//        res = 1.0 * secondsPerPixel / 60.0;
//        if (xAxis.getLowerBound() < lower + res || xAxis.getLowerBound() > lower) {
//            //nur wenn viel zu klein oder zu groß!
//            xAxis.setLowerBound(lower - res < 0 ? 0 : lower - res);
//        }
    }


//    public static synchronized void zoomYAxis(LineChart<Number, Number> lineChart, ChartData chartData) {
//        double max = 0;
//        int scale = 1;
//        final ObservableList<XYChart.Series<Number, Number>> list;
//
//        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
//            list = chartData.getChartSeriesList_SeparateCharts();
//        } else {
//            list = chartData.getChartSeriesList_OneSumChart();
//        }
//
//        for (final XYChart.Series<Number, Number> cSeries : list) {
//            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
//                if (date.getYValue().longValue() > max) {
//                    max = date.getYValue().longValue();
//                }
//            }
//        }
//
//        chartData.setScale(1);
//        while (max > 25_000) {
//            max /= 1_000;
//            scale *= 1_000;
//            chartData.setScale(chartData.getScale() * 1000);
//        }
//
//        double absMax;
//        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
//            absMax = getMaxYChartData(chartData);
//        } else {
//            absMax = getMaxYChartDataAll(chartData);
//        }
//
//        absMax /= scale;
//        if (absMax > max) {
//            System.out.println("========> absMax<, absMax: " + absMax + "  max: " + max);
//            max = absMax;
//        }
//
//        int lowDiv = max > 250 ? 100 : 50;
//        long upper = Math.round(max / lowDiv);
//        upper *= lowDiv;
//        upper = upper < max ? upper + lowDiv : upper;
//        upper = upper <= 0 ? lowDiv : upper;
//        int unit = Math.round(upper / 5);
//
//        if (upper < max) {
//            System.out.println("========> upper<, max: " + max + "  upper: " + upper + "  unit: " + unit);
//        }
//
//        PLog.sysLog("absMax: " + absMax + "  max: " + max + "  upper: " + upper + "  unit: " + unit);
//
//        NumberAxis axis = (NumberAxis) lineChart.getYAxis();
//        axis.setUpperBound(upper);
//        axis.setTickUnit(unit);
////        setYAxisLabel(lineChart, chartData);
//
//        for (final XYChart.Series<Number, Number> cSeries : list) {
//            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
//                date.setYValue(date.getYValue().longValue() / chartData.getScale());
//            }
//        }
//    }

//    private static synchronized long getMaxYChartData(ChartData chartData) {
//        // und jetzt die sichtbaren Daten eintragen
//        final boolean chartOnlyRunning = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBool();
//        final boolean chartOnlyExisting = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBool();
//        final int bandwidthFirstIdx = getFirstBandwidthIdx(chartData);
//
//        long maxValue = 0;
//        for (int i = 0; i < chartData.getBandwidthDataList().size(); ++i) {
//            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(i);
//            if (!bandwidthData.isShowing()) {
//                continue;
//            }
//
//            boolean downL = bandwidthData.getDownload() != null;
//            boolean downLRunning = bandwidthData.getDownload() != null && bandwidthData.getDownload().isStateStartedRun();
//            if (chartOnlyRunning && !downLRunning) {
//                //dann gibts den Download nicht mehr und soll auch nicht angezeigt werden
//                continue;
//            } else if (chartOnlyExisting && !downL) {
//                //sollen nur laufende angezeigt werden
//                continue;
//            }
//
//            long actValue = getMax(bandwidthData, bandwidthFirstIdx);
//            maxValue = actValue > maxValue ? actValue : maxValue;
//        }
//        return maxValue;
//    }

//    private static synchronized long getMaxYChartDataAll(ChartData chartData) {
//        // und jetzt die sichtbaren Daten eintragen
//        long maxValue=0;
//        for (int i = 0; i < chartData.getBandwidthDataList().size(); ++i) {
//            //zum MaxWert addieren
//            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(i);
//            maxValue += bandwidthData.getMaxValue(chartData);
//        }
//
//        return maxValue;
//    }

//    private static int getFirstBandwidthIdx(ChartData chartData) {
//        //0 .... xx[s], Zeit ältester Wert
//        final double timeIdx = (ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1) * chartData.getTimePerTick();
//        //0 ... xx, 0=letzter Wert in der Bandwidthliste, "älterster" idx in der BandwidthListe
//        return (int) Math.round(timeIdx / ChartFactory.DATA_ALL_SECONDS);
//    }

//    private static long getMax(BandwidthData bandwidthData, int bandwidthTimeIdx) {
//        long maxBand = 0;
//        int bandwidthIdx = bandwidthData.size() - 1 - bandwidthTimeIdx;
//        for (int i = bandwidthIdx; i < bandwidthData.size(); ++i) {
//            if (i >= 0 && i < bandwidthData.size()) {
//                long actVal = bandwidthData.get(i);
//                if (actVal > maxBand) {
//                    maxBand = actVal;
//                }
//            }
//        }
//        return maxBand;
//    }
}
