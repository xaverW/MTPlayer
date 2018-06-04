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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import de.mtplayer.mtp.gui.tools.Listener;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.util.LinkedList;

public class DownloadGuiChart {

    private BooleanProperty separatChartProp = ProgConfig.DOWNLOAD_CHART_SEPARAT.getBooleanProperty();
    private final ProgData progData;

    private int countSek = 0;
    private int scale = 1;

    private LineChart<Number, Number> lineChart = null;
    private LinkedList<Download> startedDownloads = new LinkedList<>(); // Liste gestarteter Downloads

    private final XYChart.Series<Number, Number> sumSeries =
            new XYChart.Series<>("Summe", FXCollections.observableArrayList(new XYChart.Data<Number, Number>(0.0, 0.0)));

    // Liste der LineCharts für Gesamt -> hat nur eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> listChartSum = FXCollections.observableArrayList(sumSeries);

    // Liste der LineCharts für einzele Downloads -> für jeden Download eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> listChartSerparat = FXCollections.observableArrayList();

    private AnchorPane anchorPane;

    public DownloadGuiChart(ProgData progData, AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        this.progData = progData;

        initList();
        initCharts();

        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, StatusBarController.class.getSimpleName()) {
            @Override
            public void ping() {
                search();
            }
        });

    }

    private synchronized void initList() {
        listChartSerparat.clear();
        sumSeries.getData().clear();
        scale = 1;
    }

    private void initCharts() {
        final ContextMenu cm = initContext();

        lineChart = new LineChart<>(createXAxis(), createYAxis());

        anchorPane.getChildren().add(lineChart);
        anchorPane.setPadding(new Insets(10, 10, 10, 10));

        AnchorPane.setLeftAnchor(lineChart, 10.0);
        AnchorPane.setBottomAnchor(lineChart, 10.0);
        AnchorPane.setRightAnchor(lineChart, 10.0);
        AnchorPane.setTopAnchor(lineChart, 10.0);

        lineChart.setCreateSymbols(false);
        selectChartData();

        lineChart.setTitle("Downloads");
        lineChart.getXAxis().setLabel("Zeit [min]");
        lineChart.getYAxis().setLabel("Bandbreite");
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);

        lineChart.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                cm.show(lineChart, e.getSceneX(), e.getSceneY());
            }
        });

    }

    private NumberAxis createXAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(true);
        xAxis.setLowerBound(0.0);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                int i = (int) (object.doubleValue() * 10);
                return ((i / 10.0) + "");
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        return xAxis;
    }

    private NumberAxis createYAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(true);
        xAxis.setLowerBound(0.0);
        return xAxis;
    }

    private ContextMenu initContext() {
        final ContextMenu cm = new ContextMenu();

        final CheckMenuItem allDowns = new CheckMenuItem("Downloads einzeln anzeigen");
        final MenuItem delData = new MenuItem("Löschen");
        cm.getItems().addAll(allDowns, delData);

        allDowns.selectedProperty().bindBidirectional(separatChartProp);

        allDowns.setOnAction(e -> selectChartData());
        delData.setOnAction(e -> initList());

        return cm;
    }

    private void selectChartData() {
        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            lineChart.setData(listChartSerparat);
        } else {
            lineChart.setData(listChartSum);
        }
    }

    // ============================
    // Daten generieren
    // ============================
    private synchronized void search() {
        Platform.runLater(() -> {
            searchInfos();
        });
    }

    private synchronized void searchInfos() {
        boolean found;
        ++countSek; // Sekunden
        final double countMin = countSek / 60.0; // Minuten
        startedDownloads = progData.downloadList.getListOfStartsNotFinished(DownloadInfos.ALL);

        //Downloads in "Diagramm" eintragen
        for (final Download download : startedDownloads) {
            //jeden Download eintragen
            found = false;
            for (final XYChart.Series<Number, Number> cSeries : listChartSerparat) {
                if (cSeries.getName().equals(download.getNr() + "")) { //todo beim umsortieren der Downloads ändert sich die Nr
                    //dann gibts den schon
                    cSeries.getData().add(new XYChart.Data<>(countMin, download.getStart().getBandwidth() / scale));
                    found = true;
                }
            }
            if (!found) {
                listChartSerparat.add(new XYChart.Series<Number, Number>(download.getNr() + "",
                        FXCollections.observableArrayList(new XYChart.Data<Number, Number>(countMin, download.getStart().getBandwidth() / scale))));
            }
        }
        sumSeries.getData().add(new XYChart.Data<>(countMin, progData.downloadList.getDownloadInfoAll().bandwidth / scale));
        zoomXAxis(countMin);
        zoomYAxis();
    }

    private synchronized void zoomXAxis(double count) {
        final double MIN = count - ProgConst.DOWNLOAD_CHART_MAX_TIME;
        if (MIN <= 0) {
            return;
        }

        listChartSerparat.removeIf(cs -> !cs.getData().isEmpty() && cs.getData().get(0).getXValue().intValue() < MIN);

        while (!sumSeries.getData().isEmpty() && sumSeries.getData().get(0).getXValue().intValue() < MIN) {
            sumSeries.getData().remove(0);
        }

        final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(MIN);
        xAxis.setUpperBound(count);
    }

    private synchronized void zoomYAxis() {
        double max = 0;
        final ObservableList<XYChart.Series<Number, Number>> list;

        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            list = listChartSerparat;
        } else {
            list = listChartSum;
        }

        for (final XYChart.Series<Number, Number> cSeries : list) {
            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                if ((long) date.getYValue() > max) {
                    max = (long) date.getYValue();
                }
            }
        }

        if (max > 5_000) {
            scale *= 1000; // todo muss evtl. auch wieder kleiner werden??
            for (final XYChart.Series<Number, Number> cSeries : listChartSerparat) {
                for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                    date.setYValue((long) date.getYValue() / 1_000);
                }
            }
            for (final XYChart.Series<Number, Number> cSeries : listChartSum) {
                for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                    date.setYValue((long) date.getYValue() / 1_000);
                }
            }
        }

    }

}
