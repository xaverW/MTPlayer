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

package de.p2tools.mtplayer.gui.infoPane;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.gui.chart.BandwidthDataFactory;
import de.p2tools.mtplayer.gui.chart.ChartDataFactory;
import de.p2tools.mtplayer.gui.chart.ChartFactory;
import de.p2tools.mtplayer.gui.chart.ChartGenerateFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PaneBandwidthChart extends VBox {

    private final BooleanProperty chartOnlyExistingProp = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING;
    private final BooleanProperty chartOnlyRunningProp = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING;
    private final ProgData progData;

    private LineChart<Number, Number> lineChart = null;
    private ContextMenu cm = null;

    public PaneBandwidthChart(ProgData progData) {
        this.progData = progData;
        initList();
        initCharts();
    }

    public synchronized void searchInfos(boolean visible) {
        BandwidthDataFactory.addBandwidthData();
        if (visible) {
            ChartDataFactory.runChart(lineChart);
        }
    }

    private synchronized void initList() {
        progData.chartData.setyScale(1);
    }

    private void initCharts() {
        lineChart = new LineChart<>(ChartFactory.createXAxis(), ChartFactory.createYAxis());
        lineChart.getStyleClass().add("thick-chart");
        lineChart.setLegendSide(Side.RIGHT);
        lineChart.setLegendVisible(true); // rechts die Anzeige der lines Namen
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setTitle("Downloads");

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
        lineChart.setData(progData.chartData.getChartSeriesList());

        AnchorPane.setLeftAnchor(lineChart, 0.0);
        AnchorPane.setBottomAnchor(lineChart, 0.0);
        AnchorPane.setRightAnchor(lineChart, 0.0);
        AnchorPane.setTopAnchor(lineChart, 0.0);
        getChildren().add(lineChart);
    }

    private synchronized void clearChart() {
        progData.chartData.getBandwidthDataList().clear(); // da werden alle gesammelten Daten gelöscht
        progData.chartData.setyScale(1);
    }

    private ContextMenu initContextMenu() {
        if (ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN.getValue() % 10 != 0) {
            // vorsichtshalber
            ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN.setValue(30);
        }

        final Label lblValue = new Label(" " + ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN.get() + " Min.");
        final Label lblInfo = new Label("Zeitraum:");

        final Slider slMaxTime = new Slider();
        slMaxTime.setMinWidth(250);
        slMaxTime.setMin(10);
        slMaxTime.setMax(BandwidthDataFactory.MAX_SECONDS_SHOWING / 60.0);
        slMaxTime.setBlockIncrement(10);
        slMaxTime.setShowTickLabels(true);
        slMaxTime.setSnapToTicks(true);
        slMaxTime.setShowTickMarks(true);
        slMaxTime.setMinorTickCount(0);
        slMaxTime.setMajorTickUnit(10);

        IntegerProperty ip = ProgConfig.DOWNLOAD_CHART_MAX_TIME_TO_SHOW_MIN;
        slMaxTime.valueProperty().bindBidirectional(ip);
        slMaxTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lblValue.setText(" " + newValue.intValue() + " Min.");
            }
        });

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(lblInfo, slMaxTime, lblValue);
        HBox.setHgrow(slMaxTime, Priority.ALWAYS);
        CustomMenuItem cmiTime = new CustomMenuItem(hBox);

        ToggleGroup tg = new ToggleGroup();
        final RadioMenuItem rbShowAll = new RadioMenuItem("Summe und alle Downloads anzeigen");
        rbShowAll.setToggleGroup(tg);
        rbShowAll.setSelected(ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.getValue() == ChartGenerateFactory.GEN_CHART_SHOW_ALL);
        rbShowAll.setOnAction(e -> {
            ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.setValue(ChartGenerateFactory.GEN_CHART_SHOW_ALL);
        });
        final RadioMenuItem rbShowDown = new RadioMenuItem("Nur Downloads anzeigen");
        rbShowDown.setToggleGroup(tg);
        rbShowDown.setSelected(ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.getValue() == ChartGenerateFactory.GEN_CHART_SHOW_DOWN);
        rbShowDown.setOnAction(e -> {
            ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.setValue(ChartGenerateFactory.GEN_CHART_SHOW_DOWN);
        });
        final RadioMenuItem rbShowSum = new RadioMenuItem("Nur Summe anzeigen");
        rbShowSum.setToggleGroup(tg);
        rbShowSum.setSelected(ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.getValue() == ChartGenerateFactory.GEN_CHART_SHOW_SUM);
        rbShowSum.setOnAction(e -> {
            ProgConfig.DOWNLOAD_CHART_SHOW_WHAT.setValue(ChartGenerateFactory.GEN_CHART_SHOW_SUM);
        });

        final RadioMenuItem rbAll = new RadioMenuItem("alle Downloads immer anzeigen");
        final RadioMenuItem rbOnlyExisting = new RadioMenuItem("nur noch vorhandene Downloads anzeigen");
        final RadioMenuItem rbOnlyRunning = new RadioMenuItem("nur aktuell laufende Downloads anzeigen");
        final ToggleGroup group = new ToggleGroup();
        rbAll.setToggleGroup(group);
        rbOnlyExisting.setToggleGroup(group);
        rbOnlyRunning.setToggleGroup(group);

        rbAll.setSelected(!chartOnlyExistingProp.getValue() && !chartOnlyRunningProp.getValue());
        rbOnlyExisting.selectedProperty().bindBidirectional(chartOnlyExistingProp);
        rbOnlyRunning.selectedProperty().bindBidirectional(chartOnlyRunningProp);

        rbAll.disableProperty().bind(rbShowAll.selectedProperty().not().and(rbShowDown.selectedProperty().not()));
        rbOnlyExisting.disableProperty().bind(rbShowAll.selectedProperty().not().and(rbShowDown.selectedProperty().not()));
        rbOnlyRunning.disableProperty().bind(rbShowAll.selectedProperty().not().and(rbShowDown.selectedProperty().not()));

        final MenuItem delData = new MenuItem("Diagramm löschen");
        delData.setOnAction(e -> clearChart());


        final ContextMenu cm = new ContextMenu();
        cm.getItems().addAll(cmiTime,
                new SeparatorMenuItem(), rbShowAll, rbShowDown, rbShowSum,
                new SeparatorMenuItem(), rbAll, rbOnlyExisting, rbOnlyRunning,
                new SeparatorMenuItem(), delData);
        return cm;
    }
}
