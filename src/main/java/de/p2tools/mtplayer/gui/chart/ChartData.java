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
    private IntegerProperty maxTime = ProgConfig.DOWNLOAD_CHART_MAX_TIME.getIntegerProperty();

    private int countSek = 0;
    private int scale = 1;

    // ist die eine Chart für den Gesamtwert
    private final XYChart.Series<Number, Number> chartSeriesSum =
            new XYChart.Series<>("Summe", FXCollections.observableArrayList(new XYChart.Data<Number, Number>(0.0, 0.0)));

    // Liste der LineCharts für Gesamt -> hat nur eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesListSum = FXCollections.observableArrayList(chartSeriesSum);

    // Liste der LineCharts für einzele Downloads -> für jeden Download eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesListSeparate = FXCollections.observableArrayList();

    // ist die Liste mit ALLEN LineCharts (auch denen die nicht angezeigt werden sollen)
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesListAll = FXCollections.observableArrayList();

    public ChartData() {
    }

    public int getMaxTime() {
        return maxTime.get();
    }

    public IntegerProperty maxTimeProperty() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime.set(maxTime);
    }

    public int getCountSek() {
        return countSek;
    }

    public void setCountSek(int countSek) {
        this.countSek = countSek;
    }

    public void addCountSek(int addCountSek) {
        this.countSek += addCountSek;
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

    public ObservableList<XYChart.Series<Number, Number>> getChartSeriesListSum() {
        return chartSeriesListSum;
    }

    public ObservableList<XYChart.Series<Number, Number>> getChartSeriesListSeparate() {
        return chartSeriesListSeparate;
    }

    public ObservableList<XYChart.Series<Number, Number>> getChartSeriesListAll() {
        return chartSeriesListAll;
    }

    public void genInfos() {
        ArrayList<String> info = new ArrayList<>();
        info.add(" ");
        info.add("======================");
        info.add(" Downloadchart");
        info.add("----------------------");
        final int max = ChartFactory.CHART_MAX_TIME * 60;
        info.add("--> max: " + max);

        info.add("chartSeriesListSum: " + chartSeriesListSum.size());
        for (final XYChart.Series<Number, Number> cSeries : chartSeriesListSum) {
            info.add("    " + cSeries.getName() + ": " + cSeries.getData().size());
        }

        info.add("chartSeriesListSeparate: " + chartSeriesListSeparate.size());
        for (final XYChart.Series<Number, Number> cSeries : chartSeriesListSeparate) {
            info.add("    " + cSeries.getName() + ": " + cSeries.getData().size());
        }

        info.add("chartSeriesListAll: " + chartSeriesListAll.size());
        for (final XYChart.Series<Number, Number> cSeries : chartSeriesListAll) {
            info.add("    " + cSeries.getName() + ": " + cSeries.getData().size());
        }

        info.add("======================");
        PLog.sysLog(info.toArray(new String[info.size()]));
    }
}
