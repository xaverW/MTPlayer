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

    private int countProgRunningTimeSeconds = 0;
    private double countProgRunningTimeMinutes = 0;
    private int yScale = 1;
    //    private int lastIdx = 0;
    private int oldestSecond = 0;
    private int maxTimeMinutes = downloadChartMaxTimeMinutes.get();
    private int maxTimeSeconds = downloadChartMaxTimeMinutes.get() * 60;
    private int dataPerPixel = 1;
    private int secondsPerPixel = 1;
    private int maxTimeSecondsAct = maxTimeSeconds;

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
            maxTimeMinutes = downloadChartMaxTimeMinutes.get();
            maxTimeSeconds = downloadChartMaxTimeMinutes.get() * 60;
            genActValues();
        });
    }

    public void genActValues() {
        //Zeitbereich der angezeigt werden soll
        int oldest = 0;
        for (int bi = 0; bi < getBandwidthDataList().size(); ++bi) {
            BandwidthData bandwidthData = getBandwidthDataList().get(bi);
            if (!bandwidthData.isShowing()) {
                continue;
            }
            if (oldest < bandwidthData.getStartTimeSec()) {
                oldest = bandwidthData.getStartTimeSec();
            }
        }
        oldestSecond = oldest;

        //Anzahl der Sekunden/Pixel
        int div = getCountProgRunningTimeSeconds() - oldestSecond;
        div = div < maxTimeSeconds ? div : maxTimeSeconds;
        if (div < 10 * 60) {
            maxTimeSecondsAct = 10 * 60;

//        } else if (div < 10 * 60) {
//            maxTimeSecondsAct = 10 * 60;

        } else if (div < 30 * 60) {
            maxTimeSecondsAct = 30 * 60;

        } else if (div < 60 * 60) {
            maxTimeSecondsAct = 60 * 60;

        } else if (div < 100 * 60) {
            maxTimeSecondsAct = 100 * 60;

        } else if (div < 200 * 60) {
            maxTimeSecondsAct = 200 * 60;

        } else {
            maxTimeSecondsAct = ChartFactory.MAX_SECONDS_SHOWING;
        }

        secondsPerPixel = (int) Math.round(1.0 * maxTimeSecondsAct / ChartFactory.MAX_CHART_DATA_PER_SCREEN);
        if (secondsPerPixel < 1.0 * maxTimeSecondsAct / ChartFactory.MAX_CHART_DATA_PER_SCREEN) {
            ++secondsPerPixel;
        }
        if (secondsPerPixel < 1) {
            secondsPerPixel = 1;
        }

        //Anzahl der Daten/Pixel
        dataPerPixel = (int) Math.round(1.0 * secondsPerPixel / ChartFactory.DATA_ALL_SECONDS);
        if (dataPerPixel < secondsPerPixel / ChartFactory.DATA_ALL_SECONDS) {
            ++dataPerPixel;
        }
        dataPerPixel = dataPerPixel <= 0 ? 1 : dataPerPixel;
    }

    public void addCountSek() {
        this.countProgRunningTimeSeconds += 1;
        countProgRunningTimeMinutes = countProgRunningTimeSeconds / 60.0; // Minuten
    }

    public int getTimeShowingSeconds() {
        //Zeit die angezeigt werden soll oder Programmlaufzeit wenn weniger
        int displayMinTimeShowing_sec = getCountProgRunningTimeSeconds() - getDownloadChartMaxTimeMinutes() * 60;
        if (displayMinTimeShowing_sec < 0) {
            displayMinTimeShowing_sec = 0;
        }
        return displayMinTimeShowing_sec;
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


    public int getDownloadChartMaxTimeMinutes() {
        return downloadChartMaxTimeMinutes.get();
    }

    public int getCountProgRunningTimeSeconds() {
        return countProgRunningTimeSeconds;
    }

    public void setCountProgRunningTimeSeconds(int countProgRunningTimeSeconds) {
        this.countProgRunningTimeSeconds = countProgRunningTimeSeconds;
    }

    public double getCountProgRunningTimeMinutes() {
        return countProgRunningTimeMinutes;
    }

    public void setCountProgRunningTimeMinutes(double countProgRunningTimeMinutes) {
        this.countProgRunningTimeMinutes = countProgRunningTimeMinutes;
    }

    public int getyScale() {
        return yScale;
    }

    public void setyScale(int yScale) {
        this.yScale = yScale;
    }

    public int getOldestSecond() {
        return oldestSecond;
    }

    public void setOldestSecond(int oldestSecond) {
        this.oldestSecond = oldestSecond;
    }

    public int getMaxTimeMinutes() {
        return maxTimeMinutes;
    }

    public void setMaxTimeMinutes(int maxTimeMinutes) {
        this.maxTimeMinutes = maxTimeMinutes;
    }

    public int getMaxTimeSeconds() {
        return maxTimeSeconds;
    }

    public void setMaxTimeSeconds(int maxTimeSeconds) {
        this.maxTimeSeconds = maxTimeSeconds;
    }

    public int getDataPerPixel() {
        return dataPerPixel;
    }

    public void setDataPerPixel(int dataPerPixel) {
        this.dataPerPixel = dataPerPixel;
    }

    public int getSecondsPerPixel() {
        return secondsPerPixel;
    }

    public void setSecondsPerPixel(int secondsPerPixel) {
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
