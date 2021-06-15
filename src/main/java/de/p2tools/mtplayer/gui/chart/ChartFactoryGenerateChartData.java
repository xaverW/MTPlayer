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
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.Set;

public class ChartFactoryGenerateChartData {

    private ChartFactoryGenerateChartData() {
    }

    public static synchronized void generateChartData(LineChart<Number, Number> lineChart, ChartData chartData) {
        // und jetzt die sichtbaren Daten eintragen
        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            //für die Einzel-Charts
            generateChartDataSeparated(lineChart, chartData);

        } else {
            //für das "Gesamt-Chart"
            generateChartDataAll(lineChart, chartData);
        }
    }

    public static synchronized void generateChartDataSeparated(LineChart<Number, Number> lineChart, ChartData chartData) {
        int countChart = 0;
        for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
            if (!bandwidthData.isShowing() || bandwidthData.isEmpty()) {
                //hat dann keine sichtbaren Daten mehr
                continue;
            }

            final XYChart.Series<Number, Number> chartSeries = chartData.getChartSeriesList_SeparateCharts().get(countChart++);
            bandwidthData.fillData(chartSeries, false);
        }

        colorChartName(lineChart, chartData);
    }

//    public static synchronized void generateChartDataSeparated_(LineChart<Number, Number> lineChart, ChartData chartData) {
//        final int amountDataPerPixel = chartData.getDataPerPixel(); //nur vom Slider"Zeit" abhängig
//        final int actTimeSec = chartData.getCountProgRunningTimeSeconds(); //jetzt[sec], damit es während des gesamten Durchlaufs gleich ist!
//        final int maxTimeSec = chartData.getMaxTimeSeconds();
//
//        int countChart = 0;
//
////        int lastIdx = chartData.getLastIdx();
//        final double timePerTick_sec = chartData.getSecondsPerPixel();
//
//        for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
//            if (!bandwidthData.isShowing() || bandwidthData.isEmpty()) {
//                //hat dann keine sichtbaren Daten mehr
//                continue;
//            }
//
//            final XYChart.Series<Number, Number> cSeries = chartData.getChartSeriesList_SeparateCharts().get(countChart++);
//            cSeries.setName(bandwidthData.getName());
////            System.out.println("========");
//
//            for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {
//                //ChartFactory.MAX_CHART_DATA_PER_SCREEN-1 (aktuellster Wert) ... 0
//
//                final int indexChart = ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 - i;//ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 ... 0
//                final double actTimeMin = (actTimeSec - timePerTick_sec * i) / 60.0;//jetzt[min] ... vor[min]
//                cSeries.getData().get(indexChart).setXValue(actTimeMin);
//
//                if (i * timePerTick_sec > maxTimeSec) {
//                    cSeries.getData().get(indexChart).setYValue(0);
//                    cSeries.getData().get(indexChart).setXValue(0);
//                    continue;
//                }
//
//                if (actTimeMin < 0) {
//                    cSeries.getData().get(indexChart).setYValue(0);
//                    cSeries.getData().get(indexChart).setXValue(0);
//                    continue;
//                }
//
//                int bandwidthIdx = bandwidthData.size() - 1 - i * amountDataPerPixel;
//                if (bandwidthIdx < 0) {
//                    cSeries.getData().get(indexChart).setYValue(0);
//                    continue;
//                }
//
//                long value = 0;
//                int count = 0;
//
//                if (bandwidthIdx < bandwidthData.size()) {
//                    ++count;
//                    value += bandwidthData.get(bandwidthIdx);
//                }
//
//                for (int iii = 1; iii < amountDataPerPixel; ++iii) {
//                    final int idx = bandwidthIdx - iii;
//                    if (idx < 0 || idx >= bandwidthData.size()) {
//                        continue;
//                    }
//
//                    ++count;
//                    long b = bandwidthData.get(idx);
//                    System.out.println("===> bandwidthIdx: " + bandwidthIdx + " bandwidthData idx: " + idx + " - " + b / chartData.getyScale());
//                    value += b;
//                }
//
//                if (count > 0) {
//                    value = value / count / chartData.getyScale();
//                }
//
//                System.out.println("->ii: " + i + " - bandwidthIdx: " + bandwidthIdx + /*" lastIdx: " + lastIdx +*/ " amountDataPerPixel: " + amountDataPerPixel + " value: " + value + " count: " + count + " scale: " + chartData.getyScale() + "  -  actTime: " + actTimeMin);
//                cSeries.getData().get(indexChart).setYValue(value);
//            }
//        }
//
//        colorChartName(lineChart, chartData);
//    }

    public static synchronized void generateChartDataAll(LineChart<Number, Number> lineChart, ChartData chartData) {
        final double secondsPerPixel = chartData.getSecondsPerPixel();
        int bandwidthLastIdx = 0;//letzter Wert: Suchen von bandwidthIdx+1 bis bandwidthLastIdx

//        System.out.println("============================");
        for (int i = 0; i < ChartFactory.MAX_CHART_DATA_PER_SCREEN; ++i) {
            //0 ..... ChartFactory.MAX_CHART_DATA_PER_SCREEN-1

            final int indexChart = ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 - i;//ChartFactory.MAX_CHART_DATA_PER_SCREEN - 1 ... 0
            final int bandwidthTimeIdx = (int) Math.round(i * secondsPerPixel / ChartFactory.DATA_ALL_SECONDS);//0 ... xx, 0=letzter Wert in der Bandwidthliste
            final double actTimeMin = (chartData.getCountProgRunningTimeSeconds() - secondsPerPixel * i) / 60.0;//jetzt[min] ... vor[min]

            if (actTimeMin < 0) {
                chartData.getChartSeriesOneSumChart().getData().get(indexChart).setYValue(0);
                chartData.getChartSeriesOneSumChart().getData().get(indexChart).setXValue(0);
                continue;
            }

            double actVal = 0;
            for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
                if (bandwidthData.isEmpty()) {
                    continue;
                }

                int count = 0;
                int act = 0;
                int bandwidthIdx = bandwidthData.size() - 1 - bandwidthTimeIdx;
                if (bandwidthIdx >= 0 && bandwidthIdx < bandwidthData.size()) {
                    ++count;
                    act += bandwidthData.get(bandwidthIdx);

                    for (int ii = bandwidthIdx + 1; ii < bandwidthLastIdx; ++ii) {
                        if (ii >= 0 && ii < bandwidthData.size()) {
                            ++count;
//                            actTimeMin += 1 / ChartFactory.DATA_ALL_SECONDS;
                            act += bandwidthData.get(ii);
                        }
                    }
                }
                bandwidthLastIdx = bandwidthIdx;
                if (count > 1) {
                    act /= count;
                }
                actVal += act;
            }

            chartData.getChartSeriesOneSumChart().getData().get(indexChart).setXValue(actTimeMin);
            chartData.getChartSeriesOneSumChart().getData().get(indexChart).setYValue(actVal / chartData.getyScale());
//            if (actTimeMin >= 0) {
//                System.out.println("   actTimeMin: " + (((int) (actTimeMin * 100)) / 100.0) + " actValue: " + actVal + " secondsPerPixel: " + secondsPerPixel);
//            }
        }
        colorChartName(lineChart, chartData);
    }

    private static synchronized void colorChartName(LineChart<Number, Number> lineChart, ChartData chartData) {
        // chart einfärben
        for (int bi = 0; bi < chartData.getBandwidthDataList().size(); ++bi) {
            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(bi);
            setColor(lineChart, bandwidthData);
        }
    }

    private static synchronized void setColor(LineChart<Number, Number> lineChart, BandwidthData bandwidthData) {
        final String cRed = ProgConfig.SYSTEM_DARK_THEME.getBooleanProperty().get() ? "#ff0000" : "#de0000";
        final String cGreen = ProgConfig.SYSTEM_DARK_THEME.getBooleanProperty().get() ? "#00ff00" : "#00aa00";
        Set<Node> items = lineChart.lookupAll("Label.chart-legend-item");
        for (Node item : items) {
            Label label = (Label) item;
            if (label.getText().equals(bandwidthData.getName())) {
                switch (bandwidthData.getDownloadState()) {
                    case DownloadConstants.STATE_FINISHED:
                        label.setStyle(" -fx-text-fill: " + cGreen + ";");
                        break;
                    case DownloadConstants.STATE_ERROR:
                        label.setStyle(" -fx-text-fill: " + cRed + ";");
                        break;
                    default:
                        label.setStyle("");
                }
            }
        }
    }
}
