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
    private int yScale = 1;
    private double dataPerPixel = 1;
    private double secondsPerPixel = 1;

    // Liste der LineCharts für einzelne Downloads -> für jeden Download eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> chartSeriesList = FXCollections.observableArrayList();

    // ist die Liste mit ALLEN BandwidthData (auch denen die nicht angezeigt werden sollen)
    private final ObservableList<BandwidthData> bandwidthDataList = FXCollections.observableArrayList();

    public ChartData() {
        ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN.addListener((ob, ol, ne) -> {
            //die muss dann neu gesetzt werden!!
            ChartDataFactory.genActShowingTimeValues(this);
        });
    }

    public int getyScale() {
        return yScale;
    }

    public void setyScale(int yScale) {
        this.yScale = yScale;
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

    public ObservableList<XYChart.Series<Number, Number>> getChartSeriesList() {
        return chartSeriesList;
    }

    public ObservableList<BandwidthData> getBandwidthDataList() {
        return bandwidthDataList;
    }
}
