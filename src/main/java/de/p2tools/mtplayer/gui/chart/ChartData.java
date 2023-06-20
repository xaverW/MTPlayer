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
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class ChartData {
    //    private int countProgRunningTimeSeconds = 0; // Gesamtzeit die das Chart/Programm läuft
    private int yScale = 1;
    private int maxTimeSeconds = ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN.get() * 60;
    private double dataPerPixel = 1;
    private double secondsPerPixel = 1;

    // ist die eine LineChart für den Gesamtwert
    private final XYChart.Series<Number, Number> chartSeriesOneSumChart = new XYChart.Series<>("Summe", FXCollections.observableArrayList());

    // Liste der LineCharts für Gesamt -> hat nur eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesList_OneSumChart = FXCollections.observableArrayList(chartSeriesOneSumChart);

    // Liste der LineCharts für einzelne Downloads -> für jeden Download eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesList_SeparateCharts = FXCollections.observableArrayList();

    // ist die Liste mit ALLEN BandwidthData (auch denen die nicht angezeigt werden sollen)
    private final ObservableList<BandwidthData> bandwidthDataList = FXCollections.observableArrayList();

    public ChartData() {
        ChartDataFactory.initChartSeries(chartSeriesOneSumChart);
        ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN.addListener((ob, ol, ne) -> {
            //die muss dann neu gesetzt werden!!
            maxTimeSeconds = ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN.get() * 60;
            ChartDataFactory.genActShowingTimeValues(this);
        });
    }

    public int getyScale() {
        return yScale;
    }

    public void setyScale(int yScale) {
        this.yScale = yScale;
    }

    public int getMaxTimeSeconds() {
        return maxTimeSeconds;
    }

    public void setMaxTimeSeconds(int maxTimeSeconds) {
        this.maxTimeSeconds = maxTimeSeconds;
    }

    public double getDataPerPixel() {
        return dataPerPixel;
    }

    public void setDataPerPixel(double dataPerPixel) {
        this.dataPerPixel = dataPerPixel;
    }

    public double getSecondsPerPixel() {
        return secondsPerPixel;
    }

    public void setSecondsPerPixel(double secondsPerPixel) {
        this.secondsPerPixel = secondsPerPixel;
    }

    public XYChart.Series<Number, Number> getChartSeriesOneSumChart() {
        return chartSeriesOneSumChart;
    }

    public ObservableList<XYChart.Series<Number, Number>> getChartSeriesList_OneSumChart() {
        return chartSeriesList_OneSumChart;
    }

    public ObservableList<XYChart.Series<Number, Number>> getChartSeriesList_SeparateCharts() {
        return chartSeriesList_SeparateCharts;
    }

    public ObservableList<BandwidthData> getBandwidthDataList() {
        return bandwidthDataList;
    }
}
