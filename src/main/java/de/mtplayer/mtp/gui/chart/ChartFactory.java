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


package de.mtplayer.mtp.gui.chart;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChartFactory {
    public static int CHART_MAX_TIME = 300;

    private ChartFactory() {
    }

    public static NumberAxis createXAxis() {
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

    public static NumberAxis createYAxis() {
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLowerBound(0.0);
        return yAxis;
    }

    public static synchronized void runChart(LineChart<Number, Number> lineChart, ChartData chartData, List<Download> startedDownloads,
                                             double countMinute, ProgData progData) {
        cleanUpChartData(chartData, startedDownloads, countMinute);
        inputDownloadDate(chartData, startedDownloads, countMinute, progData);
        generateChartData(lineChart, progData, chartData);
        zoomXAxis(lineChart, chartData, countMinute);
        zoomYAxis(lineChart, chartData);
    }

    private static synchronized void cleanUpChartData(ChartData chartData, List<Download> startedDownloads, double countMinute) {
        // letzten Wert bei abgeschlossenen Downloads setzen
        // leere Chart.Series löschen
        Iterator<XYChart.Series<Number, Number>> it = chartData.getChartSeriesListAll().listIterator();
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
                    // nur einen Wert "0" setzen und dann wars der letzte Wert
                    cSeries.getData().add(new XYChart.Data<>(countMinute, 0L));
                }
                if (cSeries.getData().isEmpty()) {
                    // dann wurde bereits alles gelöscht und kommt jetzt auch weg
                    it.remove();
                }
            }
        }

        // und jetzt noch alle Messpunkte nach MAX-Zeit löschen
        final double MIN = countMinute - ChartFactory.CHART_MAX_TIME;
        if (MIN <= 0) {
            // dann noch unter MAX_TIME
            return;
        }

        // Einzelcharts
        chartData.getChartSeriesListAll().stream().forEach(cs -> {
            cs.getData().removeIf(d -> d.getXValue().doubleValue() < MIN);
        });

        // Summe
        chartData.getChartSeriesSum().getData().removeIf(d -> d.getXValue().doubleValue() < MIN);
    }

    private static synchronized void inputDownloadDate(ChartData chartData, List<Download> startedDownloads,
                                                       double countMinute, ProgData progData) {
        //Downloads in "Diagramm" eintragen
        for (final Download download : startedDownloads) {
            //jeden Download eintragen
            XYChart.Series<Number, Number> cSeries = download.getCSeries();

            if (cSeries != null) {
                final long bandw = download.getStart().getBandwidth();
                cSeries.getData().add(new XYChart.Data<>(countMinute, bandw / chartData.getScale()));
            } else {
                cSeries = new XYChart.Series<>(download.getNr() + "", FXCollections.observableArrayList());
//                cSeries.setNode(new ProgIcons().DOWNLOAD_OK);//??????
                download.setCSeries(cSeries);

                cSeries.getData().add(new XYChart.Data<>(countMinute, 0L));
                cSeries.getData().add(new XYChart.Data<>(countMinute, download.getStart().getBandwidth() / chartData.getScale()));
                chartData.getChartSeriesListAll().add(cSeries);
            }
        }
        // Anzeige der Summe aller Downloads
        chartData.getChartSeriesSum().getData().add(new XYChart.Data<>(countMinute, progData.downloadInfos.getBandwidth() / chartData.getScale()));
    }

    private static synchronized void generateChartData(LineChart<Number, Number> lineChart, ProgData progData, ChartData chartData) {
        final boolean all = ProgConfig.DOWNLOAD_CHART_ALL_DOWNLOADS.getBool();
        final boolean onlyExisting = ProgConfig.DOWNLOAD_CHART_ONLY_EXISTING.getBool();

        if (all) {
            // dann sollen alle Downloads angezeigt werden, die Liste der angezeigten Series wieder mit "allen" füllen
            chartData.getChartSeriesListSeparate().setAll(chartData.getChartSeriesListAll());
            colorChartName(lineChart, progData, chartData);
            return;
        }

        chartData.getChartSeriesListSeparate().clear();
        chartData.getChartSeriesListAll().stream().forEach(cSeries -> {
            for (final Download download : progData.downloadList) {
                if (download.getCSeries() != null && download.getCSeries().equals(cSeries)) {

                    if (onlyExisting) {
                        // dann werden alle vorhandenen Downloads angezeigt
                        chartData.getChartSeriesListSeparate().add(cSeries);

                    } else if (download.isStateStartedRun()) {
                        // nur die laufenden anzeigen
                        chartData.getChartSeriesListSeparate().add(cSeries);
                    }
                    break;
                }
            }
        });
        colorChartName(lineChart, progData, chartData);
    }

    private static synchronized void zoomXAxis(LineChart<Number, Number> lineChart, ChartData chartData, double countMinute) {
        final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setUpperBound(countMinute);
        final double MIN = countMinute - chartData.getMaxTime();
        if (MIN <= 0) {
            xAxis.setLowerBound(0);
            return;
        }
        xAxis.setLowerBound(MIN);
    }

    private static synchronized void zoomYAxis(LineChart<Number, Number> lineChart,
                                               ChartData chartData) {
        double max = 0;
        final ObservableList<XYChart.Series<Number, Number>> list;

        if (ProgConfig.DOWNLOAD_CHART_SEPARAT.getBool()) {
            list = chartData.getChartSeriesListSeparate();
        } else {
            list = chartData.getChartSeriesListSum();
        }

        for (final XYChart.Series<Number, Number> cSeries : list) {
            for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                if ((long) date.getYValue() > max) {
                    max = (long) date.getYValue();
                }
            }
        }

        if (max > 5_000) {
            chartData.setScale(chartData.getScale() * 1000);
            setYAxisLabel(lineChart, chartData);

            for (final XYChart.Series<Number, Number> cSeries : chartData.getChartSeriesListSeparate()) {
                for (final XYChart.Data<Number, Number> date : cSeries.getData()) {
                    date.setYValue((long) date.getYValue() / 1_000);
                }
            }
            for (final XYChart.Data<Number, Number> date : chartData.getChartSeriesSum().getData()) {
                date.setYValue((long) date.getYValue() / 1_000);
            }
        }
    }

    public static void setYAxisLabel(LineChart<Number, Number> lineChart, ChartData chartData) {
        switch (chartData.getScale()) {
            case 1:
                lineChart.getYAxis().setLabel("Bandbreite [B/s]");
                break;
            case 1_000:
                lineChart.getYAxis().setLabel("Bandbreite [kB/s]");
                break;
            case 1_000_000:
                lineChart.getYAxis().setLabel("Bandbreite [MB/s]");
                break;
        }
    }

    private static synchronized void colorChartName(LineChart<Number, Number> lineChart, ProgData progData, ChartData chartData) {
        // chart einfärben
        chartData.getChartSeriesListSeparate().stream().forEach(cSeries -> {

            Download download = progData.downloadList.stream()
                    .filter(d -> d.getCSeries() != null && d.getCSeries().equals(cSeries))
                    .findFirst().orElse(null);

            if (download != null) {
                setColor(lineChart, cSeries, download);
            }

        });
    }

    private static synchronized void setColor(LineChart<Number, Number> lineChart, XYChart.Series<Number, Number> cSeries, Download download) {
        final String cRed = ProgConfig.SYSTEM_DARK_THEME.getBooleanProperty().get() ? "#ff0000" : "#de0000";
        final String cGreen = ProgConfig.SYSTEM_DARK_THEME.getBooleanProperty().get() ? "#00ff00" : "#00aa00";
        String name = cSeries.getName();
        Set<Node> items = lineChart.lookupAll("Label.chart-legend-item");
        for (Node item : items) {
            Label label = (Label) item;
            if (label.getText().equals(name)) {
                switch (download.getState()) {
                    case DownloadConstants.STATE_FINISHED:
                        label.setStyle(" -fx-text-fill: " + cGreen + ";");
                        break;
                    case DownloadConstants.STATE_ERROR:
                        label.setStyle(" -fx-text-fill: " + cRed + ";");
                        break;
                    default:
                        label.setStyle("");
                }
            }
        }
    }

}
