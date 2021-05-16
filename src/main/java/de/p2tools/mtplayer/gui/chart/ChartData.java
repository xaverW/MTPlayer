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
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class ChartData {
    private IntegerProperty downloadChartMaxTimeMinutes = ProgConfig.DOWNLOAD_CHART_MAX_TIME_MIN.getIntegerProperty(); // MAX Minuten todo

    private int countSek = 0;
    private double countMin = 0;
    private int scale = 1;

    // ist die eine LineChart für den Gesamtwert
    private final XYChart.Series<Number, Number> chartSeriesSum =
            new XYChart.Series<>("Summe", FXCollections.observableArrayList());

    // Liste der LineCharts für Gesamt -> hat nur eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesList_OneSumChart = FXCollections.observableArrayList(chartSeriesSum);
    // Liste der LineCharts für einzele Downloads -> für jeden Download eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesList_SeparateCharts = FXCollections.observableArrayList();

    // ist die Liste mit ALLEN BandwidthData (auch denen die nicht angezeigt werden sollen)
    private final ObservableList<BandwidthData> bandwidthDataList = FXCollections.observableArrayList();

    public ChartData() {
        ChartFactory.initChartSeries(chartSeriesSum);
    }

    public int getDownloadChartMaxTimeMinutes() {
        return downloadChartMaxTimeMinutes.get();
    }

    public IntegerProperty downloadChartMaxTimeMinutesProperty() {
        return downloadChartMaxTimeMinutes;
    }

    public void setDownloadChartMaxTimeMinutes(int downloadChartMaxTimeMinutes) {
        this.downloadChartMaxTimeMinutes.set(downloadChartMaxTimeMinutes);
    }

    public int getCountSek() {
        //Summe aller "Sekunden" die das Programm läuft
        return countSek;
    }

    public void addCountSek() {
        this.countSek += 1;
        countMin = countSek / 60.0; // Minuten
    }

    public double getCountMin() {
        return countMin;
    }

    public int getTimeShowingSeconds() {
        //Zeit die angezeigt werden soll oder Programmlaufzeit wenn weniger
        int displayMinTimeShowing_sec = getCountSek() - getDownloadChartMaxTimeMinutes() * 60;
        if (displayMinTimeShowing_sec < 0) {
            displayMinTimeShowing_sec = 0;
        }
        return displayMinTimeShowing_sec;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public XYChart.Series<Number, Number> getChartSeriesSum() {
        return chartSeriesSum;
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

    public void genInfos() {
        ArrayList<String> info = new ArrayList<>();
        info.add(" ");
        info.add("======================");
        info.add(" Downloadchart");
        info.add("----------------------");
        final int max = ChartFactory.MAX_MINUTES_SHOWING * 60;
        info.add("--> max: " + max);

        info.add("chartSeriesListSum: " + chartSeriesList_OneSumChart.size());
        for (final XYChart.Series<Number, Number> cSeries : chartSeriesList_OneSumChart) {
            info.add("    " + cSeries.getName() + ": " + cSeries.getData().size());
        }

        info.add("chartSeriesListSeparate: " + chartSeriesList_SeparateCharts.size());
        for (final XYChart.Series<Number, Number> cSeries : chartSeriesList_SeparateCharts) {
            info.add("    " + cSeries.getName() + ": " + cSeries.getData().size());
        }

        info.add("======================");
        PLog.sysLog(info.toArray(new String[info.size()]));
    }
}
