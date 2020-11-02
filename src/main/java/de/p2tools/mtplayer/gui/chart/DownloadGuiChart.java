/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
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
import de.p2tools.mtplayer.gui.tools.Listener;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class DownloadGuiChart {

    private BooleanProperty separatChartProp = ProgConfig.DOWNLOAD_CHART_SEPARAT.getBooleanProperty();
    private BooleanProperty chartAllDownloadsProp = ProgConfig.DOWNLOAD_CHART_ALL_DOWNLOADS.getBooleanProperty();
    private BooleanProperty chartOnlyExistingProp = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBooleanProperty();
    private BooleanProperty chartOnlyRunningProp = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBooleanProperty();
    private final ProgData progData;

    private LineChart<Number, Number> lineChart = null;
    private final ChartData chartData;
    private ContextMenu cm = null;
    private AnchorPane anchorPane;

    public DownloadGuiChart(ProgData progData, AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        this.progData = progData;
        chartData = progData.chartData;

        initList();
        initCharts();
        selectChartData();

        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, DownloadGuiChart.class.getSimpleName()) {
            @Override
            public void pingFx() {
                searchInfos();
            }
        });
    }

    private synchronized void initList() {
        chartData.setScale(1);
    }

    private void initCharts() {
        lineChart = new LineChart<>(ChartFactory.createXAxis(), ChartFactory.createYAxis());
        lineChart.getStyleClass().add("thick-chart");
        lineChart.setLegendSide(Side.RIGHT);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setTitle("Downloads");
        lineChart.getXAxis().setLabel("Programmlaufzeit [min]");
        ChartFactory.setYAxisLabel(lineChart, chartData);
        lineChart.setOnMouseClicked(e -> {
            if (cm != null && cm.isShowing()) {
                // hier damit beim normalen Klick das Menü wieder ausgeblendet wird
                cm.hide();
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                cm = initContextMenu();
                cm.show(lineChart, e.getScreenX(), e.getScreenY());
            }
        });

        AnchorPane.setLeftAnchor(lineChart, 0.0);
        AnchorPane.setBottomAnchor(lineChart, 0.0);
        AnchorPane.setRightAnchor(lineChart, 0.0);
        AnchorPane.setTopAnchor(lineChart, 0.0);
        anchorPane.getChildren().add(lineChart);
    }

    private void selectChartData() {
        if (separatChartProp.get()) {
            lineChart.setData(chartData.getChartSeriesList_SeparateCharts());
        } else {
            lineChart.setData(chartData.getChartSeriesList_OneSumChart());
        }
    }

    private synchronized void clearChart() {
        chartData.getBandwidthDataList().clear(); // da werden alle gesammelten Daten gelöscht
        chartData.setScale(1);
    }

    private ContextMenu initContextMenu() {
        final Label lblValue = new Label(" " + chartData.getShowMaxTimeMinutes() + " Min.");
        final Label lblInfo = new Label("Zeitraum:");

        final Slider slMaxTime = new Slider();
        slMaxTime.setMinWidth(250);
        slMaxTime.setMin(ProgData.debug ? 1 : 10);
        slMaxTime.setMax(ChartFactory.MAX_MINUTES_SHOWING);
        slMaxTime.setBlockIncrement(10);
        slMaxTime.setShowTickLabels(true);
        slMaxTime.setSnapToTicks(true);
        slMaxTime.setShowTickMarks(true);
        slMaxTime.setMinorTickCount(13);
        slMaxTime.setMajorTickUnit(140);

        slMaxTime.valueProperty().bindBidirectional(chartData.showMaxTimeMinutesProperty());
        slMaxTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lblValue.setText(" " + newValue.intValue() + " Min.");
            }
        });

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(lblInfo, slMaxTime, lblValue);
        HBox.setHgrow(slMaxTime, Priority.ALWAYS);
        CustomMenuItem cmiTime = new CustomMenuItem(hBox);


        final CheckMenuItem chkAllDowns = new CheckMenuItem("jeden Download einzeln zeichnen und nicht alle zusammenfassen");
        chkAllDowns.selectedProperty().bindBidirectional(separatChartProp);
        chkAllDowns.setOnAction(e -> selectChartData());


        final RadioMenuItem rbAll = new RadioMenuItem("alle Downloads immer anzeigen");
        final RadioMenuItem rbOnlyExisting = new RadioMenuItem("nur noch vorhandene Downloads anzeigen");
        final RadioMenuItem rbOnlyRunning = new RadioMenuItem("nur aktuell laufende Downloads anzeigen");
        final ToggleGroup group = new ToggleGroup();
        rbAll.setToggleGroup(group);
        rbOnlyExisting.setToggleGroup(group);
        rbOnlyRunning.setToggleGroup(group);

        rbAll.selectedProperty().bindBidirectional(chartAllDownloadsProp);
        rbOnlyExisting.selectedProperty().bindBidirectional(chartOnlyExistingProp);
        rbOnlyRunning.selectedProperty().bindBidirectional(chartOnlyRunningProp);

        rbAll.disableProperty().bind(chkAllDowns.selectedProperty().not());
        rbOnlyExisting.disableProperty().bind(chkAllDowns.selectedProperty().not());
        rbOnlyRunning.disableProperty().bind(chkAllDowns.selectedProperty().not());


        final MenuItem delData = new MenuItem("Diagramm löschen");
        delData.setOnAction(e -> clearChart());


        final ContextMenu cm = new ContextMenu();
        cm.getItems().addAll(cmiTime,
                new SeparatorMenuItem(), rbAll, rbOnlyExisting, rbOnlyRunning,
                new SeparatorMenuItem(), chkAllDowns, delData);
        return cm;
    }

    // ============================
    // Daten generieren
    // ============================
    private synchronized void searchInfos() {
        ChartFactory.runChart(lineChart, chartData, progData);
    }
}
