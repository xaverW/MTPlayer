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
    private int yScale = 1;
    private int lastIdx = 0;
    private int oldestSecond = 0;
    private int maxTimeMinutes = downloadChartMaxTimeMinutes.get();
    private int maxTimeSeconds = downloadChartMaxTimeMinutes.get() * 60;
    private int amountDataPerPixel = 1;
    private int secondsPerPixel = 1;

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
//            setLastIdx(0);
        });
    }

    public void genActValues() {
        setOldestTimeShowing();
        genAmountDataPerPixel();
        genLastIdx();
    }

    private void setOldestTimeShowing() {
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
    }

    private void genAmountDataPerPixel() {
        amountDataPerPixel = (int) Math.round(1.0 * secondsPerPixel / ChartFactory.DATA_ALL_SECONDS);
        if (amountDataPerPixel < secondsPerPixel / ChartFactory.DATA_ALL_SECONDS) {
            ++amountDataPerPixel;
        }

        amountDataPerPixel = amountDataPerPixel <= 0 ? 1 : amountDataPerPixel;


//        //BandwidthData per screen-point
//        amountDataPerPixel = 1;
//
//        int lastTime = getCountSeconds();
//        int divTimeMin = (lastTime - oldestSecond) / 60;
//        int minPerScreen = getDownloadChartShowMaxTimeMinutes();
//
//        if (divTimeMin <= 2 || minPerScreen <= 2) {
//            amountDataPerPixel = 2;
//        } else if (divTimeMin <= 10 || minPerScreen <= 10) {
//            amountDataPerPixel = 5;
//        } else if (divTimeMin <= 30 || minPerScreen <= 30) {
//            amountDataPerPixel = 5;
//        } else if (divTimeMin <= 60 || minPerScreen <= 60) {
//            amountDataPerPixel = 5;
//        } else if (divTimeMin <= 90 || minPerScreen <= 90) {
//            amountDataPerPixel = 10;
//        } else if (divTimeMin <= 120 || minPerScreen <= 120) {
//            amountDataPerPixel = 10;
//        } else if (divTimeMin <= 150 || minPerScreen <= 150) {
//            amountDataPerPixel = 15;
//        } else if (divTimeMin <= 200 || minPerScreen <= 200) {
//            amountDataPerPixel = 20;
//        } else if (divTimeMin <= 250 || minPerScreen <= 250) {
//            amountDataPerPixel = 25;
//        } else {
//            amountDataPerPixel = 30;
//        }
    }

    private void genLastIdx() {
        while (lastIdx < getCountSeconds() - 1) {
            lastIdx += amountDataPerPixel;
        }

        int time = lastIdx - oldestSecond;

        secondsPerPixel = (int) Math.round(1.0 * maxTimeSeconds / ChartFactory.MAX_CHART_DATA_PER_SCREEN);
        if (secondsPerPixel < 1.0 * maxTimeSeconds / ChartFactory.MAX_CHART_DATA_PER_SCREEN) {
            ++secondsPerPixel;
        }
        if (secondsPerPixel < 1) {
            secondsPerPixel = 1;
        }
    }

    public int getAmountDataPerPixel() {
        //BandwidthData per screen-point
//        final int secPerScreen = getDownloadChartShowMaxTimeMinutes() * 60;
//        final double secPerPixel = 1.0 * secPerScreen / ChartFactory.MAX_CHART_DATA_PER_SCREEN;
//        final int dataPerPixel = (int) Math.round(secPerPixel / ChartFactory.DATA_ALL_SECONDS);
//
//        return dataPerPixel <= 0 ? 1 : dataPerPixel;
        return amountDataPerPixel;
    }

    public int getDownloadChartShowMaxTimeMinutes() {
        return downloadChartMaxTimeMinutes.get();
    }

    public void setDownloadChartMaxTimeMinutes(int downloadChartMaxTimeMinutes) {
        this.downloadChartMaxTimeMinutes.set(downloadChartMaxTimeMinutes);
    }

    public int getSecondsPerPixel() {
//        return 1.0 * getDownloadChartShowMaxTimeMinutes() * 60.0 / ChartFactory.MAX_CHART_DATA_PER_SCREEN;
        return secondsPerPixel;
    }


    public int getMaxPixel() {
        //BandwidthData per screen-point
        int maxPixel = 1;
        int firstTime = 0;
        for (BandwidthData bandwidthData : bandwidthDataList) {
            if (!bandwidthData.isShowing()) {
                continue;
            }
            if (firstTime > bandwidthData.getStartTimeSec()) {
                firstTime = bandwidthData.getStartTimeSec();
            }
        }
        int lastTime = getCountSeconds();
        int divTimeMin = (lastTime - firstTime) / 60;

        if (divTimeMin <= 5) {
            maxPixel = 50;
        } else if (divTimeMin <= 10) {
            maxPixel = 100;
        } else if (divTimeMin <= 50) {
            maxPixel = 200;
        } else if (divTimeMin <= 100) {
            maxPixel = 200;
        } else {
            maxPixel = 500;
        }

        return maxPixel;
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

    public int getyScale() {
        return yScale;
    }

    public void setyScale(int yScale) {
        this.yScale = yScale;
    }

    public int getLastIdx() {
        return lastIdx;
    }

    public void setLastIdx(int lastIdx) {
        this.lastIdx = lastIdx;
    }

    public int getOldestSecond() {
        return oldestSecond;
    }

    public void setOldestSecond(int oldestSecond) {
        this.oldestSecond = oldestSecond;
    }

    public int getMaxTimeSeconds() {
        return maxTimeSeconds;
    }

    public void setMaxTimeSeconds(int maxTimeSeconds) {
        this.maxTimeSeconds = maxTimeSeconds;
    }

    public void setAmountDataPerPixel(int amountDataPerPixel) {
        this.amountDataPerPixel = amountDataPerPixel;
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
