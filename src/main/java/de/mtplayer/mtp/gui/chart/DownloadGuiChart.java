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
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

public class DownloadGuiChart {

    private BooleanProperty separatChartProp = ProgConfig.DOWNLOAD_CHART_SEPARAT.getBooleanProperty();
    private BooleanProperty allDownloadsProp = ProgConfig.DOWNLOAD_CHART_ALL_DOWNLOADS.getBooleanProperty();
    private BooleanProperty onlyExistingProp = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBooleanProperty();
    private BooleanProperty onlyRunningProp = ProgConfig.DOWNLOAD_CHART_ONLY_RUNNING.getBooleanProperty();
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
        chartData.getChartSeriesListSeparate().clear(); // da werden alle chartSeries gelöscht, jeder Download
        chartData.getChartSeriesSum().getData().clear(); // da werden die Daten in der einen chartSeries gelöscht, Summe aller Downloads
        chartData.setScale(1);
    }

    private synchronized void clearChart() {
        chartData.getChartSeriesListSeparate().stream().forEach(series -> series.getData().clear()); // da werden nur die Series gelöscht
        chartData.getChartSeriesSum().getData().clear(); // da werden die Daten in der einen chartSeries gelöscht, Summe aller Downloads
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

    private ContextMenu initContextMenu() {
        final Label lblValue = new Label(" " + chartData.getMaxTime() + " Min.");
        final Label lblInfo = new Label("Zeitraum:");

        final Slider slMaxTime = new Slider();
        slMaxTime.setMinWidth(250);
        slMaxTime.setMin(1); //<------------------------
        slMaxTime.setMax(ChartFactory.CHART_MAX_TIME);
        slMaxTime.setBlockIncrement(10);
        slMaxTime.setShowTickLabels(true);
        slMaxTime.setSnapToTicks(true);
        slMaxTime.setShowTickMarks(true);
        slMaxTime.setMinorTickCount(13);
        slMaxTime.setMajorTickUnit(140);

        slMaxTime.valueProperty().bindBidirectional(chartData.maxTimeProperty());
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

        rbAll.setOnAction(e -> ChartFactory.generateChartData(lineChart, progData, chartData));
        rbOnlyExisting.setOnAction(e -> ChartFactory.generateChartData(lineChart, progData, chartData));
        rbOnlyRunning.setOnAction(e -> ChartFactory.generateChartData(lineChart, progData, chartData));

        rbAll.selectedProperty().bindBidirectional(allDownloadsProp);
        rbOnlyExisting.selectedProperty().bindBidirectional(onlyExistingProp);
        rbOnlyRunning.selectedProperty().bindBidirectional(onlyRunningProp);

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

    private void selectChartData() {
        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            lineChart.setData(chartData.getChartSeriesListSeparate());
        } else {
            lineChart.setData(chartData.getChartSeriesListSum());
        }
    }

    // ============================
    // Daten generieren
    // ============================
    private synchronized void searchInfos() {
        chartData.addCountSek(1); // Sekunden
        final double countMinute = chartData.getCountSek() / 60.0; // Minuten
        startedDownloads = progData.downloadList.getListOfStartsNotFinished(DownloadConstants.ALL);

        ChartFactory.cleanUpChartData(chartData, startedDownloads, countMinute);
        ChartFactory.inputDownloadDate(chartData, startedDownloads, countMinute, progData);
        ChartFactory.generateChartData(lineChart, progData, chartData);
        ChartFactory.zoomXAxis(lineChart, chartData, countMinute);
        ChartFactory.zoomYAxis(lineChart, chartData);
    }


//    private synchronized void changeLegend() {
//
////        XYChart.Series<Number, Number> value = null;  //is our serie value.
////        for (int index = 0; index < value.getData().size(); index++) {
////            // we're looping for each data point, changing the color of line symbol
////            XYChart.Data dataPoint = value.getData().get(index);
////            Node lineSymbol = dataPoint.getNode().lookup(".chart-line-symbol");
////            lineSymbol.setStyle("-fx-background-color: #0000FF, white;");
////        }
////        // and this is for the color of the line
////        value.getNode().setStyle("-fx-border-style: solid; -fx-stroke: #0000FF; -fx-background-color: #0000FF;");
//
//        Set<Node> items = lineChart.lookupAll("Label.chart-legend-item");
//        int i = 0;
//        // these colors came from caspian.css .default-color0..4.chart-pie
//        Color[] colors = {Color.web("#f9d900"), Color.web("#a9e200"), Color.web("#22bad9"), Color.web("#0181e2"), Color.web("#2f357f")};
//        for (Node item : items) {
//            Label label = (Label) item;
//            if (red) {
//                red = false;
//                label.setStyle(" -fx-text-fill: green;");
//            } else {
//                red = true;
//                label.setStyle(" -fx-text-fill: red;");
//            }
////            final Rectangle rectangle = new Rectangle(10, 10, colors[i]);
////            final Glow niceEffect = new Glow();
////            niceEffect.setInput(new Reflection());
////            rectangle.setEffect(niceEffect);
////            label.setGraphic(rectangle);
//            i++;
//        }
//
//
//        for (Node n : lineChart.getChildrenUnmodifiable()) {
//            if (n instanceof Legend) {
//                for (Legend.LegendItem legendItem : ((Legend) n).getItems()) {
//                    if (legendItem.getText().equals("1")) {
//                        legendItem.getSymbol().setStyle("-fx-stroke: #333;");
////                        legendItem.getSymbol().setStyle(" -fx-text-fill: green;");
////                        legendItem.getSymbol().setStyle(" -fx-background-color: #d2ffd2;");
////                        legendItem.setSymbol(new ProgIcons().DOWNLOAD_OK);
//                    }
//
//
//                }
//            } else if (n instanceof Label) {
//                final Label label = (Label) n;
//                label.setStyle(" -fx-text-fill: green;");
//                label.getChildrenUnmodifiable().addListener(new ListChangeListener<Object>() {
//                    @Override
//                    public void onChanged(Change<?> arg0) {
//                        //make style changes here
//                        label.setStyle(" -fx-text-fill: green;");
//                    }
//
//                });
//            }
//        }
//
//    }

}
