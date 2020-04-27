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

package de.mtplayer.mtp.gui.chart;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;
import de.mtplayer.mtp.gui.tools.Listener;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadGuiChart {

    private BooleanProperty separatChartProp = ProgConfig.DOWNLOAD_CHART_SEPARAT.getBooleanProperty();
    private final ProgData progData;

    private LineChart<Number, Number> lineChart = null;
    private List<Download> startedDownloads = new ArrayList<>(); // Liste gestarteter Downloads
    private final ChartData chartData;
    private ContextMenu cm = null;
    private AnchorPane anchorPane;

    public DownloadGuiChart(ProgData progData, AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        this.progData = progData;

        chartData = new ChartData();

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
        chartData.getLineChartsSeparate().clear(); // da werden alle chartSeries gelöscht, jeder Download
        chartData.getSumChartSeries().getData().clear(); // da werden die Daten in der einen chartSeries gelöscht, Summe aller Downloads
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
//        lineChart.setOnContextMenuRequested((ContextMenuEvent e) -> {
//            if (cm != null && cm.isShowing()) {
//                cm.hide();
//            }
//            cm = initContextMenu();
//            cm.show(lineChart, e.getScreenX(), e.getScreenY());
//        });
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

    private ContextMenu initContextMenu() {
        final CheckMenuItem allDowns = new CheckMenuItem("jeden Download einzeln zeichnen und nicht alle zusammenfassen");
        allDowns.selectedProperty().bindBidirectional(separatChartProp);
        allDowns.setOnAction(e -> selectChartData());

        final Slider slMaxTime = new Slider();
        slMaxTime.setMaxWidth(Double.MAX_VALUE);
        slMaxTime.setMin(30);
        slMaxTime.setMax(300);
        slMaxTime.valueProperty().bindBidirectional(chartData.maxTimeProperty());

        final Label lblValue = new Label(" " + chartData.getMaxTime() + " Min.");
        slMaxTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lblValue.setText(" " + newValue.intValue() + " Min.");
            }
        });
        final Label lblInfo = new Label("Zeitraum:");

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(lblInfo, slMaxTime, lblValue);
        HBox.setHgrow(slMaxTime, Priority.ALWAYS);
        CustomMenuItem cmi = new CustomMenuItem(hBox);

        final MenuItem delData = new MenuItem("Diagramm löschen");
        delData.setOnAction(e -> initList());

        final ContextMenu cm = new ContextMenu();
        cm.getItems().addAll(cmi, allDowns, delData);
        return cm;
    }

    private void selectChartData() {
        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            lineChart.setData(chartData.getLineChartsSeparate());
        } else {
            lineChart.setData(chartData.getLineChartsSum());
        }
    }

    // ============================
    // Daten generieren
    // ============================
    private synchronized void searchInfos() {
        ChartFactory.cleanUpChart(progData, chartData);

        chartData.addCountSek(1); // Sekunden
        final double countMin = chartData.getCountSek() / 60.0; // Minuten
        startedDownloads = progData.downloadList.getListOfStartsNotFinished(DownloadConstants.ALL);

        //Downloads in "Diagramm" eintragen
        for (final Download download : startedDownloads) {
            //jeden Download eintragen
            XYChart.Series<Number, Number> cSeries = download.getCSeries();

            if (cSeries != null) {
                final long bandw = download.getStart().getBandwidth();
                cSeries.getData().add(new XYChart.Data<>(countMin, bandw / chartData.getScale()));
            } else {
                cSeries = new XYChart.Series<>(download.getNr() + "", FXCollections.observableArrayList());
                download.setCSeries(cSeries);

                cSeries.getData().add(new XYChart.Data<>(countMin, 0L));
                cSeries.getData().add(new XYChart.Data<>(countMin, download.getStart().getBandwidth() / chartData.getScale()));
                chartData.getLineChartsSeparate().add(cSeries);
            }
        }


        // chart in den gestarteten Downloads suchen
        Iterator<XYChart.Series<Number, Number>> it = chartData.getLineChartsSeparate().listIterator();
        while (it.hasNext()) {
            XYChart.Series<Number, Number> cSeries = it.next();
            boolean foundDownload = false;
            for (final Download download : startedDownloads) {
                if (download.getCSeries() != null && download.getCSeries().equals(cSeries)) {
                    foundDownload = true;
                    break;
                }
            }

            if (!foundDownload) {
                int size = cSeries.getData().size();
                if (size > 0 && cSeries.getData().get(size - 1).getYValue().longValue() > 0) {
                    // nur einen Wert "0" setzen und dann pausieren
                    cSeries.getData().add(new XYChart.Data<>(countMin, 0L));
                }
                if (cSeries.getData().isEmpty()) {
                    // dann wurde bereits alles gelöscht und kommt jetzt auch weg
                    it.remove();
                }
            }
        }

        // chart in allen Downloads suchen
        for (final XYChart.Series<Number, Number> cSeries : chartData.getLineChartsSeparate()) {
            boolean foundDownload = false;
            for (final Download download : progData.downloadList) {
                if (download.getCSeries() != null && download.getCSeries().equals(cSeries)) {
                    foundDownload = true;
                    break;
                }
            }

            if (!foundDownload) {
                if (!cSeries.getName().equals(" ")) {
                    cSeries.setName(" ");
                }
            }
        }

        // Anzeige der Summe aller Downloads
        chartData.getSumChartSeries().getData().add(new XYChart.Data<>(countMin, progData.downloadInfos.getBandwidth() / chartData.getScale()));
        ChartFactory.zoomXAxis(lineChart, chartData, countMin);
        ChartFactory.zoomYAxis(lineChart, chartData);
    }

}
