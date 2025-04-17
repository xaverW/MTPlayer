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

import de.p2tools.p2lib.p2event.P2EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;

public class ChartFactory {
    private ChartFactory() {
    }

    public static NumberAxis createXAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        setXAxisLabel(xAxis);
        BandwidthDataFactory.SHOW_MINUTES.addListener((u, o, n) -> setXAxisLabel(xAxis));
        xAxis.setLowerBound(0.0);
        xAxis.setSide(Side.RIGHT);
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() > 60) {
                    int i = (int) object.doubleValue();
                    return String.valueOf(i);
                } else {
                    int i = (int) (object.doubleValue() * 10);
                    return (String.valueOf(i / 10.0));
                }
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        return xAxis;
    }

    public static synchronized void zoomXAxis(LineChart<Number, Number> lineChart, ChartData chartData) {
        final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setUpperBound(P2EventHandler.countRunningTimeSeconds / (BandwidthDataFactory.SHOW_MINUTES.getValue() ? 60.0 : 1.0));

        final double secondsPerPixel = chartData.getSecondsPerPixel();
        final double MIN = P2EventHandler.countRunningTimeSeconds - BandwidthDataFactory.CHART_SUM_PIXEL * secondsPerPixel;
        final double lower = Math.max(MIN, 0);
        xAxis.setLowerBound(lower / (BandwidthDataFactory.SHOW_MINUTES.getValue() ? 60.0 : 1.0));
    }

    private static void setXAxisLabel(NumberAxis xAxis) {
        xAxis.setLabel("Programmlaufzeit " + (BandwidthDataFactory.SHOW_MINUTES.getValue() ? "[min]" : "[s]"));
    }

    public static NumberAxis createYAxis() {
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLowerBound(0.0);
        yAxis.setLabel("Wert");
        return yAxis;
    }

    public static synchronized void generateYScale(LineChart<Number, Number> lineChart, ChartData chartData) {
        int max = 0;
        int scale = 1;

        for (BandwidthData bandwidthData : chartData.getBandwidthDataList()) {
            if (!bandwidthData.isShowing()) {
                continue;
            }

            int m = bandwidthData.getMaxValue();
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
    }

    private static void setYAxisLabel(LineChart<Number, Number> lineChart, int scale) {
        switch (scale) {
            case 1 -> lineChart.getYAxis().setLabel("Bandbreite [kB/s]"); // kByte
            case 1_000 -> lineChart.getYAxis().setLabel("Bandbreite [MB/s]"); // MByte
            case 1_000_000 -> lineChart.getYAxis().setLabel("Bandbreite [GB/s]"); // GByte
        }
    }
}
