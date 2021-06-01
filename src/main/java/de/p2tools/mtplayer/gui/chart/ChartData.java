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
    private IntegerProperty downloadChartMaxTimeMinutes = ProgConfig.DOWNLOAD_CHART_SHOW_MAX_TIME_MIN.getIntegerProperty(); // MAX Minuten todo

    private int countSeconds = 0;
    private double countMinutes = 0;
    private int scale = 1;

    // ist die eine LineChart für den Gesamtwert
    private final XYChart.Series<Number, Number> chartSeriesOneSumChart = new XYChart.Series<>("Summe", FXCollections.observableArrayList());

    // Liste der LineCharts für Gesamt -> hat nur eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesList_OneSumChart = FXCollections.observableArrayList(chartSeriesOneSumChart);

    // Liste der LineCharts für einzele Downloads -> für jeden Download eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesList_SeparateCharts = FXCollections.observableArrayList();

    // ist die Liste mit ALLEN BandwidthData (auch denen die nicht angezeigt werden sollen)
    private final ObservableList<BandwidthData> bandwidthDataList = FXCollections.observableArrayList();

    public ChartData() {
        ChartFactory.initChartSeries(chartSeriesOneSumChart);
        downloadChartMaxTimeMinutes.addListener((ob, ol, ne) -> {
            //die muss dann neu gesetzt werden!!
            bandwidthDataList.stream().forEach(b -> b.setLastIdx(0));
        });
    }

    public int getDownloadChartShowMaxTimeMinutes() {
        return downloadChartMaxTimeMinutes.get();
    }

    public void setDownloadChartMaxTimeMinutes(int downloadChartMaxTimeMinutes) {
        this.downloadChartMaxTimeMinutes.set(downloadChartMaxTimeMinutes);
    }

    public double getTimePerPixel() {
        return 1.0 * getDownloadChartShowMaxTimeMinutes() * 60.0 / ChartFactory.MAX_CHART_DATA_PER_SCREEN;
    }

    public int getAmountDataPerPixel() {
        //BandwidthData per screen-point
        final int secPerScreen = getDownloadChartShowMaxTimeMinutes() * 60;
        final double secPerPixel = 1.0 * secPerScreen / ChartFactory.MAX_CHART_DATA_PER_SCREEN;
        final int dataPerPixel = (int) Math.round(secPerPixel / ChartFactory.DATA_ALL_SECONDS);

        return dataPerPixel <= 0 ? 1 : dataPerPixel;
    }

    public int getCountSeconds() {
        //Summe aller "Sekunden" die das Programm läuft
        return countSeconds;
    }

    public void addCountSek() {
        this.countSeconds += 1;
        countMinutes = countSeconds / 60.0; // Minuten
    }

    public double getCountMinutes() {
        return countMinutes;
    }

    public int getTimeShowingSeconds() {
        //Zeit die angezeigt werden soll oder Programmlaufzeit wenn weniger
        int displayMinTimeShowing_sec = getCountSeconds() - getDownloadChartShowMaxTimeMinutes() * 60;
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
