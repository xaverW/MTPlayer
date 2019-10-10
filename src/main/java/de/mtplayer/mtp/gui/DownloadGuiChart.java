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
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;
import de.mtplayer.mtp.gui.tools.Listener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

import java.util.Iterator;
import java.util.LinkedList;

public class DownloadGuiChart {

    private BooleanProperty separatChartProp = ProgConfig.DOWNLOAD_CHART_SEPARAT.getBooleanProperty();
    private IntegerProperty maxTime = ProgConfig.DOWNLOAD_CHART_MAX_TIME.getIntegerProperty();

    private final ProgData progData;

    private int countSek = 0;
    private int scale = 1;

    private LineChart<Number, Number> lineChart = null;
    private LinkedList<Download> startedDownloads = new LinkedList<>(); // Liste gestarteter Downloads

    private final XYChart.Series<Number, Number> sumChartSeries =
            new XYChart.Series<>("Summe", FXCollections.observableArrayList(new XYChart.Data<Number, Number>(0.0, 0.0)));

    // Liste der LineCharts für Gesamt -> hat nur eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> lineChartsSum = FXCollections.observableArrayList(sumChartSeries);

    // Liste der LineCharts für einzele Downloads -> für jeden Download eine LineChart
    private final ObservableList<XYChart.Series<Number, Number>> lineChartsSeparate = FXCollections.observableArrayList();

    private AnchorPane anchorPane;

    public DownloadGuiChart(ProgData progData, AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        this.progData = progData;

        initList();
        initCharts();

        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, DownloadGuiChart.class.getSimpleName()) {
            @Override
            public void pingFx() {
                searchInfos();
            }
        });

    }

    private synchronized void initList() {
        lineChartsSeparate.clear(); // da werden alle chartSeries gelöscht, jeder Download
        sumChartSeries.getData().clear(); // da werden die Daten in der einen chartSeries gelöscht, Summe aller Downloads
        scale = 1;
    }

    private void initCharts() {
        lineChart = new LineChart<>(createXAxis(), createYAxis());
        lineChart.getStyleClass().add("thick-chart");
        lineChart.setLegendSide(Side.RIGHT);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setTitle("Downloads");
        lineChart.getXAxis().setLabel("Programmlaufzeit [min]");
        setYAxisLabel();
        lineChart.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                final ContextMenu cm = initContextMenu();
                cm.show(lineChart, e.getSceneX(), e.getSceneY());
                // bei Dualscreen wird auf dem ersten angezeigt->javafx Fehler: https://bugs.openjdk.java.net/browse/JDK-8156094
            }
        });

        AnchorPane.setLeftAnchor(lineChart, 0.0);
        AnchorPane.setBottomAnchor(lineChart, 0.0);
        AnchorPane.setRightAnchor(lineChart, 0.0);
        AnchorPane.setTopAnchor(lineChart, 0.0);
        anchorPane.getChildren().add(lineChart);

        selectChartData();
    }

    private NumberAxis createXAxis() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0.0);
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() > 60) {
                    int i = (int) object.doubleValue();
                    return i + "";
                } else {
                    int i = (int) (object.doubleValue() * 10);
                    return ((i / 10.0) + "");
                }
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        return xAxis;
    }

    private NumberAxis createYAxis() {
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLowerBound(0.0);
        return yAxis;
    }

    private ContextMenu initContextMenu() {
        final CheckMenuItem allDowns = new CheckMenuItem("jeden Download einzeln zeichnen und nicht alle zusammenfassen");
        allDowns.selectedProperty().bindBidirectional(separatChartProp);
        allDowns.setOnAction(e -> selectChartData());


        final Slider slMaxTime = new Slider();
        slMaxTime.setMaxWidth(Double.MAX_VALUE);
        slMaxTime.setMin(30);
        slMaxTime.setMax(300);
        slMaxTime.valueProperty().bindBidirectional(maxTime);

        final Label lblValue = new Label(" " + maxTime.get() + " Min.");
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
            lineChart.setData(lineChartsSeparate);
        } else {
            lineChart.setData(lineChartsSum);
        }
    }

    // ============================
    // Daten generieren
    // ============================
    private synchronized void searchInfos() {
        ++countSek; // Sekunden
        final double countMin = countSek / 60.0; // Minuten
        startedDownloads = progData.downloadList.getListOfStartsNotFinished(DownloadConstants.ALL);

        //Downloads in "Diagramm" eintragen
        for (final Download download : startedDownloads) {
            //jeden Download eintragen
            XYChart.Series<Number, Number> cSeries = download.getCSeries();

            if (cSeries != null) {
                cSeries.getData().add(new XYChart.Data<>(countMin, download.getStart().getBandwidth() / scale));
            } else {
                cSeries = new XYChart.Series<>(download.getNr() + "",
                        FXCollections.observableArrayList());
                download.setCSeries(cSeries);

                cSeries.getData().add(new XYChart.Data<>(countMin, 0L));
                cSeries.getData().add(new XYChart.Data<>(countMin, download.getStart().getBandwidth() / scale));
                lineChartsSeparate.add(cSeries);
            }
        }


        // chart in den gestarteten Downloads suchen
        Iterator<XYChart.Series<Number, Number>> it = lineChartsSeparate.listIterator();
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
        for (final XYChart.Series<Number, Number> cSeries : lineChartsSeparate) {
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
        sumChartSeries.getData().add(new XYChart.Data<>(countMin, progData.downloadList.getDownloadListInfoAll().bandwidth / scale));
        zoomXAxis(countMin);
        zoomYAxis();
    }

    private synchronized void zoomXAxis(double count) {
        final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setUpperBound(count);
        final double MIN = count - maxTime.get();
        if (MIN <= 0) {
            return;
        }
        xAxis.setLowerBound(MIN);

        lineChartsSeparate.stream().forEach(cs -> {
            if (cs.getData().isEmpty()) {
                return;
            }

            cs.getData().removeIf(d -> d.getXValue().doubleValue() < MIN);
        });

        while (!sumChartSeries.getData().isEmpty() && sumChartSeries.getData().get(0).getXValue().doubleValue() < MIN) {
            sumChartSeries.getData().remove(0);
        }

    }

    private synchronized void zoomYAxis() {
        double max = 0;
        final ObservableList<XYChart.Series<Number, Number>> list;

        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            list = lineChartsSeparate;
        } else {
            list = lineChartsSum;
        }

        for (final XYChart.Series<Number, Number> cSeries : list) {
            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                if ((long) date.getYValue() > max) {
                    max = (long) date.getYValue();
                }
            }
        }

        if (max > 5_000) {
            scale *= 1000;
            setYAxisLabel();

            for (final XYChart.Series<Number, Number> cSeries : lineChartsSeparate) {
                for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                    date.setYValue((long) date.getYValue() / 1_000);
                }
            }
            for (final XYChart.Series<Number, Number> cSeries : lineChartsSum) {
                for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                    date.setYValue((long) date.getYValue() / 1_000);
                }
            }
        }

    }

    private void setYAxisLabel() {
        switch (scale) {
            case 1:
                lineChart.getYAxis().setLabel("Bandbreite [Byte/s]");
                break;
            case 1_000:
                lineChart.getYAxis().setLabel("Bandbreite [kByte/s]");
                break;
            case 1_000_000:
                lineChart.getYAxis().setLabel("Bandbreite [MByte/s]");
                break;
        }
    }
}
