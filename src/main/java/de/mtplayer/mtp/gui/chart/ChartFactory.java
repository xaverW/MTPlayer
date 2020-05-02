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


package de.mtplayer.mtp.gui.chart;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;

import java.util.Iterator;

public class ChartFactory {
    private ChartFactory() {
    }

    public static NumberAxis createXAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0.0);
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
        return yAxis;
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

    public static synchronized void zoomXAxis(LineChart<Number, Number> lineChart, ChartData chartData, double countMin) {
        final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setUpperBound(countMin);
        final double MIN = countMin - chartData.getMaxTime();
        if (MIN <= 0) {
            return;
        }
        xAxis.setLowerBound(MIN);

        chartData.getLineChartsSeparate().stream().forEach(cs -> {
            if (cs.getData().isEmpty()) {
                return;
            }

            cs.getData().removeIf(d -> d.getXValue().doubleValue() < MIN);
        });

        while (!chartData.getChartSeriesSum().getData().isEmpty() && chartData.getChartSeriesSum().getData().get(0).getXValue().doubleValue() < MIN) {
            chartData.getChartSeriesSum().getData().remove(0);
        }
    }

    public static synchronized void zoomYAxis(LineChart<Number, Number> lineChart,
                                              ChartData chartData) {
        double max = 0;
        final ObservableList<XYChart.Series<Number, Number>> list;

        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            list = chartData.getLineChartsSeparate();
        } else {
            list = chartData.getLineChartsSum();
        }

        for (final XYChart.Series<Number, Number> cSeries : list) {
            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                if ((long) date.getYValue() > max) {
                    max = (long) date.getYValue();
                }
            }
        }

        if (max > 5_000) {
            chartData.setScale(chartData.getScale() * 1000);
            setYAxisLabel(lineChart, chartData);

            for (final XYChart.Series<Number, Number> cSeries : chartData.getLineChartsSeparate()) {
                for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                    date.setYValue((long) date.getYValue() / 1_000);
                }
            }
            for (final XYChart.Series<Number, Number> cSeries : chartData.getLineChartsSum()) {
                for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                    date.setYValue((long) date.getYValue() / 1_000);
                }
            }
        }
    }

    public static synchronized void cleanUpChart(ProgData progData, ChartData chartData) {
        // charts die keinen Download mehr haben, löschen
        final boolean all = ProgConfig.DOWNLOAD_CHART_ALL_DOWNLOADS.getBool();
        final boolean onlyExisting = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBool();
//        final boolean onlyRunning = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBool();

        if (all) {
            // dann sollen alle Downloads angezeigt werden
            return;
        }

        Iterator<XYChart.Series<Number, Number>> it = chartData.getLineChartsSeparate().listIterator();
        while (it.hasNext()) {

            XYChart.Series<Number, Number> cSeries = it.next();
            boolean foundDownload = false;
            for (final Download download : progData.downloadList) {

                if (download.getCSeries() != null && download.getCSeries().equals(cSeries)) {
                    if (onlyExisting) {
                        // dann werden alle vorhandenen Downloads angezeigt
                        foundDownload = true;
                    } else if (download.isStateStartedRun()) {
                        // nur die laufenden anzeigen
                        foundDownload = true;
                    }
                    break;
                }

            }

            if (!foundDownload) {
                // dann gibts den Download nicht mehr
                it.remove();
            }

        }
    }
}
