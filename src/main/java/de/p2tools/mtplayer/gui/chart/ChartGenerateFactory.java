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
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.Set;

public class ChartGenerateFactory {
    public final static int GEN_CHART_SHOW_ALL = 0;
    public final static int GEN_CHART_SHOW_DOWN = 1;
    public final static int GEN_CHART_SHOW_SUM = 2;
    public final static String CHART_LINE_NAME_SUM = "Summe";
    public final static int POS_LINE_SUM = 0;

    private ChartGenerateFactory() {
    }

    public static synchronized void generateChartData(LineChart<Number, Number> lineChart, ChartData chartData) {
        // und jetzt die sichtbaren Daten eintragen
        ChartDataFactory.genActShowingTimeValues(chartData);
        generateChartSeries(chartData);
        colorChartName(lineChart, chartData);
    }

    public static synchronized void generateChartSeries(ChartData chartData) {
        int countChartSeries = 0;
        if (ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.getValue() == GEN_CHART_SHOW_ALL ||
                ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.getValue() == GEN_CHART_SHOW_SUM) {
            // SUMME anzeigen
            if (chartData.getChartSeriesList().isEmpty()) {
                // dann erst mal die für ALLE einfügen
                final XYChart.Series<Number, Number> chartSeries =
                        new XYChart.Series<>(FXCollections.observableArrayList());
                ChartDataFactory.initChartSeries(chartSeries);
                chartData.getChartSeriesList().add(chartSeries);
            }
            fillSumDate(chartData);
            chartData.getChartSeriesList().get(POS_LINE_SUM).setName(CHART_LINE_NAME_SUM);
            Node line = chartData.getChartSeriesList().get(POS_LINE_SUM).getNode().lookup(".chart-series-line");
            if (line != null) {
                //set some styles
                line.setStyle("-fx-stroke-width: 2px; -fx-effect: null; -fx-stroke-dash-array: 10 5 2 5;");
            }
            ++countChartSeries;

        } else if (!chartData.getChartSeriesList().isEmpty()) {
            Node line = chartData.getChartSeriesList().get(POS_LINE_SUM).getNode().lookup(".chart-series-line");
            if (line != null) {
                //dann muss der Style gelöscht werden
                line.setStyle("");
            }
        }

        if (ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.getValue() == GEN_CHART_SHOW_ALL ||
                ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.getValue() == GEN_CHART_SHOW_DOWN) {
            // DOWNLOADS anzeigen
            for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
                if (!bandwidthData.isShowing()) {
                    // hat dann keine sichtbaren Daten mehr
                    continue;
                }

                if (chartData.getChartSeriesList().size() <= countChartSeries) {
                    // dann eine neue einfügen
                    final XYChart.Series<Number, Number> chartSeries =
                            new XYChart.Series<>("", FXCollections.observableArrayList());
                    ChartDataFactory.initChartSeries(chartSeries);
                    chartData.getChartSeriesList().add(chartSeries);
                }

                // und jetzt mit Daten füllen
                final XYChart.Series<Number, Number> chartSeries =
                        chartData.getChartSeriesList().get(countChartSeries);
                fillDownloadData(chartData, bandwidthData, chartSeries);
                ++countChartSeries;
            }
        }

        int sumChartSeries = chartData.getChartSeriesList().size();
        if (sumChartSeries > countChartSeries) {
            //zu viele
            chartData.getChartSeriesList().remove(countChartSeries, sumChartSeries);
        }
    }

    private static synchronized void fillSumDate(ChartData chartData) {
        final double secondsPerPixel = chartData.getSecondsPerPixel();
        final double dataPerPixel = chartData.getDataPerPixel();
        for (int chartPos = 0; chartPos < BandwidthDataFactory.CHART_SUM_PIXEL; ++chartPos) {
            //0 ..... ChartFactory.MAX_CHART_DATA_PER_SCREEN-1
            long yValue = 0;
            final double actTimeMin = getActTime(secondsPerPixel, chartPos);
            for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
                final long value = getValue(bandwidthData, chartPos, dataPerPixel);
                yValue += value;
            }
            setValues(chartData.getChartSeriesList().get(POS_LINE_SUM), chartPos, yValue, actTimeMin);
        }
    }

    private static void fillDownloadData(ChartData chartData, BandwidthData bandwidthData, XYChart.Series<Number, Number> chartSeries) {
        final double secondsPerPixel = chartData.getSecondsPerPixel(); //nur vom Slider und den max. vorhandenen Daten abhängig
        final double dataPerPixel = chartData.getDataPerPixel();
        chartSeries.setName(bandwidthData.getName());
        for (int chartPos = 0; chartPos < BandwidthDataFactory.CHART_SUM_PIXEL; ++chartPos) {
            // 0 == der aktuellste Wert und der steht am SCHLUSS!!
            final double actTimeMin = getActTime(secondsPerPixel, chartPos);
            final long yValue = getValue(bandwidthData, chartPos, dataPerPixel);
            setValues(chartSeries, chartPos, yValue, actTimeMin);
        }
    }

    private static double getActTime(double secondsPerPixel, int chartPos) {
        return (ProgData.countRunningTimeSeconds - secondsPerPixel * chartPos - BandwidthDataFactory.GET_DATA_COUNT) /
                (BandwidthDataFactory.SHOW_MINUTES.getValue() ? 60.0 : 1.0);
    }

    private static void setValues(final XYChart.Series<Number, Number> chartSeries, int chartPos, double yValue, double actTimeMin) {
        if (actTimeMin <= 0) {
            chartSeries.getData().get(chartPos).setYValue(0);
            chartSeries.getData().get(chartPos).setXValue(0);
            return;
        }

        chartSeries.getData().get(chartPos).setYValue(yValue / ProgData.getInstance().chartData.getyScale());
        chartSeries.getData().get(chartPos).setXValue(actTimeMin);
    }

    private static long getValue(BandwidthData bandwidthData, int chartPos, double dataPerPixel) {
        int dataPos = (int) Math.round(BandwidthDataFactory.MAX_DATA - chartPos * dataPerPixel);
        dataPos = Math.max(dataPos, 0);
        dataPos = Math.min(dataPos, BandwidthDataFactory.MAX_DATA - 1);
        return Math.round(bandwidthData.data[dataPos]);
    }

    private static synchronized void colorChartName(LineChart<Number, Number> lineChart, ChartData chartData) {
        // chart einfärben
        for (int bi = 0; bi < chartData.getBandwidthDataList().size(); ++bi) {
            BandwidthData bandwidthData = chartData.getBandwidthDataList().get(bi);
            setColor(lineChart, bandwidthData);
        }
    }

    private static synchronized void setColor(LineChart<Number, Number> lineChart, BandwidthData bandwidthData) {
        final String cRed = ProgConfig.SYSTEM_DARK_THEME.getValue() ? "#ff0000" : "#de0000";
        final String cGreen = ProgConfig.SYSTEM_DARK_THEME.getValue() ? "#00ff00" : "#00aa00";
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
